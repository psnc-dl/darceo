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

import java.io.InputStream;
import java.util.UUID;
import java.util.zip.ZipInputStream;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.async.AsyncReadRequestAlreadyPrepared;
import pl.psnc.synat.wrdz.common.async.AsyncRequestProcessor;
import pl.psnc.synat.wrdz.common.async.AsyncRequestService;
import pl.psnc.synat.wrdz.common.async.AsyncRequestServiceConsts;
import pl.psnc.synat.wrdz.common.config.Configuration;
import pl.psnc.synat.wrdz.common.rest.exception.AccessDeniedException;
import pl.psnc.synat.wrdz.common.rest.exception.BadRequestException;
import pl.psnc.synat.wrdz.common.rest.exception.InternalServerErrorException;
import pl.psnc.synat.wrdz.common.rest.exception.NoContentException;
import pl.psnc.synat.wrdz.common.rest.exception.NotFoundException;
import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.common.utility.StringEncoder;
import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.input.IncompleteDataException;
import pl.psnc.synat.wrdz.zmd.input.InvalidDataException;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectDeletionRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectVersionDeletionRequest;
import pl.psnc.synat.wrdz.zmd.object.async.ObjectAsyncReadRequestProcessor;
import pl.psnc.synat.wrdz.zmd.object.async.ObjectAsyncRequestEnum;
import pl.psnc.synat.wrdz.zmd.object.migration.ObjectMigrationManager;
import pl.psnc.synat.wrdz.zmd.object.parser.ObjectCreationParser;
import pl.psnc.synat.wrdz.zmd.object.parser.ObjectModificationParser;
import pl.psnc.synat.wrdz.zmd.object.validators.ObjectCreationValidator;
import pl.psnc.synat.wrdz.zmd.object.validators.ObjectModificationValidator;
import pl.psnc.synat.wrdz.zu.permission.ObjectPermissionManager;
import pl.psnc.synat.wrdz.zu.types.ObjectPermissionType;

/**
 * Object manager service. It accepts requests concerning basic operations on digital object, like: creating,
 * retrieving, updating and removing.
 */
@Path("/")
@ManagedBean
public class ObjectManagerService extends AsyncRequestService {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ObjectManagerService.class);

    /**
     * Business logic for object management functionality.
     */
    @EJB
    private ObjectManager objectManager;

    /** Object checker. */
    @EJB
    private ObjectChecker objectChecker;

    /** Object browser. */
    @EJB
    private ObjectBrowser objectBrowser;

    /**
     * Business logic for object migration management functionality.
     */
    @EJB
    private ObjectMigrationManager objectMigrationManager;

    /**
     * Bean of asynchronous requests processing for this functionality.
     */
    @EJB(beanName = "ObjectAsyncRequestProcessorBean")
    private AsyncRequestProcessor<ObjectAsyncRequestEnum> asyncRequestProcessor;

    /**
     * Bean of asynchronous read requests processing for this functionality.
     */
    @EJB
    private ObjectAsyncReadRequestProcessor objectAsyncReadRequestProcessor;

    /**
     * Request validator for object modification.
     */
    @EJB
    private ObjectModificationValidator objectModificationValidator;

    /**
     * Request validator for object creation.
     */
    @EJB
    private ObjectCreationValidator objectCreationValidator;

    /** Object permission manager. */
    @EJB(name = "ObjectPermissionManager")
    private ObjectPermissionManager permissionManager;

    /** User context. */
    @EJB
    private UserContext userContext;

    /**
     * Configuration.
     */
    @Inject
    private Configuration config;

    /**
     * Configuration.
     */
    @Inject
    private ZmdConfiguration zmdConfig;


    @Override
    protected String getAsyncResponseFolder() {
        return config.getAsyncCacheHome();
    }


    /**
     * Accepts request for creating a digital object consisting of given data files and metadata files. It also takes
     * some optional parameters like type of the digital object, main file, origin and derivatives. Files and metadata
     * files can be passed in two ways: directly in a part of the body of this request, and indirectly by a URL to the
     * remote file.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param multipart
     *            parameters of digital object encoded by multipart/form-data
     * @return HTTP code 202 if request was accepted or another code saying what was wrong
     */
    @POST
    @Path("object")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createObject(@Context UriInfo uriInfo, com.sun.jersey.multipart.FormDataMultiPart multipart) {

        if (!permissionManager.hasPermission(userContext.getCallerPrincipalName(), null, ObjectPermissionType.CREATE)) {
            throw new AccessDeniedException();
        }

        ObjectCreationRequest request = null;
        try {
            ObjectCreationParser parser = new ObjectCreationParser(zmdConfig.getCacheHome() + "/" + UUID.randomUUID());
            request = parser.parse(multipart);
            logger.debug("New request: " + request);
            objectCreationValidator.validateObjectCreationRequest(request);
        } catch (IncompleteDataException e) {
            throw new BadRequestException(e.getMessage());
        } catch (InvalidDataException e) {
            throw new BadRequestException(e.getMessage());
        } catch (ObjectCreationException e) {
            throw new BadRequestException(e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        Response result = processObjectCreationRequest(uriInfo, request);
        return result;
    }


    /**
     * Accepts request for creating a digital object consisting of given data files and metadata files. It also takes
     * some optional parameters like type of the digital object, main file, origin and derivatives. Files and metadata
     * files can be passed only in one way: indirectly by a URL to the remote file.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param params
     *            parameters of digital object encoded by application/x-www-form-urlencoded
     * @return HTTP code 202 if request was accepted or another code saying what was wrong
     */
    @POST
    @Path("object")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createObject(@Context UriInfo uriInfo, MultivaluedMap<String, String> params) {

        if (!permissionManager.hasPermission(userContext.getCallerPrincipalName(), null, ObjectPermissionType.CREATE)) {
            throw new AccessDeniedException();
        }

        ObjectCreationRequest request = null;
        try {
            ObjectCreationParser parser = new ObjectCreationParser(zmdConfig.getCacheHome() + "/" + UUID.randomUUID());
            request = parser.parse(params);
            logger.debug("New request: " + request);
            objectCreationValidator.validateObjectCreationRequest(request);
        } catch (IncompleteDataException e) {
            throw new BadRequestException(e.getMessage());
        } catch (InvalidDataException e) {
            throw new BadRequestException(e.getMessage());
        } catch (ObjectCreationException e) {
            throw new BadRequestException(e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        Response result = processObjectCreationRequest(uriInfo, request);
        return result;
    }


    /**
     * Accepts request for creating a digital object consisting of given data files and metadata files. Additional
     * parameters like type of the digital object, main file, origin and derivatives should be passed in separate
     * requests to update services. They can also be passed in metadata files (what is preferable). The structure of the
     * zip archive must exactly reflect the structure of digital object (global metadata in the root of archive matadata
     * folder <code>metadata</code> and metadata for particular files in folders that have the same names like these
     * files).
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param payload
     *            bytes of the zip archive with the digital object
     * @param name
     *            object name (can be <code>null</code>)
     * @return HTTP code 202 if request was accepted or another code saying what was wrong
     */
    @POST
    @Path("object")
    @Consumes(pl.psnc.synat.wrdz.common.rest.MediaType.APPLICATION_ZIP)
    public Response createObject(@Context UriInfo uriInfo, InputStream payload, @QueryParam("name") String name) {

        if (!permissionManager.hasPermission(userContext.getCallerPrincipalName(), null, ObjectPermissionType.CREATE)) {
            throw new AccessDeniedException();
        }

        ObjectCreationRequest request = null;
        try {
            ObjectCreationParser parser = new ObjectCreationParser(zmdConfig.getCacheHome() + "/" + UUID.randomUUID());
            logger.debug("New request: " + request);
            request = parser.parse(new ZipInputStream(payload), name);
            objectCreationValidator.validateObjectCreationRequest(request);
        } catch (IncompleteDataException e) {
            throw new BadRequestException(e.getMessage());
        } catch (InvalidDataException e) {
            throw new BadRequestException(e.getMessage());
        } catch (ObjectCreationException e) {
            throw new BadRequestException(e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        Response result = processObjectCreationRequest(uriInfo, request);
        return result;
    }


    /**
     * Processes a request for object creation.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param request
     *            request
     * @return response
     */
    private Response processObjectCreationRequest(UriInfo uriInfo, ObjectCreationRequest request) {
        try {
            String requestId = asyncRequestProcessor.processRequestAsynchronously(ObjectAsyncRequestEnum.CREATE_OBJECT,
                request);
            String location = uriInfo.getBaseUri() + ObjectManagerServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI
                    + AsyncRequestServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI_STATUS + requestId;
            return Response.status(Response.Status.ACCEPTED)
                    .header(AsyncRequestServiceConsts.HTTP_HEADER_LOCATION, location).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    /**
     * Accepts request for modifying a digital object at given identifier data files and metadata files (it creates new
     * version of the digital object). It also takes some optional parameters like type of the digital object, main
     * file, origin and derivatives. Files and metadata files can be passed in two ways: directly in a part of the body
     * of this request, and indirectly by a URL to the remote file.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param id
     *            public identifier of the object
     * @param multipart
     *            parameters of digital object encoded by multipart/form-data
     * @return HTTP code 202 if request was accepted or another code saying what was wrong
     */
    @POST
    @Path("object/{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response modifyObject(@Context UriInfo uriInfo, @PathParam("id") String id,
            com.sun.jersey.multipart.FormDataMultiPart multipart) {

        checkPermission(id, ObjectPermissionType.UPDATE);

        ObjectModificationRequest request = null;
        try {
            ObjectModificationParser parser = new ObjectModificationParser(zmdConfig.getCacheHome() + "/"
                    + UUID.randomUUID());
            request = parser.parse(id, multipart);
            logger.debug("New request: " + request);
            objectModificationValidator.validateObjectModificationRequest(request);
        } catch (IncompleteDataException e) {
            throw new BadRequestException(e.getMessage());
        } catch (InvalidDataException e) {
            throw new BadRequestException(e.getMessage());
        } catch (ObjectModificationException e) {
            throw new BadRequestException(e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        return processObjectModificationRequest(uriInfo, request);
    }


    /**
     * Accepts request for modifying a digital object at given identifier data files and metadata files (it creates new
     * version of the digital object). It also takes some optional parameters like type of the digital object, main
     * file, origin and derivatives. Files and metadata files can be passed only in one way: indirectly by a URL to the
     * remote file.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * 
     * @param id
     *            public identifier of the object
     * @param params
     *            parameters of digital object encoded by application/x-www-form-urlencoded
     * @return HTTP code 202 if request was accepted or another code saying what was wrong
     */
    @POST
    @Path("object/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response modifyObject(@Context UriInfo uriInfo, @PathParam("id") String id,
            MultivaluedMap<String, String> params) {

        checkPermission(id, ObjectPermissionType.UPDATE);

        ObjectModificationRequest request = null;
        try {
            ObjectModificationParser parser = new ObjectModificationParser(zmdConfig.getCacheHome() + "/"
                    + UUID.randomUUID());
            request = parser.parse(id, params);
            logger.debug("New request: " + request);
            objectModificationValidator.validateObjectModificationRequest(request);
        } catch (IncompleteDataException e) {
            throw new BadRequestException(e.getMessage());
        } catch (InvalidDataException e) {
            throw new BadRequestException(e.getMessage());
        } catch (ObjectModificationException e) {
            throw new BadRequestException(e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        return processObjectModificationRequest(uriInfo, request);
    }


    /**
     * Accepts request for modifying a digital object at given identifier data files and metadata files (it creates new
     * version of the digital object, completely replacing the previous version).The structure of the zip archive must
     * exactly reflect the structure of digital object (global metadata in the root of archive matadata folder
     * <code>metadata</code> and metadata for particular files in folders that have the same names like these files).
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param id
     *            public identifier of the object
     * @param payload
     *            bytes of the zip archive with the digital object
     * @return HTTP code 202 if request was accepted or another code saying what was wrong
     */
    @POST
    @Path("object/{id}")
    @Consumes(pl.psnc.synat.wrdz.common.rest.MediaType.APPLICATION_ZIP)
    public Response modifyObject(@Context UriInfo uriInfo, @PathParam("id") String id, InputStream payload) {

        checkPermission(id, ObjectPermissionType.UPDATE);

        ObjectModificationRequest request = null;
        try {
            ObjectModificationParser parser = new ObjectModificationParser(zmdConfig.getCacheHome() + "/"
                    + UUID.randomUUID());
            request = parser.parse(id, new ZipInputStream(payload));
            logger.debug("New request: " + request);
            objectModificationValidator.validateObjectModificationRequest(request);
        } catch (IncompleteDataException e) {
            throw new BadRequestException(e.getMessage());
        } catch (InvalidDataException e) {
            throw new BadRequestException(e.getMessage());
        } catch (ObjectModificationException e) {
            throw new BadRequestException(e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        return processObjectModificationRequest(uriInfo, request);
    }


    /**
     * Processes a request for object modification.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param request
     *            request
     * @return response
     */
    private Response processObjectModificationRequest(UriInfo uriInfo, ObjectModificationRequest request) {
        try {
            String requestId = asyncRequestProcessor.processRequestAsynchronously(ObjectAsyncRequestEnum.MODIFY_OBJECT,
                request);
            String location = uriInfo.getBaseUri() + ObjectManagerServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI
                    + AsyncRequestServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI_STATUS + requestId;
            return Response.status(Response.Status.ACCEPTED)
                    .header(AsyncRequestServiceConsts.HTTP_HEADER_LOCATION, location).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    /**
     * Accepts request for removing the digital object at given identifier (whole object with all its versions).
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param id
     *            public identifier of the object
     * @param version
     *            version number of the object to remove - without this parameter the whole object is removed
     * @return HTTP code 202 if request was accepted or another code saying what was wrong
     */
    @DELETE
    @Path("object/{id}")
    public Response deleteObject(@Context UriInfo uriInfo, @PathParam("id") String id,
            @QueryParam("version") Integer version) {

        if (!objectChecker.checkIfObjectsVersionExists(id, version)) {
            throw new NotFoundException();
        }

        checkPermission(id, ObjectPermissionType.DELETE);

        String requestId = null;
        try {
            if (version == null) {
                ObjectDeletionRequest request = new ObjectDeletionRequest(id);
                logger.debug("New request: " + request);
                requestId = asyncRequestProcessor.processRequestAsynchronously(ObjectAsyncRequestEnum.DELETE_OBJECT,
                    request);
            } else {
                ObjectVersionDeletionRequest request = new ObjectVersionDeletionRequest(id, version);
                logger.debug("New request: " + request);
                requestId = asyncRequestProcessor.processRequestAsynchronously(ObjectAsyncRequestEnum.DELETE_VERSION,
                    request);
            }
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        String location = uriInfo.getBaseUri() + ObjectManagerServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI
                + AsyncRequestServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI_STATUS + requestId;
        return Response.status(Response.Status.ACCEPTED)
                .header(AsyncRequestServiceConsts.HTTP_HEADER_LOCATION, location).build();
    }


    /**
     * Gets a digital object if it is in the cache of asynchronous request processing, or fetches it from data storage
     * otherwise. In this latter case it accepts request and returns an address with the fetching status.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param id
     *            public identifier of the object
     * @param version
     *            version number of the object - current version by default
     * @param provided
     *            whether provided metadata should be fetched
     * @param extracted
     *            whether extracted metadata should be fetched
     * @return digital object if it is in the (asynchronous request processing) cache or an address with the fetching
     *         status
     */
    @GET
    @Path("object/{id}")
    public Response getObject(@Context UriInfo uriInfo, @PathParam("id") String id,
            @QueryParam("version") Integer version, @DefaultValue("true") @QueryParam("provided") Boolean provided,
            @DefaultValue("true") @QueryParam("extracted") Boolean extracted) {

        checkPermission(id, ObjectPermissionType.READ);

        String requestId = null;
        try {
            requestId = objectAsyncReadRequestProcessor.processGetObject(id, version, provided, extracted);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (AsyncReadRequestAlreadyPrepared e) {
            return buildResponse(e.getResult());
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        String location = uriInfo.getBaseUri() + ObjectManagerServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI
                + StringEncoder.encodeUrl(id) + "/"
                + AsyncRequestServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI_STATUS + requestId;
        return Response.status(Response.Status.ACCEPTED)
                .header(AsyncRequestServiceConsts.HTTP_HEADER_LOCATION, location).build();
    }


    /**
     * Gets a digital object specified files if it is in the cache of asynchronous request processing, or fetches it
     * from data storage otherwise. In this latter case it accepts request and returns an address with the fetching
     * status.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param id
     *            public identifier of the object
     * @param version
     *            version number of the object - current version by default
     * @param provided
     *            whether provided metadata should be fetched
     * @param extracted
     *            whether extracted metadata should be fetched
     * @param filesList
     *            semicolon separated list of paths to files inside the object.
     * @return digital object specified files if it is in the (asynchronous request processing) cache or an address with
     *         the fetching status
     */
    @GET
    @Path("object/{id}/files")
    public Response getObjectFiles(@Context UriInfo uriInfo, @PathParam("id") String id,
            @QueryParam("version") Integer version, @DefaultValue("true") @QueryParam("provided") Boolean provided,
            @DefaultValue("true") @QueryParam("extracted") Boolean extracted, @QueryParam("filelist") String filesList) {

        checkPermission(id, ObjectPermissionType.READ);

        String requestId = null;
        if (filesList == null | filesList.isEmpty()) {
            throw new BadRequestException("You have to specify list of files to download.");
        }
        try {
            requestId = objectAsyncReadRequestProcessor
                    .processGetDataFiles(id, version, provided, extracted, filesList);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (AsyncReadRequestAlreadyPrepared e) {
            return buildResponse(e.getResult());
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        String location = uriInfo.getBaseUri() + ObjectManagerServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI
                + StringEncoder.encodeUrl(id) + "/files/"
                + AsyncRequestServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI_STATUS + requestId;
        return Response.status(Response.Status.ACCEPTED)
                .header(AsyncRequestServiceConsts.HTTP_HEADER_LOCATION, location).build();
    }


    /**
     * Gets a main file of the specified object at specified version. If no version was specified in the request the
     * newest one is considered the target version for this request.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param id
     *            public identifier of the object
     * @param version
     *            version number of the object - current version by default
     * @param provided
     *            whether provided metadata should be fetched
     * @param extracted
     *            whether extracted metadata should be fetched
     * @return digital object main file if it is in the (asynchronous request processing) cache or an address with the
     *         fetching status
     */
    @GET
    @Path("object/{id}/mainfile")
    public Response getMainFile(@Context UriInfo uriInfo, @PathParam("id") String id,
            @QueryParam("version") Integer version, @DefaultValue("true") @QueryParam("provided") Boolean provided,
            @DefaultValue("true") @QueryParam("extracted") Boolean extracted) {

        checkPermission(id, ObjectPermissionType.READ);

        String requestId = null;
        try {
            requestId = objectAsyncReadRequestProcessor.processGetMainFile(id, version, provided, extracted);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (AsyncReadRequestAlreadyPrepared e) {
            return buildResponse(e.getResult());
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        String location = uriInfo.getBaseUri() + ObjectManagerServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI
                + StringEncoder.encodeUrl(id) + "/mainfile/"
                + AsyncRequestServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI_STATUS + requestId;
        return Response.status(Response.Status.ACCEPTED)
                .header(AsyncRequestServiceConsts.HTTP_HEADER_LOCATION, location).build();
    }


    /**
     * Gets an metadata file of the specified object at specified version. If no version was specified in the request
     * the newest one is considered the target version for this request.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param id
     *            public identifier of the object
     * @param version
     *            version number of the object - current version by default
     * @param provided
     *            whether version's provided metadata should be included
     * @return digital object metadata if found in the (asynchronous request processing) cache or an address with the
     *         fetching status.
     */
    @GET
    @Path("object/{id}/metadata")
    public Response getMetadata(@Context UriInfo uriInfo, @PathParam("id") String id,
            @QueryParam("version") Integer version, @DefaultValue("true") @QueryParam("provided") Boolean provided) {

        checkPermission(id, ObjectPermissionType.READ);

        String requestId = null;
        try {
            requestId = objectAsyncReadRequestProcessor.processGetMetadata(id, version, provided);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (AsyncReadRequestAlreadyPrepared e) {
            return buildResponse(e.getResult());
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        String location = uriInfo.getBaseUri() + ObjectManagerServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI
                + StringEncoder.encodeUrl(id) + "/metadata/"
                + AsyncRequestServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI_STATUS + requestId;
        return Response.status(Response.Status.ACCEPTED)
                .header(AsyncRequestServiceConsts.HTTP_HEADER_LOCATION, location).build();
    }


    /**
     * Gets the origin of the object or http 204 code if this object is not a result of any migration.
     * 
     * @param id
     *            public identifier of the object
     * @return object's origin
     * 
     */
    @GET
    @Path("object/{id}/origin")
    @Produces(MediaType.APPLICATION_XML)
    public ObjectOrigin getOrigin(@PathParam("id") String id) {

        checkPermission(id, ObjectPermissionType.READ);

        ObjectOrigin origin = null;
        try {
            origin = objectMigrationManager.getOrigin(id);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        if (origin != null) {
            return origin;
        } else {
            throw new NoContentException();
        }
    }


    /**
     * Gets the derivatives of the object or http 204 code if this object is no any derivatives.
     * 
     * @param id
     *            public identifier of the object
     * @return object's derivatives
     * 
     */
    @GET
    @Path("object/{id}/derivatives")
    @Produces(MediaType.APPLICATION_XML)
    public ObjectDerivatives getDerivatives(@PathParam("id") String id) {

        checkPermission(id, ObjectPermissionType.READ);

        ObjectDerivatives derivatives = null;
        try {
            derivatives = objectMigrationManager.getDerivatives(id);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        if (derivatives != null) {
            return derivatives;
        } else {
            throw new NoContentException();
        }
    }


    /**
     * Fetches the file list belonging to the targeted version of object or to the most current version if none was
     * specified.
     * 
     * @param id
     *            object's public identifier.
     * @param version
     *            object's version number.
     * @param provided
     *            specifies whether or not provided metadata files should be included in the list.
     * @param extracted
     *            specifies whether or not extracted metadata files should be included in the list.
     * @param hashes
     *            specifies whether or not hashes should be included
     * @param absolute
     *            specifies whether or not absolute paths should be included
     * @return list of contents of the object.
     */
    @GET
    @Path("object/{id}/list")
    @Produces(MediaType.APPLICATION_XML)
    public ObjectFiles getFilesList(@PathParam("id") String id, @QueryParam("version") Integer version,
            @DefaultValue("true") @QueryParam("provided") Boolean provided,
            @DefaultValue("true") @QueryParam("extracted") Boolean extracted,
            @DefaultValue("false") @QueryParam("hashes") Boolean hashes,
            @DefaultValue("false") @QueryParam("absolute") Boolean absolute) {

        checkPermission(id, ObjectPermissionType.READ);

        ObjectFiles objectFiles = null;
        try {
            objectFiles = objectManager.getFilesList(id, version, provided, extracted, hashes, absolute);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        if (objectFiles != null) {
            return objectFiles;
        } else {
            throw new NoContentException();
        }
    }


    /**
     * Fetches the object's versions history.
     * 
     * @param id
     *            object's public identifier.
     * @param order
     *            specifies the order of the version history, if <code>asc</code> then ascending, if <code>desc</code>
     *            then descending, otherwise always descending by default.
     * @return history of the object in specified order or descending (newest-first) if no order was specified.
     */
    @GET
    @Path("object/{id}/history")
    @Produces(MediaType.APPLICATION_XML)
    public ObjectHistory getHistory(@PathParam("id") String id, @DefaultValue("desc") @QueryParam("order") String order) {

        checkPermission(id, ObjectPermissionType.READ);

        ObjectHistory objectHistory = null;
        try {
            boolean ascending = true;
            if (order.toLowerCase().equals("desc")) {
                ascending = false;
            }
            objectHistory = objectManager.getHistory(id, ascending);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        if (objectHistory != null) {
            return objectHistory;
        } else {
            throw new NoContentException();
        }
    }


    /**
     * Checks whether the current user has the required permission to the object with the given identifier.
     * 
     * @param identifier
     *            object identifier
     * @param permission
     *            required permission
     * @throws NotFoundException
     *             if the object does not exist
     * @throws AccessDeniedException
     *             if the user does not have the permission
     */
    private void checkPermission(String identifier, ObjectPermissionType permission)
            throws NotFoundException, AccessDeniedException {

        DigitalObject object;
        try {
            object = objectBrowser.getDigitalObject(identifier);
        } catch (ObjectNotFoundException e) {
            // does not exist at all
            throw new NotFoundException();
        }

        if (object.getCurrentVersion() == null) {
            // deleted
            throw new NotFoundException();
        }

        if (!permissionManager.hasPermission(userContext.getCallerPrincipalName(), object.getId(), permission)) {
            // no permission
            throw new AccessDeniedException();
        }
    }
}
