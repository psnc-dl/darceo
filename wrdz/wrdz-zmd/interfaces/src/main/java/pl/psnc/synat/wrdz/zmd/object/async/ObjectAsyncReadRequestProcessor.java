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
package pl.psnc.synat.wrdz.zmd.object.async;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.common.async.AsyncReadRequestAlreadyPrepared;
import pl.psnc.synat.wrdz.zmd.object.ObjectNotFoundException;

/**
 * Interface for processing asynchronous read requests concerning object management functionality.
 */
@Local
public interface ObjectAsyncReadRequestProcessor {

    /**
     * Processes the request of getting a digital object. Checks if it is in the asynchronous request processing cache,
     * and if it is OK then returns the response, otherwise it starts to fetch this object to the cache. Returns id of
     * the request.
     * 
     * @param identifier
     *            identifier of an object
     * @param version
     *            version of an object
     * @param provided
     *            whether to get provided metadata of an object
     * @param extracted
     *            whether to get extracted metadata of an object
     * @return request's identifier.
     * @throws AsyncReadRequestAlreadyPrepared
     *             response for asynchronous request when it is in cache and it is OK
     * @throws ObjectNotFoundException
     *             if either digital object or version within digital object do not exist
     */
    String processGetObject(String identifier, Integer version, Boolean provided, Boolean extracted)
            throws AsyncReadRequestAlreadyPrepared, ObjectNotFoundException;


    /**
     * Processes the request of getting a digital object's files. Checks if it is in the asynchronous request processing
     * cache, and if it is OK then returns the response, otherwise it starts to fetch these files to the cache. Returns
     * id of the request.
     * 
     * @param identifier
     *            identifier of an object
     * @param version
     *            version of an object
     * @param provided
     *            whether to get provided metadata of an object
     * @param extracted
     *            whether to get extracted metadata of an object
     * @param filesList
     *            semicolon separated list of file paths.
     * @return request's identifier.
     * @throws AsyncReadRequestAlreadyPrepared
     *             response for asynchronous request when it is in cache and it is OK
     * @throws ObjectNotFoundException
     *             if either digital object or version within digital object do not exist
     */
    String processGetDataFiles(String identifier, Integer version, Boolean provided, Boolean extracted, String filesList)
            throws AsyncReadRequestAlreadyPrepared, ObjectNotFoundException;


    /**
     * Processes the request of getting a digital object's metadata. Checks if it is in the asynchronous request
     * processing cache, and if it is OK then returns the response, otherwise it starts to fetch these files to the
     * cache. Returns id of the request.
     * 
     * @param identifier
     *            identifier of an object
     * @param version
     *            version of an object
     * @param provided
     *            whether to get provided metadata of an object
     * @param extracted
     *            whether to get extracted metadata of an object
     * @return request's identifier.
     * @throws AsyncReadRequestAlreadyPrepared
     *             response for asynchronous request when it is in cache and it is OK
     * @throws ObjectNotFoundException
     *             if either digital object or version within digital object do not exist
     */
    String processGetMainFile(String identifier, Integer version, Boolean provided, Boolean extracted)
            throws AsyncReadRequestAlreadyPrepared, ObjectNotFoundException;


    /**
     * Processes the request of getting a digital object's metadata. Checks if it is in the asynchronous request
     * processing cache, and if it is OK then returns the response, otherwise it starts to fetch these files to the
     * cache. Returns id of the request.
     * 
     * @param identifier
     *            identifier of an object
     * @param version
     *            version of an object
     * @param provided
     *            whether to get provided metadata of an object
     * @return request's identifier.
     * @throws AsyncReadRequestAlreadyPrepared
     *             response for asynchronous request when it is in cache and it is OK
     * @throws ObjectNotFoundException
     *             if either digital object or version within digital object do not exist
     */
    String processGetMetadata(String identifier, Integer version, Boolean provided)
            throws AsyncReadRequestAlreadyPrepared, ObjectNotFoundException;

}
