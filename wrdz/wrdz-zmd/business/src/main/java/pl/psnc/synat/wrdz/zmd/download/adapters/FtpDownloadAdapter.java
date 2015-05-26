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
package pl.psnc.synat.wrdz.zmd.download.adapters;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.zmd.download.ConnectionInformation;
import pl.psnc.synat.wrdz.zmd.exception.DownloadAdapterException;

/**
 * An adapter class which provides functionality of handling content downloading via FTP protocol.
 */
public class FtpDownloadAdapter extends AbstractDownloadAdapter implements DownloadAdapter {

    /**
     * Constructs new FTP adapter using provided information.
     * 
     * @param connectionInfo
     *            connection information for SFTP protocol.
     * @param cacheHome
     *            cache root folder.
     */
    public FtpDownloadAdapter(ConnectionInformation connectionInfo, String cacheHome) {
        super(connectionInfo, cacheHome);
    }


    @Override
    public String downloadFile(URI uri, String relativePath)
            throws DownloadAdapterException {
        String cachedFilePath = getResourceCachePath(relativePath);
        checkDestinationExistence(cachedFilePath);
        FTPClient ftpClient = new FTPClient();
        connectToFtp(ftpClient);
        loginToFtp(ftpClient);
        downloadFromFtp(ftpClient, cachedFilePath, uri.getPath());
        return cachedFilePath;
    }


    /**
     * Establishes connection to the FTP server or throws exceptions if any problems occur.
     * 
     * @param ftpClient
     *            client instance to operate on.
     * @throws DownloadAdapterException
     *             should any problems occur.
     */
    private void connectToFtp(FTPClient ftpClient)
            throws DownloadAdapterException {
        try {
            ftpClient.connect(connectionInfo.getHost(), connectionInfo.getPort());
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                throw new DownloadAdapterException(
                        "Unable to establish connection to the server, server refused connection.");
            }
        } catch (IOException e) {
            throw new DownloadAdapterException("Exception while trying to establish connection to the server.", e);
        }
    }


    /**
     * Performs login operation to log to the server or throws exceptions if any problems with logging in occur.
     * 
     * @param ftpClient
     *            client instance to operate on.
     * @throws DownloadAdapterException
     *             should any problems occur.
     */
    private void loginToFtp(FTPClient ftpClient)
            throws DownloadAdapterException {
        try {
            if (ftpClient.login(connectionInfo.getUsername(), connectionInfo.getPassword())) {
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            } else {
                throw new DownloadAdapterException("Unable to login to the server, authentication failed.");
            }
        } catch (IOException e) {
            throw new DownloadAdapterException("Exception while trying to login to the server.", e);
        }
    }


    /**
     * Downloads resource from the FTP server or throws exceptions if any problesmw ith download occur.
     * 
     * @param ftpClient
     *            client instance to operate on.
     * @param cachedFilePath
     *            path to the download destination in cache.
     * @param remotePath
     *            path to the resource on the server.
     * @throws DownloadAdapterException
     *             should any problems occur.
     */
    private void downloadFromFtp(FTPClient ftpClient, String cachedFilePath, String remotePath)
            throws DownloadAdapterException {
        OutputStream output = null;
        try {
            output = new FileOutputStream(cachedFilePath);
            if (!ftpClient.retrieveFile(remotePath, output)) {
                throw new DownloadAdapterException("Unable to download file, ftp server returned response code: "
                        + ftpClient.getReplyCode());
            }
        } catch (FileNotFoundException e) {
            throw new WrdzRuntimeException("Unable to create new file in cache.", e);
        } catch (IOException e) {
            throw new DownloadAdapterException("Exception while downloading the file - Unable to download ", e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    throw new WrdzRuntimeException("Unable to close open output stream.", e);
                }
            }
        }
    }
}
