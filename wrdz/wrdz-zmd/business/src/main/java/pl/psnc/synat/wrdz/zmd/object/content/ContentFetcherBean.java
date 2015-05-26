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

import java.io.InputStream;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.exception.DataStorageResourceException;
import pl.psnc.synat.wrdz.common.utility.UuidGenerator;
import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile;
import pl.psnc.synat.wrdz.zmd.storage.DataStorageAccess;
import pl.psnc.synat.wrdz.zmd.storage.DataStorageException;

/**
 * Provides functionality of fetching object's contents.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ContentFetcherBean implements ContentFetcher {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(ContentFetcherBean.class);

    /**
     * Provides access to module's configuration parameters.
     */
    @Inject
    private ZmdConfiguration zmdConfiguration;

    /**
     * Generates download cache identifier.
     */
    @Inject
    private UuidGenerator uuidGenerator;

    /**
     * Provides an access to objects in data storage.
     */
    @EJB
    private DataStorageAccess dataStorageAccessBean;


    @Override
    public String fetchEntireObject(ContentVersion version, boolean provided, boolean extracted)
            throws DataStorageException {
        String destinationPath = zmdConfiguration.getCacheHome() + "/" + uuidGenerator.generateCacheFolderName();
        try {
            dataStorageAccessBean.fetchObject(version, provided, extracted, destinationPath);
        } catch (DataStorageResourceException e) {
            logger.error("Object download from the data store failed!", e);
            throw new DataStorageException(e);
        }
        return destinationPath;
    }


    @Override
    public String fetchContentFiles(ContentVersion version, List<DataFile> dataFiles, boolean provided,
            boolean extracted)
            throws DataStorageException {
        String destinationPath = zmdConfiguration.getCacheHome() + "/" + uuidGenerator.generateCacheFolderName();
        try {
            dataStorageAccessBean.fetchDataFiles(version, dataFiles, provided, extracted, destinationPath);
        } catch (DataStorageResourceException e) {
            logger.error("Object download from the data store failed!", e);
            throw new DataStorageException(e);
        }
        return destinationPath;
    }


    @Override
    public String fetchMetadataFiles(ContentVersion objectVersion, boolean provided)
            throws DataStorageException {
        String destinationPath = zmdConfiguration.getCacheHome() + "/" + uuidGenerator.generateCacheFolderName();
        try {
            dataStorageAccessBean.fetchMetadataFiles(objectVersion, provided, destinationPath);
        } catch (DataStorageResourceException e) {
            logger.error("Object download from the data store failed!", e);
            throw new DataStorageException(e);
        }
        return destinationPath;
    }


    @Override
    public InputStream getMetadataFile(ContentVersion objectVersion)
            throws DataStorageException {
        try {
            return dataStorageAccessBean.getMetadataFile(objectVersion);
        } catch (DataStorageResourceException e) {
            logger.error("Object download from the data store failed!", e);
            throw new DataStorageException(e);
        }
    }


    @Override
    public InputStream getMetadataFile(MetadataFile file, ContentVersion objectVersion)
            throws DataStorageException {
        try {
            return dataStorageAccessBean.getMetadataFile(file, objectVersion);
        } catch (DataStorageResourceException e) {
            logger.error("Object download from the data store failed!", e);
            throw new DataStorageException(e);
        }
    }

}
