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

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import pl.psnc.synat.wrdz.ms.entity.messages.NotifyEmail;

/**
 * Bean that handles notification email addresses.
 */
@ManagedBean
@ViewScoped
public class NotifyEmailBean implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 8065878623299963022L;

    /** Emails. */
    private List<NotifyEmail> emails;

    /** Email browser. */
    @EJB
    private transient NotifyEmailManager emailManager;

    /** Address to be saved. */
    private String emailAddress;


    /**
     * Refreshes the currently cached notify emails.
     */
    public void refresh() {
        emails = emailManager.getEmails();
    }


    /**
     * Returns the list of notify emails.
     * 
     * @return a list of notify emails
     */
    public List<NotifyEmail> getEmails() {
        if (emails == null) {
            refresh();
        }
        return emails;
    }


    /**
     * Deletes the given email.
     * 
     * @param email
     *            email to delete
     * 
     */
    public void deleteEmail(NotifyEmail email) {
        emailManager.deleteEmail(email.getId());
        emails.remove(email);
    }


    public String getEmailAddress() {
        return emailAddress;
    }


    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }


    /**
     * Adds the currently set email address to notify emails.
     * 
     * @return null navigation rule
     */
    public String addEmail() {
        if (emailAddress != null) {
            NotifyEmail email = emailManager.saveEmail(emailAddress);
            emails.add(email);
            emailAddress = null;
        }
        return null;
    }
}
