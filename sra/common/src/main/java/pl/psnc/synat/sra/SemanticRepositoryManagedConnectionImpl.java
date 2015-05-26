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
package pl.psnc.synat.sra;

import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.transaction.xa.XAResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.sra.concurrent.SemanticRepositoryLockManager;
import pl.psnc.synat.sra.darceo.DArceoUtils;
import pl.psnc.synat.sra.darceo.TransformationCounter;
import pl.psnc.synat.sra.exception.ContextNameSyntaxException;
import pl.psnc.synat.sra.exception.MalformedQueryException;
import pl.psnc.synat.sra.exception.NoSuchContextException;
import pl.psnc.synat.sra.exception.RdfDownloadException;
import pl.psnc.synat.sra.exception.RdfParseException;
import pl.psnc.synat.sra.exception.RdfTripleSyntaxException;
import pl.psnc.synat.sra.exception.SemanticRepositoryConnectionException;
import pl.psnc.synat.sra.util.SemanticDataUtils;

/**
 * Abstract class for all managed connection to semantic repositories.
 * 
 */
public abstract class SemanticRepositoryManagedConnectionImpl implements ManagedConnection {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SemanticRepositoryManagedConnectionImpl.class);

    /** SRA Logger. */
    private static final Logger SRA_LOGGER = LoggerFactory.getLogger("SRA");

    /**
     * Connection event sender.
     */
    protected SemanticRepositoryConnectionEventSender sender;

    /**
     * The only one connection handle associated with this managed connection.
     */
    protected SemanticRepositoryConnectionImpl connection;

    /**
     * True if this repository is dArceo.
     */
    protected final boolean dArceo;

    /**
     * List of atomic write operations performed during transaction managed by this resource manager.
     */
    protected List<SemanticRepositoryWOOperation> operations;

    /**
     * dArceo semantic repository client.
     */
    protected SemanticRepositoryClient client;

    /**
     * XA resource that manage distributed transaction in which this managed connection is involved in.
     */
    protected SemanticRepositoryXAResourceImpl xares;


    /**
     * Constructor.
     * 
     * @param dArceo
     *            says that the type of repository is dArceo
     */
    public SemanticRepositoryManagedConnectionImpl(boolean dArceo) {
        this.dArceo = dArceo;
        operations = new ArrayList<SemanticRepositoryWOOperation>();
        sender = new SemanticRepositoryConnectionEventSender(this);
    }


    @Override
    public void associateConnection(Object connection)
            throws ResourceException {
        logger.warn("associate a connection" + connection);
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
        SemanticRepositoryLockManager.getInstance().unlockAll();
    }


    @Override
    public void destroy()
            throws ResourceException {
        logger.debug("destroying");
        client.disconnect();
    }


    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        logger.debug("adding a connection event listener: " + listener);
        sender.addConnectorListener(listener);
    }


    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        logger.debug("removing a connection event listener: " + listener);
        sender.removeConnectorListener(listener);
    }


    /**
     * Releases connection. Normal behavior.
     */
    public void release() {
        logger.debug("releasing");
        String context = dArceoServiceContext();
        if (context != null) {
            logger.debug("updating repository with dArceo triples");
            updateDArceoTriples(context);
        }
        sender.sendEvent(ConnectionEvent.CONNECTION_CLOSED, null, connection);
    }


    /**
     * Update the semantic repository with triples inferred from a semantic descriptor of web service.
     * 
     * @param context
     *            context to update
     * @throws SemanticRepositoryConnectionException
     *             if some unexpected error occurs
     */
    protected abstract void updateDArceoTriples(String context)
            throws SemanticRepositoryConnectionException;


    /**
     * Adds RDF triples describing one transformation.
     * 
     * @param context
     *            context
     * @param counter
     *            counter of transformation for a service
     * @param service
     *            service
     * @param inpuid
     *            input PUID URI URDF identifier
     * @param outpuid
     *            output PUID URI URDF identifier
     * 
     * @throws SemanticRepositoryConnectionException
     *             if some unexpected error occurs
     */
    protected void addDArceoTransformation(String service, String inpuid, String outpuid, String context,
            TransformationCounter counter)
            throws SemanticRepositoryConnectionException {
        String transformation = service + "Transformation" + counter.getNext(service);
        RdfTriple fileTransformation = new RdfTriple(transformation, SemanticDataUtils.PREDICATE_RDF_TYPE,
                DArceoUtils.DARCEO_SERVICE_FILE_TRANSFORMATION);
        client.addRdfTriple(fileTransformation, context);
        RdfTriple fileTransformationInput = new RdfTriple(transformation,
                DArceoUtils.PREDICATE_FILE_TRANSFORMATION_INPUT, inpuid);
        client.addRdfTriple(fileTransformationInput, context);
        RdfTriple fileTransformationOutput = new RdfTriple(transformation,
                DArceoUtils.PREDICATE_FILE_TRANSFORMATION_OUTPUT, outpuid);
        client.addRdfTriple(fileTransformationOutput, context);
        //RdfTriple fileTransformationService = new RdfTriple(service, DArceoUtils.PREDICATE_HAS_FILE_TRANSFORMATION, transformation);
        RdfTriple fileTransformationService = new RdfTriple(transformation, DArceoUtils.PREDICATE_PERFORMED_BY_SERVICE,
                service);
        client.addRdfTriple(fileTransformationService, context);
    }


    /**
     * Releases connection. Some error occurred.
     * 
     * @param ex
     *            exception associated with the error
     */
    public void release(Exception ex) {
        logger.debug("releasing with error: " + ex);
        sender.sendEvent(ConnectionEvent.CONNECTION_ERROR_OCCURRED, ex, connection);
    }


    @Override
    public LocalTransaction getLocalTransaction()
            throws ResourceException {
        throw new UnsupportedOperationException("Local transactions unsupported.");
    }


    @Override
    public XAResource getXAResource()
            throws ResourceException {
        logger.debug("getting a XA resource");
        return xares;
    }


    @Override
    public PrintWriter getLogWriter()
            throws ResourceException {
        return null;
    }


    @Override
    public void setLogWriter(PrintWriter out)
            throws ResourceException {
    }


    /**
     * Adds RDF data from the specified URL to the semantic repository.
     * 
     * @param url
     *            URL to RDF data
     * @param baseUri
     *            base URI of the rdf data
     * @param context
     *            context to which RDF data will be added
     * @throws RdfDownloadException
     *             if the namespace of RDF data already exists
     * @throws RdfParseException
     *             if an error occurred while parsing the RDF data
     * @throws ContextNameSyntaxException
     *             if the context is not valid URI
     * @throws SemanticRepositoryConnectionException
     *             if some unexpected error occurs
     */
    public void addRdfData(URL url, URI baseUri, String context)
            throws RdfDownloadException, RdfParseException, ContextNameSyntaxException,
            SemanticRepositoryConnectionException {
        SemanticRepositoryLockManager.getInstance().lockToWrite();
        client.addRdfData(url, baseUri, context);
        SemanticRepositoryWOOperation o = new SemanticRepositoryAddRdfDataOperation(url, baseUri, context);
        operations.add(o);
        SRA_LOGGER.info("operation added: " + o);
    }


    /**
     * Checks whether the last operation was adding RDF data of some dArceoService, and if yes then returns a context
     * for this service.
     * 
     * @return context for dArceoService or null the last operation was something else than adding RDF data of some
     *         dArceoService
     */
    private String dArceoServiceContext() {
        String context = null;
        if (dArceo && operations.size() > 0) {
            for (SemanticRepositoryWOOperation operation : operations) {
                if (operation instanceof SemanticRepositoryAddRdfDataOperation) {
                    if (context == null) {
                        context = ((SemanticRepositoryAddRdfDataOperation) operation).getContext();
                    } else {
                        if (!context.equals(((SemanticRepositoryAddRdfDataOperation) operation).getContext())) {
                            return null;
                        }
                    }
                } else {
                    return null;
                }
            }
        }
        return context;
    }


    /**
     * Adds a RDF triple to the semantic repository.
     * 
     * @param triple
     *            RDF triple
     * @param context
     *            context to which RDF triple will be added
     * @throws ContextNameSyntaxException
     *             if the context is not valid URI
     * @throws RdfTripleSyntaxException
     *             if some component of RDF triple is not valid URI
     * @throws SemanticRepositoryConnectionException
     *             if some unexpected error occurs
     */
    public void addRdfTriple(RdfTriple triple, String context)
            throws ContextNameSyntaxException, RdfTripleSyntaxException, SemanticRepositoryConnectionException {
        SemanticRepositoryLockManager.getInstance().lockToWrite();
        client.addRdfTriple(triple, context);
        SemanticRepositoryWOOperation o = new SemanticRepositoryAddRdfTripleOperation(triple, context);
        operations.add(o);
        SRA_LOGGER.info("operation added: " + o);
    }


    /**
     * Removes a RDF triple from the semantic repository.
     * 
     * @param triple
     *            RDF triple (subject, predicate, object)
     * @param context
     *            context to which the triple is restricted
     * @throws ContextNameSyntaxException
     *             if the context is not valid URI
     * @throws RdfTripleSyntaxException
     *             if some component of RDF triple is not valid URI
     * @throws SemanticRepositoryConnectionException
     *             if some unexpected error occurs
     */
    public void removeRdfTriple(RdfTriple triple, String context)
            throws ContextNameSyntaxException, RdfTripleSyntaxException, SemanticRepositoryConnectionException {
        SemanticRepositoryLockManager.getInstance().lockToWrite();
        client.removeRdfTriple(triple, context);
        SemanticRepositoryWOOperation o = new SemanticRepositoryRemoveRdfTripleOperation(triple, context);
        operations.add(o);
        SRA_LOGGER.info("operation added: " + o);
    }


    /**
     * Deletes all RDF triples which belong to the specified context.
     * 
     * @param context
     *            context of RDF triples
     * @throws ContextNameSyntaxException
     *             if the context is not valid URI
     * @throws NoSuchContextException
     *             if the context does not exist
     * @throws SemanticRepositoryConnectionException
     *             if some unexpected error occurs
     */
    public void clearContext(String context)
            throws ContextNameSyntaxException, NoSuchContextException, SemanticRepositoryConnectionException {
        SemanticRepositoryLockManager.getInstance().lockToWrite();
        client.clearContext(context);
        SemanticRepositoryWOOperation o = new SemanticRepositoryClearContextOperation(context);
        operations.add(o);
        SRA_LOGGER.info("operation added: " + o);
    }


    /**
     * Execute a SPARQL select query on a semantic repository and returns a list of tuples.
     * 
     * @param query
     *            SPARQL select query
     * @return list of tuples
     * @throws MalformedQueryException
     *             if query is malformed
     * @throws SemanticRepositoryConnectionException
     *             if some unexpected error occurs
     */
    public List<SparqlSelectTuple> executeSparqlSelectQuery(String query)
            throws MalformedQueryException, SemanticRepositoryConnectionException {
        SemanticRepositoryLockManager.getInstance().lockToRead();
        List<SparqlSelectTuple> result = client.executeSparqlSelectQuery(query);
        SemanticRepositoryLockManager.getInstance().unlockFromRead();
        return result;
    }

}
