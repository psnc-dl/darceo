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
package pl.psnc.synat.wrdz.zmd.dao.object;

import java.util.Collection;
import java.util.Date;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;

/**
 * Specified set of filters for queries concerning {@link DigitalObject} entities.
 */
public interface DigitalObjectFilterFactory extends GenericQueryFilterFactory<DigitalObject> {

    /**
     * Filters the entities by their digital object type.
     * 
     * @param type
     *            type of digital object
     * @return current representations of filters set
     */
    QueryFilter<DigitalObject> byType(ObjectType type);


    /**
     * Filters the entities by identifiers of the digital objects. Can use exact string match or a regexp pattern.
     * 
     * @param identifier
     *            identifier of the object or a regexp pattern for that identifier to match
     * @return current representations of filters set
     */
    QueryFilter<DigitalObject> byIdentifier(String identifier);


    /**
     * Filters the entities by version of the digital objects.
     * 
     * @param version
     *            version number of the object
     * @return current representations of filters set
     */
    QueryFilter<DigitalObject> byVersion(Integer version);


    /**
     * Filters the entities by the owner of the digital objects.
     * 
     * @param ownerId
     *            digital object owner id
     * @return current representations of filters set
     */
    QueryFilter<DigitalObject> byOwner(long ownerId);


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
    QueryFilter<DigitalObject> havingOperationsPerformedBetween(Date start, Date end)
            throws IllegalArgumentException;


    /**
     * Filters the entities checking if current version is empty or not.
     * 
     * @param notEmpty
     *            specifies whether to look for empty content versions or not empty ones.
     * @return current representations of filters set.
     */
    QueryFilter<DigitalObject> byCurrentVersionState(boolean notEmpty);


    /**
     * Filters entities by their database ids.
     * 
     * @param ids
     *            database identifiers
     * @return query filter
     */
    QueryFilter<DigitalObject> byIds(Collection<Long> ids);


    /**
     * Filters the entities by their name.
     * 
     * @param name
     *            object name
     * @return query filter
     */
    QueryFilter<DigitalObject> byName(String name);

}
