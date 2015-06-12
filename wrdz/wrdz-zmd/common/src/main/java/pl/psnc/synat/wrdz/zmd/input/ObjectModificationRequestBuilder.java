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
package pl.psnc.synat.wrdz.zmd.input;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;

/**
 * Builder of a request for modification of a digital object.
 */
public class ObjectModificationRequestBuilder {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ObjectModificationRequestBuilder.class);

    /**
     * Identifier of the object.
     */
    private String identifier;

    /**
     * Whether delete all content of the object before modification.
     */
    private boolean deleteAllContent;

    /**
     * List of data files with optionally attached file-specific metadata which have to be added to the object.
     */
    private Map<String, InputFileBuilder> inputFileToAddBuilders;

    /**
     * List of data files with optionally attached file-specific metadata which have to be modified in the object.
     */
    private Map<String, InputFileUpdateBuilder> inputFileToModifyBuilders;

    /**
     * List of data files names which have to be removed from the object.
     */
    private Set<String> inputFilesToRemove;

    /**
     * List of object metadata which have to be added to the object.
     */
    private Map<String, String> objectMetadataToAdd;

    /**
     * List of object metadata which have to be modified in the object.
     */
    private Map<String, String> objectMetadataToModify;

    /**
     * Set of object metadata which have to be removed from the object.
     */
    private Set<String> objectMetadataToRemove;

    /**
     * Builder of an information specifying object's origins.
     */
    private MigrationInformationBuilder migratedFromBuilder;

    /**
     * Map of information specifying object's derivatives to add to the object.
     */
    private Map<String, MigrationInformationBuilder> migratedToToAddBuilders;

    /**
     * Map of information specifying object's derivatives to modify in the object.
     */
    private Map<String, MigrationInformationUpdateBuilder> migratedToToModifyBuilders;

    /**
     * Set of information specifying object's derivatives to remove from the object.
     */
    private Set<String> migratedToToRemove;

    /**
     * Object-relative path to the main file of an object.
     */
    private String mainFile;

    /** UTI to temporary METS file. */
    private URI metsFileURI;


    /**
     * Constructs <code>ObjectModificationRequestBuilder</code> with passed identifier.
     * 
     * @param identifier
     *            identifier
     */
    public ObjectModificationRequestBuilder(String identifier) {
        this.identifier = identifier;
        this.deleteAllContent = false;
        this.inputFileToAddBuilders = new HashMap<String, InputFileBuilder>();
        this.inputFileToModifyBuilders = new HashMap<String, InputFileUpdateBuilder>();
        this.inputFilesToRemove = new HashSet<String>();
        this.objectMetadataToAdd = new HashMap<String, String>();
        this.objectMetadataToModify = new HashMap<String, String>();
        this.objectMetadataToRemove = new HashSet<String>();
        this.migratedToToAddBuilders = new HashMap<String, MigrationInformationBuilder>();
        this.migratedToToModifyBuilders = new HashMap<String, MigrationInformationUpdateBuilder>();
        this.migratedToToRemove = new HashSet<String>();
    }


    /**
     * Creates an object <code>ObjectModificationRequest</code> based upon parameters which was earlier passed to this
     * builder. It validates whether all needed parameters are given.
     * 
     * @return request for modification of a digital object.
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    public ObjectModificationRequest build()
            throws IncompleteDataException, InvalidDataException {
        if (identifier == null) {
            logger.debug("Identifier of the object is missing.");
            throw new IncompleteDataException("Identifier of the object is missing.");
        }
        ObjectModificationRequest request = new ObjectModificationRequest(identifier);
        request.setDeleteAllContent(deleteAllContent);
        Set<InputFile> inputFilesToAdd = new HashSet<InputFile>();
        for (String key : inputFileToAddBuilders.keySet()) {
            inputFilesToAdd.add(inputFileToAddBuilders.get(key).build());
        }
        request.setInputFilesToAdd(inputFilesToAdd);
        Set<InputFileUpdate> inputFilesToModify = new HashSet<InputFileUpdate>();
        for (String key : inputFileToModifyBuilders.keySet()) {
            inputFilesToModify.add(inputFileToModifyBuilders.get(key).build());
        }
        request.setInputFilesToModify(inputFilesToModify);
        request.setInputFilesToRemove(inputFilesToRemove);
        request.setObjectMetadataToAdd(parseObjectMetadata(objectMetadataToAdd));
        request.setObjectMetadataToModify(parseObjectMetadata(objectMetadataToModify));
        request.setObjectMetadataToRemove(objectMetadataToRemove);
        if (migratedFromBuilder != null) {
            request.setMigratedFrom(migratedFromBuilder.build());
        }
        if (!migratedToToAddBuilders.isEmpty()) {
            Set<MigrationInformation> migratedToToAdd = new HashSet<MigrationInformation>();
            for (String key : migratedToToAddBuilders.keySet()) {
                migratedToToAdd.add(migratedToToAddBuilders.get(key).build());
            }
            request.setMigratedToToAdd(migratedToToAdd);
        }
        if (!migratedToToModifyBuilders.isEmpty()) {
            Set<MigrationInformationUpdate> migratedToToModify = new HashSet<MigrationInformationUpdate>();
            for (String key : migratedToToModifyBuilders.keySet()) {
                migratedToToModify.add(migratedToToModifyBuilders.get(key).build());
            }
            request.setMigratedToToModify(migratedToToModify);
        }
        if (mainFile != null) {
            request.setMainFile(mainFile);
        }
        // check if the request is empty
        if (!deleteAllContent) {
            if (inputFileToAddBuilders.isEmpty() && inputFileToModifyBuilders.isEmpty() && inputFilesToRemove.isEmpty()
                    && objectMetadataToAdd.isEmpty() && objectMetadataToModify.isEmpty()
                    && objectMetadataToRemove.isEmpty() && migratedFromBuilder == null
                    && migratedToToAddBuilders.isEmpty() && migratedToToModifyBuilders.isEmpty()
                    && migratedToToRemove.isEmpty() && mainFile == null) {
                logger.debug("Modification request is empty.");
                throw new IncompleteDataException("Modification request is empty.");
            }
        }

        if (metsFileURI != null) {
            request.setMetsProvidedURI(metsFileURI);
        }
        return request;
    }


    /**
     * Parses metadata files and returns map with names and URI of them.
     * 
     * @param objectMetadata
     *            map of metadata files
     * @return parsed map of metadata files
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    private Map<String, URI> parseObjectMetadata(Map<String, String> objectMetadata)
            throws IncompleteDataException, InvalidDataException {
        Map<String, URI> uriObjectMetadata = new HashMap<String, URI>();
        for (String metadata : objectMetadata.keySet()) {
            URI uriMetadata = null;
            try {
                uriMetadata = new URI(metadata);
            } catch (URISyntaxException e) {
                logger.debug("Object metadata URI " + metadata + " is invalid.", e);
                throw new InvalidDataException("Object metadata URI " + metadata + " is invalid.");
            }
            String name = objectMetadata.get(metadata);
            if (name == null) {
                name = uriMetadata.getRawPath();
                if (name != null) {
                    name = FilenameUtils.getName(name);
                }
            }
            if (name == null || name.isEmpty()) {
                logger.debug("Name for the metadata " + metadata + " is missing.");
                throw new IncompleteDataException("Name for the metadata " + metadata + " is missing.");
            }
            uriObjectMetadata.put(name, uriMetadata);
        }
        return uriObjectMetadata;
    }


    /**
     * Sets a delete all content flag and returns this builder.
     * 
     * @param deleteAllContent
     *            delete all content flag
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder setDeleteAllContent(boolean deleteAllContent) {
        this.deleteAllContent = deleteAllContent;
        return this;
    }


    /**
     * Sets a source URI of a input file (to add to the object) specified by the key for this builder and returns this
     * builder.
     * 
     * @param key
     *            key of the input file
     * @param source
     *            source of the input file
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder setInputFileToAddSource(String key, String source) {
        InputFileBuilder builder = inputFileToAddBuilders.get(key);
        if (builder == null) {
            builder = new InputFileBuilder();
        }
        inputFileToAddBuilders.put(key, builder.setSource(source));
        return this;
    }


    /**
     * Sets a object-relative destination path of a input file (to add to the object) specified by the key for this
     * builder and returns this builder.
     * 
     * @param key
     *            key of the input file
     * @param destination
     *            object-relative destination path of the input file
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder setInputFileToAddDestination(String key, String destination) {
        InputFileBuilder builder = inputFileToAddBuilders.get(key);
        if (builder == null) {
            builder = new InputFileBuilder();
        }
        inputFileToAddBuilders.put(key, builder.setDestination(destination));
        return this;
    }


    /**
     * Sets the sequence property of the input file (to be added to the object) specified by the given key for this
     * builder and returns this builder.
     * 
     * @param key
     *            key of the input file
     * @param sequence
     *            sequence property, used for file ordering purposes
     * @return update builder
     */
    public ObjectModificationRequestBuilder setInputFileToAddSequence(String key, String sequence) {
        InputFileBuilder builder = inputFileToAddBuilders.get(key);
        if (builder == null) {
            builder = new InputFileBuilder();
        }
        inputFileToAddBuilders.put(key, builder.setSequence(sequence));
        return this;
    }


    /**
     * Adds a metadata file of a input file (to add to the object) specified by the key for this builder and returns
     * this builder.
     * 
     * @param key
     *            key of the input file
     * @param metadataFile
     *            metadata file of the input file
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder addInputFileToAddMetadataFile(String key, String metadataFile) {
        InputFileBuilder builder = inputFileToAddBuilders.get(key);
        if (builder == null) {
            builder = new InputFileBuilder();
        }
        inputFileToAddBuilders.put(key, builder.addMetadataFile(metadataFile));
        return this;
    }


    /**
     * Sets a source URI of a input file (to modify in the object) specified by the key for this builder and returns
     * this builder.
     * 
     * @param key
     *            key of the input file
     * @param source
     *            source of the input file
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder setInputFileToModifySource(String key, String source) {
        InputFileUpdateBuilder builder = inputFileToModifyBuilders.get(key);
        if (builder == null) {
            builder = new InputFileUpdateBuilder();
        }
        inputFileToModifyBuilders.put(key, builder.setSource(source));
        return this;
    }


    /**
     * Sets a object-relative destination path of a input file (to modify in the object) specified by the key for this
     * builder and returns this builder.
     * 
     * @param key
     *            key of the input file
     * @param destination
     *            object-relative destination path of the input file
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder setInputFileToModifyDestination(String key, String destination) {
        InputFileUpdateBuilder builder = inputFileToModifyBuilders.get(key);
        if (builder == null) {
            builder = new InputFileUpdateBuilder();
        }
        inputFileToModifyBuilders.put(key, builder.setDestination(destination));
        return this;
    }


    /**
     * Sets the sequence property of the input file (to be modified in the object) specified by the given key for this
     * builder and returns this builder.
     * 
     * @param key
     *            key of the input file
     * @param sequence
     *            sequence property, used for file ordering purposes
     * @return update builder
     */
    public ObjectModificationRequestBuilder setInputFileToModifySequence(String key, String sequence) {
        InputFileUpdateBuilder builder = inputFileToModifyBuilders.get(key);
        if (builder == null) {
            builder = new InputFileUpdateBuilder();
        }
        inputFileToModifyBuilders.put(key, builder.setSequence(sequence));
        return this;
    }


    /**
     * Adds a metadata file (to add) of a input file (to modify in the object) specified by the key for this builder and
     * returns this builder.
     * 
     * @param key
     *            key of the input file
     * @param metadataFile
     *            metadata file of the input file
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder addInputFileToModifyMetadataFileToAdd(String key, String metadataFile) {
        InputFileUpdateBuilder builder = inputFileToModifyBuilders.get(key);
        if (builder == null) {
            builder = new InputFileUpdateBuilder();
        }
        inputFileToModifyBuilders.put(key, builder.addMetadataFileToAdd(metadataFile));
        return this;
    }


    /**
     * Adds a metadata file (to modify) of a input file (to modify in the object) specified by the key for this builder
     * and returns this builder.
     * 
     * @param key
     *            key of the input file
     * @param metadataFile
     *            metadata file of the input file
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder addInputFileToModifyMetadataFileToModify(String key, String metadataFile) {
        InputFileUpdateBuilder builder = inputFileToModifyBuilders.get(key);
        if (builder == null) {
            builder = new InputFileUpdateBuilder();
        }
        inputFileToModifyBuilders.put(key, builder.addMetadataFileToModify(metadataFile));
        return this;
    }


    /**
     * Adds a metadata file (to remove) of a input file (to modify in the object) specified by the key for this builder
     * and returns this builder.
     * 
     * @param key
     *            key of the input file
     * @param metadataFile
     *            metadata file of the input file
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder addInputFileToModifyMetadataFileToRemove(String key, String metadataFile) {
        InputFileUpdateBuilder builder = inputFileToModifyBuilders.get(key);
        if (builder == null) {
            builder = new InputFileUpdateBuilder();
        }
        inputFileToModifyBuilders.put(key, builder.addMetadataFileToRemove(metadataFile));
        return this;
    }


    /**
     * Sets a object-relative destination path of a input file (to remove from the object) for this builder and returns
     * this builder.
     * 
     * @param destination
     *            object-relative destination path of the input file
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder addInputFileToRemoveDestination(String destination) {
        inputFilesToRemove.add(destination);
        return this;
    }


    /**
     * Adds a metadata file of object (to add to the object) for this builder and returns this builder.
     * 
     * @param objectMetadata
     *            metadata file of object
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder addObjectMetadataToAdd(String objectMetadata) {
        int limit = objectMetadata.indexOf("://");
        int idx = objectMetadata.indexOf('|');
        if (idx == -1 || idx > limit) {
            this.objectMetadataToAdd.put(objectMetadata, null);
        } else {
            String name = objectMetadata.substring(0, idx);
            this.objectMetadataToAdd.put(objectMetadata.substring(idx + 1), name);
        }
        return this;
    }


    /**
     * Adds a metadata file of object (to modify in the object) for this builder and returns this builder.
     * 
     * @param objectMetadata
     *            metadata file of object
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder addObjectMetadataToModify(String objectMetadata) {
        int limit = objectMetadata.indexOf("://");
        int idx = objectMetadata.indexOf('|');
        if (idx == -1 || idx > limit) {
            this.objectMetadataToModify.put(objectMetadata, null);
        } else {
            String name = objectMetadata.substring(0, idx);
            this.objectMetadataToModify.put(objectMetadata.substring(idx + 1), name);
        }
        return this;
    }


    /**
     * Adds a metadata file of object (to remove from the object) for this builder and returns this builder.
     * 
     * @param objectMetadata
     *            metadata file of object
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder addObjectMetadataToRemove(String objectMetadata) {
        this.objectMetadataToRemove.add(objectMetadata);
        return this;
    }


    /**
     * Sets a source identifier of migration information specifying object's origins for this builder and returns this
     * builder.
     * 
     * @param identifier
     *            source identifier of migration information
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder setMigratedFromIdentifier(String identifier) {
        if (migratedFromBuilder == null) {
            migratedFromBuilder = new MigrationInformationBuilder();
        }
        migratedFromBuilder = migratedFromBuilder.setIdentifier(identifier);
        return this;
    }


    /**
     * Sets a source identifier's resolver of migration information specifying object's origins for this builder and
     * returns this builder.
     * 
     * @param resolver
     *            source identifier's resolver of migration information
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder setMigratedFromIdentifierResolver(String resolver) {
        if (migratedFromBuilder == null) {
            migratedFromBuilder = new MigrationInformationBuilder();
        }
        migratedFromBuilder = migratedFromBuilder.setIdentifierResolver(resolver);
        return this;
    }


    /**
     * Sets a migration's type of migration information specifying object's origins for this builder and returns this
     * builder.
     * 
     * @param migrationType
     *            migration's type of the migration information
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder setMigratedFromMigrationType(String migrationType) {
        if (migratedFromBuilder == null) {
            migratedFromBuilder = new MigrationInformationBuilder();
        }
        migratedFromBuilder = migratedFromBuilder.setMigrationType(migrationType);
        return this;
    }


    /**
     * Sets a derivative identifier of migration information specifying object's derivative (to add to the object)
     * specified by the key for this builder and returns this builder.
     * 
     * @param key
     *            key of the migration information
     * @param identifier
     *            derivative identifier of the migration information
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder setMigratedToToAddIdentifier(String key, String identifier) {
        MigrationInformationBuilder builder = migratedToToAddBuilders.get(key);
        if (builder == null) {
            builder = new MigrationInformationBuilder();
        }
        migratedToToAddBuilders.put(key, builder.setIdentifier(identifier));
        return this;
    }


    /**
     * Sets a derivative identifier's resolver of migration information specifying object's derivative (to add to the
     * object) specified by the key for this builder and returns this builder.
     * 
     * @param key
     *            key of the migration information
     * @param resolver
     *            derivative identifier's resolver of the migration information
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder setMigratedToToAddIdentifierResolver(String key, String resolver) {
        MigrationInformationBuilder builder = migratedToToAddBuilders.get(key);
        if (builder == null) {
            builder = new MigrationInformationBuilder();
        }
        migratedToToAddBuilders.put(key, builder.setIdentifierResolver(resolver));
        return this;
    }


    /**
     * Sets a migration's type of migration information specifying object's derivative (to add to the object specified
     * by the key for this builder and returns this builder.
     * 
     * @param key
     *            key of the migration information
     * @param migrationType
     *            migration's type of the migration information
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder setMigratedToToAddMigrationType(String key, String migrationType) {
        MigrationInformationBuilder builder = migratedToToAddBuilders.get(key);
        if (builder == null) {
            builder = new MigrationInformationBuilder();
        }
        migratedToToAddBuilders.put(key, builder.setMigrationType(migrationType));
        return this;
    }


    /**
     * Sets a derivative identifier of migration information specifying object's derivative (to modify in the object)
     * specified by the key for this builder and returns this builder.
     * 
     * @param key
     *            key of the migration information
     * @param identifier
     *            derivative identifier of the migration information
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder setMigratedToToModifyIdentifier(String key, String identifier) {
        MigrationInformationUpdateBuilder builder = migratedToToModifyBuilders.get(key);
        if (builder == null) {
            builder = new MigrationInformationUpdateBuilder();
        }
        migratedToToModifyBuilders.put(key, builder.setIdentifier(identifier));
        return this;
    }


    /**
     * Sets a derivative identifier's resolver of migration information specifying object's derivative (to modify in the
     * object) specified by the key for this builder and returns this builder.
     * 
     * @param key
     *            key of the migration information
     * @param resolver
     *            derivative identifier's resolver of the migration information
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder setMigratedToToModifyIdentifierResolver(String key, String resolver) {
        MigrationInformationUpdateBuilder builder = migratedToToModifyBuilders.get(key);
        if (builder == null) {
            builder = new MigrationInformationUpdateBuilder();
        }
        migratedToToModifyBuilders.put(key, builder.setIdentifierResolver(resolver));
        return this;
    }


    /**
     * Sets a derivative identifier of migration information specifying object's derivative (to remove from the object)
     * for this builder and returns this builder.
     * 
     * @param identifier
     *            derivative identifier of the migration information
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder addMigratedToToRemoveIdentifier(String identifier) {
        migratedToToRemove.add(identifier);
        return this;
    }


    /**
     * Sets a main file of object for this builder and returns this builder.
     * 
     * @param mainFile
     *            main file of object
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder setMainFile(String mainFile) {
        if (mainFile == null) {
            this.mainFile = "";
        } else {
            this.mainFile = mainFile;
        }
        return this;
    }


    /**
     * Set URI to the provided METS file and returns this builder.
     * 
     * @param metsURI
     *            URI to the METS file
     * @return updated instance of builder
     */
    public ObjectModificationRequestBuilder setProvidedMetsURI(URI metsURI) {
        this.metsFileURI = metsURI;
        return this;
    }

}
