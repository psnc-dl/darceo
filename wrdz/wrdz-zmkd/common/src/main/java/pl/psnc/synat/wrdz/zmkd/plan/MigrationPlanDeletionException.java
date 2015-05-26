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

import pl.psnc.synat.wrdz.common.exception.WrdzException;

/**
 * Thrown when there was a problem with deleting migration plan.
 */
public class MigrationPlanDeletionException extends WrdzException {

    /** Serial version UID. */
    private static final long serialVersionUID = -6345211721415205895L;


    /**
     * Message id depicts why plan was not deleted.
     */
    public enum MessageId {

        /**
         * Message id: default message id.
         */
        DEFAULT,

        /**
         * Message id: migration plan not found.
         */
        NOT_FOUND,

        /**
         * Message id: unable to delete because of status.
         */
        STATUS_UNABLE_TO_DELETE;
    }


    /**
     * Id of exception message - can be used to localize.
     */
    private MessageId messageId;


    /**
     * Default constructor which construct exception with default message id.
     * 
     * @param message
     *            exception message
     */
    public MigrationPlanDeletionException(String message) {
        super(message);
        this.messageId = MessageId.DEFAULT;
    }


    /**
     * Creates exception with specified message.
     * 
     * @param message
     *            exception message
     * @param messageId
     *            message id label
     */
    public MigrationPlanDeletionException(String message, MessageId messageId) {
        super(message);
        this.messageId = messageId;
    }


    /**
     * Creates exception with specified message and cause.
     * 
     * @param message
     *            exception message
     * @param messageId
     *            message id label
     * @param cause
     *            cause of an exception
     */
    public MigrationPlanDeletionException(String message, MessageId messageId, Throwable cause) {
        super(message, cause);
        this.messageId = messageId;
    }


    /**
     * Returns message id of this exception.
     * 
     * @return message id label
     */
    public MessageId getMessageId() {
        return messageId;
    }
}
