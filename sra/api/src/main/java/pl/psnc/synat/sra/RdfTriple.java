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

/**
 * RDF triple.
 */
public class RdfTriple {

    /**
     * Subject.
     */
    private final String subject;

    /**
     * Predicate.
     */
    private final String predicate;

    /**
     * Object.
     */
    private final String object;


    /**
     * Constructs a RDF triple.
     * 
     * @param subject
     *            subject
     * @param predicate
     *            predicate
     * @param object
     *            object
     */
    public RdfTriple(String subject, String predicate, String object) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }


    public String getSubject() {
        return subject;
    }


    public String getPredicate() {
        return predicate;
    }


    public String getObject() {
        return object;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RdfTriple ");
        sb.append("[subject = ").append(subject);
        sb.append(", predicate = ").append(predicate);
        sb.append(", object = ").append(object);
        sb.append("]");
        return sb.toString();
    }

}
