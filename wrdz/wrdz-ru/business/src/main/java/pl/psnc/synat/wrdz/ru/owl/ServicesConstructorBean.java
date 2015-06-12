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

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.ru.dao.services.descriptors.SemanticDescriptorSchemeDao;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.SemanticDescriptorSchemeFilterFactory;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.TechnicalDescriptorDao;
import pl.psnc.synat.wrdz.ru.entity.services.DataManipulationService;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptorScheme;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.TechnicalDescriptor;
import pl.psnc.synat.wrdz.ru.entity.types.ServiceType;
import pl.psnc.synat.wrdz.ru.exceptions.EntryCreationException;

/**
 * Bean providing convenient API for constructing services out of semantic descriptor.
 */
@Stateless
public class ServicesConstructorBean implements ServicesConstructor {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ServicesConstructorBean.class);

    /**
     * Ontology manager.
     */
    @EJB
    private OntologyManager ontologyManager;

    /**
     * Technical descriptor constructor.
     */
    @EJB
    private TechnicalDescriptorConstructor technicalDescriptorConstructor;

    /**
     * Technical descriptor DAO.
     */
    @EJB
    private TechnicalDescriptorDao technicalDescriptorDao;

    /**
     * Semantic descriptor scheme DAO.
     */
    @EJB
    private SemanticDescriptorSchemeDao semanticDescriptorSchemeDao;


    @Override
    public RdfSemanticDescriptor extractInformation(String descriptorUri, SemanticDescriptor semantic)
            throws EntryCreationException {
        OWLOntology ontology = null;
        boolean isModified = semantic.getId() != 0;
        try {
            ontology = ontologyManager.loadOntology(IRI.create(descriptorUri));
            OWLClass serviceClass = ontologyManager.getOWLDataFactory().getOWLClass(SERVICE_CLASS_IRI);
            Set<OWLIndividual> services = serviceClass.getIndividuals(ontology);
            semantic.setType(getType(descriptorUri));
            semantic.setLocationUrl(descriptorUri);
            semantic.setContext(ontology.getOntologyID().getOntologyIRI().toURI().toString());
            List<DataManipulationService> dataManipulationServices = new ArrayList<DataManipulationService>();
            for (OWLIndividual service : services) {
                DataManipulationService dataManipulationService = extractService(service, ontology);
                dataManipulationService.setSemanticDescriptor(semantic);
                Set<OWLIndividual> groundings = service.getObjectPropertyValues(ontologyManager.getOWLDataFactory()
                        .getOWLObjectProperty(SUPPORTS_PROPERTY_IRI), ontology);
                for (OWLIndividual grounding : groundings) {
                    Set<OWLIndividual> technicalDescs = extractTechnicalDescriptors(grounding);
                    for (OWLIndividual technicalDesc : technicalDescs) {
                        TechnicalDescriptor technical = extractTechnicalDescriptor(semantic, dataManipulationService,
                            technicalDesc);
                        if (isModified) {
                            technicalDescriptorDao.persist(technical);
                        }
                    }
                }
                dataManipulationServices.add(dataManipulationService);
            }
            semantic.getDescribedServices().addAll(dataManipulationServices);

            return getRdfDataOfSemanticDescriptor(semantic, ontology);

        } catch (OWLOntologyCreationException e) {
            logger.error("Error caught", e);
            throw new EntryCreationException("Could not load ontology from the document under specified URI.", e);
        } finally {
            if (ontology != null) {
                ontologyManager.removeLoadedOntologies(ontology);
            }
        }
    }


    /**
     * Method finding descriptor scheme of semantic descriptor.
     * 
     * @param descriptorUri
     *            semantic descriptor URI.
     * @return semantic descriptor scheme of a passed descriptor.
     */
    private SemanticDescriptorScheme getType(String descriptorUri) {
        SemanticDescriptorSchemeFilterFactory filterFactory = semanticDescriptorSchemeDao.createQueryModifier()
                .getQueryFilterFactory();
        // dummy method, only OWL-S 1.2 schemes are accepted.
        return semanticDescriptorSchemeDao.findFirstResultBy(filterFactory
                .byNamespace("http://www.daml.org/services/owl-s/1.2/Service.owl"));
    }


    /**
     * Gets the technical descriptor and data manipulation service information from the ontology, constructs the
     * technical descriptor object and links it to semantic descriptor.
     * 
     * @param semantic
     *            semantic descriptor.
     * @param dataManipulationService
     *            data manipulation service object.
     * @param technicalDesc
     *            technical descriptor individual found in ontology.
     * @return extracted technical descriptor.
     * @throws EntryCreationException
     *             should any problem with information extraction occur.
     */
    private TechnicalDescriptor extractTechnicalDescriptor(SemanticDescriptor semantic,
            DataManipulationService dataManipulationService, OWLIndividual technicalDesc)
            throws EntryCreationException {
        String technicalPath = technicalDesc.asOWLNamedIndividual().getIRI().getStart();
        technicalPath = technicalPath.substring(0, technicalPath.length() - 1);
        TechnicalDescriptor technical = technicalDescriptorConstructor.construct(
            ontologyManager.getOntology(IRI.create(technicalPath)), technicalDesc, semantic, dataManipulationService);
        technical.getDescribedServices().add(dataManipulationService);
        dataManipulationService.setTechnicalDescriptor(technical);
        return technical;
    }


    /**
     * Extracts data manipulation service information from the ontology.
     * 
     * @param service
     *            service individual found in ontology.
     * @param ontology
     *            searched ontology.
     * @return extracted data manipulation service.
     * @throws EntryCreationException
     *             should any problems with extraction of data manipulation service information occur.
     */
    private DataManipulationService extractService(OWLIndividual service, OWLOntology ontology)
            throws EntryCreationException {
        DataManipulationService dataManipulationService = new DataManipulationService();
        Set<OWLIndividual> profiles = service.getObjectPropertyValues(ontologyManager.getOWLDataFactory()
                .getOWLObjectProperty(PRESENTS_PROPERTY_IRI), ontology);
        for (OWLIndividual profile : profiles) {
            String profilePath = profile.asOWLNamedIndividual().getIRI().getStart();
            profilePath = profilePath.substring(0, profilePath.length() - 1);
            OWLOntology profileOntology = ontologyManager.getOntology(IRI.create(profilePath));
            dataManipulationService.setIri(extractServiceIri(service));
            dataManipulationService.setName(extractServiceName(profile, profileOntology));
            dataManipulationService.setDescription(extractServiceDescription(profile, profileOntology));
            dataManipulationService.setType(extractServiceType(profile, profileOntology));
        }
        return dataManipulationService;
    }


    /**
     * Extracts service IRI from the service individual.
     * 
     * @param service
     *            service individual found in ontology.
     * @return extracted service IRI.
     * @throws EntryCreationException
     *             should any problems with extraction of data manipulation service information occur.
     */
    private String extractServiceIri(OWLIndividual service)
            throws EntryCreationException {
        if (service.isNamed()) {
            return ((OWLNamedIndividual) service).getIRI().toURI().toString();
        }
        throw new EntryCreationException(
                "The service IRI does not exist in service ontology - malformed semantic descriptor.");
    }


    /**
     * Extracts service name from profile ontology.
     * 
     * @param profile
     *            profile individual found in ontology.
     * @param profileOntology
     *            searched profile ontology.
     * @return extracted service name.
     * @throws EntryCreationException
     *             should any problems with extraction of data manipulation service information occur.
     */
    private String extractServiceName(OWLIndividual profile, OWLOntology profileOntology)
            throws EntryCreationException {
        Set<OWLLiteral> names = profile.getDataPropertyValues(
            ontologyManager.getOWLDataFactory().getOWLDataProperty(
                IRI.create("http://www.daml.org/services/owl-s/1.2/Profile.owl#serviceName")), profileOntology);
        for (OWLLiteral name : names) {
            return name.getLiteral();
        }
        throw new EntryCreationException(
                "Could not find service name in profile ontology - malformed semantic descriptor.");
    }


    /**
     * Extracts service name of described service.
     * 
     * @param profile
     *            profile individual found in ontology.
     * @param profileOntology
     *            profile ontology.
     * @return extracted service description.
     */
    private String extractServiceDescription(OWLIndividual profile, OWLOntology profileOntology) {
        Set<OWLLiteral> descriptions = profile.getDataPropertyValues(ontologyManager.getOWLDataFactory()
                .getOWLDataProperty(IRI.create("http://www.daml.org/services/owl-s/1.2/Profile.owl#textDescription")),
            profileOntology);
        for (OWLLiteral description : descriptions) {
            return description.getLiteral();
        }
        return null;
    }


    /**
     * Extracts service type of described service.
     * 
     * @param profile
     *            profile individual found in ontology.
     * @param profileOntology
     *            profile ontology.
     * @return type of service.
     * @throws EntryCreationException
     *             should specified service type be incompatible.
     */
    private ServiceType extractServiceType(OWLIndividual profile, OWLOntology profileOntology)
            throws EntryCreationException {
        Set<OWLClassExpression> types = profile.getTypes(profileOntology);
        for (OWLClassExpression type : types) {
            String fragment = type.asOWLClass().getIRI().getFragment();
            if (ADV_DELIVERY.equals(fragment)) {
                return ServiceType.ADVANCED_DATA_DELIVERY;
            } else if (MIGRATION.equals(fragment)) {
                return ServiceType.DATA_MIGRATION;
            } else if (CONVERSION.equals(fragment)) {
                return ServiceType.DATA_CONVERSION;
            } else {
                throw new EntryCreationException("Unrecognized service type, malformed semantic descriptor.");
            }
        }
        throw new RuntimeException("Unrecognized service type.");
    }


    /**
     * Extracts technical descriptors individuals from the grounding ontology.
     * 
     * @param grounding
     *            grounding individual found in ontology.
     * @return technical descriptors found in the grounding.
     */
    private Set<OWLIndividual> extractTechnicalDescriptors(OWLIndividual grounding) {
        String groundingPath = grounding.asOWLNamedIndividual().getIRI().getStart();
        groundingPath = groundingPath.substring(0, groundingPath.length() - 1);
        Set<OWLIndividual> technicalDescs = grounding.getObjectPropertyValues(
            ontologyManager.getOWLDataFactory().getOWLObjectProperty(
                IRI.create("http://www.daml.org/services/owl-s/1.2/Grounding.owl#hasAtomicProcessGrounding")),
            ontologyManager.getOntology(IRI.create(groundingPath)));
        return technicalDescs;
    }


    /**
     * Gets info about RDF data of semantic descriptor.
     * 
     * @param semantic
     *            semantic descriptor
     * @param ontology
     *            main ontology of semantic descriptor
     * @throws EntryCreationException
     *             when valid URL to some ontology document cannot be retrieved
     * @return RDF data of semantic descriptor
     */
    private RdfSemanticDescriptor getRdfDataOfSemanticDescriptor(SemanticDescriptor semantic, OWLOntology ontology)
            throws EntryCreationException {
        RdfSemanticDescriptor semanticRdfData = new RdfSemanticDescriptor(semantic.getContext());
        Set<OWLOntology> imports = ontologyManager.getImportedUnbaseOntologies(ontology);
        for (OWLOntology importedOntology : imports) {
            try {
                semanticRdfData.addRdfData(new RdfData(ontologyManager.getOntologyDocumentUrl(importedOntology),
                        importedOntology.getOntologyID().getOntologyIRI().toURI()));
            } catch (MalformedURLException e) {
                logger.error("Ontology " + importedOntology + " is located at invalid URL.", e);
                throw new EntryCreationException(e);
            }
        }
        return semanticRdfData;
    }
}
