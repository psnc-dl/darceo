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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Detailed information about a particular execution outcome.
 */
public class ExecutionOutcome {

    /** HTTP status code. */
    private Integer statusCode;

    /** Content. */
    private InputStream content;

    /** Content length. */
    private Long contentLength;

    /** Content type. */
    private String contentType;

    /** Map of headers. */
    private Map<String, String> headers;


    /**
     * Constructor for outcome not supposing any file.
     * 
     */
    public ExecutionOutcome() {
        headers = new HashMap<String, String>();
    }


    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }


    public Integer getStatusCode() {
        return statusCode;
    }


    public InputStream getContent() {
        return content;
    }


    public void setContent(InputStream content) {
        this.content = content;
    }


    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }


    public Long getContentLength() {
        return contentLength;
    }


    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    public String getContentType() {
        return contentType;
    }


    /**
     * Adds a header.
     * 
     * @param name
     *            name
     * @param value
     *            value
     */
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }


    /**
     * Gets a header value.
     * 
     * @param name
     *            name
     * @return value
     */
    public String getHeader(String name) {
        return headers.get(name);
    }

}
