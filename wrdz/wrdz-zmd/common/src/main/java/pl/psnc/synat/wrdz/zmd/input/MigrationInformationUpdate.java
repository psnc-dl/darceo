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

import pl.psnc.synat.wrdz.common.utility.FormattedToStringBuilder;

/**
 * A class representing migration information which have to be modified in the digital object.
 */
public class MigrationInformationUpdate implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -1827661810053885777L;

    /**
     * Identifier of the source or derivative object.
     */
    private final String identifier;

    /**
     * New resolver for the identifier.
     */
    private final String resolver;


    /**
     * Creates new instance with a given the source or derivative identifier, and new resolver.
     * 
     * @param identifier
     *            identifier of the source or derivative object.
     * @param resolver
     *            resolver for the identifier.
     */
    public MigrationInformationUpdate(String identifier, String resolver) {
        this.identifier = identifier;
        this.resolver = resolver;
    }


    public String getIdentifier() {
        return identifier;
    }


    public boolean isEmpty() {
        return (getIdentifier() == null && getResolver() == null);
    }


    public String getResolver() {
        return resolver;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((resolver == null) ? 0 : resolver.hashCode());
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
        if (!(obj instanceof MigrationInformationUpdate)) {
            return false;
        }
        MigrationInformationUpdate other = (MigrationInformationUpdate) obj;
        if (identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        } else if (!identifier.equals(other.identifier)) {
            return false;
        }
        if (resolver == null) {
            if (other.resolver != null) {
                return false;
            }
        } else if (!resolver.equals(other.resolver)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        FormattedToStringBuilder builder = new FormattedToStringBuilder("MigrationInformationUpdate");
        builder.append("identifier", identifier).append("resolver", resolver);
        return builder.toString();
    }

}
