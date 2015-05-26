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
package pl.psnc.synat.wrdz.ru.composition;

import java.util.Set;

/**
 * DTO for transferring informations about advanced delivery services.
 */
public class AdvancedDelivery extends AbstractService {

    /** Serial Version UID. */
    private static final long serialVersionUID = 7196801132700363013L;

    /**
     * Set of input format (IRI) of the advanced delivery service.
     */
    private Set<String> inputFormatIris;

    /**
     * Total cost of execution of the advanced delivery service.
     */
    private Integer executionCost;


    public Set<String> getInputFormatIris() {
        return inputFormatIris;
    }


    public void setInputFormatIris(Set<String> inputFormatIris) {
        this.inputFormatIris = inputFormatIris;
    }


    public Integer getExecutionCost() {
        return executionCost;
    }


    public void setExecutionCost(Integer executionCost) {
        this.executionCost = executionCost;
    }

}
