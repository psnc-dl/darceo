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
package pl.psnc.synat.wrdz.ms.mail;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.ms.entity.messages.InternalMessage;

/**
 * Sends emails.
 */
@Local
public interface MsMailer {

    /**
     * Sends email notifications about the given message.
     * 
     * @param message
     *            message to send notifications about
     */
    void sendNotification(InternalMessage message);


    /**
     * Sends an email notifying about an expiring certificate.
     * 
     * @param emailAddress
     *            user's email address
     */
    void sendCertificateExpirationWarning(String emailAddress);
}
