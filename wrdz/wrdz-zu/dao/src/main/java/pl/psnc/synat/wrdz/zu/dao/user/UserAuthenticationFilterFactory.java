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
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication;
import pl.psnc.synat.wrdz.zu.entity.user.UserAuthentication;

/**
 * Specified set of filters for queries concerning {@link UserAuthentication} entities.
 */
public interface UserAuthenticationFilterFactory extends GenericQueryFilterFactory<UserAuthentication> {

    /**
     * Produces filter that enables filtering user authentication information by their names.
     * 
     * @param username
     *            user's name to filter by.
     * @return current representation of filters set.
     */
    QueryFilter<UserAuthentication> byUsername(String username);


    /**
     * Produces filter that filter user authentication by the group they belong to.
     * 
     * @param group
     *            group the user authentications must belong to
     * @return current representation of filters set.
     */
    QueryFilter<UserAuthentication> byGroup(GroupAuthentication group);
}
