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
package pl.psnc.synat.wrdz.common.async;

/**
 * Constants used in the functionality of asynchronous request processing.
 */
public final class AsyncRequestServiceConsts {

    /**
     * Private constructor.
     */
    private AsyncRequestServiceConsts() {
    }


    /************************************************************************************************************/

    /**
     * Relative path URI for the status of the asynchronous requests.
     */
    public static final String ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI_STATUS = "status/";

    /**
     * Relative path URI for the result of the asynchronous requests.
     */
    public static final String ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI_RESULT = "result/";

    /************************************************************************************************************/

    /**
     * Http header: Location.
     */
    public static final String HTTP_HEADER_LOCATION = "Location";

    /**
     * Http header: Content-Disposition.
     */
    public static final String HTTP_HEADER_CONTENT_DISPOSITION = "Content-Disposition";

    /**
     * Value for Content-Disposition http header: inline;.
     */
    public static final String HTTP_HEADER_CONTENT_DISPOSITION_INLINE = "inline; ";

    /**
     * Prefix of value for Content-Disposition http header: filename=.
     */
    public static final String HTTP_HEADER_CONTENT_DISPOSITION_FILENAME = "filename=";

    /**
     * Http header: Content-Length.
     */
    public static final String HTTP_HEADER_CONTENT_LENGTH = "Content-Length";

}
