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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Default;

import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.IRIDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;

/**
 * Provides convenient API for efficient managing of {@link OWLOntologyManager}.
 */
@Singleton
@Startup
@Default
public class OntologyManager {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(OntologyManager.class);

    /**
     * Ontology manager instance.
     */
    private static final OWLOntologyManager MANAGER = createOWLOntologyManager();

    /**
     * Set of basic, preloaded ontologies.
     */
    private static final Set<OWLOntology> BASE_ONTOLOGIES = createBaseOntologies();


    /**
     * Loads new ontology into the manager.
     * 
     * @param iri
     *            {@link IRI} of the service ontology to load.
     * @return loaded ontology representation.
     * @throws OWLOntologyCreationException
     *             should any problem with ontology loading occur.
     */
    public OWLOntology loadOntology(IRI iri)
            throws OWLOntologyCreationException {
        logger.debug("Extracting information out of descriptor " + iri);
        //      OWLOntologyIRIMapperImpl mapper = new OWLOntologyIRIMapperImpl();
        //      mapper.addMapping(IRI.create("http://cds.synat.pcss.pl/jpgbmp/Service.owl"), iri);
        //      MANAGER.addIRIMapper(mapper);
        //return MANAGER.loadOntologyFromOntologyDocument(iri);
        return MANAGER.loadOntologyFromOntologyDocument(new IRIDocumentSource(iri));
    }


    /**
     * Finds the ontology managed by the manager.
     * 
     * @param iri
     *            {@link IRI} of the service ontology to fetch.
     * @return The ontology that has the specified {@link IRI}, or <code>null</code> if this manager does not manage an
     *         ontology with the specified IRI.
     */
    public OWLOntology getOntology(IRI iri) {
        return MANAGER.getOntology(iri);
    }


    /**
     * Removes the specified ontology.
     * 
     * @param ontology
     *            ontology to remove.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    // New transaction since this method should be called even in the case of rollback.  
    public void removeLoadedOntologies(OWLOntology ontology) {
        Set<OWLOntology> imports = ontology.getImportsClosure();
        imports.removeAll(BASE_ONTOLOGIES);
        for (OWLOntology imported : imports) {
            MANAGER.removeOntology(imported);
        }
    }


    /**
     * Gets all imported unbase ontology for specified ontology plus this ontology.
     * 
     * @param ontology
     *            ontology
     * @return all imported unbase ontology and this ontology
     */
    public Set<OWLOntology> getImportedUnbaseOntologies(OWLOntology ontology) {
        Set<OWLOntology> imports = ontology.getImportsClosure();
        imports.removeAll(BASE_ONTOLOGIES);
        return imports;
    }


    /**
     * Retrieves ontology document URL for specified ontology.
     * 
     * @param ontology
     *            ontology
     * @return URL to document
     * @throws MalformedURLException
     *             when valid URL cannot be retrieved
     */
    public URL getOntologyDocumentUrl(OWLOntology ontology)
            throws MalformedURLException {
        return MANAGER.getOntologyDocumentIRI(ontology).toURI().toURL();
    }


    /**
     * Creates new ontology manager and loads base ontologies into it.
     * 
     * @return new ontology manager.
     */
    private static OWLOntologyManager createOWLOntologyManager() {
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        OWLOntologyLoaderConfiguration configuration = new OWLOntologyLoaderConfiguration();
        configuration = configuration.addIgnoredImport(IRI.create("http://localhost/OntoWiki/Config/"))
                .addIgnoredImport(IRI.create("http://www.udfr.org/onto/onto.rdf"));
        ClassLoader cl = OntologyManager.class.getClassLoader();
        try {
            List<String> ontologies = new ArrayList<String>(17);
            ontologies.add("ontologies/Expression.owl");
            ontologies.add("ontologies/ObjectList.owl");
            ontologies.add("ontologies/Service.owl");
            ontologies.add("ontologies/Process.owl");
            ontologies.add("ontologies/Profile.owl");
            ontologies.add("ontologies/Grounding.owl");
            ontologies.add("ontologies/dArceoUnit.owl");
            ontologies.add("ontologies/dArceoFile.owl");
            ontologies.add("ontologies/dArceoText.owl");
            ontologies.add("ontologies/dArceoDocument.owl");
            ontologies.add("ontologies/dArceoImage.owl");
            ontologies.add("ontologies/dArceoAudio.owl");
            ontologies.add("ontologies/dArceoVideo.owl");
            ontologies.add("ontologies/dArceoService.owl");
            ontologies.add("ontologies/dArceoProcess.owl");
            ontologies.add("ontologies/dArceoProfile.owl");
            ontologies.add("ontologies/RESTfulGrounding.owl");

            for (String ontology : ontologies) {
                InputStream stream = null;
                try {
                    stream = cl.getResourceAsStream(ontology);
                    ontologyManager.loadOntologyFromOntologyDocument(new StreamDocumentSource(stream), configuration);
                } finally {
                    IOUtils.closeQuietly(stream);
                }
            }

        } catch (OWLOntologyCreationException e) {
            throw new WrdzRuntimeException("Could not initialize ontology manager properly.", e);
        }
        return ontologyManager;
    }


    /**
     * Creates a set of base, preloaded ontologies.
     * 
     * @return set of base, preloaded ontologies.
     */
    private static Set<OWLOntology> createBaseOntologies() {
        Set<OWLOntology> ontologies = MANAGER.getOntologies();
        return ontologies;
    }


    /**
     * Gets a data factory which can be used to create OWL API objects such as classes, properties, individuals, axioms
     * etc.
     * 
     * @return a reference to a data factory for creating OWL API objects.
     */
    public OWLDataFactory getOWLDataFactory() {
        return MANAGER.getOWLDataFactory();
    }

}
