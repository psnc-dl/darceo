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
 * WebApplicationException returning 415 HTTP code (UnsupportedMediaType) when it is raised.
 */
public class UnsupportedMediaType extends WebApplicationException {

    /** Serial version UID. */
    private static final long serialVersionUID = -3704316242461213790L;


    /**
     * Constructs a new <code>UnsupportedMediaType</code> without any message.
     */
    public UnsupportedMediaType() {
        super(Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build());
    }


    /**
     * Constructs a new <code>UnsupportedMediaType</code> with the specified detail message.
     * 
     * @param message
     *            the detail message
     */
    public UnsupportedMediaType(String message) {
        super(Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity(message).type(MediaType.TEXT_PLAIN)
                .build());
    }

}
