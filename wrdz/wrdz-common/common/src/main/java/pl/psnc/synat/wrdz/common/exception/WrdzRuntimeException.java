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
package pl.psnc.synat.wrdz.common.exception;

import javax.ejb.EJBException;

/**
 * The root runtime exception for the WRDZ application.
 */
public class WrdzRuntimeException extends EJBException {

    /** Serial Version UID. */
    private static final long serialVersionUID = -6019378910425654226L;


    /**
     * Constructs a new WRDZ runtime exception with the specified detail message.
     * 
     * @param message
     *            the detail message
     */
    public WrdzRuntimeException(String message) {
        super(message);
    }


    /**
     * Constructs a new WRDZ runtime exception with the specified cause.
     * 
     * @param cause
     *            the cause
     */
    public WrdzRuntimeException(Exception cause) {
        super(cause);
    }


    /**
     * Constructs a new WRDZ runtime exception with the specified detail message and cause.
     * 
     * @param message
     *            the detail message
     * @param cause
     *            the cause
     */
    public WrdzRuntimeException(String message, Exception cause) {
        super(message, cause);
    }

}
