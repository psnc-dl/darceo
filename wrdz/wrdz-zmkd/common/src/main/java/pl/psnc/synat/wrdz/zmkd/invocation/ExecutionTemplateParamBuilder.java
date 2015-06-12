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
package pl.psnc.synat.wrdz.zmkd.invocation;

import pl.psnc.synat.wrdz.zmkd.service.ServiceTemplateParamInfo;

/**
 * Builder of an execution template parameter.
 */
public class ExecutionTemplateParamBuilder {

    /**
     * Seed for the construction of the execution template parameter.
     */
    private final ServiceTemplateParamInfo servTemplateParam;


    /**
     * Constructor.
     * 
     * @param servTemplateParam
     *            seed
     */
    public ExecutionTemplateParamBuilder(ServiceTemplateParamInfo servTemplateParam) {
        this.servTemplateParam = servTemplateParam;
    }


    /**
     * Builds the execution template parameter.
     * 
     * @return execution template parameter
     * @throws MissingRequiredParametersException
     *             when some required values are absent
     */
    public ExecutionTemplateParam build()
            throws MissingRequiredParametersException {
        ExecutionTemplateParam execTemplateParam = new ExecutionTemplateParam();
        execTemplateParam.setName(servTemplateParam.getName());
        if (servTemplateParam.isRequired()
                && (servTemplateParam.getValue() == null || servTemplateParam.getValue().isEmpty())) {
            throw new MissingRequiredParametersException("Parameter " + servTemplateParam.getName()
                    + " is required but there is no value for it.");
        }
        execTemplateParam.setValue(servTemplateParam.getValue());
        return execTemplateParam;
    }

}
