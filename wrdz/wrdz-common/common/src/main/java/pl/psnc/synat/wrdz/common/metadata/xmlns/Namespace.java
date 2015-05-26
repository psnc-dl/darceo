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
package pl.psnc.synat.wrdz.common.metadata.xmlns;

/**
 * Class representimg XML namespace.
 */
public class Namespace {

    /**
     * URI of the namespace.
     */
    private final String uri;

    /**
     * Schema location for the namespace.
     */
    private final String schemaLocation;

    /**
     * Type of the namespace.
     */
    private final NamespaceType type;


    /**
     * Constructor of the namespace.
     * 
     * @param uri
     *            URI
     * @param schemaLocation
     *            schema location
     * @param type
     *            type
     */
    public Namespace(String uri, String schemaLocation, NamespaceType type) {
        this.uri = uri;
        this.schemaLocation = schemaLocation;
        this.type = type;
    }


    public String getUri() {
        return uri;
    }


    public String getSchemaLocation() {
        return schemaLocation;
    }


    public NamespaceType getType() {
        return type;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Namespace ");
        sb.append("[uri = ").append(uri);
        sb.append(", schemaLocation = ").append(schemaLocation);
        sb.append(", type = ").append(type);
        sb.append("]");
        return sb.toString();
    }

}
