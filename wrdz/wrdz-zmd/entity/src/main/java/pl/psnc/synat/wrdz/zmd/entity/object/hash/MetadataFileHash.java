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
package pl.psnc.synat.wrdz.zmd.entity.object.hash;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile;

/**
 * Represents metadata file hash.
 */
@Entity
@DiscriminatorValue("METADATAFILE")
public class MetadataFileHash extends FileHash {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 7148486575062094967L;

    /**
     * Metadata file for which hash is provided.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "FH_METADATAFILE_ID", nullable = true)
    private MetadataFile metadataFile;


    public void setMetadataFile(MetadataFile metadataFile) {
        this.metadataFile = metadataFile;
    }


    public MetadataFile getMetadataFile() {
        return metadataFile;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hashType == null) ? 0 : hashType.hashCode());
        result = prime * result + ((hashValue == null) ? 0 : hashValue.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = (metadataFile != null ? prime * result + (int) (metadataFile.getId() ^ (metadataFile.getId() >>> 32))
                : result);
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
        if (!(obj instanceof MetadataFileHash)) {
            return false;
        }
        MetadataFileHash other = (MetadataFileHash) obj;
        if (hashType != other.getHashType()) {
            return false;
        }
        if (hashValue == null) {
            if (other.getHashValue() != null) {
                return false;
            }
        } else if (!hashValue.equals(other.getHashValue())) {
            return false;
        }
        if (id != other.getId()) {
            return false;
        }
        if (type == null) {
            if (other.getType() != null) {
                return false;
            }
        } else if (!type.equals(other.getType())) {
            return false;
        }
        if (metadataFile == null) {
            if (other.getMetadataFile() != null) {
                return false;
            }
        } else if (other.getMetadataFile() != null) {
            if (metadataFile.getId() != other.getMetadataFile().getId()) {
                return false;
            }
        }
        return true;
    }


    @Override
    public String toString() {
        return "FileHash [id=" + id + ", hashType=" + hashType + ", hashValue=" + hashValue + ", type=" + type
                + ", metadataFile=" + metadataFile + "]";
    }
}
