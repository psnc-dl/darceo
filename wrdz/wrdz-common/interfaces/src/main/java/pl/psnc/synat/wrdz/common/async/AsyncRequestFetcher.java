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

import javax.ejb.Local;

import pl.psnc.synat.wrdz.common.entity.async.AsyncRequest;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult;

/**
 * Specifies the interface of the common bean that fetches responses for asynchronous requests.
 */
@Local
public interface AsyncRequestFetcher {

    /**
     * Gets the result identifier of the asynchronous request at given identifier.
     * 
     * @param requestId
     *            request identifier
     * @return asynchronous request
     * @throws AsyncRequestNotFoundException
     *             when the asynchronous request at given identifier does not exist
     */
    AsyncRequest getAsyncRequest(String requestId)
            throws AsyncRequestNotFoundException;


    /**
     * Gets the result of the asynchronous request at given URL, but only when the response is OK.
     * 
     * @param requestedUrl
     *            URL of the request
     * @return result of asynchronous request
     * @throws AsyncRequestResultNotFoundException
     *             when the OK result of asynchronous request at given URL does not exist
     */
    AsyncRequestResult getAsyncRequestOKResultByRequestedUrl(String requestedUrl)
            throws AsyncRequestResultNotFoundException;


    /**
     * Gets the id of the request at given URL that is in progress.
     * 
     * @param requestedUrl
     *            URL of the request
     * @return identifier of the request
     * @throws AsyncRequestNotFoundException
     *             when the request at given URL that is in progress does not exist
     */
    String getAsyncRequestInProgressIdByRequestedUrl(String requestedUrl)
            throws AsyncRequestNotFoundException;


    /**
     * Gets the result for asynchronous request by identifier of this result.
     * 
     * @param resultId
     *            result identifier
     * @return result of asynchronous request
     * @throws AsyncRequestResultNotFoundException
     *             when the result of asynchronous request at given identifier does not exist
     */
    AsyncRequestResult getAsyncRequestResult(String resultId)
            throws AsyncRequestResultNotFoundException;


    /**
     * Gets the result for asynchronous request by URL of request.
     * 
     * @param requestedUrl
     *            URL of the request
     * @return result of asynchronous request
     * @throws AsyncRequestResultNotFoundException
     *             when the result of asynchronous request at given URL does not exist
     */
    AsyncRequestResult getAsyncRequestResultByRequestedUrl(String requestedUrl)
            throws AsyncRequestResultNotFoundException;

}
