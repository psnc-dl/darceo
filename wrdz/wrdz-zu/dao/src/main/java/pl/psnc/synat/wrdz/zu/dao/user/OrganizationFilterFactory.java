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

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zu.entity.user.Organization;

/**
 * Specified set of filters for queries concerning {@link Organization} entities.
 */
public interface OrganizationFilterFactory extends GenericQueryFilterFactory<Organization> {

    /**
     * Filters the entities by the organization's name. Can use exact string match or a regexp pattern.
     * 
     * @param name
     *            name of the organization or a regexp pattern for that name to match
     * @return current representations of filters set
     */
    QueryFilter<Organization> byName(String name);

}
