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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;

/**
 * An entity representing file containing metadata extracted automatically by the system.
 */
@Entity
@DiscriminatorValue("FILES_EXTRACTED")
public class FileExtractedMetadata extends MetadataFile {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 7890352935343606940L;

    /**
     * The file from which metadata was extracted.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MF_DATA_FILE_ID", nullable = false)
    private DataFile extractedFrom;


    public DataFile getExtractedFrom() {
        return extractedFrom;
    }


    public void setExtractedFrom(DataFile extractedFrom) {
        this.extractedFrom = extractedFrom;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((filename == null) ? 0 : filename.hashCode());
        result = prime * result + ((repositoryFilepath == null) ? 0 : repositoryFilepath.hashCode());
        result = prime * result + ((objectFilepath == null) ? 0 : objectFilepath.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + (int) (size ^ (size >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((extractedFrom == null) ? 0 : extractedFrom.hashCode());
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
        if (!(obj instanceof FileExtractedMetadata)) {
            return false;
        }
        FileExtractedMetadata other = (FileExtractedMetadata) obj;
        if (id != other.id) {
            return false;
        }
        if (size != other.size) {
            return false;
        }
        if (type != other.type) {
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
        if (extractedFrom == null) {
            if (other.extractedFrom != null) {
                return false;
            }
        } else if (!extractedFrom.equals(other.extractedFrom)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("FileExtractedMetadata ");
        sb.append("[id = ").append(id);
        sb.append(", filename = ").append(filename);
        sb.append(", repositoryFilepath = ").append(repositoryFilepath);
        sb.append(", objectFilepath = ").append(objectFilepath);
        sb.append(", usedNamespaces = ").append(usedNamespaces);
        sb.append(", extractedFrom = ").append(extractedFrom);
        sb.append("]");
        return sb.toString();
    }

}
