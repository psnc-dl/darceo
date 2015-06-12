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
 * Indicates that the specified file format puid is invalid, ie. it could not be found in the UDFR database.
 */
public class InvalidFormatException extends MigrationPlanValidationException {

    /** Serial version UID. */
    private static final long serialVersionUID = -4303380924124122348L;

    /** Invalid format puid. */
    private final String puid;


    /**
     * Constructs a new exception with the given format puid.
     * 
     * @param puid
     *            the invalid file format puid
     */
    public InvalidFormatException(String puid) {
        super("Unknown format: " + puid);
        this.puid = puid;
    }


    /**
     * Constructs a new exception with the given format puid and cause.
     * 
     * @param formatPuid
     *            the invalid file format puid
     * @param cause
     *            the throwable that caused this exception to be thrown
     */
    public InvalidFormatException(String formatPuid, Throwable cause) {
        super("Unknown format: " + formatPuid, cause);
        this.puid = formatPuid;
    }


    public String getPuid() {
        return puid;
    }
}
