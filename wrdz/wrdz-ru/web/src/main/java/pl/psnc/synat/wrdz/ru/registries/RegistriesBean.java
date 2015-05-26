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
package pl.psnc.synat.wrdz.ru.registries;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.ru.entity.registries.RemoteRegistry;
import pl.psnc.synat.wrdz.ru.exceptions.EntryDeletionException;
import pl.psnc.synat.wrdz.ru.exceptions.NoSuchRegistryException;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Bean for supporting listing of remote registries.
 */
@ManagedBean
@ViewScoped
public class RegistriesBean implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 7463485681855409557L;

    /**
     * Remote registry manager providing support for registries listing.
     */
    @EJB
    private transient RemoteRegistryManager remoteRegistryManager;

    /** User browser. */
    @EJB(name = "UserBrowser")
    private UserBrowser userBrowser;

    /** User context. */
    @EJB
    private UserContext userContext;

    /**
     * Bean handling certificate download.
     */
    @ManagedProperty(value = "#{certificateBean}")
    private CertificateBean certificateBean;


    public CertificateBean getCertificateBean() {
        return certificateBean;
    }


    public void setCertificateBean(CertificateBean certificateBean) {
        this.certificateBean = certificateBean;
    }


    /**
     * List of the registries.
     */
    private List<RemoteRegistry> registries;

    /**
     * Registries location, can be a Java RegExp compliant string.
     */
    private String location;

    /**
     * Whether or not the remote registry is enabled to read local registry.
     */
    private Boolean readEnabled;

    /**
     * Whether or not local registry can harvest the remote registry.
     */
    private Boolean harvested;


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
     * Fetches the list of registries using passed parameters.
     */
    public void init() {
        if (registries == null) {
            registries = remoteRegistryManager.retrieveRemoteRegistries(location, readEnabled, harvested);
        }
    }


    /**
     * Initializes session.
     */
    public void initSession() {
        FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    }


    /**
     * Checks the emptiness of registries list.
     * 
     * @return whether the registries list is empty (<code>true</code>) or not (<code>false</code>).
     */
    public boolean isListEmpty() {
        if (registries == null || registries.size() == 0) {
            return true;
        }
        return false;
    }


    public List<RemoteRegistry> getRegistries() {
        return registries;
    }


    public void setRegistries(List<RemoteRegistry> registries) {
        this.registries = registries;
    }


    public String getLocation() {
        return location;
    }


    public void setLocation(String location) {
        this.location = location;
    }


    public Boolean getReadEnabled() {
        return readEnabled;
    }


    public void setReadEnabled(Boolean readEnabled) {
        this.readEnabled = readEnabled;
    }


    public Boolean getHarvested() {
        return harvested;
    }


    public void setHarvested(Boolean harvested) {
        this.harvested = harvested;
    }


    /**
     * Deletes the specified remote registry.
     * 
     * @param id
     *            remote registry's identifier.
     * @return navigation action.
     */
    public String deleteEntity(long id) {
        try {
            if (userBrowser.isAdmin(userContext.getCallerPrincipalName())) {
                remoteRegistryManager.deleteRemoteRegistry(id);
            }
        } catch (EntryDeletionException e) {
            FacesMessage facesMessage = new FacesMessage(e.getLocalizedMessage());
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage("services", facesMessage);
            return null;
        }
        return "registries.xhtml?faces-redirect=true";
    }


    /**
     * Downloads remote registry's certificate.
     * 
     * @param id
     *            remote registry's identifier.
     * @throws NoSuchRegistryException
     *             if no registry with specified identifier was found.
     */
    public void downloadCertificate(long id)
            throws NoSuchRegistryException {
        RemoteRegistry registry = remoteRegistryManager.retrieveRemoteRegistry(id);
        if (registry != null) {
            certificateBean.downloadCertificate(registry);
        } else {
            throw new NoSuchRegistryException("Specified registry not found");
        }
    }

}
