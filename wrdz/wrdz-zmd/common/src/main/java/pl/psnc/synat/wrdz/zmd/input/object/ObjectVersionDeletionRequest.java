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
 * Class representing object's version deletion request data. It contains public identifier and version number of the
 * object to delete.
 */
public class ObjectVersionDeletionRequest implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -4865684613933199205L;

    /**
     * Public identifier of the object which have to be deleted.
     */
    private final String identifier;

    /**
     * Version number of the object which have to be deleted.
     */
    private final Integer version;


    /**
     * Constructs new instance based upon given identifier and version number of the object.
     * 
     * @param identifier
     *            identifier of the object
     * @param version
     *            version number
     * 
     */
    public ObjectVersionDeletionRequest(String identifier, Integer version) {
        this.identifier = identifier;
        this.version = version;
    }


    public String getIdentifier() {
        return identifier;
    }


    public Integer getVersion() {
        return version;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
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
        if (!(obj instanceof ObjectVersionDeletionRequest)) {
            return false;
        }
        ObjectVersionDeletionRequest other = (ObjectVersionDeletionRequest) obj;
        if (identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        } else if (!identifier.equals(other.identifier)) {
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
        FormattedToStringBuilder builder = new FormattedToStringBuilder("ObjectVersionDeletionRequest");
        builder.append("identifier", identifier).append("version", version);
        return builder.toString();
    }

}
