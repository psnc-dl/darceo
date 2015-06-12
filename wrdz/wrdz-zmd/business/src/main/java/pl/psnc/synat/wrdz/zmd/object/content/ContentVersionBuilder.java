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
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import pl.psnc.synat.wrdz.common.dao.QueryModifier;
import pl.psnc.synat.wrdz.common.metadata.Metadata;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadata;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataProcessingException;
import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.dao.object.content.ContentVersionDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.ContentVersionFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.content.ContentVersionSorterBuilder;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.ObjectProvidedMetadataDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.ObjectProvidedMetadataFilterFactory;
import pl.psnc.synat.wrdz.zmd.download.DownloadTask;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.input.InputFile;
import pl.psnc.synat.wrdz.zmd.object.ObjectModificationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectStructure;
import pl.psnc.synat.wrdz.zmd.object.helpers.ObjectUtils;
import pl.psnc.synat.wrdz.zmd.object.metadata.ObjectMetadataCreator;
import pl.psnc.synat.wrdz.zmd.object.metadata.construction.LinkMetsMetadataConstructionStrategy;
import pl.psnc.synat.wrdz.zmd.output.OutputTask;
import pl.psnc.synat.wrdz.zmd.output.object.DataFilesBundle;
import pl.psnc.synat.wrdz.zmd.output.object.MetadataFilesBundle;

/**
 * Builds content version objects with their children.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ContentVersionBuilder {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -8421074346035467283L;

    /**
     * Content version DAO.
     */
    @EJB
    private ContentVersionDao contentVersionDao;

    /**
     * Object provided metadata DAO.
     */
    @EJB
    private ObjectProvidedMetadataDao objectProvidedMetadataDao;

    /**
     * Builder for version files.
     */
    @EJB
    private DataFileBuilder dataFileBuilder;

    /**
     * ZMD module configuration.
     */
    @Inject
    private ZmdConfiguration zmdConfiguration;

    /**
     * Provides helpful methods for database metadata structures handling and building.
     */
    @EJB
    private ObjectMetadataCreator objectMetadataHelper;


    /**
     * Constructs new content version for a given object from the given arguments.
     * 
     * @param object
     *            digital object, for which to create a new content version.
     * @param dataFilesBundle
     *            bundle of data files concerning a new content version
     * @param metadataFilesBundle
     *            bundle of metadata files concerning a new content version
     * @param mainFile
     *            main file of a new content version
     * @param cachePath
     *            path to the root of cache for created object.
     * @param dontInherit
     *            indicates whether or not the contents from previous versions should be inherited (if not modified or
     *            deleted), or should the new version contain only the added files.
     * @return constructed content version.
     * @throws ObjectModificationException
     *             should any error while modifying object arise.
     */
    public ContentVersion constructContentVersion(DigitalObject object, DataFilesBundle dataFilesBundle,
            MetadataFilesBundle metadataFilesBundle, String mainFile, String cachePath, boolean dontInherit)
            throws ObjectModificationException {
        ContentVersion result = constructContentVersion(object, dataFilesBundle, metadataFilesBundle, mainFile,
            cachePath, dontInherit, null, null);
        return result;
    }


    /**
     * Constructs new content version for a given object from the given arguments.
     * 
     * @param object
     *            digital object, for which to create a new content version.
     * @param dataFilesBundle
     *            bundle of data files concerning a new content version
     * @param metadataFilesBundle
     *            bundle of metadata files concerning a new content version
     * @param mainFile
     *            main file of a new content version
     * @param cachePath
     *            path to the root of cache for created object.
     * @param dontInherit
     *            indicates whether or not the contents from previous versions should be inherited (if not modified or
     *            deleted), or should the new version contain only the added files.
     * @param metsProvidedUri
     *            URI to tmpMetsFile.
     * @param inputFilesSet
     *            set of input files.
     * @return constructed content version.
     * @throws ObjectModificationException
     *             should any error while modifying object arise.
     */
    public ContentVersion constructContentVersion(DigitalObject object, DataFilesBundle dataFilesBundle,
            MetadataFilesBundle metadataFilesBundle, String mainFile, String cachePath, boolean dontInherit,
            URI metsProvidedUri, Set<InputFile> inputFilesSet)
            throws ObjectModificationException {
        ContentVersion previous = getLatestVersion(object);
        ContentVersion result = null;
        if (previous == null) {
            result = createNewVersion(object, 1);
        } else {
            result = createNewVersion(object, previous.getVersion() + 1);
        }
        contentVersionDao.persist(result);
        result.setFiles(dataFileBuilder.constructVersionContents(object, result, dataFilesBundle, cachePath,
            dontInherit));
        result.setMainFile(getMainFile(mainFile, result.getFiles()));
        List<ObjectProvidedMetadata> addedMetadata = prepareAddedMetadata(metadataFilesBundle.getAddedMetadata());
        List<ObjectProvidedMetadata> modifiedMetadata = prepareModifiedMetadata(metadataFilesBundle
                .getModifiedMetadata());
        Set<String> deletedMetadata = prepareDeletedMetadata(metadataFilesBundle.getDeletedMetadata());
        if (dontInherit) {
            result.setProvidedMetadata(createProvidedMetadata(result, null, addedMetadata, modifiedMetadata,
                deletedMetadata));
        } else {
            ObjectProvidedMetadataFilterFactory queryFilterFactory = objectProvidedMetadataDao.createQueryModifier()
                    .getQueryFilterFactory();
            List<ObjectProvidedMetadata> previousObjectProvidedMetadata = objectProvidedMetadataDao.findBy(
                queryFilterFactory.byIncludedInVersion(previous.getId()), false);
            result.getProvidedMetadata().addAll(
                createProvidedMetadata(result, previousObjectProvidedMetadata, addedMetadata, modifiedMetadata,
                    deletedMetadata));
        }
        if (zmdConfiguration.constructMetsMetadata()) {
            result.setExtractedMetadata(createVersionMetadata(result, cachePath, metsProvidedUri, inputFilesSet));
            String pathPrefix = ObjectUtils.createObjectAndVersionPath(result.getObject().getId(), result.getVersion());
            result.getExtractedMetadata().setRepositoryFilepath(
                pathPrefix + result.getExtractedMetadata().getObjectFilepath());
        }
        return result;
    }


    /**
     * Creates new, initialized content version for the given object.
     * 
     * @param object
     *            object being the parent of the version.
     * @param versionNumber
     *            number of the version to be created.
     * @return created content version.
     */
    private ContentVersion createNewVersion(DigitalObject object, int versionNumber) {
        ContentVersion result = new ContentVersion();
        result.setCreatedOn(new Date());
        result.setVersion(versionNumber);
        result.setObject(object);
        return result;
    }


    /**
     * Prepare version's added provided metadata.
     * 
     * @param addedMetadata
     *            list of version's added provided metadata.
     * @return Non-null set of version's added provided metadata.
     */
    private List<ObjectProvidedMetadata> prepareAddedMetadata(List<DownloadTask> addedMetadata) {
        List<ObjectProvidedMetadata> result = new ArrayList<ObjectProvidedMetadata>();
        if (addedMetadata != null && !addedMetadata.isEmpty()) {
            for (DownloadTask downloadTask : addedMetadata) {
                result.add(objectMetadataHelper.createProvidedMetadata(downloadTask));
            }
        }
        return result;
    }


    /**
     * Prepare version's modified provided metadata.
     * 
     * @param modifiedMetadata
     *            list of version's modified provided metadata.
     * @return Non-null set of version's modified provided metadata.
     */
    private List<ObjectProvidedMetadata> prepareModifiedMetadata(List<DownloadTask> modifiedMetadata) {
        List<ObjectProvidedMetadata> result = new ArrayList<ObjectProvidedMetadata>();
        if (modifiedMetadata != null && !modifiedMetadata.isEmpty()) {
            for (DownloadTask downloadTask : modifiedMetadata) {
                result.add(objectMetadataHelper.createProvidedMetadata(downloadTask));
            }
        }
        return result;
    }


    /**
     * Prepare version's deleted provided metadata.
     * 
     * @param deletedMetadata
     *            set of version's deleted provided metadata.
     * @return Non-null set of version's deleted provided metadata.
     */
    private Set<String> prepareDeletedMetadata(Set<String> deletedMetadata) {
        Set<String> result = new HashSet<String>();
        if (deletedMetadata != null && !deletedMetadata.isEmpty()) {
            result.addAll(deletedMetadata);
        }
        return result;
    }


    /**
     * Creates version provided metadata structures.
     * 
     * @param version
     *            version, for which to create provided metadata structures.
     * @param inherited
     *            list of inherited provided metadata from previous version.
     * @param addedMetadata
     *            list of version's added provided metadata
     * @param modifiedMetadata
     *            list of version's modified provided metadata
     * @param deletedMetadata
     *            set of version's deleted provided metadata
     * @return list of version's provided metadata.
     * @throws ObjectModificationException
     *             should any error while modifying object arise.
     */
    private List<ObjectProvidedMetadata> createProvidedMetadata(ContentVersion version,
            List<ObjectProvidedMetadata> inherited, List<ObjectProvidedMetadata> addedMetadata,
            List<ObjectProvidedMetadata> modifiedMetadata, Set<String> deletedMetadata)
            throws ObjectModificationException {
        List<ObjectProvidedMetadata> results = new ArrayList<ObjectProvidedMetadata>();
        results.addAll(addedMetadata);
        results.addAll(modifiedMetadata);
        prependObjectIdAndVersion(version, version.getObject().getId(), version.getVersion(), results);
        if (inherited != null && !inherited.isEmpty()) {
            results.addAll(removeModifiedMetadata(removeDeletedMetadata(inherited, deletedMetadata), modifiedMetadata));
        } else if (!modifiedMetadata.isEmpty() && !deletedMetadata.isEmpty()) {
            throw new ObjectModificationException(
                    "Inappropriate modification query, modified/deleted files do not exist.");
        }
        for (ObjectProvidedMetadata objectProvidedMetadata : results) {
            objectProvidedMetadata.getProvidedFor().add(version);
        }
        return results;
    }


    /**
     * Creates metadata for version.
     * 
     * @param version
     *            version, for which to create extracted metadata structures.
     * @param cachePath
     *            path to the root of cache for created object.
     * @param metsProvidedUri
     *            URI to tmpMetsFile.
     * @param inputFilesSet
     *            set of input files.
     * @return extracted metadata.
     * @throws ObjectModificationException
     *             should any error while modifying object arise.
     */
    private ObjectExtractedMetadata createVersionMetadata(ContentVersion version, String cachePath,
            URI metsProvidedUri, Set<InputFile> inputFilesSet)
            throws ObjectModificationException {
        MetsMetadata metadata = null;
        try {
            metadata = new LinkMetsMetadataConstructionStrategy().constructMetsMetadata(version, metsProvidedUri,
                inputFilesSet);
        } catch (MetsMetadataProcessingException e) {
            throw new ObjectModificationException("Construction of version metadata failed!", e);
        }
        OutputTask savedMetadata = saveMetadataToFile(metadata, cachePath);
        ObjectExtractedMetadata extractedMetadata = objectMetadataHelper.createExtractedMetadata(savedMetadata);
        return extractedMetadata;
    }


    /**
     * Save into cache a file with metadata of an object.
     * 
     * @param metadata
     *            metadata
     * @param cachePath
     *            cache path
     * @return handle to a saved file
     * @throws ObjectModificationException
     *             when saving of metadata failed
     */
    private OutputTask saveMetadataToFile(Metadata metadata, String cachePath)
            throws ObjectModificationException {
        String metadataFilename = metadata.getType().getName() + ".xml";
        OutputTask extractedFile = new OutputTask(ObjectStructure.getPathForExtractedMetadata(null, metadataFilename),
                metadataFilename);
        cachePath = cachePath + new Date().getTime() + metadataFilename;
        extractedFile.setCachePath(cachePath);
        try {
            FileUtils.writeStringToFile(new File(cachePath), metadata.getXml(), "UTF-8");
        } catch (IOException e) {
            throw new ObjectModificationException("Saving the object metadata into file " + cachePath + " failed", e);
        }
        return extractedFile;
    }


    /**
     * Removes metadata to be deleted from the list of inherited metadata.
     * 
     * @param inherited
     *            metadata inherited from previous version.
     * @param deletedMetadata
     *            set of version's deleted provided metadata
     * @return updated list of inherited metadata.
     */
    private List<ObjectProvidedMetadata> removeDeletedMetadata(List<ObjectProvidedMetadata> inherited,
            Set<String> deletedMetadata) {
        for (String path : deletedMetadata) {
            for (int i = 0; i < inherited.size(); i++) {
                if (inherited.get(i).getFilename().equals(path)) {
                    inherited.remove(i);
                    break;
                }
            }
        }
        return inherited;
    }


    /**
     * Removes metadata to be modified from the list of inherited metadata.
     * 
     * @param inherited
     *            metadata inherited from previous version.
     * @param modifiedMetadata
     *            list of version's modified provided metadata
     * @return updated list of inherited metadata.
     */
    private List<ObjectProvidedMetadata> removeModifiedMetadata(List<ObjectProvidedMetadata> inherited,
            List<ObjectProvidedMetadata> modifiedMetadata) {
        for (ObjectProvidedMetadata modified : modifiedMetadata) {
            for (int i = 0; i < inherited.size(); i++) {
                if (inherited.get(i).getFilename().equals(modified.getFilename())) {
                    inherited.remove(i);
                    break;
                }
            }
        }
        return inherited;
    }


    /**
     * Fetches the latest persisted version of the object from the database.
     * 
     * @param object
     *            object whose versions to scan.
     * @return latest object's content version.
     */
    private ContentVersion getLatestVersion(DigitalObject object) {
        QueryModifier<ContentVersionFilterFactory, ContentVersionSorterBuilder, ContentVersion> queryModifier = contentVersionDao
                .createQueryModifier();
        ContentVersionFilterFactory queryFilterFactory = queryModifier.getQueryFilterFactory();
        ContentVersionSorterBuilder querySorterBuilder = queryModifier.getQuerySorterBuilder();
        return contentVersionDao.findFirstResultBy(queryFilterFactory.byObjectId(object.getId()), querySorterBuilder
                .byVersion(false).buildSorter());
    }


    /**
     * Finds main file among all data files of the content version.
     * 
     * @param mainFile
     *            object ralative path of the main file
     * @param files
     *            list of version's files.
     * @return data file if found or <code>null</code> otherwise.
     */
    private DataFile getMainFile(String mainFile, List<DataFileVersion> files) {
        if (mainFile == null || mainFile.isEmpty()) {
            return null;
        }
        for (DataFileVersion file : files) {
            if (file.getDataFile().getFilename().equals(mainFile)) {
                return file.getDataFile();
            }
        }
        return null;
    }


    /**
     * Prepends path prefix to all provided and extracted metadata files belonging to the passed version.
     * 
     * @param result
     *            created content version of the digital object
     * @param objectId
     *            object's identifier in database.
     * @param version
     *            version number.
     * @param providedMetadata
     *            list of provided metadata files to modify.
     */
    private void prependObjectIdAndVersion(ContentVersion result, long objectId, int version,
            List<ObjectProvidedMetadata> providedMetadata) {
        String pathPrefix = ObjectUtils.createObjectAndVersionPath(objectId, version);
        for (ObjectProvidedMetadata metadataFile : providedMetadata) {
            metadataFile.setRepositoryFilepath(pathPrefix + metadataFile.getObjectFilepath());
        }
    }

}
