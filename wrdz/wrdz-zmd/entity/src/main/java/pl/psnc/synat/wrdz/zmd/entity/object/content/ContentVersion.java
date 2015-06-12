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
package pl.psnc.synat.wrdz.zmd.entity.object.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.eclipse.persistence.annotations.PrivateOwned;

import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectProvidedMetadata;

/**
 * An entity representing version of the object's content.
 */
@Entity
@Table(name = "ZMD_CONTENT_VERSIONS", schema = "darceo", uniqueConstraints = { @UniqueConstraint(columnNames = {
        "CV_VERSION", "CV_OBJECT_ID" }) })
public class ContentVersion implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -4510383777274705814L;


    /**
     * Default constructor.
     */
    public ContentVersion() {
    }


    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "contentVersionSequenceGenerator", sequenceName = "darceo.ZMD_CV_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "contentVersionSequenceGenerator")
    @Column(name = "CV_ID", unique = true, nullable = false)
    private long id;

    /**
     * Number of the version of object's content.
     */
    @Column(name = "CV_VERSION", nullable = false)
    private Integer version;

    /**
     * Date and time of version's creation.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CV_CREATED_ON", nullable = false)
    private Date createdOn;

    /**
     * Versioned object.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CV_OBJECT_ID", nullable = false)
    private DigitalObject object;

    /**
     * Version's main file (e.g. cover, title page etc.).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH, CascadeType.DETACH,
            CascadeType.MERGE, CascadeType.PERSIST })
    @JoinColumn(name = "CV_MAIN_FILE_ID", nullable = true)
    private DataFile mainFile;

    /**
     * Contents of the version.
     */
    @PrivateOwned
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contentVersion", cascade = { CascadeType.ALL })
    private List<DataFileVersion> files = new ArrayList<DataFileVersion>();

    /**
     * Version's extracted metadata. METS container aggregating metadata of all files plus provenance in the premis
     * metadata.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.ALL })
    @JoinColumn(name = "CV_EXTRACTED_METADATA_ID", nullable = true, unique = true)
    private ObjectExtractedMetadata extractedMetadata;

    /**
     * Version's provided metadata.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST })
    @JoinTable(name = "ZMD_CONTENT_VERSIONS_PROVIDED_METADATA_FILES", schema = "darceo", joinColumns = { @JoinColumn(
            name = "CV_ID", referencedColumnName = "CV_ID") }, inverseJoinColumns = { @JoinColumn(name = "MF_ID",
            referencedColumnName = "MF_ID") })
    private List<ObjectProvidedMetadata> providedMetadata = new ArrayList<ObjectProvidedMetadata>();


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public Integer getVersion() {
        return version;
    }


    public void setVersion(Integer version) {
        this.version = version;
    }


    public Date getCreatedOn() {
        return (Date) createdOn.clone();
    }


    public void setCreatedOn(Date createdOn) {
        this.createdOn = (Date) createdOn.clone();
    }


    public DigitalObject getObject() {
        return object;
    }


    public void setObject(DigitalObject object) {
        this.object = object;
    }


    public DataFile getMainFile() {
        return mainFile;
    }


    public void setMainFile(DataFile mainFile) {
        this.mainFile = mainFile;
    }


    public List<DataFileVersion> getFiles() {
        return files;
    }


    public void setFiles(List<DataFileVersion> files) {
        this.files = files;
    }


    public ObjectExtractedMetadata getExtractedMetadata() {
        return extractedMetadata;
    }


    public void setExtractedMetadata(ObjectExtractedMetadata extractedMetadata) {
        this.extractedMetadata = extractedMetadata;
    }


    public List<ObjectProvidedMetadata> getProvidedMetadata() {
        return providedMetadata;
    }


    public void setProvidedMetadata(List<ObjectProvidedMetadata> providedMetadata) {
        this.providedMetadata = providedMetadata;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((mainFile == null) ? 0 : mainFile.hashCode());
        result = prime * result + ((object == null) ? 0 : object.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
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
        if (!(obj instanceof ContentVersion)) {
            return false;
        }
        ContentVersion other = (ContentVersion) obj;
        if (createdOn == null) {
            if (other.getCreatedOn() != null) {
                return false;
            }
        } else if (!createdOn.equals(other.getCreatedOn())) {
            return false;
        }
        if (id != other.getId()) {
            return false;
        }
        if (mainFile == null) {
            if (other.getMainFile() != null) {
                return false;
            }
        } else if (!mainFile.equals(other.getMainFile())) {
            return false;
        }
        if (object == null) {
            if (other.getObject() != null) {
                return false;
            }
        } else if (!object.equals(other.getObject())) {
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
        builder.append("ContentVersion [id = ").append(id);
        builder.append(", version = ").append(version);
        builder.append(", createdOn = ").append(createdOn);
        builder.append(", object id = ").append(object.getId());
        builder.append(", extractedMetadata id = ")
                .append(extractedMetadata != null ? extractedMetadata.getId() : null);
        builder.append("]");
        return builder.toString();
    }

}
