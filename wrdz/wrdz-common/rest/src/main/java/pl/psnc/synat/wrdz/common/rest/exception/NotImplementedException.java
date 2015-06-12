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
package pl.psnc.synat.wrdz.common.rest.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * WebApplicationException returning 501 HTTP code (Not Implemented) when it is raised.
 */
public class NotImplementedException extends WebApplicationException {

    /** Serial version UID. */
    private static final long serialVersionUID = 5964373393293498081L;

    /**
     * HTTP code for the NotImplemented status.
     */
    private static final int STATUS_NOT_IMPLEMENETED = 501;


    /**
     * Constructs a new <code>NotImplementedException</code> without any message.
     */
    public NotImplementedException() {
        super(Response.status(STATUS_NOT_IMPLEMENETED).build());
    }


    /**
     * Constructs a new <code>NotImplementedException</code> with the specified detail message.
     * 
     * @param message
     *            the detail message
     */
    public NotImplementedException(String message) {
        super(Response.status(STATUS_NOT_IMPLEMENETED).entity(message).type(MediaType.TEXT_PLAIN).build());
    }

}
