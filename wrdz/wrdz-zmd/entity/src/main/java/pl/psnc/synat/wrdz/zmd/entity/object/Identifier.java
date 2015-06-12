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
package pl.psnc.synat.wrdz.zmd.entity.object;

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
import javax.persistence.UniqueConstraint;

import pl.psnc.synat.wrdz.zmd.entity.types.IdentifierType;

/**
 * An entity representing object access identifier, i.e. external identifier that is not an {@link DigitalObject} entity
 * identifier but rather an ID in one of the commonly used schemas for digital object identifier. This ID will be used
 * from the outside of ZMD module to access the object.
 */
@Entity
@Table(name = "ZMD_IDENTIFIERS", schema = "darceo", uniqueConstraints = { @UniqueConstraint(columnNames = {
        "OI_OBJECT_ID", "OI_TYPE", "OI_DEFAULT" }) })
public class Identifier implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 6767078568538362073L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "identifierSequenceGenerator", sequenceName = "darceo.ZMD_OI_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "identifierSequenceGenerator")
    @Column(name = "OI_ID", unique = true, nullable = false)
    private long id;

    /**
     * Digital object owning this identifier.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OI_OBJECT_ID", nullable = false)
    private DigitalObject object;

    /**
     * Identifier of the digital object.
     */
    @Column(name = "OI_IDENTIFIER", length = 255, nullable = false, unique = true)
    private String identifier;

    /**
     * Type of the identifier.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "OI_TYPE", length = 10, nullable = false)
    private IdentifierType type;

    /**
     * Indicates whether or not the following entry is active (for values <code>true</code> and <code>null</code>) or
     * inactive (if value is <code>false</code>).
     */
    @Column(name = "OI_ACTIVE", nullable = true)
    private Boolean isActive;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public DigitalObject getObject() {
        return object;
    }


    public void setObject(DigitalObject object) {
        this.object = object;
    }


    public String getIdentifier() {
        return identifier;
    }


    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }


    public IdentifierType getType() {
        return type;
    }


    public void setType(IdentifierType type) {
        this.type = type;
    }


    public Boolean getIsActive() {
        return isActive;
    }


    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((isActive == null) ? 0 : isActive.hashCode());
        result = prime * result + ((object == null) ? 0 : (int) (object.getId() ^ (object.getId() >>> 32)));
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
        if (!(obj instanceof Identifier)) {
            return false;
        }
        Identifier other = (Identifier) obj;
        if (id != other.getId()) {
            return false;
        }
        if (identifier == null) {
            if (other.getIdentifier() != null) {
                return false;
            }
        } else if (!identifier.equals(other.getIdentifier())) {
            return false;
        }
        if (isActive == null) {
            if (other.getIsActive() != null) {
                return false;
            }
        } else if (!isActive.equals(other.getIsActive())) {
            return false;
        }
        if (object == null) {
            if (other.getObject() != null) {
                return false;
            }
        } else if (other.getObject() == null) {
            return false;
        } else {
            if (object.getId() != other.getObject().getId()) {
                return false;
            }
        }
        if (type != other.getType()) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "Identifier [id=" + id + ", object=" + object.getId() + ", identifier=" + identifier + ", type=" + type
                + ", isActive=" + isActive + "]";
    }
}
