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
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder of an input file object.
 */
public class InputFileBuilder {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(InputFileBuilder.class);

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
     * Map of metadata files describing this file. Map from the URI to the name of the file (inversely than in the
     * <code>InputFile</code> class, since URI is mandatory parameter and name is not.
     */
    private Map<String, String> metadataFiles;


    /**
     * Creates an object <code>InputFile</code> based upon parameters which was earlier passed to this builder. It
     * validates whether all needed parameters are passed.
     * 
     * @return input data file
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    public InputFile build()
            throws IncompleteDataException, InvalidDataException {
        if (source == null) {
            logger.debug("Source URI for the object-relative destination path " + destination + " is missing.");
            throw new IncompleteDataException("Source URI is missing.");
        }
        URI uriSource = null;
        try {
            uriSource = new URI(source);
        } catch (URISyntaxException e) {
            logger.debug("Source URI " + source + " is invalid.", e);
            throw new InvalidDataException("Source URI " + source + " is invalid.");
        }
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
        Integer seq = null;
        if (sequence != null) {
            try {
                seq = Integer.valueOf(sequence);
            } catch (NumberFormatException e) {
                logger.debug("Incorrect sequence value: " + sequence, e);
                throw new InvalidDataException("Invalid sequence value: " + sequence);
            }
        }
        if (destination == null) {
            return new InputFile(uriSource, seq, uriMetadataFiles);
        } else {
            return new InputFile(uriSource, destination, seq, uriMetadataFiles);
        }
    }


    /**
     * Sets a source URI of the input file for this builder and returns this builder.
     * 
     * @param source
     *            source URI
     * @return updated instance of builder
     */
    public InputFileBuilder setSource(String source) {
        this.source = source;
        return this;
    }


    /**
     * Sets a object-relative destination path of the input file for this builder and returns this builder.
     * 
     * @param destination
     *            object-relative destination path
     * @return updated instance of builder
     */
    public InputFileBuilder setDestination(String destination) {
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
    public InputFileBuilder setSequence(String sequence) {
        this.sequence = sequence;
        return this;
    }


    /**
     * Adds a metadata file of the input file for this builder and returns this builder.
     * 
     * @param metadataFile
     *            metadata file URI
     * @return updated instance of builder
     */
    public InputFileBuilder addMetadataFile(String metadataFile) {
        if (this.metadataFiles == null) {
            this.metadataFiles = new HashMap<String, String>();
        }
        int limit = metadataFile.indexOf("://");
        int idx = metadataFile.indexOf('|');
        if (idx == -1 || idx > limit) {
            this.metadataFiles.put(metadataFile, null);
        } else {
            String name = metadataFile.substring(0, idx);
            this.metadataFiles.put(metadataFile.substring(idx + 1), name);
        }
        return this;
    }

}
