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
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.common.utility.ZipUtility;
import pl.psnc.synat.wrdz.zmkd.entity.format.FileFormat;
import pl.psnc.synat.wrdz.zmkd.object.DataFileInfo;
import pl.psnc.synat.wrdz.zmkd.service.ServiceBodyParamInfo;

/**
 * Builder of an execution body parameter for one file as input.
 */
public class OneFileExecutionBodyParamBuilder {

    /**
     * Seed for the construction of the execution body parameter.
     */
    protected final ServiceBodyParamInfo servBodyParam;

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
     * @param servBodyParam
     *            seed
     * @param dataFileInfo
     *            info about data file to put as a parameter.
     * @param fileFormat
     *            file format
     * @param workDir
     *            working directory
     */
    public OneFileExecutionBodyParamBuilder(ServiceBodyParamInfo servBodyParam, DataFileInfo dataFileInfo,
            FileFormat fileFormat, File workDir) {
        this.servBodyParam = servBodyParam;
        this.dataFileInfo = dataFileInfo;
        this.fileFormat = fileFormat;
        this.workDir = workDir;
    }


    /**
     * Builds the execution body parameter based upon its one file value.
     * 
     * @return execution body parameter
     * @throws MissingRequiredParametersException
     *             when some required values are absent
     * @throws ZipArchivingException
     *             when ZIP archiving failed
     */
    public ExecutionBodyParam build()
            throws MissingRequiredParametersException, ZipArchivingException {
        if (servBodyParam.getSemanticType().equals(InvocationConsts.FILE_TYPE)
                || servBodyParam.getSemanticType().equals(InvocationConsts.FILE_BUNDLE_TYPE)) {
            if (servBodyParam.getMimetypes() != null) {
                ExecutionBodyParam execBodyParam = new ExecutionBodyParam();
                if (servBodyParam.isRequired() && (servBodyParam.getValue() == null && dataFileInfo == null)) {
                    throw new MissingRequiredParametersException(
                            "Body Parameter is required but there is no value for it.");
                }
                String mimetype = findMimetypeOption();
                if (servBodyParam.getValue() != null) {
                    throw new WrdzRuntimeException("Files other than being converted are not allowed now!");
                } else if (dataFileInfo != null) {
                    if (servBodyParam.isBundle() && InvocationConsts.MIMETYPE_APPLICATION_ZIP.equals(mimetype)) {
                        List<File> srcFiles = new ArrayList<File>();
                        srcFiles.add(dataFileInfo.getFile());
                        File destFile = new File(workDir, FilenameUtils.getBaseName(dataFileInfo.getPath()) + ".zip");
                        try {
                            ZipUtility.zip(srcFiles, destFile);
                        } catch (IOException e) {
                            throw new ZipArchivingException(e);
                        }
                        execBodyParam.setFileValue(new FileValue(destFile, mimetype, destFile.getName()));
                    } else {
                        execBodyParam.setFileValue(new FileValue(dataFileInfo.getFile(), mimetype, FilenameUtils
                                .getName(dataFileInfo.getPath())));
                    }
                }
                return execBodyParam;
            }
        }
        return null;
    }


    /**
     * Finds a proper mimetype from all alternatives parsed from WADL file.
     * 
     * @return mimetype or null when none matches
     */
    private String findMimetypeOption() {
        if (fileFormat.getMimetype() != null) {
            for (Entry<String, String> mimetype : servBodyParam.getMimetypes().entrySet()) {
                if (mimetype.getValue() != null && mimetype.getValue().equals(fileFormat.getMimetype())) {
                    return mimetype.getValue();
                }
            }
            return fileFormat.getMimetype();
        }
        if (servBodyParam.isBundle()) {
            for (Entry<String, String> mimetype : servBodyParam.getMimetypes().entrySet()) {
                if (!mimetype.getValue().equals(InvocationConsts.MIMETYPE_APPLICATION_ZIP)) {
                    return mimetype.getValue();
                }
            }
        }
        for (Entry<String, String> mimetype : servBodyParam.getMimetypes().entrySet()) {
            if (mimetype.getValue() != null) {
                return mimetype.getValue();
            }
        }
        return null;
    }

}
