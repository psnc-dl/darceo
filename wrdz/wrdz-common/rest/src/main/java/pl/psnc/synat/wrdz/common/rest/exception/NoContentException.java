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
import javax.ws.rs.core.Response;

/**
 * WebApplicationException returning 204 HTTP code (No Content) when it is raised.
 */
public class NoContentException extends WebApplicationException {

    /** Serial version UID. */
    private static final long serialVersionUID = 2184910174526383076L;


    /**
     * Constructs a new <code>NoContentException</code> without any message.
     */
    public NoContentException() {
        super(Response.status(Response.Status.NO_CONTENT).build());
    }

}
