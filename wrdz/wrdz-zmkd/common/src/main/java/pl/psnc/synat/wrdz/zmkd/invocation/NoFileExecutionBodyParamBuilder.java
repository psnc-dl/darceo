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

import pl.psnc.synat.wrdz.zmkd.service.ServiceBodyParamInfo;

/**
 * Builder of an execution body parameter that does not cover a file parameter.
 */
public class NoFileExecutionBodyParamBuilder {

    /**
     * Seed for the construction of the execution body parameter.
     */
    protected final ServiceBodyParamInfo servBodyParam;


    /**
     * Constructor.
     * 
     * @param servBodyParam
     *            seed
     */
    public NoFileExecutionBodyParamBuilder(ServiceBodyParamInfo servBodyParam) {
        this.servBodyParam = servBodyParam;
    }


    /**
     * Builds the execution request parameter based upon its non file values.
     * 
     * @return execution body parameter
     * @throws MissingRequiredParametersException
     *             when some required values are absent
     */
    public ExecutionBodyParam build()
            throws MissingRequiredParametersException {
        if (!servBodyParam.getSemanticType().equals(InvocationConsts.FILE_TYPE)
                && !servBodyParam.getSemanticType().equals(InvocationConsts.FILE_BUNDLE_TYPE)) {
            if (servBodyParam.getTechnicalTypes() != null) {
                ExecutionBodyParam execBodyParam = new ExecutionBodyParam();
                if (servBodyParam.isRequired() && servBodyParam.getValue() == null) {
                    throw new MissingRequiredParametersException(
                            "Body Parameter is required but there is no value for it.");
                }
                execBodyParam.setValue(servBodyParam.getValue());
                return execBodyParam;
            }
        }
        return null;
    }
}
