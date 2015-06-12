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
package pl.psnc.synat.wrdz.zu.user;

import java.util.List;

import javax.ejb.Remote;

import pl.psnc.synat.wrdz.zu.dto.user.OrganizationDto;
import pl.psnc.synat.wrdz.zu.dto.user.UserDto;

/**
 * Provides information about registered users.
 */
@Remote
public interface UserBrowser {

    /**
     * Returns a list of user DTOs.
     * 
     * @return a list of user DTOs
     */
    List<UserDto> getUsers();


    /**
     * Retrieves the user with the given name.
     * 
     * @param username
     *            username
     * @return user DTO, or <code>null</code> if no such user exists
     */
    UserDto getUser(String username);


    /**
     * Retrieves the user with the given id.
     * 
     * @param id
     *            user identifier
     * @return user DTO, or <code>null</code> if no such user exists
     */
    UserDto getUser(long id);


    /**
     * Retrieves the id of the user with the given name.
     * 
     * @param username
     *            username
     * @return user id, or <code>null</code> if no such user exists
     */
    Long getUserId(String username);


    /**
     * Returns the given user's organization.
     * 
     * @param username
     *            username
     * @return organization DTO, or <code>null</code> if no such user exists
     */
    OrganizationDto getOrganization(String username);


    /**
     * Checks whether the given user is an administrator.
     * 
     * @param username
     *            username
     * @return <code>true</code> if the user is an administrator, <code>false</code> otherwise
     */
    boolean isAdmin(String username);


    /**
     * Returns the given user's email address (if set).
     * 
     * @param username
     *            username
     * @return user email address, or <code>null</code>
     */
    String getEmail(String username);
}
