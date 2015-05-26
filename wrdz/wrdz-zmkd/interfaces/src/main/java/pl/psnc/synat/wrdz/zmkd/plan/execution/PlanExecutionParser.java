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
package pl.psnc.synat.wrdz.zmkd.plan.execution;

import java.util.List;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.zmkd.entity.plan.Delivery;
import pl.psnc.synat.wrdz.zmkd.entity.plan.TransformationPath;

/**
 * Specifies interface of the plan execution parser.
 */
@Local
public interface PlanExecutionParser {

    /**
     * Parses the transformation path to the necessary information how to invoke services of this path.
     * 
     * @param path
     *            transformation path
     * @return list of info about transformations
     * @throws InconsistentServiceDescriptionException
     *             when some missing or ambiguous values between OWL-S and WADL description occur
     * 
     */
    List<TransformationInfo> parseTransformationPath(TransformationPath path)
            throws InconsistentServiceDescriptionException;


    /**
     * Parses the delivery description to the necessary information how to invoke the service of this delivery.
     * 
     * @param delivery
     *            delivery service description
     * @return info about delivery
     * @throws InconsistentServiceDescriptionException
     *             when some missing or ambiguous values between OWL-S and WADL description occur
     * 
     */
    DeliveryInfo parseDelivery(Delivery delivery)
            throws InconsistentServiceDescriptionException;

}
