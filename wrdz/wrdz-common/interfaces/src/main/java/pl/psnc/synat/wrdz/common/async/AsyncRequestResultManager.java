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
package pl.psnc.synat.wrdz.common.async;

import java.io.IOException;

import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult;

/**
 * Handles the management of asynchronous request results.
 */
public interface AsyncRequestResultManager {

    /**
     * Prepares a result for the asynchronous request with the given identifier.
     * 
     * @param requestId
     *            request id
     * @return result
     */
    AsyncRequestResult prepareResult(String requestId);


    /**
     * Prepares a result for the asynchronous request with the given identifier.
     * 
     * @param requestId
     *            request id
     * @param code
     *            result code
     * @return result
     */
    AsyncRequestResult prepareResult(String requestId, Integer code);


    /**
     * Saves the given result string in the file associated with the given result identifier.
     * 
     * @param resultId
     *            id of the result of an asynchronous request
     * @param result
     *            the result of asynchronous request
     * @throws IOException
     *             when some error during file creation occurs
     */
    void saveResultString(String resultId, String result)
            throws IOException;


    /**
     * Saves the contents of the file denoted by the given path in the file associated with the given result identifier.
     * 
     * @param resultId
     *            id of the result of an asynchronous request
     * @param path
     *            absolute path to the file with the results of an asynchronous request
     * @throws IOException
     *             when some error during file creation occurs
     */
    void saveResultFile(String resultId, String path)
            throws IOException;
}
