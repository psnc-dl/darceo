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
package pl.psnc.synat.wrdz.zmd.download;

import pl.psnc.synat.wrdz.zmd.download.adapters.DownloadAdapter;
import pl.psnc.synat.wrdz.zmd.download.adapters.FtpDownloadAdapter;
import pl.psnc.synat.wrdz.zmd.download.adapters.HttpDownloadAdapter;
import pl.psnc.synat.wrdz.zmd.download.adapters.HttpsDownloadAdapter;
import pl.psnc.synat.wrdz.zmd.download.adapters.SftpDownloadAdapter;
import pl.psnc.synat.wrdz.zmd.exception.DownloadAdapterException;

/**
 * Enum performing the role of abstract factory of {@link DownloadAdapter} objects.
 */
public enum DownloadAdapterFactory {

    /**
     * SFTP protocol adapter.
     */
    SFTP("sftp") {

        @Override
        public DownloadAdapter getAdapter(ConnectionInformation connectionInfo, String cacheHome)
                throws DownloadAdapterException {
            return new SftpDownloadAdapter(connectionInfo, cacheHome);
        }
    },

    /**
     * FTP protocol adapter.
     */
    FTP("ftp") {

        @Override
        public DownloadAdapter getAdapter(ConnectionInformation connectionInfo, String cacheHome)
                throws DownloadAdapterException {
            return new FtpDownloadAdapter(connectionInfo, cacheHome);
        }
    },

    /**
     * HTTP protocol adapter.
     */
    HTTP("http") {

        @Override
        public DownloadAdapter getAdapter(ConnectionInformation connectionInfo, String cacheHome)
                throws DownloadAdapterException {
            return new HttpDownloadAdapter(connectionInfo, cacheHome);
        }
    },

    /**
     * HTTPS protocol adapter.
     */
    HTTPS("https") {

        @Override
        public DownloadAdapter getAdapter(ConnectionInformation connectionInfo, String cacheHome)
                throws DownloadAdapterException {
            return new HttpsDownloadAdapter(connectionInfo, cacheHome);
        }
    };

    /**
     * String representing protocol symbol in URL address.
     */
    private final String protocol;


    /**
     * Default constructor initializing enum with the string value.
     * 
     * @param protocol
     *            string representing protocol symbol in URL address.
     */
    DownloadAdapterFactory(String protocol) {
        this.protocol = protocol;
    }


    /**
     * Returns the {@link String} representing the name of the protocol of the adapter.
     * 
     * @return name of the protocol of the adapter being produced.
     */
    public String getProtocol() {
        return protocol;
    }


    /**
     * Returns the instance of {@link DownloadAdapter} that is able to communicate via this protocol.
     * 
     * @param connectionInfo
     *            provides information about the connection for this adapter
     * @param cacheHome
     *            represents an absolute path to the download cache folder
     * @return instance of {@link DownloadAdapter} that is able to communicate via this protocol.
     * @throws DownloadAdapterException
     *             if any problem with download adapter initialization occur.
     */
    public DownloadAdapter getAdapter(ConnectionInformation connectionInfo, String cacheHome)
            throws DownloadAdapterException {
        return null;
    }

}
