/**
 * Copyright 2015 Poznań Supercomputing and Networking Center
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.gnu.org/licenses/gpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.psnc.synat.wrdz.zmd.object.validators;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;
import pl.psnc.synat.wrdz.zmd.object.ObjectBrowser;
import pl.psnc.synat.wrdz.zmd.object.ObjectChecker;
import pl.psnc.synat.wrdz.zmd.object.ObjectModificationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectNotFoundException;

/**
 * Bean validating the object modification request.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ObjectModificationValidatorBean implements ObjectModificationValidator {

    /**
     * Digital object browser.
     */
    @EJB
    private ObjectBrowser objectBrowser;

    /** Object checker. */
    @EJB
    private ObjectChecker objectChecker;

    /**
     * Content modification validator bean.
     */
    @EJB
    private ContentModificationValidator contentModificationValidator;

    /**
     * Validates migration operation.
     */
    @EJB
    private MigrationOperationValidator migrationOperationValidator;

    /**
     * Validator object.
     */
    @Inject
    private Validator validator;


    @Override
    public void validateObjectModificationRequest(ObjectModificationRequest request)
            throws ObjectModificationException {
        Set<ConstraintViolation<ObjectModificationRequest>> validate = validator.validate(request);
        if (validate.size() > 0) {
            throw new ObjectModificationException("Changeset proposed in request is invalid or empty.");
        }
        if (!checkIfObjectMetadataOperationsAreExclusive(request)) {
            throw new ObjectModificationException(
                    "Invalid modification request, simultaneous operations on the same resource detected.");
        }
        if (!request.getDeleteAllContent()) {
            if (!checkIfObjectMetadataOperationsReferencesAreCorrect(request)) {
                throw new ObjectModificationException(
                        "Invalid modification request, trying to add already existing resource or modify/delete nonexistent one.");
            }
        }
        migrationOperationValidator.validateMigrationOperations(request);
        contentModificationValidator.validateContentModificationRequest(request);
    }


    /**
     * Validates if operations specified by the request are exclusive, i.e. if not more than operation is specified to
     * be performed on single file..
     * 
     * @param modificationRequest
     *            digital object's modification request object.
     * @return <code>true</code> if request part is valid, otherwise <code>false</code>.
     */
    private boolean checkIfObjectMetadataOperationsAreExclusive(ObjectModificationRequest modificationRequest) {
        Set<String> paths = new HashSet<String>();
        Set<String> objectMetadataToRemove = modificationRequest.getObjectMetadataToRemove();
        if (objectMetadataToRemove != null) {
            paths.addAll(objectMetadataToRemove);
        }
        if (modificationRequest.getObjectMetadataToAdd() != null) {
            Set<String> objectMetadataToAdd = modificationRequest.getObjectMetadataToAdd().keySet();
            for (String metadataPath : objectMetadataToAdd) {
                if (!paths.add(metadataPath)) {
                    return false;
                }
            }
        }
        if (modificationRequest.getObjectMetadataToModify() != null) {
            Set<String> objectMetadataToModify = modificationRequest.getObjectMetadataToModify().keySet();
            for (String metadataPath : objectMetadataToModify) {
                if (!paths.add(metadataPath)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Checks if operations on object's metadata reference exiting metadata files.
     * 
     * @param modificationRequest
     *            digital object's modification request object.
     * @return <code>true</code> if request part is valid, otherwise <code>false</code>.
     * @throws ObjectModificationException
     *             when object specified for modification does not exist.
     */
    private boolean checkIfObjectMetadataOperationsReferencesAreCorrect(ObjectModificationRequest modificationRequest)
            throws ObjectModificationException {
        ContentVersion objectsVersion;
        try {
            objectsVersion = objectBrowser.getObjectsVersion(modificationRequest.getIdentifier(), null);
        } catch (ObjectNotFoundException e) {
            throw new ObjectModificationException("Object specified for modification does not exist.");
        }
        boolean check = checkAddedMetadata(objectsVersion, modificationRequest.getObjectMetadataToAdd());
        if (check) {
            check = check && checkModifiedMetadata(objectsVersion, modificationRequest.getObjectMetadataToModify());
            if (check) {
                check = check && checkRemovedMetadata(objectsVersion, modificationRequest.getObjectMetadataToRemove());
            }
        }
        return check;
    }


    /**
     * Checks if object's added metadata have no name collisions with existing metadata files.
     * 
     * @param version
     *            current version (before modification).
     * @param objectMetadataToAdd
     *            map of metadata to add to object.
     * @return <code>true</code> if request part is valid, otherwise <code>false</code>.
     */
    private boolean checkAddedMetadata(ContentVersion version, Map<String, URI> objectMetadataToAdd) {
        if (objectMetadataToAdd == null || objectMetadataToAdd.isEmpty()) {
            return true;
        } else {
            Set<String> paths = objectMetadataToAdd.keySet();
            for (String name : paths) {
                if (objectChecker.checkIfObjectProvidedMetadataExistsInVersion(name, version)) {
                    return false;
                }
            }
            return true;
        }
    }


    /**
     * Checks if object's metadata to be modified exists in object.
     * 
     * @param version
     *            current version (before modification).
     * @param objectMetadataToModify
     *            map of metadata to modify in the object.
     * @return <code>true</code> if request part is valid, otherwise <code>false</code>.
     */
    private boolean checkModifiedMetadata(ContentVersion version, Map<String, URI> objectMetadataToModify) {
        if (objectMetadataToModify == null || objectMetadataToModify.isEmpty()) {
            return true;
        } else {
            Set<String> paths = objectMetadataToModify.keySet();
            for (String name : paths) {
                if (!objectChecker.checkIfObjectProvidedMetadataExistsInVersion(name, version)) {
                    return false;
                }
            }
            return true;
        }
    }


    /**
     * Checks if object's metadata to be deleted exists in the object.
     * 
     * @param version
     *            current version (before modification).
     * @param objectMetadataToRemove
     *            set of paths of metadata files to be removed.
     * @return <code>true</code> if request part is valid, otherwise <code>false</code>.
     */
    private boolean checkRemovedMetadata(ContentVersion version, Set<String> objectMetadataToRemove) {
        if (objectMetadataToRemove == null || objectMetadataToRemove.isEmpty()) {
            return true;
        } else {
            for (String name : objectMetadataToRemove) {
                if (!objectChecker.checkIfObjectProvidedMetadataExistsInVersion(name, version)) {
                    return false;
                }
            }
            return true;
        }
    }

}
