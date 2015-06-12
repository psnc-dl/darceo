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

import java.net.URI;
import java.net.URL;

/**
 * Info about RDF data.
 */
public class RdfData {

    /**
     * Url to RDF data.
     */
    private final URL url;

    /**
     * Base URI of the rdf data.
     */
    private final URI baseUri;


    /**
     * Constructs new object with URL to RDF data and its base URI.
     * 
     * @param url
     *            URL
     * @param baseUri
     *            base URI
     */
    public RdfData(URL url, URI baseUri) {
        this.url = url;
        this.baseUri = baseUri;
    }


    public URL getUrl() {
        return url;
    }


    public URI getBaseUri() {
        return baseUri;
    }

}
