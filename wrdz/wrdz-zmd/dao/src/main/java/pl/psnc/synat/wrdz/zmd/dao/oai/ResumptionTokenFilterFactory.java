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
package pl.psnc.synat.wrdz.zmd.dao.oai;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.entity.oai.ResumptionToken;

/**
 * Specified set of filters for queries concerning {@link ResumptionToken} entities.
 */
public interface ResumptionTokenFilterFactory extends GenericQueryFilterFactory<ResumptionToken> {

    /**
     * Filters the entities by token identifier.
     * 
     * @param id
     *            token identifier.
     * @return current representation of filters set.
     */
    QueryFilter<ResumptionToken> byId(String id);


    /**
     * Filters the entities by operation type.
     * 
     * @param type
     *            operation type.
     * @return current representation of filters set.
     */
    QueryFilter<ResumptionToken> byType(String type);

}
