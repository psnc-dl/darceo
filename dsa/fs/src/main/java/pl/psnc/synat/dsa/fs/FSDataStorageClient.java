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
package pl.psnc.synat.dsa.fs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.FileUtils;

import pl.psnc.synat.dsa.DataStorageClient;
import pl.psnc.synat.dsa.exception.NameSyntaxException;

/**
 * Client for the file system protocol. It can handle one mounted archive.
 * 
 */
public class FSDataStorageClient implements DataStorageClient {

    /** The root folder where the archive is. */
    private final String root;

    /** Free space in bytes in the root folder. */
    private long freeSpace;


    /**
     * Creates a new file system client.
     * 
     * @param root
     *            root folder
     */
    public FSDataStorageClient(String root) {
        this.root = root;
    }


    @Override
    public void connect()
            throws IOException {
        File folder = new File(root);
        if (!folder.isDirectory()) {
            throw new IOException("The root folder " + root + " does not exist!");
        }
        updateFreeSpace();
    }


    @Override
    public void updateFreeSpace() {
        File file = new File(root);
        freeSpace = file.getUsableSpace();
    }


    @Override
    public long getFreeSpace() {
        return freeSpace;
    }


    @Override
    public void disconnect() {
    }


    @Override
    public boolean directoryExists(String path) {
        File folder = new File(root + path);
        return folder.isDirectory();
    }


    @Override
    public void createDirectory(String path)
            throws IOException {
        File folder = new File(root + path);
        if (!folder.mkdir()) {
            throw new IOException("Directory '" + path + "' in '" + root + "' could not be created.");
        }
    }


    @Override
    public void deleteDirectory(String path)
            throws IOException {
        File folder = new File(root + path);
        FileUtils.deleteDirectory(folder);
    }


    @Override
    public Collection<File> listDirectory(String path)
            throws IOException {
        File folder = new File(root + path);
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            return Arrays.asList(files);
        } else {
            return Collections.emptyList();
        }
    }


    @Override
    public boolean fileExists(String path) {
        File file = new File(root + path);
        return file.isFile();
    }


    @Override
    public void putFile(String path, String localPath)
            throws IOException {
        File file = new File(root + path);
        File localFile = new File(localPath);
        FileUtils.copyFile(localFile, file, false);
    }


    @Override
    public void putFile(String dstPath, InputStream src)
            throws IOException {
        File file = new File(root + dstPath);
        FileUtils.copyInputStreamToFile(src, file);
    }


    @Override
    public void moveFile(String oldpath, String newpath)
            throws IOException {
        File oldfile = new File(root + oldpath);
        File newfile = new File(root + newpath);
        if (!oldfile.renameTo(newfile)) {
            throw new IOException("File '" + oldpath + "' could not be renamed as '" + newpath + "' in " + root + "'.");
        }
    }


    @Override
    public void deleteFile(String path)
            throws IOException {
        File file = new File(root + path);
        if (!file.delete()) {
            throw new IOException("File '" + path + "' in " + root + "' could not be deleted.");
        }
    }


    @Override
    public File getFileInfo(String path)
            throws IOException {
        return new File(root + path);
    }


    @Override
    public void getFile(String path, String localPath)
            throws IOException {
        File file = new File(root + path);
        File localFile = new File(localPath);
        if (!localFile.getParentFile().exists()) {
            if (!localFile.getParentFile().mkdirs()) {
                throw new IOException(new NameSyntaxException("Directories on the path " + localPath
                        + " cannot be created."));
            }
        }
        FileUtils.copyFile(file, localFile, false);
    }


    @Override
    public InputStream getFile(String path)
            throws IOException {
        File file = new File(root + path);
        return FileUtils.openInputStream(file);
    }


    @Override
    public boolean exists(String path) {
        File file = new File(root + path);
        return file.exists();
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("FSDataStorageClient ");
        sb.append("[root = ").append(root);
        sb.append("]");
        return sb.toString();
    }

}
