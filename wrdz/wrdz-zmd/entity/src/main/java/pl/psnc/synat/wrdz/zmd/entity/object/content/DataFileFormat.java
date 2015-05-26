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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import pl.psnc.synat.wrdz.common.metadata.tech.FileType;

/**
 * An entity representing format of the file containing object's data.
 */
@Entity
@Table(name = "ZMD_DATA_FILE_FORMATS", schema = "darceo")
public class DataFileFormat implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -3370239618566965261L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "dataFileFormatSequenceGenerator", sequenceName = "darceo.ZMD_DFF_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dataFileFormatSequenceGenerator")
    @Column(name = "DFF_ID", unique = true, nullable = false)
    private long id;

    /**
     * Mime type of the format.
     */
    @Column(name = "DFF_MIMETYPE", length = 50, nullable = false)
    private String mimeType;

    /**
     * Format's identifier in the PRONOM database.
     */
    @Column(name = "DFF_PUID", length = 50, nullable = true)
    private String puid;

    /**
     * All names of the format.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinTable(name = "ZMD_DATA_FILE_FORMATS_DATA_FILE_FORMAT_NAMES", schema = "darceo", joinColumns = { @JoinColumn(
            name = "DFF_ID", referencedColumnName = "DFF_ID") }, inverseJoinColumns = { @JoinColumn(name = "DFFN_ID",
            referencedColumnName = "DFFN_ID") })
    protected List<DataFileFormatName> names = new ArrayList<DataFileFormatName>();

    /**
     * Version of the format.
     */
    @Column(name = "DFF_VERSION", length = 50, nullable = true)
    private String version;

    /**
     * Format type.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "DFF_TYPE", length = 10, nullable = false)
    private FileType type;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getMimeType() {
        return mimeType;
    }


    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }


    public String getPuid() {
        return puid;
    }


    public void setPuid(String puid) {
        this.puid = puid;
    }


    public List<DataFileFormatName> getNames() {
        return names;
    }


    public void setNames(List<DataFileFormatName> names) {
        this.names = names;
    }


    /**
     * Adds new data file format name.
     * 
     * @param name
     *            data file format name
     */
    public void addName(DataFileFormatName name) {
        this.names.add(name);
    }


    /**
     * Adds many new data file format name.
     * 
     * @param names
     *            data file format names
     */
    public void addNames(List<DataFileFormatName> names) {
        this.names.addAll(names);
    }


    public String getVersion() {
        return version;
    }


    public void setVersion(String version) {
        this.version = version;
    }


    public FileType getType() {
        return type;
    }


    public void setType(FileType type) {
        this.type = type;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
        result = prime * result + ((puid == null) ? 0 : puid.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        if (!(obj instanceof DataFileFormat)) {
            return false;
        }
        DataFileFormat other = (DataFileFormat) obj;
        if (id != other.getId()) {
            return false;
        }
        if (mimeType == null) {
            if (other.getMimeType() != null) {
                return false;
            }
        } else if (!mimeType.equals(other.getMimeType())) {
            return false;
        }
        if (puid == null) {
            if (other.getPuid() != null) {
                return false;
            }
        } else if (!puid.equals(other.getPuid())) {
            return false;
        }
        if (type != other.getType()) {
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
        return "DataFileFormat [id=" + id + ", mimeType=" + mimeType + ", puid=" + puid + ", version=" + version
                + ", type=" + type + "]";
    }

}
