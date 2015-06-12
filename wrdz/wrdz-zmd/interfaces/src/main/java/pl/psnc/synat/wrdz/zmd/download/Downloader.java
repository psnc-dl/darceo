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
import java.util.Map;
import java.util.Set;

import pl.psnc.synat.wrdz.zmd.exception.DownloadException;
import pl.psnc.synat.wrdz.zmd.input.InputFile;
import pl.psnc.synat.wrdz.zmd.input.InputFileUpdate;
import pl.psnc.synat.wrdz.zmd.output.OutputFile;
import pl.psnc.synat.wrdz.zmd.output.OutputFileUpdate;

/**
 * Specifies the interface of a class providing methods for preparing the content for object adding and modification
 * operations that include data fetching.
 */
public interface Downloader {

    /**
     * Downloads object's files and metadata associated with them into cache.
     * 
     * @param inputFiles
     *            list of resources and associated metadata to download into cache.
     * @param cacheDir
     *            name of the directory in cache
     * @return list of completed download tasks.
     * @throws DownloadException
     *             if any error during download occurs
     * @throws IllegalArgumentException
     *             if passed list references a null or empty list
     */
    List<OutputFile> downloadFilesToCache(Set<InputFile> inputFiles, String cacheDir)
            throws DownloadException, IllegalArgumentException;


    /**
     * Downloads object's updated files and metadata associated with them into cache.
     * 
     * @param inputFiles
     *            list of resources and associated metadata to download into cache.
     * @param cacheDir
     *            name of the directory in cache
     * @return list of completed download tasks.
     * @throws DownloadException
     *             if any error during download occurs
     * @throws IllegalArgumentException
     *             if passed list references a null or empty list
     */
    List<OutputFileUpdate> downloadUpdatedFilesToCache(Set<InputFileUpdate> inputFiles, String cacheDir)
            throws DownloadException, IllegalArgumentException;


    /**
     * Downloads resources from the given URIs into the cache.
     * 
     * @param contentPath
     *            path to content resource relative to content folder
     * @param resources
     *            list of resources to download into cache.
     * @param cacheDir
     *            name of the directory in cache
     * @return list of completed download tasks.
     * @throws DownloadException
     *             if any error during download occurs
     * @throws IllegalArgumentException
     *             if passed list references a null or empty list
     */
    List<DownloadTask> downloadMetadataToCache(String contentPath, Map<String, URI> resources, String cacheDir)
            throws DownloadException, IllegalArgumentException;
}
