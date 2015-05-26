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
package pl.psnc.synat.wrdz.zmd.scape;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.async.AsyncReadRequestAlreadyPrepared;
import pl.psnc.synat.wrdz.common.async.AsyncRequestProcessor;
import pl.psnc.synat.wrdz.common.async.AsyncRequestService;
import pl.psnc.synat.wrdz.common.async.AsyncRequestServiceConsts;
import pl.psnc.synat.wrdz.common.config.Configuration;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataProcessingException;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataReader;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataReaderFactory;
import pl.psnc.synat.wrdz.common.rest.exception.AccessDeniedException;
import pl.psnc.synat.wrdz.common.rest.exception.BadRequestException;
import pl.psnc.synat.wrdz.common.rest.exception.InternalServerErrorException;
import pl.psnc.synat.wrdz.common.rest.exception.NoContentException;
import pl.psnc.synat.wrdz.common.rest.exception.NotFoundException;
import pl.psnc.synat.wrdz.common.rest.exception.UnsupportedMediaType;
import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.common.utility.StringEncoder;
import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.entity.IntellectualEntityCollection;
import pl.psnc.synat.wrdz.zmd.entity.LifeCycleStatesCollection;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;
import pl.psnc.synat.wrdz.zmd.input.IncompleteDataException;
import pl.psnc.synat.wrdz.zmd.input.InvalidDataException;
import pl.psnc.synat.wrdz.zmd.input.object.FileFetchingRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;
import pl.psnc.synat.wrdz.zmd.object.FetchingException;
import pl.psnc.synat.wrdz.zmd.object.ObjectBrowser;
import pl.psnc.synat.wrdz.zmd.object.ObjectChecker;
import pl.psnc.synat.wrdz.zmd.object.ObjectCreationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectHistory;
import pl.psnc.synat.wrdz.zmd.object.ObjectManager;
import pl.psnc.synat.wrdz.zmd.object.ObjectModificationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectNotFoundException;
import pl.psnc.synat.wrdz.zmd.object.async.ObjectAsyncReadRequestProcessor;
import pl.psnc.synat.wrdz.zmd.object.async.ObjectAsyncRequestEnum;
import pl.psnc.synat.wrdz.zmd.object.migration.ObjectMigrationManager;
import pl.psnc.synat.wrdz.zmd.object.validators.ObjectCreationValidator;
import pl.psnc.synat.wrdz.zmd.object.validators.ObjectModificationValidator;
import pl.psnc.synat.wrdz.zmd.output.ResultFile;
import pl.psnc.synat.wrdz.zmd.scape.parser.EntityCreationParser;
import pl.psnc.synat.wrdz.zmd.scape.parser.EntityModificationParser;
import pl.psnc.synat.wrdz.zu.permission.ObjectPermissionManager;
import pl.psnc.synat.wrdz.zu.types.ObjectPermissionType;

/**
 * Entity manager service. It accepts requests concerning basic operations on digital object, like: creating,
 * retrieving, updating and removing.
 */
@Path("/")
@ManagedBean
public class EntityManagerService extends AsyncRequestService {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(EntityManagerService.class);

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
     * Gets the specified version of the entity. Return METS representation of the entity.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param eid
     *            entity id
     * @param vid
     *            version id
     * @param useReferences
     *            determines if references to metadata files should be used in returned METS file.
     * @return digital object specified files if it is in the (asynchronous request processing) cache or an address with
     *         the fetching status
     */
    @GET
    @Path("entity/{eid}/{vid}")
    public Response getEntityVersion(@Context UriInfo uriInfo, @PathParam("eid") String eid,
            @PathParam("vid") Integer vid, @DefaultValue("true") @QueryParam("useReferences") Boolean useReferences) {
        checkPermission(eid, ObjectPermissionType.READ);
        String resultMets = getMetsForEntity(eid, vid, useReferences);
        //TODO TH
        //try change to input stream
        return Response.status(Response.Status.OK).type("text/xml").entity(resultMets).build();
    }


    /**
     * Gets the specified metadata section of the entity. Return XML representation of the section.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param eid
     *            entity id
     * @param mid
     *            metadata section id
     * @return metadata section in xml
     */
    @GET
    @Path("metadata/{eid}/{mid}")
    public Response getMetadataSection(@Context UriInfo uriInfo, @PathParam("eid") String eid,
            @PathParam("mid") String mid) {
        checkPermission(eid, ObjectPermissionType.READ);
        InputStream result = null;
        try {
            //read mets file from storage and return content
            result = objectManager.getMetadataSectionById(eid, mid);
        } catch (FetchingException e) {
            return Response.status(Response.Status.NOT_FOUND).type("text/xml").build();
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        return Response.status(Response.Status.OK).type("text/xml").entity(result).build();
    }


    /**
     * Gets the specified metadata section of the entity. Return XML representation of the section.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param eid
     *            entity id
     * @param vid
     *            version id
     * @param mid
     *            metadata section id
     * @return metadata section in xml
     */
    @GET
    @Path("metadata/{eid}/{vid}/{mid}")
    public Response getMetadataSection(@Context UriInfo uriInfo, @PathParam("eid") String eid,
            @PathParam("eid") Integer vid, @PathParam("mid") String mid) {
        checkPermission(eid, ObjectPermissionType.READ);
        InputStream result = null;
        try {
            //read mets file from storage and return content
            result = objectManager.getMetadataSectionById(eid, mid, vid);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        return Response.status(Response.Status.OK).type("text/xml").entity(result).build();
    }


    /**
     * Gets the specified metadata section of the entity. Return XML representation of the section.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param eid
     *            entity id
     * @param vid
     *            version id
     * @param fid
     *            version id
     * @param mid
     *            metadata section id
     * @return metadata section in xml
     */
    @GET
    @Path("metadata/{eid}/{fid}/{vid}/{mid}")
    public Response getMetadataSection(@Context UriInfo uriInfo, @PathParam("eid") String eid,
            @PathParam("fid") String fid, @PathParam("eid") Integer vid, @PathParam("mid") String mid) {
        checkPermission(eid, ObjectPermissionType.READ);
        InputStream result = null;
        try {
            //read mets file from storage and return content
            result = objectManager.getMetadataSectionById(eid, mid, vid, fid);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        return Response.status(Response.Status.OK).type("text/xml").entity(result).build();
    }


    /**
     * Gets the set of entities specified in list. Return METS representations of the entity.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @return digital object specified files if it is in the (asynchronous request processing) cache or an address with
     *         the fetching status
     */
    @GET
    @Path("entity-list")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_XML)
    public IntellectualEntityCollection getEntityList(@Context UriInfo uriInfo, String message) {
        List<String> list = new LinkedList<String>();
        list.addAll(Arrays.asList(message.split("\\r?\\n")));

        IntellectualEntityCollection resultCollection = new IntellectualEntityCollection();
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String line = (String) it.next();
            String[] parsedLine = parseEntityListLine(line);
            String eid = parsedLine[1];
            Integer vid = Integer.parseInt(parsedLine[2]);
            boolean useReferences = Boolean.parseBoolean(parsedLine[3]);

            checkPermission(eid, ObjectPermissionType.READ);

            String resultMets = null;
            try {
                if (vid == -1) {
                    vid = (int) (objectBrowser.getDigitalObject(eid).getCurrentVersion().getId());
                }
                if (useReferences) {
                    //read mets file from storage and return content
                    resultMets = objectManager.getMetsForObject(eid, vid);
                } else {
                    //get last operation for selected version
                    ContentVersion objectVersion = objectBrowser.getObjectsVersion(eid, vid);
                    List<Operation> listOp = objectVersion.getExtractedMetadata().getMetadataContent();
                    Operation lastOperation = listOp.get(listOp.size() - 1);
                    resultMets = lastOperation.getContents();
                }

            } catch (ObjectNotFoundException e) {
                throw new NotFoundException(e.getMessage());
            } catch (Exception e) {
                logger.error("Internal Server Error", e);
                throw new InternalServerErrorException(e.getMessage());
            }

            MetsMetadataReader metsReader = MetsMetadataReaderFactory.getInstance().getMetsMetadataReader();
            try {
                metsReader.parse(new ByteArrayInputStream(resultMets.getBytes("UTF-8")));
            } catch (MetsMetadataProcessingException e) {
                logger.error("[METS parsing error!]", e.toString());
                throw new InternalServerErrorException("[METS parsing error!] " + e.getMessage());
            } catch (UnsupportedEncodingException e1) {
                logger.error("[METS parsing error!] " + e1.toString());
                throw new InternalServerErrorException("[METS parsing error!] " + e1.getMessage());
            }
            resultCollection.getMets().add(metsReader.getMets());
        }
        return resultCollection;
    }


    /**
     * Accepts request for creating an entity consisting of given data files and metadata files - based on METS.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param payload
     *            bytes of the METS file with the entity
     * @return HTTP code 202 if request was accepted or another code saying what was wrong
     */
    @POST
    @Path("entity")
    @Consumes(MediaType.APPLICATION_XML)
    public Response createEntity(@Context UriInfo uriInfo, InputStream payload) {

        if (!permissionManager.hasPermission(userContext.getCallerPrincipalName(), null, ObjectPermissionType.CREATE)) {
            throw new AccessDeniedException();
        }

        ObjectCreationRequest request = null;
        try {
            EntityCreationParser parser = new EntityCreationParser(zmdConfig.getCacheHome() + "/" + UUID.randomUUID());
            logger.debug("New request: " + request);
            request = parser.parse(payload);
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
        String entityId = "";
        try {
            entityId = objectManager.createObject(request);
        } catch (ObjectCreationException e) {
            throw new UnsupportedMediaType();
        }

        return Response.status(Response.Status.OK).type("text/plain").entity(entityId).build();
    }


    /**
     * Accepts request for creating an entity consisting of given data files and metadata files - based on METS.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param payload
     *            bytes of the METS file with the entity
     * @return HTTP code 202 if request was accepted or another code saying what was wrong
     */
    @POST
    @Path("entity-async")
    @Consumes(MediaType.APPLICATION_XML)
    public Response createObjectAsynch(@Context UriInfo uriInfo, InputStream payload) {

        if (!permissionManager.hasPermission(userContext.getCallerPrincipalName(), null, ObjectPermissionType.CREATE)) {
            throw new AccessDeniedException();
        }

        ObjectCreationRequest request = null;
        try {
            EntityCreationParser parser = new EntityCreationParser(zmdConfig.getCacheHome() + "/" + UUID.randomUUID());
            logger.debug("New request: " + request);
            request = parser.parse(payload);
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

        Response result = processEntityCreationRequest(uriInfo, request);
        return result;
    }


    /**
     * Processes a request for entity creation.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param request
     *            creation request
     * @return response
     */
    private Response processEntityCreationRequest(UriInfo uriInfo, ObjectCreationRequest request) {
        try {
            String requestId = asyncRequestProcessor.processRequestAsynchronously(ObjectAsyncRequestEnum.CREATE_OBJECT,
                request);
            String location = uriInfo.getBaseUri() + EntityManagerServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI
                    + AsyncRequestServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI_STATUS + requestId;
            return Response.status(Response.Status.ACCEPTED)
                    .header(AsyncRequestServiceConsts.HTTP_HEADER_LOCATION, location).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    /**
     * Accepts request for modifying an entity at given identifier, data files and metadata files (it creates new
     * version of entity). It also takes some optional parameters like type of the digital object, main file, origin and
     * derivatives. Files and metadata files can be passed in two ways: directly in a part of the body of this request,
     * and indirectly by a URL to the remote file.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param id
     *            public identifier of the object
     * @param payload
     *            parameters of digital object encoded by application/xml (METS format)
     * @return HTTP code 202 if request was accepted or another code saying what was wrong
     */
    @POST
    @Path("entity/{id}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response modifyObject(@Context UriInfo uriInfo, @PathParam("id") String id, InputStream payload) {

        checkPermission(id, ObjectPermissionType.UPDATE);

        ObjectModificationRequest request = null;
        try {
            EntityModificationParser parser = new EntityModificationParser(zmdConfig.getCacheHome() + "/"
                    + UUID.randomUUID());
            request = parser.parse(payload, id);
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

        return processEntityModificationRequest(uriInfo, request);
    }


    /**
     * Accepts request for modifying an entity at given identifier, data files and metadata files (it creates new
     * version of entity). It also takes some optional parameters like type of the digital object, main file, origin and
     * derivatives. Files and metadata files can be passed in two ways: directly in a part of the body of this request,
     * and indirectly by a URL to the remote file.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param id
     *            public identifier of the object
     * @param payload
     *            parameters of digital object encoded by application/xml (METS format)
     * @return HTTP code 202 if request was accepted or another code saying what was wrong
     */
    @PUT
    @Path("entity/{id}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response modifyObjectSynch(@Context UriInfo uriInfo, @PathParam("id") String id, InputStream payload) {

        checkPermission(id, ObjectPermissionType.UPDATE);

        ObjectModificationRequest request = null;
        int version = -1;
        try {
            EntityModificationParser parser = new EntityModificationParser(zmdConfig.getCacheHome() + "/"
                    + UUID.randomUUID());
            request = parser.parse(payload, id);
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
        try {
            version = objectManager.modifyObject(request);
        } catch (ObjectModificationException e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }

        Response.status(Response.Status.ACCEPTED).type("text/plain").entity(version).build();
        return processEntityModificationRequest(uriInfo, request);
    }


    /**
     * Processes a request for entity modification.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param request
     *            request
     * @return response
     */
    private Response processEntityModificationRequest(UriInfo uriInfo, ObjectModificationRequest request) {
        try {
            String requestId = asyncRequestProcessor.processRequestAsynchronously(ObjectAsyncRequestEnum.MODIFY_OBJECT,
                request);
            String location = uriInfo.getBaseUri() + EntityManagerServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI
                    + AsyncRequestServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI_STATUS + requestId;
            return Response.status(Response.Status.ACCEPTED)
                    .header(AsyncRequestServiceConsts.HTTP_HEADER_LOCATION, location).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    /**
     * Gets an entity specified files.
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
    @Path("entity/{id}")
    public Response getEntity(@Context UriInfo uriInfo, @PathParam("id") String id,
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
        String location = uriInfo.getBaseUri() + EntityManagerServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI
                + StringEncoder.encodeUrl(id) + "/"
                + AsyncRequestServiceConsts.ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI_STATUS + requestId;
        return Response.status(Response.Status.ACCEPTED)
                .header(AsyncRequestServiceConsts.HTTP_HEADER_LOCATION, location).build();
    }


    /**
     * Gets an entity specified files.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param eid
     *            entity identifier
     * @param fid
     *            file identifier
     * @return digital object specified files if it is in the (asynchronous request processing) cache or an address with
     *         the fetching status
     */
    @GET
    @Path("file/{eid}/rep1/{fid}")
    public Response getEntityFiles(@Context UriInfo uriInfo, @PathParam("eid") String eid, @PathParam("fid") String fid) {
        ResultFile res = getEntityFile(eid, fid, null);

        return Response.status(Response.Status.ACCEPTED).type("application/octet-stream").entity(res).build();
    }


    /**
     * Gets an entity specified files if it is in the cache of asynchronous request processing, or fetches it from data
     * storage otherwise. In this latter case it accepts request and returns an address with the fetching status.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param eid
     *            entity identifier
     * @param fid
     *            file identifier
     * @param vid
     *            version number of the object - current version by default
     * @return digital object specified files if it is in the (asynchronous request processing) cache or an address with
     *         the fetching status
     */
    @GET
    @Path("file/{eid}/rep1/{fid}/{vid}")
    public Response getEntityFiles(@Context UriInfo uriInfo, @PathParam("eid") String eid,
            @PathParam("fid") String fid, @PathParam("vid") Integer vid) {
        ResultFile res = getEntityFile(eid, fid, vid);

        return Response.status(Response.Status.ACCEPTED).type("application/octet-stream").entity(res).build();
    }


    /**
     * Fetches the entity's versions history.
     * 
     * @param id
     *            entity's public identifier.
     * @param order
     *            specifies the order of the version history, if <code>asc</code> then ascending, if <code>desc</code>
     *            then descending, otherwise always descending by default.
     * @return history of the object in specified order or descending (newest-first) if no order was specified.
     */
    @GET
    @Path("entity-version-list/{id}")
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
     * Fetches the entity life-cycle.
     * 
     * @param id
     *            entity's public identifier.
     * @param order
     *            specifies the order of the version history, if <code>asc</code> then ascending, if <code>desc</code>
     *            then descending, otherwise always descending by default.
     * @return list of life-cycle states.
     */
    @GET
    @Path("lifecyle/{eid}")
    @Produces(MediaType.APPLICATION_XML)
    public LifeCycleStatesCollection getEntityLifeCycleStates(@PathParam("eid") String eid) {

        checkPermission(eid, ObjectPermissionType.READ);

        LifeCycleStatesCollection states = null;
        try {
            List<Operation> listOp = new LinkedList<Operation>();
            //get operation list for entity
            List<ContentVersion> contentVersions = objectBrowser.getContentVersions(eid, true);
            if (contentVersions != null) {
                Iterator<ContentVersion> it = contentVersions.iterator();
                while (it.hasNext()) {
                    ContentVersion contentVersion = (ContentVersion) it.next();
                    listOp.addAll(contentVersion.getExtractedMetadata().getMetadataContent());
                }
            }
            //ContentVersion objectVersion = objectBrowser.getObjectsVersion(eid, null);
            states = new LifeCycleStatesCollection();
            states.parseOperationsToLifeCycleStates(listOp, eid);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException();
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        if (states != null) {
            return states;
        } else {
            throw new NoContentException();
        }
    }


    /**
     * Checks whether the current user has the required permission to the object with the given identifier.
     * 
     * @param line
     *            line with API URL to Intellectual Entity
     * @return res array of paramaters values
     */
    private String[] parseEntityListLine(String line) {
        String[] tmp = line.split("\\|\\?");
        //set default values of parameters
        String[] res = { "", null, "-1", "yes" };

        //read parameters
        int ind = 0;
        while (ind < tmp.length) {
            res[ind++] = tmp[ind];
        }
        return res;
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


    /**
     * Get METS representation of Intellectual Entity.
     * 
     * @param eid
     *            id of IE
     * @param vid
     *            version ID
     * @param useReferences
     *            determines when references are used in METS
     * @throws NotFoundException
     *             if the object does not exist
     * @return mets representation of IE
     */
    private String getMetsForEntity(String eid, Integer vid, Boolean useReferences)
            throws NotFoundException {
        checkPermission(eid, ObjectPermissionType.READ);
        String resultMets = null;
        try {
            if (useReferences) {
                //read mets file from storage and return content
                resultMets = objectManager.getMetsForObject(eid, vid);
            } else {
                //get last operation for selected version
                ContentVersion objectVersion = objectBrowser.getObjectsVersion(eid, vid);
                List<Operation> list = objectVersion.getExtractedMetadata().getMetadataContent();
                Operation lastOperation = list.get(list.size() - 1);
                resultMets = lastOperation.getContents();
            }

        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        return resultMets;
    }


    /**
     * Accepts request for modifying an entity at given identifier, data files and metadata files (it creates new
     * version of entity). It also takes some optional parameters like type of the digital object, main file, origin and
     * derivatives. Files and metadata files can be passed in two ways: directly in a part of the body of this request,
     * and indirectly by a URL to the remote file.
     * 
     * @param uriInfo
     *            provides information about requested URI
     * @param eid
     *            public identifier of the entity
     * @param mid
     *            identifier of the metadata section
     * @param payload
     *            parameters of digital object encoded by application/xml (METS format)
     * @return HTTP code 202 if request was accepted or another code saying what was wrong
     */
    @PUT
    @Path("entity/{eid}/{mid}")
    @Consumes(MediaType.APPLICATION_XML)
    public Response modifyEntityMetadata(@Context UriInfo uriInfo, @PathParam("eid") String eid,
            @PathParam("mid") String mid, InputStream payload) {

        checkPermission(eid, ObjectPermissionType.UPDATE);

        ObjectModificationRequest request = null;
        int version = -1;
        try {
            EntityModificationParser parser = new EntityModificationParser(zmdConfig.getCacheHome() + "/"
                    + UUID.randomUUID());
            request = parser.parse(payload, eid, mid);

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
        try {
            version = objectManager.modifyObject(request);
        } catch (ObjectModificationException e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }

        Response.status(Response.Status.ACCEPTED).type("text/plain").entity(version).build();
        return processEntityModificationRequest(uriInfo, request);
    }


    /**
     * Get file representation of Intellectual Entity.
     * 
     * @param eid
     *            id of IE
     * @param fid
     *            file ID
     * @param vid
     *            version id
     * @throws NotFoundException
     *             if the object does not exist
     * @return mets representation of IE
     */
    private ResultFile getEntityFile(String eid, String fid, Integer vid)
            throws NotFoundException {
        checkPermission(eid, ObjectPermissionType.READ);
        ResultFile res = null;
        Map<String, String> filesIdLocMap = new HashMap<String, String>();
        try {
            //read mets file from storage and return mapping extracted from content
            filesIdLocMap = objectManager.getFilesIdLocationMap(eid, vid);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }

        try {
            List<String> files = new LinkedList<String>();
            if (filesIdLocMap.containsKey(fid)) {
                files.add(filesIdLocMap.get(fid));
            } else {
                logger.error("Not found file with such ID: " + fid);
                throw new NotFoundException("Not found file with such ID: " + fid);
            }
            FileFetchingRequest request = new FileFetchingRequest(eid, files);
            request.setVersion(vid);
            request.setExtracted(false);
            request.setProvided(false);
            res = objectManager.getContentFiles(request);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (Exception e) {
            logger.error("Internal Server Error", e);
            throw new InternalServerErrorException(e.getMessage());
        }
        return res;
    }
}
