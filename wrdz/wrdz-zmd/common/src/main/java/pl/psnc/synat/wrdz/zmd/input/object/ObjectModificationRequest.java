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
import pl.psnc.synat.wrdz.zmd.input.InputFile;
import pl.psnc.synat.wrdz.zmd.input.InputFileUpdate;
import pl.psnc.synat.wrdz.zmd.input.MigrationInformation;
import pl.psnc.synat.wrdz.zmd.input.MigrationInformationUpdate;
import pl.psnc.synat.wrdz.zmd.input.object.validation.constraints.ValidObjectModificationQuery;

/**
 * Class representing object modification request data. It contains informations which data and matadata files have to
 * be added, modified and deleted. Moreover it contains information how to change the origin and derivatives of the
 * object, and how to change the main file of the object.
 */
@ValidObjectModificationQuery
public class ObjectModificationRequest implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -6828354825677976504L;

    /**
     * Public identifier of the object which have to be modified.
     */
    private final String identifier;

    /**
     * Whether delete all content (data and metadata) before modifying of the object. This flag is needed in
     * modification of the object by providing the zip file - it replaces all content by a new one.
     */
    private boolean deleteAllContent;

    /**
     * List of data files with optionally attached file-specific metadata which have to be added to the object.
     */
    private Set<InputFile> inputFilesToAdd;

    /**
     * List of data files with optionally attached file-specific metadata which have to be modified in the object.
     */
    private Set<InputFileUpdate> inputFilesToModify;

    /**
     * List of data files names which have to be removed from the object.
     */
    private Set<String> inputFilesToRemove;

    /**
     * List of object metadata which have to be added to the object.
     */
    private Map<String, URI> objectMetadataToAdd;

    /**
     * List of object metadata which have to be modified in the object.
     */
    private Map<String, URI> objectMetadataToModify;

    /**
     * Set of object metadata which have to be removed from the object.
     */
    private Set<String> objectMetadataToRemove;

    /**
     * Information specifying object's origins or null if the origin does not change.
     */
    private MigrationInformation migratedFrom;

    /**
     * Information specifying object's derivatives which have to be added.
     */
    private Set<MigrationInformation> migratedToToAdd;

    /**
     * Information specifying object's derivatives which have to be modified.
     */
    private Set<MigrationInformationUpdate> migratedToToModify;

    /**
     * Information specifying object's derivatives which have to be removed.
     */
    private Set<String> migratedToToRemove;

    /**
     * Object-relative path to the main file of an object or null if the main file does not change. Empty string means
     * deletion of the information about the main file.
     */
    private String mainFile;

    /** URI to provided METS file. */
    private URI providedMets;


    /**
     * Constructs new instance based upon given identifier of the object and its new constituents.
     * 
     * @param identifier
     *            identifier of the object
     * @throws IllegalArgumentException
     *             if any of arguments is null
     */
    public ObjectModificationRequest(String identifier)
            throws IllegalArgumentException {
        this.identifier = identifier;
        this.deleteAllContent = false;
        this.providedMets = null;
    }


    public String getIdentifier() {
        return identifier;
    }


    public boolean getDeleteAllContent() {
        return deleteAllContent;
    }


    public void setDeleteAllContent(boolean deleteAllContent) {
        this.deleteAllContent = deleteAllContent;
    }


    public Set<InputFile> getInputFilesToAdd() {
        return inputFilesToAdd;
    }


    public void setInputFilesToAdd(Set<InputFile> inputFilesToAdd) {
        this.inputFilesToAdd = inputFilesToAdd;
    }


    public Set<InputFileUpdate> getInputFilesToModify() {
        return inputFilesToModify;
    }


    public void setInputFilesToModify(Set<InputFileUpdate> inputFilesToModify) {
        this.inputFilesToModify = inputFilesToModify;
    }


    public Set<String> getInputFilesToRemove() {
        return inputFilesToRemove;
    }


    public void setInputFilesToRemove(Set<String> inputFilesToRemove) {
        this.inputFilesToRemove = inputFilesToRemove;
    }


    public Map<String, URI> getObjectMetadataToAdd() {
        return objectMetadataToAdd;
    }


    public void setObjectMetadataToAdd(Map<String, URI> objectMetadataToAdd) {
        this.objectMetadataToAdd = objectMetadataToAdd;
    }


    public Map<String, URI> getObjectMetadataToModify() {
        return objectMetadataToModify;
    }


    public void setObjectMetadataToModify(Map<String, URI> objectMetadataToModify) {
        this.objectMetadataToModify = objectMetadataToModify;
    }


    public Set<String> getObjectMetadataToRemove() {
        return objectMetadataToRemove;
    }


    public void setObjectMetadataToRemove(Set<String> objectMetadataToRemove) {
        this.objectMetadataToRemove = objectMetadataToRemove;
    }


    public MigrationInformation getMigratedFrom() {
        return migratedFrom;
    }


    public void setMigratedFrom(MigrationInformation migratedFrom) {
        this.migratedFrom = migratedFrom;
    }


    public Set<MigrationInformation> getMigratedToToAdd() {
        return migratedToToAdd;
    }


    public void setMigratedToToAdd(Set<MigrationInformation> migratedToToAdd) {
        this.migratedToToAdd = migratedToToAdd;
    }


    public Set<MigrationInformationUpdate> getMigratedToToModify() {
        return migratedToToModify;
    }


    public void setMigratedToToModify(Set<MigrationInformationUpdate> migratedToToModify) {
        this.migratedToToModify = migratedToToModify;
    }


    public Set<String> getMigratedToToRemove() {
        return migratedToToRemove;
    }


    public void setMigratedToToRemove(Set<String> migratedToToRemove) {
        this.migratedToToRemove = migratedToToRemove;
    }


    public String getMainFile() {
        return mainFile;
    }


    public void setMainFile(String mainFile) {
        this.mainFile = mainFile;
    }


    public void setMetsProvidedURI(URI metsURI) {
        this.providedMets = metsURI;
    }


    public URI getMetsProvidedURI() {
        return this.providedMets;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (deleteAllContent ? 1231 : 1237);
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((inputFilesToAdd == null) ? 0 : inputFilesToAdd.hashCode());
        result = prime * result + ((inputFilesToModify == null) ? 0 : inputFilesToModify.hashCode());
        result = prime * result + ((inputFilesToRemove == null) ? 0 : inputFilesToRemove.hashCode());
        result = prime * result + ((mainFile == null) ? 0 : mainFile.hashCode());
        result = prime * result + ((migratedFrom == null) ? 0 : migratedFrom.hashCode());
        result = prime * result + ((migratedToToAdd == null) ? 0 : migratedToToAdd.hashCode());
        result = prime * result + ((migratedToToModify == null) ? 0 : migratedToToModify.hashCode());
        result = prime * result + ((migratedToToRemove == null) ? 0 : migratedToToRemove.hashCode());
        result = prime * result + ((objectMetadataToAdd == null) ? 0 : objectMetadataToAdd.hashCode());
        result = prime * result + ((objectMetadataToModify == null) ? 0 : objectMetadataToModify.hashCode());
        result = prime * result + ((objectMetadataToRemove == null) ? 0 : objectMetadataToRemove.hashCode());
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
        if (!(obj instanceof ObjectModificationRequest)) {
            return false;
        }
        ObjectModificationRequest other = (ObjectModificationRequest) obj;
        if (deleteAllContent != other.deleteAllContent) {
            return false;
        }
        if (identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        } else if (!identifier.equals(other.identifier)) {
            return false;
        }
        if (inputFilesToAdd == null) {
            if (other.inputFilesToAdd != null) {
                return false;
            }
        } else if (!inputFilesToAdd.equals(other.inputFilesToAdd)) {
            return false;
        }
        if (inputFilesToModify == null) {
            if (other.inputFilesToModify != null) {
                return false;
            }
        } else if (!inputFilesToModify.equals(other.inputFilesToModify)) {
            return false;
        }
        if (inputFilesToRemove == null) {
            if (other.inputFilesToRemove != null) {
                return false;
            }
        } else if (!inputFilesToRemove.equals(other.inputFilesToRemove)) {
            return false;
        }
        if (mainFile == null) {
            if (other.mainFile != null) {
                return false;
            }
        } else if (!mainFile.equals(other.mainFile)) {
            return false;
        }
        if (migratedFrom == null) {
            if (other.migratedFrom != null) {
                return false;
            }
        } else if (!migratedFrom.equals(other.migratedFrom)) {
            return false;
        }
        if (migratedToToAdd == null) {
            if (other.migratedToToAdd != null) {
                return false;
            }
        } else if (!migratedToToAdd.equals(other.migratedToToAdd)) {
            return false;
        }
        if (migratedToToModify == null) {
            if (other.migratedToToModify != null) {
                return false;
            }
        } else if (!migratedToToModify.equals(other.migratedToToModify)) {
            return false;
        }
        if (migratedToToRemove == null) {
            if (other.migratedToToRemove != null) {
                return false;
            }
        } else if (!migratedToToRemove.equals(other.migratedToToRemove)) {
            return false;
        }
        if (objectMetadataToAdd == null) {
            if (other.objectMetadataToAdd != null) {
                return false;
            }
        } else if (!objectMetadataToAdd.equals(other.objectMetadataToAdd)) {
            return false;
        }
        if (objectMetadataToModify == null) {
            if (other.objectMetadataToModify != null) {
                return false;
            }
        } else if (!objectMetadataToModify.equals(other.objectMetadataToModify)) {
            return false;
        }
        if (objectMetadataToRemove == null) {
            if (other.objectMetadataToRemove != null) {
                return false;
            }
        } else if (!objectMetadataToRemove.equals(other.objectMetadataToRemove)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        FormattedToStringBuilder builder = new FormattedToStringBuilder("ObjectModificationRequest");
        builder.append("identifier", identifier).append("deleteAllContent", deleteAllContent)
                .append("inputFilesToAdd", inputFilesToAdd).append("inputFilesToModify", inputFilesToModify)
                .append("inputFilesToRemove", inputFilesToRemove).append("objectMetadataToAdd", objectMetadataToAdd)
                .append("objectMetadataToModify", objectMetadataToModify)
                .append("objectMetadataToRemove", objectMetadataToRemove).append("migratedFrom", migratedFrom)
                .append("migratedToToAdd", migratedToToAdd).append("migratedToToModify", migratedToToModify)
                .append("migratedToToRemove", migratedToToRemove).append("mainFile", mainFile);
        return builder.toString();
    }
}
