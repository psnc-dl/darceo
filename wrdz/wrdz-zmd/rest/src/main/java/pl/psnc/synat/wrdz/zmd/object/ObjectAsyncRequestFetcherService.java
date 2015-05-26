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
package pl.psnc.synat.wrdz.zmd.object;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.async.AsyncRequestFetcherService;
import pl.psnc.synat.wrdz.common.async.AsyncRequestNotFoundException;
import pl.psnc.synat.wrdz.common.async.AsyncRequestServiceConsts;
import pl.psnc.synat.wrdz.common.config.Configuration;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequest;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResultConsts;
import pl.psnc.synat.wrdz.common.rest.exception.NotFoundException;
import pl.psnc.synat.wrdz.common.utility.StringEncoder;

/**
 * Service that fetch responses for asynchronous requests submitted to the object management functionality of ZMD
 * module.
 */
@Path("async/object")
@ManagedBean
public class ObjectAsyncRequestFetcherService extends AsyncRequestFetcherService {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ObjectAsyncRequestFetcherService.class);

    /**
     * Configuration.
     */
    @Inject
    private Configuration config;


    @Override
    protected String getAsyncBaseUri(UriInfo uriInfo) {
        return getPrimalBaseUri(uriInfo) + ObjectManagerServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI;
    }


    @Override
    protected String getPrimalBaseUri(UriInfo uriInfo) {
        logger.debug("base URI: " + uriInfo.getBaseUri());
        return uriInfo.getBaseUri().toString();
    }


    @Override
    protected String getAsyncResponseFolder() {
        return config.getAsyncCacheHome();
    }


    /**
     * Checks whether the digital object is in the (asynchronous request processing) cache and redirect to service that
     * returns it.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param requestId
     *            id of asynchronous request
     * @param id
     *            public identifier of the object
     * @return information whether the digital object is ready to download
     */
    @GET
    @Path("{id}/status/{requestId}")
    public Response getObjectStatus(@Context UriInfo uriInfo, @PathParam("requestId") String requestId,
            @PathParam("id") String id) {
        return getReadStatus(uriInfo, requestId, id);
    }


    /**
     * Gets the status of object's files asynchronous request.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param requestId
     *            id of asynchronous request.
     * @param id
     *            public identifier of the object.
     * @return status of the request.
     */
    @GET
    @Path("{id}/files/status/{requestId}")
    public Response getFilesStatus(@Context UriInfo uriInfo, @PathParam("requestId") String requestId,
            @PathParam("id") String id) {
        return getReadStatus(uriInfo, requestId, id);
    }


    /**
     * Gets the status of object's main file asynchronous request.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param requestId
     *            id of asynchronous request.
     * @param id
     *            public identifier of the object.
     * @return status of the request.
     */
    @GET
    @Path("{id}/mainfile/status/{requestId}")
    public Response getMainFileStatus(@Context UriInfo uriInfo, @PathParam("requestId") String requestId,
            @PathParam("id") String id) {
        return getReadStatus(uriInfo, requestId, id);
    }


    /**
     * Gets the status of object's metadata asynchronous request.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param requestId
     *            id of asynchronous request.
     * @param id
     *            public identifier of the object.
     * @return status of the request.
     */
    @GET
    @Path("{id}/metadata/status/{requestId}")
    public Response getMetadataStatus(@Context UriInfo uriInfo, @PathParam("requestId") String requestId,
            @PathParam("id") String id) {
        return getReadStatus(uriInfo, requestId, id);
    }


    /**
     * Checks whether the response for asynchronous read request is ready and if it ready then redirect to the proper
     * response. This is special method that overrides the simmilar method without id parameter - in case when object
     * identifier is URL we have to encode it.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param requestId
     *            id of asynchronous request
     * @param id
     *            public identifier of the object.
     * @return response location or information whether is it ready
     */
    protected Response getReadStatus(@Context UriInfo uriInfo, String requestId, String id) {
        try {
            AsyncRequest request = asyncRequestFetcherBean.getAsyncRequest(requestId);
            if (request.isInProgress()) {
                return Response.status(Response.Status.OK).build();
            } else {
                String location;
                if (request.getResult().getCode().equals(AsyncRequestResultConsts.HTTP_CODE_OK)) {
                    if (id.contains("http://")) {
                        location = getPrimalBaseUri(uriInfo)
                                + request.getRequestedUrl().replace(id, StringEncoder.encodeUrl(id));
                    } else {
                        location = getPrimalBaseUri(uriInfo) + request.getRequestedUrl();
                    }
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
