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
import java.util.Date;

import pl.psnc.synat.wrdz.common.utility.FormattedToStringBuilder;
import pl.psnc.synat.wrdz.zmd.entity.types.MigrationType;

/**
 * A class representing migration information of the digital object being registered in the system.
 */
public class MigrationInformation implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 39696657547782907L;

    /**
     * Identifier of the source or derivative object.
     */
    private final String identifier;

    /**
     * Resolver for the identifier.
     */
    private String resolver;

    /**
     * Type of migration performed when creating an object.
     */
    private final MigrationType type;

    /**
     * Date and time of the migration.
     */
    private Date date;

    /**
     * Some optional info concerning the migration.
     */
    private String info;


    /**
     * Creates new instance representing migration operation performed in order to create object, given the source or
     * derivative identifier and migration type.
     * 
     * @param identifier
     *            identifier of the source or derivative object.
     * @param type
     *            type of migration performed.
     */
    public MigrationInformation(String identifier, MigrationType type) {
        this.identifier = identifier;
        this.type = type;
    }


    /**
     * Creates new instance representing migration operation performed in order to create object, given the source or
     * derivative identifier, resolver for it and migration type.
     * 
     * @param identifier
     *            identifier of the source or derivative object.
     * @param resolver
     *            resolver for the identifier.
     * @param type
     *            type of migration performed.
     */
    public MigrationInformation(String identifier, String resolver, MigrationType type) {
        this.identifier = identifier;
        this.resolver = resolver;
        this.type = type;
    }


    public String getIdentifier() {
        return identifier;
    }


    public MigrationType getType() {
        return type;
    }


    public void setResolver(String resolver) {
        this.resolver = resolver;
    }


    public String getResolver() {
        return resolver;
    }


    public void setDate(Date date) {
        this.date = date;
    }


    public Date getDate() {
        return date;
    }


    public void setInfo(String info) {
        this.info = info;
    }


    public String getInfo() {
        return info;
    }


    public boolean isEmpty() {
        return (getIdentifier() == null && getResolver() == null && type == null);
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((resolver == null) ? 0 : resolver.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((info == null) ? 0 : info.hashCode());
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
        if (!(obj instanceof MigrationInformation)) {
            return false;
        }
        MigrationInformation other = (MigrationInformation) obj;
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
        if (date == null) {
            if (other.date != null) {
                return false;
            }
        } else if (!date.equals(other.date)) {
            return false;
        }
        if (info == null) {
            if (other.info != null) {
                return false;
            }
        } else if (!info.equals(other.info)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        FormattedToStringBuilder builder = new FormattedToStringBuilder("MigrationInformation");
        builder.append("identifier", identifier).append("resolver", resolver).append("type", type).append("date", date)
                .append("info", info);
        return builder.toString();
    }

}
