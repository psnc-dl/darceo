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

import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;

/**
 * Represents data file hash.
 */
@Entity
@DiscriminatorValue("DATAFILE")
public class DataFileHash extends FileHash {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -622847288762922805L;

    /**
     * Data file for which the hash is stored.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "FH_DATAFILE_ID", nullable = true)
    private DataFile dataFile;


    public void setDataFile(DataFile dataFile) {
        this.dataFile = dataFile;
    }


    public DataFile getDataFile() {
        return dataFile;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hashType == null) ? 0 : hashType.hashCode());
        result = prime * result + ((hashValue == null) ? 0 : hashValue.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = (dataFile != null ? prime * result + (int) (dataFile.getId() ^ (dataFile.getId() >>> 32)) : result);
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
        if (!(obj instanceof DataFileHash)) {
            return false;
        }
        DataFileHash other = (DataFileHash) obj;
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
        if (dataFile == null) {
            if (other.getDataFile() != null) {
                return false;
            }
        } else if (other.getDataFile() != null) {
            if (dataFile.getId() != other.getDataFile().getId()) {
                return false;
            }
        }
        return true;
    }


    @Override
    public String toString() {
        return "FileHash [id=" + id + ", hashType=" + hashType + ", hashValue=" + hashValue + ", type=" + type
                + ", dataFile=" + dataFile + "]";
    }

}
