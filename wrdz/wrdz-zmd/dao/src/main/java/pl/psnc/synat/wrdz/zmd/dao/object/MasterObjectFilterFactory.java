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
import pl.psnc.synat.wrdz.zmd.entity.object.MasterObject;

/**
 * Specified set of filters for queries concerning {@link MasterObject} entities.
 */
public interface MasterObjectFilterFactory extends GenericQueryFilterFactory<MasterObject> {

    /**
     * Filters the entities by the transformation operation they're result of, operation being represented by its
     * identifier (primary key value).
     * 
     * @param id
     *            identifier (primary key value) of the transformation operation
     * @return current representations of filters set
     */
    QueryFilter<MasterObject> byTransformedFrom(Long id);

}
