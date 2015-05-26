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

import pl.psnc.synat.wrdz.zmd.exception.DownloadAdapterException;

/**
 * Specifies the interface for classes that provide handling of content downloading for a specific protocol (i.e. FTP,
 * SFTP, HTTP, HTTPS). Each class implementing this interface should handle only one such protocol.
 */
public interface DownloadAdapter {

    /**
     * Downloads the resource from the given address into the cache area.
     * 
     * @param uri
     *            URL compliant URI of the resource to download.
     * @param relativePath
     *            object-relative path of the resource
     * @return the absolute path to the cached file.
     * @throws DownloadAdapterException
     *             if any problems with download adapter occur
     */
    String downloadFile(URI uri, String relativePath)
            throws DownloadAdapterException;

}
