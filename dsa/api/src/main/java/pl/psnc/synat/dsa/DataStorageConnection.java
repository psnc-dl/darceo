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

import pl.psnc.synat.dsa.exception.DataStorageConnectionException;
import pl.psnc.synat.dsa.exception.NameAlreadyExistsException;
import pl.psnc.synat.dsa.exception.NameSyntaxException;
import pl.psnc.synat.dsa.exception.NoSuchDirectoryException;
import pl.psnc.synat.dsa.exception.NoSuchFileException;
import pl.psnc.synat.dsa.exception.NotDirectoryException;
import pl.psnc.synat.dsa.exception.NotFileException;

/**
 * Common interface to data storages.
 *
 */
public interface DataStorageConnection {

    /**
     * Creates a directory at the specified name and all the missing above-directories.
     *
     * @param path
     *            absolute path to the directory being created
     * @throws NameSyntaxException
     *             if the specified name for a new directory is incorrect
     * @throws NameAlreadyExistsException
     *             if the directory already exists
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    void createDirectory(String path)
            throws NameSyntaxException, NameAlreadyExistsException, DataStorageConnectionException;


    /**
     * Deletes a directory at the specified name (with the whole content recursively).
     *
     * @param path
     *            absolute path to the directory being deleted
     * @throws NoSuchDirectoryException
     *             if the directory does not exist
     * @throws NotDirectoryException
     *             if the specified name points to a file
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    void deleteDirectory(String path)
            throws NoSuchDirectoryException, NotDirectoryException, DataStorageConnectionException;


    /**
     * Retrieves information about the contents of a directory at the specified name.
     *
     * @param path
     *            absolute path to the directory being listed
     * @return content of the directory
     * @throws NoSuchDirectoryException
     *             if the directory does not exist
     * @throws NotDirectoryException
     *             if the specified name points to a file
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    Collection<FileInfo> listDirectory(String path)
            throws NoSuchDirectoryException, NotDirectoryException, DataStorageConnectionException;


    /**
     * Checks whether the specified directory exists.
     *
     * @param path
     *            absolute path to the directory being checked
     * @return if the specified name points to a directory
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    boolean directoryExists(String path)
            throws DataStorageConnectionException;


    /**
     * Creates a file at the specified name with a data from the specified file from a local file system. It creates
     * also all the missing above-directories.
     *
     * @param path
     *            absolute path to the file being created
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
    void putFile(String path, String localPath)
            throws NameSyntaxException, NameAlreadyExistsException, NoSuchFileException, DataStorageConnectionException;


    /**
     * Creates a file at the specified name with a data from the input stream. It creates
     * also all the missing above-directories.
     *
     * @param path
     *            absolute path to the file being created
     * @param src
     *            input stream of the source file
     * @throws NameSyntaxException
     *             if the specified name for a new file is incorrect
     * @throws NameAlreadyExistsException
     *             if the file already exists
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    void putFile(String path, InputStream src)
            throws NameSyntaxException, NameAlreadyExistsException, DataStorageConnectionException;


    /**
     * Deletes a file at the specified name.
     *
     * @param path
     *            absolute path to the file being deleted
     * @throws NoSuchFileException
     *             if the file does not exist
     * @throws NotFileException
     *             if the specified name points to a directory
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    void deleteFile(String path)
            throws NoSuchFileException, NotFileException, DataStorageConnectionException;


    /**
     * Retrieves a file at the specified name and saves it in a local file system.
     *
     * @param path
     *            absolute path to the file being retrived
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
    void getFile(String path, String localPath)
            throws NoSuchFileException, NotFileException, NameSyntaxException, DataStorageConnectionException;


    /**
     * Retrieves a file at the specified name and returns input stream.
     *
     * @param path
     *            absolute path to the file being retrived
     * @return input stream of file
     * @throws NoSuchFileException
     *             if the file does not exist
     * @throws NotFileException
     *             if the specified name points to a directory
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    InputStream getFile(String path)
            throws NoSuchFileException, NotFileException, DataStorageConnectionException;


    /**
     * Checks whether the specified file exists.
     *
     * @param path
     *            absolute path to the file being checked
     * @return if the specified name points to a file
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    boolean fileExists(String path)
            throws DataStorageConnectionException;


    /**
     * Retrieves information about a file (or a directory) at the specified name. Method returns null, if no file system
     * object at that name exists (a file or a directory).
     *
     * @param path
     *            absolute path to the file or directory
     * @return information about the file (directory) or null
     * @throws DataStorageConnectionException
     *             if some unexpected error occurs
     */
    FileInfo getFileInfo(String path)
            throws DataStorageConnectionException;


    /**
     * Release connection to the pool.
     */
    void close();

}
