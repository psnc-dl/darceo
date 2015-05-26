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
package pl.psnc.synat.wrdz.ru.exceptions;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;

/**
 * Exception thrown when specified operation on the services registry is not allowed.
 */
public class IllegalRegistryOperationException extends WrdzRuntimeException {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -961092075009598183L;


    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message
     *            the detailed message
     */
    public IllegalRegistryOperationException(String message) {
        super(message);
    }


    /**
     * Constructs a new exception with the specified cause.
     * 
     * @param cause
     *            the {@link Exception} that caused this exception.
     */
    public IllegalRegistryOperationException(Exception cause) {
        super(cause);
    }


    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * @param message
     *            the detailed message
     * @param cause
     *            the {@link Exception} that caused this exception.
     */
    public IllegalRegistryOperationException(String message, Exception cause) {
        super(message, cause);
    }

}
