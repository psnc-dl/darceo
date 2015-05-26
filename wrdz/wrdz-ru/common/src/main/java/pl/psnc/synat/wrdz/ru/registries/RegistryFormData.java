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
package pl.psnc.synat.wrdz.ru.registries;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import pl.psnc.synat.wrdz.ru.entity.registries.RemoteRegistry;
import pl.psnc.synat.wrdz.ru.registries.validation.constraints.ValidModificationOperation;
import pl.psnc.synat.wrdz.ru.registries.validation.groups.Creation;
import pl.psnc.synat.wrdz.ru.registries.validation.groups.Modification;

/**
 * Represents registry data transferred from the user. Can be validated checking for consistency for creation (
 * {@link Creation} group) or modification ({@link Modification} group).
 */
@ValidModificationOperation(groups = { Modification.class })
public class RegistryFormData implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -7952801925697322437L;

    /**
     * Registry's name.
     */
    @NotNull(groups = { Creation.class })
    @Size(max = 100)
    private String name;

    /**
     * Registry's location.
     */
    @NotNull(groups = { Creation.class })
    @Size(max = 255)
    private String location;

    /**
     * Registry's certificate.
     */
    @NotNull(groups = { Creation.class })
    private byte[] certificate;

    /**
     * Registry's description.
     */
    @Size(max = 255)
    private String description;

    /**
     * Registry's ability to read local registry.
     */
    private Boolean readEnabled;

    /**
     * Local registry's ability to harvest this registry.
     */
    private Boolean harvested;

    /**
     * Date of the last harvest.
     */
    private Date lastHarvest;


    /**
     * Constructs empty form.
     */
    public RegistryFormData() {
        // empty default constructor
    }


    /**
     * Loads form data from the registry entity.
     * 
     * @param registry
     *            entity from which to copy data.
     */
    public RegistryFormData(RemoteRegistry registry) {
        name = registry.getName();
        location = registry.getLocationUrl();
        description = registry.getDescription();
        readEnabled = registry.isReadEnabled();
        harvested = registry.isHarvested();
        lastHarvest = registry.getLatestHarvestDate();
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getLocation() {
        return location;
    }


    public void setLocation(String location) {
        this.location = location;
    }


    public byte[] getCertificate() {
        return certificate;
    }


    public void setCertificate(byte[] certificate) {
        this.certificate = certificate;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public Boolean getReadEnabled() {
        return readEnabled;
    }


    public void setReadEnabled(Boolean readEnabled) {
        this.readEnabled = readEnabled;
    }


    public Boolean getHarvested() {
        return harvested;
    }


    public void setHarvested(Boolean harvested) {
        this.harvested = harvested;
    }


    public Date getLastHarvest() {
        return lastHarvest;
    }


    public void setLastHarvest(Date lastHarvest) {
        this.lastHarvest = lastHarvest;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(certificate);
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((harvested == null) ? 0 : harvested.hashCode());
        result = prime * result + ((lastHarvest == null) ? 0 : lastHarvest.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((readEnabled == null) ? 0 : readEnabled.hashCode());
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
        if (!(obj instanceof RegistryFormData)) {
            return false;
        }
        RegistryFormData other = (RegistryFormData) obj;
        if (!Arrays.equals(certificate, other.certificate)) {
            return false;
        }
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (harvested == null) {
            if (other.harvested != null) {
                return false;
            }
        } else if (!harvested.equals(other.harvested)) {
            return false;
        }
        if (lastHarvest == null) {
            if (other.lastHarvest != null) {
                return false;
            }
        } else if (!lastHarvest.equals(other.lastHarvest)) {
            return false;
        }
        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (readEnabled == null) {
            if (other.readEnabled != null) {
                return false;
            }
        } else if (!readEnabled.equals(other.readEnabled)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RegistryFormData [name=").append(name).append(", location=").append(location)
                .append(", certificate=").append(Arrays.toString(certificate)).append(", description=")
                .append(description).append(", readEnabled=").append(readEnabled).append(", harvested=")
                .append(harvested).append(", lastHarvest=").append(lastHarvest).append("]");
        return builder.toString();
    }

}
