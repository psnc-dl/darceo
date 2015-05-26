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

import java.net.URI;
import java.util.List;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.zmd.exception.DownloadException;

/**
 * Specifies the interface for classes that manage the process of downloading of content linked via URL.
 */
@Local
public interface DownloadManager {

    /**
     * Downloads a single file from the given address into the cache.
     * 
     * @param uri
     *            URL compliant URI of the resource to download.
     * @param relativePath
     *            object-relative path of the resource.
     * @return the absolute path to the cached file.
     * @throws DownloadException
     *             if any problems with download occur.
     * @throws IllegalArgumentException
     *             if URI is <code>null</code> or is not a valid URL.
     */
    String downloadFromUri(URI uri, String relativePath)
            throws DownloadException, IllegalArgumentException;


    /**
     * Downloads a list of files int the cache given the information about their location.
     * 
     * @param files
     *            list of files to download.
     * @param cacheDir
     *            name of the directory in cache
     * @return list of downloaded files.
     * @throws DownloadException
     *             if any problems with download occur.
     * @throws IllegalArgumentException
     *             if file list is empty or <code>null</code>.
     */
    List<DownloadTask> downloadFiles(List<DownloadTask> files, String cacheDir)
            throws DownloadException, IllegalArgumentException;

}
