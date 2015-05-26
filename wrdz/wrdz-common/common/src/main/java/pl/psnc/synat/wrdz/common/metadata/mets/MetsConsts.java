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
package pl.psnc.synat.wrdz.common.metadata.mets;

/**
 * Constants used in the METS metadata.
 */
public final class MetsConsts {

    /**
     * Private constructor.
     */
    private MetsConsts() {
    }


    /************************************************************************************************************/

    /**
     * Mets namespace prefix.
     */
    public static final String METS_PREFIX = "mets";

    /**
     * Mets namespace URI.
     */
    public static final String METS_NAMESPACE_URI = "http://www.loc.gov/METS/";

    /**
     * Mets schema location.
     */
    public static final String METS_SCHEMA_LOCATION = "http://www.loc.gov/standards/mets/mets.xsd";

    /************************************************************************************************************/

    /**
     * Mets version.
     */
    public static final String METS_VERSION = "1.9";

    /**
     * Object version prefix for record status in the header.
     */
    public static final String METS_OBJECT_VERSION_STATUS = "VERSION ";

    /**
     * Type of location of metadata in mdRef element.
     */
    public static final String METS_METADATA_LOCATION_TYPE_RELATIVE = "RELATIVE";

    /**
     * Type of unrecognized metadata type.
     */
    public static final String METS_METADATA_TYPE_OTHER = "OTHER";

    /**
     * Type of location of data file in FLocat element.
     */
    public static final String METS_DATA_LOCATION_TYPE_RELATIVE = "RELATIVE";

    /**
     * Premis relationship <em>derivation</em> type.
     */
    public static final String PREMIS_RELATIONSHIP_DERIVATION_TYPE = "derivation";

    /**
     * Premis <em>has source</em> subtype of relationship <em>derivation</em> type.
     */
    public static final String PREMIS_RELATIONSHIP_HAS_SOURCE_SUBTYPE = "has source";

    /**
     * Premis <em>is source of</em> subtype of relationship <em>derivation</em> type.
     */
    public static final String PREMIS_RELATIONSHIP_IS_SOURCE_OF_SUBTYPE = "is source of";

    /**
     * Premis object creation event type.
     */
    public static final String PREMIS_CREATION_EVENT_TYPE = "CREATION";

    /**
     * Premis object modification event type.
     */
    public static final String PREMIS_MODIFICATION_EVENT_TYPE = "MODIFICATION";

    /**
     * Premis event type.
     */
    public static final String PREMIS_EVENT_TYPE = "evt";

}
