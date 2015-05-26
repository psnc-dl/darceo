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
package pl.psnc.synat.wrdz.zmkd.plan.execution;

import pl.psnc.synat.wrdz.common.exception.WrdzException;

/**
 * Thrown when transformation failed for some reason.
 */
public class TransformationException extends WrdzException {

    /** Serial Version UID. */
    private static final long serialVersionUID = -1728805137206619041L;

    /**
     * IRI of a service which failed.
     */
    private final String serviceIri;


    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param serviceIri
     *            IRI of a service which failed
     * @param message
     *            the detail message
     */
    public TransformationException(String serviceIri, String message) {
        super(message);
        this.serviceIri = serviceIri;
    }


    /**
     * Constructs a new exception with the specified cause.
     * 
     * @param serviceIri
     *            IRI of a service which failed
     * @param cause
     *            the cause
     */
    public TransformationException(String serviceIri, Throwable cause) {
        super(cause);
        this.serviceIri = serviceIri;
    }


    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * @param serviceIri
     *            IRI of a service which failed
     * @param message
     *            the detail message
     * @param cause
     *            the cause
     */
    public TransformationException(String serviceIri, String message, Throwable cause) {
        super(message, cause);
        this.serviceIri = serviceIri;
    }


    public String getServiceIri() {
        return serviceIri;
    }

}
