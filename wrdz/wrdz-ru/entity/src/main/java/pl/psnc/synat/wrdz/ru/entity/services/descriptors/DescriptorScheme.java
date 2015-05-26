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
import java.net.URL;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import pl.psnc.synat.wrdz.ru.entity.types.DescriptorType;

/**
 * 
 * Descriptor scheme identifying kind of descriptor, e.g. WSDL, WADL, OWL-S etc.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DS_TYPE", discriminatorType = DiscriminatorType.STRING)
@Table(name = "RU_DESCRIPTOR_SCHEMES", schema = "darceo", uniqueConstraints = { @UniqueConstraint(columnNames = {
        "DS_NAME", "DS_VERSION" }) })
public abstract class DescriptorScheme implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1624588440666704607L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "descriptorSchemeSequenceGenerator", sequenceName = "darceo.RU_DS_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "descriptorSchemeSequenceGenerator")
    @Column(name = "DS_ID", unique = true, nullable = false)
    protected long id;

    /**
     * Descriptor type name.
     */
    @Column(name = "DS_NAME", nullable = false, length = 100)
    protected String name;

    /**
     * Descriptor type main namespace.
     */
    @Column(name = "DS_NAMESPACE", nullable = false, unique = true, length = 255)
    protected String namespace;

    /**
     * Descriptor type version.
     */
    @Column(name = "DS_VERSION", nullable = true, length = 50)
    protected String version;

    /**
     * Descriptor type version.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "DS_TYPE", length = 10, nullable = false)
    protected DescriptorType type;


    /**
     * Constructs new, empty descriptor scheme.
     */
    public DescriptorScheme() {
        // empty default constructor.
    }


    /**
     * Constructs new descriptor scheme using specified name and namespace location.
     * 
     * @param name
     *            name of the scheme.
     * @param namespace
     *            namespace location.
     */
    public DescriptorScheme(String name, URL namespace) {
        this.name = name;
        this.namespace = namespace.toString();
    }


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


    public String getNamespace() {
        return namespace;
    }


    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }


    public String getVersion() {
        return version;
    }


    public void setVersion(String version) {
        this.version = version;
    }


    public DescriptorType getType() {
        return type;
    }


    public void setType(DescriptorType type) {
        this.type = type;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
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
        if (!(obj instanceof DescriptorScheme)) {
            return false;
        }
        DescriptorScheme other = (DescriptorScheme) obj;
        if (id != other.getId()) {
            return false;
        }
        if (type != other.getType()) {
            return false;
        }
        if (name == null) {
            if (other.getName() != null) {
                return false;
            }
        } else if (!name.equals(other.getName())) {
            return false;
        }
        if (namespace == null) {
            if (other.getNamespace() != null) {
                return false;
            }
        } else if (!namespace.equals(other.getNamespace())) {
            return false;
        }
        if (version == null) {
            if (other.getVersion() != null) {
                return false;
            }
        } else if (!version.equals(other.getVersion())) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DescriptorScheme [id=").append(id).append(", name=").append(name).append(", namespace=")
                .append(namespace).append(", version=").append(version).append(", type=").append(type).append("]");
        return builder.toString();
    }

}
