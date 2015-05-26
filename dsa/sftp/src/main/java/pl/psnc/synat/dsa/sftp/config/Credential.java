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
package pl.psnc.synat.dsa.sftp.config;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity representing a credential to SFTP server.
 * 
 */
@Entity
@Table(name = "DSA_CREDENTIALS", schema = "darceo")
@NamedQuery(name = "Credential.findByOrganization",
        query = "SELECT e FROM Credential e WHERE e.organization = :organization")
public class Credential implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -9069428739304506078L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "credentialsSequenceGenerator", sequenceName = "darceo.DSA_C_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credentialsSequenceGenerator")
    @Column(name = "C_ID", unique = true, nullable = false)
    private long id;

    /**
     * Organization to which this credential belongs.
     */
    @Column(name = "C_ORGANIZATION", length = 127, nullable = false, unique = true)
    private String organization;

    /**
     * Username of credential.
     */
    @Column(name = "C_USERNAME", length = 63, nullable = false)
    private String username;

    /**
     * Private key for the username.
     */
    @Column(name = "C_PRIVATE_KEY", nullable = false)
    private byte[] privateKey;

    /**
     * Public key for the username.
     */
    @Column(name = "C_PUBLIC_KEY", nullable = false)
    private byte[] publicKey;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getOrganization() {
        return organization;
    }


    public void setOrganization(String organization) {
        this.organization = organization;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
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

}
