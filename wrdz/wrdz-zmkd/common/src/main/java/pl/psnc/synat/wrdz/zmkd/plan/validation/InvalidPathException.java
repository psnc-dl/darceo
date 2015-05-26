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
 * Indicates that the migration path specified in the migration plan is invalid.
 * 
 * A path is invalid if it includes unknown/non-existant services, the specified input/output formats do not match the
 * service descriptors, or if specified service parameters are not supported.
 */
public class InvalidPathException extends MigrationPlanValidationException {

    /** Serial version UID. */
    private static final long serialVersionUID = 2682965189303106647L;


    /**
     * Constructs a new exception with the given detail message.
     * 
     * @param message
     *            the detail message
     */
    public InvalidPathException(String message) {
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
    public InvalidPathException(String message, Throwable cause) {
        super(message, cause);
    }
}
