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
import java.util.Date;

import javax.persistence.CascadeType;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.entity.types.OperationType;

/**
 * Entity representing operation performed on service present in registry.
 */
@Entity
@Table(name = "RU_SERVICE_OPERATIONS", schema = "darceo")
public class RegistryOperation implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 9015642385081037228L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "serviceOperationSequenceGenerator", sequenceName = "darceo.RU_SO_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "serviceOperationSequenceGenerator")
    @Column(name = "SO_ID", unique = true, nullable = false)
    private long id;

    /**
     * Type of operation.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "SO_TYPE", nullable = false)
    private OperationType type;

    /**
     * Date of operation.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SO_DATE", nullable = false)
    private Date date;

    /**
     * Service targeted by the operation.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "SO_SD_ID", nullable = false)
    private SemanticDescriptor target;

    /**
     * Defines whether or not operations are exposed to other registries (is public).
     */
    @Column(name = "SO_IS_PUBLIC", nullable = false)
    private boolean exposed;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public OperationType getType() {
        return type;
    }


    public void setType(OperationType type) {
        this.type = type;
    }


    public Date getDate() {
        return date;
    }


    public void setDate(Date date) {
        this.date = date;
    }


    public SemanticDescriptor getTarget() {
        return target;
    }


    public void setTarget(SemanticDescriptor target) {
        this.target = target;
    }


    public boolean isExposed() {
        return exposed;
    }


    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + (exposed ? 1231 : 1237);
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
        if (!(obj instanceof RegistryOperation)) {
            return false;
        }
        RegistryOperation other = (RegistryOperation) obj;
        if (id != other.getId()) {
            return false;
        }
        if (exposed != other.isExposed()) {
            return false;
        }
        if (type != other.getType()) {
            return false;
        }
        if (date == null) {
            if (other.getDate() != null) {
                return false;
            }
        } else if (!date.equals(other.getDate())) {
            return false;
        }
        if (target == null) {
            if (other.getTarget() != null) {
                return false;
            }
        } else if (!target.equals(other.getTarget())) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RegistryOperation [id=").append(id).append(", type=").append(type).append(", date=")
                .append(date).append(", exposed=").append(exposed).append(", target=");
        if (target != null) {
            builder.append(target.getId());
        } else {
            builder.append("null");
        }
        builder.append("]");
        return builder.toString();
    }

}
