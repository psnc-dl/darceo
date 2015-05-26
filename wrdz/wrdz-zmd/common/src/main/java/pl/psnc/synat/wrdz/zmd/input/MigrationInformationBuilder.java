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

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.zmd.entity.types.MigrationType;

/**
 * Builder of an information about migration.
 */
public class MigrationInformationBuilder {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(MigrationInformationBuilder.class);

    /**
     * Date formatter compatible with the xsd:dateTime format.
     */
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Identifier of the source or derivative object.
     */
    private String identifier;

    /**
     * Identifier's resolver of the source or derivative object.
     */
    private String resolver;

    /**
     * Type of migration performed when creating an object.
     */
    private String migrationType;

    /**
     * Date and time of the migration.
     */
    private String date;

    /**
     * Some optional info concerning the migration.
     */
    private String info;


    /**
     * Creates an object <code>MigrationInformation</code> based upon parameters which was earlier passed to this
     * builder. It validates whether all needed parameters are passed.
     * 
     * @return information about migration
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    public MigrationInformation build()
            throws IncompleteDataException, InvalidDataException {
        if (identifier == null) {
            logger.debug("Identifier for the migration type " + migrationType + " is missing.");
            throw new IncompleteDataException("Source identifier is missing.");
        }
        if (migrationType == null) {
            logger.debug("Migration type for the identifier " + identifier + " is missing.");
            throw new IncompleteDataException("Migration type is missing.");
        }
        MigrationType type = null;
        try {
            type = MigrationType.valueOf(migrationType.toUpperCase());
        } catch (Exception e) {
            logger.debug("Migration type " + migrationType + " is invalid.", e);
            throw new InvalidDataException("Migration type " + migrationType + " is invalid.");
        }
        MigrationInformation migrationInformation = new MigrationInformation(identifier, resolver, type);
        if (date != null) {
            try {
                migrationInformation.setDate(DATE_FORMATTER.parse(date));
            } catch (ParseException e) {
                logger.debug("Migration date " + date + " is invalid.", e);
                throw new InvalidDataException("Migration date " + date
                        + " is invalid. The system requires this date in the format" + DATE_FORMATTER.toPattern());
            }
        }
        if (info != null && info.length() > 0) {
            migrationInformation.setInfo(info);
        }
        return migrationInformation;
    }


    /**
     * Sets a source or derivative object identifier of the migration information for this builder and returns this
     * builder.
     * 
     * @param identifier
     *            source or derivative identifier
     * @return updated instance of builder
     */
    public MigrationInformationBuilder setIdentifier(String identifier) {
        this.identifier = identifier;
        return this;
    }


    /**
     * Sets a source or derivative object identifier's resolver of the migration information for this builder and
     * returns this builder.
     * 
     * @param resolver
     *            source or derivative identifier's resolver
     * @return updated instance of builder
     */
    public MigrationInformationBuilder setIdentifierResolver(String resolver) {
        this.resolver = resolver;
        return this;
    }


    /**
     * Sets a migration's type of the migration information for this builder and returns this builder.
     * 
     * @param migrationType
     *            migration's type
     * @return updated instance of builder
     */
    public MigrationInformationBuilder setMigrationType(String migrationType) {
        this.migrationType = migrationType;
        return this;
    }


    /**
     * Sets a migration's date of the migration information for this builder and returns this builder.
     * 
     * @param date
     *            migration's date
     * @return updated instance of builder
     */
    public MigrationInformationBuilder setMigrationDate(String date) {
        this.date = date;
        return this;
    }


    /**
     * Sets a migration's info of the migration information for this builder and returns this builder.
     * 
     * @param info
     *            migration's info
     * @return updated instance of builder
     */
    public MigrationInformationBuilder setMigrationInfo(String info) {
        this.info = info;
        return this;
    }

}
