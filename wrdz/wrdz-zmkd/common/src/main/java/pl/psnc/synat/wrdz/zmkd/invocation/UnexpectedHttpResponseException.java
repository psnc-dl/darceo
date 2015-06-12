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
package pl.psnc.synat.wrdz.zmkd.invocation;

import pl.psnc.synat.wrdz.common.exception.WrdzException;

/**
 * Thrown when retrieved HTTP response is different than this described in OWL-S and WADL.
 */
public class UnexpectedHttpResponseException extends WrdzException {

    /** Serial Version UID. */
    private static final long serialVersionUID = 2901572217203501771L;

    /** HTTP status code of the response. */
    private final int statusCode;

    /** Content type of the response. */
    private final String contentType;


    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message
     *            the detail message
     * @param statusCode
     *            HTTP status code
     * @param contentType
     *            content type
     */
    public UnexpectedHttpResponseException(String message, int statusCode, String contentType) {
        super(message);
        this.statusCode = statusCode;
        this.contentType = contentType;
    }


    /**
     * Constructs a new exception with the specified cause.
     * 
     * @param cause
     *            the cause
     * @param statusCode
     *            HTTP status code
     * @param contentType
     *            content type
     */
    public UnexpectedHttpResponseException(Throwable cause, int statusCode, String contentType) {
        super(cause);
        this.statusCode = statusCode;
        this.contentType = contentType;
    }


    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * @param message
     *            the detail message
     * @param cause
     *            the cause
     * @param statusCode
     *            HTTP status code
     * @param contentType
     *            content type
     */
    public UnexpectedHttpResponseException(String message, Throwable cause, int statusCode, String contentType) {
        super(message, cause);
        this.statusCode = statusCode;
        this.contentType = contentType;
    }


    public int getStatusCode() {
        return statusCode;
    }


    public String getContentType() {
        return contentType;
    }

}
