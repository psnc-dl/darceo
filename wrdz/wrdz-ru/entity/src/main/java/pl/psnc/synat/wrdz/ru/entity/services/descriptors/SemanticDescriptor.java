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
package pl.psnc.synat.wrdz.ru.entity.services.descriptors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import pl.psnc.synat.wrdz.ru.entity.services.DataManipulationService;
import pl.psnc.synat.wrdz.ru.entity.services.RegistryOperation;

/**
 * 
 * Semantic descriptor describing the service using semantic technologies.
 */
@Entity
@Table(name = "RU_SEM_DESCRIPTORS", schema = "darceo", uniqueConstraints = { @UniqueConstraint(columnNames = {
        "SD_DMS_ID", "SD_TDT_ID" }) })
public class SemanticDescriptor implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 4181940795079659723L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "semanticDescriptorSequenceGenerator", sequenceName = "darceo.RU_SD_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "semanticDescriptorSequenceGenerator")
    @Column(name = "SD_ID", unique = true, nullable = false)
    private long id;

    /**
     * Data manipulation service described by this descriptor.
     */
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "semanticDescriptor")
    //,            cascade = { CascadeType.REFRESH })
    private List<DataManipulationService> describedServices = new ArrayList<DataManipulationService>();

    /**
     * Technical descriptors of services described by this descriptor.
     */
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "semanticDescriptor",
            cascade = { CascadeType.ALL })
    private List<TechnicalDescriptor> technicalDescriptors = new ArrayList<TechnicalDescriptor>();

    /**
     * Type of semantic descriptor.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    //, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "SD_DS_ID", nullable = false)
    private SemanticDescriptorScheme type;

    /**
     * Data manipulation service location URL.
     */
    @Column(name = "SD_LOCATION", nullable = false, unique = true, length = 255)
    private String locationUrl;

    /**
     * Context of a semantic descriptor in a semantic repository.
     */
    @Column(name = "SD_CONTEXT", nullable = false, unique = true, length = 255)
    private String context;

    /**
     * Defines whether or not services are exposed to other registries (is public).
     */
    @Column(name = "SD_IS_PUBLIC", nullable = false)
    private boolean exposed;

    /**
     * Defines address of registry being the origin of services. If <code>null</code> then these are services defined by
     * the local registry.
     */
    @Column(name = "SD_ORIGIN", nullable = true, length = 1024)
    private String origin;

    /**
     * Defines whether or not descriptor is deleted.
     */
    @Column(name = "SD_IS_DELETED", nullable = false)
    private boolean deleted;

    /**
     * Operations on this semantic descriptor.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "target", orphanRemoval = true, cascade = { CascadeType.ALL })
    private List<RegistryOperation> operations = new ArrayList<RegistryOperation>();


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    /**
     * Fetches the list of data manipulation services described by this descriptor.
     * 
     * @return list of data manipulation services described by this descriptor, empty if no services are described.
     */
    public List<DataManipulationService> getDescribedServices() {
        return describedServices;
    }


    public void setDescribedServices(List<DataManipulationService> describedServices) {
        this.describedServices = describedServices;
    }


    public SemanticDescriptorScheme getType() {
        return type;
    }


    public void setType(SemanticDescriptorScheme type) {
        this.type = type;
    }


    public String getLocationUrl() {
        return locationUrl;
    }


    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }


    public String getContext() {
        return context;
    }


    public void setContext(String context) {
        this.context = context;
    }


    /**
     * Fetches the list of technical descriptors linked in this descriptor.
     * 
     * @return list of technical descriptors linked in this descriptor, empty if no technical descriptors are linked.
     */
    public List<TechnicalDescriptor> getTechnicalDescriptors() {
        return technicalDescriptors;
    }


    public void setTechnicalDescriptors(List<TechnicalDescriptor> technicalDescriptors) {
        this.technicalDescriptors = technicalDescriptors;
    }


    public boolean isExposed() {
        return exposed;
    }


    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }


    public String getOrigin() {
        return origin;
    }


    public void setOrigin(String origin) {
        this.origin = origin;
    }


    public boolean isDeleted() {
        return deleted;
    }


    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }


    public boolean isLocal() {
        return origin == null;
    }


    public List<RegistryOperation> getOperations() {
        return operations;
    }


    public void setOperations(List<RegistryOperation> operations) {
        this.operations = operations;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + (exposed ? 1231 : 1237);
        result = prime * result + (deleted ? 1231 : 1237);
        result = prime * result + ((locationUrl == null) ? 0 : locationUrl.hashCode());
        result = prime * result + ((origin == null) ? 0 : origin.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        if (!(obj instanceof SemanticDescriptor)) {
            return false;
        }
        SemanticDescriptor other = (SemanticDescriptor) obj;
        if (id != other.getId()) {
            return false;
        }
        if (exposed != other.isExposed()) {
            return false;
        }
        if (deleted != other.isDeleted()) {
            return false;
        }
        if (origin == null) {
            if (other.getOrigin() != null) {
                return false;
            }
        } else if (!origin.equals(other.getOrigin())) {
            return false;
        }
        if (locationUrl == null) {
            if (other.getLocationUrl() != null) {
                return false;
            }
        } else if (!locationUrl.equals(other.getLocationUrl())) {
            return false;
        }
        if (type == null) {
            if (other.getType() != null) {
                return false;
            }
        } else if (other.getType() != null) {
            return type.getId() == other.getType().getId();
        } else {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SemanticDescriptor [id=").append(id).append(", describedServices=");
        if (describedServices == null) {
            builder.append("null");
        } else {
            builder.append("[");
            for (int i = 0; i < describedServices.size(); i++) {
                builder.append(describedServices.get(i).getId());
                if (i < describedServices.size() - 1) {
                    builder.append(", ");
                }
            }
            builder.append("]");
        }
        builder.append(", type=");
        if (type != null) {
            builder.append(type.getId());
        } else {
            builder.append("null");
        }
        if (technicalDescriptors == null) {
            builder.append("null");
        } else {
            builder.append("[");
            for (int i = 0; i < technicalDescriptors.size(); i++) {
                builder.append(technicalDescriptors.get(i).getId());
                if (i < technicalDescriptors.size() - 1) {
                    builder.append(", ");
                }
            }
            builder.append("]");
        }
        builder.append(", operations=");
        if (operations == null) {
            builder.append("null");
        } else {
            builder.append("[");
            for (int i = 0; i < operations.size(); i++) {
                builder.append(operations.get(i).getId());
                if (i < operations.size() - 1) {
                    builder.append(", ");
                }
            }
            builder.append("]");
        }
        builder.append(", exposed=").append(exposed).append(", deleted=").append(deleted).append(", origin=")
                .append(origin).append(", locationUrl=").append(locationUrl).append("]");
        return builder.toString();
    }

}
