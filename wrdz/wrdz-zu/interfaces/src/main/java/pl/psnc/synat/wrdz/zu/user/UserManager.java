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

import javax.ejb.Local;

import pl.psnc.synat.wrdz.zu.entity.user.User;
import pl.psnc.synat.wrdz.zu.exceptions.NameExistsException;

/**
 * Manages user entities.
 */
@Local
public interface UserManager {

    /**
     * Fetches the user with the given id.
     * 
     * @param id
     *            user identifier
     * @return the found user, or <code>null</code>
     */
    User getUser(long id);


    /**
     * Lists all user entities.
     * 
     * @return user entities
     * 
     */
    List<User> getUsers();


    /**
     * Creates a new user.
     * 
     * The given user entity must include user authentication and a reference to an organization with a valid id.
     * 
     * @param user
     *            the user to be created
     * @param groupsCreatable
     *            whether user can create groups
     * @param objectsCreatable
     *            whether user can create digital objects
     * @return the created user
     * @throws NameExistsException
     *             if the given user name is already taken
     */
    User createUser(User user, boolean groupsCreatable, boolean objectsCreatable)
            throws NameExistsException;


    /**
     * Modifies an existing user.
     * 
     * @param user
     *            user modification
     * @param groupsCreatable
     *            whether user can create groups
     * @param objectsCreatable
     *            whether user can create digital objects
     * @return the modified user
     * @throws NameExistsException
     *             if the given user name is already taken
     */
    User modifyUser(User user, boolean groupsCreatable, boolean objectsCreatable)
            throws NameExistsException;

}
