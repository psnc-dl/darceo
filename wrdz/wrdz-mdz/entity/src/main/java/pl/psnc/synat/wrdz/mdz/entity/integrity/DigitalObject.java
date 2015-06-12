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
package pl.psnc.synat.wrdz.mdz.entity.integrity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Represents a WRDZ digital object for the purposes of data integrity verification.
 */
@Entity(name = "MdzDigitalObject")
@Table(name = "MDZ_DIGITAL_OBJECTS", schema = "darceo")
public class DigitalObject implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -912019247394062315L;

    /** ZMD digital object identifier. */
    @Id
    @Column(name = "DO_IDENTIFIER", length = 255, nullable = false, unique = true)
    private String identifier;

    /** Date when this object was added for verification. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DO_ADDED_ON", nullable = false)
    private Date addedOn;

    /** Date when this object was verified. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DO_VERIFIED_ON", nullable = true)
    private Date verifiedOn;

    /** Whether the hashes were correct. */
    @Column(name = "DO_CORRECT", nullable = true)
    private Boolean correct;


    public String getIdentifier() {
        return identifier;
    }


    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }


    public Date getAddedOn() {
        return addedOn;
    }


    public void setAddedOn(Date addedOn) {
        this.addedOn = addedOn;
    }


    public Date getVerifiedOn() {
        return verifiedOn;
    }


    public void setVerifiedOn(Date verifiedOn) {
        this.verifiedOn = verifiedOn;
    }


    public Boolean getCorrect() {
        return correct;
    }


    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }
}
