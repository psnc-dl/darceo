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
package pl.psnc.synat.wrdz.ru.owl;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.TechnicalDescriptorSchemeDao;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.TechnicalDescriptorSchemeFilterFactory;
import pl.psnc.synat.wrdz.ru.entity.services.DataManipulationService;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.TechnicalDescriptor;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.TechnicalDescriptorScheme;
import pl.psnc.synat.wrdz.ru.exceptions.EntryCreationException;

/**
 * Provides convenient method for constructing technical descriptors subtree along with data manipulation services as
 * leaves.
 */
@Stateless
public class TechnicalDescriptorConstructorBean implements TechnicalDescriptorConstructor {

    /**
     * WADL grounding scheme prefix.
     */
    private static final String WADL_GROUNDING_PREFIX = "http://www.fullsemanticweb.com/ontology/RESTfulGrounding/v1.0/RESTfulGrounding.owl#";

    /**
     * WSDL grounding scheme prefix.
     */
    private static final String WSDL_GROUNDING_PREFIX = "http://www.daml.org/services/owl-s/1.2/Grounding.owl#";

    /**
     * Ontology manager.
     */
    @EJB
    private OntologyManager ontologyManager;

    /**
     * Technical descriptor scheme DAO for finding descriptor schemes.
     */
    @EJB
    private TechnicalDescriptorSchemeDao technicalDescriptorSchemeDao;


    @Override
    public TechnicalDescriptor construct(OWLOntology ontology, OWLIndividual technicalDesc,
            SemanticDescriptor semantic, DataManipulationService dataManipulationService)
            throws EntryCreationException {
        Set<OWLClassExpression> types = technicalDesc.getTypes(ontology);
        for (OWLClassExpression type : types) {
            String prefix = type.asOWLClass().getIRI().getStart();
            TechnicalDescriptor technical = null;
            if (WADL_GROUNDING_PREFIX.equals(prefix)) {
                TechnicalDescriptorScheme scheme = getTypeFromDB("WADL", "1.0");
                technical = createDescriptor(technicalDesc, ontology,
                    IRI.create(WADL_GROUNDING_PREFIX + "wadlDocument"), semantic);
                dataManipulationService.setLocationUrl(getServiceUrlFromWadl(ontology, technicalDesc,
                    technical.getLocationUrl()));
                technical.setType(scheme);
                return technical;
            } else if (WSDL_GROUNDING_PREFIX.equals(prefix)) {
                TechnicalDescriptorScheme scheme = getTypeFromDB("WSDL", "1.1");
                technical = createDescriptor(technicalDesc, ontology,
                    IRI.create("http://www.daml.org/services/owl-s/1.2/Grounding.owl#wsdlDocument"), semantic);
                dataManipulationService.setLocationUrl(getServiceUrlFromWsdl(ontology, technicalDesc,
                    technical.getLocationUrl()));
                technical.setType(scheme);
                return technical;
            } else {
                throw new EntryCreationException("Unrecognized technical descriptor type " + prefix);
            }
        }
        throw new EntryCreationException("Malformed semantic descriptor. No technical descriptors found.");
    }


    /**
     * Creates new technical descriptor from the given individual in the specified ontology.
     * 
     * @param technicalDesc
     *            individual from the ontology representing technical descriptor.
     * @param ontology
     *            currently browsed ontology.
     * @param propertyIri
     *            IRI of the property containing technical descriptor.
     * @param semantic
     *            constructed semantic descriptor tree root.
     * @return extracted and constructed technical descriptor.
     */
    private TechnicalDescriptor createDescriptor(OWLIndividual technicalDesc, OWLOntology ontology, IRI propertyIri,
            SemanticDescriptor semantic) {
        Set<OWLLiteral> locations = technicalDesc.getDataPropertyValues(ontologyManager.getOWLDataFactory()
                .getOWLDataProperty(propertyIri), ontology);
        TechnicalDescriptor result = new TechnicalDescriptor();
        if (locations.size() != 1) {
            throw new RuntimeException("Bad property specified or unsupported descriptor type.");
        }
        for (OWLLiteral location : locations) {
            result.setLocationUrl(location.getLiteral().trim());
            List<TechnicalDescriptor> technicalDescriptors = semantic.getTechnicalDescriptors();
            for (TechnicalDescriptor technicalDescriptor : technicalDescriptors) {
                if (technicalDescriptor.getLocationUrl().equals(location.getLiteral().trim())) {
                    return technicalDescriptor;
                }
            }
            result.setSemanticDescriptor(semantic);
        }
        semantic.getTechnicalDescriptors().add(result);
        return result;
    }


    /**
     * Finds the technical descriptor scheme matching the passed arguments.
     * 
     * @param name
     *            name of technical descriptor scheme.
     * @param version
     *            version of technical descriptor scheme.
     * @return technical descriptor scheme matching the given arguments.
     */
    private TechnicalDescriptorScheme getTypeFromDB(String name, String version) {
        TechnicalDescriptorSchemeFilterFactory filterFactory = technicalDescriptorSchemeDao.createQueryModifier()
                .getQueryFilterFactory();
        QueryFilter<TechnicalDescriptorScheme> filter = filterFactory.and(filterFactory.byName(name),
            filterFactory.byVersion(version));
        TechnicalDescriptorScheme scheme = technicalDescriptorSchemeDao.findFirstResultBy(filter);
        if (scheme == null) {
            throw new RuntimeException("Lacking enries in database, database probably corrupt.");
        }
        return scheme;
    }


    /**
     * Finds the service address in the given WSDL using the information contained in semantic descriptor.
     * 
     * @param ontology
     *            currently browsed ontology.
     * @param technicalDesc
     *            individual from the ontology representing technical descriptor.
     * @param techDescriptorUrl
     *            location of technical descriptor.
     * @return URL specifying the service address (location).
     * @throws EntryCreationException
     *             should any problems with parsing WSDL document occur.
     */
    private String getServiceUrlFromWsdl(OWLOntology ontology, OWLIndividual technicalDesc, String techDescriptorUrl)
            throws EntryCreationException {
        Set<OWLIndividual> operationRefs = technicalDesc.getObjectPropertyValues(ontologyManager.getOWLDataFactory()
                .getOWLObjectProperty(IRI.create(WSDL_GROUNDING_PREFIX + "wsdlOperation")), ontology);
        for (OWLIndividual operationRef : operationRefs) {
            Set<OWLLiteral> operations = operationRef.getDataPropertyValues(ontologyManager.getOWLDataFactory()
                    .getOWLDataProperty(IRI.create(WSDL_GROUNDING_PREFIX + "operation")), ontology);
            for (OWLLiteral operation : operations) {
                String[] split = operation.getLiteral().split("#");
                String id = split[split.length - 1].trim();
                String xpath = "//" + TechnicalNamespaceContext.WSDL_PREFIX + ":service/"
                        + TechnicalNamespaceContext.WSDL_PREFIX + ":port/" + TechnicalNamespaceContext.WSDL_SOAP_PREFIX
                        + ":address";
                NodeList nodes = getNode(techDescriptorUrl, xpath);
                String path = findOperationUrl(techDescriptorUrl, nodes, id);
                return path;
            }
        }
        throw new EntryCreationException("Could not find location of the service specified in grounding.");
    }


    /**
     * Gets URL address of a given operation from the WSDL file.
     * 
     * @param techDescriptorUrl
     *            location of technical descriptor.
     * @param nodes
     *            list of nodes browsed to find matching operation.
     * @param operationId
     *            identifier of operation specified in the grounding.
     * @return URL specifying the service address (location).
     * @throws EntryCreationException
     *             if no URL was found for the specified operation.
     */
    private String findOperationUrl(String techDescriptorUrl, NodeList nodes, String operationId)
            throws EntryCreationException {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node addressNode = nodes.item(i);
            String binding = addressNode.getParentNode().getAttributes().getNamedItem("binding").getNodeValue();
            String[] split = binding.split(":");
            binding = split[split.length - 1].trim();
            String xpath = "//" + TechnicalNamespaceContext.WSDL_PREFIX + ":binding[@name='" + binding + "']/"
                    + TechnicalNamespaceContext.WSDL_PREFIX + ":operation[@name='" + operationId + "']";
            NodeList bindings = getNode(techDescriptorUrl, xpath);
            if (bindings.getLength() == 1) {
                return addressNode.getAttributes().getNamedItem("location").getNodeValue();
            }
        }
        throw new EntryCreationException("Could not find location of the service specified in grounding.");
    }


    /**
     * Finds the service address in the given WADL using the information contained in semantic descriptor.
     * 
     * @param ontology
     *            currently browsed ontology.
     * @param technicalDesc
     *            individual from the ontology representing technical descriptor.
     * @param techDescriptorUrl
     *            location of technical descriptor.
     * @return URL specifying the service address (location).
     * @throws EntryCreationException
     *             should any problems with parsing WADL document occur.
     */
    private String getServiceUrlFromWadl(OWLOntology ontology, OWLIndividual technicalDesc, String techDescriptorUrl)
            throws EntryCreationException {
        Set<OWLIndividual> resourceMethods = technicalDesc.getObjectPropertyValues(ontologyManager.getOWLDataFactory()
                .getOWLObjectProperty(IRI.create(WADL_GROUNDING_PREFIX + "wadlResourceMethod")), ontology);
        for (OWLIndividual resourceMethod : resourceMethods) {
            Set<OWLLiteral> resources = resourceMethod.getDataPropertyValues(ontologyManager.getOWLDataFactory()
                    .getOWLDataProperty(IRI.create(WADL_GROUNDING_PREFIX + "resource")), ontology);
            for (OWLLiteral resource : resources) {
                String[] split = resource.getLiteral().split("#");
                String id = split[split.length - 1].trim();
                String xpath = "//" + TechnicalNamespaceContext.WADL_PREFIX + ":resource[@id='" + id + "']";
                NodeList nodes = getNode(techDescriptorUrl, xpath);
                String path = constructUrl(nodes.item(0));
                return path;
            }
        }
        throw new EntryCreationException("Could not find location of the service specified in grounding.");
    }


    /**
     * Performs specified XPath query on technical descriptor under the specified location.
     * 
     * @param techDescriptorUrl
     *            location of technical descriptor.
     * @param xpathExpression
     *            xpath expression to look for.
     * @return list of matching nodes.
     * @throws EntryCreationException
     *             should any errors while parsing schema occur.
     */
    private NodeList getNode(String techDescriptorUrl, String xpathExpression)
            throws EntryCreationException {
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(techDescriptorUrl);
            XPath xpath = XPathFactory.newInstance().newXPath();
            TechnicalNamespaceContext namespaceContext = new TechnicalNamespaceContext();
            xpath.setNamespaceContext(namespaceContext);
            XPathExpression expr = xpath.compile(xpathExpression);
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            return (NodeList) result;
        } catch (ParserConfigurationException e) {
            throw new EntryCreationException("Could not instantiate XML DOM parser.", e);
        } catch (SAXException e) {
            throw new EntryCreationException("Could not parse scheme correctly, probably malformed XML.", e);
        } catch (IOException e) {
            throw new EntryCreationException("Could not read schema from the given location.", e);
        } catch (XPathExpressionException e) {
            throw new WrdzRuntimeException("Invalid xpath syntax in string: " + xpathExpression, e);
        }
    }


    /**
     * Gets URL address of a given operation from the WADL file.
     * 
     * @param node
     *            currently parsed node.
     * @return URL specifying the service address (location).
     */
    private String constructUrl(Node node) {
        Node base = node.getAttributes().getNamedItem("base");
        if (base == null) {
            String result = constructUrl(node.getParentNode());
            return result + node.getAttributes().getNamedItem("path").getNodeValue();
        } else {
            return base.getNodeValue();
        }
    }

}
