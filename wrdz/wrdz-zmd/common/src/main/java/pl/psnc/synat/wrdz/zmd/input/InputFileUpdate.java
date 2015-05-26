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
import java.util.Map;
import java.util.Set;

import pl.psnc.synat.wrdz.common.utility.FormattedToStringBuilder;

/**
 * A class representing file which have to be modified in the digital object. It also carries information about
 * modification of associated metadata.
 */
public class InputFileUpdate extends InputFile {

    /** Serial version UID. */
    private static final long serialVersionUID = 4071720451940569892L;

    /**
     * Map of metadata files which have to be modified (name and uri).
     */
    private final Map<String, URI> metadataFilesToModify;

    /**
     * Set of metadata files which have to be removed (names).
     */
    private final Set<String> metadataFilesToRemove;


    /**
     * Constructs new instance with a given destination and lists of operations on metadata.
     * 
     * @param destination
     *            object-relative path to the file including file name
     * @param sequence
     *            sequence value, used for file ordering purposes
     * @param metadataFilesToAdd
     *            list of associated user-provided metadata files to add
     * @param metadataFilesToModify
     *            list of associated user-provided metadata files to modify
     * @param metadataFilesToRemove
     *            list of associated user-provided metadata files to remove
     */
    public InputFileUpdate(String destination, Integer sequence, Map<String, URI> metadataFilesToAdd,
            Map<String, URI> metadataFilesToModify, Set<String> metadataFilesToRemove) {
        super(null, destination, sequence, metadataFilesToAdd);
        this.metadataFilesToModify = metadataFilesToModify;
        this.metadataFilesToRemove = metadataFilesToRemove;
    }


    /**
     * Constructs new instance with a given destination, source URI and lists of operations on metadata..
     * 
     * @param source
     *            address of the resource in the remote repository
     * @param destination
     *            object-relative path to the file including file name
     * @param sequence
     *            sequence value, used for file ordering purposes
     * @param metadataFilesToAdd
     *            list of associated user-provided metadata files to add
     * @param metadataFilesToModify
     *            list of associated user-provided metadata files to modify
     * @param metadataFilesToRemove
     *            list of associated user-provided metadata files to remove
     */
    public InputFileUpdate(URI source, String destination, Integer sequence, Map<String, URI> metadataFilesToAdd,
            Map<String, URI> metadataFilesToModify, Set<String> metadataFilesToRemove) {
        super(source, destination, sequence, metadataFilesToAdd);
        this.metadataFilesToModify = metadataFilesToModify;
        this.metadataFilesToRemove = metadataFilesToRemove;
    }


    public Map<String, URI> getMetadataFilesToModify() {
        return metadataFilesToModify;
    }


    public Set<String> getMetadataFilesToRemove() {
        return metadataFilesToRemove;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((metadataFilesToModify == null) ? 0 : metadataFilesToModify.hashCode());
        result = prime * result + ((metadataFilesToRemove == null) ? 0 : metadataFilesToRemove.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof InputFileUpdate)) {
            return false;
        }
        InputFileUpdate other = (InputFileUpdate) obj;
        if (metadataFilesToModify == null) {
            if (other.metadataFilesToModify != null) {
                return false;
            }
        } else if (!metadataFilesToModify.equals(other.metadataFilesToModify)) {
            return false;
        }
        if (metadataFilesToRemove == null) {
            if (other.metadataFilesToRemove != null) {
                return false;
            }
        } else if (!metadataFilesToRemove.equals(other.metadataFilesToRemove)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        FormattedToStringBuilder builder = new FormattedToStringBuilder("InputFileUpdate");
        builder.append("source", getSource()).append("destination", getDestination())
                .append("metadataFilesToModify", metadataFilesToModify)
                .append("metadataFilesToRemove", metadataFilesToRemove);
        return builder.toString();
    }

}
