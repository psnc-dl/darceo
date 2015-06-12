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

import pl.psnc.synat.wrdz.common.utility.FormattedToStringBuilder;

/**
 * Class representing object fetching request data. It contains information about target object identifier, version and
 * whether or not to include provided and/or extracted metadata.
 */
public class ObjectFetchingRequest implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 7424682403253887911L;

    /**
     * Object's identifier.
     */
    private final String identifier;

    /**
     * Object's version.
     */
    private final Integer version;

    /**
     * Defines whether or not to include provided metadata.
     */
    private final Boolean provided;

    /**
     * Defines whether or not to include extracted metadata.
     */
    private final Boolean extracted;


    /**
     * Creates new instance of this class.
     * 
     * @param identifier
     *            object's identifier.
     * @param version
     *            object's version.
     * @param provided
     *            defines whether or not to include provided metadata.
     * @param extracted
     *            defines whether or not to include extracted metadata.
     */
    public ObjectFetchingRequest(String identifier, Integer version, Boolean provided, Boolean extracted) {
        this.identifier = identifier;
        this.version = version;
        this.provided = provided;
        this.extracted = extracted;
    }


    public String getIdentifier() {
        return identifier;
    }


    public Integer getVersion() {
        return version;
    }


    public Boolean getProvided() {
        return provided;
    }


    public Boolean getExtracted() {
        return extracted;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((extracted == null) ? 0 : extracted.hashCode());
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((provided == null) ? 0 : provided.hashCode());
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
        if (!(obj instanceof ObjectFetchingRequest)) {
            return false;
        }
        ObjectFetchingRequest other = (ObjectFetchingRequest) obj;
        if (extracted == null) {
            if (other.extracted != null) {
                return false;
            }
        } else if (!extracted.equals(other.extracted)) {
            return false;
        }
        if (identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        } else if (!identifier.equals(other.identifier)) {
            return false;
        }
        if (provided == null) {
            if (other.provided != null) {
                return false;
            }
        } else if (!provided.equals(other.provided)) {
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
        FormattedToStringBuilder builder = new FormattedToStringBuilder("ObjectFetchingRequest");
        builder.append("identifier", identifier).append("version", version).append("provided", provided)
                .append("extracted=", extracted);
        return builder.toString();
    }

}
