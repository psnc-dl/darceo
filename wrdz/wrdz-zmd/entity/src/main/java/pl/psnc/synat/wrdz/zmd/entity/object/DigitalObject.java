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
package pl.psnc.synat.wrdz.zmd.entity.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.PrivateOwned;

import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Conversion;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;

/**
 * An abstract entity representing digital object.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DO_TYPE", discriminatorType = DiscriminatorType.STRING)
@Table(name = "ZMD_DIGITAL_OBJECTS", schema = "darceo")
public abstract class DigitalObject implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -2701792256005147602L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "digitalObjectSequenceGenerator", sequenceName = "darceo.ZMD_DO_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "digitalObjectSequenceGenerator")
    @Column(name = "DO_ID", unique = true, nullable = false)
    protected long id;

    /**
     * Object's type specified by {@link ObjectType}.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "DO_TYPE", length = 10, nullable = false)
    protected ObjectType type;

    /**
     * Date of the last modification of object state.
     */
    @OneToMany(mappedBy = "object", fetch = FetchType.LAZY)
    protected List<Operation> operations = new ArrayList<Operation>();

    /**
     * Object's default identifier.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.ALL })
    @JoinColumn(name = "DO_IDENTIFIER_ID", nullable = true)
    protected Identifier defaultIdentifier;

    /**
     * All identifiers of this object.
     */
    @PrivateOwned
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "object", cascade = { CascadeType.ALL })
    protected List<Identifier> identifiers = new ArrayList<Identifier>();

    /**
     * Current version of the object's content.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "DO_CURRENT_VERSION", nullable = true)
    protected ContentVersion currentVersion;

    /**
     * All versions of object's content (including the current one).
     */
    @PrivateOwned
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "object", cascade = { CascadeType.ALL })
    protected List<ContentVersion> versions = new ArrayList<ContentVersion>();

    /**
     * All conversion operations performed on this object.
     */
    @PrivateOwned
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "source", cascade = { CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REFRESH })
    protected List<Conversion> convertedTo = new ArrayList<Conversion>();

    /**
     * Id of the owner of this object.
     */
    @Column(name = "DO_OWNER_ID", nullable = true)
    protected Long ownerId;

    /** Object name. */
    @Column(name = "DO_NAME", nullable = true)
    protected String name;


    /**
     * Creates new instance of digital object.
     * 
     * @param type
     *            object's type.
     */
    public DigitalObject(ObjectType type) {
        this.type = type;
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public ObjectType getType() {
        return type;
    }


    public void setType(ObjectType type) {
        this.type = type;
    }


    public List<Operation> getOperations() {
        return operations;
    }


    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }


    public Identifier getDefaultIdentifier() {
        return defaultIdentifier;
    }


    public void setDefaultIdentifier(Identifier defaultIdentifier) {
        this.defaultIdentifier = defaultIdentifier;
    }


    public List<Identifier> getIdentifiers() {
        return identifiers;
    }


    public void setIdentifiers(List<Identifier> identifiers) {
        this.identifiers = identifiers;
    }


    public ContentVersion getCurrentVersion() {
        return currentVersion;
    }


    public void setCurrentVersion(ContentVersion currentVersion) {
        this.currentVersion = currentVersion;
    }


    public List<ContentVersion> getVersions() {
        return versions;
    }


    public void setVersions(List<ContentVersion> versions) {
        this.versions = versions;
    }


    /**
     * Fetches objects create by conversion from this object.
     * 
     * @return list of objects or empty list if none was found.
     */
    public List<Conversion> getConvertedTo() {
        return convertedTo;
    }


    public void setConvertedTo(List<Conversion> convertedTo) {
        this.convertedTo = convertedTo;
    }


    public Long getOwnerId() {
        return ownerId;
    }


    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    /**
     * Gets object's origin.
     * 
     * @return object's origin or <code>null</code> if none was found.
     */
    @SuppressWarnings("rawtypes")
    public abstract Migration getOrigin();


    /**
     * Adds migration information about object's derivative.
     * 
     * @param migration
     *            migration operation resulting in creation of the derivative.
     */
    @SuppressWarnings("rawtypes")
    public abstract void addDerivative(Migration migration);


    /**
     * Adds migration information about object's origin.
     * 
     * @param migration
     *            migration operation resulting in creation of this object.
     */
    @SuppressWarnings("rawtypes")
    public abstract void addSource(Migration migration);


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((currentVersion == null) ? 0 : (int) (currentVersion.getId() ^ (currentVersion.getId() >>> 32)));
        result = prime
                * result
                + ((defaultIdentifier == null) ? 0
                        : (int) (defaultIdentifier.getId() ^ (defaultIdentifier.getId() >>> 32)));
        result = prime * result + (int) (id ^ (id >>> 32));
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
        if (!(obj instanceof DigitalObject)) {
            return false;
        }
        DigitalObject other = (DigitalObject) obj;
        if (id != other.getId()) {
            return false;
        }
        if (type != other.getType()) {
            return false;
        }
        if (defaultIdentifier == null) {
            if (other.getDefaultIdentifier() != null) {
                return false;
            }
        } else if (other.getDefaultIdentifier() != null) {
            if (defaultIdentifier.getId() != other.getDefaultIdentifier().getId()) {
                return false;
            }
        } else {
            return false;
        }
        if (currentVersion == null) {
            if (other.getCurrentVersion() != null) {
                return false;
            }
        } else if (other.getCurrentVersion() != null) {
            if (currentVersion.getId() != other.getCurrentVersion().getId()) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DigitalObject [id=" + id + ", type=" + type + ", defaultIdentifier=");
        if (getDefaultIdentifier() == null) {
            sb.append("null]");
        } else {
            sb.append("[id=" + getDefaultIdentifier().getId() + "], currentVersion=");
        }
        if (getCurrentVersion() == null) {
            sb.append("null]]");
        } else {
            sb.append("[id=" + getCurrentVersion().getId() + "]]");
        }
        return sb.toString();
    }

}
