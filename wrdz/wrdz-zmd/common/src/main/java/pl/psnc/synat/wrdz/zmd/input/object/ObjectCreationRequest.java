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
package pl.psnc.synat.wrdz.zmd.input.object;

import java.io.Serializable;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import pl.psnc.synat.wrdz.common.utility.FormattedToStringBuilder;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;
import pl.psnc.synat.wrdz.zmd.input.InputFile;
import pl.psnc.synat.wrdz.zmd.input.MigrationInformation;
import pl.psnc.synat.wrdz.zmd.input.object.validation.constraints.ValidObjectCreationQuery;

/**
 * Class representing object creation request data. It contains information about object's content and metadata,
 * additionally specifying object's origin (source object) and derived objects.
 */
@ValidObjectCreationQuery
public class ObjectCreationRequest implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 7119308971599589197L;

    /**
     * Proposed id part of object identifier.
     */
    private String proposedId;

    /**
     * List of desired object contents with optionally attached file-specific metadata.
     */
    private final Set<InputFile> inputFiles;

    /**
     * Object metadata provided by user.
     */
    private final Map<String, URI> objectMetadata;

    /**
     * Type of object.
     */
    private final ObjectType type;

    /**
     * Information specifying object's origins.
     */
    private MigrationInformation migratedFrom;

    /**
     * Information specifying object's derivatives.
     */
    private Set<MigrationInformation> migratedTo;

    /**
     * Object-relative path to the main file of an object.
     */
    private String mainFile;

    /** Object name. */
    private String name;

    /** URI to provided METS file. */
    private URI providedMets;


    /**
     * Constructs new instance based upon given input files list and object type.
     * 
     * @param inputFiles
     *            list of desired object contents with optionally attached file-specific metadata.
     * @param type
     *            object type.
     * @throws IllegalArgumentException
     *             if any of arguments is null or the input file list is empty.
     */
    public ObjectCreationRequest(Set<InputFile> inputFiles, ObjectType type)
            throws IllegalArgumentException {
        if (type == null) {
            throw new IllegalArgumentException("Object type cannot be null!");
        }
        if (inputFiles == null || inputFiles.size() == 0) {
            throw new IllegalArgumentException("Input files list cannot be null nor empty!");
        }
        this.inputFiles = inputFiles;
        this.objectMetadata = null;
        this.type = type;
    }


    /**
     * Constructs new instance based upon given input files, object's metadata and object type.
     * 
     * @param inputFiles
     *            list of desired object contents with optionally attached file-specific metadata.
     * @param objectMetadata
     *            object's metadata provided by user.
     * @param type
     *            object type.
     * @throws IllegalArgumentException
     *             if object type or input files list are null or the input files list is empty.
     */
    public ObjectCreationRequest(Set<InputFile> inputFiles, Map<String, URI> objectMetadata, ObjectType type)
            throws IllegalArgumentException {
        if (type == null) {
            throw new IllegalArgumentException("Object type cannot be null!");
        }
        if (inputFiles == null || inputFiles.size() == 0) {
            throw new IllegalArgumentException("Input files list cannot be null nor empty!");
        }
        this.inputFiles = inputFiles;
        this.objectMetadata = objectMetadata;
        this.type = type;
    }


    public String getProposedId() {
        return proposedId;
    }


    public void setProposedId(String proposedId) {
        this.proposedId = proposedId;
    }


    public MigrationInformation getMigratedFrom() {
        return migratedFrom;
    }


    public void setMigratedFrom(MigrationInformation migratedFrom) {
        this.migratedFrom = migratedFrom;
    }


    public Set<MigrationInformation> getMigratedTo() {
        return migratedTo;
    }


    public void setMigratedTo(Set<MigrationInformation> migratedTo) {
        this.migratedTo = migratedTo;
    }


    public String getMainFile() {
        return mainFile;
    }


    public void setMainFile(String mainFile) {
        this.mainFile = mainFile;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public void setMetsProvidedURI(URI metsURI) {
        this.providedMets = metsURI;
    }


    public URI getMetsProvidedURI() {
        return this.providedMets;
    }


    public Set<InputFile> getInputFiles() {
        return inputFiles;
    }


    public Map<String, URI> getObjectMetadata() {
        return objectMetadata;
    }


    public ObjectType getType() {
        return type;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((inputFiles == null) ? 0 : inputFiles.hashCode());
        result = prime * result + ((migratedFrom == null) ? 0 : migratedFrom.hashCode());
        result = prime * result + ((migratedTo == null) ? 0 : migratedTo.hashCode());
        result = prime * result + ((objectMetadata == null) ? 0 : objectMetadata.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((mainFile == null) ? 0 : mainFile.hashCode());
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
        if (!(obj instanceof ObjectCreationRequest)) {
            return false;
        }
        ObjectCreationRequest other = (ObjectCreationRequest) obj;
        if (inputFiles == null) {
            if (other.inputFiles != null) {
                return false;
            }
        } else if (!inputFiles.equals(other.inputFiles)) {
            return false;
        }
        if (migratedFrom == null) {
            if (other.migratedFrom != null) {
                return false;
            }
        } else if (!migratedFrom.equals(other.migratedFrom)) {
            return false;
        }
        if (migratedTo == null) {
            if (other.migratedTo != null) {
                return false;
            }
        } else if (!migratedTo.equals(other.migratedTo)) {
            return false;
        }
        if (objectMetadata == null) {
            if (other.objectMetadata != null) {
                return false;
            }
        } else if (!objectMetadata.equals(other.objectMetadata)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        if (mainFile == null) {
            if (other.mainFile != null) {
                return false;
            }
        } else if (!mainFile.equals(other.mainFile)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        FormattedToStringBuilder builder = new FormattedToStringBuilder("ObjectCreationRequest");
        builder.append("proposedId", proposedId).append("inputFiles", inputFiles)
                .append("objectMetadata", objectMetadata).append("type", type).append("migratedFrom", migratedFrom)
                .append("migratedTo", migratedTo).append("mainFile", mainFile).append("providedMets", providedMets);
        return builder.toString();
    }

}
