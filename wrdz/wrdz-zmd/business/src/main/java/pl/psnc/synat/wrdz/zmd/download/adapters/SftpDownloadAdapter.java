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

import java.net.URI;

import pl.psnc.synat.wrdz.zmd.download.CertificateInformation;
import pl.psnc.synat.wrdz.zmd.download.ConnectionInformation;
import pl.psnc.synat.wrdz.zmd.download.SftpConnectionInformation;
import pl.psnc.synat.wrdz.zmd.exception.DownloadAdapterException;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * An adapter class which provides functionality of handling content downloading via SFTP protocol.
 */
public class SftpDownloadAdapter extends AbstractDownloadAdapter implements DownloadAdapter {

    /**
     * Connection information for SFTP protocol.
     */
    private final SftpConnectionInformation sftpConnectionInfo;

    /**
     * Java Secure Channel instance.
     */
    private static final JSch JSCH = new JSch();


    /**
     * Constructs new SFTP adapter using provided information.
     * 
     * @param connectionInfo
     *            connection information for SFTP protocol.
     * @param cacheHome
     *            cache root folder.
     * @throws DownloadAdapterException
     *             if problem with Java Secure Channel while creating new entity occur.
     */
    public SftpDownloadAdapter(ConnectionInformation connectionInfo, String cacheHome)
            throws DownloadAdapterException {
        super(connectionInfo, cacheHome);
        sftpConnectionInfo = new SftpConnectionInformation(connectionInfo);
        createJSchIdentity();
    }


    @Override
    public String downloadFile(URI uri, String relativePath)
            throws DownloadAdapterException {
        String cachedFilePath = getResourceCachePath(relativePath);
        checkDestinationExistence(cachedFilePath);
        Session session = createConnectedSession();
        ChannelSftp channel = null;
        try {
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            channel.get(uri.getPath(), cachedFilePath);
        } catch (JSchException e) {
            throw new DownloadAdapterException("Exception while opening the JSch SFTP channel", e);
        } catch (SftpException e) {
            String altPath = null;
            try {
                altPath = channel.getHome() + uri.getPath();
                channel.get(altPath, cachedFilePath);
            } catch (SftpException e1) {
                throw new DownloadAdapterException("Exception while trying to download resource " + uri.getPath()
                        + " or " + altPath, e);
            }
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            session.disconnect();
        }
        return cachedFilePath;
    }


    /**
     * Creates new identity in JSch.
     * 
     * @throws DownloadAdapterException
     *             if any problems during creation occur.
     */
    private void createJSchIdentity()
            throws DownloadAdapterException {
        CertificateInformation certificateInfo = sftpConnectionInfo.getCertificateInfo();
        if (certificateInfo != null) {
            try {
                JSCH.addIdentity(sftpConnectionInfo.getUsername(), certificateInfo.getPrivateKey(),
                    certificateInfo.getPublicKey(), new byte[0]);
            } catch (JSchException e) {
                throw new DownloadAdapterException("Could create JSch identity", e);
            }
        }
    }


    /**
     * Creates new connected JSch SFTP session.
     * 
     * @return connected JSch session.
     * @throws DownloadAdapterException
     *             if any problems during connection occur.
     */
    private Session createConnectedSession()
            throws DownloadAdapterException {
        Session session = null;
        try {
            session = JSCH.getSession(connectionInfo.getUsername(), connectionInfo.getHost(), connectionInfo.getPort());
            session.setUserInfo(sftpConnectionInfo);
            session.connect();
        } catch (JSchException e) {
            throw new DownloadAdapterException("Could not create connected session.", e);
        }
        return session;
    }

}
