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
package pl.psnc.synat.wrdz.zmd.download;

/**
 * A class representing user's authentication information for accessing a repository.
 */
public class ConnectionInformation {

    /**
     * Host part of the URL.
     */
    protected final String host;

    /**
     * Port on which server listens.
     */
    protected final Integer port;

    /**
     * Name of the user.
     */
    protected final String username;

    /**
     * User's password.
     */
    protected String password;

    /**
     * Certificate information.
     */
    protected CertificateInformation certificateInfo;


    /**
     * Creates new instance of connection info with null fields.
     */
    public ConnectionInformation() {
        this.host = null;
        this.port = null;
        this.username = null;
    }


    /**
     * Creates new instance of connection info for simple user-password authentication.
     * 
     * @param host
     *            host name.
     * @param port
     *            port number on which host listens.
     * @param username
     *            name of the user.
     * @param password
     *            user's password.
     */
    public ConnectionInformation(String host, Integer port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }


    /**
     * Creates new instance of connection info for authentication with certificate.
     * 
     * @param host
     *            host name.
     * @param port
     *            port number on which host listens.
     * @param username
     *            name of the user.
     * @param certificateInfo
     *            certificate info containing keys and passphrase.
     */
    public ConnectionInformation(String host, Integer port, String username, CertificateInformation certificateInfo) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.certificateInfo = certificateInfo;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public CertificateInformation getCertificateInfo() {
        return certificateInfo;
    }


    public void setCertificateInfo(CertificateInformation certificateInfo) {
        this.certificateInfo = certificateInfo;
    }


    public String getHost() {
        return host;
    }


    public Integer getPort() {
        return port;
    }


    public String getUsername() {
        return username;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((certificateInfo == null) ? 0 : certificateInfo.hashCode());
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
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
        if (!(obj instanceof ConnectionInformation)) {
            return false;
        }
        ConnectionInformation other = (ConnectionInformation) obj;
        if (certificateInfo == null) {
            if (other.certificateInfo != null) {
                return false;
            }
        } else if (!certificateInfo.equals(other.certificateInfo)) {
            return false;
        }
        if (host == null) {
            if (other.host != null) {
                return false;
            }
        } else if (!host.equals(other.host)) {
            return false;
        }
        if (password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!password.equals(other.password)) {
            return false;
        }
        if (port == null) {
            if (other.port != null) {
                return false;
            }
        } else if (!port.equals(other.port)) {
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
        return "ConnectionInformation [host=" + host + ", port=" + port + ", username=" + username + ", password="
                + password + ", certificateInfo=" + certificateInfo + "]";
    }

}
