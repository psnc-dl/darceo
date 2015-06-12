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

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * A class representing user of WRDZ.
 */
@Entity
@Table(name = "ZU_USERS", schema = "darceo")
public class User implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -5854514897710318406L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "userSequenceGenerator", sequenceName = "darceo.ZU_USR_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSequenceGenerator")
    @Column(name = "USR_ID", unique = true, nullable = false)
    private long id;

    /**
     * User's username in WRDZ.
     */
    @Column(name = "USR_USERNAME", length = 255, unique = true, nullable = false)
    private String username;

    /**
     * User's subdirectory in the organization's repository. If null or empty then organization's repository root is
     * used.
     */
    @Column(name = "USR_HOMEDIR", length = 255, nullable = false)
    private String homeDir;

    /**
     * User authentication data.
     */
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = { CascadeType.ALL })
    private UserAuthentication userData;

    /**
     * Organization that user belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USR_ORG_ID", nullable = false)
    private Organization organization;

    /**
     * Whether the user is an administrator.
     */
    @Column(name = "USR_ADMIN", nullable = false)
    private boolean admin;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getHomeDir() {
        return homeDir;
    }


    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }


    public Organization getOrganization() {
        return organization;
    }


    public void setOrganization(Organization organization) {
        this.organization = organization;
    }


    public UserAuthentication getUserData() {
        return userData;
    }


    public void setUserData(UserAuthentication userData) {
        this.userData = userData;
    }


    public boolean isAdmin() {
        return admin;
    }


    public void setAdmin(boolean admin) {
        this.admin = admin;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((homeDir == null) ? 0 : homeDir.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((organization == null) ? 0 : organization.hashCode());
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
        if (!(obj instanceof User)) {
            return false;
        }
        User other = (User) obj;
        if (homeDir == null) {
            if (other.homeDir != null) {
                return false;
            }
        } else if (!homeDir.equals(other.homeDir)) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (organization == null) {
            if (other.getOrganization() != null) {
                return false;
            }
        } else if ((other.getOrganization() != null && organization.getId() != other.getOrganization().getId())
                || other.getOrganization() == null) {
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
        return "User [id=" + id + ", username=" + username + ", homeDir=" + homeDir + ", organization=" + organization
                + "]";
    }

}
