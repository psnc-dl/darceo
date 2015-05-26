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
package pl.psnc.synat.wrdz.zmd.object.content;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.metadata.Metadata;
import pl.psnc.synat.wrdz.common.metadata.adm.AdmMetadata;
import pl.psnc.synat.wrdz.common.metadata.adm.AdmMetadataProcessingException;
import pl.psnc.synat.wrdz.common.metadata.tech.ExtractedMetadata;
import pl.psnc.synat.wrdz.common.metadata.tech.TechMetadata;
import pl.psnc.synat.wrdz.common.metadata.tech.TechMetadataExtractionException;
import pl.psnc.synat.wrdz.common.metadata.tech.TechMetadataExtractor;
import pl.psnc.synat.wrdz.common.metadata.tech.TechMetadataExtractorFactory;
import pl.psnc.synat.wrdz.common.performance.Profiler;
import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileVersionDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileVersionFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.validation.DataFileValidationDao;
import pl.psnc.synat.wrdz.zmd.download.DownloadTask;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.hash.DataFileHash;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.validation.DataFileValidation;
import pl.psnc.synat.wrdz.zmd.entity.types.ValidationStatus;
import pl.psnc.synat.wrdz.zmd.object.ObjectModificationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectStructure;
import pl.psnc.synat.wrdz.zmd.object.hash.HashGenerator;
import pl.psnc.synat.wrdz.zmd.object.helpers.FileFormatDictionaryBean;
import pl.psnc.synat.wrdz.zmd.object.helpers.FileValidationDictionaryBean;
import pl.psnc.synat.wrdz.zmd.object.helpers.ObjectUtils;
import pl.psnc.synat.wrdz.zmd.object.metadata.AdministrativeMetadataScheme;
import pl.psnc.synat.wrdz.zmd.object.metadata.FileMetadataCreator;
import pl.psnc.synat.wrdz.zmd.output.OutputFile;
import pl.psnc.synat.wrdz.zmd.output.OutputFileUpdate;
import pl.psnc.synat.wrdz.zmd.output.OutputTask;
import pl.psnc.synat.wrdz.zmd.output.object.DataFilesBundle;

/**
 * Build's content version's contents, i.e. list of data files with their metadata and all related information.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DataFileBuilder {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DataFileBuilder.class);

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -4668616901571711930L;

    /**
     * Default identifier scheme.
     */
    private AdministrativeMetadataScheme administrativeMetadataScheme;

    /**
     * Data file DAO.
     */
    @EJB
    private DataFileDao dataFileDao;

    /**
     * Data file version DAO.
     */
    @EJB
    private DataFileVersionDao dataFileVersionDaoBean;

    /**
     * Data file validation DAO.
     */
    @EJB
    private DataFileValidationDao dataFileValidationDaoBean;

    /**
     * Provides useful methods for file's object's metadata.
     */
    @EJB
    private FileMetadataCreator fileMetadataHelper;

    /**
     * Provides an access to file formats dictionary.
     */
    @EJB
    private FileFormatDictionaryBean fileFormatDictionaryBean;

    /**
     * Provides an access to validation objects dictionary.
     */
    @EJB
    private FileValidationDictionaryBean fileValidationDictionaryBean;

    /**
     * File hash generator.
     */
    @Inject
    private HashGenerator hashGenerator;

    /**
     * ZMD module configuration.
     */
    @Inject
    private ZmdConfiguration zmdConfig;


    /**
     * Initializes instance specific values dependent on injected beans.
     */
    @PostConstruct
    public void initialize() {
        administrativeMetadataScheme = AdministrativeMetadataScheme.valueOf(zmdConfig
                .getDefaultAdministrativeMetadataType());
    }


    /**
     * Constructs new content version contents for a given object and content version.
     * 
     * @param object
     *            digital object, for which to create a new content version's contents.
     * @param contentVersion
     *            content version, for which to create new contents.
     * @param dataFilesBundle
     *            bundle of data files concerning a new content version
     * @param cachePath
     *            cache path
     * @param dontInherit
     *            indicates whether or not the contents from previous versions should be inherited (if not modified or
     *            deleted), or should the new version contain only the added files.
     * 
     * @return list of version's contents.
     * @throws ObjectModificationException
     *             should any error while modifying object arise.
     */
    public List<DataFileVersion> constructVersionContents(DigitalObject object, ContentVersion contentVersion,
            DataFilesBundle dataFilesBundle, String cachePath, boolean dontInherit)
            throws ObjectModificationException {
        List<OutputFile> addedDataFiles = prepareAddedDataFiles(dataFilesBundle.getAddedData());
        List<OutputFileUpdate> modifiedDataFiles = prepareModifiedDataFiles(dataFilesBundle.getModifiedData());
        Set<String> deletedDataFiles = prepareDeletedMetadata(dataFilesBundle.getDeletedData());
        List<DataFileVersion> result = new ArrayList<DataFileVersion>();
        addFilesToList(object.getId(), result, contentVersion, addedDataFiles, cachePath);
        if (!dontInherit) {
            addModifiedFiles(object.getId(), result, contentVersion, modifiedDataFiles, cachePath);
            addInheritedFiles(object.getId(), result, contentVersion, modifiedDataFiles, deletedDataFiles);
        }
        return result;
    }


    /**
     * Prepare version's added data files.
     * 
     * @param addedData
     *            list of version's added data files
     * @return Non-null set of version's added data files
     */
    private List<OutputFile> prepareAddedDataFiles(List<OutputFile> addedData) {
        List<OutputFile> result = new ArrayList<OutputFile>();
        if (addedData != null && !addedData.isEmpty()) {
            result.addAll(addedData);
        }
        return result;
    }


    /**
     * Prepare version's modified data files.
     * 
     * @param modifiedData
     *            list of version's modified data files
     * @return Non-null set of version's modified data files.
     */
    private List<OutputFileUpdate> prepareModifiedDataFiles(List<OutputFileUpdate> modifiedData) {
        List<OutputFileUpdate> result = new ArrayList<OutputFileUpdate>();
        if (modifiedData != null && !modifiedData.isEmpty()) {
            result.addAll(modifiedData);
        }
        return result;
    }


    /**
     * Prepare version's deleted data files.
     * 
     * @param deletedData
     *            set of version's deleted data files
     * @return Non-null set of version's deleted data files.
     */
    private Set<String> prepareDeletedMetadata(Set<String> deletedData) {
        Set<String> result = new HashSet<String>();
        if (deletedData != null && !deletedData.isEmpty()) {
            result.addAll(deletedData);
        }
        return result;
    }


    /**
     * Adds inherited files from the previous version to currently constructed version contents.
     * 
     * @param objectId
     *            object's id in the database.
     * @param list
     *            list of contents to which add files.
     * @param contentVersion
     *            constructed version.
     * @param modifiedDataFiles
     *            list of version's modified data files
     * @param deletedDataFiles
     *            set of version's deleted data files
     */
    private void addInheritedFiles(Long objectId, List<DataFileVersion> list, ContentVersion contentVersion,
            List<OutputFileUpdate> modifiedDataFiles, Set<String> deletedDataFiles) {
        int versionNo = contentVersion.getVersion();
        DataFileVersionFilterFactory queryFilterFactory = dataFileVersionDaoBean.createQueryModifier()
                .getQueryFilterFactory();
        QueryFilter<DataFileVersion> filter = queryFilterFactory.byContentVersionNo(objectId, versionNo - 1, false);
        if (!deletedDataFiles.isEmpty()) {
            filter = queryFilterFactory.and(filter,
                queryFilterFactory.not(queryFilterFactory.byFilenames(deletedDataFiles)));
        }
        if (!modifiedDataFiles.isEmpty()) {
            Set<String> toRemove = new HashSet<String>();
            for (OutputFileUpdate outputFile : modifiedDataFiles) {
                toRemove.add(outputFile.getFile().getFilename());
            }
            filter = queryFilterFactory.and(filter, queryFilterFactory.not(queryFilterFactory.byFilenames(toRemove)));
        }

        List<DataFileVersion> files = dataFileVersionDaoBean.findBy(filter, true);
        for (DataFileVersion file : files) {

            DataFileVersion newVersion = new DataFileVersion();
            newVersion.setContentVersion(contentVersion);
            newVersion.setDataFile(file.getDataFile());
            newVersion.setSequence(file.getSequence());
            dataFileVersionDaoBean.persist(newVersion);

            file.getDataFile().getIncludedIn().add(newVersion);

            for (FileProvidedMetadata providedMetadataFile : file.getProvidedMetadata()) {
                providedMetadataFile.getProvidedFor().add(newVersion);
                newVersion.getProvidedMetadata().add(providedMetadataFile);
            }

            list.add(newVersion);
        }
    }


    /**
     * Applies modification operations on data files existing in the previous version and adds them to the contents of
     * the current one.
     * 
     * @param objectId
     *            object's id in the database.
     * @param list
     *            list of contents to which add files.
     * @param contentVersion
     *            constructed version.
     * @param modifiedDataFiles
     *            list of version's modified data files
     * @param cachePath
     *            cache path
     * @throws ObjectModificationException
     *             should any error while modifying object arise.
     */
    private void addModifiedFiles(Long objectId, List<DataFileVersion> list, ContentVersion contentVersion,
            List<OutputFileUpdate> modifiedDataFiles, String cachePath)
            throws ObjectModificationException {
        int versionNo = contentVersion.getVersion();
        DataFileVersionFilterFactory queryFilterFactory = dataFileVersionDaoBean.createQueryModifier()
                .getQueryFilterFactory();
        QueryFilter<DataFileVersion> baseFilter = queryFilterFactory.byContentVersionNo(objectId, versionNo - 1, false);
        for (OutputFileUpdate outputFile : modifiedDataFiles) {
            QueryFilter<DataFileVersion> filter = queryFilterFactory.and(baseFilter,
                queryFilterFactory.byFilename(outputFile.getFile().getFilename()));
            DataFileVersion previousVersion = dataFileVersionDaoBean.findFirstResultBy(filter);
            if (previousVersion == null) {
                throw new ObjectModificationException("File specified for modification does not exist: "
                        + outputFile.getFile().getInnerPath());
            }
            String prefix = ObjectUtils.createObjectAndVersionPath(objectId, versionNo);
            try {
                if (outputFile.getFile().getUri() == null) {
                    list.add(updateModifiedFile(prefix, previousVersion, outputFile, contentVersion, cachePath));
                } else {
                    list.add(createModifiedFile(prefix, previousVersion, outputFile, contentVersion, cachePath));
                }
            } catch (TechMetadataExtractionException e) {
                throw new ObjectModificationException("Extraction of metadata failed for the file "
                        + outputFile.getFile().getInnerPath(), e);
            } catch (AdmMetadataProcessingException e) {
                throw new ObjectModificationException("Construction of administrative metadata failed for the file "
                        + outputFile.getFile().getInnerPath(), e);
            }
        }
    }


    /**
     * Updated an existing modified file entry with new file information.
     * 
     * @param prefix
     *            file prefix containing path to the version's root folder.
     * @param oldVersion
     *            previous version of the file.
     * @param modifications
     *            file modification information and resources (cached file location).
     * @param contentVersion
     *            constructed version.
     * @param cachePath
     *            cache path
     * @return returns the new version of the file
     * @throws TechMetadataExtractionException
     *             when extraction of technical metadata failed
     * @throws AdmMetadataProcessingException
     *             when construction of administrative metadata failed
     * @throws ObjectModificationException
     *             thrown when object modification fails.
     */
    private DataFileVersion updateModifiedFile(String prefix, DataFileVersion oldVersion,
            OutputFileUpdate modifications, ContentVersion contentVersion, String cachePath)
            throws TechMetadataExtractionException, AdmMetadataProcessingException, ObjectModificationException {

        DataFile dataFile = oldVersion.getDataFile();

        DataFileVersion newVersion = new DataFileVersion();
        newVersion.setContentVersion(contentVersion);
        newVersion.setDataFile(dataFile);
        newVersion.setSequence(modifications.getSequence());
        dataFileVersionDaoBean.persist(newVersion);

        dataFile.getIncludedIn().add(newVersion);

        createProvidedMetadataFiles(prefix, newVersion, modifications.getMetadataFiles());
        createProvidedMetadataFiles(prefix, newVersion, modifications.getModifiedMetadataFiles());
        addInheritedMetadataFiles(newVersion, oldVersion, modifications.getDeletedMetadataFiles());

        return newVersion;
    }


    /**
     * Creates a modified file entry out of old file information and new file information.
     * 
     * @param prefix
     *            file prefix containing path to the version's root folder.
     * @param oldVersion
     *            previous file version
     * @param modifications
     *            file modification information and resources (cached file location).
     * @param contentVersion
     *            constructed version.
     * @param cachePath
     *            cache path
     * @return returns the new version of the file
     * @throws TechMetadataExtractionException
     *             when extraction of technical metadata failed
     * @throws AdmMetadataProcessingException
     *             when construction of administrative metadata failed
     * @throws ObjectModificationException
     *             thrown when object modification fails.
     */
    private DataFileVersion createModifiedFile(String prefix, DataFileVersion oldVersion,
            OutputFileUpdate modifications, ContentVersion contentVersion, String cachePath)
            throws TechMetadataExtractionException, AdmMetadataProcessingException, ObjectModificationException {

        DataFile dataFile = createFile(modifications, cachePath, contentVersion.getCreatedOn());
        dataFile.setRepositoryFilepath(prefix + dataFile.getObjectFilepath());
        dataFile.setFilename(oldVersion.getDataFile().getFilename());
        dataFile.setObjectFilepath(oldVersion.getDataFile().getObjectFilepath());
        dataFileDao.persist(dataFile);

        DataFileVersion newVersion = new DataFileVersion();
        newVersion.setContentVersion(contentVersion);
        newVersion.setDataFile(dataFile);
        newVersion.setSequence(modifications.getSequence());
        dataFileVersionDaoBean.persist(newVersion);

        dataFile.getIncludedIn().add(newVersion);

        createExtractedMetadataFiles(prefix, dataFile, modifications);

        createProvidedMetadataFiles(prefix, newVersion, modifications.getMetadataFiles());
        createProvidedMetadataFiles(prefix, newVersion, modifications.getModifiedMetadataFiles());
        addInheritedMetadataFiles(newVersion, oldVersion, modifications.getDeletedMetadataFiles());

        return newVersion;
    }


    /**
     * Creates list of provided metadata file objects and remove it if it exists at the list of previous version
     * metadata files.
     * 
     * @param pathPrefix
     *            file path prefix.
     * @param file
     *            data file version for which metadata is provided.
     * @param downloadTasks
     *            list of metadata file resources provided by user.
     */
    private void createProvidedMetadataFiles(String pathPrefix, DataFileVersion file, List<DownloadTask> downloadTasks) {

        if (downloadTasks != null && !downloadTasks.isEmpty()) {
            for (DownloadTask downloadTask : downloadTasks) {
                FileProvidedMetadata tmp = fileMetadataHelper.createFileProvidedMetadata(downloadTask);
                tmp.setRepositoryFilepath(pathPrefix + tmp.getObjectFilepath());
                tmp.getProvidedFor().add(file);
                file.getProvidedMetadata().add(tmp);
            }
        }
    }


    /**
     * Creates file's extracted metadata.
     * 
     * @param pathPrefix
     *            version's folder prefix to add to new file paths.
     * @param dataFile
     *            file that is the result of modification.
     * @param modifications
     *            set of modifications to be applied on file.
     */
    private void createExtractedMetadataFiles(String pathPrefix, DataFile dataFile, OutputFile modifications) {
        if (modifications.getTechMetadataFiles() != null) {
            for (OutputTask techMetadataFile : modifications.getTechMetadataFiles()) {
                FileExtractedMetadata tmp = fileMetadataHelper.createFileExtractedMetadata(techMetadataFile);
                tmp.setRepositoryFilepath(pathPrefix + tmp.getObjectFilepath());
                tmp.setExtractedFrom(dataFile);
                dataFile.getExtractedMetadata().add(tmp);
            }
        }
        if (modifications.getAdmMetadataFile() != null) {
            FileExtractedMetadata tmp = fileMetadataHelper.createFileExtractedMetadata(modifications
                    .getAdmMetadataFile());
            tmp.setRepositoryFilepath(pathPrefix + tmp.getObjectFilepath());
            tmp.setExtractedFrom(dataFile);
            dataFile.getExtractedMetadata().add(tmp);
        }
    }


    /**
     * Adds provided metadata files from the old version that should be inherited in the new version.
     * 
     * A provided metadata file is inherited if the new version does not contain a provided metadata file with the same
     * filename and that filename was not excluded using the deleted metadata files parameter.
     * 
     * @param newVersion
     *            version that the inherited files will be added to
     * @param oldVersion
     *            version from which to inherit the files
     * @param deletedMetadataFiles
     *            which filenames should be excluded from the process
     */
    private void addInheritedMetadataFiles(DataFileVersion newVersion, DataFileVersion oldVersion,
            Set<String> deletedMetadataFiles) {

        Set<String> excluded = new HashSet<String>();
        if (deletedMetadataFiles != null) {
            excluded.addAll(deletedMetadataFiles);
        }
        for (FileProvidedMetadata metadata : newVersion.getProvidedMetadata()) {
            excluded.add(metadata.getFilename());
        }

        for (FileProvidedMetadata metadata : oldVersion.getProvidedMetadata()) {
            if (!excluded.contains(metadata.getFilename())) {
                newVersion.getProvidedMetadata().add(metadata);
                metadata.getProvidedFor().add(newVersion);
            }
        }
    }


    /**
     * Creates new {@link DataFile} entity representing content file of the object out of passed output file task. It
     * extracts technical metadata from the file as well.
     * 
     * @param source
     *            output file representing file information to be saved in database.
     * @param cachePath
     *            cache path
     * @param versionDate
     *            date date of the version
     * @return unpersisted entity object containing info about the object's file.
     * @throws TechMetadataExtractionException
     *             when extraction of technical metadata failed
     * @throws AdmMetadataProcessingException
     *             when construction of administrative metadata failed
     */
    private DataFile createFile(OutputFile source, String cachePath, Date versionDate)
            throws TechMetadataExtractionException, AdmMetadataProcessingException {
        DataFile file = new DataFile();
        file.setFilename(source.getFile().getFilename());
        file.setObjectFilepath(source.getFile().getInnerPath());
        file.setCachePath(source.getFile().getCachePath());
        List<DataFileHash> hashes = new ArrayList<DataFileHash>();
        hashes.add(hashGenerator.getDataFileHash(source.getFile(), file));
        file.setHashes(hashes);
        file.setSize((new File(source.getFile().getCachePath()).length()));
        ExtractedMetadata extractedMetadata = null;
        if (zmdConfig.extractTechnicalMetadata()) {
            TechMetadataExtractor extractor = TechMetadataExtractorFactory.getInstance().getTechMetadataExtractor();
            logger.debug("extracting metadata from " + source.getFile().getCachePath());
            Profiler.start("technical metadata extraction");
            try {
                extractedMetadata = extractor.extract(source.getFile().getCachePath());
            } finally {
                Profiler.stop("technical metadata extraction");
            }
            file.setValidation(createDataFileValidation(extractedMetadata));
            List<OutputTask> techMetadataFiles = new ArrayList<OutputTask>();
            if (extractedMetadata.getTechMetadata() != null) {
                for (TechMetadata techMetadata : extractedMetadata.getTechMetadata()) {
                    techMetadataFiles.add(saveMetadataToFile(source, techMetadata, cachePath));
                }
            }
            source.setTechMetadataFiles(techMetadataFiles);
        }
        file.setFormat(fileFormatDictionaryBean.getFileFormat(extractedMetadata));
        AdmMetadata administrativeMetadata = administrativeMetadataScheme.getStrategy()
                .constructAdministrativeMetadata(file, extractedMetadata, versionDate,
                    zmdConfig.getMaxValidationMessages());
        source.setAdmMetadataFile(saveMetadataToFile(source, administrativeMetadata, cachePath));
        return file;
    }


    /**
     * Save into cache a file with the extracted technical metadata.
     * 
     * @param source
     *            output file from which metadata were extracted
     * @param metadata
     *            extracted technical or administrative metadata
     * @param cachePath
     *            cache path
     * @return handle to a saved file
     * @throws TechMetadataExtractionException
     *             when saving of extracted technical/administrative metadata failed
     */
    private OutputTask saveMetadataToFile(OutputFile source, Metadata metadata, String cachePath)
            throws TechMetadataExtractionException {
        if (metadata == null) {
            return null;
        }
        String metadataFilename = metadata.getType().getName() + ".xml";
        OutputTask extractedFile = new OutputTask(ObjectStructure.getPathForExtractedMetadata(source.getFile()
                .getFilename(), metadataFilename), metadataFilename);
        cachePath = cachePath + source.getFile().getInnerPath() + new Date().getTime() + metadataFilename;
        extractedFile.setCachePath(cachePath);
        try {
            FileUtils.writeStringToFile(new File(cachePath), metadata.getXml(), "UTF-8");
        } catch (IOException e) {
            throw new TechMetadataExtractionException("Saving the extracted technical metadata into file " + cachePath
                    + " failed", e);
        }
        return extractedFile;
    }


    /**
     * Creates data file validation entity based upon extracted metadata.
     * 
     * @param extractedMetadata
     *            extracted metadata
     * @return data file validation
     */
    private DataFileValidation createDataFileValidation(ExtractedMetadata extractedMetadata) {
        DataFileValidation dataFileValidation = new DataFileValidation(ValidationStatus.valueOf(extractedMetadata
                .getFileStatus().getStatus().name()));
        dataFileValidationDaoBean.persist(dataFileValidation);
        dataFileValidation.setWarnings(fileValidationDictionaryBean.getFileValidationMessages(extractedMetadata
                .getFileStatus().getWarnings()));
        return dataFileValidationDaoBean.merge(dataFileValidation);
    }


    /**
     * Adds downloaded files to content's list.
     * 
     * @param objectId
     *            object's id in the database.
     * @param list
     *            list of contents to which add files.
     * @param contentVersion
     *            constructed version.
     * @param files
     *            list of downloaded files to be added to content's.
     * @param cachePath
     *            cache path
     * @throws ObjectModificationException
     *             should any error while modifying object arise.
     */
    private void addFilesToList(Long objectId, List<DataFileVersion> list, ContentVersion contentVersion,
            List<OutputFile> files, String cachePath)
            throws ObjectModificationException {
        if (files != null) {

            String prefix = ObjectUtils.createObjectAndVersionPath(objectId, contentVersion.getVersion());

            for (OutputFile outputFile : files) {
                DataFile file = null;
                try {
                    file = createFile(outputFile, cachePath, contentVersion.getCreatedOn());
                } catch (TechMetadataExtractionException e) {
                    throw new ObjectModificationException("Extraction of metadata failed for the file "
                            + outputFile.getFile().getInnerPath(), e);
                } catch (AdmMetadataProcessingException e) {
                    throw new ObjectModificationException(
                            "Construction of administrative metadata failed for the file "
                                    + outputFile.getFile().getInnerPath(), e);
                }
                file.setRepositoryFilepath(prefix + file.getObjectFilepath());
                dataFileDao.persist(file);

                DataFileVersion fileVersion = new DataFileVersion();
                fileVersion.setContentVersion(contentVersion);
                fileVersion.setDataFile(file);
                fileVersion.setSequence(outputFile.getSequence());
                dataFileVersionDaoBean.persist(fileVersion);

                file.getIncludedIn().add(fileVersion);

                createProvidedMetadataFiles(prefix, fileVersion, outputFile.getMetadataFiles());
                createExtractedMetadataFiles(prefix, file, outputFile);

                list.add(fileVersion);
            }
        }
    }
}
