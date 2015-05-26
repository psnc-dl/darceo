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

import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication;
import pl.psnc.synat.wrdz.zu.exceptions.NameExistsException;

/**
 * Manages user groups.
 */
@Local
public interface GroupManager {

    /**
     * Fetches the group with the given id.
     * 
     * @param id
     *            group identifier
     * @return the found group, or <code>null</code>
     */
    GroupAuthentication getGroup(long id);


    /**
     * Fetches the groups with the given ids.
     * 
     * @param ids
     *            group identifiers
     * @return the found groups
     */
    List<GroupAuthentication> getGroups(List<Long> ids);


    /**
     * Lists all groups that aren't private (single-user).
     * 
     * @return public groups
     */
    List<GroupAuthentication> getPublicGroups();


    /**
     * Lists all private (single-user) groups.
     * 
     * @return private groups
     */
    List<GroupAuthentication> getPrivateGroups();


    /**
     * Creates a new public group.
     * 
     * @param group
     *            the group to be created
     * @param groupsCreatable
     *            whether users belonging to this group can create groups
     * @param objectsCreatable
     *            whether users belonging to this group can create digital objects
     * @return the created group
     * @throws NameExistsException
     *             if the given group name is already taken
     */
    GroupAuthentication createGroup(GroupAuthentication group, boolean groupsCreatable, boolean objectsCreatable)
            throws NameExistsException;


    /**
     * Modifies an existing public group.
     * 
     * @param group
     *            group modification
     * @param groupsCreatable
     *            whether users belonging to this group can create groups
     * @param objectsCreatable
     *            whether users belonging to this group can create digital objects
     * @return the modified group
     * @throws NameExistsException
     *             if the given group name is already taken
     */
    GroupAuthentication modifyGroup(GroupAuthentication group, boolean groupsCreatable, boolean objectsCreatable)
            throws NameExistsException;


    /**
     * Deletes the group with the given id.
     * 
     * @param groupId
     *            group identifier
     */
    void deleteGroup(long groupId);
}
