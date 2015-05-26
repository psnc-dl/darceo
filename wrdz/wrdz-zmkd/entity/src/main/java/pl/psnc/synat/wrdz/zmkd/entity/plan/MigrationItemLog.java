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
package pl.psnc.synat.wrdz.zmkd.entity.plan;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Represents an item of migration, i.e. identifier of WRDZ digital object and information about migration process
 * concerning this object.
 */
@Entity()
@Table(name = "ZMKD_MIGRATION_ITEM_LOGS", schema = "darceo")
public class MigrationItemLog implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 3707745670681665688L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "zmkdMigrationItemLogsSequenceGenerator", sequenceName = "darceo.ZMKD_MIL_ID_SEQ",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "zmkdMigrationItemLogsSequenceGenerator")
    @Column(name = "MIL_ID", unique = true, nullable = false)
    private long id;

    /**
     * ZMD digital object identifier.
     */
    @Column(name = "MIL_OBJECT_IDENTIFIER", unique = false, nullable = false, length = 255)
    private String objectIdentifier;

    /**
     * Migration plan for this item of plan.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MIL_MIGRATION_PLAN_ID", nullable = false)
    private MigrationPlan migrationPlan;

    /**
     * Migration status.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "MIL_STATUS", length = 20, nullable = false)
    private MigrationItemStatus status;

    /**
     * Date and time when the migration of this object started.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MIL_STARTED_ON", nullable = true)
    private Date startedOn;

    /**
     * Date and time when the migration of this object ended.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MIL_ENDED_ON", nullable = true)
    private Date endedOn;

    /**
     * Optional error message params.
     */
    @Column(name = "MIL_ERROR_MESSAGE_PARAMS", unique = false, nullable = true, length = 1024)
    private String errorMessageParams;

    /**
     * ZMD object creation request id.
     */
    @Column(name = "MIL_REQUEST_ID", unique = true, nullable = true)
    private String requestId;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getObjectIdentifier() {
        return objectIdentifier;
    }


    public void setObjectIdentifier(String objectIdentifier) {
        this.objectIdentifier = objectIdentifier;
    }


    public MigrationPlan getMigrationPlan() {
        return migrationPlan;
    }


    public void setMigrationPlan(MigrationPlan migrationPlan) {
        this.migrationPlan = migrationPlan;
    }


    public MigrationItemStatus getStatus() {
        return status;
    }


    public void setStatus(MigrationItemStatus status) {
        this.status = status;
    }


    public Date getStartedOn() {
        return startedOn;
    }


    public void setStartedOn(Date startedOn) {
        this.startedOn = startedOn;
    }


    public Date getEndedOn() {
        return endedOn;
    }


    public void setEndedOn(Date endedOn) {
        this.endedOn = endedOn;
    }


    public String getErrorMessageParams() {
        return errorMessageParams;
    }


    public void setErrorMessageParams(String errorMessageParams) {
        this.errorMessageParams = errorMessageParams;
    }


    public String getRequestId() {
        return requestId;
    }


    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

}
