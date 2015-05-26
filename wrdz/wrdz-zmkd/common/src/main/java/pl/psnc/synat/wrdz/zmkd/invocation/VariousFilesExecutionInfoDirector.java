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
import java.util.Map;
import java.util.Map.Entry;

import pl.psnc.synat.wrdz.zmkd.entity.format.FileFormat;
import pl.psnc.synat.wrdz.zmkd.object.DataFileInfo;
import pl.psnc.synat.wrdz.zmkd.service.ServiceFormParamInfo;
import pl.psnc.synat.wrdz.zmkd.service.ServiceInfo;

/**
 * Director of the execution builders - in case many files of the various formats as an input.
 */
public class VariousFilesExecutionInfoDirector extends NoFileExecutionInfoDirector {

    /**
     * Constructor.
     * 
     * @param servInfo
     *            service info
     */
    public VariousFilesExecutionInfoDirector(ServiceInfo servInfo) {
        super(servInfo);
    }


    /**
     * Creates the execution hop info.
     * 
     * @param classifiedFileInfos
     *            classified (by file format) data files to put as parameters.
     * @param workDir
     *            working directory
     * @return execution hop info
     * @throws MissingRequiredParametersException
     *             when some parameters are missing
     * @throws ZipArchivingException
     *             when ZIP archiving failed
     */
    public ExecutionInfo create(Map<FileFormat, List<DataFileInfo>> classifiedFileInfos, File workDir)
            throws MissingRequiredParametersException, ZipArchivingException {
        ExecutionInfo execHopInfo = create();

        if (classifiedFileInfos.size() == 1) { // only when at most one file format
            if (servInfo.getBodyParam() != null) {
                FileFormat fileFormat = classifiedFileInfos.keySet().iterator().next();

                ManyFilesExecutionBodyParamBuilder builder = new ManyFilesExecutionBodyParamBuilder(
                        servInfo.getBodyParam(), classifiedFileInfos.get(fileFormat), fileFormat, workDir);
                ExecutionBodyParam execBodyParam = builder.build();
                if (execBodyParam != null) {
                    execHopInfo.setRequestParam(execBodyParam);
                }
            }
        }

        for (Entry<FileFormat, List<DataFileInfo>> classification : classifiedFileInfos.entrySet()) {

            List<ExecutionFormParam> execFormParams = execHopInfo.getFormParams();
            for (ServiceFormParamInfo transfFormParam : servInfo.getFormParams()) {
                ManyFilesExecutionFormParamBuilder builder = new ManyFilesExecutionFormParamBuilder(transfFormParam,
                        classification.getValue(), classification.getKey(), workDir);
                ExecutionFormParam execFormParam = builder.build();
                if (execFormParam != null) {
                    execFormParams.add(execFormParam);
                }
            }
        }

        return execHopInfo;
    }

}
