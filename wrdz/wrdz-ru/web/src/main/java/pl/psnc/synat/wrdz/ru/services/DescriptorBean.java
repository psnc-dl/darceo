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
package pl.psnc.synat.wrdz.ru.services;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.exceptions.AccessDeniedException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryCreationException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryModificationException;
import pl.psnc.synat.wrdz.ru.exceptions.IllegalRegistryOperationException;
import pl.psnc.synat.wrdz.ru.exceptions.NoSuchDescriptorException;
import pl.psnc.synat.wrdz.ru.services.descriptors.SemanticDescriptorManager;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Bean managing descriptor operations (delete, modify, add).
 */
@ManagedBean
@ViewScoped
public class DescriptorBean implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 5826410143864688658L;

    /**
     * Semantic descriptor manager.
     */
    @EJB
    private transient SemanticDescriptorManager semanticDescriptorManager;

    /** User browser. */
    @EJB(name = "UserBrowser")
    private UserBrowser userBrowser;

    /** User context. */
    @EJB
    private UserContext userContext;

    /**
     * Semantic descriptor primary key value, if <code>null</code> then none specified (i.e. new entity).
     */
    private Long id;

    /**
     * Semantic descriptor specified by the {@link #id}.
     */
    private SemanticDescriptor descriptor;

    /**
     * Location of the semantic descriptor.
     */
    private String owlsLocation = null;

    /**
     * Whether or not the descriptor is exposed (public visibility vs. private).
     */
    private Boolean exposed = Boolean.FALSE;


    /**
     * Checks if the current user has admin access rights.
     * 
     * @throws IOException
     *             if redirect to the error page fails
     */
    public void checkRights()
            throws IOException {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (!userBrowser.isAdmin(userContext.getCallerPrincipalName())) {
                HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance()
                        .getExternalContext().getResponse();
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                FacesContext.getCurrentInstance().responseComplete();
                return;
            }
        }
    }


    /**
     * Checks validity of the request.
     * 
     * @throws NoSuchDescriptorException
     *             if specified descriptor does not exist in the database.
     */
    public void checkValidity()
            throws NoSuchDescriptorException {
        if (id != null && descriptor == null) {
            throw new NoSuchDescriptorException("No such service in the database.");
        }
    }


    public Long getId() {
        return id;
    }


    /**
     * If triggered method will try to fetch the specified semantic descriptor.
     * 
     * @param id
     *            semantic descriptor primary key value, if <code>null</code> then none specified (i.e. new entity).
     */
    public void setId(Long id) {
        this.id = id;
        if (id != null) {
            descriptor = semanticDescriptorManager.retrieveActiveDescriptor(id);
            if (descriptor != null) {
                exposed = descriptor.isExposed();
            }
        }
    }


    public String getOwlsLocation() {
        return owlsLocation;
    }


    public void setOwlsLocation(String owlsLocation) {
        this.owlsLocation = owlsLocation;
    }


    public String getLocationUrl() {
        return descriptor.getLocationUrl();
    }


    public boolean isExposed() {
        return exposed;
    }


    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }


    public boolean isObjectPresent() {
        return id != null;
    }


    /**
     * Modifies descriptor and whole substructure.
     * 
     * @return navigation directive.
     */
    public String modifyEntity() {
        try {
            if (userBrowser.isAdmin(userContext.getCallerPrincipalName())) {
                if (owlsLocation != null && !owlsLocation.trim().isEmpty()) {
                    descriptor = semanticDescriptorManager.modifyDescriptor(descriptor, new URI(owlsLocation), exposed);
                } else {
                    descriptor = semanticDescriptorManager.modifyDescriptor(descriptor, null, exposed);
                }
            }
        } catch (EntryModificationException e) {
            createErrorMessage(e);
            return null;
        } catch (IllegalRegistryOperationException e) {
            createErrorMessage(e);
            return null;
        } catch (AccessDeniedException e) {
            createErrorMessage(e);
            return null;
        } catch (URISyntaxException e) {
            createErrorMessage("Passed string does not conform to URI syntax!");
            return null;
        }
        return "services.xhtml?faces-redirect=true";
    }


    /**
     * Adds new descriptor and whole substructure.
     * 
     * @return navigation directive.
     */
    public String addEntity() {
        try {
            if (userBrowser.isAdmin(userContext.getCallerPrincipalName())) {
                semanticDescriptorManager.createDescriptor(new URI(owlsLocation), exposed);
            }
        } catch (EntryCreationException e) {
            createErrorMessage(e);
            return null;
        } catch (AccessDeniedException e) {
            createErrorMessage(e);
            return null;
        } catch (URISyntaxException e) {
            createErrorMessage("Passed string does not conform to URI syntax!");
            return null;
        }
        return "services.xhtml?faces-redirect=true";
    }


    /**
     * Constructs new error message with a given cause.
     * 
     * @param cause
     *            error cause.
     */
    private void createErrorMessage(Throwable cause) {
        FacesMessage facesMessage = new FacesMessage(cause.getLocalizedMessage());
        facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
        FacesContext.getCurrentInstance().addMessage("descriptor:location", facesMessage);
    }


    /**
     * Constructs new error message with a given message contents.
     * 
     * @param message
     *            error message contents.
     */
    private void createErrorMessage(String message) {
        FacesMessage facesMessage = new FacesMessage(message);
        facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
        FacesContext.getCurrentInstance().addMessage("descriptor:location", facesMessage);
    }
}
