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
package pl.psnc.synat.wrdz.zmd.entity.object.migration;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.types.MigrationType;

/**
 * An abstract entity representing migration of an object.
 * 
 * @param <S>
 *            Type of migrationSource object.
 * @param <T>
 *            Type of derived object.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "M_TYPE", discriminatorType = DiscriminatorType.STRING)
@Table(name = "ZMD_MIGRATIONS", schema = "darceo")
public abstract class Migration<S extends DigitalObject, T extends DigitalObject> implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 6671060146815812209L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "migrationSequenceGenerator", sequenceName = "darceo.ZMD_M_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "migrationSequenceGenerator")
    @Column(name = "M_ID", unique = true, nullable = false)
    protected long id;

    /**
     * Identifier of the migrationSource object, preferably in the URL compliant form.
     */
    @Column(name = "M_SOURCE_ID", length = 255, nullable = true)
    protected String sourceIdentifier;

    /**
     * Source's identifier resolver, preferably in the URL compliant form.
     */
    @Column(name = "M_SRC_ID_RESOLVER", length = 255, nullable = true)
    protected String sourceIdentifierResolver;

    /**
     * Identifier of the migrationResult object, preferably in the URL compliant form.
     */
    @Column(name = "M_RESULT_ID", length = 255, nullable = true)
    protected String resultIdentifier;

    /**
     * Source's identifier resolver, preferably in the URL compliant form.
     */
    @Column(name = "M_RES_ID_RESOLVER", length = 255, nullable = true)
    protected String resultIdentifierResolver;

    /**
     * Type of the migration operation.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "M_TYPE", length = 15, nullable = false)
    protected MigrationType type;

    /**
     * Date of the migration operation.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "M_DATE", nullable = true)
    protected Date date;

    /**
     * Info about migration.
     */
    @Column(name = "M_INFO", length = 512, nullable = true)
    protected String info;

    /**
     * Source object being converted.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "M_SOURCE_OBJECT_ID", nullable = true)
    protected DigitalObject migrationSource;

    /**
     * Result of conversion.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "M_RESULT_OBJECT_ID", nullable = true, unique = true)
    protected DigitalObject migrationResult;


    /**
     * Creates new instance of migration.
     * 
     * @param type
     *            migration type.
     */
    public Migration(MigrationType type) {
        this.type = type;
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getSourceIdentifier() {
        return sourceIdentifier;
    }


    public void setSourceIdentifier(String sourceIdentifier) {
        this.sourceIdentifier = sourceIdentifier;
    }


    public String getSourceIdentifierResolver() {
        return sourceIdentifierResolver;
    }


    public void setSourceIdentifierResolver(String sourceIdentifierResolver) {
        this.sourceIdentifierResolver = sourceIdentifierResolver;
    }


    public String getResultIdentifier() {
        return resultIdentifier;
    }


    public void setResultIdentifier(String resultIdentifier) {
        this.resultIdentifier = resultIdentifier;
    }


    public String getResultIdentifierResolver() {
        return resultIdentifierResolver;
    }


    public void setResultIdentifierResolver(String resultIdentifierResolver) {
        this.resultIdentifierResolver = resultIdentifierResolver;
    }


    public MigrationType getType() {
        return type;
    }


    public void setType(MigrationType type) {
        this.type = type;
    }


    public Date getDate() {
        return date;
    }


    public void setDate(Date date) {
        this.date = date;
    }


    public String getInfo() {
        return info;
    }


    public void setInfo(String info) {
        this.info = info;
    }


    /**
     * Fetches the migrationSource object of this migration operation, i.e. object that this migration used to produce
     * output object.
     * 
     * @return migrationSource object of this migration operation or null, if migrationSource is not an object from the
     *         repository.
     */
    public DigitalObject getMigrationSource() {
        return migrationSource;
    }


    /**
     * Fetches the derivative object of this migration operation, i.e. object that this migration produced.
     * 
     * @return derivative object of this migration operation or null, if derivative is not an object in the repository.
     */
    public DigitalObject getMigrationResult() {
        return migrationResult;
    }


    /**
     * Sets the migrationSource object of this migration operation, i.e. object that this migration used to produce
     * output object.
     * 
     * @param migrationSource
     *            migrationSource object of this migration operation or null, if migrationSource is not an object from
     *            the repository.
     */
    public void setMigrationSource(DigitalObject migrationSource) {
        this.migrationSource = migrationSource;
    }


    /**
     * Sets the derivative object of this migration operation, i.e. object that this migration produced.
     * 
     * @param migrationResult
     *            derivative object of this migration operation or null, if derivative is not an object in the
     *            repository.
     */
    public void setMigrationResult(DigitalObject migrationResult) {
        this.migrationResult = migrationResult;
    }

}
