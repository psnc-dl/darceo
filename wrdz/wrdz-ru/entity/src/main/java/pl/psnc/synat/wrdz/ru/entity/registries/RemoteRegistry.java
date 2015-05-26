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
package pl.psnc.synat.wrdz.ru.entity.registries;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Class representing remote service registry.
 */
@Entity
@Table(name = "RU_REMOTE_REGISTRIES", schema = "darceo")
public class RemoteRegistry implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -2389020091359257382L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "remoteRegistrySequenceGenerator", sequenceName = "darceo.RU_REG_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "remoteRegistrySequenceGenerator")
    @Column(name = "REG_ID", unique = true, nullable = false)
    private long id;

    /**
     * Remote service registry name.
     */
    @Column(name = "REG_NAME", nullable = true, unique = true, length = 100)
    private String name;

    /**
     * Remote service registry location URL.
     */
    @Column(name = "REG_LOCATION", nullable = false, unique = true, length = 255)
    private String locationUrl;

    /**
     * Remote service registry description.
     */
    @Column(name = "REG_DESCRIPTION", nullable = true, length = 255)
    private String description;

    /**
     * Remote service registry's username.
     */
    @Column(name = "REG_USERNAME", nullable = false)
    private String username;

    /**
     * Defines whether or not this remote service registry is allowed to read the local one.
     */
    @Column(name = "REG_READ_ENABLED", nullable = false)
    private boolean readEnabled;

    /**
     * Defines whether or not this remote service registry is harvested by the local one.
     */
    @Column(name = "REG_HARVESTED", nullable = false)
    private boolean harvested;

    /**
     * Stores the timestamp of the last harvesting performed on this remote service registry.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REG_LATEST_HARVEST", nullable = true)
    private Date latestHarvestDate;


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


    public String getLocationUrl() {
        return locationUrl;
    }


    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public boolean isReadEnabled() {
        return readEnabled;
    }


    public void setReadEnabled(boolean readEnabled) {
        this.readEnabled = readEnabled;
    }


    public boolean isHarvested() {
        return harvested;
    }


    public void setHarvested(boolean harvested) {
        this.harvested = harvested;
    }


    public Date getLatestHarvestDate() {
        return latestHarvestDate;
    }


    public void setLatestHarvestDate(Date latestHarvestDate) {
        this.latestHarvestDate = latestHarvestDate;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + username.hashCode();
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + (harvested ? 1231 : 1237);
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((latestHarvestDate == null) ? 0 : latestHarvestDate.hashCode());
        result = prime * result + ((locationUrl == null) ? 0 : locationUrl.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (readEnabled ? 1231 : 1237);
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
        if (!(obj instanceof RemoteRegistry)) {
            return false;
        }
        RemoteRegistry other = (RemoteRegistry) obj;
        if (id != other.getId()) {
            return false;
        }
        if (readEnabled != other.isReadEnabled()) {
            return false;
        }
        if (harvested != other.isHarvested()) {
            return false;
        }
        if (latestHarvestDate == null) {
            if (other.getLatestHarvestDate() != null) {
                return false;
            }
        } else if (!latestHarvestDate.equals(other.getLatestHarvestDate())) {
            return false;
        }
        if (locationUrl == null) {
            if (other.getLocationUrl() != null) {
                return false;
            }
        } else if (!locationUrl.equals(other.getLocationUrl())) {
            return false;
        }
        if (name == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!name.equals(other.getName())) {
            return false;
        }
        if (username == null) {
            if (other.getUsername() != null) {
                return false;
            }
        } else if (!username.equals(other.getUsername())) {
            return false;
        }
        if (description == null) {
            if (other.getDescription() != null) {
                return false;
            }
        } else if (!description.equals(other.getDescription())) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RemoteRegistry [id=").append(id).append(", name=").append(name).append(", locationUrl=")
                .append(locationUrl).append(", description=").append(description).append(", username=")
                .append(username).append(", readEnabled=").append(readEnabled).append(", harvested=").append(harvested)
                .append(", latestHarvestDate=").append(latestHarvestDate).append("]");
        return builder.toString();
    }

}
