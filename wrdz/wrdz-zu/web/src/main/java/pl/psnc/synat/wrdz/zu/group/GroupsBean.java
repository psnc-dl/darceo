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
package pl.psnc.synat.wrdz.zu.group;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication;
import pl.psnc.synat.wrdz.zu.permission.GroupManagementPermissionManager;
import pl.psnc.synat.wrdz.zu.types.ManagementPermissionType;
import pl.psnc.synat.wrdz.zu.user.GroupManager;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Manages public group listing and deletion.
 */
@ManagedBean
@ViewScoped
public class GroupsBean {

    /** Group manager. */
    @EJB
    private GroupManager groupManager;

    /** Group permission manager. */
    @EJB
    private GroupManagementPermissionManager permissionManager;

    /** User browser. */
    @EJB
    private UserBrowser userBrowser;

    /** User context. */
    @EJB
    private UserContext userContext;

    /** Groups. */
    private List<GroupAuthentication> groups;

    /** Whether the current user is an admin. */
    private boolean isAdmin;

    /** Readable group identifiers. */
    private Set<Long> readableIds;

    /** Updateable group identifiers. */
    private Set<Long> updateableIds;

    /** Grantable group identifiers. */
    private Set<Long> grantableIds;


    /**
     * Returns the cached group list.
     * 
     * @return the group list
     */
    public List<GroupAuthentication> getGroups() {
        if (groups == null) {
            refresh();
        }
        return groups;
    }


    /**
     * Refreshes the cached groups.
     */
    public void refresh() {

        isAdmin = userBrowser.isAdmin(userContext.getCallerPrincipalName());

        Set<Long> groupIds = null;
        if (!isAdmin) {
            readableIds = new HashSet<Long>(permissionManager.fetchWithPermission(userContext.getCallerPrincipalName(),
                ManagementPermissionType.READ));
            updateableIds = new HashSet<Long>(permissionManager.fetchWithPermission(
                userContext.getCallerPrincipalName(), ManagementPermissionType.UPDATE));
            grantableIds = new HashSet<Long>(permissionManager.fetchWithPermission(
                userContext.getCallerPrincipalName(), ManagementPermissionType.GRANT));

            groupIds = new HashSet<Long>();
            groupIds.addAll(readableIds);
            groupIds.addAll(updateableIds);
            groupIds.addAll(grantableIds);

            if (!groupIds.isEmpty()) {
                groups = groupManager.getGroups(new ArrayList<Long>(groupIds));
            } else {
                groups = new ArrayList<GroupAuthentication>();
            }
        } else {
            groups = groupManager.getPublicGroups();
        }

    }


    /**
     * Checks whether the user has the given permission to the group with the given identifier.
     * 
     * @param groupIdentifier
     *            group identifier (database primary key)
     * @param permission
     *            required permission
     * @return <code>true</code> if the user has the required permission to the given resource, <code>false</code>
     *         otherwise
     */
    public boolean hasPermission(Long groupIdentifier, String permission) {
        if (isAdmin) {
            return true;
        }

        if (ManagementPermissionType.READ.name().equals(permission.toUpperCase())) {
            return readableIds.contains(groupIdentifier);
        }
        if (ManagementPermissionType.UPDATE.name().equals(permission.toUpperCase())) {
            return updateableIds.contains(groupIdentifier);
        }
        if (ManagementPermissionType.GRANT.name().equals(permission.toUpperCase())) {
            return grantableIds.contains(groupIdentifier);
        }
        return false;
    }


    /**
     * Checks whether the user has the CREATE group permission.
     * 
     * @return <code>true</code> if the user has the CREATE permission, <code>false</code> otherwise
     */
    public boolean hasCreatePermission() {
        if (isAdmin) {
            return true;
        }

        return permissionManager.hasPermission(userContext.getCallerPrincipalName(), null,
            ManagementPermissionType.CREATE);
    }


    /**
     * Deletes the group entity.
     * 
     * @param groupId
     *            group identifier
     * @return navigation directive
     */
    public String deleteGroup(long groupId) {
        if (permissionManager.hasPermission(userContext.getCallerPrincipalName(), groupId,
            ManagementPermissionType.UPDATE)) {
            groupManager.deleteGroup(groupId);
        }
        return "groups.xhtml?faces-redirect=true";
    }

}
