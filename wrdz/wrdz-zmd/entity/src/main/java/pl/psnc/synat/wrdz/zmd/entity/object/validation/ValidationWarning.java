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
package pl.psnc.synat.wrdz.zmd.entity.object.validation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * An entity representing a warning message which was returned from the process of the data file validation.
 */
@Entity
@Table(name = "ZMD_VALIDATION_WARNING", schema = "darceo")
public class ValidationWarning {

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "validationWarningSequenceGenerator", sequenceName = "darceo.ZMD_VW_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "validationWarningSequenceGenerator")
    @Column(name = "VW_ID", unique = true, nullable = false)
    private long id;

    /**
     * Name of the format.
     */
    @Column(name = "VW_WARNING", length = 512, nullable = false, unique = true)
    private String warning;


    /**
     * Default constructor.
     */
    public ValidationWarning() {
    }


    /**
     * Constructor with warning.
     * 
     * @param warning
     *            warning
     */
    public ValidationWarning(String warning) {
        this.warning = warning;
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getWarning() {
        return warning;
    }


    public void setWarning(String warning) {
        this.warning = warning;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((warning == null) ? 0 : warning.hashCode());
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
        if (!(obj instanceof ValidationWarning)) {
            return false;
        }
        ValidationWarning other = (ValidationWarning) obj;
        if (id != other.getId()) {
            return false;
        }
        if (warning == null) {
            if (other.warning != null) {
                return false;
            }
        } else if (!warning.equals(other.warning)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("ValidationWarning ");
        sb.append("[id = ").append(id);
        sb.append(", warning = ").append(warning);
        sb.append("]");
        return sb.toString();
    }

}
