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

import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.zmkd.plan.execution.TransformationInfo;

/**
 * Responsible for a migration plan processing.
 * <p>
 * Processing can be postponed when an object's data isn't immediately available for download (for some digital object
 * covered by the plan) or when some asynchronous migration service is involved. In those cases, the processor keeps
 * track (and persists it) of that object's identifier or that service.
 */
@Local
public interface MigrationPlanProcessor {

    /**
     * Iteratively processes all active digital objects, migrating them according to the plan with the given id.
     * 
     * This method runs asynchronously and is cancellable without losing progress (ie. calling it again after it has
     * been cancelled resumes the processing instead of restarting it).
     * 
     * If an object is not immediately available for download, the processor is set to wait for the object's identifier,
     * and processing stops. A restart of the processor is required when the object becomes available. Similarly, if
     * some asynchronous service was invoked, the processor is set to wait for the result and processing stops.
     * 
     * @see Future#cancel(boolean)
     * @param planId
     *            identifier of a migration plan
     * @return empty Future object that allows the processing to be stopped if needed
     */
    Future<Void> processAll(long planId);


    /**
     * Processes a single digital object, migrating it according to the plan with the given id.
     * 
     * This method fetches the object's files from ZMD, unzips all files, parses their technical and administrative
     * metadata and passes all files that are in the format from the migration plan to subsequent migration services.
     * Finally it creates a new digital object (also its metadata) and stores it in ZMD.
     * 
     * If the object is not immediately available for download, the processor is set to wait for the object's
     * identifier, and this method exits. Similarly when some asynchronous service was invoked.
     * 
     * @param planId
     *            identifier of a migration plan
     * @param path
     *            detailed information about the migration path's hops, in the order they should be executed
     * @return processing result
     * @throws MigrationProcessingException
     *             when some error occurred
     * @throws MigrationPlanNotFoundException
     *             when specified plan was not found
     */
    MigrationProcessingResult processOne(long planId, List<TransformationInfo> path)
            throws MigrationProcessingException, MigrationPlanNotFoundException;

}
