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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * A class representing remote repository for which we store the access information.
 */
@Entity
@Table(name = "ZMD_REMOTE_REPOSITORIES", schema = "darceo", uniqueConstraints = { @UniqueConstraint(columnNames = {
        "RR_PORT", "RR_HOST" }) })
public class RemoteRepository implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -8454610509236638634L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "remoteRepositorySequenceGenerator", sequenceName = "darceo.ZMD_RR_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "remoteRepositorySequenceGenerator")
    @Column(name = "RR_ID", unique = true, nullable = false)
    private long id;

    /**
     * Name of the protocol (acronym) used to communicate with repository.
     */
    @Column(name = "RR_PROTOCOL", length = 5, nullable = false)
    private String protocol;

    /**
     * Name of the host of the repository.
     */
    @Column(name = "RR_HOST", length = 50, nullable = false)
    private String host;

    /**
     * Port on which repository listens.
     */
    @Column(name = "RR_PORT", nullable = true)
    private Integer port;

    /**
     * Informative description of the repository.
     */
    @Column(name = "RR_DESCRIPTION", length = 200, nullable = true)
    private String description;


    public long getId() {
        return id;
    }


    public String getProtocol() {
        return protocol;
    }


    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }


    public String getHost() {
        return host;
    }


    public void setHost(String host) {
        this.host = host;
    }


    public Integer getPort() {
        return port;
    }


    public void setPort(Integer port) {
        this.port = port;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
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
        if (!(obj instanceof RemoteRepository)) {
            return false;
        }
        RemoteRepository other = (RemoteRepository) obj;
        if (description == null) {
            if (other.getDescription() != null) {
                return false;
            }
        } else if (!description.equals(other.getDescription())) {
            return false;
        }
        if (host == null) {
            if (other.getHost() != null) {
                return false;
            }
        } else if (!host.equals(other.getHost())) {
            return false;
        }
        if (id != other.getId()) {
            return false;
        }
        if (port == null) {
            if (other.getPort() != null) {
                return false;
            }
        } else if (!port.equals(other.getPort())) {
            return false;
        }
        if (protocol == null) {
            if (other.getProtocol() != null) {
                return false;
            }
        } else if (!protocol.equals(other.getProtocol())) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "RemoteRepository [id=" + id + ", protocol=" + protocol + ", host=" + host + ", port=" + port
                + ", description=" + description + "]";
    }

}
