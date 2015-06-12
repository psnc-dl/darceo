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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.common.utility.ZipUtility;
import pl.psnc.synat.wrdz.zmkd.entity.format.FileFormat;
import pl.psnc.synat.wrdz.zmkd.object.DataFileInfo;
import pl.psnc.synat.wrdz.zmkd.service.ServiceFormParamInfo;

/**
 * Builder of an execution form parameter for one file as input.
 */
public class OneFileExecutionFormParamBuilder {

    /**
     * Seed for the construction of the execution form parameter.
     */
    protected final ServiceFormParamInfo servFormParam;

    /**
     * Info about data file to put as a parameter.
     */
    private final DataFileInfo dataFileInfo;

    /**
     * File format.
     */
    private final FileFormat fileFormat;

    /**
     * Working directory.
     */
    private final File workDir;


    /**
     * Constructor.
     * 
     * @param servFormParam
     *            seed
     * @param dataFileInfo
     *            info about data file to put as a parameter.
     * @param fileFormat
     *            file format
     * @param workDir
     *            working directory
     */
    public OneFileExecutionFormParamBuilder(ServiceFormParamInfo servFormParam, DataFileInfo dataFileInfo,
            FileFormat fileFormat, File workDir) {
        this.servFormParam = servFormParam;
        this.dataFileInfo = dataFileInfo;
        this.fileFormat = fileFormat;
        this.workDir = workDir;
    }


    /**
     * Builds the execution form parameter based upon its one file value.
     * 
     * @return execution request parameter
     * @throws MissingRequiredParametersException
     *             when some required values are absent
     * @throws ZipArchivingException
     *             when ZIP archiving failed
     */
    public ExecutionFormParam build()
            throws MissingRequiredParametersException, ZipArchivingException {
        if (servFormParam.getSemanticType().equals(InvocationConsts.FILE_TYPE)
                || servFormParam.getSemanticType().equals(InvocationConsts.FILE_BUNDLE_TYPE)) {
            if (servFormParam.getMimetype() != null) {
                ExecutionFormParam execFormParam = new ExecutionFormParam();
                if (servFormParam.isRequired() && (servFormParam.getValues() == null && dataFileInfo == null)) {
                    throw new MissingRequiredParametersException("Parameter " + servFormParam.getName()
                            + " is required but there is no value for it.");
                }
                execFormParam.setName(servFormParam.getName());
                String mimetype = findMimetypeOption();
                if (servFormParam.getValues() != null) {
                    throw new WrdzRuntimeException("Files other than being converted are not allowed now!");
                } else if (dataFileInfo != null) {
                    if (servFormParam.isBundle() && InvocationConsts.MIMETYPE_APPLICATION_ZIP.equals(mimetype)) {
                        List<File> srcFiles = new ArrayList<File>();
                        srcFiles.add(dataFileInfo.getFile());
                        File destFile = new File(workDir, FilenameUtils.getBaseName(dataFileInfo.getPath()) + ".zip");
                        try {
                            ZipUtility.zip(srcFiles, destFile);
                        } catch (IOException e) {
                            throw new ZipArchivingException(e);
                        }
                        execFormParam.addFileValue(new FileValue(destFile, mimetype, destFile.getName()));
                    } else {
                        execFormParam.addFileValue(new FileValue(dataFileInfo.getFile(), mimetype, FilenameUtils
                                .getName(dataFileInfo.getPath())));
                    }
                }
                return execFormParam;
            }
        }
        return null;
    }


    /**
     * Finds a proper mimetype.
     * 
     * @return mimetype or null
     */
    private String findMimetypeOption() {
        return servFormParam.getMimetype();
    }

}
