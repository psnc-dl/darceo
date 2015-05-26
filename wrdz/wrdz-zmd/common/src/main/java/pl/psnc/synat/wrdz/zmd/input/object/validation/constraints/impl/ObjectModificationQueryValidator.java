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
package pl.psnc.synat.wrdz.zmd.input.object.validation.constraints.impl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.validation.constraints.ValidObjectModificationQuery;

/**
 * Implements validation of object modification request.
 */
public class ObjectModificationQueryValidator implements
        ConstraintValidator<ValidObjectModificationQuery, ObjectModificationRequest> {

    @Override
    public void initialize(ValidObjectModificationQuery constraintAnnotation) {
    }


    @Override
    public boolean isValid(ObjectModificationRequest object, ConstraintValidatorContext constraintContext) {
        if (object.getDeleteAllContent()) {
            if (validateModificationWithoutInheritanceConfiguration(object)) {
                return true;
            }
        } else {
            if (validateModificationWithInheritanceConfiguration(object)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Validates modification request with the inheritance of files from older version.
     * 
     * @param request
     *            object modification request.
     * @return <code>true</code> if request part is valid, otherwise <code>false</code>.
     */
    private boolean validateModificationWithInheritanceConfiguration(ObjectModificationRequest request) {
        boolean check = request.getInputFilesToAdd() != null && !request.getInputFilesToAdd().isEmpty();
        check = check || (request.getObjectMetadataToAdd() != null && !request.getObjectMetadataToAdd().isEmpty());
        check = check || (request.getInputFilesToModify() != null || !request.getInputFilesToModify().isEmpty());
        check = check || (request.getInputFilesToRemove() != null || !request.getInputFilesToRemove().isEmpty());
        check = check
                || (request.getObjectMetadataToModify() != null || !request.getObjectMetadataToModify().isEmpty());
        check = check
                || (request.getObjectMetadataToRemove() != null || !request.getObjectMetadataToRemove().isEmpty());
        check = check || (request.getMigratedFrom() != null);
        check = check || (request.getMigratedToToAdd() != null && !request.getMigratedToToAdd().isEmpty());
        check = check || (request.getMigratedToToModify() != null && !request.getMigratedToToModify().isEmpty());
        check = check || (request.getMigratedToToRemove() != null && !request.getMigratedToToRemove().isEmpty());
        return check;
    }


    /**
     * Validates modification request without the inheritance of files from older version.
     * 
     * @param request
     *            object modification request.
     * @return <code>true</code> if request part is valid, otherwise <code>false</code>.
     */
    private boolean validateModificationWithoutInheritanceConfiguration(ObjectModificationRequest request) {
        boolean check = request.getInputFilesToAdd() != null && !request.getInputFilesToAdd().isEmpty();
        check = check || (request.getObjectMetadataToAdd() != null && !request.getObjectMetadataToAdd().isEmpty());
        check = check || (request.getMigratedFrom() != null);
        check = check || (request.getMigratedToToAdd() != null && !request.getMigratedToToAdd().isEmpty());
        check = check || (request.getMigratedToToModify() != null && !request.getMigratedToToModify().isEmpty());
        check = check || (request.getMigratedToToRemove() != null && !request.getMigratedToToRemove().isEmpty());
        check = check && (request.getInputFilesToModify() == null || request.getInputFilesToModify().isEmpty());
        check = check && (request.getInputFilesToRemove() == null || request.getInputFilesToRemove().isEmpty());
        check = check && (request.getObjectMetadataToModify() == null || request.getObjectMetadataToModify().isEmpty());
        check = check && (request.getObjectMetadataToRemove() == null || request.getObjectMetadataToRemove().isEmpty());
        return check;
    }

}
