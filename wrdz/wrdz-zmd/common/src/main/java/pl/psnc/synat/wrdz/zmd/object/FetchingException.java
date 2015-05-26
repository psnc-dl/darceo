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
package pl.psnc.synat.wrdz.zmd.object;

import javax.ejb.ApplicationException;

import pl.psnc.synat.wrdz.common.exception.WrdzException;

/**
 * Thrown when creation of a digital object failed.
 */
@ApplicationException(rollback = true)
public class FetchingException extends WrdzException {

    /** Serial version UID. */
    private static final long serialVersionUID = 1734119672944540269L;


    /**
     * Constructs a new <code>ObjectFetchingException</code> with the specified detail message.
     * 
     * @param message
     *            the detail message
     */
    public FetchingException(String message) {
        super(message);
    }


    /**
     * Constructs a new <code>ObjectFetchingException</code> with the specified cause.
     * 
     * @param cause
     *            exception being the cause of this exception.
     */
    public FetchingException(Throwable cause) {
        super(cause);
    }


    /**
     * Constructs a new <code>ObjectFetchingException</code> with the specified detail message and cause.
     * 
     * @param message
     *            the detail message
     * @param cause
     *            exception being the cause of this exception.
     */
    public FetchingException(String message, Throwable cause) {
        super(message, cause);
    }

}
