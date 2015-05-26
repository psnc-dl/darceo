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
package pl.psnc.synat.wrdz.zmkd.plan.validation;

/**
 * Indicates that no valid migration path from the input to the output format could be constructed from known services.
 */
public class NoPathException extends MigrationPlanValidationException {

    /** Serial version UID. */
    private static final long serialVersionUID = -3246881158460326476L;


    /**
     * Constructs a new exception with the given detail message.
     * 
     * @param message
     *            the detail message
     */
    public NoPathException(String message) {
        super(message);
    }


    /**
     * Constructs a new exception with the given detail message and cause.
     * 
     * @param message
     *            the detail message
     * @param cause
     *            the throwable that caused this exception to be thrown
     */
    public NoPathException(String message, Throwable cause) {
        super(message, cause);
    }
}
