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
package pl.psnc.synat.wrdz.mdz.integrity;

import java.util.concurrent.Future;

import javax.ejb.Local;

/**
 * Responsible for the actual digital object processing.
 * <p>
 * Processing can be postponed when an object's data isn't immediately available for download. In that case, the
 * processor keeps track of that object's identifier and must be manually restarted when that object becomes available.
 */
@Local
public interface IntegrityProcessor {

    /**
     * Iteratively processes all active digital objects, checking their data integrity.
     * 
     * This method runs asynchronously and is cancellable without losing progress (ie. calling it again after it has
     * been cancelled resumes the processing instead of restarting it).
     * 
     * If an object is not immediately available for download, the processor is set to wait for the object's identifier,
     * and processing stops. A restart of the processor is required when the object becomes available.
     * 
     * @see Future#cancel(boolean)
     * @return empty Future object that allows the processing to be stopped if needed
     */
    Future<Void> processAll();


    /**
     * Processes a single digital object, verifying its data integrity.
     * 
     * This method fetches the object's files from ZMD, calculates their hashes and checks them against the hash values
     * stored in the ZMD database. A message is sent if the calculated and retrieved hash values do not match.
     * 
     * If the object is not immediately available for download, the processor is set to wait for the object's
     * identifier, and this method exits.
     * 
     * @return processing result
     */
    IntegrityProcessingResult processOne();


    /**
     * Checks whether the processor is currently waiting for the object with the given identifier, and the wait status
     * is cleared if the result is positive.
     * 
     * @param objectIdentifier
     *            identifier of the digital object
     * @return <code>true</code> if the processor is waiting for the given object; <code>false</code> otherwise
     */
    boolean isWaitingFor(String objectIdentifier);


    /**
     * Unconditionally clears the wait status.
     */
    void clearWait();


    /**
     * Logs statistics and clears the data table.
     */
    void finishCycle();
}
