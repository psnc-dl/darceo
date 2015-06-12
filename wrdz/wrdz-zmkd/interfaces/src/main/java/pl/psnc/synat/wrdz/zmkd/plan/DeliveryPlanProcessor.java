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
package pl.psnc.synat.wrdz.zmkd.plan;

import java.util.concurrent.Future;

import javax.ejb.Local;

/**
 * Responsible for delivery plan processing.
 */
@Local
public interface DeliveryPlanProcessor {

    /**
     * Processes the delivery plan with the given id.
     * <p>
     * This method fetches the related object's files from ZMD, unzips them, parses their technical and administrative
     * metadata, passes all files that require conversion through the plan's conversion paths and forwards the converted
     * version of the object to the desired advanced delivery service.
     * 
     * If the object is not immediately available for download, the delivery plan executor is notified of this plan's
     * wait status, and this method exits.
     * 
     * @param planId
     *            plan identifier
     * @return null future object
     */
    Future<Void> process(String planId);
}
