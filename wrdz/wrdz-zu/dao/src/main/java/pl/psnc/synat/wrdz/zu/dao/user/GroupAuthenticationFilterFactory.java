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
package pl.psnc.synat.wrdz.zu.dao.user;

import java.util.List;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication;

/**
 * Specified set of filters for queries concerning {@link GroupAuthentication} entities.
 */
public interface GroupAuthenticationFilterFactory extends GenericQueryFilterFactory<GroupAuthentication> {

    /**
     * Produces filter that filters groups by their identifiers.
     * 
     * @param identifiers
     *            group identifiers
     * @return filter
     */
    QueryFilter<GroupAuthentication> byIdentifiers(List<Long> identifiers);


    /**
     * Produces filter that enables filtering groups by their names.
     * 
     * @param name
     *            group's name to filter by.
     * @return current representation of filters set.
     */
    QueryFilter<GroupAuthentication> byGroupName(String name);


    /**
     * Produces a filter that filters group by the single-user flag.
     * 
     * @param singleUser
     *            the flag value
     * @return current representation of filters set.
     */
    QueryFilter<GroupAuthentication> bySingleUser(boolean singleUser);
}
