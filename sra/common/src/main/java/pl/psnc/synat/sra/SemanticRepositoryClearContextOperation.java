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
 * "Clear context" operation on a semantic repository.
 * 
 */
public class SemanticRepositoryClearContextOperation extends SemanticRepositoryWOOperation {

    /**
     * Context which will be cleared.
     */
    private final String context;


    /**
     * Constructs operation.
     * 
     * @param context
     *            context which will be cleared
     */
    public SemanticRepositoryClearContextOperation(String context) {
        this.context = context;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("ClearContext Operation ");
        sb.append("[context = ").append(context);
        sb.append("]");
        return sb.toString();
    }

}
