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

/**
 * 
 * Technical descriptor describing the technical details of service interface and usage.
 */
@Entity
@Table(name = "RU_TECH_DESCRIPTORS", schema = "darceo", uniqueConstraints = { @UniqueConstraint(columnNames = {
        "TD_DMS_ID", "TD_TDT_ID" }) })
public class TechnicalDescriptor implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 4181940795079659723L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "technicalDescriptorSequenceGenerator", sequenceName = "darceo.RU_TD_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "technicalDescriptorSequenceGenerator")
    @Column(name = "TD_ID", unique = true, nullable = false)
    private long id;

    /**
     * Data manipulation service described by this descriptor.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, mappedBy = "technicalDescriptor")
    private List<DataManipulationService> describedServices = new ArrayList<DataManipulationService>();

    /**
     * Type of technical descriptor.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TD_DS_ID", nullable = false)
    private TechnicalDescriptorScheme type;

    /**
     * Semantic descriptor containing services described by this technical descriptor.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TD_SD_ID", nullable = false)
    private SemanticDescriptor semanticDescriptor;

    /**
     * Data manipulation service location URL.
     */
    @Column(name = "TD_LOCATION", nullable = false, unique = true, length = 255)
    private String locationUrl;


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


    public TechnicalDescriptorScheme getType() {
        return type;
    }


    public void setType(TechnicalDescriptorScheme type) {
        this.type = type;
    }


    public String getLocationUrl() {
        return locationUrl;
    }


    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }


    public SemanticDescriptor getSemanticDescriptor() {
        return semanticDescriptor;
    }


    public void setSemanticDescriptor(SemanticDescriptor semanticDescriptor) {
        this.semanticDescriptor = semanticDescriptor;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((locationUrl == null) ? 0 : locationUrl.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((semanticDescriptor == null) ? 0 : semanticDescriptor.hashCode());
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
        if (!(obj instanceof TechnicalDescriptor)) {
            return false;
        }
        TechnicalDescriptor other = (TechnicalDescriptor) obj;
        if (id != other.getId()) {
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
        if (semanticDescriptor == null) {
            if (other.getSemanticDescriptor() != null) {
                return false;
            }
        } else if (other.getSemanticDescriptor() != null) {
            return semanticDescriptor.getId() == other.getSemanticDescriptor().getId();
        } else {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TechnicalDescriptor [id=").append(id).append(", describedServices=");
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
        builder.append(", semanticDescriptor=");
        if (semanticDescriptor != null) {
            builder.append(semanticDescriptor.getId());
        } else {
            builder.append("null");
        }
        builder.append(", locationUrl=").append(locationUrl).append("]");
        return builder.toString();
    }

}
