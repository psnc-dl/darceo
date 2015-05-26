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
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication;
import pl.psnc.synat.wrdz.zu.entity.user.UserAuthentication;
import pl.psnc.synat.wrdz.zu.exceptions.NoSuchUserException;
import pl.psnc.synat.wrdz.zu.i18n.ZuMessageUtils;
import pl.psnc.synat.wrdz.zu.permission.GroupManagementPermissionManager;
import pl.psnc.synat.wrdz.zu.types.ManagementPermissionType;
import pl.psnc.synat.wrdz.zu.user.GroupAssignmentManager;
import pl.psnc.synat.wrdz.zu.user.GroupManager;

/**
 * Manages group membership (adding and removing users).
 */
@ManagedBean
@ViewScoped
public class GroupMembershipBean implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 7762356663743737459L;

    /** Group identifier. */
    private Long id;

    /** Group entity specified by the {@link #id}. */
    private GroupAuthentication group;

    /** Username. */
    private String username;

    /** Group manager. */
    @EJB
    private GroupManager groupManager;

    /** Group assignment manaager. */
    @EJB
    private GroupAssignmentManager groupAssignmentManager;

    /** Group permission manager. */
    @EJB
    private GroupManagementPermissionManager permissionManager;

    /** User context. */
    @EJB
    private UserContext userContext;


    /**
     * Fetches the entity corresponding to the set id and checks if the current user has the required access rights.
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

                if (!permissionManager.hasPermission(userContext.getCallerPrincipalName(), id,
                    ManagementPermissionType.UPDATE)) {
                    errorPage(HttpServletResponse.SC_FORBIDDEN);
                }

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


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    /**
     * Lists current group users.
     * 
     * @return list of users assigned to the group
     */
    public List<UserAuthentication> getUsers() {
        return groupAssignmentManager.getUsers(id);
    }


    /**
     * Adds user to the group.
     * 
     * @return navigation directive.
     */
    public String addUser() {
        try {
            if (permissionManager.hasPermission(userContext.getCallerPrincipalName(), id,
                ManagementPermissionType.UPDATE)) {
                groupAssignmentManager.addUser(username, id);
            }
            return "groupMembership.xhtml?id=" + id + "&faces-redirect=true";
        } catch (NoSuchUserException e) {
            FacesMessage facesMessage = new FacesMessage(ZuMessageUtils.getMessage("members.notfound"));
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage("userForm:username", facesMessage);
            return null;
        }
    }


    /**
     * Removes the given user from the group.
     * 
     * @param username
     *            username
     * @return navigation directive.
     */
    public String removeUser(String username) {
        try {
            if (permissionManager.hasPermission(userContext.getCallerPrincipalName(), id,
                ManagementPermissionType.UPDATE)) {
                groupAssignmentManager.removeUser(username, id);
            }
            return "groupMembership.xhtml?id=" + id + "&faces-redirect=true";
        } catch (NoSuchUserException e) {
            FacesMessage facesMessage = new FacesMessage(ZuMessageUtils.getMessage("members.notfound"));
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage("userForm:username", facesMessage);
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
