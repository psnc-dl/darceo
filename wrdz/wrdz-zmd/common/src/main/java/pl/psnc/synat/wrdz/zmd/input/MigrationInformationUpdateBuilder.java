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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder of an information about migration update.
 */
public class MigrationInformationUpdateBuilder {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(MigrationInformationUpdateBuilder.class);

    /**
     * Identifier of the source or derivative object.
     */
    private String identifier;

    /**
     * Identifier's resolver of the source or derivative object.
     */
    private String resolver;


    /**
     * Creates an object <code>MigrationInformationUpdate</code> based upon parameters which was earlier passed to this
     * builder. It validates whether all needed parameters are passed.
     * 
     * @return information about migration
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    public MigrationInformationUpdate build()
            throws IncompleteDataException, InvalidDataException {
        if (identifier == null) {
            logger.debug("Identifier for the resolver " + resolver + " is missing.");
            throw new IncompleteDataException("Source identifier is missing.");
        }
        return new MigrationInformationUpdate(identifier, resolver);
    }


    /**
     * Sets a source or derivative object identifier of the migration information for this builder and returns this
     * builder.
     * 
     * @param identifier
     *            source or derivative identifier
     * @return updated instance of builder
     */
    public MigrationInformationUpdateBuilder setIdentifier(String identifier) {
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
    public MigrationInformationUpdateBuilder setIdentifierResolver(String resolver) {
        this.resolver = resolver;
        return this;
    }

}
