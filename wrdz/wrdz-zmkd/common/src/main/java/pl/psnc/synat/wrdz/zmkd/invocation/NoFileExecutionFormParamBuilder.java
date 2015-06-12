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

import pl.psnc.synat.wrdz.zmkd.service.ServiceFormParamInfo;

/**
 * Builder of an execution form parameter for non file values.
 */
public class NoFileExecutionFormParamBuilder {

    /**
     * Seed for the construction of the execution form parameter.
     */
    protected final ServiceFormParamInfo servFormParam;


    /**
     * Constructor.
     * 
     * @param servFormParam
     *            seed
     */
    public NoFileExecutionFormParamBuilder(ServiceFormParamInfo servFormParam) {
        this.servFormParam = servFormParam;
    }


    /**
     * Builds the execution form parameter for non file values.
     * 
     * @return execution form parameter
     * @throws MissingRequiredParametersException
     *             when some required values are absent
     */
    public ExecutionFormParam build()
            throws MissingRequiredParametersException {
        if (!servFormParam.getSemanticType().equals(InvocationConsts.FILE_TYPE)
                && !servFormParam.getSemanticType().equals(InvocationConsts.FILE_BUNDLE_TYPE)) {
            if (servFormParam.getTechnicalType() != null) {
                ExecutionFormParam execFormParam = new ExecutionFormParam();
                if (servFormParam.isRequired() && servFormParam.getValues() == null) {
                    throw new MissingRequiredParametersException("Parameter " + servFormParam.getName()
                            + " is required but there is no value for it.");
                }
                execFormParam.setName(servFormParam.getName());
                if (servFormParam.getValues() != null) {
                    for (String value : servFormParam.getValues()) {
                        execFormParam.addValue(value);
                    }
                }
                return execFormParam;
            }
        }
        return null;
    }

}
