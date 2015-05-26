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
package pl.psnc.synat.wrdz.zmkd.config;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;

/**
 * Provides an access to SPARQL queries used in the ZMKD module.
 */
@Singleton
public class SparqlQueries {

    /** Path to the file containing the sparql query that retrieves UDFR format IRI by PUID. */
    private static final String FORMAT_IRI_QUERY_FILE = "/sparqls/iri-by-puid.sparql";

    /** Path to the file containing the sparql query that retrieves format PUID by UDFR IRI. */
    private static final String FORMAT_PUID_QUERY_FILE = "/sparqls/puid-by-iri.sparql";

    /** Path to the file containing the sparql query that retrieves format mimetype and default extension by UDFR IRI. */
    private static final String FORMAT_MIMETYPE_QUERY_FILE = "/sparqls/mimetype-by-iri.sparql";

    /** Sparql query that retrieves UDFR format IRI by PUID. */
    private String formatIriQuery;

    /** Sparql query that retrieves format PUID by UDFR IRI. */
    private String formatPuidQuery;

    /** Sparql query that retrieves format mimetype and default extension by UDFR IRI. */
    private String formatMimetypeQuery;


    /**
     * Reads the sparql queries from resource files.
     */
    @PostConstruct
    protected void init() {
        formatIriQuery = readSparqlQuery(FORMAT_IRI_QUERY_FILE);
        formatPuidQuery = readSparqlQuery(FORMAT_PUID_QUERY_FILE);
        formatMimetypeQuery = readSparqlQuery(FORMAT_MIMETYPE_QUERY_FILE);
    }


    /**
     * Reads a SPARQL query from a given file.
     * 
     * @param queryFilePath
     *            path to a file with query
     * @return query as string
     */
    private String readSparqlQuery(String queryFilePath) {
        InputStream queryInput = null;
        try {
            queryInput = getClass().getResourceAsStream(queryFilePath);
            return IOUtils.toString(queryInput, "UTF-8");
        } catch (IOException e) {
            throw new WrdzRuntimeException("Could not read sparql query files", e);
        } finally {
            IOUtils.closeQuietly(queryInput);
        }
    }


    public String getFormatIriQuery() {
        return formatIriQuery;
    }


    public String getFormatPuidQuery() {
        return formatPuidQuery;
    }


    public String getFormatMimetypeQuery() {
        return formatMimetypeQuery;
    }

}
