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
package pl.psnc.synat.wrdz.zmd.object.content;

import java.io.InputStream;
import java.util.List;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile;
import pl.psnc.synat.wrdz.zmd.storage.DataStorageException;

/**
 * Specifies the interface of object's contents fetcher.
 */
@Local
public interface ContentFetcher {

    /**
     * Downloads the object's content into the cache.
     * 
     * @param version
     *            content version to be fetched from the repository.
     * @param provided
     *            if <code>true</code>, enables extraction of provided metadata, otherwise it is skipped.
     * @param extracted
     *            if <code>true</code>, enables extraction of extracted metadata, otherwise it is skipped.
     * @return path to the cached downloaded object root folder.
     * @throws DataStorageException
     *             should any problems with data storage arise.
     */
    String fetchEntireObject(ContentVersion version, boolean provided, boolean extracted)
            throws DataStorageException;


    /**
     * Downloads the objects's content subset into the cache.
     * 
     * @param version
     *            content version to be fetched from the repository.
     * @param dataFiles
     *            list of data files to download.
     * @param provided
     *            if <code>true</code>, enables extraction of provided metadata, otherwise it is skipped.
     * @param extracted
     *            if <code>true</code>, enables extraction of extracted metadata, otherwise it is skipped.
     * @return path to the cached downloaded files root folder.
     * @throws DataStorageException
     *             should any problems with data storage arise.
     */
    String fetchContentFiles(ContentVersion version, List<DataFile> dataFiles, boolean provided, boolean extracted)
            throws DataStorageException;


    /**
     * Downloads the objects's metadata into the cache.
     * 
     * @param objectVersion
     *            object's version from which to download metadata.
     * @param provided
     *            if <code>true</code>, enables extraction of provided metadata, otherwise it is skipped.
     * @return path to the cached downloaded files root folder.
     * @throws DataStorageException
     *             should any problems with data storage arise.
     */
    String fetchMetadataFiles(ContentVersion objectVersion, boolean provided)
            throws DataStorageException;


    /**
     * Get the objects's metadata into the input stream.
     * 
     * @param objectVersion
     *            object's version from which to download metadata.
     * @return input stream to the mets file.
     * @throws DataStorageException
     *             should any problems with data storage arise.
     */
    InputStream getMetadataFile(ContentVersion objectVersion)
            throws DataStorageException;


    /**
     * Get selected metadata file into the input stream.
     * 
     * @param file
     *            metadata file info
     * @param objectVersion
     *            object's version from which to download metadata.
     * @return input stream to the mets file.
     * @throws DataStorageException
     *             should any problems with data storage arise.
     */
    InputStream getMetadataFile(MetadataFile file, ContentVersion objectVersion)
            throws DataStorageException;
}
