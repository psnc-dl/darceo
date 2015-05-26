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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * Abstract class for all managed connection to many data storages.
 * 
 */
public abstract class MultiDataStorageManagedConnection extends DataStorageManagedConnection {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(OneDataStorageManagedConnection.class);

    /** DSA Logger. */
    private static final Logger DSA_LOGGER = LoggerFactory.getLogger("DSA");

    /** List of clients for data storage systems/protocols. */
    protected List<DataStorageClient> clients;

    /**
     * Map of a list of atomic write operations performed by the clients during transaction managed by this managed
     * connection.
     */
    protected Map<DataStorageClient, List<DataStorageWOOperation>> operations;

    /** Number of copies that should be distributed among archives. */
    protected int redundancy;


    /**
     * Constructor.
     * 
     * @param redundancy
     *            redundancy ratio
     */
    public MultiDataStorageManagedConnection(int redundancy) {
        this.redundancy = redundancy;
        operations = new HashMap<DataStorageClient, List<DataStorageWOOperation>>();
    }


    @Override
    public void cleanup()
            throws ResourceException {
        super.cleanup();
        for (Map.Entry<DataStorageClient, List<DataStorageWOOperation>> entry : operations.entrySet()) {
            entry.getValue().clear();
        }
        try {
            for (DataStorageClient client : clients) {
                client.updateFreeSpace();
            }
            Collections.sort(clients, new DataStorageClientComparator());
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
        for (DataStorageClient client : clients) {
            client.disconnect();
        }
    }


    @Override
    void createDirectory(String path)
            throws NameSyntaxException, NameAlreadyExistsException, DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToCreate(path, clients.subList(0, redundancy));
        for (DataStorageClient client : clients) {
            if (doesDirectoryExist(client, path, operations.get(client))) {
                throw new NameAlreadyExistsException("Directory '" + path + "' already exists.");
            }
        }
        int i = 0;
        for (DataStorageClient client : clients) {
            reflectFoldersStructure(client, path, operations.get(client));
            i++;
            if (i == redundancy) {
                break;
            }
        }
    }


    @Override
    void deleteDirectory(String path)
            throws NoSuchDirectoryException, NotDirectoryException, DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToDelete(path);
        Map<DataStorageClient, Boolean> exist = new HashMap<DataStorageClient, Boolean>();
        for (DataStorageClient client : clients) {
            exist.put(client, doesDirectoryExist(client, path, operations.get(client)));
        }
        boolean exists = false;
        for (Boolean value : exist.values()) {
            exists |= value;
        }
        if (!exists) {
            throw new NoSuchDirectoryException("Directory '" + path + "' does not exist.");
        }
        String txpath = xares.getTransactionFolderName() + "/" + path;
        for (DataStorageClient client : clients) {
            if (exist.get(client)) {
                try {
                    if (client.directoryExists(txpath)) {
                        logger.debug("directory " + txpath + " exists for the client " + client);
                        client.deleteDirectory(txpath);
                    }
                    DataStorageWOOperation o = new DataStorageWOOperation(DataStorageWOOperation.Type.DELETE_DIRECTORY,
                            path);
                    operations.get(client).add(o);
                    DSA_LOGGER.info("operation added " + o + " in the scope of transaction's folder: "
                            + xares.getTransactionFolderName() + ";client: " + client);
                } catch (IOException e) {
                    logger.error("There was a problem while deleting a directory: " + txpath, e);
                    throw new DataStorageConnectionException(e);
                }
            }
        }
    }


    @Override
    Collection<FileInfo> listDirectory(String path)
            throws NoSuchDirectoryException, NotDirectoryException, DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToRead(path);
        Map<DataStorageClient, Boolean> exist = new HashMap<DataStorageClient, Boolean>();
        for (DataStorageClient client : clients) {
            exist.put(client, doesDirectoryExist(client, path, operations.get(client)));
        }
        boolean exists = false;
        for (Boolean value : exist.values()) {
            exists |= value;
        }
        if (!exists) {
            throw new NoSuchDirectoryException("Directory '" + path + "' does not exist.");
        }

        String txpath = xares.getTransactionFolderName() + "/" + path;
        Collection<FileInfo> infos = new ArrayList<FileInfo>();
        Collection<File> files = null;
        try {
            logger.debug("listing " + txpath + " direcory");
            files = clients.get(0).listDirectory(txpath); // first client is sufficient for the transaction
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
        for (DataStorageClient client : clients) {
            if (exist.get(client)) {
                if (!wasDirectoryRemoved(path, operations.get(client))) {
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
                        for (DataStorageWOOperation o : operations.get(client)) {
                            if ((o.getType().equals(DataStorageWOOperation.Type.DELETE_FILE))
                                    && filepath.toString().equals(o.getPath())) {
                                logger.debug("File " + filepath.toString()
                                        + " was removed earlier in the same transaction");
                                continue fff;
                            }
                        }
                        FileInfo info = new FileInfoBuilder(file).build();
                        info.setPath(path);
                        if (!infos.contains(info)) {
                            infos.add(info);
                        }
                    }
                }
            }
        }
        return infos;
    }


    @Override
    boolean directoryExists(String path)
            throws DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToRead(path);
        Map<DataStorageClient, Boolean> exist = new HashMap<DataStorageClient, Boolean>();
        for (DataStorageClient client : clients) {
            exist.put(client, doesDirectoryExist(client, path, operations.get(client)));
        }
        boolean exists = false;
        for (Boolean value : exist.values()) {
            exists |= value;
        }
        return exists;
    }


    @Override
    void putFile(String path, String localPath)
            throws NameSyntaxException, NameAlreadyExistsException, NoSuchFileException, DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToCreate(path, clients.subList(0, redundancy));
        for (DataStorageClient client : clients) {
            if (doesFileExist(client, path, operations.get(client))) {
                throw new NameAlreadyExistsException("File '" + path + "' already exists.");
            }
        }
        int i = 0;
        for (DataStorageClient client : clients) {
            reflectFoldersStructure(client, FilenameUtils.getParentFolder(path), operations.get(client));
            String txpath = xares.getTransactionFolderName() + "/" + path;
            try {
                client.putFile(txpath, localPath);
                DataStorageWOOperation o = new DataStorageWOOperation(DataStorageWOOperation.Type.PUT_FILE, path);
                operations.get(client).add(o);
                DSA_LOGGER.info("operation added " + o + " in the scope of transaction's folder: "
                        + xares.getTransactionFolderName() + ";client: " + client);
            } catch (IOException e) {
                logger.error("There was a problem while putting a file: " + txpath, e);
                if (e instanceof FileNotFoundException) {
                    throw new NoSuchFileException(e);
                }
                throw new DataStorageConnectionException(e);
            }

            i++;
            if (i == redundancy) {
                break;
            }
        }
    }


    @Override
    void putFile(String path, InputStream src)
            throws NameSyntaxException, NameAlreadyExistsException, DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToCreate(path, clients.subList(0, redundancy));
        for (DataStorageClient client : clients) {
            if (doesFileExist(client, path, operations.get(client))) {
                throw new NameAlreadyExistsException("File '" + path + "' already exists.");
            }
        }
        int i = 0;
        for (DataStorageClient client : clients) {
            reflectFoldersStructure(client, FilenameUtils.getParentFolder(path), operations.get(client));
            String txpath = xares.getTransactionFolderName() + "/" + path;
            try {
                client.putFile(txpath, src);
                DataStorageWOOperation o = new DataStorageWOOperation(DataStorageWOOperation.Type.PUT_FILE, path);
                operations.get(client).add(o);
                DSA_LOGGER.info("operation added " + o + " in the scope of transaction's folder: "
                        + xares.getTransactionFolderName() + ";client: " + client);
            } catch (IOException e) {
                logger.error("There was a problem while putting a file: " + txpath, e);
                if (e instanceof FileNotFoundException) {
                    throw new NoSuchFileException(e);
                }
                throw new DataStorageConnectionException(e);
            }
            i++;
            if (i == redundancy) {
                break;
            }
        }
    }


    @Override
    void deleteFile(String path)
            throws NoSuchFileException, NotFileException, DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToDelete(path);
        Map<DataStorageClient, Boolean> exist = new HashMap<DataStorageClient, Boolean>();
        for (DataStorageClient client : clients) {
            exist.put(client, doesFileExist(client, path, operations.get(client)));
        }
        boolean exists = false;
        for (Boolean value : exist.values()) {
            exists |= value;
        }
        if (!exists) {
            throw new NoSuchFileException("File '" + path + "' does not exist.");
        }
        String txpath = xares.getTransactionFolderName() + "/" + path;
        for (DataStorageClient client : clients) {
            if (exist.get(client)) {
                try {
                    if (client.fileExists(txpath)) {
                        logger.debug("file " + txpath + " exists for the client " + client);
                        client.deleteFile(txpath);
                    }
                    DataStorageWOOperation o = new DataStorageWOOperation(DataStorageWOOperation.Type.DELETE_FILE, path);
                    operations.get(client).add(o);
                    DSA_LOGGER.info("operation added " + o + " in the scope of transaction's folder: "
                            + xares.getTransactionFolderName() + ";client:" + client);
                } catch (IOException e) {
                    logger.error("There was a problem while deleting a file:" + txpath, e);
                    throw new DataStorageConnectionException(e);
                }
            }
        }
    }


    @Override
    void getFile(String path, String localPath)
            throws NoSuchFileException, NotFileException, NameSyntaxException, DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToRead(path);
        Map<DataStorageClient, Boolean> exist = new HashMap<DataStorageClient, Boolean>();
        for (DataStorageClient client : clients) {
            exist.put(client, doesFileExist(client, path, operations.get(client)));
        }
        boolean exists = false;
        for (Boolean value : exist.values()) {
            exists |= value;
        }
        if (!exists) {
            throw new NoSuchFileException("File '" + path + "' does not exist.");
        }
        String txpath = xares.getTransactionFolderName() + "/" + path;
        for (DataStorageClient client : clients) {
            if (exist.get(client)) {
                try {
                    if (client.fileExists(txpath)) {
                        client.getFile(txpath, localPath);
                    } else {
                        client.getFile(path, localPath);
                    }
                    break;
                } catch (IOException e) {
                    logger.error("There was a problem while reading a file.", e);
                    if (e.getCause() != null && e.getCause() instanceof NameSyntaxException) {
                        throw (NameSyntaxException) e.getCause();
                    }
                    throw new DataStorageConnectionException(e);
                }
            }
        }
    }


    @Override
    InputStream getFile(String path)
            throws NoSuchFileException, NotFileException, DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToRead(path);
        Map<DataStorageClient, Boolean> exist = new HashMap<DataStorageClient, Boolean>();
        for (DataStorageClient client : clients) {
            exist.put(client, doesFileExist(client, path, operations.get(client)));
        }
        boolean exists = false;
        for (Boolean value : exist.values()) {
            exists |= value;
        }
        if (!exists) {
            throw new NoSuchFileException("File '" + path + "' does not exist.");
        }
        String txpath = xares.getTransactionFolderName() + "/" + path;
        for (DataStorageClient client : clients) {
            if (exist.get(client)) {
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
        }
        throw new DataStorageConnectionException("File exists but cannot be retrived (!)");
    }


    @Override
    boolean fileExists(String path)
            throws DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToRead(path);
        Map<DataStorageClient, Boolean> exist = new HashMap<DataStorageClient, Boolean>();
        for (DataStorageClient client : clients) {
            exist.put(client, doesFileExist(client, path, operations.get(client)));
        }
        boolean exists = false;
        for (Boolean value : exist.values()) {
            exists |= value;
        }
        return exists;
    }


    @Override
    FileInfo getFileInfo(String path)
            throws DataStorageConnectionException {
        DataStorageLockManager.getInstance().lockToRead(path);

        Map<DataStorageClient, Boolean> exist = new HashMap<DataStorageClient, Boolean>();
        for (DataStorageClient client : clients) {
            exist.put(client, doesFileExist(client, path, operations.get(client)));
        }
        boolean exists = false;
        for (Boolean value : exist.values()) {
            exists |= value;
        }
        File file = null;
        for (DataStorageClient client : clients) {
            if (exist.get(client)) {
                String txpath = xares.getTransactionFolderName() + "/" + path;
                try {
                    if (client.exists(txpath)) {
                        logger.debug("file or directory" + path + " exists in the transaction folder for the client "
                                + client);
                        file = client.getFileInfo(txpath);
                    } else {
                        logger.debug("file or directory " + path + " exists in the root folder for the client "
                                + client);
                        file = client.getFileInfo(path);
                    }
                } catch (IOException e) {
                    logger.error("There was a problem while getting info about a file or a directory.", e);
                    throw new DataStorageConnectionException(e);
                }
                FileInfo info = new FileInfoBuilder(file).build();
                info.setPath(path);
                return info;
            }
        }
        return null;
    }

}
