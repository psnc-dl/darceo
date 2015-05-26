﻿/**
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

/**
 * "Remove RDF triple" operation to semantic repository.
 * 
 */
public class SemanticRepositoryRemoveRdfTripleOperation extends SemanticRepositoryWOOperation {

    /**
     * Subject.
     */
    private final RdfTriple triple;

    /**
     * Context to which RDF data will be added.
     */
    private final String context;


    /**
     * Constructs operation.
     * 
     * @param triple
     *            RDF triple
     * @param context
     *            context in which RDF data are restricted
     */
    public SemanticRepositoryRemoveRdfTripleOperation(RdfTriple triple, String context) {
        super();
        this.triple = triple;
        this.context = context;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("RemoveRdfTriple Operation ");
        sb.append("[triple = ").append(triple);
        sb.append(", context = ").append(context);
        sb.append("]");
        return sb.toString();
    }

}
