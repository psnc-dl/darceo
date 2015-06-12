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
package pl.psnc.synat.wrdz.ms.messages;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.ms.entity.messages.InternalMessage;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Bean that handles internal messages.
 */
@ManagedBean
@ViewScoped
public class InternalMessageBean implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 8065878623299963022L;

    /** Messages. */
    private List<InternalMessage> messages;

    /** Message browser. */
    @EJB
    private transient InternalMessageManager messageManager;

    /** User browser. */
    @EJB(name = "UserBrowser")
    private UserBrowser userBrowser;

    /** User context. */
    @EJB
    private UserContext userContext;


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
                FacesContext ctx = FacesContext.getCurrentInstance();
                ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "forbidden.xhtml");
            }
        }
    }


    /**
     * Refreshes the currently cached data internal messages.
     */
    public void refresh() {
        messages = messageManager.getMessages();
    }


    /**
     * Returns the list of internal messages.
     * 
     * @return a list of internal messages
     */
    public List<InternalMessage> getMessages() {
        if (messages == null) {
            refresh();
        }
        return messages;
    }


    /**
     * Deletes the given message.
     * 
     * @param message
     *            message to delete
     * 
     */
    public void deleteMessage(InternalMessage message) {
        messageManager.deleteMessage(message.getId());
        messages.remove(message);
    }
}
