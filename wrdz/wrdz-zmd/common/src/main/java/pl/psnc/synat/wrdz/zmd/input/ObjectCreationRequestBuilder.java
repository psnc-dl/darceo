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

import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;

/**
 * Builder of a request for creation of a digital object.
 */
public class ObjectCreationRequestBuilder {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ObjectCreationRequestBuilder.class);

    /**
     * Proposed id part of object identifier.
     */
    private String proposedId;

    /**
     * List of desired object contents with optionally attached file-specific metadata.
     */
    private Map<String, InputFileBuilder> inputFileBuilders;

    /**
     * Object metadata provided by user. Map from the URI to the name of the file (inversely than in the
     * <code>ObjectCreationRequest</code> class, since URI is mandatory parameter and name is not.
     */
    private Map<String, String> objectMetadata;

    /**
     * Type of object.
     */
    private String objectType;

    /**
     * Builder of an information specifying object's origins.
     */
    private MigrationInformationBuilder migratedFromBuilder;

    /**
     * Map of information specifying object's derivatives.
     */
    private Map<String, MigrationInformationBuilder> migratedToBuilders;

    /**
     * Object-relative path to the main file of an object.
     */
    private String mainFile;

    /** Object name. */
    private String objectName;

    /** Object name. */
    private URI metsFileURI;


    /**
     * Constructs <code>ObjectCreationRequestBuilder</code> with default parameters.
     */
    public ObjectCreationRequestBuilder() {
        this.inputFileBuilders = new HashMap<String, InputFileBuilder>();
        this.migratedToBuilders = new HashMap<String, MigrationInformationBuilder>();
        this.objectMetadata = new HashMap<String, String>();
        this.objectType = ObjectType.MASTER.name();
        this.metsFileURI = null;
    }


    /**
     * Creates an object <code>ObjectCreationRequest</code> based upon parameters which was earlier passed to this
     * builder. It validates whether all needed parameters are given.
     * 
     * @return request for creation of a digital object.
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    public ObjectCreationRequest build()
            throws IncompleteDataException, InvalidDataException {
        Set<InputFile> inputFiles = new HashSet<InputFile>();
        for (String key : inputFileBuilders.keySet()) {
            inputFiles.add(inputFileBuilders.get(key).build());
        }
        if (inputFiles.size() == 0) {
            logger.debug("There is no data files.");
            throw new IncompleteDataException("There is no data files.");
        }
        Map<String, URI> uriObjectMetadata = parseObjectMetadata(objectMetadata);
        ObjectType type = null;
        try {
            type = ObjectType.valueOf(objectType.toUpperCase());
        } catch (Exception e) {
            logger.debug("Object type " + objectType + " is invalid.", e);
            throw new InvalidDataException("Object type " + objectType + " is invalid.");
        }
        ObjectCreationRequest request = new ObjectCreationRequest(inputFiles, uriObjectMetadata, type);
        if (migratedFromBuilder != null) {
            request.setMigratedFrom(migratedFromBuilder.build());
        }
        if (!migratedToBuilders.isEmpty()) {
            Set<MigrationInformation> migratedTo = new HashSet<MigrationInformation>();
            for (String key : migratedToBuilders.keySet()) {
                migratedTo.add(migratedToBuilders.get(key).build());
            }
            request.setMigratedTo(migratedTo);
        }
        if (mainFile != null) {
            request.setMainFile(mainFile);
        }
        if (proposedId != null) {
            request.setProposedId(proposedId);
        }
        if (objectName != null) {
            request.setName(objectName);
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
     * Sets a proposed id part of the object identifier for this builder and returns this builder.
     * 
     * @param proposedId
     *            id part of the object identifier
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder setProposedId(String proposedId) {
        this.proposedId = proposedId;
        return this;
    }


    /**
     * Sets a source URI of a input file specified by the key for this builder and returns this builder.
     * 
     * @param key
     *            key of the input file
     * @param source
     *            source of the input file
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder setInputFileSource(String key, String source) {
        InputFileBuilder builder = inputFileBuilders.get(key);
        if (builder == null) {
            builder = new InputFileBuilder();
        }
        inputFileBuilders.put(key, builder.setSource(source));
        return this;
    }


    /**
     * Sets a object-relative destination path of a input file specified by the key for this builder and returns this
     * builder.
     * 
     * @param key
     *            key of the input file
     * @param destination
     *            object-relative destination path of the input file
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder setInputFileDestination(String key, String destination) {
        InputFileBuilder builder = inputFileBuilders.get(key);
        if (builder == null) {
            builder = new InputFileBuilder();
        }
        inputFileBuilders.put(key, builder.setDestination(destination));
        return this;
    }


    /**
     * Sets the sequence property of the input file specified by the given key for this builder and returns this
     * builder.
     * 
     * @param key
     *            key of the input file
     * @param sequence
     *            sequence property, used for file ordering purposes
     * @return update builder
     */
    public ObjectCreationRequestBuilder setInputFileSequence(String key, String sequence) {
        InputFileBuilder builder = inputFileBuilders.get(key);
        if (builder == null) {
            builder = new InputFileBuilder();
        }
        inputFileBuilders.put(key, builder.setSequence(sequence));
        return this;
    }


    /**
     * Adds a metadata file of a input file specified by the key for this builder and returns this builder.
     * 
     * @param key
     *            key of the input file
     * @param metadataFile
     *            metadata file of the input file
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder addInputFileMetadataFile(String key, String metadataFile) {
        InputFileBuilder builder = inputFileBuilders.get(key);
        if (builder == null) {
            builder = new InputFileBuilder();
        }
        inputFileBuilders.put(key, builder.addMetadataFile(metadataFile));
        return this;
    }


    /**
     * Adds a metadata file of object for this builder and returns this builder.
     * 
     * @param objectMetadata
     *            metadata file of object
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder addObjectMetadata(String objectMetadata) {
        int limit = objectMetadata.indexOf("://");
        int idx = objectMetadata.indexOf('|');
        if (idx == -1 || idx > limit) {
            this.objectMetadata.put(objectMetadata, null);
        } else {
            String name = objectMetadata.substring(0, idx);
            this.objectMetadata.put(objectMetadata.substring(idx + 1), name);
        }
        return this;
    }


    /**
     * Sets a type of object for this builder and returns this builder.
     * 
     * @param objectType
     *            type of object
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder setObjectType(String objectType) {
        this.objectType = objectType;
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
    public ObjectCreationRequestBuilder setMigratedFromIdentifier(String identifier) {
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
    public ObjectCreationRequestBuilder setMigratedFromIdentifierResolver(String resolver) {
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
     *            migration's type of migration information
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder setMigratedFromMigrationType(String migrationType) {
        if (migratedFromBuilder == null) {
            migratedFromBuilder = new MigrationInformationBuilder();
        }
        migratedFromBuilder = migratedFromBuilder.setMigrationType(migrationType);
        return this;
    }


    /**
     * Sets a migration's date of migration information specifying object's origins for this builder and returns this
     * builder.
     * 
     * @param date
     *            migration's date of migration information
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder setMigratedFromMigrationDate(String date) {
        if (migratedFromBuilder == null) {
            migratedFromBuilder = new MigrationInformationBuilder();
        }
        migratedFromBuilder = migratedFromBuilder.setMigrationDate(date);
        return this;
    }


    /**
     * Sets a migration's info of migration information specifying object's origins for this builder and returns this
     * builder.
     * 
     * @param info
     *            migration's info of migration information
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder setMigratedFromMigrationInfo(String info) {
        if (migratedFromBuilder == null) {
            migratedFromBuilder = new MigrationInformationBuilder();
        }
        migratedFromBuilder = migratedFromBuilder.setMigrationInfo(info);
        return this;
    }


    /**
     * Sets a derivative identifier of migration information specifying object's derivative specified by the key for
     * this builder and returns this builder.
     * 
     * @param key
     *            key of the migration information
     * @param identifier
     *            derivative identifier of the migration information
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder setMigratedToIdentifier(String key, String identifier) {
        MigrationInformationBuilder builder = migratedToBuilders.get(key);
        if (builder == null) {
            builder = new MigrationInformationBuilder();
        }
        migratedToBuilders.put(key, builder.setIdentifier(identifier));
        return this;
    }


    /**
     * Sets a derivative identifier's resolver of migration information specifying object's derivative specified by the
     * key for this builder and returns this builder.
     * 
     * @param key
     *            key of the migration information
     * @param resolver
     *            derivative identifier's resolver of the migration information
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder setMigratedToIdentifierResolver(String key, String resolver) {
        MigrationInformationBuilder builder = migratedToBuilders.get(key);
        if (builder == null) {
            builder = new MigrationInformationBuilder();
        }
        migratedToBuilders.put(key, builder.setIdentifierResolver(resolver));
        return this;
    }


    /**
     * Sets a migration's type of migration information specifying object's derivative specified by the key for this
     * builder and returns this builder.
     * 
     * @param key
     *            key of the migration information
     * @param migrationType
     *            migration's type of the migration information
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder setMigratedToMigrationType(String key, String migrationType) {
        MigrationInformationBuilder builder = migratedToBuilders.get(key);
        if (builder == null) {
            builder = new MigrationInformationBuilder();
        }
        migratedToBuilders.put(key, builder.setMigrationType(migrationType));
        return this;
    }


    /**
     * Sets a migration's date of migration information specifying object's derivative specified by the key for this
     * builder and returns this builder.
     * 
     * @param key
     *            key of the migration information
     * @param migrationDate
     *            migration's date of the migration information
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder setMigratedToMigrationDate(String key, String migrationDate) {
        MigrationInformationBuilder builder = migratedToBuilders.get(key);
        if (builder == null) {
            builder = new MigrationInformationBuilder();
        }
        migratedToBuilders.put(key, builder.setMigrationDate(migrationDate));
        return this;
    }


    /**
     * Sets a migration's info of migration information specifying object's derivative specified by the key for this
     * builder and returns this builder.
     * 
     * @param key
     *            key of the migration information
     * @param migrationInfo
     *            migration's info of the migration information
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder setMigratedToMigrationInfo(String key, String migrationInfo) {
        MigrationInformationBuilder builder = migratedToBuilders.get(key);
        if (builder == null) {
            builder = new MigrationInformationBuilder();
        }
        migratedToBuilders.put(key, builder.setMigrationInfo(migrationInfo));
        return this;
    }


    /**
     * Sets a main file of object for this builder and returns this builder.
     * 
     * @param mainFile
     *            main file of object
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder setMainFile(String mainFile) {
        this.mainFile = mainFile;
        return this;
    }


    /**
     * Sets the object name and returns this builder.
     * 
     * @param name
     *            object name
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder setName(String name) {
        this.objectName = name;
        return this;
    }


    /**
     * Set URI to the provided METS file and returns this builder.
     * 
     * @param metsURI
     *            URI to the METS file
     * @return updated instance of builder
     */
    public ObjectCreationRequestBuilder setProvidedMetsURI(URI metsURI) {
        this.metsFileURI = metsURI;
        return this;
    }
}
