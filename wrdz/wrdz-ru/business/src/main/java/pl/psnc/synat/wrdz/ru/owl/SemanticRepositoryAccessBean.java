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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.sra.SemanticRepositoryConnection;
import pl.psnc.synat.sra.SemanticRepositoryConnectionFactory;
import pl.psnc.synat.sra.SemanticRepositoryConnectionSpec;
import pl.psnc.synat.sra.SparqlSelectTuple;
import pl.psnc.synat.sra.exception.SemanticRepositoryResourceException;
import pl.psnc.synat.wrdz.ru.composition.AdvancedDelivery;
import pl.psnc.synat.wrdz.ru.composition.ServiceOutcome;
import pl.psnc.synat.wrdz.ru.composition.ServiceParam;
import pl.psnc.synat.wrdz.ru.composition.Transformation;
import pl.psnc.synat.wrdz.ru.composition.TransformationChain;
import pl.psnc.synat.wrdz.ru.config.SparqlQueries;
import pl.psnc.synat.wrdz.ru.grounding.WadlNodes;

/**
 * Provides an access to a semantic repository.
 * 
 */
@Stateless
public class SemanticRepositoryAccessBean implements SemanticRepositoryAccess {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SemanticRepositoryAccessBean.class);

    /**
     * Factory of semantic repository connections.
     **/
    @Resource(mappedName = "jca/sra", shareable = false)
    private SemanticRepositoryConnectionFactory srConnectionFactory;

    /**
     * Bean that caches SPARQL queries.
     */
    @Inject
    private SparqlQueries sparqlQueries;

    /**
     * Connection to semantic repository.
     */
    private SemanticRepositoryConnection connection;


    /**
     * Retrieves a connection to semantic repository.
     * 
     * @throws SemanticRepositoryResourceException
     *             when connection cannot be retrieved
     */
    private void initConnection()
            throws SemanticRepositoryResourceException {
        SemanticRepositoryConnectionSpec spec = new SemanticRepositoryConnectionSpec(true);
        connection = srConnectionFactory.getConnection(spec);
    }


    /**
     * Close the connection.
     */
    private void closeConnection() {
        connection.close();
        connection = null;
    }


    @Override
    public void addSemanticDescrptorRdfData(RdfSemanticDescriptor semanticRdfData)
            throws SemanticRepositoryResourceException {
        initConnection();
        try {
            for (RdfData rdfData : semanticRdfData.getRdfData()) {
                connection.addRdfData(rdfData.getUrl(), rdfData.getBaseUri(), semanticRdfData.getContext());
            }
        } finally {
            closeConnection();
        }
    }


    @Override
    public void removeSemanticDescrptorRdfData(String context)
            throws SemanticRepositoryResourceException {
        initConnection();
        try {
            connection.clearContext(context);
        } finally {
            closeConnection();
        }
    }


    @Override
    public boolean verifyServiceChainParameters(TransformationChain chain, String inputFormatIri, String outputFormatIri)
            throws SemanticRepositoryResourceException {
        initConnection();
        try {
            String input = inputFormatIri;
            for (Transformation transformation : chain.getTransformations()) {
                if (!input.equals(transformation.getInputFormatIri())) {
                    return false;
                }
                Set<String> output = new HashSet<String>();
                Set<String> classes = new HashSet<String>();
                String query = String.format(sparqlQueries.getServiceFormatsQuery(), transformation.getOntologyIri(),
                    transformation.getServiceName());
                List<SparqlSelectTuple> tuples = connection.executeSparqlSelectQuery(query);
                for (SparqlSelectTuple tuple : tuples) {
                    if (input.equals(tuple.getValue("inFormat"))) {
                        if (tuple.getValue("outFormat") != null) {
                            output.add(tuple.getValue("outFormat"));
                        } else {
                            classes.add(tuple.getValue("outClass"));
                        }
                    }
                }
                for (String classUri : classes) {
                    String classFormatQuery = String.format(sparqlQueries.getClassFormatQuery(), classUri);
                    tuples = connection.executeSparqlSelectQuery(classFormatQuery);
                    for (SparqlSelectTuple tuple : tuples) {
                        output.add(tuple.getValue("format"));
                    }
                }
                if (output.contains(transformation.getOutputFormatIri())) {
                    input = transformation.getOutputFormatIri();
                } else {
                    return false;
                }
            }
            return (input.equals(outputFormatIri));
        } finally {
            closeConnection();
        }
    }


    @Override
    public List<TransformationChain> getTransfromationChainsByFormats(String inputFormatIri, String outputFormatIri)
            throws SemanticRepositoryResourceException {
        initConnection();
        try {
            List<TransformationChain> transformationChains = new ArrayList<TransformationChain>();
            String atomicTransformationsQuery = String.format(sparqlQueries.getAtomicTransformationsQuery(),
                inputFormatIri, outputFormatIri);
            List<SparqlSelectTuple> tuples = connection.executeSparqlSelectQuery(atomicTransformationsQuery);
            for (SparqlSelectTuple tuple : tuples) {
                TransformationChain transformationChain = new TransformationChain();
                Transformation transformation = new Transformation();
                transformation.setServiceIri(tuple.getValue("service"));
                transformation.setInputFormatIri(inputFormatIri);
                transformation.setOutputFormatIri(outputFormatIri);
                transformation.setParameters(getServiceParameters(transformation.getServiceIri()));
                transformation.setOutcomes(getServiceOutcomes(transformation.getServiceIri()));
                transformationChain.addTransformation(transformation,
                    getServiceExecutionCost(transformation.getServiceIri()));
                transformationChains.add(transformationChain);
            }
            String composite2TransformationsQuery = String.format(sparqlQueries.getComposite2TransformationsQuery(),
                inputFormatIri, outputFormatIri, inputFormatIri, outputFormatIri);
            tuples = connection.executeSparqlSelectQuery(composite2TransformationsQuery);
            for (SparqlSelectTuple tuple : tuples) {
                TransformationChain transformationChain = new TransformationChain();
                for (int i = 1; i <= 2; i++) {
                    Transformation transformation = new Transformation();
                    transformation.setServiceIri(tuple.getValue("service" + i));
                    transformation.setInputFormatIri(tuple.getValue("inpuid" + i));
                    transformation.setOutputFormatIri(tuple.getValue("outpuid" + i));
                    transformation.setParameters(getServiceParameters(transformation.getServiceIri()));
                    transformation.setOutcomes(getServiceOutcomes(transformation.getServiceIri()));
                    transformationChain.addTransformation(transformation,
                        getServiceExecutionCost(transformation.getServiceIri()));
                }
                transformationChains.add(transformationChain);
            }
            String composite3TransformationsQuery = String.format(sparqlQueries.getComposite3TransformationsQuery(),
                inputFormatIri, outputFormatIri, inputFormatIri, outputFormatIri, inputFormatIri, outputFormatIri,
                inputFormatIri, outputFormatIri);
            tuples = connection.executeSparqlSelectQuery(composite3TransformationsQuery);
            for (SparqlSelectTuple tuple : tuples) {
                TransformationChain transformationChain = new TransformationChain();
                for (int i = 1; i <= 3; i++) {
                    Transformation transformation = new Transformation();
                    transformation.setServiceIri(tuple.getValue("service" + i));
                    transformation.setInputFormatIri(tuple.getValue("inpuid" + i));
                    transformation.setOutputFormatIri(tuple.getValue("outpuid" + i));
                    transformation.setParameters(getServiceParameters(transformation.getServiceIri()));
                    transformation.setOutcomes(getServiceOutcomes(transformation.getServiceIri()));
                    transformationChain.addTransformation(transformation,
                        getServiceExecutionCost(transformation.getServiceIri()));
                }
                transformationChains.add(transformationChain);
            }
            return transformationChains;
        } finally {
            closeConnection();
        }
    }


    /**
     * Gets from the semantic repository the execution cost of a given service.
     * 
     * @param serviceIri
     *            service IRI
     * @return execution cost or null
     */
    private Integer getServiceExecutionCost(String serviceIri) {
        String serviceExecutionCostQuery = String.format(sparqlQueries.getServiceExecutionCostQuery(), serviceIri);
        List<SparqlSelectTuple> tuples = connection.executeSparqlSelectQuery(serviceExecutionCostQuery);
        if (tuples.size() == 1) {
            String cost = tuples.get(0).getValue("cost");
            if (cost != null) {
                try {
                    return new Integer(cost);
                } catch (NumberFormatException e) {
                    logger.error("Execution cost of the service " + serviceIri + " is not a number " + cost);
                }
            }
        }
        return null;
    }


    /**
     * Gets from the semantic repository all parameters of a given service.
     * 
     * @param serviceIri
     *            service IRI
     * @return list of service parameters (values are null)
     */
    private List<ServiceParam> getServiceParameters(String serviceIri) {
        List<ServiceParam> parameters = new ArrayList<ServiceParam>();
        String serviceExecutionParametersQuery = String.format(sparqlQueries.getServiceParametersQuery(), serviceIri);
        List<SparqlSelectTuple> tuples = connection.executeSparqlSelectQuery(serviceExecutionParametersQuery);
        for (SparqlSelectTuple tuple : tuples) {
            parameters.add(new ServiceParam(tuple.getValue("paramname"), tuple.getValue("paramtype"),
                    getBundleType(tuple.getValue("paramtype"))));
        }
        return parameters;
    }


    /**
     * Gets from the semantic repository outcomes of a given service.
     * 
     * @param serviceIri
     *            service IRI
     * @return map in which keys are outcomes of a service (values are null)
     */
    private List<ServiceOutcome> getServiceOutcomes(String serviceIri) {
        List<ServiceOutcome> outcomes = new ArrayList<ServiceOutcome>();
        String serviceExecutionOutcomesQuery = String.format(sparqlQueries.getServiceOutcomesQuery(), serviceIri);
        List<SparqlSelectTuple> tuples = connection.executeSparqlSelectQuery(serviceExecutionOutcomesQuery);
        for (SparqlSelectTuple tuple : tuples) {
            outcomes.add(new ServiceOutcome(tuple.getValue("outcomename"), tuple.getValue("outcometype"),
                    getBundleType(tuple.getValue("outcometype"))));
        }
        return outcomes;
    }


    /**
     * Gets the bundle type for specified type of a parameter or a outcome.
     * 
     * @param type
     *            parameter or outcome type
     * @return bundle type or null
     */
    private String getBundleType(String type) {
        String bundleTypeQuery = String.format(sparqlQueries.getBundleTypeQuery(), type);
        List<SparqlSelectTuple> tuples = connection.executeSparqlSelectQuery(bundleTypeQuery);
        if (!tuples.isEmpty()) {
            return tuples.get(0).getValue("bundletype");
        } else {
            return null;
        }
    }


    @Override
    public WadlNodes getWadlNodesForService(String serviceIri)
            throws SemanticRepositoryResourceException {
        initConnection();
        try {
            String wadlServiceMethodQuery = String.format(sparqlQueries.getWadlServiceMethodQuery(), serviceIri);
            List<SparqlSelectTuple> tuples = connection.executeSparqlSelectQuery(wadlServiceMethodQuery);
            if (tuples.isEmpty()) {
                return null;
            }
            WadlNodes wadlNodes = new WadlNodes();
            wadlNodes.setWadlUri(tuples.get(0).getValue("wadldoc"));
            wadlNodes.setResource(tuples.get(0).getValue("wadlres"));
            wadlNodes.setMethod(tuples.get(0).getValue("wadlmet"));
            Map<String, String> parameters = new HashMap<String, String>();
            String wadlServiceParametersQuery = String
                    .format(sparqlQueries.getWadlServiceParametersQuery(), serviceIri);
            tuples = connection.executeSparqlSelectQuery(wadlServiceParametersQuery);
            for (SparqlSelectTuple tuple : tuples) {
                parameters.put(tuple.getValue("wadlparam"), tuple.getValue("paramname"));
            }
            wadlNodes.setParameters(parameters);
            Map<String, String> outcomes = new HashMap<String, String>();
            String wadlServiceOutcomesQuery = String.format(sparqlQueries.getWadlServiceOutcomesQuery(), serviceIri);
            tuples = connection.executeSparqlSelectQuery(wadlServiceOutcomesQuery);
            for (SparqlSelectTuple tuple : tuples) {
                outcomes.put(tuple.getValue("wadloutcome"), tuple.getValue("outcomename"));
            }
            wadlNodes.setOutcomes(outcomes);
            return wadlNodes;
        } finally {
            closeConnection();
        }
    }


    @Override
    public List<AdvancedDelivery> getAdvancedDeliverySerices()
            throws SemanticRepositoryResourceException {
        initConnection();
        try {
            List<AdvancedDelivery> advancedDeliveryServices = new ArrayList<AdvancedDelivery>();
            String advancedDeliveryServicesQuery = sparqlQueries.getAdvancedDeliveryServicesQuery();
            List<SparqlSelectTuple> tuples = connection.executeSparqlSelectQuery(advancedDeliveryServicesQuery);
            for (SparqlSelectTuple tuple : tuples) {
                AdvancedDelivery advancedDelivery = new AdvancedDelivery();
                advancedDelivery.setServiceIri(tuple.getValue("service"));
                advancedDelivery.setExecutionCost(getServiceExecutionCost(advancedDelivery.getServiceIri()));
                advancedDelivery.setInputFormatIris(getAdvancedDeliveryFormats(advancedDelivery.getServiceIri()));
                advancedDelivery.setParameters(getServiceParameters(advancedDelivery.getServiceIri()));
                advancedDelivery.setOutcomes(getServiceOutcomes(advancedDelivery.getServiceIri()));
                advancedDeliveryServices.add(advancedDelivery);
            }
            return advancedDeliveryServices;
        } finally {
            closeConnection();
        }
    }


    /**
     * Finds all input format of a given advanced delivery service.
     * 
     * @param serviceIri
     *            service IRI
     * @return set of all input formats - set of UDFR fromats IRI
     */
    private Set<String> getAdvancedDeliveryFormats(String serviceIri) {
        Set<String> formats = new HashSet<String>();
        String advancedDeliveryFormatsQuery = String
                .format(sparqlQueries.getAdvancedDeliveryFormatsQuery(), serviceIri);
        List<SparqlSelectTuple> tuples = connection.executeSparqlSelectQuery(advancedDeliveryFormatsQuery);
        for (SparqlSelectTuple tuple : tuples) {
            formats.add(tuple.getValue("format"));
        }
        return formats;
    }

}
