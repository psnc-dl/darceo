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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import pl.psnc.synat.dsa.exception.DataStorageResourceException;
import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.ContentVersionDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.ContentVersionFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileProvidedMetadataDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.ObjectProvidedMetadataDao;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.storage.DataStorageAccess;
import pl.psnc.synat.wrdz.zu.permission.ObjectPermissionManager;

/**
 * Provides methods for deletion of object or it's version from the system.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ObjectDeleterBean implements ObjectDeleter {

    /**
     * Digital object DAO bean.
     */
    @EJB
    private DigitalObjectDao digitalObjectDao;

    /**
     * Content version DAO bean.
     */
    @EJB
    private ContentVersionDao contentVersionDao;

    /**
     * Data file DAO.
     */
    @EJB
    private DataFileDao dataFileDao;

    /**
     * Object's provided metadata DAO.
     */
    @EJB
    private ObjectProvidedMetadataDao objectsProvidedMetadataDao;

    /**
     * File's provided metadata DAO.
     */
    @EJB
    private FileProvidedMetadataDao fileProvidedMetadataDao;

    /**
     * Provides an access to objects in data storage.
     */
    @EJB
    private DataStorageAccess dataStorageAccessBean;

    /** Object permission manager. */
    @EJB(name = "ObjectPermissionManager")
    private ObjectPermissionManager permissionManager;


    @Override
    public int deleteContentVersion(ContentVersion previousVersion)
            throws ObjectDeletionException {
        DigitalObject object = previousVersion.getObject();
        List<ContentVersion> removedcontentVersions = getContentVersionsAfter(object.getId(),
            previousVersion.getVersion());
        if (removedcontentVersions != null && !removedcontentVersions.isEmpty()) {
            List<ContentVersion> versions = object.getVersions();
            for (ContentVersion contentVersion : removedcontentVersions) {
                try {
                    dataStorageAccessBean.deleteVersion(contentVersion);
                } catch (DataStorageResourceException e) {
                    throw new ObjectDeletionException("Unable to delete version " + contentVersion.getVersion()
                            + " - removal from data store failed.", e);
                }
                versions.remove(contentVersion);
                deleteContentVersionFromDb(contentVersion);
            }
            object.setCurrentVersion(previousVersion);
            object.setVersions(versions);
            digitalObjectDao.merge(object);
            return previousVersion.getVersion();
        }
        return 0;
    }


    @Override
    public void deleteDigitalObject(DigitalObject object)
            throws ObjectDeletionException {
        try {
            dataStorageAccessBean.deleteObject(object);
        } catch (DataStorageResourceException e) {
            throw new ObjectDeletionException("Unable to delete object - removal from data store failed.", e);
        }
        object.setCurrentVersion(null);
        List<ContentVersion> versions = object.getVersions();
        for (ContentVersion contentVersion : versions) {
            deleteContentVersionFromDb(contentVersion);
        }
        object.setVersions(null);
        digitalObjectDao.merge(object);
        permissionManager.removePermissions(object.getId());
    }


    /**
     * Gets list of content versions after the specified one.
     * 
     * @param objectId
     *            object which versions are to be retrieved.
     * @param versionNo
     *            number of version all fetched versions'numbers have to be greater than.
     * @return results of the query to the database.
     */
    @SuppressWarnings("unchecked")
    private List<ContentVersion> getContentVersionsAfter(Long objectId, Integer versionNo) {
        ContentVersionFilterFactory queryFilterFactory = contentVersionDao.createQueryModifier()
                .getQueryFilterFactory();
        return contentVersionDao.findBy(
            queryFilterFactory.and(queryFilterFactory.byObjectId(objectId),
                queryFilterFactory.byVersionNewerThan(versionNo)), true);
    }


    /**
     * Deletes content version from database.
     * 
     * @param contentVersion
     *            content version to be removed.
     */
    private void deleteContentVersionFromDb(ContentVersion contentVersion) {
        removeOrphanFiles(contentVersion);
        removeOrphanFileProvidedMetadata(contentVersion.getFiles());
        removeOrphanProvidedMetadata(contentVersion.getProvidedMetadata(), contentVersion.getId());
        contentVersionDao.delete(contentVersion);
    }


    /**
     * Removes metadata that will be orphaned when content version is deleted.
     * 
     * @param contentVersion
     *            content version for which to remove the metadata it will orphan when deleted.
     */
    private void removeOrphanFiles(ContentVersion contentVersion) {
        List<DataFileVersion> files = contentVersion.getFiles();
        List<DataFile> orphaned = new ArrayList<DataFile>();
        for (DataFileVersion file : files) {
            if (file.getDataFile().getIncludedIn().size() == 1) {
                orphaned.add(file.getDataFile());
                dataFileDao.delete(file.getDataFile());
            }
        }
    }


    private void removeOrphanFileProvidedMetadata(List<DataFileVersion> files) {
        for (DataFileVersion fileVersion : files) {
            for (FileProvidedMetadata metadata : fileVersion.getProvidedMetadata()) {
                if (metadata.getProvidedFor().size() == 1) {
                    fileProvidedMetadataDao.delete(metadata);
                }
            }
        }
    }


    /**
     * Removes orphaned object's provided metadata.
     * 
     * @param providedMetadata
     *            object's provided metadata.
     * @param versionId
     *            version identifier.
     */
    private void removeOrphanProvidedMetadata(List<ObjectProvidedMetadata> providedMetadata, long versionId) {
        for (ObjectProvidedMetadata providedMetadataFile : providedMetadata) {
            List<ContentVersion> includedIn = providedMetadataFile.getProvidedFor();
            if (includedIn.size() == 1 && includedIn.get(0).getId() == versionId) {
                objectsProvidedMetadataDao.delete(providedMetadataFile);
            }
        }
    }
}
