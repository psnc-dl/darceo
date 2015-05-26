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

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.CharacterCodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.zu.authentication.AuthenticationHelper;
import pl.psnc.synat.wrdz.zu.entity.user.Organization;
import pl.psnc.synat.wrdz.zu.entity.user.User;
import pl.psnc.synat.wrdz.zu.entity.user.UserAuthentication;
import pl.psnc.synat.wrdz.zu.exceptions.NameExistsException;
import pl.psnc.synat.wrdz.zu.permission.GroupManagementPermissionManager;
import pl.psnc.synat.wrdz.zu.permission.ObjectPermissionManager;

/**
 * Manages user operations (modify, add).
 */
@ManagedBean
@ViewScoped
public class UserBean implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -5723997954092956218L;

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(UserBean.class);

    /** Random number generator. */
    private static final Random RANDOM = new Random(new Date().getTime());

    /** Group permission manager. */
    @EJB
    private GroupManagementPermissionManager groupPermissionManager;

    /** Object permission manager. */
    @EJB
    private ObjectPermissionManager objectPermissionManager;

    /** User identifier. */
    private Long id;

    /** User entity specified by the {@link #id}. */
    private User user;

    /** Whether user can create groups. */
    private boolean groupsCreatable;

    /** Whether user can create digital objects. */
    private boolean objectsCreatable;

    /** Organizations. */
    private List<Organization> organizations;

    /** Organization map (by id). */
    private Map<Long, Organization> organizationMap;

    /** User manager. */
    @EJB
    private UserManager userManager;

    /** User browser. */
    @EJB
    private UserBrowser userBrowser;

    /** User context. */
    @EJB
    private UserContext userContext;

    /** Organization manager. */
    @EJB
    private OrganizationManager organizationManager;

    /** Authentication helper. */
    @Inject
    private AuthenticationHelper authenticationHelper;


    /**
     * Initializes the list of defined organizations.
     */
    @PostConstruct
    protected void init() {
        organizations = organizationManager.getOrganizations();
        organizationMap = new HashMap<Long, Organization>();
        for (Organization organization : organizations) {
            organizationMap.put(organization.getId(), organization);
        }
    }


    /**
     * Fetches the entity corresponding to the set id if it's not null, otherwise creates a new empty user instance.
     * Also checks if the current user has admin access rights.
     */
    public void initUser() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (!userBrowser.isAdmin(userContext.getCallerPrincipalName())) {
                HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance()
                        .getExternalContext().getResponse();
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                FacesContext.getCurrentInstance().responseComplete();
                return;
            }

            if (id != null) {
                user = userManager.getUser(id);
                groupsCreatable = groupPermissionManager.getUserCreatePermission(user.getUsername());
                objectsCreatable = objectPermissionManager.getUserCreatePermission(user.getUsername());

                if (user == null) {
                    HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance()
                            .getExternalContext().getResponse();
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    FacesContext.getCurrentInstance().responseComplete();
                    return;
                }

                for (Organization organization : organizations) {
                    if (organization.getId() == user.getOrganization().getId()) {
                        user.setOrganization(organization);
                        break;
                    }
                }
            } else {
                user = new User();
                UserAuthentication userAuth = new UserAuthentication();
                userAuth.setUser(user);
                userAuth.setActive(true);
                user.setUserData(userAuth);
            }
        }
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
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


    public String getPassword() {
        return "";
    }


    /**
     * Computes and sets a salt and a hash of the password.
     * 
     * @param password
     *            password
     */
    public void setPassword(String password) {
        if (password != null && !password.isEmpty()) {
            try {
                byte[] salt = new byte[32];
                if (user.getUserData().getPasswordSalt() == null) {
                    salt = new byte[32];
                    RANDOM.nextBytes(salt);
                } else {
                    salt = user.getUserData().getPasswordSalt().toByteArray();
                }
                user.getUserData().setPasswordHash(
                    new String(authenticationHelper.hashPassword(password.toCharArray(), salt)));
                user.getUserData().setPasswordSalt(new BigInteger(salt));
            } catch (CharacterCodingException e) {
                logger.error("Error while hashing a password", e);
                throw new WrdzRuntimeException(e);
            }
        }
    }


    public String getUsername() {
        return user.getUsername();
    }


    /**
     * Sets the username.
     * 
     * @param username
     *            the username
     */
    public void setUsername(String username) {
        user.setUsername(username);
    }


    public String getHomeDir() {
        return user.getHomeDir();
    }


    /**
     * Sets the home directory path.
     * 
     * @param homeDir
     *            the home directory path
     */
    public void setHomeDir(String homeDir) {
        user.setHomeDir(homeDir);
    }


    public Long getOrganizationId() {
        return user.getOrganization() != null ? user.getOrganization().getId() : null;
    }


    /**
     * Sets the user organization to the one matching the given id.
     * 
     * @param organizationId
     *            organization id
     */
    public void setOrganizationId(Long organizationId) {
        user.setOrganization(organizationMap.get(organizationId));
    }


    public boolean isActive() {
        return user.getUserData().getActive();
    }


    /**
     * Marks the user as active or inactive.
     * 
     * @param active
     *            whether the user is active
     */
    public void setActive(boolean active) {
        user.getUserData().setActive(active);
    }


    public String getFirstName() {
        return user.getUserData().getFirstName();
    }


    /**
     * Sets the first name.
     * 
     * @param firstName
     *            the first name
     */
    public void setFirstName(String firstName) {
        user.getUserData().setFirstName(firstName);
    }


    public String getMiddleInitial() {
        return user.getUserData().getMiddleInitial();
    }


    /**
     * Sets the middle initial.
     * 
     * @param middleInitial
     *            the middle initial
     */
    public void setMiddleInitial(String middleInitial) {
        user.getUserData().setMiddleInitial(middleInitial);
    }


    public String getLastName() {
        return user.getUserData().getLastName();
    }


    /**
     * Sets the last name.
     * 
     * @param lastName
     *            the last name
     */
    public void setLastName(String lastName) {
        user.getUserData().setLastName(lastName);
    }


    public String getDisplayName() {
        return user.getUserData().getDisplayName();
    }


    /**
     * Sets the display name.
     * 
     * @param displayName
     *            the display name
     */
    public void setDisplayName(String displayName) {
        user.getUserData().setDisplayName(displayName);
    }


    public String getEmail() {
        return user.getUserData().getEmail();
    }


    /**
     * Sets the email address.
     * 
     * @param email
     *            the email address
     */
    public void setEmail(String email) {
        user.getUserData().setEmail(email);
    }


    public boolean isAdmin() {
        return user.isAdmin();
    }


    /**
     * Sets the admin property.
     * 
     * @param admin
     *            whether the user is an administrator
     */
    public void setAdmin(boolean admin) {
        user.setAdmin(admin);
    }


    /**
     * Checks whether the entity contains a password hash.
     * 
     * @return <code>true</code> if the entity has a password set, <code>false</code> otherwise
     */
    public boolean isPasswordSet() {
        return user.getUserData().getPasswordHash() != null;
    }


    /**
     * Checks whether the entity contains a certificate.
     * 
     * @return <code>true</code> if the entity has a certificate set, <code>false</code> otherwise
     */
    public boolean isCertificateSet() {
        return user.getUserData().getCertificate() != null && !user.getUserData().getCertificate().isEmpty();
    }


    public List<Organization> getOrganizations() {
        return organizations;
    }


    /**
     * Handler triggered by completion of file upload.
     * 
     * @param event
     *            file upload event.
     */
    public void uploadCertificate(FileUploadEvent event) {
        UploadedFile item = event.getUploadedFile();
        String certificate = StringUtils.newStringUtf8(Base64.encodeBase64(item.getData()));
        user.getUserData().setCertificate(certificate);
    }


    /**
     * Downloads the current certificate.
     */
    public void downloadCertificate() {
        if (user.getUserData().getCertificate() != null) {
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
                    .getResponse();
            response.setHeader("Content-Disposition", "attachment;filename=" + user.getUserData().getDisplayName()
                    + ".der");
            try {
                String pemCertificate = user.getUserData().getCertificate();
                byte[] derCertificate = Base64.decodeBase64(pemCertificate);
                response.getOutputStream().write(derCertificate);
                response.setContentLength(derCertificate.length);
                response.getOutputStream().flush();
                response.getOutputStream().close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            FacesContext.getCurrentInstance().responseComplete();
        }
    }


    /**
     * Clears uploaded certificate data.
     */
    public void clearCertificate() {
        user.getUserData().setCertificate(null);
    }


    /**
     * Creates a new user entity.
     * 
     * @return navigation directive.
     */
    public String createUser() {
        try {
            if (userBrowser.isAdmin(userContext.getCallerPrincipalName())) {
                userManager.createUser(user, groupsCreatable, objectsCreatable);
            }
            return "users.xhtml?faces-redirect=true";
        } catch (NameExistsException e) {
            FacesMessage facesMessage = new FacesMessage("Username already taken.");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage("userForm:username", facesMessage);
            return null;
        }
    }


    /**
     * Modifies the user entity.
     * 
     * @return navigation directive.
     */
    public String modifyUser() {
        try {
            if (userBrowser.isAdmin(userContext.getCallerPrincipalName())) {
                userManager.modifyUser(user, groupsCreatable, objectsCreatable);
            }
            return "users.xhtml?faces-redirect=true";
        } catch (NameExistsException e) {
            FacesMessage facesMessage = new FacesMessage("Username already taken.");
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage("userForm:username", facesMessage);
            return null;
        }
    }
}
