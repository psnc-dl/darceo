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

import java.util.List;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.ms.entity.messages.NotifyEmail;

/**
 * Manages the NotifyEmail entities.
 */
@Local
public interface NotifyEmailManager {

    /**
     * Returns a list of notify emails.
     * 
     * @return a list of notify emails
     */
    List<NotifyEmail> getEmails();


    /**
     * Adds a new email address to the notify emails.
     * 
     * @param emailAddress
     *            the address to save
     * @return persisted notify email
     */
    NotifyEmail saveEmail(String emailAddress);


    /**
     * Deletes the notify email with the given identifier.
     * 
     * @param emailId
     *            email identifier
     */
    void deleteEmail(long emailId);
}
