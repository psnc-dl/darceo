﻿/**
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
 * WebApplicationException returning 503 HTTP code (Service Unavailable) when it is raised.
 */
public class ServiceUnavailableException extends WebApplicationException {

    /** Serial version UID. */
    private static final long serialVersionUID = -3245467917694790166L;


    /**
     * Constructs a new <code>ServiceUnavailableException</code> without any message.
     */
    public ServiceUnavailableException() {
        super(Response.status(Response.Status.SERVICE_UNAVAILABLE).build());
    }


    /**
     * Constructs a new <code>ServiceUnavailableException</code> with the specified detail message.
     * 
     * @param message
     *            the detail message
     */
    public ServiceUnavailableException(String message) {
        super(Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(message).type(MediaType.TEXT_PLAIN).build());
    }

}
