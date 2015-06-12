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

import pl.psnc.synat.wrdz.zmd.entity.types.ValidationStatus;

/**
 * An entity representing a result of the data file validation.
 */
@Entity
@Table(name = "ZMD_DATA_FILE_VALIDATION", schema = "darceo")
public class DataFileValidation {

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "dataFileValidationSequenceGenerator", sequenceName = "darceo.ZMD_DFV_ID_SEQ",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dataFileValidationSequenceGenerator")
    @Column(name = "DFV_ID", unique = true, nullable = false)
    private long id;

    /**
     * Validation status.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "DFN_STATUS", length = 11, nullable = false)
    private ValidationStatus status;

    /**
     * All names of the format.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
    @JoinTable(name = "ZMD_DATA_FILE_VALIDATION_VALIDATION_WARNING", schema = "darceo", joinColumns = { @JoinColumn(
            name = "DFV_ID", referencedColumnName = "DFV_ID") }, inverseJoinColumns = { @JoinColumn(name = "VW_ID",
            referencedColumnName = "VW_ID") })
    protected List<ValidationWarning> warnings = new ArrayList<ValidationWarning>();


    /**
     * Default constructor.
     */
    public DataFileValidation() {
    }


    /**
     * Constructor with the validation status.
     * 
     * @param status
     *            status
     */
    public DataFileValidation(ValidationStatus status) {
        this.status = status;
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public ValidationStatus getStatus() {
        return status;
    }


    public void setStatus(ValidationStatus status) {
        this.status = status;
    }


    public List<ValidationWarning> getWarnings() {
        return warnings;
    }


    public void setWarnings(List<ValidationWarning> warnings) {
        this.warnings = warnings;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((status == null) ? 0 : status.hashCode());
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
        if (!(obj instanceof DataFileValidation)) {
            return false;
        }
        DataFileValidation other = (DataFileValidation) obj;
        if (id != other.getId()) {
            return false;
        }
        if (status == null) {
            if (other.status != null) {
                return false;
            }
        } else if (!status.equals(other.status)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("DataFileValidation ");
        sb.append("[id = ").append(id);
        sb.append(", status = ").append(status);
        sb.append("]");
        return sb.toString();
    }

}
