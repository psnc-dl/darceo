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

import javax.ejb.Local;

/**
 * Provides methods for managing the status of migration items.
 */
@Local
public interface MigrationItemManager {

    /**
     * Logs that the migration of the digital object with the given identifier was started.
     * 
     * @param planId
     *            migration plan id
     * @param objectIdentifier
     *            digital object identifier
     */
    void logMigrationStarted(long planId, String objectIdentifier);


    /**
     * Logs that the migrated version of the digital object with the given identifier was sent to ZMD.
     * 
     * @param planId
     *            migration plan id
     * @param objectIdentifier
     *            digital object identifier
     * @param requestId
     *            creation request identifier
     */
    void logUploaded(long planId, String objectIdentifier, String requestId);


    /**
     * Logs that the given creation request was successfully handled.
     * 
     * @param requestId
     *            creation request identifier
     */
    void logCreationSuccessful(String requestId);


    /**
     * Logs that the digital object migration failed due to the user not having the required security rights.
     * 
     * @param planId
     *            migration plan id
     * @param objectIdentifier
     *            digital object identifier
     * @param errorMessageParams
     *            error description
     */
    void logPermissionError(long planId, String objectIdentifier, String errorMessageParams);


    /**
     * Logs that fetching the digital object with the given identifier failed.
     * 
     * @param planId
     *            migration plan id
     * @param objectIdentifier
     *            digital object identifier
     * @param errorMessageParams
     *            error description
     */
    void logFetchingError(long planId, String objectIdentifier, String errorMessageParams);


    /**
     * Logs that calling a service for the digital object with the given identifier failed.
     * 
     * @param planId
     *            migration plan id
     * @param objectIdentifier
     *            digital object identifier
     * @param errorMessageParams
     *            error description
     */
    void logServiceError(long planId, String objectIdentifier, String errorMessageParams);


    /**
     * Logs that the creation of the migration result for the object with the given identifier failed.
     * 
     * @param planId
     *            migration plan id
     * @param objectIdentifier
     *            digital object identifier
     * @param errorMessageParams
     *            error description
     */
    void logCreationError(long planId, String objectIdentifier, String errorMessageParams);


    /**
     * Logs that the given creation request failed.
     * 
     * @param requestId
     *            creation request identifier
     * @param errorMessageParams
     *            error description
     */
    void logCreationError(String requestId, String errorMessageParams);


    /**
     * Logs that the migration of the digital object with the given identifier failed.
     * 
     * @param planId
     *            migration plan id
     * @param objectIdentifier
     *            digital object identifier
     * @param errorMessageParams
     *            error description
     */
    void logError(long planId, String objectIdentifier, String errorMessageParams);
}
