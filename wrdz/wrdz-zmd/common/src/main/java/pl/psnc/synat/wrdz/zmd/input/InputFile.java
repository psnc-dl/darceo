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

import java.io.Serializable;
import java.net.URI;
import java.util.Map;

import pl.psnc.synat.wrdz.common.utility.FormattedToStringBuilder;

/**
 * A class representing file handle to resource in remote repository.
 */
public class InputFile implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 1904530299555233848L;

    /**
     * Path to the resource in the remote repository.
     */
    private final URI source;

    /**
     * Object-relative path to the file including file name.
     */
    private final String destination;

    /**
     * Sequence property. Used for file ordering.
     */
    private final Integer sequence;

    /**
     * Map of metadata files describing this file (name and uri).
     */
    private final Map<String, URI> metadataFilesToAdd;


    /**
     * Constructs new instance with a given source URI and the destination object-relative file path set to the object
     * root, filename being the name stated in the URI path part. File won't have any associated user-provided metadata.
     * 
     * @param source
     *            address of the resource in the remote repository.
     */
    public InputFile(URI source) {
        this.source = source;
        String[] path = source.getPath().split("\\Q/\\E");
        this.destination = path[path.length - 1];
        this.sequence = null;
        this.metadataFilesToAdd = null;
    }


    /**
     * Constructs new instance with a given source URI and the destination. File won't have any associated user-provided
     * metadata.
     * 
     * @param source
     *            address of the resource in the remote repository.
     * @param destination
     *            object-relative path to the file including file name.
     */
    public InputFile(URI source, String destination) {
        this.source = source;
        this.destination = destination;
        this.sequence = null;
        this.metadataFilesToAdd = null;
    }


    /**
     * Constructs new instance with a given source URI and the destination object-relative file path set to the object
     * root, filename being the name stated in the URI path part. File will have associated user-provided metadata.
     * 
     * @param source
     *            address of the resource in the remote repository.
     * @param sequence
     *            sequence value, used for file ordering purposes
     * @param metadataFilesToAdd
     *            list of associated user-provided metadata files describing this file.
     */
    public InputFile(URI source, Integer sequence, Map<String, URI> metadataFilesToAdd) {
        this.source = source;
        String[] path = source.getPath().split("\\Q/\\E");
        this.destination = path[path.length - 1];
        this.sequence = sequence;
        this.metadataFilesToAdd = metadataFilesToAdd;
    }


    /**
     * Constructs new instance with a given source URI and the destination. File will have associated user-provided
     * metadata.
     * 
     * @param source
     *            address of the resource in the remote repository.
     * @param destination
     *            object-relative path to the file including file name.
     * @param sequence
     *            sequence value, used for file ordering purposes
     * @param metadataFilesToAdd
     *            list of associated user-provided metadata files describing this file.
     */
    public InputFile(URI source, String destination, Integer sequence, Map<String, URI> metadataFilesToAdd) {
        this.source = source;
        this.destination = destination;
        this.sequence = sequence;
        this.metadataFilesToAdd = metadataFilesToAdd;
    }


    public URI getSource() {
        return source;
    }


    public String getDestination() {
        return destination;
    }


    public Integer getSequence() {
        return sequence;
    }


    public Map<String, URI> getMetadataFilesToAdd() {
        return metadataFilesToAdd;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((destination == null) ? 0 : destination.hashCode());
        result = prime * result + ((metadataFilesToAdd == null) ? 0 : metadataFilesToAdd.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
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
        if (!(obj instanceof InputFile)) {
            return false;
        }
        InputFile other = (InputFile) obj;
        if (destination == null) {
            if (other.destination != null) {
                return false;
            }
        } else if (!destination.equals(other.destination)) {
            return false;
        }
        if (sequence == null) {
            if (other.sequence != null) {
                return false;
            }
        } else if (!sequence.equals(other.sequence)) {
            return false;
        }
        if (metadataFilesToAdd == null) {
            if (other.metadataFilesToAdd != null) {
                return false;
            }
        } else if (!metadataFilesToAdd.equals(other.metadataFilesToAdd)) {
            return false;
        }
        if (source == null) {
            if (other.source != null) {
                return false;
            }
        } else if (!source.equals(other.source)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        FormattedToStringBuilder builder = new FormattedToStringBuilder("InputFile");
        builder.append("source", source).append("destination", destination)
                .append("metadataFilesToAdd", metadataFilesToAdd);
        return builder.toString();
    }

}
