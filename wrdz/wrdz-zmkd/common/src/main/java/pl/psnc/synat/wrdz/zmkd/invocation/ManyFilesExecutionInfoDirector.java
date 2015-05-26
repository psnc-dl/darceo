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

import java.io.File;
import java.util.List;

import pl.psnc.synat.wrdz.zmkd.entity.format.FileFormat;
import pl.psnc.synat.wrdz.zmkd.object.DataFileInfo;
import pl.psnc.synat.wrdz.zmkd.service.ServiceFormParamInfo;
import pl.psnc.synat.wrdz.zmkd.service.ServiceInfo;

/**
 * Director of the execution builders - in case many files as input.
 */
public class ManyFilesExecutionInfoDirector extends NoFileExecutionInfoDirector {

    /**
     * Constructor.
     * 
     * @param servInfo
     *            service info
     */
    public ManyFilesExecutionInfoDirector(ServiceInfo servInfo) {
        super(servInfo);
    }


    /**
     * Creates the execution hop info.
     * 
     * @param dataFileInfos
     *            info about files to transform
     * @param fileFormat
     *            file format
     * @param workDir
     *            working directory
     * @return execution hop info
     * @throws MissingRequiredParametersException
     *             when some parameters are missing
     * @throws ZipArchivingException
     *             when ZIP archiving failed
     */
    public ExecutionInfo create(List<DataFileInfo> dataFileInfos, FileFormat fileFormat, File workDir)
            throws MissingRequiredParametersException, ZipArchivingException {
        ExecutionInfo execHopInfo = create();

        if (servInfo.getBodyParam() != null) {
            ManyFilesExecutionBodyParamBuilder builder = new ManyFilesExecutionBodyParamBuilder(
                    servInfo.getBodyParam(), dataFileInfos, fileFormat, workDir);
            ExecutionBodyParam execBodyParam = builder.build();
            if (execBodyParam != null) {
                execHopInfo.setRequestParam(execBodyParam);
            }
        }

        List<ExecutionFormParam> execFormParams = execHopInfo.getFormParams();
        for (ServiceFormParamInfo transfFormParam : servInfo.getFormParams()) {
            ManyFilesExecutionFormParamBuilder builder = new ManyFilesExecutionFormParamBuilder(transfFormParam,
                    dataFileInfos, fileFormat, workDir);
            ExecutionFormParam execFormParam = builder.build();
            if (execFormParam != null) {
                execFormParams.add(execFormParam);
            }
        }

        return execHopInfo;
    }

}
