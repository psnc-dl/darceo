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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PreRemove;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.PrivateOwned;

import pl.psnc.synat.wrdz.zmkd.entity.format.FileFormat;

/**
 * Migration plan.
 */
@Entity
@Table(name = "ZMKD_MIGRATION_PLANS", schema = "darceo")
public class MigrationPlan implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 7851863573210829492L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "migrationPlanSequenceGenerator", sequenceName = "darceo.ZMKD_MP_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "migrationPlanSequenceGenerator")
    @Column(name = "MP_ID", unique = true, nullable = false)
    private long id;

    /**
     * XML file with a migration plan.
     */
    @Basic(fetch = FetchType.LAZY, optional = true)
    @Column(name = "MP_XML_FILE", nullable = true, unique = false)
    private String xmlFile;

    /**
     * Input file format.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "MP_INPUT_FILE_FORMAT", nullable = true)
    private FileFormat inputFileFormat;

    /**
     * Output file format.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "MP_OUTPUT_FILE_FORMAT", nullable = true)
    private FileFormat outputFileFormat;

    /**
     * List of ZMD digital object identifiers with logs concerning migration process.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "migrationPlan", cascade = { CascadeType.ALL })
    @PrivateOwned
    private List<MigrationItemLog> migrationItems = new ArrayList<MigrationItemLog>();

    /**
     * Migration paths.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "migrationPlan", cascade = { CascadeType.ALL })
    @PrivateOwned
    private List<MigrationPath> migrationPaths = new ArrayList<MigrationPath>();

    /**
     * Active migration path.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "MP_ACTIVE_PATH_ID", nullable = true)
    private MigrationPath activePath;

    /**
     * Status of migration plan.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "MP_STATUS", length = 20, nullable = false)
    private MigrationPlanStatus status;

    /**
     * Internal status of migration plan.
     */
    @Column(name = "MP_AWAITING_OBJECT", length = 255, nullable = true, unique = false)
    private String objectIdentifierAwaited;

    /**
     * Optional error message.
     */
    @Column(name = "MP_AWAITING_SERVICE", length = 255, nullable = true, unique = false)
    private String serviceIdentifierAwaited;

    /**
     * Id of the owner of this migration plan.
     */
    @Column(name = "MP_OWNER_ID", nullable = false, unique = false)
    protected Long ownerId;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getXmlFile() {
        return xmlFile;
    }


    public void setXmlFile(String xmlFile) {
        this.xmlFile = xmlFile;
    }


    public FileFormat getInputFileFormat() {
        return inputFileFormat;
    }


    public void setInputFileFormat(FileFormat inputFileFormat) {
        this.inputFileFormat = inputFileFormat;
    }


    public FileFormat getOutputFileFormat() {
        return outputFileFormat;
    }


    public void setOutputFileFormat(FileFormat outputFileFormat) {
        this.outputFileFormat = outputFileFormat;
    }


    public List<MigrationItemLog> getMigrationItems() {
        return migrationItems;
    }


    public void setMigrationItems(List<MigrationItemLog> migrationItems) {
        this.migrationItems = migrationItems;
    }


    public List<MigrationPath> getMigrationPaths() {
        return migrationPaths;
    }


    public void setMigrationPaths(List<MigrationPath> migrationPaths) {
        this.migrationPaths = migrationPaths;
    }


    public MigrationPath getActivePath() {
        return activePath;
    }


    public void setActivePath(MigrationPath activePath) {
        this.activePath = activePath;
    }


    public MigrationPlanStatus getStatus() {
        return status;
    }


    public void setStatus(MigrationPlanStatus status) {
        this.status = status;
    }


    public String getObjectIdentifierAwaited() {
        return objectIdentifierAwaited;
    }


    public void setObjectIdentifierAwaited(String objectIdentifierAwaited) {
        this.objectIdentifierAwaited = objectIdentifierAwaited;
    }


    public String getServiceIdentifierAwaited() {
        return serviceIdentifierAwaited;
    }


    public void setServiceIdentifierAwaited(String serviceIdentifierAwaited) {
        this.serviceIdentifierAwaited = serviceIdentifierAwaited;
    }


    public Long getOwnerId() {
        return ownerId;
    }


    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }


    /**
     * Clears the active path so that both the plan and its paths can be deleted.
     */
    @PreRemove
    protected void preRemove() {
        this.activePath = null;
    }
}
