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
package pl.psnc.synat.wrdz.zmkd.plan.execution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;

import net.java.dev.wadl._2009._02.Application;
import net.java.dev.wadl._2009._02.Method;
import net.java.dev.wadl._2009._02.Param;
import net.java.dev.wadl._2009._02.ParamStyle;
import net.java.dev.wadl._2009._02.Representation;
import net.java.dev.wadl._2009._02.Resource;
import net.java.dev.wadl._2009._02.Resources;
import net.java.dev.wadl._2009._02.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.ru.grounding.ServiceGrounder;
import pl.psnc.synat.wrdz.ru.grounding.WadlNodes;
import pl.psnc.synat.wrdz.ru.services.descriptors.TechnicalDescriptorBrowser;
import pl.psnc.synat.wrdz.zmkd.entity.plan.Delivery;
import pl.psnc.synat.wrdz.zmkd.entity.plan.Service;
import pl.psnc.synat.wrdz.zmkd.entity.plan.ServiceOutcome;
import pl.psnc.synat.wrdz.zmkd.entity.plan.ServiceParameter;
import pl.psnc.synat.wrdz.zmkd.entity.plan.Transformation;
import pl.psnc.synat.wrdz.zmkd.entity.plan.TransformationPath;
import pl.psnc.synat.wrdz.zmkd.service.HttpMethod;
import pl.psnc.synat.wrdz.zmkd.service.OutcomeStyle;
import pl.psnc.synat.wrdz.zmkd.service.RequestType;
import pl.psnc.synat.wrdz.zmkd.service.ServiceBodyParamInfo;
import pl.psnc.synat.wrdz.zmkd.service.ServiceFormParamInfo;
import pl.psnc.synat.wrdz.zmkd.service.ServiceInfo;
import pl.psnc.synat.wrdz.zmkd.service.ServiceInfoBuilder;
import pl.psnc.synat.wrdz.zmkd.service.ServiceOutcomeInfo;
import pl.psnc.synat.wrdz.zmkd.service.ServiceQueryParamInfo;
import pl.psnc.synat.wrdz.zmkd.service.ServiceTemplateParamInfo;
import pl.psnc.synat.wrdz.zmkd.wadl.WadlParserFactory;

/**
 * Parser of the transformation path and delivery.
 */
@Stateless
public class PlanExecutionParserBean implements PlanExecutionParser {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(PlanExecutionParserBean.class);

    /**
     * Technical descriptor browser.
     */
    @EJB(name = "TechnicalDescriptorBrowser")
    private TechnicalDescriptorBrowser techDescriptorBrowserBean;

    /**
     * Service grounder.
     */
    @EJB(name = "ServiceGrounder")
    private ServiceGrounder serviceGrounderBean;


    @Override
    public List<TransformationInfo> parseTransformationPath(TransformationPath transformationPath)
            throws InconsistentServiceDescriptionException {
        List<TransformationInfo> path = new ArrayList<TransformationInfo>();
        try {
            for (Transformation transformation : transformationPath.getTransformations()) {
                TransformationInfoBuilder builder = new TransformationInfoBuilder(transformation);
                path.add((TransformationInfo) parseServiceDescriptions(builder, transformation));
            }
        } catch (IOException e) {
            throw new WrdzRuntimeException("Could not retrieve a WADL", e);
        } catch (JAXBException e) {
            throw new WrdzRuntimeException("Could not parse a path", e);
        }
        return path;
    }


    @Override
    public DeliveryInfo parseDelivery(Delivery delivery)
            throws InconsistentServiceDescriptionException {
        DeliveryInfoBuilder builder = new DeliveryInfoBuilder(delivery);
        try {
            return (DeliveryInfo) parseServiceDescriptions(builder, delivery);
        } catch (IOException e) {
            throw new WrdzRuntimeException("Could not retrieve a WADL", e);
        } catch (JAXBException e) {
            throw new WrdzRuntimeException("Could not parse a delivery", e);
        }
    }


    /**
     * Parses the common part of transformation and delivery - abstract service.
     * 
     * @param builder
     *            builder of specific concrete service
     * @param service
     *            service
     * @return info about delivery
     * @throws InconsistentServiceDescriptionException
     *             when some missing or ambiguous values between OWL-S and WADL description occur
     * @throws IOException
     *             when the description cannot be retrieved
     * @throws JAXBException
     *             when the description cannot be parsed
     */
    private ServiceInfo parseServiceDescriptions(ServiceInfoBuilder<? extends ServiceInfo> builder, Service service)
            throws InconsistentServiceDescriptionException, IOException, JAXBException {
        WadlNodes wadlNodes = serviceGrounderBean.retrieveWadlNodes(service.getServiceIri());
        String location = techDescriptorBrowserBean.getLocation(service.getServiceIri(), service.getOntologyIri());
        if (!wadlNodes.getWadlUri().equals(location)) {
            throw new InconsistentServiceDescriptionException("Inconsistent data - different WADL locations "
                    + wadlNodes.getWadlUri() + " and " + location);
        }
        Application wadl = getWadl(location);
        builder.setServiceIri(service.getServiceIri());
        Map<Resource, String> serviceAddress = findServiceAddress(wadl.getResources(), location,
            wadlNodes.getResource());
        if (serviceAddress == null) {
            throw new InconsistentServiceDescriptionException("Service address (resource) node with id "
                    + wadlNodes.getResource() + " does not exist in WADL.");
        }
        Resource serviceAddressNode = serviceAddress.keySet().iterator().next();
        builder.setAddress(serviceAddress.get(serviceAddressNode));
        Map<Method, String> serviceMethod = findServiceMethod(serviceAddressNode, location, wadlNodes.getMethod());
        if (serviceMethod == null) {
            throw new InconsistentServiceDescriptionException("Service method node with id " + wadlNodes.getMethod()
                    + " does not exist in WADL.");
        }
        Method serviceMethodNode = serviceMethod.keySet().iterator().next();
        builder.setMethod(HttpMethod.valueOf(serviceMethod.get(serviceMethodNode)));
        builder.setRequestType(findServiceRequestType(serviceMethodNode));
        builder.setTemplateParams(findServiceTemplateParameters(serviceAddressNode, location,
            wadlNodes.getParameters(), service.getParameters()));
        builder.setMatrixParams(findServiceQueryParameters(serviceAddressNode, location, wadlNodes.getParameters(),
            service.getParameters()));
        builder.setQueryParams(findServiceQueryParameters(serviceMethodNode, location, wadlNodes.getParameters(),
            service.getParameters()));
        builder.setBodyParam(findServiceBodyParameter(serviceMethodNode, location, wadlNodes.getParameters(),
            service.getParameters()));
        builder.setFormParams(findServiceFormParameters(serviceMethodNode, location, wadlNodes.getParameters(),
            service.getParameters()));
        builder.setOutcomes(findServiceOutcomes(serviceMethodNode, location, wadlNodes.getOutcomes(),
            service.getOutcomes()));
        return builder.build();
    }


    /**
     * Retrieves the WADL file from the given location and parse it by JAXB.
     * 
     * @param location
     *            URI to the WADL file
     * @return parsed WADL to java object
     * @throws IOException
     *             if a communication or read/write error occurs
     * @throws JAXBException
     *             if retrieved document is not correct WADL document
     */
    private Application getWadl(String location)
            throws IOException, JAXBException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet req = new HttpGet(location);
        HttpResponse resp = httpClient.execute(req);
        if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity entity = resp.getEntity();
            if (entity != null) {
                try {
                    return WadlParserFactory.getInstance().getWadlParser().unmarshalWadl(entity.getContent());
                } finally {
                    EntityUtils.consumeQuietly(entity);
                }
            } else {
                throw new IOException("The WADL file does not exist.");
            }
        } else {
            throw new IOException("Could not retrieve the WADL file; status: " + resp.getStatusLine());
        }
    }


    /**
     * Finds among list of trees of resources the resource of the specified id and assembles the address of the service.
     * 
     * @param resources
     *            list of trees of resources in WADL file
     * @param wadlLocation
     *            location of WADL - prefix for id of WADL nodes
     * @param resourceId
     *            seeking resource id
     * @return map with the last node of address of the service (resource) or null if such does not exist
     */
    private Map<Resource, String> findServiceAddress(List<Resources> resources, String wadlLocation, String resourceId) {
        String prefixId = wadlLocation + "#";
        for (Resources resource : resources) {
            for (Resource subresource : resource.getResource()) {
                Map<Resource, String> result = findServiceAddressNode(resource.getBase(), subresource, prefixId,
                    resourceId);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }


    /**
     * Finds service address node recursively.
     * 
     * @param path
     *            current path
     * @param resource
     *            current resource
     * @param prefixId
     *            prefix for id of WADL nodes
     * @param resourceId
     *            seeking resource id
     * @return map with the last node of address of the service (resource) and this address or null
     */
    private Map<Resource, String> findServiceAddressNode(String path, Resource resource, String prefixId,
            String resourceId) {
        if (resourceId.equals(prefixId + resource.getId())) {
            Map<Resource, String> result = new HashMap<Resource, String>(1);
            result.put((Resource) resource, path + ((Resource) resource).getPath());
            return result;
        }
        for (Object subresource : resource.getMethodOrResource()) {
            if (subresource instanceof Resource) {
                Map<Resource, String> result = findServiceAddressNode(path + resource.getPath(),
                    (Resource) subresource, prefixId, resourceId);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }


    /**
     * Finds among list of method (subnodes of the passed resource node) the method of the specified id.
     * 
     * @param resource
     *            resource node
     * @param wadlLocation
     *            location of WADL - prefix for id of WADL nodes
     * @param methodId
     *            seeking method id
     * @return map with the last node of service method or null if such does not exist
     */
    private Map<Method, String> findServiceMethod(Resource resource, String wadlLocation, String methodId) {
        String prefixId = wadlLocation + "#";
        for (Object subresource : resource.getMethodOrResource()) {
            if (subresource instanceof Method) {
                if (methodId.equals(prefixId + ((Method) subresource).getId())) {
                    Map<Method, String> result = new HashMap<Method, String>(1);
                    result.put((Method) subresource, ((Method) subresource).getName());
                    return result;
                }
            }
        }
        return null;
    }


    /**
     * Finds request type by the first representation of a request.
     * 
     * @param serviceMethodNode
     *            node in WADL file where to look for data
     * @return request type
     */
    private RequestType findServiceRequestType(Method serviceMethodNode) {
        String methodName = serviceMethodNode.getName();
        if (methodName.equals("POST") || methodName.equals("PUT")) {
            Representation representation = getFirstRequestRepresentation(serviceMethodNode);
            if (representation != null) {
                if (representation.getMediaType().equals("multipart/form-data")) {
                    return RequestType.FORMDATA;
                }
                if (representation.getMediaType().equals("application/x-www-form-urlencoded")) {
                    return RequestType.FORMURLENCODED;
                }
                if (representation.getMediaType().equals("application/xml")) {
                    return RequestType.XML;
                }
                if (representation.getMediaType().equals("application/json")) {
                    return RequestType.JSON;
                }
                return RequestType.OCTETSTREAM;
            } else {
                return RequestType.NULL;
            }
        } else {
            return RequestType.NULL;
        }
    }


    /**
     * Gets the first occurrence of the representation in the method node in WADL file. It determines the type of
     * request: form (also XML and JSON in the future) or file as a body.
     * 
     * @param serviceMethodNode
     *            node in WADL file where to look for data
     * @return first representation node
     */
    private Representation getFirstRequestRepresentation(Method serviceMethodNode) {
        if (serviceMethodNode.getRequest() != null && serviceMethodNode.getRequest().getRepresentation() != null) {
            if (!serviceMethodNode.getRequest().getRepresentation().isEmpty()) {
                return serviceMethodNode.getRequest().getRepresentation().get(0);
            }
        }
        return null;
    }


    /**
     * Finds all service template parameters.
     * 
     * @param serviceAddressNode
     *            node in WADL file where to look for data
     * @param wadlLocation
     *            location of WADL - prefix for id of WADL nodes
     * @param wadlParameters
     *            mapping of id nodes in WADL file to names of OWL-S parameters
     * @param owlsParameters
     *            list of all OWL-S parameters
     * @return list of template parameters
     * @throws InconsistentServiceDescriptionException
     *             when some missing or ambiguous values between OWL-S and WADL description occur
     */
    private List<ServiceTemplateParamInfo> findServiceTemplateParameters(Resource serviceAddressNode,
            String wadlLocation, Map<String, String> wadlParameters, List<ServiceParameter> owlsParameters)
            throws InconsistentServiceDescriptionException {
        String prefixId = wadlLocation + "#";
        List<ServiceTemplateParamInfo> result = new ArrayList<ServiceTemplateParamInfo>();
        for (Param param : serviceAddressNode.getParam()) {
            if (param.getStyle().equals(ParamStyle.TEMPLATE)) {
                String name = null;
                if (param.getId() != null) {
                    name = wadlParameters.get(prefixId + param.getId());
                }
                if (name == null && param.getName() != null) {
                    name = wadlParameters.get(prefixId + param.getName());
                }
                if (name != null) {
                    result.add(parseParamAsTemplateParam(param, name, owlsParameters));
                } else {
                    throw new InconsistentServiceDescriptionException(
                            "There is no mapping in OWL-S grounding for template " + WadlToStringUtils.toString(param));
                }
            }
        }
        return result;
    }


    /**
     * Parses parameter as a template parameter.
     * 
     * @param param
     *            template parameter in WADL
     * @param name
     *            parameter name in OWL-S
     * @param owlsParameters
     *            list of all OWL-S parameters
     * @return transformation query parameter
     * @throws InconsistentServiceDescriptionException
     *             thrown when description of the parameter is inconsistent
     */
    private ServiceTemplateParamInfo parseParamAsTemplateParam(Param param, String name,
            List<ServiceParameter> owlsParameters)
            throws InconsistentServiceDescriptionException {
        ServiceTemplateParamInfo paramInfo = new ServiceTemplateParamInfo();
        paramInfo.setSemanticName(name);
        paramInfo.setName(param.getName() != null ? param.getName() : param.getId());
        if (param.isRepeating()) {
            throw new InconsistentServiceDescriptionException("Parameter " + name
                    + " is repeating --- it is impossible for template parameters.");
        }
        paramInfo.setTechnicalType(PlanExecutionParserUtils.getXSTypeAsString(param.getType()));
        boolean found = false;
        for (ServiceParameter owlsParameter : owlsParameters) {
            if (owlsParameter.getName().equals(name)) {
                if (found) {
                    throw new InconsistentServiceDescriptionException("Template param " + name + " can be only one.");
                }
                paramInfo.setSemanticType(owlsParameter.getType());
                if (owlsParameter.getBundleType() != null) {
                    throw new InconsistentServiceDescriptionException("Parameter " + name
                            + " is bundle --- it is impossible for template parameters.");
                }
                paramInfo.setValue(owlsParameter.getValue());
                found = true;
            }
        }
        if (found) {
            return paramInfo;
        } else {
            throw new InconsistentServiceDescriptionException("There is no prameter " + name + " in OWL-S description.");
        }
    }


    /**
     * Finds all service query parameters described as matrix parameters.
     * 
     * @param serviceAddressNode
     *            node in WADL file where to look for data
     * @param wadlLocation
     *            location of WADL - prefix for id of WADL nodes
     * @param wadlParameters
     *            mapping of id nodes in WADL file to names of OWL-S parameters
     * @param owlsParameters
     *            list of all OWL-S parameters
     * @return list of query parameters
     * @throws InconsistentServiceDescriptionException
     *             when some missing or ambiguous values between OWL-S and WADL description occur
     */
    private List<ServiceQueryParamInfo> findServiceQueryParameters(Resource serviceAddressNode, String wadlLocation,
            Map<String, String> wadlParameters, List<ServiceParameter> owlsParameters)
            throws InconsistentServiceDescriptionException {
        String prefixId = wadlLocation + "#";
        List<ServiceQueryParamInfo> result = new ArrayList<ServiceQueryParamInfo>();
        for (Param param : serviceAddressNode.getParam()) {
            if (param.getStyle().equals(ParamStyle.MATRIX)) {
                String name = null;
                if (param.getId() != null) {
                    name = wadlParameters.get(prefixId + param.getId());
                }
                if (name == null && param.getName() != null) {
                    name = wadlParameters.get(prefixId + param.getName());
                }
                if (name != null) {
                    result.add(parseParamAsQueryParam(param, name, owlsParameters));
                } else {
                    throw new InconsistentServiceDescriptionException(
                            "There is no mapping in OWL-S grounding for matrix " + WadlToStringUtils.toString(param));
                }
            }
        }
        return result;
    }


    /**
     * Finds all service query parameters described as query parameters.
     * 
     * @param serviceMethodNode
     *            node in WADL file where to look for data
     * @param wadlLocation
     *            location of WADL - prefix for id of WADL nodes
     * @param wadlParameters
     *            mapping of id nodes in WADL file to names of OWL-S parameters
     * @param owlsParameters
     *            list of all OWL-S parameters
     * @return list of query parameters
     * @throws InconsistentServiceDescriptionException
     *             when some missing or ambiguous values between OWL-S and WADL description occur
     */
    private List<ServiceQueryParamInfo> findServiceQueryParameters(Method serviceMethodNode, String wadlLocation,
            Map<String, String> wadlParameters, List<ServiceParameter> owlsParameters)
            throws InconsistentServiceDescriptionException {
        String prefixId = wadlLocation + "#";
        List<ServiceQueryParamInfo> result = new ArrayList<ServiceQueryParamInfo>();
        if (serviceMethodNode.getRequest() != null) {
            for (Param param : serviceMethodNode.getRequest().getParam()) {
                if (param.getStyle().equals(ParamStyle.QUERY)) {
                    String name = null;
                    if (param.getId() != null) {
                        name = wadlParameters.get(prefixId + param.getId());
                    }
                    if (name == null && param.getName() != null) {
                        name = wadlParameters.get(prefixId + param.getName());
                    }
                    if (name != null) {
                        result.add(parseParamAsQueryParam(param, name, owlsParameters));
                    } else {
                        throw new InconsistentServiceDescriptionException(
                                "There is no mapping in OWL-S grounding for matrix "
                                        + WadlToStringUtils.toString(param));
                    }
                }
            }
        }
        return result;
    }


    /**
     * Parses parameter as a query parameter.
     * 
     * @param param
     *            query parameter in WADL
     * @param name
     *            parameter name in OWL-S
     * @param owlsParameters
     *            list of all OWL-S parameters
     * @return transformation query parameter
     * @throws InconsistentServiceDescriptionException
     *             thrown when description of the parameter is inconsistent
     */
    private ServiceQueryParamInfo parseParamAsQueryParam(Param param, String name, List<ServiceParameter> owlsParameters)
            throws InconsistentServiceDescriptionException {
        ServiceQueryParamInfo paramInfo = new ServiceQueryParamInfo();
        paramInfo.setSemanticName(name);
        paramInfo.setName(param.getName() != null ? param.getName() : param.getId());
        paramInfo.setRepeating(param.isRepeating());
        paramInfo.setRequired(param.isRequired());
        paramInfo.setTechnicalType(PlanExecutionParserUtils.getXSTypeAsString(param.getType()));
        boolean found = false;
        for (ServiceParameter owlsParameter : owlsParameters) {
            if (owlsParameter.getName().equals(name)) {
                if (!found) {
                    paramInfo.setSemanticType(owlsParameter.getType());
                } else {
                    if (!paramInfo.getSemanticType().equals(owlsParameter.getType())) {
                        throw new InconsistentServiceDescriptionException("Parameter " + name
                                + " has two different semantic types.");
                    }
                }
                if (!found) {
                    paramInfo.setBundleType(owlsParameter.getBundleType());
                } else {
                    if (!paramInfo.getBundleType().equals(owlsParameter.getBundleType())) {
                        throw new InconsistentServiceDescriptionException("Parameter " + name
                                + " has two different bundle types.");
                    }
                }
                if (paramInfo.isBundle() && !paramInfo.isRepeating()) {
                    throw new InconsistentServiceDescriptionException("Parameter " + name
                            + " is bundle, but repeating is false in WADL .");
                }
                if (!paramInfo.isBundle() && paramInfo.isRepeating()) {
                    throw new InconsistentServiceDescriptionException("Parameter " + name
                            + " is individual, but repeating is true in WADL.");
                }
                if (owlsParameter.getValue() != null) {
                    paramInfo.addValue(owlsParameter.getValue());
                }
                found = true;
            }
        }
        if (found) {
            return paramInfo;
        } else {
            throw new InconsistentServiceDescriptionException("There is no prameter " + name + " in OWL-S description.");
        }
    }


    /**
     * Finds a service query parameter described as a body parameter.
     * 
     * Request can have many optional representation but we assume in our functionality that all representation can have
     * the same (semantic) type (described in WADL file by the same id which is mapped in OWL-S grounding to the same
     * OWL-S parameter). The first representation in WADL file determines what request type it is.
     * 
     * @param serviceMethodNode
     *            node in WADL file where to look for data
     * @param wadlLocation
     *            location of WADL - prefix for id of WADL nodes
     * @param wadlParameters
     *            mapping of id nodes in WADL file to names of OWL-S parameters
     * @param owlsParameters
     *            list of all OWL-S parameters
     * @return body parameter
     * @throws InconsistentServiceDescriptionException
     *             when some missing or ambiguous values between OWL-S and WADL description occur
     */
    private ServiceBodyParamInfo findServiceBodyParameter(Method serviceMethodNode, String wadlLocation,
            Map<String, String> wadlParameters, List<ServiceParameter> owlsParameters)
            throws InconsistentServiceDescriptionException {
        String prefixId = wadlLocation + "#";
        ServiceBodyParamInfo result = null;
        Representation representation = getFirstRequestRepresentation(serviceMethodNode);
        if (representation != null) {
            if (!representation.getMediaType().equals("multipart/form-data")
                    && !representation.getMediaType().equals("application/x-www-form-urlencoded")
                    && !representation.getMediaType().equals("application/xml")
                    && !representation.getMediaType().equals("application/json")) {
                String name = null;
                if (representation.getId() != null) {
                    name = wadlParameters.get(prefixId + representation.getId());
                }
                if (name == null && representation.getParam() != null && representation.getParam().get(0) != null) {
                    name = wadlParameters.get(prefixId + representation.getParam().get(0).getId());
                }
                if (name == null) {
                    name = wadlParameters.get(prefixId + representation.getParam().get(0).getName());
                }
                if (name != null) {
                    result = parseRepresentationsAsBodyParam(serviceMethodNode.getRequest().getRepresentation(), name,
                        owlsParameters);
                } else {
                    throw new InconsistentServiceDescriptionException(
                            "There is no mapping in OWL-S grounding for "
                                    + WadlToStringUtils.toString(representation)
                                    + (representation.getParam() != null && representation.getParam().get(0) != null ? " or its parameter "
                                            + WadlToStringUtils.toString(representation.getParam().get(0))
                                            : ""));
                }
            }
        }
        return result;
    }


    /**
     * Parses request representations as a body parameter.
     * 
     * @param representations
     *            list of request representations
     * @param name
     *            parameter name
     * @param owlsParameters
     *            list of all OWL-S parameters
     * @return transformation query parameter
     * @throws InconsistentServiceDescriptionException
     *             thrown when description of the parameter is inconsistent
     */
    private ServiceBodyParamInfo parseRepresentationsAsBodyParam(List<Representation> representations, String name,
            List<ServiceParameter> owlsParameters)
            throws InconsistentServiceDescriptionException {
        ServiceBodyParamInfo paramInfo = new ServiceBodyParamInfo();
        paramInfo.setSemanticName(name);
        boolean found = false;
        for (Representation representation : representations) {
            if (parseTextRepresentationAsBodyParam(representation, name, owlsParameters, paramInfo)) {
                found = true;
            }
            if (parseFileRepresentationAsBodyParam(representation, name, owlsParameters, paramInfo)) {
                found = true;
            }
        }
        if (found) {
            if (paramInfo.isBundle() && !paramInfo.getMimetypes().containsValue("application/zip")) {
                throw new InconsistentServiceDescriptionException("Parameter " + name
                        + " is bundle, but mimetype of the request's representation is not application/zip (in WADL).");
            }
            return paramInfo;
        } else {
            throw new InconsistentServiceDescriptionException("There is no prameter " + name + " in OWL-S description.");
        }
    }


    /**
     * Checks whether the request representation is a text representation and parses it as a body parameter.
     * 
     * @param representation
     *            request representations
     * @param name
     *            parameter name
     * @param owlsParameters
     *            list of all OWL-S parameters
     * @param paramInfo
     *            body param to fill with parsed values
     * @return transformation query parameter
     * @throws InconsistentServiceDescriptionException
     *             thrown when description of the parameter is inconsistent
     */
    private boolean parseTextRepresentationAsBodyParam(Representation representation, String name,
            List<ServiceParameter> owlsParameters, ServiceBodyParamInfo paramInfo)
            throws InconsistentServiceDescriptionException {
        boolean found = false;
        if (representation.getParam() != null && !representation.getParam().isEmpty()) {
            if (!representation.getMediaType().equals("text/plain")) {
                throw new InconsistentServiceDescriptionException("Error for "
                        + WadlToStringUtils.toString(representation)
                        + ", only text/plain representation can have parameters.");
            }
            if (representation.getParam().size() != 1) {
                throw new InconsistentServiceDescriptionException("Error for "
                        + WadlToStringUtils.toString(representation)
                        + ", text/plain representation can have at most 1 parameter.");
            }
            Param param = representation.getParam().get(0);
            if (param.getName() == null) {
                throw new InconsistentServiceDescriptionException("No name for " + WadlToStringUtils.toString(param));
            }
            // many types means option in this case!
            paramInfo.addTechnicalType(param.getName(), PlanExecutionParserUtils.getXSTypeAsString(param.getType()));

            for (ServiceParameter owlsParameter : owlsParameters) {
                if (owlsParameter.getName().equals(name)) {
                    if (!found) {
                        paramInfo.setSemanticType(owlsParameter.getType());
                    } else {
                        if (!paramInfo.getSemanticType().equals(owlsParameter.getType())) {
                            throw new InconsistentServiceDescriptionException("Parameter " + name
                                    + " has two different semantic types.");
                        }
                    }
                    if (!found) {
                        paramInfo.setBundleType(owlsParameter.getBundleType());
                    } else {
                        if (!paramInfo.getBundleType().equals(owlsParameter.getBundleType())) {
                            throw new InconsistentServiceDescriptionException("Parameter " + name
                                    + " has two different bundle types.");
                        }
                    }
                    if (!found) {
                        paramInfo.setValue(owlsParameter.getValue());
                    }
                    found = true;
                }
            }
        }
        return found;
    }


    /**
     * Checks whether the request representation is a file representation and parses it as a body parameter. The
     * text/plain as a whole is admissible as well.
     * 
     * @param representation
     *            request representations
     * @param name
     *            parameter name
     * @param owlsParameters
     *            list of all OWL-S parameters
     * @param paramInfo
     *            request param to fill with parsed values
     * @return transformation query parameter
     * @throws InconsistentServiceDescriptionException
     *             thrown when description of the parameter is inconsistent
     */
    private boolean parseFileRepresentationAsBodyParam(Representation representation, String name,
            List<ServiceParameter> owlsParameters, ServiceBodyParamInfo paramInfo)
            throws InconsistentServiceDescriptionException {
        boolean found = false;
        // many mimetypes means option in this case!
        if (representation.getParam() == null || representation.getParam().isEmpty()) {
            paramInfo.addMimetype(representation.getId(), representation.getMediaType());
            for (ServiceParameter owlsParameter : owlsParameters) {
                if (owlsParameter.getName().equals(name)) {
                    if (!found) {
                        paramInfo.setSemanticType(owlsParameter.getType());
                    } else {
                        if (!paramInfo.getSemanticType().equals(owlsParameter.getType())) {
                            throw new InconsistentServiceDescriptionException("Parameter " + name
                                    + " has two different semantic types.");
                        }
                    }
                    if (!found) {
                        paramInfo.setBundleType(owlsParameter.getBundleType());
                    } else {
                        if (!paramInfo.getBundleType().equals(owlsParameter.getBundleType())) {
                            throw new InconsistentServiceDescriptionException("Parameter " + name
                                    + " has two different bundle types.");
                        }
                    }
                    if (!found) {
                        paramInfo.setValue(owlsParameter.getValue());
                    }
                    found = true;
                }
            }
        }
        return found;
    }


    /**
     * Finds a service query parameter described as a request parameter.
     * 
     * Request can have many optional representation but we assume in our functionality that all representation can have
     * the same (semantic) type (described in WADL file by the same id which is mapped in OWL-S grounding to the same
     * OWL-S parameter). The first representation in WADL file determines what request type is.
     * 
     * @param serviceMethodNode
     *            node in WADL file where to look for data
     * @param wadlLocation
     *            location of WADL - prefix for id of WADL nodes
     * @param wadlParameters
     *            mapping of id nodes in WADL file to names of OWL-S parameters
     * @param owlsParameters
     *            list of all OWL-S parameters
     * @return list of form parameter
     * @throws InconsistentServiceDescriptionException
     *             when some missing or ambiguous values between OWL-S and WADL description occur
     */
    private List<ServiceFormParamInfo> findServiceFormParameters(Method serviceMethodNode, String wadlLocation,
            Map<String, String> wadlParameters, List<ServiceParameter> owlsParameters)
            throws InconsistentServiceDescriptionException {
        String prefixId = wadlLocation + "#";
        List<ServiceFormParamInfo> result = new ArrayList<ServiceFormParamInfo>();
        Representation representation = getFirstRequestRepresentation(serviceMethodNode);
        if (representation != null) {
            if (representation.getMediaType().equals("multipart/form-data")
                    || representation.getMediaType().equals("application/x-www-form-urlencoded")) { // XML and JSON in the future 
                for (Param param : representation.getParam()) {
                    String name = null;
                    if (param.getId() != null) {
                        name = wadlParameters.get(prefixId + param.getId());
                    }
                    if (name == null && param.getName() != null) {
                        name = wadlParameters.get(prefixId + param.getName());
                    }
                    if (name != null) {
                        result.add(parseParamAsFormParam(param, name, owlsParameters));
                    } else {
                        throw new InconsistentServiceDescriptionException(
                                "There is no mapping in OWL-S grounding for matrix "
                                        + WadlToStringUtils.toString(param));
                    }
                }
            }
        }
        return result;
    }


    /**
     * Parses parameter as a form parameter.
     * 
     * @param param
     *            parameter
     * @param name
     *            parameter name
     * @param owlsParameters
     *            list of all OWL-S parameters
     * @return transformation query parameter
     * @throws InconsistentServiceDescriptionException
     *             thrown when description of the parameter is inconsistent
     */
    private ServiceFormParamInfo parseParamAsFormParam(Param param, String name, List<ServiceParameter> owlsParameters)
            throws InconsistentServiceDescriptionException {
        ServiceFormParamInfo paramInfo = new ServiceFormParamInfo();
        paramInfo.setSemanticName(name);
        paramInfo.setName(param.getName() != null ? param.getName() : param.getId());
        paramInfo.setRepeating(param.isRepeating());
        paramInfo.setRequired(param.isRequired());
        String mediaType = PlanExecutionParserUtils.getMediaTypeFromAttributes(param.getOtherAttributes());
        if (mediaType != null) {
            paramInfo.setMimetype(mediaType);
        } else {
            paramInfo.setTechnicalType(PlanExecutionParserUtils.getXSTypeAsString(param.getType()));
        }
        boolean found = false;
        for (ServiceParameter owlsParameter : owlsParameters) {
            if (owlsParameter.getName().equals(name)) {
                if (!found) {
                    paramInfo.setSemanticType(owlsParameter.getType());
                } else {
                    if (!paramInfo.getSemanticType().equals(owlsParameter.getType())) {
                        throw new InconsistentServiceDescriptionException("Parameter " + name
                                + " has two different semantic types.");
                    }
                }
                if (!found) {
                    paramInfo.setBundleType(owlsParameter.getBundleType());
                } else {
                    if (!paramInfo.getBundleType().equals(owlsParameter.getBundleType())) {
                        throw new InconsistentServiceDescriptionException("Parameter " + name
                                + " has two different bundle types.");
                    }
                }
                if (paramInfo.isBundle() && !paramInfo.isRepeating()) {
                    if (paramInfo.getMimetype() == null) {
                        throw new InconsistentServiceDescriptionException("Parameter " + name
                                + " is bundle, but repeating is false in WADL - in case of non file values.");
                    } else {
                        if (!paramInfo.getMimetype().equals("application/zip")) {
                            throw new InconsistentServiceDescriptionException(
                                    "Parameter "
                                            + name
                                            + " is bundle, but repeating is false in WADL - in case of file value and mimetype different that application/zip.");
                        }
                    }
                }
                if (!paramInfo.isBundle() && paramInfo.isRepeating()) {
                    throw new InconsistentServiceDescriptionException("Parameter " + name
                            + " is individual, but repeating is true in WADL.");
                }
                if (owlsParameter.getValue() != null) {
                    paramInfo.addValue(owlsParameter.getValue());
                }
                found = true;
            }
        }
        if (found) {
            return paramInfo;
        } else {
            throw new InconsistentServiceDescriptionException("There is no prameter " + name + " in OWL-S description.");
        }
    }


    /**
     * Finds all service outcomes.
     * 
     * Result can have many optional representation but we assume in our functionality that all representation can have
     * the same (semantic) type.
     * 
     * @param serviceMethodNode
     *            node in WADL file where to look for data
     * @param wadlLocation
     *            location of WADL - prefix for id of WADL nodes
     * @param wadlOutcomes
     *            mapping of id nodes in WADL file to names of OWL-S outcomes
     * @param owlsOutcomes
     *            list of all OWL-S outcomes
     * @return List of potential outcomes
     * @throws InconsistentServiceDescriptionException
     *             when some missing or ambiguous values between OWL-S and WADL description occur
     */
    private List<ServiceOutcomeInfo> findServiceOutcomes(Method serviceMethodNode, String wadlLocation,
            Map<String, String> wadlOutcomes, List<ServiceOutcome> owlsOutcomes)
            throws InconsistentServiceDescriptionException {
        String prefixId = wadlLocation + "#";
        List<ServiceOutcomeInfo> result = new ArrayList<ServiceOutcomeInfo>();
        for (Response response : serviceMethodNode.getResponse()) {
            Set<Integer> statuses = new HashSet<Integer>();
            for (Long status : response.getStatus()) {
                statuses.add(status.intValue());
            }
            for (Param param : response.getParam()) {
                if (param.getStyle().equals(ParamStyle.HEADER)) {
                    String name = null;
                    if (param.getId() != null) {
                        name = wadlOutcomes.get(prefixId + param.getId());
                    }
                    if (name == null && param.getName() != null) {
                        name = wadlOutcomes.get(prefixId + param.getName());
                    }
                    if (name != null) {
                        result.add(parseParamAsHeaderOutcome(param, name, statuses, owlsOutcomes));
                    } else {
                        if (statuses.contains(new Integer(200))) {
                            logger.debug("There is no mapping in OWL-S grounding for header outcome " + param
                                    + ", but it is optional since status code is not 200.");
                        } else {
                            throw new InconsistentServiceDescriptionException(
                                    "There is no mapping in OWL-S grounding for header outcome " + param);
                        }
                    }
                }
            }
            String name = null;
            Representation representation = getFirstResponseRepresentation(response);
            if (representation != null) {
                name = wadlOutcomes.get(prefixId + representation.getId());
                if (name == null && representation.getParam() != null && representation.getParam().get(0) != null) {
                    name = wadlOutcomes.get(prefixId + representation.getParam().get(0).getId());
                }
                if (name == null) {
                    name = wadlOutcomes.get(prefixId + representation.getParam().get(0).getName());
                }
                if (name != null) {
                    result.add(parseRepresentationsAsBodyOutcome(response.getRepresentation(), name, statuses,
                        owlsOutcomes));
                } else {
                    throw new InconsistentServiceDescriptionException(
                            "There is no mapping in OWL-S grounding for "
                                    + WadlToStringUtils.toString(representation)
                                    + (representation.getParam() != null && representation.getParam().get(0) != null ? " or its parameter "
                                            + WadlToStringUtils.toString(representation.getParam().get(0))
                                            : ""));
                }
            }
        }
        return result;
    }


    /**
     * Gets the first occurrence of the response representation in the method node in WADL.
     * 
     * @param response
     *            node in WADL file where to look for data
     * @return first response representation node
     */
    private Representation getFirstResponseRepresentation(Response response) {
        if (response.getRepresentation() != null) {
            for (Representation representation : response.getRepresentation()) {
                return representation;
            }
        }
        return null;
    }


    /**
     * Parses parameter as a header outcome.
     * 
     * @param param
     *            parameter
     * @param name
     *            outcome name
     * @param statuses
     *            set of status codes
     * @param owlsOutcomes
     *            list of all OWL-S outcomes
     * @return transformation header outcome
     * @throws InconsistentServiceDescriptionException
     *             thrown when description of the outcome is inconsistent
     */
    private ServiceOutcomeInfo parseParamAsHeaderOutcome(Param param, String name, Set<Integer> statuses,
            List<ServiceOutcome> owlsOutcomes)
            throws InconsistentServiceDescriptionException {
        ServiceOutcomeInfo outcomeInfo = new ServiceOutcomeInfo();
        outcomeInfo.setSemanticName(name);
        outcomeInfo.setName(param.getName() != null ? param.getName() : param.getId());
        outcomeInfo.setStyle(OutcomeStyle.HEADER);
        outcomeInfo.setStatuses(statuses);
        outcomeInfo.setRequired(param.isRequired());
        outcomeInfo.setRepeating(param.isRepeating());
        outcomeInfo.addTechnicalType(param.getName(), PlanExecutionParserUtils.getXSTypeAsString(param.getType()));
        boolean found = false;
        for (ServiceOutcome owlsOutcome : owlsOutcomes) {
            if (owlsOutcome.getName().equals(name)) {
                if (found) {
                    throw new InconsistentServiceDescriptionException("Outcome " + name + " occurs more than ones.");
                }
                outcomeInfo.setSemanticType(owlsOutcome.getType());
                outcomeInfo.setBundleType(owlsOutcome.getBundleType());
                if (outcomeInfo.isBundle() && !outcomeInfo.isRepeating()) {
                    throw new InconsistentServiceDescriptionException("Outcome " + name
                            + " is bundle, but repeating is false in WADL.");
                }
                if (!outcomeInfo.isBundle() && outcomeInfo.isRepeating()) {
                    throw new InconsistentServiceDescriptionException("Outcome  " + name
                            + " is individual, but repeating is true in WADL.");
                }
                found = true;
            }
        }
        if (found) {
            return outcomeInfo;
        } else {
            throw new InconsistentServiceDescriptionException("There is no outcome " + name + " in OWL-S description.");
        }
    }


    /**
     * Parses response representations as a body outcome.
     * 
     * @param representations
     *            list of response representations
     * @param name
     *            outcome name
     * @param statuses
     *            set of status codes
     * @param owlsOutcomes
     *            list of all OWL-S outcomes
     * @return transformation body outcome
     * @throws InconsistentServiceDescriptionException
     *             thrown when description of the parameter is inconsistent
     */

    private ServiceOutcomeInfo parseRepresentationsAsBodyOutcome(List<Representation> representations, String name,
            Set<Integer> statuses, List<ServiceOutcome> owlsOutcomes)
            throws InconsistentServiceDescriptionException {
        ServiceOutcomeInfo outcomeInfo = new ServiceOutcomeInfo();
        outcomeInfo.setSemanticName(name);
        outcomeInfo.setStatuses(statuses);
        outcomeInfo.setStyle(OutcomeStyle.BODY);
        outcomeInfo.setRequired(true);
        outcomeInfo.setRepeating(false);
        boolean found = false;
        for (Representation representation : representations) {
            if (parseTextRepresentationAsBodyOutcome(representation, name, owlsOutcomes, outcomeInfo)) {
                found = true;
            }
            if (parseFileRepresentationAsBodyOutcome(representation, name, owlsOutcomes, outcomeInfo)) {
                found = true;
            }
        }
        if (found) {
            if (outcomeInfo.isBundle() && !outcomeInfo.getMimetypes().containsValue("application/zip")) {
                throw new InconsistentServiceDescriptionException("Outcome " + name
                        + " is bundle, but mimetype of the response's representation is not application/zip (in WADL).");
            }
            return outcomeInfo;
        } else {
            throw new InconsistentServiceDescriptionException("There is no outcome " + name + " in OWL-S description.");
        }
    }


    /**
     * Checks whether the response representation is a text representation and parses it as a body outcome.
     * 
     * @param representation
     *            response representations
     * @param name
     *            outcome name
     * @param owlsOutcomes
     *            list of all OWL-S outcomes
     * @param outcomeInfo
     *            outcome to fill with parsed values
     * @return transformation body outcome
     * @throws InconsistentServiceDescriptionException
     *             thrown when description of the parameter is inconsistent
     */
    private boolean parseTextRepresentationAsBodyOutcome(Representation representation, String name,
            List<ServiceOutcome> owlsOutcomes, ServiceOutcomeInfo outcomeInfo)
            throws InconsistentServiceDescriptionException {
        boolean found = false;
        if (representation.getParam() != null && !representation.getParam().isEmpty()) {
            if (!representation.getMediaType().equals("text/plain")) {
                throw new InconsistentServiceDescriptionException("Error for "
                        + WadlToStringUtils.toString(representation)
                        + ", only text/plain representation can have parameters.");
            }
            if (representation.getParam().size() != 1) {
                throw new InconsistentServiceDescriptionException("Error for "
                        + WadlToStringUtils.toString(representation)
                        + ", text/plain representation can have at most 1 parameter.");
            }
            Param param = representation.getParam().get(0);
            if (param.getName() == null) {
                throw new InconsistentServiceDescriptionException("No name for " + WadlToStringUtils.toString(param));
            }
            // many types means option in this case!
            outcomeInfo.addTechnicalType(param.getName(), PlanExecutionParserUtils.getXSTypeAsString(param.getType()));

            for (ServiceOutcome owlsOutcome : owlsOutcomes) {
                if (owlsOutcome.getName().equals(name)) {
                    if (found) {
                        // there cannot be many the same outcomes, against to params, since outcome are not definable in a migration plan
                        throw new InconsistentServiceDescriptionException("Outcome " + name + " occurs more than ones.");
                    }
                    outcomeInfo.setSemanticType(owlsOutcome.getType());
                    outcomeInfo.setBundleType(owlsOutcome.getBundleType());
                    found = true;
                }
            }
        }
        return found;
    }


    /**
     * Checks whether the response representation is a file representation and parses it as a body outcome. The
     * text/plain as a whole is admissible as well.
     * 
     * @param representation
     *            request representations
     * @param name
     *            outcome name
     * @param owlsOutcomes
     *            list of all OWL-S outcome
     * @param outcomeInfo
     *            outcome to fill with parsed values
     * @return transformation body outcome
     * @throws InconsistentServiceDescriptionException
     *             thrown when description of the parameter is inconsistent
     */
    private boolean parseFileRepresentationAsBodyOutcome(Representation representation, String name,
            List<ServiceOutcome> owlsOutcomes, ServiceOutcomeInfo outcomeInfo)
            throws InconsistentServiceDescriptionException {
        boolean found = false;
        // many file values means option in this case!
        if (representation.getParam() == null || representation.getParam().isEmpty()) {
            outcomeInfo.addMimetype(representation.getId(), representation.getMediaType());
            for (ServiceOutcome owlsOutcome : owlsOutcomes) {
                if (owlsOutcome.getName().equals(name)) {
                    if (found) {
                        // there cannot be many the same outcomes, against to params, since outcome are not definable in a migration plan
                        throw new InconsistentServiceDescriptionException("Outcome " + name + " occurs more than ones.");
                    }
                    outcomeInfo.setSemanticType(owlsOutcome.getType());
                    outcomeInfo.setBundleType(owlsOutcome.getBundleType());
                    found = true;
                }
            }
        }
        return found;
    }

}
