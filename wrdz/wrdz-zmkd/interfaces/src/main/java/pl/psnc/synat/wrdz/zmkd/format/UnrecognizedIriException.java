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
 * Thrown when the passed UDFR IRI is not recognized.
 */
public class UnrecognizedIriException extends WrdzException {

    /** Serial version UID. */
    private static final long serialVersionUID = 3755301560525656838L;

    private final String formatIri;


    /**
     * Constructs a new <code>UnrecognizedPuidException</code> with the specified detail message and IRI.
     * 
     * @param message
     *            the detail message
     * @param formatIri
     *            the format IRI
     */
    public UnrecognizedIriException(String message, String formatIri) {
        super(message);
        this.formatIri = formatIri;
    }


    /**
     * Constructs a new <code>UnrecognizedPuidException</code> exception with the specified detail message, IRI and
     * cause.
     * 
     * @param message
     *            the detail message
     * @param formatIri
     *            the format IRI
     * @param cause
     *            the cause
     */
    public UnrecognizedIriException(String message, String formatIri, Throwable cause) {
        super(message, cause);
        this.formatIri = formatIri;
    }


    public String getFormatIri() {
        return formatIri;
    }
}
