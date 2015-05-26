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

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier;
import pl.psnc.synat.wrdz.zmd.entity.types.IdentifierType;

/**
 * Specified set of filters for queries concerning {@link Identifier} entities.
 */
public interface IdentifierFilterFactory extends GenericQueryFilterFactory<Identifier> {

    /**
     * Filters the entities by their digital object id.
     * 
     * @param objectId
     *            digital object's database id value
     * @return current representations of filters set
     */
    QueryFilter<Identifier> byObject(Long objectId);


    /**
     * Filters the entities by identifier value. Can use only exact string.
     * 
     * @param identifier
     *            identifier value to match
     * @return current representations of filters set
     */
    QueryFilter<Identifier> byIdentifier(String identifier);


    /**
     * Filters the entities by their type.
     * 
     * @param type
     *            type of identifier
     * @return current representations of filters set
     */
    QueryFilter<Identifier> byType(IdentifierType type);

}
