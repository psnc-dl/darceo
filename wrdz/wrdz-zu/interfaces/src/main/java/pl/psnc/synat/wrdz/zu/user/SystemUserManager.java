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

import javax.ejb.Remote;

/**
 * Manages system users.
 */
@Remote
public interface SystemUserManager {

    /**
     * Creates a system user. If an organization with the given name does not exist, it is created as well.
     * 
     * @param name
     *            user name
     * @param certificate
     *            user certificate
     * @param displayName
     *            user display name
     * @param organizationName
     *            organization name
     * @return user id
     */
    long createSystemUser(String name, String certificate, String displayName, String organizationName);


    /**
     * Updates the certificate of the system user with the given name.
     * 
     * @param name
     *            user name
     * @param certificate
     *            user certificate
     */
    void updateSystemUser(String name, String certificate);


    /**
     * Deletes the system user with the given name.
     * 
     * @param name
     *            user name
     */
    void deleteSystemUser(String name);


    /**
     * Retrieves the user's certificate.
     * 
     * @param name
     *            user name
     * @return certificate, or <code>null</code>
     */
    String getCertificate(String name);
}
