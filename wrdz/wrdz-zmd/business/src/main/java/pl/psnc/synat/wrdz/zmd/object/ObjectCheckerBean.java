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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.ContentVersionDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileProvidedMetadataDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileProvidedMetadataFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.ObjectProvidedMetadataDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.ObjectProvidedMetadataFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectProvidedMetadata;

/**
 * Bean providing additional functionalities supporting {@link ObjectManagerBean}.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ObjectCheckerBean implements ObjectChecker {

    /**
     * Content version DAO.
     */
    @EJB
    private ContentVersionDao contentVersionDao;

    /**
     * Digital object DAO.
     */
    @EJB
    private DigitalObjectDao digitalObjectDao;

    /**
     * Data file DAO.
     */
    @EJB
    private DataFileDao dataFileDao;

    /**
     * File's provided metadata DAO.
     */
    @EJB
    private FileProvidedMetadataDao fileProvidedMetadataDao;

    /**
     * Object's provided metadata DAO.
     */
    @EJB
    private ObjectProvidedMetadataDao objectProvidedMetadataDao;


    @Override
    public boolean checkIfDigitalObjectExists(String identifier) {
        DigitalObject digitalObject = digitalObjectDao.getDigitalObject(identifier);
        return (digitalObject != null);
    }


    @Override
    public boolean checkIfObjectsVersionExists(String identifier, Integer versionNo) {
        DigitalObject digitalObject = digitalObjectDao.getDigitalObject(identifier);
        if (digitalObject != null && digitalObject.getCurrentVersion() != null) {
            Integer currentVersionNo = digitalObject.getCurrentVersion().getVersion();
            if (currentVersionNo.equals(versionNo) || versionNo == null) {
                return true;
            }
            if (currentVersionNo > versionNo) {
                ContentVersion contentVersion = contentVersionDao.getContentVersion(digitalObject.getId(), versionNo);
                if (contentVersion != null) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public boolean checkIfDataFileExistsInVersion(String path, ContentVersion version) {
        DataFile dataFile = dataFileDao.getDataFileFromVersion(path, version);
        return (dataFile != null);
    }


    @Override
    public boolean checkIfFileProvidedMetadataExistsInVersion(String name, ContentVersion version, DataFile dataFile) {
        FileProvidedMetadataFilterFactory filterFactory = fileProvidedMetadataDao.createQueryModifier()
                .getQueryFilterFactory();
        QueryFilter<FileProvidedMetadata> filter = filterFactory.byIncludedInVersion(version.getId());
        filter = filterFactory.and(filter, filterFactory.byProvidedFor(dataFile.getId()));
        filter = filterFactory.and(filter, filterFactory.byMetadataFileName(name));
        return fileProvidedMetadataDao.findFirstResultBy(filter) != null;
    }


    @Override
    public boolean checkIfObjectProvidedMetadataExistsInVersion(String name, ContentVersion version) {
        ObjectProvidedMetadataFilterFactory filterFactory = objectProvidedMetadataDao.createQueryModifier()
                .getQueryFilterFactory();
        QueryFilter<ObjectProvidedMetadata> filter = filterFactory.byIncludedInVersion(version.getId());
        filter = filterFactory.and(filter, filterFactory.byMetadataFileName(name));
        return objectProvidedMetadataDao.findFirstResultBy(filter) != null;
    }


    @Override
    public boolean containsDataFiles(String identifier, String formatPuid) {
        return digitalObjectDao.containsDataFiles(identifier, formatPuid);
    }

}
