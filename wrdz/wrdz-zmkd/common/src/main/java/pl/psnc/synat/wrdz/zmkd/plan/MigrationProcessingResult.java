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

/**
 * The possible outcomes the migration process of a single digital object can have.
 */
public enum MigrationProcessingResult {

    /**
     * An object was successfully processed.
     */
    PROCESSED,

    /**
     * The processing was paused because the object was not yet available for download or some asynchronous service was
     * invokes.
     */
    PAUSED,

    /**
     * There are no more objects to be processed.
     */
    FINISHED;
}
