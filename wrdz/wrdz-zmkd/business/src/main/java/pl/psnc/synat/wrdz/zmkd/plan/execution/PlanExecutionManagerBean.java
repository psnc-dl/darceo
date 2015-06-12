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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.common.utility.UuidGenerator;
import pl.psnc.synat.wrdz.common.utility.ZipUtility;
import pl.psnc.synat.wrdz.zmkd.entity.format.FileFormat;
import pl.psnc.synat.wrdz.zmkd.format.FileFormatDictionaryBean;
import pl.psnc.synat.wrdz.zmkd.format.UdfrServiceException;
import pl.psnc.synat.wrdz.zmkd.format.UnrecognizedPuidException;
import pl.psnc.synat.wrdz.zmkd.invocation.ExecutionInfo;
import pl.psnc.synat.wrdz.zmkd.invocation.ExecutionOutcome;
import pl.psnc.synat.wrdz.zmkd.invocation.InvalidHttpRequestException;
import pl.psnc.synat.wrdz.zmkd.invocation.InvalidHttpResponseException;
import pl.psnc.synat.wrdz.zmkd.invocation.InvocationConsts;
import pl.psnc.synat.wrdz.zmkd.invocation.ManyFilesExecutionInfoDirector;
import pl.psnc.synat.wrdz.zmkd.invocation.MissingRequiredParametersException;
import pl.psnc.synat.wrdz.zmkd.invocation.OneFileExecutionInfoDirector;
import pl.psnc.synat.wrdz.zmkd.invocation.RestServiceCaller;
import pl.psnc.synat.wrdz.zmkd.invocation.UnexpectedHttpResponseException;
import pl.psnc.synat.wrdz.zmkd.invocation.VariousFilesExecutionInfoDirector;
import pl.psnc.synat.wrdz.zmkd.invocation.ZipArchivingException;
import pl.psnc.synat.wrdz.zmkd.object.DataFileInfo;
import pl.psnc.synat.wrdz.zmkd.object.DataFileInfoComparator;
import pl.psnc.synat.wrdz.zmkd.object.DigitalObjectInfo;
import pl.psnc.synat.wrdz.zmkd.service.OutcomeStyle;
import pl.psnc.synat.wrdz.zmkd.service.ServiceOutcomeInfo;

/**
 * Transformation manager bean.
 */
@Stateless
public class PlanExecutionManagerBean implements PlanExecutionManager {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(PlanExecutionManagerBean.class);

    /** File format dictionary bean. */
    @EJB
    private FileFormatDictionaryBean fileFormatDictionaryBean;

    /** Rest Service caller. */
    @Inject
    private RestServiceCaller restServiceCaller;

    /** Generates file identifiers. */
    @Inject
    private UuidGenerator uuidGenerator;


    @Override
    public void transform(DigitalObjectInfo objectInfo, List<TransformationInfo> path)
            throws TransformationException {

        String inputPuid = path.get(0).getInputFileFormat().getPuid();
        Collections.sort(objectInfo.getFiles(), new DataFileInfoComparator());
        List<DataFileInfo> transformedFiles = filterByFormat(objectInfo.getFiles(), inputPuid); // files to transform
        List<DataFileInfo> filesToTransform = new ArrayList<DataFileInfo>();

        for (TransformationInfo transfInfo : path) {
            filesToTransform.clear();
            filesToTransform.addAll(transformedFiles);
            transformedFiles.clear();
            switch (transfInfo.getType()) {
                case ONE_TO_ONE:
                    transformedFiles.addAll(transformOneToOne(transfInfo, filesToTransform, objectInfo.getWorkDir()));
                    break;
                case ONE_TO_MANY:
                    transformedFiles.addAll(transformOneToMany(transfInfo, filesToTransform, objectInfo.getWorkDir(),
                        objectInfo.getFiles()));
                    break;
                case MANY_TO_ONE:
                    transformedFiles.add(transformManyToOne(transfInfo, filesToTransform, objectInfo.getWorkDir(),
                        objectInfo.getFiles()));
                    break;
                default:
                    throw new WrdzRuntimeException("Incorrect type of the transformation");

            }
            objectInfo.getFiles().removeAll(filesToTransform);
            objectInfo.getFiles().addAll(transformedFiles);
            Collections.sort(objectInfo.getFiles(), new DataFileInfoComparator());
        }
    }


    @Override
    public String deliver(DigitalObjectInfo objectInfo, DeliveryInfo deliveryInfo)
            throws DeliveryException {
        Map<FileFormat, List<DataFileInfo>> classification = classifyByFormat(objectInfo.getFiles());
        ExecutionInfo execHopInfo = null;
        ExecutionOutcome execOutcome = null;
        List<ServiceOutcomeInfo> validatedOutcomes = null;
        try {
            execHopInfo = new VariousFilesExecutionInfoDirector(deliveryInfo).create(classification,
                objectInfo.getWorkDir());
            execOutcome = restServiceCaller.invoke(execHopInfo);
            validatedOutcomes = restServiceCaller.validateOutcome(execOutcome, deliveryInfo.getOutcomes());
        } catch (MissingRequiredParametersException e) {
            logger.error(e.toString(), e);
            throw new DeliveryException(deliveryInfo.getServiceIri(), e);
        } catch (ZipArchivingException e) {
            logger.error(e.toString(), e);
            throw new DeliveryException(deliveryInfo.getServiceIri(), e);
        } catch (InvalidHttpRequestException e) {
            logger.error(e.toString(), e);
            throw new DeliveryException(deliveryInfo.getServiceIri(), e);
        } catch (InvalidHttpResponseException e) {
            logger.error(e.toString(), e);
            throw new DeliveryException(deliveryInfo.getServiceIri(), e);
        } catch (UnexpectedHttpResponseException e) {
            try {
                logger.error(IOUtils.toString(execOutcome.getContent()), e);
            } catch (IOException f) {
                logger.debug("Error retrieving the content", f);
            }
            IOUtils.closeQuietly(execOutcome.getContent());
            throw new DeliveryException(deliveryInfo.getServiceIri(), e);
        }
        try {
            return retrieveClientLocationFromOutcome(execOutcome, validatedOutcomes);
        } catch (InvalidHttpResponseException e) {
            logger.error(e.toString(), e);
            throw new DeliveryException(deliveryInfo.getServiceIri(), e);
        } catch (UnexpectedHttpResponseException e) {
            logger.error(e.toString(), e);
            throw new DeliveryException(deliveryInfo.getServiceIri(), e);
        } finally {
            IOUtils.closeQuietly(execOutcome.getContent());
        }
    }


    /**
     * Filters the list of digital object files, retrieved from METS, by format PRONOM identifier.
     * 
     * @param files
     *            list of digital object files structures
     * @param puid
     *            PRONOM identifier retrieved from migration plan
     * @return filtered list
     */
    private List<DataFileInfo> filterByFormat(List<DataFileInfo> files, final String puid) {
        List<DataFileInfo> filtered = new ArrayList<DataFileInfo>();
        for (DataFileInfo file : files) {
            if (puid.equals(file.getPuid())) {
                filtered.add(file);
            }
        }
        return filtered;
    }


    /**
     * Classify the list of digital object files, retrieved from METS, by format PRONOM identifier.
     * 
     * @param files
     *            list of digital object files structures
     * @return map with PRONOM identifiers and list of files of that type
     */
    private Map<FileFormat, List<DataFileInfo>> classifyByFormat(List<DataFileInfo> files) {
        Map<FileFormat, List<DataFileInfo>> classification = new HashMap<FileFormat, List<DataFileInfo>>();
        for (DataFileInfo file : files) {
            FileFormat fileFormat = getFileFormatOfDataFile(file);
            if (fileFormat != null) {
                List<DataFileInfo> dataFileInfos = classification.get(fileFormat);
                if (dataFileInfos == null) {
                    dataFileInfos = new ArrayList<DataFileInfo>();
                    classification.put(fileFormat, dataFileInfos);
                }
                dataFileInfos.add(file);
            }
        }
        return classification;
    }


    /**
     * Transforms a list of files by invoking the transformation service for each file.
     * 
     * @param transfInfo
     *            info about transformation
     * @param filesToTransform
     *            list of files to transform
     * @param workDir
     *            working directory
     * @return list of transformed files
     * @throws TransformationException
     *             when something went wrong
     */
    private List<DataFileInfo> transformOneToOne(TransformationInfo transfInfo, List<DataFileInfo> filesToTransform,
            File workDir)
            throws TransformationException {
        List<DataFileInfo> result = new ArrayList<DataFileInfo>();
        for (DataFileInfo dataFileInfo : filesToTransform) {
            FileFormat fileFormat = getFileFormatOfDataFile(dataFileInfo);
            ExecutionInfo execHopInfo = null;
            ExecutionOutcome execOutcome = null;
            List<ServiceOutcomeInfo> validatedOutcomes = null;
            try {
                execHopInfo = new OneFileExecutionInfoDirector(transfInfo).create(dataFileInfo, fileFormat, workDir);
                execOutcome = restServiceCaller.invoke(execHopInfo);
                validatedOutcomes = restServiceCaller.validateOutcome(execOutcome, transfInfo.getOutcomes());
            } catch (MissingRequiredParametersException e) {
                logger.error(e.toString(), e);
                throw new TransformationException(transfInfo.getServiceIri(), e);
            } catch (ZipArchivingException e) {
                logger.error(e.toString(), e);
                throw new TransformationException(transfInfo.getServiceIri(), e);
            } catch (InvalidHttpRequestException e) {
                logger.error(e.toString(), e);
                throw new TransformationException(transfInfo.getServiceIri(), e);
            } catch (InvalidHttpResponseException e) {
                logger.error(e.toString(), e);
                throw new TransformationException(transfInfo.getServiceIri(), e);
            } catch (UnexpectedHttpResponseException e) {
                try {
                    logger.error(IOUtils.toString(execOutcome.getContent()), e);
                } catch (IOException f) {
                    logger.debug("Error retrieving the content", f);
                }
                IOUtils.closeQuietly(execOutcome.getContent());
                throw new TransformationException(transfInfo.getServiceIri(), e);
            }
            try {
                result.add(retrieveDataFileFromOutcome(execOutcome, validatedOutcomes,
                    transfInfo.getOutputFileFormat(), dataFileInfo, workDir));
            } catch (InvalidHttpResponseException e) {
                logger.error(e.toString(), e);
                throw new TransformationException(transfInfo.getServiceIri(), e);
            } catch (UnexpectedHttpResponseException e) {
                logger.error(e.toString(), e);
                throw new TransformationException(transfInfo.getServiceIri(), e);
            } finally {
                IOUtils.closeQuietly(execOutcome.getContent());
            }
        }
        return result;
    }


    /**
     * Transforms a list of files by invoking the transformation service for each file. Moreover updates sequence
     * numbers for the remaining files, passed as the last argument of this method.
     * 
     * @param transfInfo
     *            info about transformation
     * @param filesToTransform
     *            list of files to transform
     * @param workDir
     *            working directory
     * @param allFilesToUpdateSequences
     *            all files - in order to update their sequence numbers
     * @return list of transformed files
     * @throws TransformationException
     *             when something went wrong
     */
    private List<DataFileInfo> transformOneToMany(TransformationInfo transfInfo, List<DataFileInfo> filesToTransform,
            File workDir, List<DataFileInfo> allFilesToUpdateSequences)
            throws TransformationException {
        List<DataFileInfo> result = new ArrayList<DataFileInfo>();
        for (DataFileInfo dataFileInfo : filesToTransform) {
            FileFormat fileFormat = getFileFormatOfDataFile(dataFileInfo);
            ExecutionInfo execHopInfo = null;
            ExecutionOutcome execOutcome = null;
            List<ServiceOutcomeInfo> validatedOutcomes = null;
            try {
                execHopInfo = new OneFileExecutionInfoDirector(transfInfo).create(dataFileInfo, fileFormat, workDir);
                execOutcome = restServiceCaller.invoke(execHopInfo);
                validatedOutcomes = restServiceCaller.validateOutcome(execOutcome, transfInfo.getOutcomes());
            } catch (MissingRequiredParametersException e) {
                logger.error(e.toString(), e);
                throw new TransformationException(transfInfo.getServiceIri(), e);
            } catch (ZipArchivingException e) {
                logger.error(e.toString(), e);
                throw new TransformationException(transfInfo.getServiceIri(), e);
            } catch (InvalidHttpRequestException e) {
                logger.error(e.toString(), e);
                throw new TransformationException(transfInfo.getServiceIri(), e);
            } catch (InvalidHttpResponseException e) {
                logger.error(e.toString(), e);
                throw new TransformationException(transfInfo.getServiceIri(), e);
            } catch (UnexpectedHttpResponseException e) {
                try {
                    logger.error(IOUtils.toString(execOutcome.getContent()), e);
                } catch (IOException f) {
                    logger.debug("Error retrieving the content", f);
                }
                IOUtils.closeQuietly(execOutcome.getContent());
                throw new TransformationException(transfInfo.getServiceIri(), e);
            }
            try {
                List<DataFileInfo> transformed = retrieveDataFilesFromOutcome(execOutcome, validatedOutcomes,
                    transfInfo.getOutputFileFormat(), dataFileInfo, workDir);
                if (transformed.size() > 1) { //update sequence numbers in case of many result files
                    Integer lastSequence = transformed.get(transformed.size() - 1).getSequence();
                    if (lastSequence != null) {
                        Integer difference = lastSequence - dataFileInfo.getSequence();
                        int i = allFilesToUpdateSequences.size() - 1;
                        DataFileInfo current = allFilesToUpdateSequences.get(i);
                        while (!current.equals(dataFileInfo)) {
                            current.addToSequence(difference);
                            i--;
                            current = allFilesToUpdateSequences.get(i);
                        }
                    }
                }
                result.addAll(transformed);
            } catch (ZipArchivingException e) {
                logger.error(e.toString(), e);
                throw new TransformationException(transfInfo.getServiceIri(), e);
            } catch (InvalidHttpResponseException e) {
                logger.error(e.toString(), e);
                throw new TransformationException(transfInfo.getServiceIri(), e);
            } catch (UnexpectedHttpResponseException e) {
                logger.error(e.toString(), e);
                throw new TransformationException(transfInfo.getServiceIri(), e);
            } finally {
                IOUtils.closeQuietly(execOutcome.getContent());
            }
        }
        return result;
    }


    /**
     * Transforms a list of files by invoking the transformation service for all files once. As a result we suppose the
     * only one file. Moreover updates sequence numbers for the remaining files, passed as the last argument of this
     * method.
     * 
     * @param transfInfo
     *            info about transformation
     * @param filesToTransform
     *            list of files to transform
     * @param workDir
     *            working directory
     * @param allFilesToUpdateSequences
     *            all files - in order to update their sequence numbers
     * @return transformed files
     * @throws TransformationException
     *             when something went wrong
     */
    private DataFileInfo transformManyToOne(TransformationInfo transfInfo, List<DataFileInfo> filesToTransform,
            File workDir, List<DataFileInfo> allFilesToUpdateSequences)
            throws TransformationException {
        FileFormat fileFormat = getFileFormatOfDataFile(filesToTransform.get(0));
        ExecutionInfo execHopInfo = null;
        ExecutionOutcome execOutcome = null;
        List<ServiceOutcomeInfo> validatedOutcomes = null;
        try {
            execHopInfo = new ManyFilesExecutionInfoDirector(transfInfo).create(filesToTransform, fileFormat, workDir);
            execOutcome = restServiceCaller.invoke(execHopInfo);
            validatedOutcomes = restServiceCaller.validateOutcome(execOutcome, transfInfo.getOutcomes());
        } catch (MissingRequiredParametersException e) {
            logger.error(e.toString(), e);
            throw new TransformationException(transfInfo.getServiceIri(), e);
        } catch (ZipArchivingException e) {
            logger.error(e.toString(), e);
            throw new TransformationException(transfInfo.getServiceIri(), e);
        } catch (InvalidHttpRequestException e) {
            logger.error(e.toString(), e);
            throw new TransformationException(transfInfo.getServiceIri(), e);
        } catch (InvalidHttpResponseException e) {
            logger.error(e.toString(), e);
            throw new TransformationException(transfInfo.getServiceIri(), e);
        } catch (UnexpectedHttpResponseException e) {
            try {
                logger.error(IOUtils.toString(execOutcome.getContent()), e);
            } catch (IOException f) {
                logger.debug("Error retrieving the content", f);
            }
            IOUtils.closeQuietly(execOutcome.getContent());
            throw new TransformationException(transfInfo.getServiceIri(), e);
        }
        try {
            DataFileInfo transformed = retrieveDataFileFromOutcome(execOutcome, validatedOutcomes,
                transfInfo.getOutputFileFormat(), filesToTransform, workDir);
            Integer firstSequence = transformed.getSequence();
            if (firstSequence != null) {
                filesToTransform.get(filesToTransform.size() - 1);
                Integer difference = filesToTransform.get(filesToTransform.size() - 1).getSequence() - firstSequence;
                int i = allFilesToUpdateSequences.size() - 1;
                DataFileInfo current = allFilesToUpdateSequences.get(i);
                while (!filesToTransform.contains(current)) {
                    current.subtractFromSequence(difference);
                    i--;
                    current = allFilesToUpdateSequences.get(i);
                }
            }
            return transformed;
        } catch (InvalidHttpResponseException e) {
            logger.error(e.toString(), e);
            throw new TransformationException(transfInfo.getServiceIri(), e);
        } catch (UnexpectedHttpResponseException e) {
            logger.error(e.toString(), e);
            throw new TransformationException(transfInfo.getServiceIri(), e);
        } finally {
            IOUtils.closeQuietly(execOutcome.getContent());
        }
    }


    /**
     * Retrieves from execution outcome the data file info. The remaining outcomes are omitted, since file
     * transformation is limited to chains of services that returns files always.
     * 
     * @param execOutcome
     *            execution outcome
     * @param servOutcomeInfos
     *            list of validated service outcome infos
     * @param outputFileFormat
     *            supposed output format
     * @param srcFileInfo
     *            info about source file
     * @param workDir
     *            working directory
     * @return info about destination file
     * @throws InvalidHttpResponseException
     *             when content of the response cannot be retrieved
     * @throws UnexpectedHttpResponseException
     *             when no data file can be retrieved
     */
    private DataFileInfo retrieveDataFileFromOutcome(ExecutionOutcome execOutcome,
            List<ServiceOutcomeInfo> servOutcomeInfos, FileFormat outputFileFormat, DataFileInfo srcFileInfo,
            File workDir)
            throws InvalidHttpResponseException, UnexpectedHttpResponseException {
        if (validateTransfromationOutcome(servOutcomeInfos) == null) {
            throw new UnexpectedHttpResponseException(
                    "No description of the service matches to the supposed transformation outcome",
                    execOutcome.getStatusCode(), execOutcome.getContentType());
        }
        String path = FilenameUtils.getBaseName(srcFileInfo.getPath()) + "." + outputFileFormat.getExtension();
        File destFile = new File(workDir, uuidGenerator.generateRandomFileName());
        try {
            saveContentIntoFile(execOutcome, destFile);
        } catch (IOException e) {
            throw new InvalidHttpResponseException("Error retrieving the content", e);
        }
        DataFileInfo destFileInfo = new DataFileInfo(path, outputFileFormat.getPuid(), srcFileInfo.getSequence(),
                destFile);
        return destFileInfo;
    }


    /**
     * Retrieves from execution outcome the data files infos. The remaining outcomes are omitted, since file
     * transformation is limited to chains of services that returns files always.
     * 
     * @param execOutcome
     *            execution outcome
     * @param servOutcomeInfos
     *            list of validated service outcome infos
     * @param outputFileFormat
     *            supposed output format
     * @param srcFileInfo
     *            info about source file
     * @param workDir
     *            working directory
     * @return info about destination files
     * @throws InvalidHttpResponseException
     *             when content of the response cannot be retrieved
     * @throws UnexpectedHttpResponseException
     *             when no data file can be retrieved
     * @throws ZipArchivingException
     *             when ZIP archiving failed
     */
    private List<DataFileInfo> retrieveDataFilesFromOutcome(ExecutionOutcome execOutcome,
            List<ServiceOutcomeInfo> servOutcomeInfos, FileFormat outputFileFormat, DataFileInfo srcFileInfo,
            File workDir)
            throws InvalidHttpResponseException, UnexpectedHttpResponseException, ZipArchivingException {
        if (validateTransfromationOutcome(servOutcomeInfos) == null) {
            throw new UnexpectedHttpResponseException(
                    "No description of the service matches to the supposed transformation outcome",
                    execOutcome.getStatusCode(), execOutcome.getContentType());
        }
        List<DataFileInfo> destFileInfos = new ArrayList<DataFileInfo>();
        if (execOutcome.getContentType().startsWith("application/zip")) {
            File destDir = new File(workDir, uuidGenerator.generateCacheFolderName());
            destDir.mkdir();
            try {
                ZipUtility.unzip(execOutcome.getContent(), destDir);
            } catch (IOException e) {
                throw new ZipArchivingException(e);
            }
            Iterator<File> it = FileUtils.iterateFiles(destDir, null, true);
            String base = FilenameUtils.getBaseName(srcFileInfo.getPath());
            int inc = 0;
            while (it.hasNext()) {
                Integer sequence = srcFileInfo.getSequence();
                if (sequence != null) {
                    sequence += inc;
                }
                inc++;
                String path = base + inc + "." + outputFileFormat.getExtension();
                DataFileInfo destFileInfo = new DataFileInfo(path, outputFileFormat.getPuid(), sequence, it.next());
                destFileInfos.add(destFileInfo);
            }
        } else {
            String path = FilenameUtils.getBaseName(srcFileInfo.getPath()) + "." + outputFileFormat.getExtension();
            File destFile = new File(workDir, uuidGenerator.generateRandomFileName());
            try {
                saveContentIntoFile(execOutcome, destFile);
            } catch (IOException e) {
                throw new InvalidHttpResponseException("Error retrieving the content", e);
            }
            DataFileInfo destFileInfo = new DataFileInfo(path, outputFileFormat.getPuid(), srcFileInfo.getSequence(),
                    destFile);
            destFileInfos.add(destFileInfo);
        }
        return destFileInfos;
    }


    /**
     * Retrieves from execution outcome the data file info. The remaining outcomes are omitted, since file
     * transformation is limited to chains of services that returns files always.
     * 
     * @param execOutcome
     *            execution outcome
     * @param servOutcomeInfos
     *            list of validated service outcome infos
     * @param outputFileFormat
     *            supposed output format
     * @param srcFileInfos
     *            info about source files
     * @param workDir
     *            working directory
     * @return info about destination file
     * @throws InvalidHttpResponseException
     *             when content of the response cannot be retrieved
     * @throws UnexpectedHttpResponseException
     *             when no data file can be retrieved
     */
    private DataFileInfo retrieveDataFileFromOutcome(ExecutionOutcome execOutcome,
            List<ServiceOutcomeInfo> servOutcomeInfos, FileFormat outputFileFormat, List<DataFileInfo> srcFileInfos,
            File workDir)
            throws InvalidHttpResponseException, UnexpectedHttpResponseException {
        if (validateTransfromationOutcome(servOutcomeInfos) == null) {
            throw new UnexpectedHttpResponseException(
                    "No description of the service matches to the supposed transformation outcome",
                    execOutcome.getStatusCode(), execOutcome.getContentType());
        }
        String path = FilenameUtils.getBaseName(srcFileInfos.get(0).getPath()) + "." + outputFileFormat.getExtension();
        File destFile = new File(workDir, uuidGenerator.generateRandomFileName());
        try {
            saveContentIntoFile(execOutcome, destFile);
        } catch (IOException e) {
            throw new InvalidHttpResponseException("Error retrieving the content", e);
        }
        DataFileInfo destFileInfo = new DataFileInfo(path, outputFileFormat.getPuid(), srcFileInfos.get(0)
                .getSequence(), destFile);
        return destFileInfo;
    }


    /**
     * Retrieves from execution outcome the address of the client location. The remaining outcomes are omitted, since
     * object delivery is limited to services that returns this address.
     * 
     * @param execOutcome
     *            execution outcome
     * @param servOutcomeInfos
     *            list of validated service outcome infos
     * @return client location
     * @throws InvalidHttpResponseException
     *             when content of the response cannot be retrieved
     * @throws UnexpectedHttpResponseException
     *             when no data file can be retrieved
     */
    private String retrieveClientLocationFromOutcome(ExecutionOutcome execOutcome,
            List<ServiceOutcomeInfo> servOutcomeInfos)
            throws InvalidHttpResponseException, UnexpectedHttpResponseException {
        ServiceOutcomeInfo properOutcome = validateDeliveryOutcome(servOutcomeInfos);
        if (properOutcome == null) {
            throw new UnexpectedHttpResponseException(
                    "No description of the service matches to the supposed delivery outcome",
                    execOutcome.getStatusCode(), execOutcome.getContentType());
        }
        String location = null;
        switch (properOutcome.getStyle()) {
            case BODY:
                try {
                    location = IOUtils.toString(execOutcome.getContent());
                } catch (IOException e) {
                    throw new InvalidHttpResponseException("Error retrieving the content", e);
                }
                break;
            case HEADER:
                location = execOutcome.getHeader(properOutcome.getName());
                break;
            default:
                throw new WrdzRuntimeException("Unknow outcome style " + properOutcome.getStyle());
        }
        if (location == null || location.isEmpty()) {
            throw new UnexpectedHttpResponseException("No client location returned by the service",
                    execOutcome.getStatusCode(), execOutcome.getContentType());
        }
        return location;
    }


    /**
     * Saves the content (input stream) into the specified file.
     * 
     * @param execOutcome
     *            execution outcome with the input stream to save
     * @param destFile
     *            destination file
     * @throws IOException
     *             when the content cannot be retrieved or saved
     */
    protected void saveContentIntoFile(ExecutionOutcome execOutcome, File destFile)
            throws IOException {
        OutputStream destFileStream = new FileOutputStream(destFile);
        try {
            IOUtils.copy(execOutcome.getContent(), destFileStream);
        } catch (IOException e) {
            destFile.delete();
            throw e;
        } finally {
            IOUtils.closeQuietly(destFileStream);
        }
    }


    /**
     * Validates the description of service outcomes in case of transformation and returns the proper one.
     * 
     * @param servOutcomeInfos
     *            list of validated description of service outcomes
     * @return the proper service outcome or null
     */
    protected ServiceOutcomeInfo validateTransfromationOutcome(List<ServiceOutcomeInfo> servOutcomeInfos) {
        for (ServiceOutcomeInfo servOutcomeInfo : servOutcomeInfos) {
            if (servOutcomeInfo.getStyle().equals(OutcomeStyle.BODY)
                    && (servOutcomeInfo.getSemanticType().equals(InvocationConsts.FILE_TYPE) || servOutcomeInfo
                            .getSemanticType().equals(InvocationConsts.FILE_BUNDLE_TYPE))) {
                return servOutcomeInfo;
            }
        }
        return null;
    }


    /**
     * Validates the description of service outcomes in case of delivery and returns the proper one.
     * 
     * @param servOutcomeInfos
     *            list of validated description of service outcomes
     * @return the proper service outcome or null
     */
    protected ServiceOutcomeInfo validateDeliveryOutcome(List<ServiceOutcomeInfo> servOutcomeInfos) {
        for (ServiceOutcomeInfo servOutcomeInfo : servOutcomeInfos) {
            if (servOutcomeInfo.getSemanticType().equals(InvocationConsts.CLIENT_LOCATION_TYPE)) {
                return servOutcomeInfo;
            }
        }
        return null;
    }


    /**
     * Gets file format for a given data file info.
     * 
     * @param dataFileInfo
     *            data file
     * @return file format
     * 
     */
    private FileFormat getFileFormatOfDataFile(DataFileInfo dataFileInfo) {
        FileFormat fileFormat = null;
        try {
            fileFormat = fileFormatDictionaryBean.findByPuid(dataFileInfo.getPuid());
        } catch (UnrecognizedPuidException e) {
            logger.warn("PUID " + dataFileInfo.getPuid() + " is not recognized.", e);
        } catch (UdfrServiceException e) {
            logger.warn("UDFR service is not working now.", e);
        }
        if (fileFormat == null) {
            fileFormat = new FileFormat(dataFileInfo.getPuid());
        } else {
            fileFormat = new FileFormat(fileFormat.getPuid(), fileFormat.getUdfrIri(), fileFormat.getExtension(),
                    fileFormat.getMimetype());
        }
        if (fileFormat.getExtension() == null) {
            fileFormat.setExtension(FilenameUtils.getExtension(dataFileInfo.getPath()));
        }
        return fileFormat;
    }

}
