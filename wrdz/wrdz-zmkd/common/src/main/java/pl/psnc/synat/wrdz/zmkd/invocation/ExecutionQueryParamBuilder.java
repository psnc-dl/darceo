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

import pl.psnc.synat.wrdz.zmkd.service.ServiceQueryParamInfo;

/**
 * Builder of an execution query parameter.
 */
public class ExecutionQueryParamBuilder {

    /**
     * XML Schema boolean type URI.
     */
    private static final String XS_BOOLEAN_TYPE = "http://www.w3.org/2001/XMLSchema#boolean";

    /**
     * Seed for the construction of the execution query parameter.
     */
    private final ServiceQueryParamInfo servQueryParam;


    /**
     * Constructor.
     * 
     * @param servQueryParam
     *            seed
     */
    public ExecutionQueryParamBuilder(ServiceQueryParamInfo servQueryParam) {
        this.servQueryParam = servQueryParam;
    }


    /**
     * Builds the execution query parameter.
     * 
     * @return execution query parameter
     * @throws MissingRequiredParametersException
     *             when some required values are absent
     */
    public ExecutionQueryParam build()
            throws MissingRequiredParametersException {
        ExecutionQueryParam execQueryParam = new ExecutionQueryParam();
        execQueryParam.setName(servQueryParam.getName());
        if (servQueryParam.isRequired() && servQueryParam.getValues() == null) {
            throw new MissingRequiredParametersException("Parameter " + servQueryParam.getName()
                    + " is required but there is no value for it.");
        }
        if (XS_BOOLEAN_TYPE.equals(servQueryParam.getTechnicalType())) {
            if (servQueryParam.getValues() == null) {
                return null;
            } else { // boolean can have only one value
                if (servQueryParam.getValues().get(0).equalsIgnoreCase("true")) {
                    return execQueryParam; // empty list of values means that this parameter have to be present
                } else {
                    return null; // false value is the same as no value
                }
            }
        }
        for (String value : servQueryParam.getValues()) {
            execQueryParam.addValue(value);
        }
        return execQueryParam;
    }

}
