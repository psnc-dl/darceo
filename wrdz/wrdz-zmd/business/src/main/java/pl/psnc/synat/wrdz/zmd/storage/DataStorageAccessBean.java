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
package pl.psnc.synat.wrdz.zmd.storage;

import java.io.InputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import pl.psnc.synat.dsa.DataStorageConnection;
import pl.psnc.synat.dsa.DataStorageConnectionFactory;
import pl.psnc.synat.dsa.DataStorageConnectionSpec;
import pl.psnc.synat.dsa.exception.DataStorageResourceException;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.object.ObjectStructure;
import pl.psnc.synat.wrdz.zmd.object.helpers.ObjectUtils;
import pl.psnc.synat.wrdz.zu.dto.user.OrganizationDto;
import pl.psnc.synat.wrdz.zu.dto.user.UserDto;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Provides an access to objects (and its content and metadata) in a repository.
 */
@Stateless
public class DataStorageAccessBean implements DataStorageAccess {

    /** User browser. */
    @EJB(name = "UserBrowser")
    private UserBrowser userBrowser;

    /**
     * Factory of data storage connections.
     **/
    @Resource(mappedName = "jca/dsa", shareable = false)
    private DataStorageConnectionFactory dsConnectionFactory;

    /**
     * Connection to data storage.
     */
    private DataStorageConnection connection;


    /**
     * Retrieves a connection to data storage of the given organization.
     * 
     * @param organizationName
     *            name of the organization
     * @throws DataStorageResourceException
     *             when connection cannot be retrieved
     */
    private void initConnection(String organizationName)
            throws DataStorageResourceException {
        DataStorageConnectionSpec spec = new DataStorageConnectionSpec(organizationName);
        connection = dsConnectionFactory.getConnection(spec);
    }


    /**
     * Close the connection.
     */
    private void closeConnection() {
        connection.close();
        connection = null;
    }


    /**
     * Constructs the path to the root folder.
     * 
     * @param userHomeDir
     *            user home directory
     * @param organizationRootPath
     *            organization root path
     * @return root folder path
     */
    private String getRootForUser(String userHomeDir, String organizationRootPath) {

        if (organizationRootPath.endsWith("/")) {
            organizationRootPath = organizationRootPath.substring(0, organizationRootPath.length() - 1);
        }
        if (userHomeDir.startsWith("/")) {
            organizationRootPath += userHomeDir;
        } else {
            organizationRootPath += "/" + userHomeDir;
        }
        if (organizationRootPath.endsWith("/")) {
            organizationRootPath = organizationRootPath.substring(0, organizationRootPath.length() - 1);
        }
        return organizationRootPath;
    }


    /**
     * Stores a data file with its metadata files in data storage.
     * 
     * @param dataFile
     *            date file
     * @param root
     *            root folder for the object
     */
    private void storeDataFile(DataFile dataFile, String root) {
        if (dataFile.getCachePath() != null) {
            storeFile(root, dataFile.getRepositoryFilepath(), dataFile.getCachePath());
        }
        for (FileExtractedMetadata metadataFile : dataFile.getExtractedMetadata()) {
            storeMetadataFile(metadataFile, root);
        }
    }


    /**
     * Fetches a data file with its metadata (optional) files from data storage.
     * 
     * @param dataFile
     *            data file
     * @param provided
     *            whether provided metadata should be fetched
     * @param extracted
     *            whether extracted metadata should be fetched
     * @param version
     *            version of the data file and its metadata files
     * @param root
     *            root folder for the object
     * @param destination
     *            destination folder
     */
    private void fetchDataFile(DataFile dataFile, boolean provided, boolean extracted, ContentVersion version,
            String root, String destination) {
        fetchFile(root, dataFile.getRepositoryFilepath(), destination + "/" + dataFile.getObjectFilepath());
        if (provided) {
            for (DataFileVersion fileVersion : dataFile.getIncludedIn()) {
                if (fileVersion.getContentVersion().equals(version)) {
                    for (FileProvidedMetadata metadataFile : fileVersion.getProvidedMetadata()) {
                        fetchFile(root, metadataFile.getRepositoryFilepath(),
                            destination + "/" + metadataFile.getObjectFilepath());
                    }
                }
            }

        }
        if (extracted) {
            for (FileExtractedMetadata metadataFile : dataFile.getExtractedMetadata()) {
                fetchFile(root, metadataFile.getRepositoryFilepath(),
                    destination + "/" + metadataFile.getObjectFilepath());
            }
        }
    }


    /**
     * Stores a global metadata file in data storage.
     * 
     * @param metadataFile
     *            metadata file
     * @param root
     *            root folder for the object
     */
    private void storeMetadataFile(MetadataFile metadataFile, String root) {
        if (metadataFile.getCachePath() != null) {
            storeFile(root, metadataFile.getRepositoryFilepath(), metadataFile.getCachePath());
        }
    }


    /**
     * Fetches a metadata data file from data storage.
     * 
     * @param metadataFile
     *            metadata file
     * @param root
     *            root folder for the object
     * @param destination
     *            destination folder
     */
    private void fetchMetadataFile(MetadataFile metadataFile, String root, String destination) {
        fetchFile(root, metadataFile.getRepositoryFilepath(), destination + "/" + metadataFile.getObjectFilepath());
    }


    /**
     * Get a metadata data file from data storage.
     * 
     * @param metadataFile
     *            metadata file
     * @param root
     *            root folder for the object
     * @return input stream to metadata file
     */
    private InputStream getMetadataFile(MetadataFile metadataFile, String root) {
        return getFile(root, metadataFile.getRepositoryFilepath());
    }


    /**
     * Stores a file in data storage.
     * 
     * @param root
     *            root folder for the object
     * @param targetPath
     *            path in a context of the object
     * @param sourcePath
     *            source path in the cache
     */
    private void storeFile(String root, String targetPath, String sourcePath) {
        String path = root + "/" + targetPath;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        connection.putFile(path, sourcePath);
    }


    /**
     * Fetches a file from data storage.
     * 
     * @param root
     *            root folder for the object
     * @param sourcePath
     *            path in a context of the object
     * @param targetPath
     *            target path in the cache
     */
    private void fetchFile(String root, String sourcePath, String targetPath) {
        String path = root + "/" + sourcePath;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        connection.getFile(path, targetPath);
    }


    /**
     * Get an input stream to a file from data storage.
     * 
     * @param root
     *            root folder for the object
     * @param sourcePath
     *            path in a context of the object
     */
    public InputStream getFile(String root, String sourcePath) {
        String path = root + "/" + sourcePath;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return connection.getFile(path);
    }


    @Override
    public void createObject(DigitalObject object)
            throws DataStorageResourceException {
        createVersion(object.getCurrentVersion());
    }


    @Override
    public void createVersion(ContentVersion version)
            throws DataStorageResourceException {
        UserDto owner = userBrowser.getUser(version.getObject().getOwnerId());
        OrganizationDto organization = userBrowser.getOrganization(owner.getUsername());
        initConnection(organization.getName());
        String root = getRootForUser(owner.getHomeDir(), organization.getRootPath());
        try {
            for (DataFileVersion file : version.getFiles()) {
                storeDataFile(file.getDataFile(), root);
                for (FileProvidedMetadata metadataFile : file.getProvidedMetadata()) {
                    storeMetadataFile(metadataFile, root);
                }
            }
            for (ObjectProvidedMetadata metadataFile : version.getProvidedMetadata()) {
                storeMetadataFile(metadataFile, root);
            }
            if (version.getExtractedMetadata() != null) {
                storeMetadataFile(version.getExtractedMetadata(), root);
            }
        } finally {
            closeConnection();
        }
    }


    @Override
    public void fetchObject(ContentVersion version, boolean provided, boolean extracted, String destPath)
            throws DataStorageResourceException {
        UserDto owner = userBrowser.getUser(version.getObject().getOwnerId());
        OrganizationDto organization = userBrowser.getOrganization(owner.getUsername());
        initConnection(organization.getName());
        String root = getRootForUser(owner.getHomeDir(), organization.getRootPath());
        try {
            for (DataFileVersion file : version.getFiles()) {
                fetchDataFile(file.getDataFile(), provided, extracted, version, root, destPath);
            }
            if (provided) {
                for (MetadataFile metadataFile : version.getProvidedMetadata()) {
                    fetchMetadataFile(metadataFile, root, destPath);
                }
            }
            if (extracted) {
                fetchMetadataFile(version.getExtractedMetadata(), root, destPath);
            }
        } finally {
            closeConnection();
        }
    }


    @Override
    public void fetchDataFiles(ContentVersion version, List<DataFile> dataFiles, boolean provided, boolean extracted,
            String destPath)
            throws DataStorageResourceException {
        UserDto owner = userBrowser.getUser(version.getObject().getOwnerId());
        OrganizationDto organization = userBrowser.getOrganization(owner.getUsername());
        initConnection(organization.getName());
        String root = getRootForUser(owner.getHomeDir(), organization.getRootPath());
        try {
            for (DataFile dataFile : dataFiles) {
                fetchDataFile(dataFile, provided, extracted, version, root, destPath);
            }
        } finally {
            closeConnection();
        }
    }


    @Override
    public InputStream getMetadataFile(ContentVersion objectVersion)
            throws DataStorageResourceException {
        UserDto owner = userBrowser.getUser(objectVersion.getObject().getOwnerId());
        OrganizationDto organization = userBrowser.getOrganization(owner.getUsername());
        initConnection(organization.getName());
        String root = getRootForUser(owner.getHomeDir(), organization.getRootPath());
        try {
            if (objectVersion.getExtractedMetadata() != null) {
                return getMetadataFile(objectVersion.getExtractedMetadata(), root);
            } else {
                throw new DataStorageResourceException("Mets file for object not found!");
            }
        } finally {
            closeConnection();
        }
    }


    @Override
    public InputStream getMetadataFile(MetadataFile file, ContentVersion objectVersion)
            throws DataStorageResourceException {
        UserDto owner = userBrowser.getUser(objectVersion.getObject().getOwnerId());
        OrganizationDto organization = userBrowser.getOrganization(owner.getUsername());
        initConnection(organization.getName());
        String root = getRootForUser(owner.getHomeDir(), organization.getRootPath());
        InputStream result = null;
        try {
            result = getMetadataFile(file, root);
            if (result == null) {
                throw new DataStorageResourceException("Metadata file for object not found!");
            }
        } finally {
            closeConnection();
        }
        return result;
    }


    @Override
    public void fetchMetadataFiles(ContentVersion objectVersion, boolean provided, String destPath)
            throws DataStorageResourceException {
        UserDto owner = userBrowser.getUser(objectVersion.getObject().getOwnerId());
        OrganizationDto organization = userBrowser.getOrganization(owner.getUsername());
        initConnection(organization.getName());
        String root = getRootForUser(owner.getHomeDir(), organization.getRootPath());
        try {
            if (objectVersion.getExtractedMetadata() != null) {
                fetchMetadataFile(objectVersion.getExtractedMetadata(), root, destPath);
            }
            if (provided && !objectVersion.getProvidedMetadata().isEmpty()) {
                for (MetadataFile metadataFile : objectVersion.getProvidedMetadata()) {
                    fetchMetadataFile(metadataFile, root, destPath);
                }
            }
        } finally {
            closeConnection();
        }
    }


    @Override
    public void deleteVersion(ContentVersion version)
            throws DataStorageResourceException {
        UserDto owner = userBrowser.getUser(version.getObject().getOwnerId());
        OrganizationDto organization = userBrowser.getOrganization(owner.getUsername());
        initConnection(organization.getName());
        String root = getRootForUser(owner.getHomeDir(), organization.getRootPath());
        String fullPath = root + "/" + ObjectUtils.createObjectIdPath(version.getObject().getId()) + "/"
                + ObjectUtils.createVersionPath(version.getVersion());
        if (fullPath.startsWith("/")) {
            fullPath = fullPath.substring(1);
        }
        try {
            connection.deleteDirectory(fullPath + "/" + ObjectStructure.CONTENT);
            connection.deleteDirectory(fullPath + "/" + ObjectStructure.METADATA);
        } finally {
            closeConnection();
        }
    }


    @Override
    public void deleteObject(DigitalObject object)
            throws DataStorageResourceException {
        UserDto owner = userBrowser.getUser(object.getOwnerId());
        OrganizationDto organization = userBrowser.getOrganization(owner.getUsername());
        initConnection(organization.getName());
        String root = getRootForUser(owner.getHomeDir(), organization.getRootPath());
        String fullPath = root + "/" + ObjectUtils.createObjectIdPath(object.getId());
        if (fullPath.startsWith("/")) {
            fullPath = fullPath.substring(1);
        }
        try {
            connection.deleteDirectory(fullPath + "/00"); // not more then 16581375 versions!
        } finally {
            closeConnection();
        }
    }

}
