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

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication;
import pl.psnc.synat.wrdz.zu.exceptions.NameExistsException;
import pl.psnc.synat.wrdz.zu.permission.GroupManagementPermissionManager;
import pl.psnc.synat.wrdz.zu.permission.ObjectPermissionManager;
import pl.psnc.synat.wrdz.zu.types.ManagementPermissionType;
import pl.psnc.synat.wrdz.zu.user.GroupManager;

/**
 * Manages group operations (modify, add).
 */
@ManagedBean
@ViewScoped
public class GroupBean implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -4581185122874150819L;

    /** Group identifier. */
    private Long id;

    /** Group entity specified by the {@link #id}. */
    private GroupAuthentication group;

    /** Group manager. */
    @EJB
    private GroupManager groupManager;

    /** Group permission manager. */
    @EJB
    private GroupManagementPermissionManager groupPermissionManager;

    /** Object permission manager. */
    @EJB
    private ObjectPermissionManager objectPermissionManager;

    /** User context. */
    @EJB
    private UserContext userContext;

    /** Whether users belonging to this group can create groups. */
    private boolean groupsCreatable;

    /** Whether users belonging to this group can create digital objects. */
    private boolean objectsCreatable;


    /**
     * Fetches the entity corresponding to the set id if it's not null, otherwise creates a new empty group instance.
     * Also checks if the current user has the required access rights.
     */
    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (id != null) {

                group = groupManager.getGroup(id);
                if (group == null) {
                    errorPage(HttpServletResponse.SC_NOT_FOUND);
                } else if (group.isSingleUser()) {
                    errorPage(HttpServletResponse.SC_FORBIDDEN);
                }

                if (!groupPermissionManager.hasPermission(userContext.getCallerPrincipalName(), id,
                    ManagementPermissionType.UPDATE)) {
                    errorPage(HttpServletResponse.SC_FORBIDDEN);
                }

                groupsCreatable = groupPermissionManager.getGroupCreatePermission(group.getGroupname());
                objectsCreatable = objectPermissionManager.getGroupCreatePermission(group.getGroupname());
            } else {

                if (!groupPermissionManager.hasPermission(userContext.getCallerPrincipalName(), null,
                    ManagementPermissionType.CREATE)) {
                    errorPage(HttpServletResponse.SC_FORBIDDEN);
                }

                group = new GroupAuthentication();
            }
        }
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getGroupname() {
        return group.getGroupname();
    }


    /**
     * Sets the groupname.
     * 
     * @param groupname
     *            the groupname
     */
    public void setGroupname(String groupname) {
        group.setGroupname(groupname);
    }


    public boolean isGroupsCreatable() {
        return groupsCreatable;
    }


    public boolean isObjectsCreatable() {
        return objectsCreatable;
    }


    public void setGroupsCreatable(boolean groupsCreatable) {
        this.groupsCreatable = groupsCreatable;
    }


    public void setObjectsCreatable(boolean objectsCreatable) {
        this.objectsCreatable = objectsCreatable;
    }


    /**
     * Creates a new group entity.
     * 
     * @return navigation directive.
     */
    public String createGroup() {
        try {
            if (groupPermissionManager.hasPermission(userContext.getCallerPrincipalName(), null,
                ManagementPermissionType.CREATE)) {
                groupManager.createGroup(group, groupsCreatable, objectsCreatable);
            }
            return "groups.xhtml?faces-redirect=true";
        } catch (NameExistsException e) {
            FacesMessage facesMessage = new FacesMessage("Groupname already taken.");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage("groupForm:groupname", facesMessage);
            return null;
        }
    }


    /**
     * Modifies the group entity.
     * 
     * @return navigation directive.
     */
    public String modifyGroup() {

        try {
            if (groupPermissionManager.hasPermission(userContext.getCallerPrincipalName(), group.getId(),
                ManagementPermissionType.UPDATE)) {
                groupManager.modifyGroup(group, groupsCreatable, objectsCreatable);
            }
            return "groups.xhtml?faces-redirect=true";
        } catch (NameExistsException e) {
            FacesMessage facesMessage = new FacesMessage("Groupname already taken.");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage("groupForm:groupname", facesMessage);
            return null;
        }
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
