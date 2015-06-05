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

import pl.psnc.synat.sra.exception.ContextNameSyntaxException;
import pl.psnc.synat.sra.exception.MalformedQueryException;
import pl.psnc.synat.sra.exception.NoSuchContextException;
import pl.psnc.synat.sra.exception.RdfDownloadException;
import pl.psnc.synat.sra.exception.RdfParseException;
import pl.psnc.synat.sra.exception.RdfTripleSyntaxException;
import pl.psnc.synat.sra.exception.SemanticRepositoryConnectionException;

/**
 * Common interface to semantic repositories.
 * 
 */
public interface SemanticRepositoryConnection {

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
    void addRdfData(URL url, URI baseUri, String context)
            throws RdfDownloadException, RdfParseException, ContextNameSyntaxException,
            SemanticRepositoryConnectionException;


    /**
     * Adds a RDF triple to the semantic repository.
     * 
     * @param triple
     *            RDF triple (subject, predicate, object)
     * @param context
     *            context to which RDF triple will be added
     * @throws ContextNameSyntaxException
     *             if the context is not valid URI
     * @throws RdfTripleSyntaxException
     *             if some component of RDF triple is not valid URI
     * @throws SemanticRepositoryConnectionException
     *             if some unexpected error occurs
     */
    void addRdfTriple(RdfTriple triple, String context)
            throws ContextNameSyntaxException, RdfTripleSyntaxException, SemanticRepositoryConnectionException;


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
    void removeRdfTriple(RdfTriple triple, String context)
            throws ContextNameSyntaxException, RdfTripleSyntaxException, SemanticRepositoryConnectionException;


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
    void clearContext(String context)
            throws ContextNameSyntaxException, NoSuchContextException, SemanticRepositoryConnectionException;


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
    List<SparqlSelectTuple> executeSparqlSelectQuery(String query)
            throws MalformedQueryException, SemanticRepositoryConnectionException;


    /**
     * Release connection to the pool.
     */
    void close();

}
