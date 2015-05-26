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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileProvidedMetadata;

/**
 * An entity representing content version's data files.
 */
@Entity
@Table(name = "ZMD_DATA_FILE_VERSIONS", schema = "darceo", uniqueConstraints = { @UniqueConstraint(columnNames = {
        "DFV_CONTENT_VERSION_ID", "DFV_DATA_FILE_ID" }) })
public class DataFileVersion implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -7092338549546614906L;


    /**
     * Default constructor.
     */
    public DataFileVersion() {
    }


    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "dataFileVersionSequenceGenerator", sequenceName = "darceo.ZMD_DFVER_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dataFileVersionSequenceGenerator")
    @Column(name = "DFV_ID", unique = true, nullable = false)
    private long id;

    /**
     * Content version this data file version is part of.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "DFV_CONTENT_VERSION_ID", nullable = false)
    private ContentVersion contentVersion;

    /**
     * Data file this data file version represents.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "DFV_DATA_FILE_ID", nullable = false)
    private DataFile dataFile;

    /**
     * Metadata provided for this file version.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "ZMD_DATA_FILE_VERSIONS_METADATA_FILES", schema = "darceo", joinColumns = { @JoinColumn(
            name = "DFV_ID", referencedColumnName = "DFV_ID") }, inverseJoinColumns = { @JoinColumn(name = "MF_ID",
            referencedColumnName = "MF_ID") })
    private List<FileProvidedMetadata> providedMetadata = new ArrayList<FileProvidedMetadata>();

    /**
     * Sequence value.
     */
    @Column(name = "DFV_SEQUENCE", nullable = true)
    private Integer sequence;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public ContentVersion getContentVersion() {
        return contentVersion;
    }


    public void setContentVersion(ContentVersion contentVersion) {
        this.contentVersion = contentVersion;
    }


    public DataFile getDataFile() {
        return dataFile;
    }


    public void setDataFile(DataFile dataFile) {
        this.dataFile = dataFile;
    }


    public List<FileProvidedMetadata> getProvidedMetadata() {
        return providedMetadata;
    }


    public void setProvidedMetadata(List<FileProvidedMetadata> providedMetadata) {
        this.providedMetadata = providedMetadata;
    }


    public Integer getSequence() {
        return sequence;
    }


    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contentVersion == null) ? 0 : contentVersion.hashCode());
        result = prime * result + ((dataFile == null) ? 0 : dataFile.hashCode());
        result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        DataFileVersion other = (DataFileVersion) obj;
        if (contentVersion == null) {
            if (other.contentVersion != null) {
                return false;
            }
        } else if (!contentVersion.equals(other.contentVersion)) {
            return false;
        }
        if (dataFile == null) {
            if (other.dataFile != null) {
                return false;
            }
        } else if (!dataFile.equals(other.dataFile)) {
            return false;
        }
        if (sequence == null) {
            if (other.sequence != null) {
                return false;
            }
        } else if (!sequence.equals(other.sequence)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DataFileVersion [contentVersion.id=").append(contentVersion.getId());
        builder.append(", dataFile.id=").append(dataFile.getId());
        builder.append(", sequence=").append(sequence);
        builder.append("]");
        return builder.toString();
    }
}
