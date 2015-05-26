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
package pl.psnc.synat.wrdz.common.entity.async;

/**
 * Constants of http codes and content types of responses to asynchronous requests.
 */
public final class AsyncRequestResultConsts {

    /**
     * Private constructor.
     */
    private AsyncRequestResultConsts() {
    }


    /************************************************************************************************************/

    /**
     * HTTP code 200: OK.
     */
    public static final Integer HTTP_CODE_OK = 200;

    /**
     * HTTP code 403: Not Found.
     */
    public static final Integer HTTP_CODE_FORBIDDEN = 403;

    /**
     * HTTP code 404: Not Found.
     */
    public static final Integer HTTP_CODE_NOT_FOUND = 404;

    /**
     * HTTP code 500: Internal Server Error.
     */
    public static final Integer HTTP_CODE_INTERNAL_SERVER_ERROR = 500;

    /************************************************************************************************************/

    /**
     * Content type: text/plain.
     */
    public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

    /**
     * Content type: application/xml.
     */
    public static final String CONTENT_TYPE_APPLICATION_XML = "application/xml";

    /**
     * Content type: application/json.
     */
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";

    /**
     * Content type: application/zip.
     */
    public static final String CONTENT_TYPE_APPLICATION_ZIP = "application/zip";

    /**
     * Content type: application/octet-stream.
     */
    public static final String CONTENT_TYPE_APPLICATION_OCTET_STREAM = "application/octet-stream";

    /************************************************************************************************************/

    /**
     * Content type charset value: UTF-8.
     */
    public static final String CONTENT_TYPE_CHARSET_UTF8 = "; charset=utf-8";

}
