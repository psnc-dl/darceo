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

import java.net.URI;
import java.util.Map;
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
import pl.psnc.synat.wrdz.common.performance.Profiler;
import pl.psnc.synat.wrdz.common.utility.UuidGenerator;
import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.download.Downloader;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.exception.DownloadException;
import pl.psnc.synat.wrdz.zmd.input.InputFile;
import pl.psnc.synat.wrdz.zmd.input.InputFileUpdate;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;
import pl.psnc.synat.wrdz.zmd.output.object.ObjectModificationCache;
import pl.psnc.synat.wrdz.zmd.storage.DataStorageAccess;
import pl.psnc.synat.wrdz.zmd.storage.DataStorageException;

/**
 * Class providing functionality of modification of digital objects.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ObjectModifierBean implements ObjectModifier {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ObjectModifierBean.class);

    /**
     * Download manager bean.
     */
    @EJB
    private Downloader downloader;

    /**
     * Digital object builder.
     */
    @EJB
    DigitalObjectBuilder digitalObjectBuilder;

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
    public ContentVersion modifyObject(ObjectModificationRequest request, DigitalObject target)
            throws ObjectModificationException {
        ContentVersion newVersion = null;
        String cacheDir = uuidGenerator.generateCacheFolderName() + "/";
        try {
            ObjectModificationCache cachedFiles = downloadResourcesToCache(request, cacheDir);
            newVersion = createVersionStructure(request, target, cachedFiles);
            saveVersionInRepository(newVersion);
            if (request.getMigratedFrom() != null) {
                objectUpdaterBean.updateOriginOrDerivative(request.getMigratedFrom().getIdentifier());
            }
        } catch (DownloadException e) {
            throw new ObjectModificationException("Could not create new version - download problems occured!", e);
        } catch (IllegalArgumentException e) {
            throw new ObjectModificationException("Could not create new version - illegal query request fomat!", e);
        } catch (DataStorageException e) {
            throw new ObjectModificationException(
                    "Could not create new version - unable to save object into the repository, error occured.", e);
        }
        return newVersion;
    }


    /**
     * Downloads new files specified in modification request (as both added or modified) into the cache.
     * 
     * @param request
     *            object's modification request.
     * @param cacheDir
     *            specifies the target directory in cache that will contain downloaded structure.
     * @return object containing information about downloaded files and their location in cache.
     * @throws DownloadException
     *             if any error during download occurs
     * @throws IllegalArgumentException
     *             if request passes data in unrecognized format.
     */
    private ObjectModificationCache downloadResourcesToCache(ObjectModificationRequest request, String cacheDir)
            throws DownloadException, IllegalArgumentException {
        Profiler.start("downloading resources to cache");
        try {
            ObjectModificationCache result = new ObjectModificationCache(cacheHome + "/" + cacheDir);
            Set<InputFile> filesToAdd = request.getInputFilesToAdd();
            if (filesToAdd != null && !filesToAdd.isEmpty()) {
                result.setAddedFiles(downloader.downloadFilesToCache(filesToAdd, cacheDir));
            }
            Map<String, URI> metadataToAdd = request.getObjectMetadataToAdd();
            if (metadataToAdd != null && !metadataToAdd.isEmpty()) {
                result.setAddedMetadata(downloader.downloadMetadataToCache(null, metadataToAdd, cacheDir));
            }
            if (!request.getDeleteAllContent()) {
                Set<InputFileUpdate> filesToModify = request.getInputFilesToModify();
                if (filesToModify != null && !filesToModify.isEmpty()) {
                    result.setModifiedFiles(downloader.downloadUpdatedFilesToCache(filesToModify, cacheDir));
                }
                Map<String, URI> metadataToModify = request.getObjectMetadataToModify();
                if (metadataToModify != null && !metadataToModify.isEmpty()) {
                    result.setModifiedMetadata(downloader.downloadMetadataToCache(null, metadataToModify, cacheDir));
                }
            }
            return result;
        } finally {
            Profiler.stop("downloading resources to cache");
        }
    }


    /**
     * Creates new version's structure in the database.
     * 
     * @param request
     *            object's modification request.
     * @param object
     *            object to which the version is to be added.
     * @param cachedFiles
     *            object containing information about downloaded files and their location in cache.
     * @return created content version object persisted in the persistence context.
     * @throws ObjectModificationException
     *             if any error while creating structure in database occurs.
     */
    private ContentVersion createVersionStructure(ObjectModificationRequest request, DigitalObject object,
            ObjectModificationCache cachedFiles)
            throws ObjectModificationException {
        Profiler.start("creating object structure");
        try {
            return digitalObjectBuilder.buildDigitalObject(request, object, cachedFiles).getCurrentVersion();
        } finally {
            Profiler.stop("creating object structure");
        }
    }


    /**
     * Creates object's structure in the repository.
     * 
     * @param version
     *            version being created
     * @throws DataStorageException
     *             should any problem with repository occur.
     */
    private void saveVersionInRepository(ContentVersion version)
            throws DataStorageException {
        Profiler.start("saving version in repository");
        try {
            dataStorageAccessBean.createVersion(version);
        } catch (DataStorageResourceException e) {
            logger.error("Object modification in data storage failed!", e);
            throw new DataStorageException(e);
        } finally {
            Profiler.stop("saving version in repository");
        }
    }

}
