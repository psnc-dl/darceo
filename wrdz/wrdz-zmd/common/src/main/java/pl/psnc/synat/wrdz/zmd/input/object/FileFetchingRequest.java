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
import java.util.ArrayList;
import java.util.List;

import pl.psnc.synat.wrdz.common.utility.FormattedToStringBuilder;

/**
 * Represents request to fetch a list of object's files.
 */
public class FileFetchingRequest implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -603238815522218539L;

    /**
     * Target object's identifier.
     */
    private final String identifier;

    /**
     * Target object's version.
     */
    private Integer version;

    /**
     * Specifies whether or not files extracted metadata should be included in the request.
     */
    private boolean extracted;

    /**
     * Specifies whether or not files provided metadata should be included in the request.
     */
    private boolean provided;

    /**
     * List of object's files to fetch.
     */
    private final List<String> files = new ArrayList<String>();


    /**
     * Constructs new file fetching request for .
     * 
     * @param identifier
     *            target object's identifier.
     */
    public FileFetchingRequest(String identifier) {
        this.identifier = identifier;
    }


    /**
     * Constructs new file fetching request for .
     * 
     * @param identifier
     *            target object's identifier.
     * @param files
     *            list of files to fetch from the file store.
     * 
     * @throws NullPointerException
     *             if files list is <code>null</code>.
     */
    public FileFetchingRequest(String identifier, List<String> files)
            throws NullPointerException {
        this.identifier = identifier;
        this.files.addAll(files);
    }


    public Integer getVersion() {
        return version;
    }


    public void setVersion(Integer version) {
        this.version = version;
    }


    public boolean isExtracted() {
        return extracted;
    }


    public void setExtracted(boolean extracted) {
        this.extracted = extracted;
    }


    public boolean isProvided() {
        return provided;
    }


    public void setProvided(boolean provided) {
        this.provided = provided;
    }


    public String getIdentifier() {
        return identifier;
    }


    public List<String> getFiles() {
        return files;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (extracted ? 1231 : 1237);
        result = prime * result + ((files == null) ? 0 : files.hashCode());
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + (provided ? 1231 : 1237);
        result = prime * result + ((version == null) ? 0 : version.hashCode());
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
        if (!(obj instanceof FileFetchingRequest)) {
            return false;
        }
        FileFetchingRequest other = (FileFetchingRequest) obj;
        if (extracted != other.extracted) {
            return false;
        }
        if (files == null) {
            if (other.files != null) {
                return false;
            }
        } else if (!files.equals(other.files)) {
            return false;
        }
        if (identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        } else if (!identifier.equals(other.identifier)) {
            return false;
        }
        if (provided != other.provided) {
            return false;
        }
        if (version == null) {
            if (other.version != null) {
                return false;
            }
        } else if (!version.equals(other.version)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        FormattedToStringBuilder builder = new FormattedToStringBuilder("FileFetchingRequest");
        builder.append("identifier", identifier).append("version", version).append("extracted", extracted)
                .append("provided", provided).append("files", files);
        return builder.toString();
    }

}
