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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import pl.psnc.synat.wrdz.zmd.entity.object.hash.DataFileHash;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.validation.DataFileValidation;

/**
 * An entity representing file containing object's data.
 */
@Entity
@Table(name = "ZMD_DATA_FILES", schema = "darceo")
public class DataFile implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 6127606734276237939L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "dataFileSequenceGenerator", sequenceName = "darceo.ZMD_DF_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dataFileSequenceGenerator")
    @Column(name = "DF_ID", unique = true, nullable = false)
    private long id;

    /**
     * Full path to the file in the repository.
     */
    @Column(name = "DF_REPO_PATH", length = 255, nullable = false)
    private String repositoryFilepath;

    /**
     * Object-relative path of the file.
     */
    @Column(name = "DF_OBJ_PATH", length = 255, nullable = false)
    private String objectFilepath;

    /**
     * Filename of the file.
     */
    @Column(name = "DF_FILENAME", length = 255, nullable = false)
    private String filename;

    /**
     * File format.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "DF_FORMAT_ID", nullable = false)
    private DataFileFormat format;

    /**
     * Result of file format validation.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH, CascadeType.REMOVE })
    @JoinColumn(name = "DF_DFV_ID", nullable = true)
    private DataFileValidation validation;

    /**
     * File size in bytes.
     */
    @Column(name = "DF_SIZE", nullable = false)
    private long size;

    /**
     * Metadata extracted from this file.
     */
    @OneToMany(mappedBy = "extractedFrom", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE, CascadeType.MERGE,
            CascadeType.PERSIST })
    private List<FileExtractedMetadata> extractedMetadata = new ArrayList<FileExtractedMetadata>();

    /**
     * List of version this file is included in.
     */
    @OneToMany(mappedBy = "dataFile", fetch = FetchType.LAZY)
    private List<DataFileVersion> includedIn = new ArrayList<DataFileVersion>();

    /**
     * List of versions this file is a main file for.
     */
    @OneToMany(mappedBy = "mainFile", fetch = FetchType.LAZY)
    private List<ContentVersion> mainFileIn = new ArrayList<ContentVersion>();

    /**
     * List of hashes generated for this file.
     */
    @OneToMany(mappedBy = "dataFile", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    private List<DataFileHash> hashes = new ArrayList<DataFileHash>();

    /**
     * Path of the file in the cache - used in the procedures of creation and modification of the object.
     */
    @Transient
    private String cachePath;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getRepositoryFilepath() {
        return repositoryFilepath;
    }


    public void setRepositoryFilepath(String repositoryFilepath) {
        this.repositoryFilepath = repositoryFilepath;
    }


    public String getObjectFilepath() {
        return objectFilepath;
    }


    public void setObjectFilepath(String objectFilepath) {
        this.objectFilepath = objectFilepath;
    }


    public String getFilename() {
        return filename;
    }


    public void setFilename(String filename) {
        this.filename = filename;
    }


    public DataFileFormat getFormat() {
        return format;
    }


    public void setFormat(DataFileFormat format) {
        this.format = format;
    }


    public DataFileValidation getValidation() {
        return validation;
    }


    public void setValidation(DataFileValidation validation) {
        this.validation = validation;
    }


    public void setSize(long size) {
        this.size = size;
    }


    public long getSize() {
        return size;
    }


    public List<FileExtractedMetadata> getExtractedMetadata() {
        return extractedMetadata;
    }


    public void setExtractedMetadata(List<FileExtractedMetadata> extractedMetadata) {
        this.extractedMetadata = extractedMetadata;
    }


    public void setIncludedIn(List<DataFileVersion> includedIn) {
        this.includedIn = includedIn;
    }


    public List<DataFileVersion> getIncludedIn() {
        return includedIn;
    }


    public void setMainFileIn(List<ContentVersion> mainFileIn) {
        this.mainFileIn = mainFileIn;
    }


    public List<ContentVersion> getMainFileIn() {
        return mainFileIn;
    }


    public void setHashes(List<DataFileHash> hashes) {
        this.hashes = hashes;
    }


    public List<DataFileHash> getHashes() {
        return hashes;
    }


    public String getCachePath() {
        return cachePath;
    }


    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((filename == null) ? 0 : filename.hashCode());
        result = prime * result + ((repositoryFilepath == null) ? 0 : repositoryFilepath.hashCode());
        result = prime * result + ((objectFilepath == null) ? 0 : objectFilepath.hashCode());
        result = prime * result + ((format == null) ? 0 : format.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + (int) (size ^ (size >>> 32));
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
        if (!(obj instanceof DataFile)) {
            return false;
        }
        DataFile other = (DataFile) obj;
        if (id != other.id) {
            return false;
        }
        if (size != other.size) {
            return false;
        }
        if (filename == null) {
            if (other.filename != null) {
                return false;
            }
        } else if (!filename.equals(other.filename)) {
            return false;
        }
        if (repositoryFilepath == null) {
            if (other.repositoryFilepath != null) {
                return false;
            }
        } else if (!repositoryFilepath.equals(other.repositoryFilepath)) {
            return false;
        }
        if (objectFilepath == null) {
            if (other.objectFilepath != null) {
                return false;
            }
        } else if (!objectFilepath.equals(other.objectFilepath)) {
            return false;
        }
        if (format == null) {
            if (other.format != null) {
                return false;
            }
        } else if (!format.equals(other.format)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("DataFile ");
        sb.append("[id = ").append(id);
        sb.append(", filename = ").append(filename);
        sb.append(", repositoryFilepath = ").append(repositoryFilepath);
        sb.append(", objectFilepath = ").append(objectFilepath);
        sb.append(", format = ").append(format);
        sb.append("]");
        return sb.toString();
    }
}
