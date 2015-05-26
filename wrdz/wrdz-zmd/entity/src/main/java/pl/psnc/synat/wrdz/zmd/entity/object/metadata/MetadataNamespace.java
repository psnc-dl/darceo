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
package pl.psnc.synat.wrdz.zmd.entity.object.metadata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;

/**
 * An entity class representing metadata namespace information.
 */
@Entity
@Table(name = "ZMD_METADATA_NAMESPACES", schema = "darceo")
public class MetadataNamespace implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2691908633126070088L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "metadataNamespaceSequenceGenerator", sequenceName = "darceo.ZMD_MN_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "metadataNamespaceSequenceGenerator")
    @Column(name = "MN_ID", unique = true, nullable = false)
    private long id;

    /**
     * Namespace type.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "MN_TYPE", length = 15, nullable = false)
    private NamespaceType type;

    /**
     * XML namespace location.
     */
    @Column(name = "MN_XMLNS", nullable = false)
    private String xmlns;

    /**
     * XML Schema location.
     */
    @Column(name = "MN_SCHEMA_LOCATION", nullable = true)
    private String schemaLocation;


    /**
     * Default constructor.
     */
    public MetadataNamespace() {
    }


    /**
     * Constructs namespace based upon namespece URI and schema location.
     * 
     * @param xmlns
     *            namespace URI
     * @param schemaLocation
     *            schema location
     * @param type
     *            type of metadata namespace
     */
    public MetadataNamespace(String xmlns, String schemaLocation, NamespaceType type) {
        this.xmlns = xmlns;
        this.schemaLocation = schemaLocation;
        this.type = type;
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public NamespaceType getType() {
        return type;
    }


    public void setType(NamespaceType type) {
        this.type = type;
    }


    public String getXmlns() {
        return xmlns;
    }


    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }


    public String getSchemaLocation() {
        return schemaLocation;
    }


    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((xmlns == null) ? 0 : xmlns.hashCode());
        result = prime * result + ((schemaLocation == null) ? 0 : schemaLocation.hashCode());
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
        if (!(obj instanceof MetadataNamespace)) {
            return false;
        }
        MetadataNamespace other = (MetadataNamespace) obj;
        if (id != other.id) {
            return false;
        }
        if (xmlns == null) {
            if (other.xmlns != null) {
                return false;
            }
        } else if (!xmlns.equals(other.xmlns)) {
            return false;
        }
        if (schemaLocation == null) {
            if (other.schemaLocation != null) {
                return false;
            }
        } else if (!schemaLocation.equals(other.schemaLocation)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("MetadataNamespace ");
        sb.append("[id = ").append(id);
        sb.append(", xmlns = ").append(xmlns);
        sb.append(", schemaLocation = ").append(schemaLocation);
        sb.append(", type = ").append(type);
        sb.append("]");
        return sb.toString();
    }

}
