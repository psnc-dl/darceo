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

import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;
import pl.psnc.synat.wrdz.zmd.object.ObjectChecker;
import pl.psnc.synat.wrdz.zmd.object.ObjectCreationException;

/**
 * Bean validating the object modification request.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ObjectCreationValidatorBean implements ObjectCreationValidator {

    /** Object checker. */
    @EJB
    private ObjectChecker objectChecker;

    /**
     * Content modification validator bean.
     */
    @EJB
    private ContentModificationValidator contentModificationValidator;

    /**
     * Validates migration operation information.
     */
    @EJB
    private MigrationOperationValidator migrationOperationValidator;

    /**
     * Validator object.
     */
    @Inject
    private Validator validator;


    @Override
    public void validateObjectCreationRequest(ObjectCreationRequest creationRequest)
            throws ObjectCreationException {
        Set<ConstraintViolation<ObjectCreationRequest>> validate = validator.validate(creationRequest);
        if (validate.size() > 0) {
            throw new ObjectCreationException("Cannot create empty object.");
        }
        checkProposedIdentifier(creationRequest.getProposedId());
        migrationOperationValidator.validateMigrationOperations(creationRequest);
        contentModificationValidator.validateContentCreationRequest(creationRequest);
    }


    /**
     * Checks if proposed identifier is available.
     * 
     * @param identifier
     *            proposed identifier.
     * @throws ObjectCreationException
     *             should proposed identifier already exist in the database.
     */
    private void checkProposedIdentifier(String identifier)
            throws ObjectCreationException {
        if (identifier != null && objectChecker.checkIfDigitalObjectExists(identifier)) {
            throw new ObjectCreationException("Object with identifier " + identifier + " already exists.");
        }
    }

}
