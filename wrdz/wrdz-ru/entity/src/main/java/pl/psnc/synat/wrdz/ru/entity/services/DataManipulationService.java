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
package pl.psnc.synat.wrdz.ru.entity.services;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.TechnicalDescriptor;
import pl.psnc.synat.wrdz.ru.entity.types.ServiceType;

/**
 * Entity representing Data Manipulation Service.
 */
@Entity
@Table(name = "RU_DMS_SERVICES", schema = "darceo")
public class DataManipulationService implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2935657277343597649L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "dataManipulationServiceSequenceGenerator", sequenceName = "darceo.RU_DMS_ID_SEQ",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dataManipulationServiceSequenceGenerator")
    @Column(name = "DMS_ID", unique = true, nullable = false)
    private long id;

    /**
     * Data Manipulation Service IRI.
     */
    @Column(name = "DMS_IRI", nullable = false, unique = true, length = 255)
    private String iri;

    /**
     * Data Manipulation Service name.
     */
    @Column(name = "DMS_NAME", nullable = false, length = 100)
    private String name;

    /**
     * Data Manipulation Service location URL.
     */
    @Column(name = "DMS_LOCATION", nullable = false, length = 255)
    private String locationUrl;

    /**
     * Data Manipulation Service description.
     */
    @Column(name = "DMS_DESCRIPTION", nullable = true, length = 255)
    private String description;

    /**
     * Data Manipulation Service's type specified by {@link ServiceType}.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "DMS_TYPE", length = 15, nullable = false)
    private ServiceType type;

    /**
     * Technical descriptor of this Data Manipulation Service.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DMS_TD_ID", nullable = false)
    private TechnicalDescriptor technicalDescriptor;

    /**
     * Semantic descriptor of this Data Manipulation Service.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DMS_SD_ID", nullable = false)
    private SemanticDescriptor semanticDescriptor;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getIri() {
        return iri;
    }


    public void setIri(String iri) {
        this.iri = iri;
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


    public ServiceType getType() {
        return type;
    }


    public void setType(ServiceType type) {
        this.type = type;
    }


    public TechnicalDescriptor getTechnicalDescriptor() {
        return technicalDescriptor;
    }


    public void setTechnicalDescriptor(TechnicalDescriptor technicalDescriptor) {
        this.technicalDescriptor = technicalDescriptor;
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
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((locationUrl == null) ? 0 : locationUrl.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((iri == null) ? 0 : iri.hashCode());
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
        if (!(obj instanceof DataManipulationService)) {
            return false;
        }
        DataManipulationService other = (DataManipulationService) obj;
        if (description == null) {
            if (other.getDescription() != null) {
                return false;
            }
        } else if (!description.equals(other.getDescription())) {
            return false;
        }
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
        if (name == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!name.equals(other.getName())) {
            return false;
        }
        if (iri == null) {
            if (other.getIri() != null) {
                return false;
            }
        } else if (!iri.equals(other.getIri())) {
            return false;
        }
        if (type != other.getType()) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DataManipulationService [id=").append(id).append(", name=").append(name).append(", iri=")
                .append(iri).append(", locationUrl=").append(locationUrl).append(", description=").append(description)
                .append(", type=").append(type);
        builder.append(", technicalDescriptor=");
        if (technicalDescriptor == null) {
            builder.append("null");
        } else {
            builder.append(technicalDescriptor.getId());
        }
        builder.append(", semanticDescriptor=");
        if (semanticDescriptor == null) {
            builder.append("null");
        } else {
            builder.append(semanticDescriptor.getId());
        }
        return builder.toString();
    }

}
