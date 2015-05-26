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
 * Specific properties of a connection to a semantic repository.
 * 
 */
public class SemanticRepositoryConnectionSpec {

    /**
     * Whether a connection to dArceo repository - some logic is enabled for this repository.
     */
    private final boolean dArceo;


    /**
     * Constructs properties with dArceo flag set to false.
     * 
     */
    public SemanticRepositoryConnectionSpec() {
        this.dArceo = false;
    }


    /**
     * Constructs properties with dArceo flag.
     * 
     * @param dArceo
     *            dArceo flag
     */
    public SemanticRepositoryConnectionSpec(boolean dArceo) {
        this.dArceo = dArceo;
    }


    /**
     * Returns dArceo flag.
     * 
     * @return dArceo flag.
     */
    public boolean getDArceo() {
        return dArceo;
    }

}
