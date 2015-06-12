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

import pl.psnc.synat.wrdz.zmd.download.ConnectionInformation;
import pl.psnc.synat.wrdz.zmd.exception.DownloadAdapterException;

/**
 * An adapter class which provides functionality of handling content downloading via HTTPS protocol.
 */
public class HttpsDownloadAdapter extends AbstractDownloadAdapter implements DownloadAdapter {

    /**
     * Constructs new HTTPS adapter using provided information.
     * 
     * @param connectionInfo
     *            connection information for SFTP protocol.
     * @param cacheHome
     *            cache root folder.
     */
    public HttpsDownloadAdapter(ConnectionInformation connectionInfo, String cacheHome) {
        super(connectionInfo, cacheHome);
    }


    @Override
    public String downloadFile(URI uri, String relativePath)
            throws DownloadAdapterException {
        throw new DownloadAdapterException("The download adapter for the HTTPS protocol is not yet implemented.");
    }

}
