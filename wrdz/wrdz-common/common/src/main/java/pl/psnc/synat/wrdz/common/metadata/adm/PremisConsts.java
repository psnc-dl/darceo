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
package pl.psnc.synat.wrdz.common.metadata.adm;

/**
 * Constants used in the PREMIS metadata.
 */
public final class PremisConsts {

    /**
     * Private constructor.
     */
    private PremisConsts() {
    }


    /************************************************************************************************************/

    /**
     * Premis namespace prefix.
     */
    public static final String PREMIS_PREFIX = "premis";

    /**
     * Premis namespace URI.
     */
    public static final String PREMIS_NAMESPACE_URI = "info:lc/xmlns/premis-v2";

    /**
     * Premis schema location.
     */
    public static final String PREMIS_SCHEMA_LOCATION = "http://www.loc.gov/standards/premis/v2/premis-v2-1.xsd";

    /************************************************************************************************************/

    /**
     * Premis version.
     */
    public static final String PREMIS_VERSION = "2.1";

    /**
     * Premis filename identifier.
     */
    public static final String PREMIS_FILE_IDENTIFIER = "FILENAME";

    /**
     * Premis file creation event type.
     */
    public static final String PREMIS_CREATION_EVENT_TYPE = "CREATION";

    /**
     * Premis format validation event type.
     */
    public static final String PREMIS_VALIDATION_EVENT_TYPE = "VALIDATION";

}
