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
package pl.psnc.synat.wrdz.zmkd.entity.plan;

/**
 * Migration status of digital objects.
 */
public enum MigrationItemStatus {

    /**
     * Migration has not started yet.
     */
    NOT_YET_STARTED,

    /**
     * Migration is in progress.
     */
    IN_PROGRESS,

    /**
     * Migrated object has been uploaded to ZMD and is awaiting creation confirmation.
     */
    UPLOADED,

    /**
     * Migration ended successfully.
     */
    DONE,

    /**
     * There was a problem during a migration process.
     */
    ERROR,

    /**
     * Could not migrate the object because of insufficient access rights.
     */
    ERROR_PERMISSIONS,

    /**
     * Fetching the object from ZMD failed.
     */
    ERROR_FETCHING,

    /**
     * Calling a service failed.
     */
    ERROR_SERVICE,

    /**
     * Creation of the migrated object failed.
     */
    ERROR_CREATION;
}
