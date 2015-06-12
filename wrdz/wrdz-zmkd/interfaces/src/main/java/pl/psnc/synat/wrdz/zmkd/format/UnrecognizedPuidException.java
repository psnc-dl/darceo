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
package pl.psnc.synat.wrdz.zmkd.format;

import pl.psnc.synat.wrdz.common.exception.WrdzException;

/**
 * Thrown when the passed PUID is unrecognized.
 */
public class UnrecognizedPuidException extends WrdzException {

    /** Serial version UID. */
    private static final long serialVersionUID = 8116400478125331831L;

    private final String formatPuid;


    /**
     * Constructs a new <code>UnrecognizedPuidException</code> with the specified detail message and puid.
     * 
     * @param message
     *            the detail message
     * @param formatPuid
     *            the format puid
     */
    public UnrecognizedPuidException(String message, String formatPuid) {
        super(message);
        this.formatPuid = formatPuid;
    }


    /**
     * Constructs a new <code>UnrecognizedPuidException</code> exception with the specified detail message, puid and
     * cause.
     * 
     * @param message
     *            the detail message
     * @param formatPuid
     *            the format puid
     * @param cause
     *            the cause
     */
    public UnrecognizedPuidException(String message, String formatPuid, Throwable cause) {
        super(message, cause);
        this.formatPuid = formatPuid;
    }


    public String getFormatPuid() {
        return formatPuid;
    }
}
