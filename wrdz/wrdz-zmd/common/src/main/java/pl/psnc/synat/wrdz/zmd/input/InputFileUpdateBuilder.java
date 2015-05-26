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

/**
 * Builder of an input file update.
 */
public class InputFileUpdateBuilder {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(InputFileUpdateBuilder.class);

    /**
     * Path to the resource in the remote repository.
     */
    private String source;

    /**
     * Object-relative path to the file including file name.
     */
    private String destination;

    /**
     * Sequence property, used for file ordering purposes.
     */
    private String sequence;

    /**
     * Map of metadata files describing this file (to add to the digital object). Map from the URI to the name of the
     * file (inversely than in the <code>InputFile</code> class, since URI is mandatory parameter and name is not.
     */
    private Map<String, String> metadataFilesToAdd;

    /**
     * Map of metadata files describing this file (to modify in the digital object). Map from the URI to the name of the
     * file (inversely than in the <code>InputFile</code> class, since URI is mandatory parameter and name is not.
     */
    private Map<String, String> metadataFilesToModify;

    /**
     * Set of metadata files describing this file (to remove from the digital object). Set of names.
     */
    private Set<String> metadataFilesToRemove;


    /**
     * Creates an object <code>InputFileUpdate</code> based upon parameters which was earlier passed to this builder. It
     * validates whether all needed parameters are passed.
     * 
     * @return input data file update
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    public InputFileUpdate build()
            throws IncompleteDataException, InvalidDataException {
        if (destination == null) {
            logger.debug("Destination of the object-relative destination path " + destination + " is missing.");
            throw new IncompleteDataException("Destination is missing.");
        }
        URI uriSource = null;
        if (source != null) {
            try {
                uriSource = new URI(source);
            } catch (URISyntaxException e) {
                logger.debug("Source URI " + source + " is invalid.", e);
                throw new InvalidDataException("Source URI " + source + " is invalid.");
            }
        }
        Integer seq = null;
        if (sequence != null) {
            try {
                seq = Integer.valueOf(sequence);
            } catch (NumberFormatException e) {
                logger.debug("Incorrect sequence value: " + sequence, e);
                throw new InvalidDataException("Invalid sequence value: " + sequence);
            }
        }
        Map<String, URI> uriMetadataFilesToAdd = parseMetadataFiles(metadataFilesToAdd);
        Map<String, URI> uriMetadataFilesToModify = parseMetadataFiles(metadataFilesToModify);
        return new InputFileUpdate(uriSource, destination, seq, uriMetadataFilesToAdd, uriMetadataFilesToModify,
                metadataFilesToRemove);
    }


    /**
     * Parses metadata files and returns map with names and URI of them.
     * 
     * @param metadataFiles
     *            map of metadata files
     * @return parsed map of metadata files
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    private Map<String, URI> parseMetadataFiles(Map<String, String> metadataFiles)
            throws IncompleteDataException, InvalidDataException {
        Map<String, URI> uriMetadataFiles = null;
        if (metadataFiles != null) {
            uriMetadataFiles = new HashMap<String, URI>();
            for (String metadata : metadataFiles.keySet()) {
                URI uriMetadata = null;
                try {
                    uriMetadata = new URI(metadata);
                } catch (URISyntaxException e) {
                    logger.debug("Object metadata URI " + metadata + " is invalid.", e);
                    throw new InvalidDataException("Object metadata URI " + metadata + " is invalid.");
                }
                String name = metadataFiles.get(metadata);
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
                uriMetadataFiles.put(name, uriMetadata);
            }
        }
        return uriMetadataFiles;
    }


    /**
     * Sets a source URI of the input file update for this builder and returns this builder.
     * 
     * @param source
     *            source URI
     * @return updated instance of builder
     */
    public InputFileUpdateBuilder setSource(String source) {
        this.source = source;
        return this;
    }


    /**
     * Sets a object-relative destination path of the input file update for this builder and returns this builder.
     * 
     * @param destination
     *            object-relative destination path
     * @return updated instance of builder
     */
    public InputFileUpdateBuilder setDestination(String destination) {
        this.destination = destination;
        return this;
    }


    /**
     * Sets the sequence property value for this builder and returns this builder.
     * 
     * @param sequence
     *            value of the sequence property
     * @return updated instance of builder
     */
    public InputFileUpdateBuilder setSequence(String sequence) {
        this.sequence = sequence;
        return this;
    }


    /**
     * Adds a metadata file to add of the input file upadte for this builder and returns this builder.
     * 
     * @param metadataFileToAdd
     *            metadata file URI to add
     * @return updated instance of builder
     */
    public InputFileUpdateBuilder addMetadataFileToAdd(String metadataFileToAdd) {
        this.metadataFilesToAdd = addMetadataFile(this.metadataFilesToAdd, metadataFileToAdd);
        return this;
    }


    /**
     * Adds a metadata file to modify of the input file upadte for this builder and returns this builder.
     * 
     * @param metadataFileToModify
     *            metadata file URI to modify
     * @return updated instance of builder
     */
    public InputFileUpdateBuilder addMetadataFileToModify(String metadataFileToModify) {
        this.metadataFilesToModify = addMetadataFile(this.metadataFilesToModify, metadataFileToModify);
        return this;
    }


    /**
     * Adds a metadata file to remove of the input file upadte for this builder and returns this builder.
     * 
     * @param metadataFileToRemove
     *            metadata file URI to remove
     * @return updated instance of builder
     */
    public InputFileUpdateBuilder addMetadataFileToRemove(String metadataFileToRemove) {
        if (this.metadataFilesToRemove == null) {
            this.metadataFilesToRemove = new HashSet<String>();
        }
        this.metadataFilesToRemove.add(metadataFileToRemove);
        return this;
    }


    /**
     * Adds a metadata file to the corresponding map of metadata files.
     * 
     * @param metadataFiles
     *            map of metadata files
     * @param metadataFile
     *            metadata file
     * @return updated map of metadata files
     */
    private Map<String, String> addMetadataFile(Map<String, String> metadataFiles, String metadataFile) {
        if (metadataFiles == null) {
            metadataFiles = new HashMap<String, String>();
        }
        int limit = metadataFile.indexOf("://");
        int idx = metadataFile.indexOf('|');
        if (idx == -1 || idx > limit) {
            metadataFiles.put(metadataFile, null);
        } else {
            String name = metadataFile.substring(0, idx);
            metadataFiles.put(metadataFile.substring(idx + 1), name);
        }
        return metadataFiles;
    }

}
