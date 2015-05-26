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
package pl.psnc.synat.sra.owlim;

import javax.resource.spi.ConnectionRequestInfo;

/**
 * Specific parameters of request for connection to OWLIM-style semantic repository.
 * 
 */
public class OwlimSemanticRepositoryConnectionRequestInfo implements ConnectionRequestInfo {

    /**
     * Whether a connection to dArceo repository.
     */
    private final boolean dArceo;


    /**
     * Constructs parameters with the dArceo flag.
     * 
     * @param dArceo
     *            dArceo flag
     */
    public OwlimSemanticRepositoryConnectionRequestInfo(boolean dArceo) {
        this.dArceo = dArceo;
    }


    /**
     * Returns dArceo flag.
     * 
     * @return dArceo flag.
     */
    public boolean isdArceo() {
        return dArceo;
    }

}
