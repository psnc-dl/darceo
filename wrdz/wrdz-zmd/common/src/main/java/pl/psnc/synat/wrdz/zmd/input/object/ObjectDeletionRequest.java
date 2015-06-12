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
 * Class representing object deletion request data. It contains public identifier of the object to delete.
 */
public class ObjectDeletionRequest implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -1662601621474090010L;

    /**
     * Public identifier of the object which have to be deleted.
     */
    private final String identifier;


    /**
     * Constructs new instance based upon given identifier of the object.
     * 
     * @param identifier
     *            identifier of the object
     */
    public ObjectDeletionRequest(String identifier) {
        this.identifier = identifier;
    }


    public String getIdentifier() {
        return identifier;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
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
        if (!(obj instanceof ObjectDeletionRequest)) {
            return false;
        }
        ObjectDeletionRequest other = (ObjectDeletionRequest) obj;
        if (identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        } else if (!identifier.equals(other.identifier)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        FormattedToStringBuilder builder = new FormattedToStringBuilder("ObjectDeletionRequest");
        builder.append("identifier", identifier);
        return builder.toString();
    }

}
