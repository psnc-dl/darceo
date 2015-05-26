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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.transaction.xa.XAResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.exception.DataStorageConnectionException;
import pl.psnc.synat.dsa.exception.NameAlreadyExistsException;
import pl.psnc.synat.dsa.exception.NameSyntaxException;
import pl.psnc.synat.dsa.exception.NoSuchDirectoryException;
import pl.psnc.synat.dsa.exception.NoSuchFileException;
import pl.psnc.synat.dsa.exception.NotDirectoryException;
import pl.psnc.synat.dsa.exception.NotFileException;
import pl.psnc.synat.dsa.util.FilenameUtils;

/**
 * Abstract class for all managed connection to data storage.
 * 
 */
public abstract class DataStorageManagedConnection implements ManagedConnection {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DataStorageManagedConnection.class);

    /** DSA Logger. */
    private static final Logger DSA_LOGGER = LoggerFactory.getLogger("DSA");

    /**
     * Connection event sender.
     */
    private DataStorageConnectionEventSender sender;

    /**
     * The only one connection handle associated with this managed connection.
     */
    protected AbstractDataStorageConnection connection;

    /**
     * XA resource that manage distributed transaction in which this managed connection is involved in.
     */
    protected DataStorageXAResource xares;


    /**
     * Constructor.
     */
    public DataStorageManagedConnection() {
        sender = new DataStorageConnectionEventSender(this);
    }


    @Override
    public void associateConnection(Object connection)
            throws ResourceException {
        logger.warn("associate a connection " + connection);
    }


    @Override
    public void cleanup()
            throws ResourceException {
        logger.debug("cleaning");
        if (connection != null) {
            connection.invalidate();
        }
        connection = null;
    }


    @Override
    public void destroy()
            throws ResourceException {
        logger.debug("destroying");
    }


    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        logger.debug("adding a connection event listener: " + listener);
        sender.addConnectorListener(listener);
    }


    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        logger.debug("removing a connection event listener: " + listener);
        sender.removeConnectorListener(listener);
    }


    /**
     * Releases connection. Normal behavior.
     */
    public void release() {
        logger.debug("releasing");
        sender.sendEvent(ConnectionEvent.CONNECTION_CLOSED, null, connection);
    }


    /**
     * Releases connection. Some error occurred.
     * 
     * @param ex
     *            exception associated with the error
     */
    public void release(Exception ex) {
        logger.debug("releasing with error: " + ex);
        sender.sendEvent(ConnectionEvent.CONNECTION_ERROR_OCCURRED, ex, connection);
    }


    @Override
    public LocalTransaction getLocalTransaction()
            throws ResourceException {
        throw new UnsupportedOperationException("Local transactions unsupported.");
    }


    @Override
    public XAResource getXAResource()
            throws ResourceException {
        logger.debug("getting a XA resource");
        return xares;
    }


    @Override
    public PrintWriter getLogWriter()
            throws ResourceException {
        return null;
    }


    @Override
    public void setLogWriter(PrintWriter out)
            throws ResourceException {
    }


    /**
     * Reflects structure of folders into a separate folder for transaction scope on a data storage specified by a
     * client. Adds new folders to the list of write operations.
     * 
     * @param client
     *            client connected to a dtata storage
     * @param path
     *            path to the directory which has to be reflected
     * @param operations
     *            write operations done in the transaction
     * @throws NameSyntaxException
     *             if the specified name for the directory is incorrect
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    protected void reflectFoldersStructure(DataStorageClient client, String path,
            List<DataStorageWOOperation> operations)
            throws NameSyntaxException, DataStorageConnectionException {
        try {
            if (!client.directoryExists(xares.getTransactionFolderName())) {
                client.createDirectory(xares.getTransactionFolderName());
            }
        } catch (IOException e) {
            logger.error("There was a problem while creating a directory: " + xares.getTransactionFolderName(), e);
            throw new DataStorageConnectionException(e);
        }
        StringBuffer crtpath = new StringBuffer();
        StringBuffer ctxpath = new StringBuffer(xares.getTransactionFolderName()).append("/");
        List<String> subfolders = FilenameUtils.splitPath(path);
        for (String folder : subfolders) {
            crtpath.append(folder);
            ctxpath.append(folder);
            try {
                if (client.directoryExists(crtpath.toString())) {
                    logger.debug("directory " + crtpath.toString() + " exists");
                    if (client.directoryExists(ctxpath.toString())) {
                        logger.debug("directory " + ctxpath.toString() + " exists");
                    } else {
                        logger.debug("directory " + ctxpath.toString() + " does not exist");
                        client.createDirectory(ctxpath.toString());
                        if (wasDirectoryRemoved(crtpath.toString(), operations)) {
                            logger.info("directory " + ctxpath.toString() + " was created as a new directory "
                                    + crtpath.toString());
                            DataStorageWOOperation o = new DataStorageWOOperation(
                                    DataStorageWOOperation.Type.CREATE_DIRECTORY, crtpath.toString());
                            operations.add(o);
                            DSA_LOGGER.info("operation added " + o + " in the scope of transaction's folder: "
                                    + xares.getTransactionFolderName() + ";client: " + client);
                        } else {
                            logger.info("directory " + ctxpath.toString() + " was created as reflection of "
                                    + crtpath.toString());
                        }
                    }
                } else {
                    logger.debug("directory " + crtpath.toString() + " does not exist");
                    if (client.directoryExists(ctxpath.toString())) {
                        logger.debug("directory " + ctxpath.toString() + " exists");
                    } else {
                        logger.debug("directory " + ctxpath.toString() + " does not exist");
                        client.createDirectory(ctxpath.toString());
                        logger.info("directory " + ctxpath.toString() + " was created as a new directory "
                                + crtpath.toString());
                        DataStorageWOOperation o = new DataStorageWOOperation(
                                DataStorageWOOperation.Type.CREATE_DIRECTORY, crtpath.toString());
                        operations.add(o);
                        DSA_LOGGER.info("operation added " + o + " in the scope of transaction's folder: "
                                + xares.getTransactionFolderName() + ";client: " + client);
                    }
                }
            } catch (IOException e) {
                logger.error("There was a problem while creating a directory.", e);
                throw new DataStorageConnectionException(e);
            }
            crtpath.append("/");
            ctxpath.append("/");
        }
    }


    /**
     * Creates a new directory. In the scope (folder) of the transaction.
     * 
     * @param path
     *            path to the directory being created
     * @throws NameSyntaxException
     *             if the specified name for a new directory is incorrect
     * @throws NameAlreadyExistsException
     *             if the directory already exists
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    abstract void createDirectory(String path)
            throws NameSyntaxException, NameAlreadyExistsException, DataStorageConnectionException;


    /**
     * Deletes a directory (with the whole content recursively). In the scope (folder) of the transaction.
     * 
     * @param path
     *            path to the directory being deleted
     * @throws NoSuchDirectoryException
     *             if the directory does not exist or the parent directory does not exist
     * @throws NotDirectoryException
     *             if the specified name points to a file
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    abstract void deleteDirectory(String path)
            throws NoSuchDirectoryException, NotDirectoryException, DataStorageConnectionException;


    /**
     * Lists a directory (in the scope of the transaction).
     * 
     * @param path
     *            path to the directory being listed
     * @return content of the directory
     * @throws NoSuchDirectoryException
     *             if the directory does not exist or the parent directory does not exist
     * @throws NotDirectoryException
     *             if the specified name points to a file
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    abstract Collection<FileInfo> listDirectory(String path)
            throws NoSuchDirectoryException, NotDirectoryException, DataStorageConnectionException;


    /**
     * Checks whether the specified path points to a directory (in the scope of the transaction).
     * 
     * @param path
     *            path to the directory being checked
     * @return if the specified path points to a directory
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    abstract boolean directoryExists(String path)
            throws DataStorageConnectionException;


    /**
     * Creates a file (in the scope of the transaction). Put it from a file in a local file system.
     * 
     * @param path
     *            path to the file being created
     * @param localPath
     *            absolute path of the source file (in a local file system)
     * @throws NameSyntaxException
     *             if the specified name for a new file is incorrect
     * @throws NameAlreadyExistsException
     *             if the file already exists
     * @throws NoSuchFileException
     *             if the source file does not exist
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    abstract void putFile(String path, String localPath)
            throws NameSyntaxException, NameAlreadyExistsException, NoSuchFileException, DataStorageConnectionException;


    /**
     * Creates a file (in the scope of the transaction). Put it from an input stream.
     * 
     * @param path
     *            path to the file being created
     * @param src
     *            input stream of the source file
     * @throws NameSyntaxException
     *             if the specified name for a new file is incorrect
     * @throws NameAlreadyExistsException
     *             if the file already exists
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    abstract void putFile(String path, InputStream src)
            throws NameSyntaxException, NameAlreadyExistsException, DataStorageConnectionException;


    /**
     * Deletes a file (in the scope of the transaction).
     * 
     * @param path
     *            path to the file being deleted
     * @throws NoSuchFileException
     *             if the file does not exist
     * @throws NotFileException
     *             if the specified name points to a directory
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    abstract void deleteFile(String path)
            throws NoSuchFileException, NotFileException, DataStorageConnectionException;


    /**
     * Retrieves a file (in the scope of the transaction). Saves this file in a local file system.
     * 
     * @param path
     *            path to file being retrieved
     * @param localPath
     *            absolute path of the destination file (in a local file system)
     * @throws NoSuchFileException
     *             if the file does not exist
     * @throws NotFileException
     *             if the specified name points to a directory
     * @throws NameSyntaxException
     *             if the specified name for a destination file is incorrect
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    abstract void getFile(String path, String localPath)
            throws NoSuchFileException, NotFileException, NameSyntaxException, DataStorageConnectionException;


    /**
     * Retrieves a file (in the scope of the transaction) as an input stream.
     * 
     * @param path
     *            path to file being retrieved
     * @return input stream of file
     * @throws NoSuchFileException
     *             if the file does not exist
     * @throws NotFileException
     *             if the specified name points to a directory
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    abstract InputStream getFile(String path)
            throws NoSuchFileException, NotFileException, DataStorageConnectionException;


    /**
     * Checks whether the specified path points to a file (in the scope of the transaction).
     * 
     * @param path
     *            path to the file being checked
     * @return if the specified name points to a file
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    abstract boolean fileExists(String path)
            throws DataStorageConnectionException;


    /**
     * Retrieves information about a file or a directory (in the scope of the transaction).
     * 
     * @param path
     *            path to the file or directory
     * @return information about the file or directory or null (if it does not exist)
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    abstract FileInfo getFileInfo(String path)
            throws DataStorageConnectionException;


    /**
     * Checks whether the directory was removed earlier in the same transaction.
     * 
     * @param path
     *            path to the directory
     * @param operations
     *            write operations done in the transaction
     * @return whether it was removed
     */
    protected boolean wasDirectoryRemoved(String path, List<DataStorageWOOperation> operations) {
        for (DataStorageWOOperation o : operations) {
            if ((o.getType().equals(DataStorageWOOperation.Type.DELETE_DIRECTORY)) && path.startsWith(o.getPath())) {
                logger.debug("directory " + path + " was earlier removed in this transaction");
                return true;
            }
        }
        return false;
    }


    /**
     * Checks whether the file (or a directory) was removed earlier in the same transaction.
     * 
     * @param path
     *            path to the file
     * @param operations
     *            write operations done in the transaction
     * @return whether it was removed
     */
    protected boolean wasFileOrDirectoryRemoved(String path, List<DataStorageWOOperation> operations) {
        for (DataStorageWOOperation o : operations) {
            if ((o.getType().equals(DataStorageWOOperation.Type.DELETE_DIRECTORY)) && path.startsWith(o.getPath())) {
                logger.debug("directory " + org.apache.commons.io.FilenameUtils.getPath(path)
                        + " was removed earlier in the same transaction");
                return true;
            }
            if ((o.getType().equals(DataStorageWOOperation.Type.DELETE_FILE)) && path.equals(o.getPath())) {
                logger.debug("file " + path + " was removed earlier in the same transaction");
                return true;
            }
        }
        return false;
    }


    /**
     * Checks whether the directory exists on a data storage connected by the specified client. This checking is done in
     * the scope of the transaction, i.e. possible deletions are handle as well.
     * 
     * @param client
     *            client connected to a data storage
     * @param path
     *            path to the directory
     * @param operations
     *            delete operations done by the client
     * @return whether it exists
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    protected boolean doesDirectoryExist(DataStorageClient client, String path, List<DataStorageWOOperation> operations)
            throws DataStorageConnectionException {
        boolean removed = wasDirectoryRemoved(path, operations);
        try {
            if (!removed && client.directoryExists(path)) {
                logger.debug("directory " + path + " exists in the root folder");
                return true;
            }
            String txpath = xares.getTransactionFolderName() + "/" + path;
            if (client.directoryExists(txpath)) {
                logger.debug("directory " + path + " exists in the transaction's folder: "
                        + xares.getTransactionFolderName());
                return true;
            }
        } catch (IOException e) {
            logger.error("There was a problem while getting info about a directory: " + path, e);
            throw new DataStorageConnectionException(e);
        }
        logger.debug("directory " + path + " does not exist");
        return false;
    }


    /**
     * Checks whether the file exists (in the scope of the transaction). This checking is done in the scope of the
     * transaction, i.e. possible deletions are handle as well.
     * 
     * @param client
     *            client connected to a data storage
     * @param path
     *            path to the file
     * @param operations
     *            delete operations done by the client
     * @return whether it exists
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    protected boolean doesFileExist(DataStorageClient client, String path, List<DataStorageWOOperation> operations)
            throws DataStorageConnectionException {
        boolean removed = wasFileOrDirectoryRemoved(path, operations);
        try {
            if (!removed && client.fileExists(path)) {
                logger.debug("file " + path + " exists in the root folder");
                return true;
            }
            String txpath = xares.getTransactionFolderName() + "/" + path;
            if (client.fileExists(txpath)) {
                logger.debug("file " + path + " exists in the transaction's folder: "
                        + xares.getTransactionFolderName());
                return true;
            }
        } catch (IOException e) {
            logger.error("There was a problem while getting info about a file: " + path, e);
            throw new DataStorageConnectionException(e);
        }
        logger.debug("file " + path + " does not exist");
        return false;
    }

}
