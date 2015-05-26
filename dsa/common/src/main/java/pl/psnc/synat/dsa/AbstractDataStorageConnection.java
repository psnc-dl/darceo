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

import java.io.InputStream;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.exception.DataStorageConnectionException;
import pl.psnc.synat.dsa.exception.NameAlreadyExistsException;
import pl.psnc.synat.dsa.exception.NameSyntaxException;
import pl.psnc.synat.dsa.exception.NoSuchDirectoryException;
import pl.psnc.synat.dsa.exception.NoSuchFileException;
import pl.psnc.synat.dsa.exception.NotDirectoryException;
import pl.psnc.synat.dsa.exception.NotFileException;

/**
 * Connection to data storage.
 *
 */
public abstract class AbstractDataStorageConnection implements DataStorageConnection {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(AbstractDataStorageConnection.class);

    /**
     * Managed connection associated with this connection.
     */
    protected DataStorageManagedConnection managedConnection;


    /**
     * Creates a new connection associated with managed connection.
     *
     * @param managedConnection
     *            managed connection
     */
    public AbstractDataStorageConnection(DataStorageManagedConnection managedConnection) {
        this.managedConnection = managedConnection;
    }


    /**
     * Invalidate the connection.
     */
    public void invalidate() {
        managedConnection = null;
    }


    @Override
    public void createDirectory(String path)
            throws NameSyntaxException, NameAlreadyExistsException, DataStorageConnectionException {
        logger.debug("creating directory: " + path);
        if (path.startsWith("/")) {
            managedConnection.createDirectory(path.substring(1));
        } else {
            managedConnection.createDirectory(path);
        }
    }


    @Override
    public void deleteDirectory(String path)
            throws NoSuchDirectoryException, NotDirectoryException, DataStorageConnectionException {
        logger.debug("deleting directory: " + path);
        if (path.startsWith("/")) {
            managedConnection.deleteDirectory(path.substring(1));
        } else {
            managedConnection.deleteDirectory(path);
        }
    }


    @Override
    public Collection<FileInfo> listDirectory(String path)
            throws NoSuchDirectoryException, NotDirectoryException, DataStorageConnectionException {
        logger.debug("listing directory: " + path);
        if (path.startsWith("/")) {
            return managedConnection.listDirectory(path.substring(1));
        } else {
            return managedConnection.listDirectory(path);
        }
    }


    @Override
    public boolean directoryExists(String path)
            throws DataStorageConnectionException {
        logger.debug("checking directory: " + path);
        if (path.startsWith("/")) {
            return managedConnection.directoryExists(path.substring(1));
        } else {
            return managedConnection.directoryExists(path);
        }
    }


    @Override
    public void putFile(String path, String localPath)
            throws NameSyntaxException, NameAlreadyExistsException, NoSuchFileException, DataStorageConnectionException {
        logger.debug("putting file: " + path + " from " + localPath);
        if (path.startsWith("/")) {
            managedConnection.putFile(path.substring(1), localPath);
        } else {
            managedConnection.putFile(path, localPath);
        }
    }


    @Override
    public void putFile(String path, InputStream src) throws NameSyntaxException, NameAlreadyExistsException, DataStorageConnectionException {
        logger.debug("putting file: " + path + " from input stream");
        if (path.startsWith("/")) {
            managedConnection.putFile(path.substring(1), src);
        } else {
            managedConnection.putFile(path, src);
        }
    }


    @Override
    public void deleteFile(String path)
            throws NoSuchFileException, NotFileException, DataStorageConnectionException {
        logger.debug("deleting file: " + path);
        if (path.startsWith("/")) {
            managedConnection.deleteFile(path.substring(1));
        } else {
            managedConnection.deleteFile(path);
        }
    }


    @Override
    public void getFile(String path, String localPath)
            throws NoSuchFileException, NotFileException, NameSyntaxException, DataStorageConnectionException {
        logger.debug("getting file: " + path + " to " + localPath);
        if (path.startsWith("/")) {
            managedConnection.getFile(path.substring(1), localPath);
        } else {
            managedConnection.getFile(path, localPath);
        }
    }


    @Override
    public InputStream getFile(String path) throws NoSuchFileException, NotFileException, DataStorageConnectionException {
        logger.debug("getting file: " + path + " to input stream");
        if (path.startsWith("/")) {
            return managedConnection.getFile(path.substring(1));
        } else {
            return managedConnection.getFile(path);
        }
    }


    @Override
    public boolean fileExists(String path)
            throws DataStorageConnectionException {
        logger.debug("checking file: " + path);
        if (path.startsWith("/")) {
            return managedConnection.fileExists(path.substring(1));
        } else {
            return managedConnection.fileExists(path);
        }
    }


    @Override
    public FileInfo getFileInfo(String path)
            throws DataStorageConnectionException {
        logger.debug("getting file info: " + path);
        if (path.startsWith("/")) {
            return managedConnection.getFileInfo(path.substring(1));
        } else {
            return managedConnection.getFileInfo(path);
        }
    }


    @Override
    public void close() {
        managedConnection.release();
    }

}
