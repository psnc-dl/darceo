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
package pl.psnc.synat.wrdz.ru.registries;

import java.util.Set;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang.IncompleteArgumentException;

import pl.psnc.synat.wrdz.ru.entity.registries.RemoteRegistry;
import pl.psnc.synat.wrdz.ru.exceptions.EntryCreationException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryDeletionException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryModificationException;
import pl.psnc.synat.wrdz.ru.exceptions.UnableToParseFormException;
import pl.psnc.synat.wrdz.ru.registries.validation.groups.Creation;
import pl.psnc.synat.wrdz.ru.registries.validation.groups.Modification;

import com.sun.jersey.multipart.FormDataMultiPart;

/**
 * Service providing RESTful API for services CRUD operations.
 */
@Path("/registries")
@ManagedBean
public class RemoteRegistryService {

    /**
     * Remote registry manager for business logic operations.
     */
    @EJB
    private RemoteRegistryManager remoteRegistryManager;

    /**
     * Validator object.
     */
    @Inject
    private Validator validator;


    /**
     * Creates new remote registry.
     * 
     * @param multipart
     *            multipart form passed.
     * @return HTTP response 200 (<code>OK</code>) if method succeeds, 400 (<code>Bad Request</code>) if request is
     *         malformed or 500 (<code>Internal Server Error</code>) if some internal processing fails unexpectedly.
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createRegistry(FormDataMultiPart multipart) {
        RegistryFormParser parser = new RegistryFormParser(multipart);
        RemoteRegistry retrieved;
        try {
            RegistryFormData registryInformation = parser.getRegistryInformation();
            validateInformationCompleteness(registryInformation, Creation.class);
            retrieved = parser.buildRegistryFromInformation(registryInformation);
            remoteRegistryManager.createRemoteRegistry(retrieved,
                StringUtils.newStringUtf8(Base64.encodeBase64(registryInformation.getCertificate())));
        } catch (UnableToParseFormException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
        } catch (IncompleteArgumentException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
        } catch (EntryCreationException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
        } catch (EJBException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN)
                    .entity("Error occured while adding registry, try again.").build();
        }
        return Response.status(Status.OK).type(MediaType.TEXT_PLAIN).entity("Registry successfully created.").build();
    }


    /**
     * Modifies existing remote registry.
     * 
     * @param id
     *            identifier of the modified registry.
     * @param multipart
     *            multipart form passed.
     * @return HTTP response 200 (<code>OK</code>) if method succeeds, 400 (<code>Bad Request</code>) if request is
     *         malformed or references nonexistent object, 500 (<code>Internal Server Error</code>) if some internal
     *         processing fails unexpectedly.
     */
    @POST
    @Path("/{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response modifyRegistry(@PathParam("id") long id, FormDataMultiPart multipart) {
        RemoteRegistry modifiedRegistry = remoteRegistryManager.retrieveRemoteRegistry(id);
        if (modifiedRegistry == null) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN)
                    .entity("Cannot modify nonexistent registry.").build();
        }
        RegistryFormParser parser = new RegistryFormParser(modifiedRegistry, multipart);
        RemoteRegistry retrieved;
        try {
            RegistryFormData registryInformation = parser.getRegistryInformation();
            validateInformationCompleteness(registryInformation, Modification.class);
            retrieved = parser.buildRegistryFromInformation(registryInformation);
            remoteRegistryManager.updateRemoteRegistry(retrieved,
                StringUtils.newStringUtf8(Base64.encodeBase64(registryInformation.getCertificate())));
        } catch (UnableToParseFormException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
        } catch (IncompleteArgumentException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
        } catch (EntryModificationException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
        } catch (EJBException e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.TEXT_PLAIN)
                    .entity("Error occured while modifying registry, try again.").build();
        }
        return Response.status(Status.OK).type(MediaType.TEXT_PLAIN).entity("Registry successfully modified.").build();
    }


    /**
     * Performs validation of extracted registry information in context of given operation.
     * 
     * @param registryInformation
     *            validateg information.
     * @param operation
     *            validation group specifying operation.
     * @throws IncompleteArgumentException
     *             should validated registry information be incomplete for specified operation.
     */
    @SuppressWarnings("rawtypes")
    private void validateInformationCompleteness(RegistryFormData registryInformation, Class operation)
            throws IncompleteArgumentException {
        Set<ConstraintViolation<RegistryFormData>> validate = validator.validate(registryInformation, operation);
        if (validate.size() > 0) {
            throw new IllegalArgumentException("Request is missing required data.");
        }
    }


    /**
     * Deletes the specified remote registry.
     * 
     * @param id
     *            identifier of the deleted remote registry.
     * @return HTTP response 200 (<code>OK</code>) if method succeeds or 400 (<code>Bad Request</code>) if request
     *         references nonexistent object.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteRegistry(@PathParam("id") long id) {
        try {
            remoteRegistryManager.deleteRemoteRegistry(id);
        } catch (EntryDeletionException e) {
            return Response.status(Status.BAD_REQUEST).type(MediaType.TEXT_PLAIN).entity(e.getMessage()).build();
        }
        return Response.status(Status.OK).type(MediaType.TEXT_PLAIN).entity("Registry successfully deleted.").build();
    }

}
