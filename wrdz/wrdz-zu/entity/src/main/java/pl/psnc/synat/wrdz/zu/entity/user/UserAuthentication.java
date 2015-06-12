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
package pl.psnc.synat.wrdz.zu.entity.user;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Entity representing a user authentication data for JAAS.
 */
@Entity
@Table(name = "ZU_AUTH_USER", schema = "darceo")
public class UserAuthentication {

    /**
     * Default constructor.
     * 
     */
    public UserAuthentication() {
    }


    /**
     * Constructs authentication data for the user.
     * 
     * @param user
     *            user
     */
    public UserAuthentication(User user) {
        this.user = user;
        this.active = Boolean.TRUE;
    }


    /**
     * Entity's identifier (primary key).
     */
    @Id
    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "AUSR_ID", unique = true, nullable = false)
    private User user;

    /** Primary (single-user) group. */
    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "AUSR_PRIMARY_GROUP_ID", unique = true, nullable = true)
    private GroupAuthentication primaryGroup;

    /**
     * JAAS group the user belongs to.
     */
    @ManyToMany(targetEntity = GroupAuthentication.class, fetch = FetchType.LAZY, cascade = { CascadeType.MERGE,
            CascadeType.PERSIST })
    @JoinTable(name = "ZU_AUTH_GROUP_AUTH_USER", schema = "darceo", joinColumns = { @JoinColumn(name = "AGAU_AUSR_ID",
            referencedColumnName = "AUSR_ID") }, inverseJoinColumns = { @JoinColumn(name = "AGAU_AGRP_ID",
            referencedColumnName = "AGRP_ID") })
    private List<GroupAuthentication> groups = new ArrayList<GroupAuthentication>();

    /**
     * Whether the user is active.
     */
    @Column(name = "AUSR_ACTIVE", nullable = false)
    private Boolean active;

    /**
     * First name.
     */
    @Column(name = "AUSR_FISRT_NAME", length = 31, nullable = true)
    private String firstName;

    /**
     * Middle initial.
     */
    @Column(name = "AUSR_MIDDLE_INITIAL", length = 15, nullable = true)
    private String middleInitial;

    /**
     * Last name.
     */
    @Column(name = "AUSR_LAST_NAME", length = 31, nullable = true)
    private String lastName;

    /**
     * Display name. Concatenation of first name, middle initial and last name by default.
     */
    @Column(name = "AUSR_DISPLAY_NAME", length = 127, nullable = true)
    private String displayName;

    /**
     * Password's hash.
     */
    @Column(name = "AUSR_PASSWORD_HASH", length = 64, nullable = true)
    private String passwordHash;

    /**
     * Public key for the username.
     */
    @Column(name = "AUSR_PASSWORD_SALT", precision = 128, scale = 0, nullable = true)
    private BigInteger passwordSalt;

    /**
     * User's certificate.
     */
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "AUSR_CERTIFICATE", nullable = true)
    private String certificate;

    /** Email address. */
    @Column(name = "AUSR_EMAIL", length = 255, nullable = true)
    private String email;


    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }


    public GroupAuthentication getPrimaryGroup() {
        return primaryGroup;
    }


    public void setPrimaryGroup(GroupAuthentication primaryGroup) {
        this.primaryGroup = primaryGroup;
    }


    /**
     * Fetches list of groups user belongs to.
     * 
     * @return user's groups.
     */
    public List<GroupAuthentication> getGroups() {
        return groups;
    }


    public void setGroups(List<GroupAuthentication> groups) {
        this.groups = groups;
    }


    public Boolean getActive() {
        return active;
    }


    public void setActive(Boolean active) {
        this.active = active;
    }


    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getMiddleInitial() {
        return middleInitial;
    }


    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }


    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    /**
     * Getter that returns a default display name.
     * 
     * @return default display name
     */
    public String getDisplayName() {
        if (displayName != null && displayName.length() > 0) {
            return displayName;
        } else {
            StringBuffer sb = new StringBuffer(100);
            sb.append(firstName).append(' ').append(middleInitial).append(' ').append(lastName);
            return sb.toString().trim();
        }
    }


    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public String getPasswordHash() {
        return passwordHash;
    }


    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }


    public BigInteger getPasswordSalt() {
        return passwordSalt;
    }


    public void setPasswordSalt(BigInteger passwordSalt) {
        this.passwordSalt = passwordSalt;
    }


    public String getCertificate() {
        return certificate;
    }


    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }

}
