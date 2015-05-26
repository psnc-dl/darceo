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
package pl.psnc.synat.wrdz.zmd.dao.object.metadata.operation;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.persistence.LockModeType;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDao;
import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;

/**
 * An interface for a class managing the persistence of {@link Operation} class. It declares additional operations
 * available for {@link Operation} object apart from basic contract defined in {@link ExtendedGenericDao}.
 */
@Local
public interface OperationDao extends
        ExtendedGenericDao<OperationFilterFactory, OperationSorterBuilder, Operation, Long> {

    /**
     * Locks the table containing entity in the manner specified by the parameter.
     * 
     * @param exclusive
     *            if true then {@link LockModeType#PESSIMISTIC_WRITE} is performed, else
     *            {@link LockModeType#PESSIMISTIC_READ} is executed.
     */
    void lockTable(boolean exclusive);


    /**
     * Fetches the list of metadata types available in the whole repository (if argument is null) or for a specified
     * object (if id is given).
     * 
     * @param objectId
     *            object's id in the database or null, if query is to fetch all available types.
     * @return namespace types available.
     */
    List<NamespaceType> findUsedNamespaceTypes(Long objectId);


    /**
     * Fetches the list of operations for a specified object.
     * 
     * @param objectId
     *            object's id in the database or null, if query is to fetch all available types.
     * @return list of operations.
     */
    Operation getOperationForObject(Long objectId);


    /**
     * Counts the number of entities updated within given period possessing given parameters.
     * 
     * @param from
     *            beginning of time period by which to filter entities.
     * @param until
     *            end of time period by which to filter entities.
     * @param prefix
     *            namespace type of the metadata by which to filter entities.
     * @param set
     *            object type by which to filter entities.
     * @return number of entities matching the query.
     */
    Long countChangedObjects(Date from, Date until, NamespaceType prefix, ObjectType set);


    /**
     * Gets specified page of the entities updated within given period possessing given parameters.
     * 
     * @param from
     *            beginning of time period by which to filter entities.
     * @param until
     *            end of time period by which to filter entities.
     * @param prefix
     *            namespace type of the metadata by which to filter entities.
     * @param set
     *            object type by which to filter entities.
     * @param offset
     *            page offset (first element's position).
     * @param pageSize
     *            size of the results' page.
     * @return results matching the query.
     */
    List<Operation> getChanges(Date from, Date until, NamespaceType prefix, ObjectType set, int offset, int pageSize);

}
