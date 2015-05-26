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

import pl.psnc.synat.wrdz.zmkd.object.DigitalObjectInfo;

/**
 * Specifies interface of the plan execution manager.
 */
@Local
public interface PlanExecutionManager {

    /**
     * Transforms an object described by objectInfo invoking transformation services described by the path.
     * 
     * @param objectInfo
     *            info describing object to transform
     * @param path
     *            info about transformation path
     * @throws TransformationException
     *             when something went wrong
     */
    void transform(DigitalObjectInfo objectInfo, List<TransformationInfo> path)
            throws TransformationException;


    /**
     * Deliver an object described by objectInfo invoking the delivery service.
     * 
     * @param objectInfo
     *            info describing object to deliver
     * @param deliveryInfo
     *            info about delivery
     * @return location of the client of the advanced delivery service
     * @throws DeliveryException
     *             when something went wrong
     */
    String deliver(DigitalObjectInfo objectInfo, DeliveryInfo deliveryInfo)
            throws DeliveryException;

}
