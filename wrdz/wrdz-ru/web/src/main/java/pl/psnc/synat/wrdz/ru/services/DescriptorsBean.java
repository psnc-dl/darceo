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

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.exceptions.EntryDeletionException;
import pl.psnc.synat.wrdz.ru.exceptions.IllegalRegistryOperationException;
import pl.psnc.synat.wrdz.ru.services.descriptors.SemanticDescriptorManager;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Bean managing descriptors list operations.
 */
@ManagedBean
@ViewScoped
public class DescriptorsBean implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 5826410143864688658L;

    /**
     * Semantic descriptor manager.
     */
    @EJB
    private transient SemanticDescriptorManager descriptorManager;

    /** User context. */
    @EJB
    private UserContext userContext;

    /** User browser. */
    @EJB(name = "UserBrowser")
    private UserBrowser userBrowser;

    /** Fetched list of semantic descriptors. */
    private List<SemanticDescriptor> descriptors;

    /**
     * Whether or not the fetched descriptors should be exposed (public visibility vs. private).
     */
    private Boolean exposed;

    /**
     * Whether or not the descriptor is locally defined.
     */
    private Boolean local;


    /**
     * Refreshes the underlying object model.
     */
    public void refresh() {
        descriptors = descriptorManager.retrieveActiveDescriptors(exposed, local);
    }


    /**
     * Returns the cached descriptors.
     * 
     * @return cached descriptors
     */
    public List<SemanticDescriptor> getDescriptors() {
        if (descriptors == null) {
            refresh();
        }
        return descriptors;
    }


    public Boolean getExposed() {
        return exposed;
    }


    public void setExposed(Boolean exposed) {
        this.exposed = exposed;
    }


    public Boolean getLocal() {
        return local;
    }


    public void setLocal(Boolean local) {
        this.local = local;
    }


    /**
     * Deletes the semantic descriptor with specified primary key value.
     * 
     * @param id
     *            identifier of the semantic descriptor to be deleted.
     * @return navigation directive.
     */
    public String deleteEntity(long id) {
        try {
            if (userBrowser.isAdmin(userContext.getCallerPrincipalName())) {
                descriptorManager.deleteDescriptor(id);
            }
        } catch (IllegalRegistryOperationException e) {
            FacesMessage facesMessage = new FacesMessage(e.getLocalizedMessage());
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage("services", facesMessage);
            return null;
        } catch (EntryDeletionException e) {
            FacesMessage facesMessage = new FacesMessage(e.getLocalizedMessage());
            facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage("services", facesMessage);
            return null;
        }
        return "services.xhtml?faces-redirect=true";
    }


    /**
     * Manages the filtering options by redirecting to proper address.
     * 
     * @return navigation directive.
     */
    public String getFilteringResultAddress() {
        StringBuilder builder = new StringBuilder("services.xhtml");
        builder.append("?faces-redirect=true");
        if (exposed != null) {
            builder.append("&public=");
            builder.append(exposed);
        }
        if (local != null) {
            builder.append("&local=");
            builder.append(local);
        }
        return builder.toString();
    }
}
