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

import pl.psnc.synat.wrdz.zmkd.entity.format.FileFormat;
import pl.psnc.synat.wrdz.zmkd.service.ServiceInfo;

/**
 * Detailed information about a particular delivery.
 */
public class DeliveryInfo extends ServiceInfo {

    /**
     * Input file formats.
     */
    private List<FileFormat> inputFileFormats;


    public List<FileFormat> getInputFileFormats() {
        return inputFileFormats;
    }


    public void setInputFileFormats(List<FileFormat> inputFileFormats) {
        this.inputFileFormats = inputFileFormats;
    }

}
