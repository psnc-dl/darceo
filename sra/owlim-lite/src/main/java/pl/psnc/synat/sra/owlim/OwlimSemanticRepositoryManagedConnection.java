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
package pl.psnc.synat.sra.owlim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;

import org.openrdf.repository.Repository;
import org.openrdf.repository.manager.RepositoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.sra.RdfTriple;
import pl.psnc.synat.sra.SemanticRepositoryAddRdfDataOperation;
import pl.psnc.synat.sra.SemanticRepositoryManagedConnectionImpl;
import pl.psnc.synat.sra.SemanticRepositoryWOOperation;
import pl.psnc.synat.sra.SparqlSelectTuple;
import pl.psnc.synat.sra.concurrent.SemanticRepositoryLockManager;
import pl.psnc.synat.sra.darceo.DArceoQueries;
import pl.psnc.synat.sra.darceo.DArceoUtils;
import pl.psnc.synat.sra.darceo.TransformationCounter;
import pl.psnc.synat.sra.exception.SemanticRepositoryCommitException;
import pl.psnc.synat.sra.exception.SemanticRepositoryConnectionException;
import pl.psnc.synat.sra.exception.SemanticRepositoryResourceException;
import pl.psnc.synat.sra.util.SemanticDataUtils;
import pl.psnc.synat.sra.util.SparqlQueries;

/**
 * Managed connection to OWLIM style semantic repositories.
 * 
 */
public class OwlimSemanticRepositoryManagedConnection extends SemanticRepositoryManagedConnectionImpl {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(OwlimSemanticRepositoryManagedConnection.class);

    /**
     * OLWIM-Lite repository manager.
     */
    private RepositoryManager repositoryManager;


    /**
     * Creates a new managed connection.
     * 
     * @param repositoryManager
     *            repository manager
     * @param repository
     *            OWLIM repository created by the resource adapter
     * @param cxRequestInfo
     *            parameters of connection
     * @throws SemanticRepositoryResourceException
     *             if some IO exception occurred
     */
    public OwlimSemanticRepositoryManagedConnection(RepositoryManager repositoryManager, Repository repository,
            OwlimSemanticRepositoryConnectionRequestInfo cxRequestInfo)
            throws SemanticRepositoryResourceException {
        super(cxRequestInfo.isdArceo());
        logger.debug("OwlimSemanticRepositoryConnectionRequestInfo: " + cxRequestInfo);
        this.repositoryManager = repositoryManager;
        this.client = new OwlimSemanticRepositoryClient(repository);
        this.client.connect();
        xares = new OwlimSemanticRepositoryXAResource(operations, client);
    }


    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        logger.debug("getting a connection");
        logger.debug("Subject: " + subject);
        logger.debug("ConnectionRequestInfo: " + cxRequestInfo);
        if (connection == null) {
            connection = new OwlimSemanticRepositoryConnection(this);
        }
        return connection;
    }


    @Override
    public ManagedConnectionMetaData getMetaData()
            throws ResourceException {
        logger.debug("getting a metadata");
        return new OwlimSemanticRepositoryManagedConnectionMetaData();
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("OwlimSemanticRepositoryManagedConnection ");
        sb.append("[hashCode = ").append(hashCode());
        sb.append(", connection = ").append(connection);
        sb.append(", client = ").append(client);
        sb.append(", xares = ").append(xares);
        sb.append("]");
        return sb.toString();
    }


    @Override
    public void cleanup()
            throws ResourceException {
        logger.debug("cleaning");
        if (connection != null) {
            connection.invalidate();
        }
        connection = null;
        operations.clear();
        ((OwlimSemanticRepositoryClient) client).invalidate();
        SemanticRepositoryLockManager.getInstance().unlockAll();
    }


    @Override
    protected void updateDArceoTriples(String context)
            throws SemanticRepositoryConnectionException {
        Repository tempRepository = null;
        OwlimSemanticRepositoryClient tempClient = null;
        try {
            tempRepository = repositoryManager.getRepository("working");
            tempRepository.initialize();
            tempClient = new OwlimSemanticRepositoryClient(tempRepository);
        } catch (Exception e) {
            logger.debug("Error while initializing a temporary repository", e);
            throw new SemanticRepositoryConnectionException("Error while initializing the working repository", e);
        }
        try {
            for (SemanticRepositoryWOOperation operation : operations) {
                SemanticRepositoryAddRdfDataOperation addRdfDataOperation = (SemanticRepositoryAddRdfDataOperation) operation;
                tempClient.addRdfData(addRdfDataOperation.getUrl(), addRdfDataOperation.getBaseUri(),
                    addRdfDataOperation.getContext());
            }
            tempClient.commit();
            TransformationCounter counter = new TransformationCounter();
            Map<String, String> instances = new HashMap<String, String>();
            String mainQuery = DArceoQueries.getInOutPuidQuery(context, "service", "inpuid", "outclass", "infilecond",
                "inoutfmtcond");
            List<SparqlSelectTuple> mainQueryResult = tempClient.executeSparqlSelectQuery(mainQuery);
            for (SparqlSelectTuple tuple : mainQueryResult) {
                String service = tuple.getValue("service");
                String inpuid = tuple.getValue("inpuid");
                String outclass = tuple.getValue("outclass");
                String outinstance = instances.get(outclass);
                if (outinstance == null) {
                    outinstance = SemanticDataUtils.classToInstance(outclass);
                    instances.put(outclass, outinstance);
                    RdfTriple tempTriple = new RdfTriple(outinstance, SemanticDataUtils.PREDICATE_RDF_TYPE, outclass);
                    tempClient.addRdfTriple(tempTriple, context);
                    tempClient.commit();
                }
                // Still errors in the OWLIM reasoning - filtering below is not sufficient. 
                // It is better to use a query about the class
                //String fileFormatQuery = DArceoQueries.getFileFormatQuery(outinstance, outclass, "outfmt");
                String fileFormatQuery = DArceoQueries.getFileFormatQuery(outclass, "outfmt");
                List<SparqlSelectTuple> fileFormatQueryResult = tempClient.executeSparqlSelectQuery(fileFormatQuery);
                // There is some error in OWLIM since a result of the above query should have only 1 tuple!
                // Sometimes it has more, and we have to filter it!
                for (SparqlSelectTuple inputPuidTuple : fileFormatQueryResult) {
                    String outfmt = inputPuidTuple.getValue("outfmt");
                    String fileFormatQueryAsk = SparqlQueries.getRdfTypeQueryAsk(outfmt,
                        "http://www.udfr.org/onto#FileFormat");
                    if (!tempClient.executeSparqlAskQuery(fileFormatQueryAsk)) {
                        logger.debug("The type of file " + outfmt + " is incorrect");
                        continue;
                    }
                    String rdfTypeQuery = SparqlQueries.getRdfTypeQuery(outfmt, "type");
                    List<SparqlSelectTuple> rdfTypeQueryResult = tempClient.executeSparqlSelectQuery(rdfTypeQuery);
                    boolean directPuid = true;
                    for (SparqlSelectTuple rdfTypeTuple : rdfTypeQueryResult) {
                        if (rdfTypeTuple.getValue("type").equals(DArceoUtils.PROCESS_INPUT_PARAMETER)) {
                            String inoutfmtcond = tuple.getValue("inoutfmtcond");
                            String outputPuidQuery = DArceoQueries.getOutputPuidFromInputQuery(inoutfmtcond, outfmt,
                                "outpuid");
                            List<SparqlSelectTuple> outputPuidQueryResult = tempClient
                                    .executeSparqlSelectQuery(outputPuidQuery);
                            for (SparqlSelectTuple outputPuidTuple : outputPuidQueryResult) {
                                String outpuid = outputPuidTuple.getValue("outpuid");
                                addDArceoTransformation(service, inpuid, outpuid, context, counter);
                            }
                            directPuid = false;
                            break;
                        } else if (rdfTypeTuple.getValue("type").equals(DArceoUtils.PROCESS_LOCAL_PARAMETER)) {
                            String infilecond = tuple.getValue("infilecond");
                            String outputPuidQuery = DArceoQueries.getOutputPuidFromLocalQuery(infilecond, outfmt,
                                "outpuid");
                            List<SparqlSelectTuple> outputPuidQueryResult = tempClient
                                    .executeSparqlSelectQuery(outputPuidQuery);
                            for (SparqlSelectTuple outputPuidTuple : outputPuidQueryResult) {
                                String outpuid = outputPuidTuple.getValue("outpuid");
                                if (outpuid.equals(inpuid)) {
                                    addDArceoTransformation(service, inpuid, outpuid, context, counter);
                                }
                            }
                            directPuid = false;
                            break;
                        }
                    }
                    if (directPuid) {
                        addDArceoTransformation(service, inpuid, outfmt, context, counter);
                    }
                }
            }
        } catch (SemanticRepositoryCommitException e) {
            logger.debug("Error while saving data to a temporary repository", e);
            throw new SemanticRepositoryConnectionException("Error while committing data to the working repository", e);
        } finally {
            try {
                tempClient.clearContext(context);
                tempClient.commit();
                tempClient.disconnect();
            } catch (Exception e) {
                logger.debug("Error while cleaning the working repository", e);
                throw new SemanticRepositoryConnectionException("Error while cleaning the working repository", e);
            } finally {
                try {
                    tempRepository.shutDown();
                } catch (Exception f) {
                    logger.debug("Error while shutting down the working repository", f);
                    throw new SemanticRepositoryConnectionException("Error while shutting down the working repository",
                            f);
                }
            }
        }
    }
}
