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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.entity.async.AsyncRequest;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResultConsts;
import pl.psnc.synat.wrdz.common.rest.exception.NotFoundException;

/**
 * Base class for services that fetch responses for asynchronous requests submitted to WRDZ application.
 */
public abstract class AsyncRequestFetcherService extends AsyncRequestService {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(AsyncRequestFetcherService.class);

    /**
     * Bean that fetches responses for asynchronous requests.
     */
    @EJB
    protected AsyncRequestFetcher asyncRequestFetcherBean;


    /**
     * Gets the base URI of the async service.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @return base URI of the async service
     */
    protected abstract String getAsyncBaseUri(UriInfo uriInfo);


    /**
     * Gets the primal base URI of the service.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * 
     * @return primal base URI of the service
     */
    protected abstract String getPrimalBaseUri(UriInfo uriInfo);


    /**
     * Checks whether the response for asynchronous request is ready and if it ready then redirect to the response.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param requestId
     *            id of asynchronous request
     * @return response location or information whether is it ready
     */
    @GET
    @Path("status/{requestId}")
    public Response getStatus(@Context UriInfo uriInfo, @PathParam("requestId") String requestId) {
        logger.debug("request id: " + requestId);
        AsyncRequest request = null;
        try {
            request = asyncRequestFetcherBean.getAsyncRequest(requestId);
        } catch (AsyncRequestNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
        if (request.isInProgress()) {
            return Response.status(Response.Status.OK).build();
        } else {
            String location = getAsyncBaseUri(uriInfo) + "result/" + request.getResult().getId();
            return Response.status(Response.Status.SEE_OTHER)
                    .header(AsyncRequestServiceConsts.HTTP_HEADER_LOCATION, location).build();
        }
    }


    /**
     * Returns the prepared result of asynchronous request.
     * 
     * @param resultId
     *            id of result
     * @return result
     */
    @GET
    @Path("result/{resultId}")
    public Response getResult(@PathParam("resultId") String resultId) {
        logger.debug("resultId id: " + resultId);
        AsyncRequestResult result = null;
        try {
            result = asyncRequestFetcherBean.getAsyncRequestResult(resultId);
        } catch (AsyncRequestResultNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
        return buildResponse(result);
    }


    /**
     * Checks whether the response for asynchronous read request is ready and if it ready then redirect to the proper
     * response.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param requestId
     *            id of asynchronous request
     * @return response location or information whether is it ready
     */
    protected Response getReadStatus(@Context UriInfo uriInfo, String requestId) {
        try {
            AsyncRequest request = asyncRequestFetcherBean.getAsyncRequest(requestId);
            if (request.isInProgress()) {
                return Response.status(Response.Status.OK).build();
            } else {
                String location;
                if (request.getResult().getCode().equals(AsyncRequestResultConsts.HTTP_CODE_OK)) {
                    location = getPrimalBaseUri(uriInfo) + request.getRequestedUrl();
                } else {
                    location = getAsyncBaseUri(uriInfo) + "result/" + request.getResult().getId();
                }
                return Response.status(Response.Status.SEE_OTHER)
                        .header(AsyncRequestServiceConsts.HTTP_HEADER_LOCATION, location).build();
            }
        } catch (AsyncRequestNotFoundException e) {
            throw new NotFoundException();
        }
    }

}
