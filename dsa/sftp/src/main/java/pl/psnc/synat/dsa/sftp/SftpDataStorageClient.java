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
package pl.psnc.synat.dsa.sftp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.DataStorageClient;
import pl.psnc.synat.dsa.exception.DataStorageResourceException;
import pl.psnc.synat.dsa.exception.NameSyntaxException;
import pl.psnc.synat.dsa.sftp.config.Credential;
import pl.psnc.synat.dsa.sftp.util.ConfigUtil;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

/**
 * Client for the SFTP protocol.
 * 
 */
public class SftpDataStorageClient implements DataStorageClient {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SftpDataStorageClient.class);

    /**
     * Host of data storage.
     */
    private final String host;

    /**
     * Port of data storage.
     */
    private final Integer port;

    /**
     * Line of known hosts file with the public key of data storage.
     */
    private final String knownHost;

    /**
     * Credential to data storage.
     */
    private final Credential credential;

    /**
     * Jsch instance.
     */
    private final JSch jsch;

    /**
     * Open session to SFTP server.
     */
    private Session session;

    /**
     * SFTP channel to send commands.
     */
    private ChannelSftp channel;


    /**
     * Creates a new SFTP client.
     * 
     * @param host
     *            host of data storage
     * @param port
     *            port of data storage
     * @param publicKeyType
     *            public key type of data storage
     * @param publicKey
     *            public key of data storage
     * @param credential
     *            credential to data storage
     * @throws IOException
     *             when configuration is wrong
     * @throws DataStorageResourceException
     *             when decryption failed
     */
    public SftpDataStorageClient(String host, Integer port, String publicKeyType, String publicKey,
            Credential credential)
            throws IOException, DataStorageResourceException {
        this.host = host;
        this.port = port;
        this.knownHost = host + " " + publicKeyType + " " + publicKey;
        this.credential = credential;
        this.jsch = new JSch();
        try {
            this.jsch.setKnownHosts(new ByteArrayInputStream(knownHost.getBytes("UTF-8")));
            jsch.addIdentity(credential.getUsername(), ConfigUtil.decrypt(credential.getPrivateKey()),
                ConfigUtil.decrypt(credential.getPublicKey()), null);
        } catch (JSchException e) {
            logger.error("Configuring sftp client failed!", e);
            throw new DataStorageResourceException("Configuring sftp client failed!");
        }
    }


    @Override
    public void connect()
            throws IOException {
        try {
            session = jsch.getSession(credential.getUsername(), host, port);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "yes");
            session.setConfig(config);
            session.connect();
        } catch (JSchException e) {
            logger.error("Connecting to host " + host + " failed!", e);
            throw new IOException("Connecting to host failed!");
        }
        try {
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            logger.error("Opening SFTP channel failed!", e);
            throw new IOException("Opening SFTP channel failed!");
        }
    }


    @Override
    public void updateFreeSpace()
            throws IOException {
    }


    @Override
    public long getFreeSpace() {
        return 0L;
    }


    @Override
    public void disconnect() {
        channel.quit();
        session.disconnect();
        session = null;
    }


    @Override
    public boolean directoryExists(String path)
            throws IOException {
        try {
            channel.ls(path);
        } catch (SftpException e) {
            if (e.id == 2) {
                return false; // no such file
            }
            logger.error("checking if the directory exists failed", e);
            throw new IOException(e);
        }
        return true;
    }


    @Override
    public void createDirectory(String path)
            throws IOException {
        try {
            channel.mkdir(path);
        } catch (SftpException e) {
            logger.error("creation of the directory failed", e);
            throw new IOException(e);
        }
    }


    @Override
    public void deleteDirectory(String path)
            throws IOException {
        try {
            traverse(channel, path);
        } catch (SftpException e) {
            logger.error("deletion of the directory failed.", e);
            throw new IOException(e);
        }
    }


    /**
     * Delete directory and its content recursively.
     * 
     * @param channel
     *            open channel to SFTP server
     * @param path
     *            path of the directory
     * @throws SftpException
     *             when something went wrong
     */
    @SuppressWarnings("unchecked")
    private void traverse(ChannelSftp channel, String path)
            throws SftpException {
        SftpATTRS attrs = channel.stat(path);
        if (attrs.isDir()) {
            Vector<LsEntry> files = channel.ls(path);
            if (files != null && files.size() > 0) {
                Iterator<LsEntry> it = files.iterator();
                while (it.hasNext()) {
                    LsEntry entry = it.next();
                    if ((!entry.getFilename().equals(".")) && (!entry.getFilename().equals(".."))) {
                        traverse(channel, path + "/" + entry.getFilename());
                    }
                }
            }
            channel.rmdir(path);
        } else {
            channel.rm(path);
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public Collection<File> listDirectory(String path)
            throws IOException {
        try {
            Vector<LsEntry> files = channel.ls(path);
            List<File> result = new ArrayList<File>();
            if (files != null && files.size() > 0) {
                Iterator<LsEntry> it = files.iterator();
                while (it.hasNext()) {
                    LsEntry entry = it.next();
                    result.add(new File(path + "/" + entry.getFilename()));
                }
            }
            return result;
        } catch (SftpException e) {
            if (e.id == 2) {
                return Collections.emptyList();
            }
            logger.error("listing the directory failed.", e);
            throw new IOException(e);
        }
    }


    @Override
    public boolean fileExists(String path)
            throws IOException {
        try {
            channel.stat(path);
        } catch (SftpException e) {
            if (e.id == 2) {
                return false; // no such file
            }
            logger.error("checking if the file exists failed", e);
            throw new IOException(e);
        }
        return true;
    }


    @Override
    public void putFile(String path, String localPath)
            throws IOException {
        try {
            channel.put(localPath, path);
        } catch (SftpException e) {
            logger.error("putting the file failed.", e);
            throw new IOException(e);
        }
    }


    @Override
    public void putFile(String dstPath, InputStream src)
            throws IOException {
        try {
            channel.put(src, dstPath);
        } catch (SftpException e) {
            logger.error("putting the file failed.", e);
            throw new IOException(e);
        }
    }


    @Override
    public void moveFile(String oldpath, String newpath)
            throws IOException {
        try {
            channel.rename(oldpath, newpath);
        } catch (SftpException e) {
            logger.error("moving the file failed", e);
            throw new IOException(e);
        }
    }


    @Override
    public void deleteFile(String path)
            throws IOException {
        try {
            channel.rm(path);
        } catch (SftpException e) {
            logger.error("deletion of the file failed", e);
            throw new IOException(e);
        }
    }


    @Override
    public File getFileInfo(String path)
            throws IOException {
        try {
            channel.stat(path);
            return new File(path);
        } catch (SftpException e) {
            if (e.id == 2) {
                return null; // no such directory
            }
            logger.error("getting file info failed.", e);
            throw new IOException(e);
        }
    }


    @Override
    public void getFile(String path, String localPath)
            throws IOException {
        File localFile = new File(localPath);
        if (!localFile.getParentFile().exists()) {
            if (!localFile.getParentFile().mkdirs()) {
                throw new IOException(new NameSyntaxException("Directories on the path " + localPath
                        + " cannot be created."));
            }
        }
        try {
            channel.get(path, localPath);
        } catch (SftpException e) {
            logger.error("getting the file failed.", e);
            throw new IOException(e);
        }
    }


    @Override
    public InputStream getFile(String path)
            throws IOException {
        try {
            return channel.get(path);
        } catch (SftpException e) {
            logger.error("getting the file failed.", e);
            throw new IOException(e);
        }
    }


    @Override
    public boolean exists(String path) {
        try {
            channel.stat(path);
        } catch (SftpException e) {
            if (e.id == 2) {
                return false; // no such file
            }
            logger.error("checking if the file or directory exists failed", e);
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("FSDataStorageClient ");
        sb.append("[hots = ").append(host);
        sb.append("]");
        return sb.toString();
    }

}
