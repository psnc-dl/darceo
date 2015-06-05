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

/**
 * "Add RDF data" operation to semantic repository.
 * 
 */
public class SemanticRepositoryAddRdfDataOperation extends SemanticRepositoryWOOperation {

    /**
     * URL to RDF data.
     */
    private final URL url;

    /**
     * Base URI of the rdf data.
     */
    private final URI baseUri;

    /**
     * Context to which RDF data will be added.
     */
    private final String context;


    /**
     * Constructs operation.
     * 
     * @param url
     *            URL to RDF data
     * @param baseUri
     *            base URI of the rdf data
     * @param context
     *            context to which RDF data will be added
     */
    public SemanticRepositoryAddRdfDataOperation(URL url, URI baseUri, String context) {
        this.url = url;
        this.baseUri = baseUri;
        this.context = context;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("AddRdfData Operation ");
        sb.append("[url = ").append(url);
        sb.append(", baseUri = ").append(baseUri);
        sb.append(", context = ").append(context);
        sb.append("]");
        return sb.toString();
    }


    public URL getUrl() {
        return url;
    }


    public URI getBaseUri() {
        return baseUri;
    }


    public String getContext() {
        return context;
    }

}
