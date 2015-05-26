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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Entity representing a group authentication data for JAAS.
 */
@Entity
@Table(name = "ZU_AUTH_GROUP", schema = "darceo", uniqueConstraints = @UniqueConstraint(columnNames = {
        "AGRP_GROUPNAME", "AGRP_SINGLE_USER" }))
public class GroupAuthentication {

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "groupAuthenticationSequenceGenerator", sequenceName = "darceo.ZU_AGRP_ID_SEQ",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "groupAuthenticationSequenceGenerator")
    @Column(name = "AGRP_ID", unique = true, nullable = false)
    private long id;

    /**
     * Name of a group.
     */
    @Column(name = "AGRP_GROUPNAME", length = 64, nullable = false)
    private String groupname;

    /** Whether the group is private (single user). */
    @Column(name = "AGRP_SINGLE_USER", nullable = false)
    private boolean singleUser;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getGroupname() {
        return groupname;
    }


    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }


    public boolean isSingleUser() {
        return singleUser;
    }


    public void setSingleUser(boolean singleUser) {
        this.singleUser = singleUser;
    }

}
