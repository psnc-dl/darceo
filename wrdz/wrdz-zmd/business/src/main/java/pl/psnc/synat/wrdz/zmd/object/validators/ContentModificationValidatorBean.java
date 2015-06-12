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

import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.input.InputFile;
import pl.psnc.synat.wrdz.zmd.input.InputFileUpdate;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;
import pl.psnc.synat.wrdz.zmd.object.FileNotFoundException;
import pl.psnc.synat.wrdz.zmd.object.ObjectBrowser;
import pl.psnc.synat.wrdz.zmd.object.ObjectChecker;
import pl.psnc.synat.wrdz.zmd.object.ObjectCreationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectModificationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectNotFoundException;

/**
 * Bean validating the content modification request.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ContentModificationValidatorBean implements ContentModificationValidator {

    /**
     * Digital object finder.
     */
    @EJB
    private ObjectBrowser objectBrowser;

    /** Object checker. */
    @EJB
    private ObjectChecker objectChecker;


    @Override
    public void validateContentModificationRequest(ObjectModificationRequest modificationRequest)
            throws ObjectModificationException {
        ContentVersion objectsVersion;
        try {
            objectsVersion = objectBrowser.getObjectsVersion(modificationRequest.getIdentifier(), null);
        } catch (ObjectNotFoundException e) {
            throw new ObjectModificationException("Object specified for modification does not exist.");
        }
        if (!checkIfContentOperationsAreExclusive(modificationRequest.getInputFilesToAdd(),
            modificationRequest.getInputFilesToModify(), modificationRequest.getInputFilesToRemove())) {
            throw new ObjectModificationException(
                    "Invalid modification request, simultaneous operations on the same resource detected.");
        }
        if (!modificationRequest.getDeleteAllContent()) {
            if (!checkIfContentOperationsReferencesAreCorrect(objectsVersion, modificationRequest.getInputFilesToAdd(),
                modificationRequest.getInputFilesToModify(), modificationRequest.getInputFilesToRemove())) {
                throw new ObjectModificationException(
                        "Invalid modification request, trying to add already existing resource or modify/delete nonexistent one.");
            }
        }
        if (!checkMainFile(objectsVersion, modificationRequest.getMainFile(), modificationRequest.getInputFilesToAdd(),
            modificationRequest.getInputFilesToRemove())) {
            throw new ObjectModificationException(
                    "Invalid modification request, specified main file does not exist in the object's inherited files or data files added by request.");
        }
    }


    @Override
    public void validateContentCreationRequest(ObjectCreationRequest creationRequest)
            throws ObjectCreationException {
        if (!checkIfContentOperationsAreExclusive(creationRequest.getInputFiles(), null, null)) {
            throw new ObjectCreationException(
                    "Invalid creation request, simultaneous operations on the same resource detected.");
        }
        if (!checkMainFile(creationRequest.getMainFile(), creationRequest.getInputFiles())) {
            throw new ObjectCreationException(
                    "Invalid creation request, specified main file does not exist in the object data files added by request.");
        }
    }


    @Override
    public boolean checkIfContentOperationsReferencesAreCorrect(ContentVersion objectsVersion,
            Set<InputFile> filesToAdd, Set<InputFileUpdate> filesToModify, Set<String> filesToRemove)
            throws ObjectModificationException {
        boolean check = checkAddedFiles(objectsVersion, filesToAdd);
        if (check) {
            check = check && checkModifiedFiles(objectsVersion, filesToModify);
            if (check) {
                check = check && checkRemovedFiles(objectsVersion, filesToRemove);
            }
        }
        return check;
    }


    @Override
    public boolean checkIfFileMetadataOperationsReferencesAreCorrect(ContentVersion version, InputFileUpdate fileUpdate)
            throws ObjectModificationException {
        DataFile dataFileInVersion;
        try {
            dataFileInVersion = objectBrowser.getDataFileFromVersion(fileUpdate.getDestination(), version);
        } catch (FileNotFoundException e) {
            throw new ObjectModificationException("Data file specified for modification does not exist in version "
                    + version.getVersion() + ".");
        }
        boolean check = checkAddedFileMetadata(version, fileUpdate.getMetadataFilesToAdd(), dataFileInVersion);
        if (check) {
            check = check
                    && checkModifiedFileMetadata(version, fileUpdate.getMetadataFilesToModify(), dataFileInVersion);
            if (check) {
                check = check
                        && checkRemovedFileMetadata(version, fileUpdate.getMetadataFilesToRemove(), dataFileInVersion);
            }
        }
        return check;
    }


    @Override
    public boolean checkIfContentOperationsAreExclusive(Set<InputFile> filesToAdd, Set<InputFileUpdate> filesToModify,
            Set<String> filesToRemove) {
        Set<String> paths = new HashSet<String>();
        if (filesToRemove != null) {
            paths.addAll(filesToRemove);
        }
        Set<InputFile> inputFilesToAdd = filesToAdd;
        if (inputFilesToAdd != null) {
            for (InputFile inputFile : inputFilesToAdd) {
                if (!paths.add(inputFile.getDestination())) {
                    return false;
                }
            }
        }
        Set<InputFileUpdate> inputFilesToModify = filesToModify;
        if (inputFilesToModify != null) {
            for (InputFileUpdate inputFileUpdate : inputFilesToModify) {
                if (!paths.add(inputFileUpdate.getDestination())
                        || !checkIfFileMetadataOperationsAreExclusive(inputFileUpdate)) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public boolean checkIfFileMetadataOperationsAreExclusive(InputFileUpdate inputFileUpdate) {
        Set<String> metadataFilesToRemove = inputFileUpdate.getMetadataFilesToRemove();
        Set<String> metadataFilesToAdd = null;
        if (inputFileUpdate.getMetadataFilesToAdd() != null) {
            metadataFilesToAdd = inputFileUpdate.getMetadataFilesToAdd().keySet();
        }
        Set<String> metadataFilesToModify = null;
        if (inputFileUpdate.getMetadataFilesToModify() != null) {
            metadataFilesToModify = inputFileUpdate.getMetadataFilesToModify().keySet();
        }
        try {
            unionOfNonintersectingSets(unionOfNonintersectingSets(metadataFilesToRemove, metadataFilesToAdd),
                metadataFilesToModify);
        } catch (IllegalArgumentException e) {
            return true;
        }
        return true;
    }


    /**
     * Checks the validity of request part specifying content files to be added.
     * 
     * @param version
     *            current version (before modification).
     * @param filesToAdd
     *            set of added files.
     * @return <code>true</code> if request part is valid, otherwise <code>false</code>.
     */
    private boolean checkAddedFiles(ContentVersion version, Set<InputFile> filesToAdd) {
        if (filesToAdd != null) {
            for (InputFile fileToAdd : filesToAdd) {
                if (objectChecker.checkIfDataFileExistsInVersion(fileToAdd.getDestination(), version)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Checks the validity of request part specifying content files to be modified.
     * 
     * @param version
     *            current version (before modification).
     * @param filesToModify
     *            set of file updates.
     * @return <code>true</code> if request part is valid, otherwise <code>false</code>.
     * @throws ObjectModificationException
     *             when modification request references nonexistent file.
     */
    private boolean checkModifiedFiles(ContentVersion version, Set<InputFileUpdate> filesToModify)
            throws ObjectModificationException {
        if (filesToModify != null) {
            for (InputFileUpdate fileToModify : filesToModify) {
                if (!objectChecker.checkIfDataFileExistsInVersion(fileToModify.getDestination(), version)
                        || !checkIfFileMetadataOperationsReferencesAreCorrect(version, fileToModify)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Checks the validity of request part specifying content files to be removed.
     * 
     * @param version
     *            current version (before modification).
     * @param filesToRemove
     *            list of object's content folder relative paths to files to be removed.
     * @return <code>true</code> if request part is valid, otherwise <code>false</code>.
     */
    private boolean checkRemovedFiles(ContentVersion version, Set<String> filesToRemove) {
        if (filesToRemove != null) {
            for (String path : filesToRemove) {
                if (!objectChecker.checkIfDataFileExistsInVersion(path, version)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Checks the validity of request part specifying file's metadata to add.
     * 
     * @param version
     *            current version (before modification).
     * @param fileMetadataToAdd
     *            map of metadata files to be added.
     * @param file
     *            content file to which metadata files set is to be added.
     * @return <code>true</code> if request part is valid, otherwise <code>false</code>.
     */
    private boolean checkAddedFileMetadata(ContentVersion version, Map<String, URI> fileMetadataToAdd, DataFile file) {
        if (fileMetadataToAdd == null || fileMetadataToAdd.isEmpty()) {
            return true;
        } else {
            Set<String> paths = fileMetadataToAdd.keySet();
            for (String name : paths) {
                if (objectChecker.checkIfFileProvidedMetadataExistsInVersion(name, version, file)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Checks the validity of request part specifying file's metadata to modify.
     * 
     * @param version
     *            current version (before modification).
     * @param metadataFilesToModify
     *            map of metadata files to be modified.
     * @param file
     *            content file which metadata files set is to be modified.
     * @return <code>true</code> if request part is valid, otherwise <code>false</code>.
     */
    private boolean checkModifiedFileMetadata(ContentVersion version, Map<String, URI> metadataFilesToModify,
            DataFile file) {
        if (metadataFilesToModify == null || metadataFilesToModify.isEmpty()) {
            return true;
        } else {
            Set<String> paths = metadataFilesToModify.keySet();
            for (String name : paths) {
                if (!objectChecker.checkIfFileProvidedMetadataExistsInVersion(name, version, file)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Checks the validity of request part specifying file's metadata to remove.
     * 
     * @param version
     *            current version (before modification).
     * @param metadataFilesToRemove
     *            list of metadata files to be removed.
     * @param file
     *            content file which metadata files set is to be modified.
     * @return <code>true</code> if request part is valid, otherwise <code>false</code>.
     */
    private boolean checkRemovedFileMetadata(ContentVersion version, Set<String> metadataFilesToRemove, DataFile file) {
        if (metadataFilesToRemove == null || metadataFilesToRemove.isEmpty()) {
            return true;
        } else {
            for (String name : metadataFilesToRemove) {
                if (!objectChecker.checkIfFileProvidedMetadataExistsInVersion(name, version, file)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Checks for main file presence in the file set.
     * 
     * @param mainFilePath
     *            path to the main file.
     * @param addedFiles
     *            set of files added to the object.
     * @return <code>true</code> if file designated by the path is present in the set, <code>false</code> otherwise.
     */
    private boolean checkMainFile(String mainFilePath, Set<InputFile> addedFiles) {
        if (mainFilePath == null || mainFilePath.isEmpty()) {
            return true;
        }
        if (addedFiles != null) {
            for (InputFile inputFile : addedFiles) {
                if (inputFile.getDestination().equals(mainFilePath)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Checks for main file presence in the files added or present in version and its absence in deleted files set.
     * 
     * @param objectsVersion
     *            version of the object.
     * @param mainFilePath
     *            path to the main file.
     * @param addedFiles
     *            set of files added to the object.
     * @param removedFiles
     *            set of files to be removed.
     * @return if specified main file is present in the list of object's files.
     */
    private boolean checkMainFile(ContentVersion objectsVersion, String mainFilePath, Set<InputFile> addedFiles,
            Set<String> removedFiles) {
        if (mainFilePath == null || mainFilePath.isEmpty()) {
            return true;
        }
        if (addedFiles != null) {
            for (InputFile inputFile : addedFiles) {
                if (mainFilePath.equals(inputFile.getDestination())) {
                    return true;
                }
            }
        }
        if (removedFiles != null) {
            for (String path : removedFiles) {
                if (mainFilePath.equals(path)) {
                    return false;
                }
            }
        }
        if (objectChecker.checkIfDataFileExistsInVersion(mainFilePath, objectsVersion)) {
            return true;
        }
        return false;
    }


    /**
     * Creates a union of two nonintersecting sets.
     * 
     * @param set1
     *            first of the sets.
     * @param set2
     *            second of the sets.
     * @return union of passed sets.
     * @throws IllegalArgumentException
     *             if passed sets were intersecting.
     */
    private Set<String> unionOfNonintersectingSets(Set<String> set1, Set<String> set2)
            throws IllegalArgumentException {
        Set<String> result = new HashSet<String>();
        if (set1 == null) {
            if (set2 != null) {
                result.addAll(set2);
            }
        } else {
            if (set2 == null) {
                result.addAll(set1);
            } else {
                result.addAll(set1);
                result.addAll(set2);
                if (result.size() != set1.size() + set2.size()) {
                    throw new IllegalArgumentException("Sets are intersecting!");
                }
            }
        }
        return result;
    }
}
