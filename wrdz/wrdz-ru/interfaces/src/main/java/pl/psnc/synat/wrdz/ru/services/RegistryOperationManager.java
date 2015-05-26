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
package pl.psnc.synat.wrdz.ru.services;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.ru.entity.services.RegistryOperation;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.entity.types.OperationType;
import pl.psnc.synat.wrdz.ru.exceptions.HarvestingException;

/**
 * Defines the contract for classes managing {@link RegistryOperation} entities.
 */
@Local
public interface RegistryOperationManager {

    /**
     * Creates new <code>deletion</code> operation entry for the specified {@link SemanticDescriptor}.
     * 
     * @param target
     *            descriptor being described by the operation.
     * @return newly created operation.
     */
    RegistryOperation createDeleteOperation(SemanticDescriptor target);


    /**
     * Creates new <code>modification</code> operation entry for the specified {@link SemanticDescriptor}.
     * 
     * @param target
     *            descriptor being described by the operation.
     * @param changedVisibility
     *            change in visibility or null if none applied.
     * @return newly created operation.
     */
    RegistryOperation createModificationOperation(SemanticDescriptor target, OperationType changedVisibility);


    /**
     * Creates new <code>creation</code> operation entry for the specified {@link SemanticDescriptor}.
     * 
     * @param target
     *            descriptor being described by the operation.
     * @return newly created operation.
     */
    RegistryOperation createCreationOperation(SemanticDescriptor target);


    /**
     * Fetches database time locking the table storing {@link RegistryOperation} entities in exclusive mode. Guarantees
     * sequential saving of operations and synchronized access to them.
     * 
     * @return current database time.
     */
    Date getDatabaseTime();


    /**
     * Fetches the list of registry operations that occurred after the specified date until now.
     * 
     * @param from
     *            date from which to start harvesting.
     * @return list of found operations.
     * @throws HarvestingException
     *             should passed parameter point to the date in the future.
     */
    Operations getOperations(Date from)
            throws HarvestingException;


    /**
     * Fetches the list of operations performed on the semantic descriptor at or after the given point in time.
     * 
     * @param descriptorId
     *            semantic descriptor identifier which operations are being fetched.
     * @param from
     *            date marking the beginning of period from which operations are fetched.
     * @return list of fetched operations performed on the specified descriptor.
     */
    List<RegistryOperation> getOperations(long descriptorId, Date from);


    /**
     * Creates new operation for the specified semantic descriptor out of the harvested information.
     * 
     * @param operation
     *            harvested operation data.
     * @param target
     *            semantic descriptor described by operation.
     * @return constructed and persisted registry operation entity.
     */
    RegistryOperation createOperation(Operation operation, SemanticDescriptor target);


    /**
     * Creates modification operation and operations marking publishing and unpublishing events.
     * 
     * @param descriptor
     *            modified semantic descriptor.
     * @param exposed
     *            current visibility.
     * @param wasExposed
     *            previous visibility.
     */
    void createModificationOperation(SemanticDescriptor descriptor, boolean exposed, boolean wasExposed);

}
