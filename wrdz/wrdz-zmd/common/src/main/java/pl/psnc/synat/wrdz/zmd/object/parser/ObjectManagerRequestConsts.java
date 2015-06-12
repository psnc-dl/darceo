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
package pl.psnc.synat.wrdz.zmd.object.parser;

/**
 * Constants used by parsers of requests concerning object management functionality.
 */
public final class ObjectManagerRequestConsts {

    /**
     * Private constructor.
     */
    private ObjectManagerRequestConsts() {
    }


    /************************************************************************************************************/

    /**
     * Prefix of a parameter specifying the proposed id part of a digital object identifier - for methods which accept
     * requests for creating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_PROPOSED_ID_PREFIX = "proposed-id";

    /**
     * Prefix of parameters specifying files of a digital object - for methods which accept requests for creating these
     * objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_FILE_PREFIX = "file";

    /**
     * Prefix of parameters specifying files to add to the digital object - for methods which accept requests for
     * updating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_ADD_FILE_PREFIX = "add-file";

    /**
     * Prefix of parameters specifying files to modify in the digital object - for methods which accept requests for
     * updating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_MOD_FILE_PREFIX = "mod-file";

    /**
     * Prefix of parameters specifying files to remove from the digital object - for methods which accept requests for
     * updating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_DEL_FILE_PREFIX = "del-file";

    /**
     * Suffix of parameters specifying source files of a digital object - for methods which accept requests for creating
     * and updating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_FILE_SRC_SUFFIX = "src";

    /**
     * Suffix of parameters specifying object-relative path for files of a digital object - for methods which accept
     * requests for creating and updating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_FILE_DEST_SUFFIX = "dest";

    /**
     * Suffix of parameters specifying the file sequence property - for methods which accept requests for creating and
     * updating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_FILE_SEQ_SUFFIX = "seq";

    /**
     * Suffix of parameters specifying metadata files for files of a digital object - for methods which accept requests
     * for creating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_FILE_METADATA_SUFFIX = "md";

    /**
     * Suffix of parameters specifying metadata files to add to the digital object for files of a digital object - for
     * methods which accept requests for updating these objects (for parameters encoded by form-data and
     * x-www-form-urlencoded).
     */
    static final String REQUEST_MOD_FILE_METADATA_ADD_SUFFIX = "md-add";

    /**
     * Suffix of parameters specifying metadata files to modify in the digital object for files of a digital object -
     * for methods which accept requests for updating these objects (for parameters encoded by form-data and
     * x-www-form-urlencoded).
     */
    static final String REQUEST_MOD_FILE_METADATA_MOD_SUFFIX = "md-mod";

    /**
     * Suffix of parameters specifying metadata files to remove from the digital object for files of a digital object -
     * for methods which accept requests for updating these objects (for parameters encoded by form-data and
     * x-www-form-urlencoded).
     */
    static final String REQUEST_MOD_FILE_METADATA_DEL_SUFFIX = "md-del";

    /**
     * Prefix of a parameter specifying metadata files of a digital object concerning whole digital object - for methods
     * which accept requests for creating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_METADATA_PREFIX = "metadata";

    /**
     * Prefix of a parameter specifying metadata files to add to the digital object concerning whole digital object -
     * for methods which accept requests for updating these objects (for parameters encoded by form-data and
     * x-www-form-urlencoded).
     */
    static final String REQUEST_ADD_METADATA_PREFIX = "add-metadata";

    /**
     * Prefix of a parameter specifying metadata files to modify in the digital object concerning whole digital object -
     * for methods which accept requests for updating these objects (for parameters encoded by form-data and
     * x-www-form-urlencoded).
     */
    static final String REQUEST_MOD_METADATA_PREFIX = "mod-metadata";

    /**
     * Prefix of a parameter specifying metadata files to remove from the digital object concerning whole digital object
     * - for methods which accept requests for updating these objects (for parameters encoded by form-data and
     * x-www-form-urlencoded).
     */
    static final String REQUEST_DEL_METADATA_PREFIX = "del-metadata";

    /**
     * Prefix of a parameter specifying type of a digital object - for methods which accept requests for creating these
     * objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_OBJECT_TYPE_PREFIX = "object-type";

    /**
     * Prefix of parameters specifying an origin of digital object - for methods which accept requests for creating and
     * updating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_ORIGIN_PREFIX = "origin";

    /**
     * Suffix of parameters specifying an identifier of the source object - for methods which accept requests for
     * creating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_ORIGIN_ID_SUFFIX = "id";

    /**
     * Suffix of parameters specifying a resolver of an identifier of the source object - for methods which accept
     * requests for creating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_ORIGIN_ID_RESOLVER_SUFFIX = "id-resolver";

    /**
     * Suffix of parameters specifying the type of origin digital object - for methods which accept requests for
     * creating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_ORIGIN_TYPE_SUFFIX = "type";

    /**
     * Suffix of parameters specifying the date the migration from an origin digital object - for methods which accept
     * requests for creating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_ORIGIN_DATE_SUFFIX = "date";

    /**
     * Suffix of parameters specifying some info concerning the migration from an origin digital object - for methods
     * which accept requests for creating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_ORIGIN_INFO_SUFFIX = "info";

    /**
     * Prefix of parameters specifying a derivative of digital object - for methods which accept requests for creating
     * these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_DERIVATIVE_PREFIX = "derivative";

    /**
     * Prefix of parameters specifying a derivative of digital object to add it to him - for methods which accept
     * requests for updating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_ADD_DERIVATIVE_PREFIX = "add-derivative";

    /**
     * Prefix of parameters specifying a derivative of digital object to modify it in him - for methods which accept
     * requests for updating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_MOD_DERIVATIVE_PREFIX = "mod-derivative";

    /**
     * Prefix of parameters specifying a derivative of digital object to remove it from him - for methods which accept
     * requests for updating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_DEL_DERIVATIVE_PREFIX = "del-derivative";

    /**
     * Suffix of parameters specifying an identifier of the derivative object - for methods which accept requests for
     * creating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_DERIVATIVE_ID_SUFFIX = "id";

    /**
     * Suffix of parameters specifying a resolver of an identifier of the derivative object - for methods which accept
     * requests for creating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_DERIVATIVE_ID_RESOLVER_SUFFIX = "id-resolver";

    /**
     * Suffix of parameters specifying the type of derivative digital object - for methods which accept requests for
     * creating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_DERIVATIVE_TYPE_SUFFIX = "type";

    /**
     * Suffix of parameters specifying the date the migration to a derivative digital object - for methods which accept
     * requests for creating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_DERIVATIVE_DATE_SUFFIX = "date";

    /**
     * Suffix of parameters specifying some info concerning the migration to a derivative digital object - for methods
     * which accept requests for creating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_DERIVATIVE_INFO_SUFFIX = "info";

    /**
     * Prefix of a parameter specifying the main file of a digital object - for methods which accept requests for
     * creating these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_MAIN_FILE_PREFIX = "main-file";

    /**
     * Prefix of a parameter specifying the name of a digital object - for methods which accept requests for creating
     * these objects (for parameters encoded by form-data and x-www-form-urlencoded).
     */
    static final String REQUEST_NAME_PREFIX = "name";

}
