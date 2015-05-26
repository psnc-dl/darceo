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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * An entity representing format name of the file containing object's data.
 */
@Entity
@Table(name = "ZMD_DATA_FILE_FORMAT_NAMES", schema = "darceo")
public class DataFileFormatName {

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "dataFileFormatNameSequenceGenerator", sequenceName = "darceo.ZMD_DFFN_ID_SEQ",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dataFileFormatNameSequenceGenerator")
    @Column(name = "DFFN_ID", unique = true, nullable = false)
    private long id;

    /**
     * Name of the format.
     */
    @Column(name = "DFFN_NAME", length = 254, nullable = false, unique = true)
    private String name;


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


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
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
        if (!(obj instanceof DataFileFormatName)) {
            return false;
        }
        DataFileFormatName other = (DataFileFormatName) obj;
        if (id != other.getId()) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("DataFileFormatName ");
        sb.append("[id = ").append(id);
        sb.append(", name = ").append(name);
        sb.append("]");
        return sb.toString();
    }

}
