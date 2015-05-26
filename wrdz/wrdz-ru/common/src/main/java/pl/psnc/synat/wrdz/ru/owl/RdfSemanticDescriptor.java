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
import java.util.List;

/**
 * Info about RDF data that make up a semantic descriptor describing the service.
 * 
 */
public class RdfSemanticDescriptor {

    /**
     * Unique context for RDF data that make up a semantic descriptor.
     */
    private final String context;

    /**
     * List of RDF data.
     */
    private List<RdfData> rdfData;


    /**
     * Construct new object with a context.
     * 
     * @param context
     *            context
     */
    public RdfSemanticDescriptor(String context) {
        this.context = context;
        rdfData = new ArrayList<RdfData>();
    }


    public String getContext() {
        return context;
    }


    public List<RdfData> getRdfData() {
        return rdfData;
    }


    /**
     * Add new RDF data to the list.
     * 
     * @param rdfData
     *            new RDF data
     */
    public void addRdfData(RdfData rdfData) {
        this.rdfData.add(rdfData);
    }

}
