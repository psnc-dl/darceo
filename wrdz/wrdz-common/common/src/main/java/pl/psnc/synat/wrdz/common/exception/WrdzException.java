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

/**
 * The root exception for the WRDZ application.
 */
public class WrdzException extends Exception {

    /** Serial Version UID. */
    private static final long serialVersionUID = -2296038721405206675L;


    /**
     * Constructs a new WRDZ exception with the specified detail message.
     * 
     * @param message
     *            the detail message
     */
    public WrdzException(String message) {
        super(message);
    }


    /**
     * Constructs a new WRDZ exception with the specified cause.
     * 
     * @param cause
     *            the cause
     */
    public WrdzException(Throwable cause) {
        super(cause);
    }


    /**
     * Constructs a new WRDZ exception with the specified detail message and cause.
     * 
     * @param message
     *            the detail message
     * @param cause
     *            the cause
     */
    public WrdzException(String message, Throwable cause) {
        super(message, cause);
    }

}
