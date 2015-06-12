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

import java.util.ArrayList;
import java.util.List;

import pl.psnc.synat.wrdz.zmkd.service.ServiceFormParamInfo;
import pl.psnc.synat.wrdz.zmkd.service.ServiceInfo;
import pl.psnc.synat.wrdz.zmkd.service.ServiceQueryParamInfo;
import pl.psnc.synat.wrdz.zmkd.service.ServiceTemplateParamInfo;

/**
 * Director of the execution builders for non file parameters .
 */
public class NoFileExecutionInfoDirector {

    /**
     * Seed for the construction of the execution info - service info.
     */
    protected final ServiceInfo servInfo;


    /**
     * Constructor of the builder.
     * 
     * @param servInfo
     *            seed for the construction
     */
    public NoFileExecutionInfoDirector(ServiceInfo servInfo) {
        this.servInfo = servInfo;
    }


    /**
     * Creates the execution hop info based upon only its not file values.
     * 
     * @return execution hop info
     * @throws MissingRequiredParametersException
     *             when some parameters are missing
     */
    public ExecutionInfo create()
            throws MissingRequiredParametersException {
        ExecutionInfo execHopInfo = new ExecutionInfo();
        execHopInfo.setAddress(servInfo.getAddress());
        execHopInfo.setMethod(servInfo.getMethod());
        execHopInfo.setRequestType(servInfo.getRequestType());

        List<ExecutionTemplateParam> execTemplateParams = new ArrayList<ExecutionTemplateParam>();
        for (ServiceTemplateParamInfo transfTemplateParam : servInfo.getTemplateParams()) {
            ExecutionTemplateParamBuilder builder = new ExecutionTemplateParamBuilder(transfTemplateParam);
            execTemplateParams.add(builder.build());
        }
        execHopInfo.setTemplateParams(execTemplateParams);

        List<ExecutionQueryParam> execQueryParams = new ArrayList<ExecutionQueryParam>();
        for (ServiceQueryParamInfo transfQueryParam : servInfo.getQueryParams()) {
            ExecutionQueryParamBuilder builder = new ExecutionQueryParamBuilder(transfQueryParam);
            ExecutionQueryParam execQueryParam = builder.build();
            if (execQueryParam != null) {
                execQueryParams.add(execQueryParam);
            }
        }
        execHopInfo.setQueryParams(execQueryParams);

        if (servInfo.getBodyParam() != null) {
            NoFileExecutionBodyParamBuilder builder = new NoFileExecutionBodyParamBuilder(servInfo.getBodyParam());
            ExecutionBodyParam execBodyParam = builder.build();
            if (execBodyParam != null) {
                execHopInfo.setRequestParam(execBodyParam);
            }
        }

        List<ExecutionFormParam> execFormParams = new ArrayList<ExecutionFormParam>();
        for (ServiceFormParamInfo transfFormParam : servInfo.getFormParams()) {
            NoFileExecutionFormParamBuilder builder = new NoFileExecutionFormParamBuilder(transfFormParam);
            ExecutionFormParam execFormParam = builder.build();
            if (execFormParam != null) {
                execFormParams.add(execFormParam);
            }
        }
        execHopInfo.setFormParams(execFormParams);

        return execHopInfo;
    }

}
