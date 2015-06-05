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
package pl.psnc.synat.wrdz.realm.user;

import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.security.auth.x500.X500Principal;

import org.glassfish.security.common.PrincipalImpl;

import pl.psnc.synat.wrdz.realm.WrdzUserRealm;

/**
 * Class representing the credentials of WRDZ system user.
 */
public class WrdzUserCredentials {

    /**
     * User name/ login (for simple authentication).
     */
    private String username;

    /**
     * User password (for simple authentication).
     */
    private char[] password;

    /**
     * User principal (for certificate authentication).
     */
    private PrincipalImpl userPrincipal;

    /**
     * User certificates (for certificate authentication).
     */
    private X509Certificate[] certificates;

    /**
     * User certificate's principal (for certificate authentication).
     */
    private X500Principal certificatePrincipal;

    /**
     * Groups user belongs to.
     */
    private String[] groups;

    /**
     * Current realm performing authentication.
     */
    private WrdzUserRealm currentRealm;


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public char[] getPassword() {
        return password;
    }


    public void setPassword(char[] password) {
        this.password = password;
    }


    public PrincipalImpl getUserPrincipal() {
        return userPrincipal;
    }


    public void setUserPrincipal(PrincipalImpl userPrincipal) {
        this.userPrincipal = userPrincipal;
    }


    public X509Certificate[] getCertificates() {
        return certificates;
    }


    public void setCertificates(X509Certificate[] certificates) {
        this.certificates = certificates;
    }


    public X500Principal getCertificatePrincipal() {
        return certificatePrincipal;
    }


    public void setCertificatePrincipal(X500Principal certificatePrincipal) {
        this.certificatePrincipal = certificatePrincipal;
    }


    public String[] getGroups() {
        return groups;
    }


    public void setGroups(String[] groups) {
        this.groups = groups;
    }


    public WrdzUserRealm getCurrentRealm() {
        return currentRealm;
    }


    public void setCurrentRealm(WrdzUserRealm currentRealm) {
        this.currentRealm = currentRealm;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((certificatePrincipal == null) ? 0 : certificatePrincipal.hashCode());
        result = prime * result + Arrays.hashCode(certificates);
        result = prime * result + Arrays.hashCode(groups);
        result = prime * result + Arrays.hashCode(password);
        result = prime * result + ((userPrincipal == null) ? 0 : userPrincipal.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof WrdzUserCredentials)) {
            return false;
        }
        WrdzUserCredentials other = (WrdzUserCredentials) obj;
        if (certificatePrincipal == null) {
            if (other.certificatePrincipal != null) {
                return false;
            }
        } else if (!certificatePrincipal.equals(other.certificatePrincipal)) {
            return false;
        }
        if (!Arrays.equals(certificates, other.certificates)) {
            return false;
        }
        if (!Arrays.equals(groups, other.groups)) {
            return false;
        }
        if (!Arrays.equals(password, other.password)) {
            return false;
        }
        if (userPrincipal == null) {
            if (other.userPrincipal != null) {
                return false;
            }
        } else if (!userPrincipal.equals(other.userPrincipal)) {
            return false;
        }
        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WrdzUserCredentials [username=").append(username).append(", password=")
                .append(Arrays.toString(password)).append(", userPrincipal=").append(userPrincipal)
                .append(", certificates=").append(Arrays.toString(certificates)).append(", certificatePrincipal=")
                .append(certificatePrincipal).append(", groups=").append(Arrays.toString(groups))
                .append(", currentRealm=").append(currentRealm).append("]");
        return builder.toString();
    }


    /**
     * Checks whether or not credentials stored in this instance are valid for performing login (either
     * username/password pair or certificate chain is present.
     * 
     * @return indicator showing whether or not stored information is valid for login.
     */
    public boolean isValid() {
        if ((username != null && !username.isEmpty() && password != null && password.length > 0)
                || (certificates != null && certificates.length > 0)) {
            return true;
        }
        return false;
    }

}
