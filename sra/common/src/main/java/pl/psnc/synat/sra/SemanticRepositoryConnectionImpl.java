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

import java.net.URI;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.sra.exception.ContextNameSyntaxException;
import pl.psnc.synat.sra.exception.MalformedQueryException;
import pl.psnc.synat.sra.exception.NoSuchContextException;
import pl.psnc.synat.sra.exception.RdfDownloadException;
import pl.psnc.synat.sra.exception.RdfParseException;
import pl.psnc.synat.sra.exception.RdfTripleSyntaxException;
import pl.psnc.synat.sra.exception.SemanticRepositoryConnectionException;

/**
 * Connection to semantic repository.
 * 
 */
public abstract class SemanticRepositoryConnectionImpl implements SemanticRepositoryConnection {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SemanticRepositoryConnectionImpl.class);

    /**
     * Managed connection associated with this connection.
     */
    protected SemanticRepositoryManagedConnectionImpl managedConnection;


    /**
     * Creates a new connection associated with managed connection.
     * 
     * @param managedConnection
     *            managed connection
     */
    public SemanticRepositoryConnectionImpl(SemanticRepositoryManagedConnectionImpl managedConnection) {
        this.managedConnection = managedConnection;
    }


    /**
     * Invalidate the connection.
     */
    public void invalidate() {
        managedConnection = null;
    }


    @Override
    public void addRdfData(URL url, URI baseUri, String context)
            throws RdfDownloadException, RdfParseException, ContextNameSyntaxException,
            SemanticRepositoryConnectionException {
        logger.debug("adding RDF data");
        logger.debug("url " + url);
        logger.debug("baseUri " + baseUri);
        logger.debug("context " + context);
        managedConnection.addRdfData(url, baseUri, context);
    }


    @Override
    public void addRdfTriple(RdfTriple triple, String context)
            throws ContextNameSyntaxException, RdfTripleSyntaxException, SemanticRepositoryConnectionException {
        logger.debug("adding RDF triple");
        logger.debug("triple" + triple);
        logger.debug("context " + context);
        managedConnection.addRdfTriple(triple, context);
    }


    @Override
    public void removeRdfTriple(RdfTriple triple, String context)
            throws ContextNameSyntaxException, RdfTripleSyntaxException, SemanticRepositoryConnectionException {
        logger.debug("removing RDF triple");
        logger.debug("triple" + triple);
        logger.debug("context " + context);
        managedConnection.removeRdfTriple(triple, context);
    }


    @Override
    public void clearContext(String context)
            throws ContextNameSyntaxException, NoSuchContextException, SemanticRepositoryConnectionException {
        logger.debug("clearing context");
        logger.debug("context " + context);
        managedConnection.clearContext(context);
    }


    @Override
    public List<SparqlSelectTuple> executeSparqlSelectQuery(String query)
            throws MalformedQueryException, SemanticRepositoryConnectionException {
        logger.debug("executing SPARQL select query");
        logger.debug("query " + query);
        return managedConnection.executeSparqlSelectQuery(query);
    }


    @Override
    public void close() {
        managedConnection.release();
    }

}
