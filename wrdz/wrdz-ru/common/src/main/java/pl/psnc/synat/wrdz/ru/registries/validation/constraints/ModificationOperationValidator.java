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
package pl.psnc.synat.wrdz.ru.registries.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import pl.psnc.synat.wrdz.ru.registries.RegistryFormData;

/**
 * Implementation of OAI-PMH query that uses tokens, i.e. ListRecords and ListIdentifiers (in ZMD ListSets has constant
 * output of 3 elements so no tokens were implemented there).
 */
public class ModificationOperationValidator implements
        ConstraintValidator<ValidModificationOperation, RegistryFormData> {

    @Override
    public void initialize(ValidModificationOperation constraintAnnotation) {
    }


    @Override
    public boolean isValid(RegistryFormData object, ConstraintValidatorContext constraintContext) {
        boolean result = object.getName() != null;
        result = result || object.getLocation() != null;
        result = result || object.getCertificate() != null;
        result = result || object.getDescription() != null;
        result = result || object.getReadEnabled() != null;
        result = result || object.getHarvested() != null;
        return result;
    }

}
