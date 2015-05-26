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
 * WebApplicationException returning 500 HTTP code (Internal Server Error) when it is raised.
 */
public class InternalServerErrorException extends WebApplicationException {

    /** Serial version UID. */
    private static final long serialVersionUID = 6005655030848031446L;


    /**
     * Constructs a new <code>InternalServerErrorException</code> without any message.
     */
    public InternalServerErrorException() {
        super(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
    }


    /**
     * Constructs a new <code>InternalServerErrorException</code> with the specified detail message.
     * 
     * @param message
     *            the detail message
     */
    public InternalServerErrorException(String message) {
        super(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).type(MediaType.TEXT_PLAIN).build());
    }

}
