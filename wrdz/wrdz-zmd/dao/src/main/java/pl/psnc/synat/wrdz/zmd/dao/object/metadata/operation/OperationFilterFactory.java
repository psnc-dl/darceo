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

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;
import pl.psnc.synat.wrdz.zmd.entity.types.OperationType;

/**
 * Specified set of filters for queries concerning {@link Operation} entities.
 */
public interface OperationFilterFactory extends GenericQueryFilterFactory<Operation> {

    /**
     * Filters the entities by the operation type.
     * 
     * @param type
     *            type of the operation.
     * @return current representations of filters set.
     */
    QueryFilter<Operation> byType(OperationType type);


    /**
     * Filters the entities by the digital object they are connected to.
     * 
     * @param id
     *            digital object's primary key value.
     * @return current representations of filters set.
     */
    QueryFilter<Operation> byDigitalObject(long id);


    /**
     * Filters the entities by the digital object they are connected to using object's default identifier.
     * 
     * @param identifier
     *            digital object's default public identifier.
     * @return current representations of filters set.
     * @throws IllegalArgumentException
     *             if specified string is <code>null</code> or empty.
     */
    QueryFilter<Operation> byDigitalObject(String identifier)
            throws IllegalArgumentException;


    /**
     * Filters the entities by the date of operation that is included in specified time range.
     * 
     * @param start
     *            start of the time interval by which operations are filtered, if null all operations from the beginning
     *            to the specified end date will be returned.
     * @param end
     *            end of the time interval by which operations are filtered, if null all operations from the specified
     *            start date to the current moment will be returned.
     * @return current representations of filters set.
     * @throws IllegalArgumentException
     *             if both arguments are null or start date is greater or equal to end date.
     */
    QueryFilter<Operation> byDateBetween(Date start, Date end)
            throws IllegalArgumentException;


    /**
     * Filters the entities by the namespace type.
     * 
     * @param type
     *            metadata type (main namespace type).
     * @return current representations of filters set.
     * @throws IllegalArgumentException
     *             if argument is null.
     */
    QueryFilter<Operation> byMetadataType(NamespaceType type)
            throws IllegalArgumentException;

}
