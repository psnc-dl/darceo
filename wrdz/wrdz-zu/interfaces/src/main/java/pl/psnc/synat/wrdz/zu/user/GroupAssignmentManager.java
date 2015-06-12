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

import pl.psnc.synat.wrdz.zu.entity.user.UserAuthentication;
import pl.psnc.synat.wrdz.zu.exceptions.NoSuchUserException;

/**
 * Manages user-to-group assignment.
 */
@Local
public interface GroupAssignmentManager {

    /**
     * Lists all users assigned to the group with the given id.
     * 
     * @param groupId
     *            group identifier
     * @return assigned users
     */
    List<UserAuthentication> getUsers(long groupId);


    /**
     * Adds the user with the given name to the group with the given id.
     * 
     * @param username
     *            username
     * @param groupId
     *            group identifier
     * @throws NoSuchUserException
     *             if the user with the given name doesn't exist
     */
    void addUser(String username, long groupId)
            throws NoSuchUserException;


    /**
     * Removes the user with the given name from the group with the given id.
     * 
     * @param username
     *            username
     * @param groupId
     *            group identifier
     * @throws NoSuchUserException
     *             if the user with the given name doesn't exist
     */
    void removeUser(String username, long groupId)
            throws NoSuchUserException;
}
