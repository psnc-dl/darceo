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
package pl.psnc.synat.wrdz.zmd.object;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.exception.DataStorageResourceException;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataProcessingException;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataReader;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataReaderFactory;
import pl.psnc.synat.wrdz.common.performance.Profiler;
import pl.psnc.synat.wrdz.common.utility.UuidGenerator;
import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.download.DownloadTask;
import pl.psnc.synat.wrdz.zmd.download.Downloader;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.exception.DownloadException;
import pl.psnc.synat.wrdz.zmd.input.InputFile;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;
import pl.psnc.synat.wrdz.zmd.output.object.ObjectCreationCache;
import pl.psnc.synat.wrdz.zmd.storage.DataStorageAccess;
import pl.psnc.synat.wrdz.zmd.storage.DataStorageException;

/**
 * Class providing functionality of creation of digital objects.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ObjectCreatorBean implements ObjectCreator {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ObjectCreatorBean.class);

    /**
     * Download manager bean.
     */
    @EJB
    private Downloader downloader;

    /**
     * Digital object builder bean.
     */
    @EJB
    private DigitalObjectBuilder digitalObjectBuilder;

    /**
     * Provides an access to objects in data storage.
     */
    @EJB
    private ObjectUpdater objectUpdaterBean;

    /**
     * Provides an access to objects in data storage.
     */
    @EJB
    private DataStorageAccess dataStorageAccessBean;

    /**
     * ZMD module configuration.
     */
    @Inject
    private ZmdConfiguration zmdConfiguration;

    /**
     * Absolute path to the cache root folder.
     */
    private String cacheHome;

    /**
     * Identifier generator for cache session.
     */
    @Inject
    private UuidGenerator uuidGenerator;


    /**
     * Initializes this EJB.
     */
    @PostConstruct
    public void initialize() {
        cacheHome = zmdConfiguration.getCacheHome();
    }


    @Override
    public DigitalObject createObject(ObjectCreationRequest request)
            throws ObjectCreationException {
        String cacheDir = uuidGenerator.generateCacheFolderName() + "/";
        ObjectCreationCache cachedFiles = null;
        DigitalObject object = null;
        try {
            cachedFiles = downloadResourcesToCache(request, cacheDir);
            parseProvidedMetadataToRequest(request, cachedFiles);
            object = createObjectStructure(request, cachedFiles);
            if (zmdConfiguration.saveInRepository()) {
                saveObjectInRepository(object);
            }
            if (request.getMigratedFrom() != null) {
                objectUpdaterBean.updateOriginOrDerivative(request.getMigratedFrom().getIdentifier());
            }
        } catch (DownloadException e) {
            logger.error("Caught DownloadException: ", e);
            throw new ObjectCreationException("Could not create object - download problems occured!");
        } catch (IllegalArgumentException e) {
            throw new ObjectCreationException("Could not create object - illegal query request fomat!");
        } catch (DataStorageException e) {
            throw new ObjectCreationException("Could not create object - data storge exception.");
        } catch (ObjectModificationException e) {
            throw new ObjectCreationException("Could not create object - problem with updating an origin object.");
        }
        return object;
    }


    /**
     * Downloads new files specified in creation request into the cache.
     * 
     * @param request
     *            object's creation request.
     * @param cacheDir
     *            specifies the target directory in cache that will contain downloaded structure.
     * @return object containing information about downloaded files and their location in cache.
     * @throws DownloadException
     *             if any error during download occurs
     * @throws IllegalArgumentException
     *             if request passes data in unrecognized format.
     */
    private ObjectCreationCache downloadResourcesToCache(ObjectCreationRequest request, String cacheDir)
            throws DownloadException, IllegalArgumentException {
        Profiler.start("downloading resources to cache");
        try {
            ObjectCreationCache result = new ObjectCreationCache(cacheHome + "/" + cacheDir);
            Set<InputFile> filesToAdd = request.getInputFiles();
            if (filesToAdd != null && !filesToAdd.isEmpty()) {
                result.setAddedFiles(downloader.downloadFilesToCache(filesToAdd, cacheDir));
            }
            Map<String, URI> metadataToAdd = request.getObjectMetadata();
            if (metadataToAdd != null && !metadataToAdd.isEmpty()) {
                result.setAddedMetadata(downloader.downloadMetadataToCache(null, metadataToAdd, cacheDir));
            }
            return result;
        } finally {
            Profiler.stop("downloading resources to cache");
        }
    }


    /**
     * Parses the provided object metadata to some parameters absent in the request and set them into this request.
     * Morever, in case when these METS metadata carry only these parameters, it remove it from the list of provided
     * metadata of an object.
     * 
     * @param request
     *            object's creation request.
     * @param cachedFiles
     *            object containing information about downloaded files and their location in cache.
     */
    private void parseProvidedMetadataToRequest(ObjectCreationRequest request, ObjectCreationCache cachedFiles) {
        Profiler.start("parsing provided metadata");
        try {
            if (cachedFiles.getAddedMetadata() != null && request.getObjectMetadata() != null) {
                DownloadTask downloadedMetadataToRemove = null;
                String objectMatadataNameToRemove = null;
                for (DownloadTask downloadedMetadata : cachedFiles.getAddedMetadata()) {
                    MetsMetadataReader metsMetadataReader = MetsMetadataReaderFactory.getInstance()
                            .getMetsMetadataReader();
                    try {
                        metsMetadataReader.parse(new File(downloadedMetadata.getCachePath()));
                        String providedId = metsMetadataReader.getObjectIdentifier();
                        if (providedId != null) {
                            if (request.getProposedId() == null) {
                                request.setProposedId(providedId);
                            }
                            if (metsMetadataReader.isEmpty()) {
                                for (Entry<String, URI> objectMetadata : request.getObjectMetadata().entrySet()) {
                                    if (objectMetadata.getValue().equals(downloadedMetadata.getUri())) {
                                        objectMatadataNameToRemove = objectMetadata.getKey();
                                        downloadedMetadataToRemove = downloadedMetadata;
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (MetsMetadataProcessingException e) {
                        logger.debug("provided object metadata is not correct METS metadata: " + e.toString());
                    }
                }
                if (objectMatadataNameToRemove != null) {
                    request.getObjectMetadata().remove(objectMatadataNameToRemove);
                }
                if (downloadedMetadataToRemove != null) {
                    cachedFiles.getAddedMetadata().remove(downloadedMetadataToRemove);
                }
            }
        } finally {
            Profiler.stop("parsing provided metadata");
        }
    }


    /**
     * Creates object's structure in the database.
     * 
     * @param request
     *            object's creation request.
     * @param cachedFiles
     *            object containing information about downloaded files and their location in cache.
     * @return object's identifier
     * @throws ObjectCreationException
     *             if any error while creating structure in database occurs.
     */
    private DigitalObject createObjectStructure(ObjectCreationRequest request, ObjectCreationCache cachedFiles)
            throws ObjectCreationException {
        Profiler.start("creating object structure");
        try {
            return digitalObjectBuilder.buildDigitalObject(request, cachedFiles);
        } catch (ObjectModificationException e) {
            throw new ObjectCreationException(e);
        } finally {
            Profiler.stop("creating object structure");
        }
    }


    /**
     * Saves object's files and metadata files in the repository.
     * 
     * @param object
     *            digital object to which the content and metadata belong
     * @throws DataStorageException
     *             when some error during getting a connection to data storage occurs
     */
    private void saveObjectInRepository(DigitalObject object)
            throws DataStorageException {
        Profiler.start("saving object in repository");
        try {
            dataStorageAccessBean.createObject(object);
        } catch (DataStorageResourceException e) {
            logger.error("Object creation in data storage failed!", e);
            throw new DataStorageException(e);
        } finally {
            Profiler.stop("saving object in repository");
        }
    }

}
