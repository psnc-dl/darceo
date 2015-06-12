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
package pl.psnc.synat.wrdz.zu.permission;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.psnc.synat.wrdz.zu.exceptions.NotAuthorizedException;
import pl.psnc.synat.wrdz.zu.types.PermissionType;

/**
 * Base interface for all permission managers.
 * 
 * @param <T>
 *            type of permissions managed.
 */
public interface PermissionManager<T extends PermissionType> extends Serializable {

    /**
     * Retrieves the identifiers of those resources that the user with the given name has the given permission to.
     * 
     * @param username
     *            name of the user
     * @param permission
     *            desired permission
     * @return list of resource identifiers
     */
    List<Long> fetchWithPermission(String username, T permission);


    /**
     * Checks whether or not particular user has particular permissions to the given resource.
     * 
     * @param username
     *            name of the user for whom the permissions are checked.
     * @param resourceId
     *            resource for which the permissions are checked.
     * @param permissionType
     *            type of checked permission.
     * @return <code>true</code> if user has given permission to the resource, <code>false</code> otherwise.
     */
    boolean hasPermission(String username, Long resourceId, T permissionType);


    /**
     * Same as {@link #hasPermission(String, Long, PermissionType)}, except an exception is thrown if the given user
     * does not have the permission to the given resource.
     * 
     * @param username
     *            name of the user
     * @param resourceId
     *            resource identifier
     * @param permissionType
     *            required permission
     * @throws NotAuthorizedException
     *             if the check fails
     */
    void checkPermission(String username, Long resourceId, T permissionType)
            throws NotAuthorizedException;


    /**
     * Sets the permissions to the given resource.
     * 
     * @param username
     *            name of the resource owner
     * @param resourceId
     *            resource for which the permissions are set.
     */
    void setOwnerPermissions(String username, long resourceId);


    /**
     * Retrieves a map of all user permissions to the given resource.
     * 
     * @param resourceId
     *            resource identifier
     * @return all existing user permissions in the form of a <username, permissions> map
     */
    Map<String, Set<T>> getUserPermissions(long resourceId);


    /**
     * Changes the permissions to the given resource for the user with the given username.
     * 
     * Permissions not present in the given set are be removed if they have been set before.
     * 
     * @param username
     *            name of the user
     * @param resourceId
     *            resource identifier
     * @param permissions
     *            desired permissions
     */
    void setUserPermissions(String username, long resourceId, Set<T> permissions);


    /**
     * Retrieves a map of all group permissions to the given resource.
     * 
     * @param resourceId
     *            resource identifier
     * @return all existing group permissions in the form of a <groupname, permissions> map
     */
    Map<String, Set<T>> getGroupPermissions(long resourceId);


    /**
     * Changes the permissions to the given resource for the group with the given groupname.
     * 
     * Permissions not present in the given set are be removed if they have been set before.
     * 
     * @param groupname
     *            name of the group
     * @param resourceId
     *            resource identifier
     * @param permissions
     *            desired permissions
     */
    void setGroupPermissions(String groupname, long resourceId, Set<T> permissions);


    /**
     * Returns <code>true</code> if the user has the create permission <strong>explicitly</strong> set for them.
     * 
     * Permissions inherited from groups are not taken into account.
     * 
     * @param username
     *            name of the user
     * @return <code>true</code> if the user has the create permission, <code>false</code> otherwise
     */
    boolean getUserCreatePermission(String username);


    /**
     * Sets the permission that allows to create resources of the <code>T</code> type.
     * 
     * @param username
     *            name of the user for whom the permission is set.
     * @param value
     *            create permission value
     */
    void setUserCreatePermission(String username, boolean value);


    /**
     * Returns <code>true</code> if the group has the create permission.
     * 
     * @param groupname
     *            name of the group
     * @return <code>true</code> if the group has the create permission, <code>false</code> otherwise
     */
    boolean getGroupCreatePermission(String groupname);


    /**
     * Sets the permission that allows to create resources of the <code>T</code> type.
     * 
     * @param groupname
     *            name of the group for whom the permission is set.
     * @param value
     *            create permission value
     */
    void setGroupCreatePermission(String groupname, boolean value);


    /**
     * Removes all permissions to the given resource.
     * 
     * @param resourceId
     *            resource identifier
     */
    void removePermissions(long resourceId);


    /**
     * Removes all permissions for the given group.
     * 
     * @param groupId
     *            group identifier
     */
    void removePermissionsForGroup(long groupId);

}
