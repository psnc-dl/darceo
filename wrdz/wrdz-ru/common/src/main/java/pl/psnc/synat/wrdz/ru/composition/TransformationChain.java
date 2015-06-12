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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for transferring a transformation chain..
 */
public class TransformationChain implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -8859844275166845799L;

    /**
     * List of services.
     */
    private List<Transformation> chain;

    /**
     * The total cost of execution of all services in a chain.
     */
    private Integer executionCost;


    /**
     * Default constructor.
     */
    public TransformationChain() {
        this.chain = new ArrayList<Transformation>();
        this.executionCost = null;
    }


    /**
     * Adds a transformation DTO to the chain.
     * 
     * @param transformation
     *            transformation DTO
     * @param executionCost
     *            cost of the transformation
     */
    public void addTransformation(Transformation transformation, Integer executionCost) {
        chain.add(transformation);
        if (executionCost != null) {
            if (this.executionCost != null) {
                this.executionCost += executionCost;
            } else {
                this.executionCost = executionCost;
            }
        }
    }


    /**
     * Returns the size of a transformation chain.
     * 
     * @return size of a chain
     */
    public int getSize() {
        return chain.size();
    }


    public List<Transformation> getTransformations() {
        return chain;
    }


    public Integer getExecutionCost() {
        return executionCost;
    }

}
