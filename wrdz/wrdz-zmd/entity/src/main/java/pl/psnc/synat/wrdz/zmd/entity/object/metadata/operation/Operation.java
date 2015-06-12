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
package pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile;
import pl.psnc.synat.wrdz.zmd.entity.types.OperationType;

/**
 * An abstract entity representing an operation performed on digital object's metadata.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "OP_NAME", discriminatorType = DiscriminatorType.STRING)
@Table(name = "ZMD_METADATA_OPERATIONS", schema = "darceo")
public abstract class Operation implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -5473001435922167197L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "operationSequenceGenerator", sequenceName = "darceo.ZMD_OP_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operationSequenceGenerator")
    @Column(name = "OP_ID", unique = true, nullable = false)
    protected long id;

    /**
     * Operation type.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "OP_NAME", length = 15, nullable = false)
    protected OperationType operation;

    /**
     * Object, on which operation has been issued.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OP_OBJECT", nullable = false)
    protected DigitalObject object;

    /**
     * Date of operation.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "OP_DATE", nullable = false)
    protected Date date;

    /**
     * Metadata file contents.
     */
    @Basic(fetch = FetchType.LAZY, optional = true)
    @Column(name = "OP_CONTENTS", nullable = true, unique = false)
    protected String contents;

    /**
     * Metadata file type (main scheme).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "OP_MD_TYPE", length = 10, nullable = false)
    protected NamespaceType metadataType;

    /**
     * List of connected metadata files.
     */
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "metadataContent")
    protected List<MetadataFile> metadataFiles = new ArrayList<MetadataFile>();


    /**
     * Default constructor for class.
     */
    public Operation() {
        this.date = new Date();
        this.metadataType = NamespaceType.UNKNOWN;
    }


    /**
     * Creates new instance of an operation for the specified object.
     * 
     * @param object
     *            digital object on which operation has been performed.
     */
    public Operation(DigitalObject object) {
        this.object = object;
        this.date = new Date();
        this.metadataType = NamespaceType.UNKNOWN;
    }


    /**
     * Creates new instance of an operation for the specified object with specified operation date.
     * 
     * @param object
     *            digital object on which operation has been performed.
     * @param date
     *            date of operation.
     */
    public Operation(DigitalObject object, Date date) {
        this.object = object;
        this.date = date;
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public OperationType getOperation() {
        return operation;
    }


    public void setOperation(OperationType operation) {
        this.operation = operation;
    }


    public DigitalObject getObject() {
        return object;
    }


    public void setObject(DigitalObject object) {
        this.object = object;
    }


    public Date getDate() {
        return date;
    }


    public void setDate(Date date) {
        this.date = date;
    }


    public String getContents() {
        return contents;
    }


    public void setContents(String contents) {
        this.contents = contents;
    }


    public NamespaceType getMetadataType() {
        return metadataType;
    }


    public void setMetadataType(NamespaceType metadataType) {
        this.metadataType = metadataType;
    }


    /**
     * Gets list of metadata files.
     * 
     * @return list of metadata files or empty list if none was found.
     */
    public List<MetadataFile> getMetadataFiles() {
        return metadataFiles;
    }


    public void setMetadataFiles(List<MetadataFile> metadataFiles) {
        this.metadataFiles = metadataFiles;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((object == null) ? 0 : object.hashCode());
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
        result = prime * result + ((metadataType == null) ? 0 : metadataType.hashCode());
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
        if (!(obj instanceof Operation)) {
            return false;
        }
        Operation other = (Operation) obj;
        if (date == null) {
            if (other.getDate() != null) {
                return false;
            }
        } else if (!date.equals(other.getDate())) {
            return false;
        }
        if (id != other.getId()) {
            return false;
        }
        if (object == null) {
            if (other.getObject() != null) {
                return false;
            }
        } else if (!object.equals(other.getObject())) {
            return false;
        }
        if (operation != other.getOperation()) {
            return false;
        }
        if (metadataType != other.getMetadataType()) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "Operation [id=" + id + ", operation=" + operation + ", object=" + object + ", date=" + date + "]";
    }

}
