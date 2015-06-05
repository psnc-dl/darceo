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

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.sra.RdfTriple;
import pl.psnc.synat.sra.SemanticRepositoryClient;
import pl.psnc.synat.sra.SparqlSelectTuple;
import pl.psnc.synat.sra.SparqlSelectTupleImpl;
import pl.psnc.synat.sra.exception.ContextNameSyntaxException;
import pl.psnc.synat.sra.exception.MalformedQueryException;
import pl.psnc.synat.sra.exception.NoSuchContextException;
import pl.psnc.synat.sra.exception.RdfDownloadException;
import pl.psnc.synat.sra.exception.RdfParseException;
import pl.psnc.synat.sra.exception.RdfTripleSyntaxException;
import pl.psnc.synat.sra.exception.SemanticRepositoryCommitException;
import pl.psnc.synat.sra.exception.SemanticRepositoryConnectionException;
import pl.psnc.synat.sra.exception.SemanticRepositoryResourceException;
import pl.psnc.synat.sra.exception.SemanticRepositoryRollbackException;
import pl.psnc.synat.sra.owlim.exception.SemanticRepositoryFlushException;

/**
 * Client of the OWLIM-Lite semantic repository system that can cope with limitations of this version.
 * 
 */
public class OwlimSemanticRepositoryClient implements SemanticRepositoryClient {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(OwlimSemanticRepositoryConnectionFactory.class);

    /**
     * dArceo OWLIM repository.
     */
    private final Repository repository;

    /**
     * Dedicated connection to OWLIM repository retrieved from it - but only when it's really needed.
     */
    private RepositoryConnection repositoryConnection;


    /**
     * Construct a client for OWLIM-Lite repository.
     * 
     * @param repository
     *            reference to OWLIM-Lite repository
     */
    public OwlimSemanticRepositoryClient(Repository repository) {
        this.repository = repository;
        this.repositoryConnection = null;
    }


    /**
     * Gets a connection to repository and save this reference as a field of this client.
     * 
     * @return repository connection
     */
    private RepositoryConnection getRepositoryConnection() {
        if (repositoryConnection == null) {
            try {
                repositoryConnection = repository.getConnection();
                repositoryConnection.setAutoCommit(false);
            } catch (RepositoryException e) {
                logger.error("Error while connecting to a OWLIM semantin repository!", e);
                throw new SemanticRepositoryConnectionException(e);
            }
        }
        return repositoryConnection;
    }


    @Override
    public void connect()
            throws SemanticRepositoryResourceException {
        try {
            RepositoryConnection trialConnection = repository.getConnection();
            // It turns out that the flush option does nor work in OWLIM-Lite version!!
            // So shutting down and initialization is necessary together with connections' synchronization
            //trialConnection.setAutoCommit(false);
            //ValueFactory factory = trialConnection.getValueFactory();
            //org.openrdf.model.URI subj = factory.createURI("http://darceo.psnc.pl/ontologies/dArceoService.owl#dummy");
            //org.openrdf.model.URI pred = factory.createURI("http://www.ontotext.com/flush");
            //org.openrdf.model.URI obj = factory.createURI("");
            //trialConnection.add(subj, pred, obj);
            //trialConnection.commit();
            trialConnection.close();
        } catch (RepositoryException e) {
            logger.error("Error while connecting to a OWLIM semantin repository!", e);
            throw new SemanticRepositoryResourceException(e);
        }
    }


    /**
     * Invalidates a connection to OWLIM-Lite repository. It's necessary since the commit operations in the OWLIM-Lite
     * repository requires shutting down and initializing a repository from scratch.
     */
    public void invalidate() {
        disconnect();
    }


    @Override
    public void disconnect() {
        try {
            if (repositoryConnection != null && repositoryConnection.isOpen()) {
                repositoryConnection.close();
            }
        } catch (RepositoryException e) {
            logger.error("Error occurred while closing OWLIM repository connection.", e);

        }
        repositoryConnection = null;
    }


    @Override
    public void commit()
            throws SemanticRepositoryCommitException {
        try {
            getRepositoryConnection().commit();
        } catch (RepositoryException e) {
            logger.error("Error occurred while committing operations on a repository connection.", e);
            throw new SemanticRepositoryCommitException(e);
        }
    }


    @Override
    public void rollback()
            throws SemanticRepositoryRollbackException {
        try {
            getRepositoryConnection().rollback();
        } catch (RepositoryException e) {
            logger.error("Error occurred while rolling back operations on a repository connection.", e);
            throw new SemanticRepositoryRollbackException(e);
        }
    }


    /**
     * Flush all committed data to disk.
     * 
     * @throws SemanticRepositoryFlushException
     *             if some error while flushing data to the disk occurs
     * @throws SemanticRepositoryConnectionException
     *             if some error occurs
     */
    public void flush()
            throws SemanticRepositoryFlushException, SemanticRepositoryConnectionException {
        try {
            repository.shutDown();
        } catch (RepositoryException e) {
            logger.error("Error occurred while shutting down a repository.", e);
            throw new SemanticRepositoryFlushException(e);
        }
        try {
            repository.initialize();
        } catch (RepositoryException e) {
            logger.error("Error occurred while initializing a repository.", e);
            throw new SemanticRepositoryConnectionException(e);
        }
    }


    @Override
    public void addRdfData(URL url, URI baseUri, String context)
            throws RdfDownloadException, RdfParseException, ContextNameSyntaxException,
            SemanticRepositoryConnectionException {
        try {
            org.openrdf.model.URI uriContext = new URIImpl(context);
            getRepositoryConnection().add(url, baseUri.toString(), RDFFormat.RDFXML, uriContext);
        } catch (IllegalArgumentException e) {
            logger.error("Context " + context + " is not valid URI.", e);
            throw new ContextNameSyntaxException(e);
        } catch (RDFParseException e) {
            logger.error("RDF data " + url + " syntax is incorrect.", e);
            throw new RdfParseException(e);
        } catch (RepositoryException e) {
            logger.error("Some problem with connection to OWLIM repository occurs", e);
            throw new SemanticRepositoryConnectionException(e);
        } catch (IOException e) {
            logger.error("RDF data " + url + " cannot be downloaded.", e);
            throw new RdfDownloadException(e);
        }
    }


    @Override
    public void addRdfTriple(RdfTriple triple, String context)
            throws ContextNameSyntaxException, RdfTripleSyntaxException, SemanticRepositoryConnectionException {
        URIImpl uriContext = null;
        try {
            uriContext = new URIImpl(context);
        } catch (IllegalArgumentException e) {
            logger.error("Context " + context + " is not valid URI.", e);
            throw new ContextNameSyntaxException(e);
        }
        try {
            ValueFactory factory = getRepositoryConnection().getValueFactory();
            org.openrdf.model.URI uriSubject = factory.createURI(triple.getSubject());
            org.openrdf.model.URI uriPredicate = factory.createURI(triple.getPredicate());
            org.openrdf.model.URI uriObject = factory.createURI(triple.getObject());
            getRepositoryConnection().add(uriSubject, uriPredicate, uriObject, uriContext);
        } catch (IllegalArgumentException e) {
            logger.error("Some component of a triple is not valid URI.", e);
            throw new RdfTripleSyntaxException(e);
        } catch (RepositoryException e) {
            logger.error("Some problem with connection to OWLIM repository occurs", e);
            throw new SemanticRepositoryConnectionException(e);
        }

    }


    @Override
    public void removeRdfTriple(RdfTriple triple, String context)
            throws ContextNameSyntaxException, RdfTripleSyntaxException, SemanticRepositoryConnectionException {
        URIImpl uriContext = null;
        try {
            uriContext = new URIImpl(context);
        } catch (IllegalArgumentException e) {
            logger.error("Context " + context + " is not valid URI.", e);
            throw new ContextNameSyntaxException(e);
        }
        try {
            ValueFactory factory = getRepositoryConnection().getValueFactory();
            org.openrdf.model.URI uriSubject = factory.createURI(triple.getSubject());
            org.openrdf.model.URI uriPredicate = factory.createURI(triple.getPredicate());
            org.openrdf.model.URI uriObject = factory.createURI(triple.getObject());
            getRepositoryConnection().remove(uriSubject, uriPredicate, uriObject, uriContext);
        } catch (IllegalArgumentException e) {
            logger.error("Some component of a triple is not valid URI.", e);
            throw new RdfTripleSyntaxException(e);
        } catch (RepositoryException e) {
            logger.error("Some problem with connection to OWLIM repository occurs", e);
            throw new SemanticRepositoryConnectionException(e);
        }

    }


    @Override
    public void clearContext(String context)
            throws ContextNameSyntaxException, NoSuchContextException, SemanticRepositoryConnectionException {
        try {
            org.openrdf.model.URI uriContext = new URIImpl(context);
            getRepositoryConnection().clear(uriContext);
        } catch (IllegalArgumentException e) {
            logger.error("Context " + context + " is not valid URI.", e);
            throw new ContextNameSyntaxException(e);
        } catch (RepositoryException e) {
            logger.error("Some problem with connection to OWLIM repository occurs", e);
            throw new SemanticRepositoryConnectionException(e);
        }
    }


    @Override
    public List<SparqlSelectTuple> executeSparqlSelectQuery(String query)
            throws MalformedQueryException, SemanticRepositoryConnectionException {
        List<SparqlSelectTuple> tuples = new LinkedList<SparqlSelectTuple>();
        Query preparedQuery = null;
        try {
            preparedQuery = getRepositoryConnection().prepareQuery(QueryLanguage.SPARQL, query);
            if (!(preparedQuery instanceof TupleQuery)) {
                logger.error("Query is not a SPARQL select query. " + query);
                throw new MalformedQueryException("The query is not a SPARQL select query.");
            }
        } catch (org.openrdf.query.MalformedQueryException e) {
            logger.error("Query is malformed.", e);
            throw new MalformedQueryException(e);
        } catch (RepositoryException e) {
            logger.error("Some problem with connection to OWLIM repository occurs", e);
            throw new SemanticRepositoryConnectionException(e);
        }
        SparqlSelectTupleImpl tuple;
        TupleQueryResult result;
        try {
            result = ((TupleQuery) preparedQuery).evaluate();
            while (result.hasNext()) {
                BindingSet set = result.next();
                tuple = new SparqlSelectTupleImpl();
                for (String name : set.getBindingNames()) {
                    tuple.setValue(name, set.getValue(name).stringValue());
                }
                tuples.add(tuple);
            }
            result.close();
        } catch (QueryEvaluationException e) {
            logger.error("Some problem with connection to OWLIM repository occurs", e);
            throw new SemanticRepositoryConnectionException(e);
        }
        return tuples;
    }


    @Override
    public boolean executeSparqlAskQuery(String query)
            throws MalformedQueryException, SemanticRepositoryConnectionException {
        Query preparedQuery = null;
        try {
            preparedQuery = getRepositoryConnection().prepareQuery(QueryLanguage.SPARQL, query);
            if (!(preparedQuery instanceof BooleanQuery)) {
                logger.error("Query is not a SPARQL ask query. " + query);
                throw new MalformedQueryException("The query is not a SPARQL ask query.");
            }
        } catch (org.openrdf.query.MalformedQueryException e) {
            logger.error("Query is malformed.", e);
            throw new MalformedQueryException(e);
        } catch (RepositoryException e) {
            logger.error("Some problem with connection to OWLIM repository occurs", e);
            throw new SemanticRepositoryConnectionException(e);
        }
        try {
            return ((BooleanQuery) preparedQuery).evaluate();
        } catch (QueryEvaluationException e) {
            logger.error("Some problem with connection to OWLIM repository occurs", e);
            throw new SemanticRepositoryConnectionException(e);
        }
    }


    @Override
    public void executeSparqlUpdateData(String update)
            throws MalformedQueryException, SemanticRepositoryConnectionException {
        Update prepareUpdate = null;
        try {
            prepareUpdate = getRepositoryConnection().prepareUpdate(QueryLanguage.SPARQL, update);
        } catch (org.openrdf.query.MalformedQueryException e) {
            logger.error("Query is malformed.", e);
            throw new MalformedQueryException(e);
        } catch (RepositoryException e) {
            logger.error("Some problem with connection to OWLIM repository occurs", e);
            throw new SemanticRepositoryConnectionException(e);
        }
        try {
            prepareUpdate.execute();
        } catch (UpdateExecutionException e) {
            logger.error("Some problem with connection to OWLIM repository occurs", e);
            throw new SemanticRepositoryConnectionException(e);
        }
    }

}
