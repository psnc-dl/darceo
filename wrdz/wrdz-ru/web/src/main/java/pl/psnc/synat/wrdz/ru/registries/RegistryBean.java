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
import java.net.URISyntaxException;
import java.util.Date;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.ru.entity.registries.RemoteRegistry;
import pl.psnc.synat.wrdz.ru.exceptions.EntryCreationException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryModificationException;
import pl.psnc.synat.wrdz.ru.exceptions.NoSuchRegistryException;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Bean managing remote registry operations (delete, modify, add).
 */
@ManagedBean
@ViewScoped
public class RegistryBean implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 5826410143864688658L;

    /**
     * Remote registry manager providing support for remote registry operations.
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
     * Bean handling the certificate download.
     */
    @ManagedProperty(value = "#{certificateBean}")
    private CertificateBean certificateBean;

    /**
     * Remote registry primary key value, if <code>null</code> then none specified (i.e. new entity).
     */
    private Long id;

    /**
     * Semantic descriptor specified by the {@link #id}.
     */
    private RemoteRegistry registry;

    /**
     * Remote registry form data.
     */
    private RegistryFormData formData = new RegistryFormData();


    public CertificateBean getCertificateBean() {
        return certificateBean;
    }


    public void setCertificateBean(CertificateBean certificateBean) {
        this.certificateBean = certificateBean;
    }


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
     * @throws NoSuchRegistryException
     *             if specified remote registry does not exist in the database.
     */
    public void checkValidity()
            throws NoSuchRegistryException {
        if (id != null && registry == null) {
            throw new NoSuchRegistryException("No such registry in the database.");
        }
    }


    /**
     * Initializes session.
     */
    public void initSession() {
        FacesContext.getCurrentInstance().getExternalContext().getSession(true);
    }


    public Long getId() {
        return id;
    }


    /**
     * If triggered method will try to fetch the specified remote registry.
     * 
     * @param id
     *            remote registry primary key value, if <code>null</code> then none specified (i.e. new entity).
     */
    public void setId(Long id) {
        this.id = id;
        if (id != null) {
            registry = remoteRegistryManager.retrieveRemoteRegistry(id);
            if (registry != null) {
                formData = new RegistryFormData(registry);
                return;
            }
        }
    }


    public String getName() {
        return formData.getName();
    }


    /**
     * Sets the remote registry's name.
     * 
     * @param name
     *            remote registry's name.
     */
    public void setName(String name) {
        this.formData.setName(name);
    }


    public String getLocation() {
        return formData.getLocation();
    }


    /**
     * Sets the remote registry's location.
     * 
     * @param location
     *            remote registry's location.
     */
    public void setLocation(String location) {
        this.formData.setLocation(location);
    }


    public byte[] getCertificate() {
        return formData.getCertificate();
    }


    /**
     * Sets the remote registry's certificate.
     * 
     * @param certificate
     *            remote registry's certificate.
     */
    public void setCertificate(byte[] certificate) {
        this.formData.setCertificate(certificate);
    }


    public String getDescription() {
        return formData.getDescription();
    }


    /**
     * Sets the remote registry's description.
     * 
     * @param description
     *            remote registry's description.
     */
    public void setDescription(String description) {
        this.formData.setDescription(description);
    }


    public Boolean getReadEnabled() {
        return formData.getReadEnabled();
    }


    /**
     * Sets the property enabling the remote registry to harvest local registry.
     * 
     * @param readEnabled
     *            whether or not the remote registry can harvest the local one.
     */
    public void setReadEnabled(Boolean readEnabled) {
        this.formData.setReadEnabled(readEnabled);
    }


    public Boolean getHarvested() {
        return formData.getHarvested();
    }


    /**
     * Sets the property marking the registry for harvesting.
     * 
     * @param harvested
     *            whether or not the remote registry is harvested.
     */
    public void setHarvested(Boolean harvested) {
        this.formData.setHarvested(harvested);
    }


    public Date getLastHarvest() {
        return formData.getLastHarvest();
    }


    public boolean isObjectPresent() {
        return id != null;
    }


    /**
     * Handler triggered by completion of file upload.
     * 
     * @param event
     *            file upload event.
     */
    public void uploadCertificate(FileUploadEvent event) {
        UploadedFile item = event.getUploadedFile();
        formData.setCertificate(item.getData());
    }


    /**
     * Method allowing to download certificate currently used by the registry.
     */
    public void downloadCurrentCertificate() {
        certificateBean.downloadCertificate(registry);
    }


    /**
     * Clears uploaded certificate data.
     */
    public void clearFile() {
        formData.setCertificate(null);
    }


    /**
     * Modifies registry and whole substructure.
     * 
     * @return navigation directive.
     */
    public String modifyEntity() {
        if (userBrowser.isAdmin(userContext.getCallerPrincipalName())) {
            if (registry != null) {
                RemoteRegistryBuilder builder = new RemoteRegistryBuilder(registry);
                RemoteRegistry modified;
                try {
                    modified = builder.addName(formData.getName()).addLocation(formData.getLocation())
                            .addDescription(formData.getDescription()).addHarvested(formData.getHarvested())
                            .addReadEnabled(formData.getReadEnabled()).build();
                    remoteRegistryManager.updateRemoteRegistry(modified,
                        StringUtils.newStringUtf8(Base64.encodeBase64(formData.getCertificate())));
                } catch (URISyntaxException e) {
                    createErrorMessage("Passed location string does not conform to URI syntax!");
                    return null;
                } catch (EntryModificationException e) {
                    createErrorMessage(e.getMessage());
                    return null;
                }
            }
        }
        return "registries.xhtml?faces-redirect=true";
    }


    /**
     * Adds new descriptor and whole substructure.
     * 
     * @return navigation directive.
     */
    public String addEntity() {
        if (userBrowser.isAdmin(userContext.getCallerPrincipalName())) {
            if (registry == null) {
                RemoteRegistryBuilder builder = new RemoteRegistryBuilder();
                RemoteRegistry added;
                try {
                    added = builder.addName(formData.getName()).addLocation(formData.getLocation())
                            .addDescription(formData.getDescription()).addHarvested(formData.getHarvested())
                            .addReadEnabled(formData.getReadEnabled()).build();
                    remoteRegistryManager.createRemoteRegistry(added,
                        StringUtils.newStringUtf8(Base64.encodeBase64(formData.getCertificate())));
                } catch (URISyntaxException e) {
                    createErrorMessage("Passed location string does not conform to URI syntax!");
                    return null;
                } catch (EntryCreationException e) {
                    createErrorMessage(e.getMessage());
                    return null;
                }
            }
        }
        return "registries.xhtml?faces-redirect=true";

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
        FacesContext.getCurrentInstance().addMessage("location", facesMessage);
    }
}
