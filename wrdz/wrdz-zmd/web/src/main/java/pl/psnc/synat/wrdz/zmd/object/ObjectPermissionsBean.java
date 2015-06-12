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
package pl.psnc.synat.wrdz.zmd.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;

import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.zmd.i18n.ZmdMessageUtils;
import pl.psnc.synat.wrdz.zu.permission.ObjectPermissionManager;
import pl.psnc.synat.wrdz.zu.types.ObjectPermissionType;

/**
 * Manages permissions to a single object.
 */
@ManagedBean
@ViewScoped
public class ObjectPermissionsBean implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 2685142056063580382L;

    /** Object identifier. */
    private Long id;

    /** Permissions assigned to users. */
    private Map<String, Set<ObjectPermissionType>> userPermissions;

    /** Permissions assigned to groups. */
    private Map<String, Set<ObjectPermissionType>> groupPermissions;

    /** List of user names that have assigned permissions. */
    private List<String> users;

    /** List of group names that have assigned permissions. */
    private List<String> groups;

    /** Name of the user/group that will have their permissions added/edited. */
    private String name;

    /** Whether the form sets permissions for a user or a group. */
    private boolean user;

    /** Whether the form adds new or edits existing permissions. */
    private boolean modification;

    /** Permissions set in the add/edit form. */
    private Set<ObjectPermissionType> permissions;

    /** Permissions to choose from. */
    private SelectItem[] permissionItems;

    /** Object permission manager. */
    @EJB(name = "ObjectPermissionManager")
    private ObjectPermissionManager permissionManager;

    /** User context. */
    @EJB
    private UserContext userContext;


    /**
     * Initializes object permissions and checks if the current user has the required access rights.
     */
    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (id != null) {

                if (!permissionManager.hasPermission(userContext.getCallerPrincipalName(), id,
                    ObjectPermissionType.GRANT)) {
                    errorPage(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                userPermissions = permissionManager.getUserPermissions(id);
                groupPermissions = permissionManager.getGroupPermissions(id);

                users = new ArrayList<String>();
                users.addAll(userPermissions.keySet());
                Collections.sort(users);

                groups = new ArrayList<String>();
                groups.addAll(groupPermissions.keySet());
                Collections.sort(groups);

                permissionItems = new SelectItem[6];
                permissionItems[0] = new SelectItem(ObjectPermissionType.READ,
                        ZmdMessageUtils.getMessage("permissions.read"));
                permissionItems[1] = new SelectItem(ObjectPermissionType.UPDATE,
                        ZmdMessageUtils.getMessage("permissions.update"));
                permissionItems[2] = new SelectItem(ObjectPermissionType.DELETE,
                        ZmdMessageUtils.getMessage("permissions.delete"));
                permissionItems[3] = new SelectItem(ObjectPermissionType.GRANT,
                        ZmdMessageUtils.getMessage("permissions.grant"));
                permissionItems[4] = new SelectItem(ObjectPermissionType.OAI_PMH_READ,
                        ZmdMessageUtils.getMessage("permissions.oai_pmh_read"));
                permissionItems[5] = new SelectItem(ObjectPermissionType.METADATA_UPDATE,
                        ZmdMessageUtils.getMessage("permissions.metadata_update"));
            } else {
                errorPage(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public List<String> getUsers() {
        return users;
    }


    public List<String> getGroups() {
        return groups;
    }


    /**
     * Checks whether the given user has the given permission.
     * 
     * @param username
     *            user name
     * @param permission
     *            object permission type
     * @return <code>true</code> if the permission is set, <code>false</code> otherwise
     */
    public boolean hasUserPermission(String username, String permission) {
        return userPermissions.containsKey(username) ? userPermissions.get(username).contains(
            ObjectPermissionType.valueOf(permission)) : false;
    }


    /**
     * Checks whether the given group has the given permission.
     * 
     * @param groupname
     *            group name
     * @param permission
     *            object permission type
     * @return <code>true</code> if the permission is set, <code>false</code> otherwise
     */
    public boolean hasGroupPermission(String groupname, String permission) {
        return groupPermissions.containsKey(groupname) ? groupPermissions.get(groupname).contains(
            ObjectPermissionType.valueOf(permission)) : false;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public boolean isModification() {
        return modification;
    }


    public SelectItem[] getPermissionItems() {
        return permissionItems;
    }


    public Set<ObjectPermissionType> getPermissions() {
        return permissions;
    }


    /**
     * Sets the permissions, automatically adding any implied permissions missing from the given set.
     * 
     * @param permissions
     *            permissions to be set
     */
    public void setPermissions(Set<ObjectPermissionType> permissions) {
        ObjectPermissionType.addImpliedPermissions(permissions);
        this.permissions = permissions;
    }


    /**
     * Prepares the form to add or edit user permissions.
     * 
     * @param username
     *            user name; can be <code>null</code>
     */
    public void prepareUserForm(String username) {
        name = username;
        user = true;
        modification = username != null && !username.isEmpty();
        permissions = username != null ? userPermissions.get(username) : EnumSet.noneOf(ObjectPermissionType.class);
    }


    /**
     * Prepares the form to add or edit group permissions.
     * 
     * @param groupname
     *            group name; can be <code>null</code>
     */
    public void prepareGroupForm(String groupname) {
        name = groupname;
        user = false;
        modification = groupname != null && !groupname.isEmpty();
        permissions = groupname != null ? groupPermissions.get(groupname) : EnumSet.noneOf(ObjectPermissionType.class);
    }


    /**
     * Saves the permissions set in the form.
     * 
     * @return navigation directive
     */
    public String save() {

        if (permissionManager.hasPermission(userContext.getCallerPrincipalName(), id, ObjectPermissionType.GRANT)) {
            if (user) {
                permissionManager.setUserPermissions(name, id, permissions);
            } else {
                permissionManager.setGroupPermissions(name, id, permissions);
            }
        }
        return "object.xhtml?faces-redirect=true&id=" + id;
    }


    /**
     * Displays an error page with the given http status.
     * 
     * @param status
     *            http status
     */
    private void errorPage(int status) {
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
                .getResponse();
        response.setStatus(status);
        FacesContext.getCurrentInstance().responseComplete();
    }
}
