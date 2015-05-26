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
package pl.psnc.synat.wrdz.zmd.entity.authentication;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Entity representing simple user authentication data for server connections.
 */
@Entity
@Table(name = "ZMD_REMOTE_REPOSITORY_AUTH", schema = "darceo", uniqueConstraints = { @UniqueConstraint(
        columnNames = { "RRA_RR_ID", "RRA_USERNAME" }) })
public class RemoteRepositoryAuthentication implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2765376025472675661L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "remoteRepositoryAuthenticationSequenceGenerator", sequenceName = "darceo.ZMD_RRA_ID_SEQ",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "remoteRepositoryAuthenticationSequenceGenerator")
    @Column(name = "RRA_ID", unique = true, nullable = false)
    private long id;

    /**
     * Remote repository for which the authentication is provided.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RRA_RR_ID", nullable = false)
    private RemoteRepository remoteRepository;

    /**
     * Username of the user of repository.
     */
    @Column(name = "RRA_USERNAME", length = 50, nullable = true)
    private String username;

    /**
     * Password for the provided username.
     */
    @Column(name = "RRA_PASSWORD", length = 50, nullable = true)
    private String password;

    /**
     * User's private key.
     */
    @Column(name = "RRA_PRIV_KEY", length = 200, nullable = true)
    private byte[] privateKey;

    /**
     * Public key of the host of repository.
     */
    @Column(name = "RRA_PUB_KEY", length = 200, nullable = true)
    private byte[] publicKey;

    /**
     * Passphrase for accessing user's private key.
     */
    @Column(name = "RRA_PASSPHRASE", length = 50, nullable = true)
    private String passphrase;

    /**
     * Determines weather or not this authentication is used as default for the repository.
     */
    @Column(name = "RRA_DEFAULT", nullable = false)
    private boolean usedByDefault;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public RemoteRepository getRemoteRepository() {
        return remoteRepository;
    }


    public void setRemoteRepository(RemoteRepository remoteRepository) {
        this.remoteRepository = remoteRepository;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public byte[] getPrivateKey() {
        return privateKey;
    }


    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }


    public byte[] getPublicKey() {
        return publicKey;
    }


    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }


    public String getPassphrase() {
        return passphrase;
    }


    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }


    public void setUsedByDefault(boolean usedByDefault) {
        this.usedByDefault = usedByDefault;
    }


    public boolean isUsedByDefault() {
        return usedByDefault;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((passphrase == null) ? 0 : passphrase.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + Arrays.hashCode(privateKey);
        result = prime * result + Arrays.hashCode(publicKey);
        result = prime * result + ((remoteRepository == null) ? 0 : remoteRepository.hashCode());
        result = prime * result + (usedByDefault ? 1231 : 1237);
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
        if (!(obj instanceof RemoteRepositoryAuthentication)) {
            return false;
        }
        RemoteRepositoryAuthentication other = (RemoteRepositoryAuthentication) obj;
        if (id != other.getId()) {
            return false;
        }
        if (usedByDefault != other.usedByDefault) {
            return false;
        }
        if (passphrase == null) {
            if (other.getPassphrase() != null) {
                return false;
            }
        } else if (!passphrase.equals(other.getPassphrase())) {
            return false;
        }
        if (password == null) {
            if (other.getPassword() != null) {
                return false;
            }
        } else if (!password.equals(other.getPassword())) {
            return false;
        }
        if (!Arrays.equals(privateKey, other.getPrivateKey())) {
            return false;
        }
        if (!Arrays.equals(publicKey, other.getPublicKey())) {
            return false;
        }
        if (remoteRepository == null) {
            if (other.getRemoteRepository() != null) {
                return false;
            }
        } else if (!remoteRepository.equals(other.getRemoteRepository())) {
            return false;
        }
        if (username == null) {
            if (other.getUsername() != null) {
                return false;
            }
        } else if (!username.equals(other.getUsername())) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "RemoteRepositoryAuthentication [id=" + id + ", remoteRepository=" + remoteRepository + ", username="
                + username + ", password=" + password + ", privateKey=" + Arrays.toString(privateKey) + ", publicKey="
                + Arrays.toString(publicKey) + ", passphrase=" + passphrase + ", usedByDefault=" + usedByDefault + "]";
    }

}
