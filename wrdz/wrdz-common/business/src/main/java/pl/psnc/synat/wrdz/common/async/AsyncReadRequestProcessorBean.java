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

import javax.ejb.EJB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult;

/**
 * Abstract common bean for processing asynchronous read requests concerning every functionality.
 */
public abstract class AsyncReadRequestProcessorBean {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(AsyncReadRequestProcessorBean.class);

    /**
     * Fetcher of asynchronous request and their results.
     */
    @EJB
    private AsyncRequestFetcher asyncRequestFetcherBean;


    /**
     * Checks whether the response for asynchronous request is prepared or it is in progress. This method must be used
     * in every method in subclasses, after constructing the requested URL.
     * 
     * @param requestedUrl
     *            URL of the request
     * @return identifier of the request when it is in progress
     * @throws AsyncReadRequestAlreadyPrepared
     *             when the response is prepared and it is OK
     * @throws AsyncRequestNotFoundException
     *             when the request in progress does not exists
     */
    protected String checkResponse(String requestedUrl)
            throws AsyncReadRequestAlreadyPrepared, AsyncRequestNotFoundException {
        try {
            AsyncRequestResult result = asyncRequestFetcherBean.getAsyncRequestOKResultByRequestedUrl(requestedUrl);
            logger.debug("Response for " + requestedUrl + " is already prepared: " + result.getId());
            throw new AsyncReadRequestAlreadyPrepared(result);
        } catch (AsyncRequestResultNotFoundException e) {
            logger.debug("There is no prepared OK response for that request: " + requestedUrl);
            String requestId = asyncRequestFetcherBean.getAsyncRequestInProgressIdByRequestedUrl(requestedUrl);
            logger.debug("Another such request is already processing: " + requestId);
            return requestId;
        }
    }

}
