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
package pl.psnc.synat.wrdz.zmkd.plan;

import javax.ejb.ApplicationException;

import pl.psnc.synat.wrdz.common.exception.WrdzException;

/**
 * Exception thrown when some problem during the migration process occurred.
 * 
 */
@ApplicationException
public class MigrationProcessingException extends WrdzException {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -7312480449554640038L;

    /**
     * Id of plan to which the error affects.
     */
    private final long planId;


    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param planId
     *            Id of plan to which the error affects
     * @param message
     *            the detailed message
     */
    public MigrationProcessingException(long planId, String message) {
        super(message);
        this.planId = planId;
    }


    /**
     * Constructs a new exception with the specified cause.
     * 
     * @param planId
     *            Id of plan to which the error affects
     * @param cause
     *            the {@link Exception} that caused this exception.
     */
    public MigrationProcessingException(long planId, Exception cause) {
        super(cause);
        this.planId = planId;
    }


    /**
     * Constructs a new exception with the specified detail message and cause.
     * 
     * @param planId
     *            Id of plan to which the error affects
     * @param message
     *            the detailed message
     * @param cause
     *            the {@link Exception} that caused this exception.
     */
    public MigrationProcessingException(long planId, String message, Exception cause) {
        super(message, cause);
        this.planId = planId;
    }


    public long getPlanId() {
        return planId;
    }

}
