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
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Interface of client to data storage services.
 * 
 */
public interface DataStorageClient {

    /**
     * Connects to a data storage.
     * 
     * @throws IOException
     *             when some error occurs
     */
    void connect()
            throws IOException;


    /**
     * Updates info about free space left in a data storage.
     * 
     * @throws IOException
     *             when some error occurs
     */
    void updateFreeSpace()
            throws IOException;


    /**
     * Returns free space in bytes left in a data storage.
     * 
     * @return free space left
     */
    long getFreeSpace();


    /**
     * Disconnects from a data storage.
     */
    void disconnect();


    /**
     * Checks whether there is a directory at specified path.
     * 
     * @param path
     *            path to the directory
     * @return whether the directory exists
     * @throws IOException
     *             when some error occurs
     */
    boolean directoryExists(String path)
            throws IOException;


    /**
     * Creates a directory. It requires that the parent directory exists.
     * 
     * @param path
     *            path of the directory
     * @throws IOException
     *             when some error occurs
     */
    void createDirectory(String path)
            throws IOException;


    /**
     * Deletes a directory recursively.
     * 
     * @param path
     *            path of the directory
     * @throws IOException
     *             when some error occurs
     */
    void deleteDirectory(String path)
            throws IOException;


    /**
     * Lists a directory.
     * 
     * @param path
     *            path of the directory
     * @return collection of files which belong to the directory
     * @throws IOException
     *             when some error occurs
     */
    Collection<File> listDirectory(String path)
            throws IOException;


    /**
     * Checks whether there is a file at specified path.
     * 
     * @param path
     *            path to the file
     * @return whether the file exists
     * @throws IOException
     *             when some error occurs
     */
    boolean fileExists(String path)
            throws IOException;


    /**
     * Creates a file and fill it with the data from localPath.
     * 
     * @param path
     *            path of the destination file
     * @param localPath
     *            path of the source file
     * @throws IOException
     *             when some error occurs
     */
    void putFile(String path, String localPath)
            throws IOException;


    /**
     * Creates a file and fill it with the data from input stream.
     * 
     * @param dstPath
     *            path of the destination file
     * @param src
     *            input stream of the source file
     * @throws IOException
     *             when some error occurs
     */
    void putFile(String dstPath, InputStream src)
            throws IOException;


    /**
     * Moves (and renames) a file.
     * 
     * @param oldpath
     *            path to the existing file
     * @param newpath
     *            new path of the file
     * @throws IOException
     *             when some error occurs
     */
    void moveFile(String oldpath, String newpath)
            throws IOException;


    /**
     * Deletes a file.
     * 
     * @param path
     *            path to the file
     * @throws IOException
     *             when some error occurs
     */
    void deleteFile(String path)
            throws IOException;


    /**
     * Retrieves an information about a file.
     * 
     * @param path
     *            path to the file
     * @return info about file
     * @throws IOException
     *             when some error occurs
     */
    File getFileInfo(String path)
            throws IOException;


    /**
     * Creates a file at localPath and fill it with the data from path.
     * 
     * @param path
     *            path of the source file
     * @param localPath
     *            path of the destination file
     * @throws IOException
     *             when some error occurs
     */
    void getFile(String path, String localPath)
            throws IOException;


    /**
     * Returns input stream with the data from path.
     * 
     * @param path
     *            path of the source file
     * @return input stream of file
     * @throws IOException
     *             when some error occurs
     */
    InputStream getFile(String path)
            throws IOException;


    /**
     * Checks whether there is a file or a directory at specified path.
     * 
     * @param path
     *            path to the file or directory
     * @return whether the file or directory exists - in case or error it returns false
     */
    boolean exists(String path);

}
