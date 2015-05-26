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
package pl.psnc.synat.wrdz.zmd.entity.object.metadata;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import pl.psnc.synat.wrdz.zmd.entity.object.hash.MetadataFileHash;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;
import pl.psnc.synat.wrdz.zmd.entity.types.MetadataType;

/**
 * An abstract entity representing file containing metadata.
 * 
 * @see {@link FileExtractedMetadata}, {@link FileProvidedMetadata}
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "MF_TYPE", discriminatorType = DiscriminatorType.STRING)
@Table(name = "ZMD_METADATA_FILES", schema = "darceo")
public abstract class MetadataFile implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 960418107965223100L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "metadataFileSequenceGenerator", sequenceName = "darceo.ZMD_MF_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "metadataFileSequenceGenerator")
    @Column(name = "MF_ID", unique = true, nullable = false)
    protected long id;

    /**
     * Full path to the file in the repository.
     */
    @Column(name = "MF_REPO_PATH", length = 255, nullable = false)
    protected String repositoryFilepath;

    /**
     * Object-relative path of the file.
     */
    @Column(name = "MF_OBJ_PATH", length = 255, nullable = false)
    protected String objectFilepath;

    /**
     * Filename of the file.
     */
    @Column(name = "MF_FILENAME", length = 255, nullable = false)
    protected String filename;

    /**
     * File size in bytes.
     */
    @Column(name = "MF_SIZE", nullable = false)
    protected long size;

    /**
     * Metadata file type.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "MF_TYPE", length = 20, nullable = false)
    protected MetadataType type;

    /**
     * Namespace of the root element of the metadata XML file.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "MF_MAIN_NAMESPACE", nullable = true)
    protected MetadataNamespace mainNamespace;

    /**
     * Namespaces used by this metadata file.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
    @JoinTable(name = "ZMD_METADATA_FILES_METADATA_NAMESPACES", schema = "darceo", joinColumns = { @JoinColumn(
            name = "MF_ID", referencedColumnName = "MF_ID") }, inverseJoinColumns = { @JoinColumn(name = "MN_ID",
            referencedColumnName = "MN_ID") })
    protected List<MetadataNamespace> usedNamespaces = new ArrayList<MetadataNamespace>();

    /**
     * List of hashes generated for this file.
     */
    @OneToMany(mappedBy = "metadataFile", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    protected List<MetadataFileHash> hashes = new ArrayList<MetadataFileHash>();

    /**
     * Metadata contents.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
    @JoinTable(name = "ZMD_METADATA_OPERATIONS_METADATA_FILES", schema = "darceo", joinColumns = { @JoinColumn(
            name = "MOMF_MF_ID", referencedColumnName = "MF_ID") }, inverseJoinColumns = { @JoinColumn(
            name = "MOMF_OP_ID", referencedColumnName = "OP_ID") })
    protected List<Operation> metadataContent = new ArrayList<Operation>();

    /**
     * Path of the file in the cache - used in the procedures of creation and modification of the object.
     */
    @Transient
    private String cachePath;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getRepositoryFilepath() {
        return repositoryFilepath;
    }


    public void setRepositoryFilepath(String repositoryFilepath) {
        this.repositoryFilepath = repositoryFilepath;
    }


    public String getObjectFilepath() {
        return objectFilepath;
    }


    public void setObjectFilepath(String objectFilepath) {
        this.objectFilepath = objectFilepath;
    }


    public String getFilename() {
        return filename;
    }


    public void setFilename(String filename) {
        this.filename = filename;
    }


    public void setSize(long size) {
        this.size = size;
    }


    public long getSize() {
        return size;
    }


    public MetadataType getType() {
        return type;
    }


    public void setType(MetadataType type) {
        this.type = type;
    }


    public List<MetadataNamespace> getUsedNamespaces() {
        return usedNamespaces;
    }


    public void setUsedNamespaces(List<MetadataNamespace> usedNamespaces) {
        this.usedNamespaces = usedNamespaces;
    }


    public void setHashes(List<MetadataFileHash> hashes) {
        this.hashes = hashes;
    }


    public List<MetadataFileHash> getHashes() {
        return hashes;
    }


    /**
     * Gets list of operations performed on this metadata file.
     * 
     * @return list of operations or empty list if none was found.
     */
    public List<Operation> getMetadataContent() {
        return metadataContent;
    }


    public void setMetadataContent(List<Operation> metadataContent) {
        this.metadataContent = metadataContent;
    }


    public MetadataNamespace getMainNamespace() {
        return mainNamespace;
    }


    public void setMainNamespace(MetadataNamespace mainNamespace) {
        this.mainNamespace = mainNamespace;
    }


    public String getCachePath() {
        return cachePath;
    }


    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }

}
