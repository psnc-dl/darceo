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
package pl.psnc.synat.wrdz.ru.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import pl.psnc.synat.wrdz.common.rest.exception.BadRequestException;
import pl.psnc.synat.wrdz.ru.entity.services.DataManipulationService;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptorScheme;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.TechnicalDescriptor;
import pl.psnc.synat.wrdz.ru.exceptions.AccessDeniedException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryCreationException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryDeletionException;
import pl.psnc.synat.wrdz.ru.exceptions.IllegalRegistryOperationException;
import pl.psnc.synat.wrdz.ru.services.descriptors.SemanticDescriptorManager;

/**
 * Service providing RESTful API for services CRUD operations.
 */
@Path("/services")
@ManagedBean
public class DataManipulationServiceService {

    /**
     * Semantic descriptor manager used for operations.
     */
    @EJB
    private SemanticDescriptorManager semanticDescriptorManager;


    /**
     * Creates new services by parsing semantic descriptor describing a set of services.
     * 
     * @param params
     *            list of parameters from the form.
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void createServices(MultivaluedMap<String, String> params) {
        URI location = getLocationFromForm(params);
        boolean exposed = getExposedFromForm(params);
        try {
            semanticDescriptorManager.createDescriptor(location, exposed);
        } catch (EntryCreationException e) {
            throw new BadRequestException("Could not parse descriptor from the given URI: " + location);
        } catch (AccessDeniedException e) {
            throw new AccessDeniedException("Access denied", e);
        }
    }


    /**
     * Retrieves information about services filtered by given parameters.
     * 
     * @param exposed
     *            whether or not services are public.
     * @param local
     *            whether or not services are locally defined.
     * @return XML representation of services structure.
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public ServicesTree listServices(@QueryParam("exposed") Boolean exposed, @QueryParam("local") Boolean local) {
        ServicesTree result = new ServicesTree();
        SemanticDescs descs = new SemanticDescs();
        result.setSemanticDescriptors(descs);
        List<SemanticDesc> semanticDescriptors = descs.getSemanticDescriptor();
        List<SemanticDescriptor> descriptors = semanticDescriptorManager.retrieveActiveDescriptors(exposed, local);
        for (SemanticDescriptor descriptor : descriptors) {
            SemanticDesc semantic = new SemanticDesc();
            semantic.setLocation(descriptor.getLocationUrl());
            semantic.setOrigin(descriptor.getOrigin());
            semantic.setPublic(descriptor.isExposed());
            SemanticDescriptorScheme type = descriptor.getType();
            Scheme scheme = new Scheme();
            scheme.setName(type.getName());
            scheme.setNamespace(type.getNamespace());
            scheme.setVersion(type.getVersion());
            semantic.setScheme(scheme);
            semantic.setTechnicalDescriptors(new TechnicalDescs());
            List<TechnicalDesc> technicals = semantic.getTechnicalDescriptors().getTechnicalDescriptor();
            List<TechnicalDescriptor> technicalDescriptors = descriptor.getTechnicalDescriptors();
            for (TechnicalDescriptor technicalDescriptor : technicalDescriptors) {
                TechnicalDesc technical = new TechnicalDesc();
                technical.setLocation(technicalDescriptor.getLocationUrl());
                Scheme tscheme = new Scheme();
                tscheme.setName(technicalDescriptor.getType().getName());
                tscheme.setNamespace(technicalDescriptor.getType().getNamespace());
                tscheme.setVersion(technicalDescriptor.getType().getVersion());
                technical.setScheme(tscheme);
                technical.setServices(new Services());
                List<Service> services = technical.getServices().getService();
                List<DataManipulationService> describedServices = technicalDescriptor.getDescribedServices();
                for (DataManipulationService dataManipulationService : describedServices) {
                    Service service = new Service();
                    service.setName(dataManipulationService.getName());
                    service.setLocation(dataManipulationService.getLocationUrl());
                    service.setType(dataManipulationService.getType().name());
                    service.setDescription(dataManipulationService.getDescription());
                    services.add(service);
                }
                technicals.add(technical);
            }
            semanticDescriptors.add(semantic);
        }
        return result;
    }


    /**
     * Performs deletion of registry entries (services and technical descriptors) connected to the semantic descriptor
     * with specified <code>id</code> and marks that semantic descriptor as deleted.
     * 
     * @param id
     *            identifier of the semantic descriptor.
     */
    @DELETE
    @Path("/{id}")
    public void deleteServices(@PathParam("id") long id) {
        try {
            semanticDescriptorManager.deleteDescriptor(id);
        } catch (IllegalRegistryOperationException e) {
            throw new BadRequestException("Not local");
        } catch (EntryDeletionException e) {
            throw new BadRequestException("Not found");
        } catch (AccessDeniedException e) {
            throw new AccessDeniedException("Access denied", e);
        }

    }


    /**
     * Extracts exposed information from the form.
     * 
     * @param params
     *            form parameters from which to extract information.
     * @return extracted exposed information.
     * @throws BadRequestException
     *             if parameter has non-boolean value or more than one parameter with same name had been passed.
     */
    private boolean getExposedFromForm(MultivaluedMap<String, String> params)
            throws BadRequestException {
        List<String> list = params.get("public");
        if (list == null || list.isEmpty()) {
            return false;
        } else if (list.size() == 1) {
            String string = list.get(0).toLowerCase();
            if (Boolean.TRUE.toString().equals(string)) {
                return true;
            }
            if (Boolean.FALSE.toString().equals(string)) {
                return false;
            }
            throw new BadRequestException("Parameter public has to be either true or false.");
        }
        throw new BadRequestException("Parameter public disambiguation, more than one value specified.");
    }


    /**
     * Extracts location URI from the form.
     * 
     * @param params
     *            form parameters from which to extract location URI.
     * @return extracted URI.
     * @throws BadRequestException
     *             if URI is malformed, missing or several URIs had been passed.
     */
    private URI getLocationFromForm(MultivaluedMap<String, String> params)
            throws BadRequestException {
        List<String> list = params.get("location");
        if (list == null || list.isEmpty()) {
            throw new BadRequestException("Missing required location parameter containing URI of semantic descriptor.");
        } else if (list.size() > 1) {
            throw new BadRequestException("Required location parameter disambiguation, more than one URI specified.");
        }
        try {
            URI result = new URI(list.get(0));
            return result;
        } catch (URISyntaxException e) {
            throw new BadRequestException("Malformed URI");
        }
    }

}
