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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * A class representing organization using WRDZ.
 */
@Entity
@Table(name = "ZU_ORGANIZATIONS", schema = "darceo")
public class Organization {

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "organizationSequenceGenerator", sequenceName = "darceo.ZU_ORG_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organizationSequenceGenerator")
    @Column(name = "ORG_ID", unique = true, nullable = false)
    private long id;

    /**
     * Organization's unique name.
     */
    @Column(name = "ORG_NAME", length = 255, unique = true, nullable = false)
    private String name;

    /**
     * Organization's repository absolute path.
     */
    @Column(name = "ORG_PATH", length = 255, nullable = false)
    private String rootPath;

    /**
     * Users registered in this organization.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organization")
    private List<User> users = new ArrayList<User>();


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getRootPath() {
        return rootPath;
    }


    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }


    /**
     * Gets the list of organization's users.
     * 
     * @return list of organization users or an empty list if no users were found.
     */
    public List<User> getUsers() {
        return users;
    }


    public void setUsers(List<User> users) {
        this.users = users;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((rootPath == null) ? 0 : rootPath.hashCode());
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
        if (!(obj instanceof Organization)) {
            return false;
        }
        Organization other = (Organization) obj;
        if (id != other.id) {
            return false;
        }
        if (name == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!name.equals(other.getName())) {
            return false;
        }
        if (rootPath == null) {
            if (other.getRootPath() != null) {
                return false;
            }
        } else if (!rootPath.equals(other.getRootPath())) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "Organization [id=" + id + ", name=" + name + ", rootPath=" + rootPath + "]";
    }

}
