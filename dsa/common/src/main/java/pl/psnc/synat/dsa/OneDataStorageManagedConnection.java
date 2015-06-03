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
package pl.psnc.synat.dsa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.resource.ResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.concurrent.DataStorageLockManager;
import pl.psnc.synat.dsa.exception.DataStorageConnectionException;
import pl.psnc.synat.dsa.exception.NameAlreadyExistsException;
import pl.psnc.synat.dsa.exception.NameSyntaxException;
import pl.psnc.synat.dsa.exception.NoSuchDirectoryException;
import pl.psnc.synat.dsa.exception.NoSuchFileException;
import pl.psnc.synat.dsa.exception.NotDirectoryException;
import pl.psnc.synat.dsa.exception.NotFileException;
import pl.psnc.synat.dsa.util.FilenameUtils;

/**
 * Abstract class for all managed connection to one data storage.
 * 
 */
public abstract class OneDataStorageManagedConnection extends DataStorageManagedConnection {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(OneDataStorageManagedConnection.class);

    /** DSA Logger. */
    private static final Logger DSA_LOGGER = LoggerFactory.getLogger("DSA");

    /** Client for data storage system/protocol. */
    protected DataStorageClient client;

    /** List of atomic write operations performed by the client during transaction managed by this managed connection. */
    protected List<DataStorageWOOperation> operations;


    /**
     * Constructor.
     */
    public OneDataStorageManagedConnection() {
        operations = new ArrayList<DataStorageWOOperation>();
    }


    @Override
    public void cleanup()
            throws ResourceException {
        super.cleanup();
        operations.clear();
        try {
            client.updateFreeSpace();
        } catch (IOException e) {
            logger.error("Error while updating free space left.", e);
            throw new ResourceException("Error while updating free space left.", e);
        } finally {
            DataStorageLockManager.getInstance().unlockAll();
        }
    }


    @Override
    public void destroy()
            throws ResourceException {
        super.destroy();
        client.disconnect();
    }


    @Override
    void createDirectory(String path)
            throws NameSyntaxException, NameAlreadyExistsException, DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToCreate(path, Arrays.asList(new DataStorageClient[] { client }));
        if (doesDirectoryExist(client, path, operations)) {
            throw new NameAlreadyExistsException("Directory '" + path + "' already exists.");
        }
        reflectFoldersStructure(client, path, operations);
    }


    @Override
    void deleteDirectory(String path)
            throws NoSuchDirectoryException, NotDirectoryException, DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToDelete(path);
        if (!doesDirectoryExist(client, path, operations)) {
            throw new NoSuchDirectoryException("Directory '" + path + "' does not exist.");
        }
        String txpath = xares.getTransactionFolderName() + "/" + path;
        try {
            if (client.directoryExists(txpath)) {
                logger.debug("directory " + txpath + " exists");
                client.deleteDirectory(txpath);
            }
            DataStorageWOOperation o = new DataStorageWOOperation(DataStorageWOOperation.Type.DELETE_DIRECTORY, path);
            operations.add(o);
            DSA_LOGGER.info("operation added " + o + " in the scope of transaction's folder: "
                    + xares.getTransactionFolderName() + ";client: " + client);
        } catch (IOException e) {
            logger.error("There was a problem while deleting a directory: " + txpath, e);
            throw new DataStorageConnectionException(e);
        }
    }


    @Override
    Collection<FileInfo> listDirectory(String path)
            throws NoSuchDirectoryException, NotDirectoryException, DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToRead(path);
        if (!doesDirectoryExist(client, path, operations)) {
            throw new NoSuchDirectoryException("Directory '" + path + "' does not exist.");
        }
        String txpath = xares.getTransactionFolderName() + "/" + path;
        Collection<FileInfo> infos = new ArrayList<FileInfo>();
        Collection<File> files = null;
        try {
            logger.debug("listing " + txpath + " direcory");
            files = client.listDirectory(txpath);
            logger.debug("found: " + files.size() + " objects");
        } catch (IOException e) {
            logger.error("There was a problem while listing a directory: " + txpath, e);
            throw new DataStorageConnectionException(e);
        }
        for (File file : files) {
            FileInfo info = new FileInfoBuilder(file).build();
            info.setPath(path);
            infos.add(info);
        }
        if (!wasDirectoryRemoved(path, operations)) {
            try {
                logger.debug("listing " + path + " directory");
                files = client.listDirectory(path);
                logger.debug("found: " + files.size() + " objects");
            } catch (IOException e) {
                logger.error("There was a problem while listing a directory: " + path, e);
                throw new DataStorageConnectionException(e);
            }
            fff: for (File file : files) {
                StringBuffer filepath = new StringBuffer(path).append("/").append(file.getName());
                for (DataStorageWOOperation o : operations) {
                    if ((o.getType().equals(DataStorageWOOperation.Type.DELETE_FILE))
                            && filepath.toString().equals(o.getPath())) {
                        logger.debug("File " + filepath.toString() + " was removed earlier in the same transaction");
                        continue fff;
                    }
                }
                FileInfo info = new FileInfoBuilder(file).build();
                info.setPath(path);
                infos.add(info);
            }
        }
        return infos;
    }


    @Override
    boolean directoryExists(String path)
            throws DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToRead(path);
        return doesDirectoryExist(client, path, operations);
    }


    @Override
    void putFile(String path, String localPath)
            throws NameSyntaxException, NameAlreadyExistsException, NoSuchFileException, DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToCreate(path, Arrays.asList(new DataStorageClient[] { client }));
        if (doesFileExist(client, path, operations)) {
            throw new NameAlreadyExistsException("File '" + path + "' already exists.");
        }
        reflectFoldersStructure(client, FilenameUtils.getParentFolder(path), operations);
        String txpath = xares.getTransactionFolderName() + "/" + path;
        try {
            client.putFile(txpath, localPath);
            DataStorageWOOperation o = new DataStorageWOOperation(DataStorageWOOperation.Type.PUT_FILE, path);
            operations.add(o);
            DSA_LOGGER.info("operation added " + o + " in the scope of transaction's folder: "
                    + xares.getTransactionFolderName() + ";client: " + client);
        } catch (IOException e) {
            logger.error("There was a problem while putting a file: " + txpath, e);
            if (e instanceof FileNotFoundException) {
                throw new NoSuchFileException(e);
            }
            throw new DataStorageConnectionException(e);
        }
    }


    @Override
    void putFile(String path, InputStream src)
            throws NameSyntaxException, NameAlreadyExistsException, DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToCreate(path, Arrays.asList(new DataStorageClient[] { client }));
        if (doesFileExist(client, path, operations)) {
            throw new NameAlreadyExistsException("File '" + path + "' already exists.");
        }
        reflectFoldersStructure(client, FilenameUtils.getParentFolder(path), operations);
        String txpath = xares.getTransactionFolderName() + "/" + path;
        try {
            client.putFile(txpath, src);
            DataStorageWOOperation o = new DataStorageWOOperation(DataStorageWOOperation.Type.PUT_FILE, path);
            operations.add(o);
            DSA_LOGGER.info("operation added " + o + " in the scope of transaction's folder: "
                    + xares.getTransactionFolderName() + ";client: " + client);
        } catch (IOException e) {
            logger.error("There was a problem while putting a file: " + txpath, e);
            if (e instanceof FileNotFoundException) {
                throw new NoSuchFileException(e);
            }
            throw new DataStorageConnectionException(e);
        }
    }


    @Override
    void deleteFile(String path)
            throws NoSuchFileException, NotFileException, DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToDelete(path);
        if (!doesFileExist(client, path, operations)) {
            throw new NoSuchFileException("File '" + path + "' does not exist.");
        }
        String txpath = xares.getTransactionFolderName() + "/" + path;
        try {
            if (client.fileExists(txpath)) {
                logger.debug("file " + txpath + " exists for the client " + client);
                client.deleteFile(txpath);
            }
            DataStorageWOOperation o = new DataStorageWOOperation(DataStorageWOOperation.Type.DELETE_FILE, path);
            operations.add(o);
            DSA_LOGGER.info("operation added " + o + " in the scope of transaction's folder: "
                    + xares.getTransactionFolderName() + ";client:" + client);
        } catch (IOException e) {
            logger.error("There was a problem while deleting a file:" + txpath, e);
            throw new DataStorageConnectionException(e);
        }
    }


    @Override
    void getFile(String path, String localPath)
            throws NoSuchFileException, NotFileException, NameSyntaxException, DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToRead(path);
        if (!doesFileExist(client, path, operations)) {
            throw new NoSuchFileException("File '" + path + "' does not exist.");
        }
        String txpath = xares.getTransactionFolderName() + "/" + path;
        try {
            if (client.fileExists(txpath)) {
                client.getFile(txpath, localPath);
            } else {
                client.getFile(path, localPath);
            }
        } catch (IOException e) {
            logger.error("There was a problem while reading a file.", e);
            if (e.getCause() != null && e.getCause() instanceof NameSyntaxException) {
                throw (NameSyntaxException) e.getCause();
            }
            throw new DataStorageConnectionException(e);
        }
    }


    @Override
    InputStream getFile(String path)
            throws NoSuchFileException, NotFileException, DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToRead(path);
        if (!doesFileExist(client, path, operations)) {
            throw new NoSuchFileException("File '" + path + "' does not exist.");
        }
        String txpath = xares.getTransactionFolderName() + "/" + path;
        try {
            if (client.fileExists(txpath)) {
                return client.getFile(txpath);
            } else {
                return client.getFile(path);
            }
        } catch (IOException e) {
            logger.error("There was a problem while reading a file.", e);
            if (e.getCause() != null && e.getCause() instanceof NameSyntaxException) {
                throw (NameSyntaxException) e.getCause();
            }
            throw new DataStorageConnectionException(e);
        }
    }


    @Override
    boolean fileExists(String path)
            throws DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToRead(path);
        return doesFileExist(client, path, operations);
    }


    @Override
    FileInfo getFileInfo(String path)
            throws DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToRead(path);
        File file = null;
        if (doesFileExist(client, path, operations)) {
            String txpath = xares.getTransactionFolderName() + "/" + path;
            try {
                if (client.exists(txpath)) {
                    logger.debug("file or directory" + path + " exists in the transaction folder for the client "
                            + client);
                    file = client.getFileInfo(txpath);
                } else {
                    logger.debug("file or directory " + path + " exists in the root folder for the client " + client);
                    file = client.getFileInfo(path);
                }
            } catch (IOException e) {
                logger.error("There was a problem while getting info about a file or a directory.", e);
                throw new DataStorageConnectionException(e);
            }
            FileInfo info = new FileInfoBuilder(file).build();
            info.setPath(path);
            return info;
        } else {
            return null;
        }
    }

}
