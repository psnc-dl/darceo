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

import javax.ejb.Local;

import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;
import pl.psnc.synat.wrdz.zmd.object.ObjectCreationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectModificationException;

/**
 * Interface for migration specifying request validator.
 */
@Local
public interface MigrationOperationValidator {

    /**
     * Validates migration part of modification request object.
     * 
     * @param request
     *            modification request object.
     * @throws ObjectModificationException
     *             if request is invalid.
     */
    void validateMigrationOperations(ObjectModificationRequest request)
            throws ObjectModificationException;


    /**
     * Validates migration part of creation request object.
     * 
     * @param request
     *            creation request object.
     * @throws ObjectCreationException
     *             if request is invalid.
     */
    void validateMigrationOperations(ObjectCreationRequest request)
            throws ObjectCreationException;

}
