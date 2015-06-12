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
package pl.psnc.synat.wrdz.zmd.storage;

import java.io.InputStream;
import java.util.List;

import javax.ejb.Local;

import pl.psnc.synat.dsa.exception.DataStorageResourceException;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile;

/**
 * Interface access to objects (and its content and metadata) in a repository.
 */
@Local
public interface DataStorageAccess {

    /**
     * Creates object in the repository.
     * 
     * @param object
     *            digital object to which the content and metadata belong
     * @throws DataStorageResourceException
     *             when some error during getting a connection to data storage occurs
     */
    void createObject(DigitalObject object)
            throws DataStorageResourceException;


    /**
     * Creates new version in the repository.
     * 
     * @param version
     *            version representation in the database.
     * @throws DataStorageResourceException
     *             when some error during getting a connection to data storage occurs
     */
    void createVersion(ContentVersion version)
            throws DataStorageResourceException;


    /**
     * Fetches object from the repository.
     * 
     * @param objectVersion
     *            version of the object
     * @param provided
     *            whether provided metadata should be fetched
     * @param extracted
     *            whether extracted metadata should be fetched
     * @param destPath
     *            object's directory in download cache (or any parent directory being the target of download)
     * @throws DataStorageResourceException
     *             if any data storage related problems occur.
     */
    void fetchObject(ContentVersion objectVersion, boolean provided, boolean extracted, String destPath)
            throws DataStorageResourceException;


    /**
     * Fetches data files from the repository.
     * 
     * @param version
     *            version of the object
     * @param dataFiles
     *            data files to get
     * @param provided
     *            whether provided metadata should be retrieved
     * @param extracted
     *            whether extracted metadata should be retrieved
     * @param destPath
     *            object's directory in download cache (or any parent directory being the target of download)
     * @throws DataStorageResourceException
     *             if any data storage related problems occur.
     */
    void fetchDataFiles(ContentVersion version, List<DataFile> dataFiles, boolean provided, boolean extracted,
            String destPath)
            throws DataStorageResourceException;


    /**
     * Fetches metadata files of the object from the repository.
     * 
     * @param objectVersion
     *            object's content
     * @param provided
     *            whether provided metadata should be retrieved
     * @param destPath
     *            object's directory in download cache (or any parent directory being the target of download)
     * @throws DataStorageResourceException
     *             if any data storage related problems occur.
     */
    void fetchMetadataFiles(ContentVersion objectVersion, boolean provided, String destPath)
            throws DataStorageResourceException;


    /**
     * Get metadata file (METS) of the object from the repository.
     * 
     * @param objectVersion
     *            object's content
     * @return input stream to the mets file
     * 
     * @throws DataStorageResourceException
     *             if any data storage related problems occur.
     */
    InputStream getMetadataFile(ContentVersion objectVersion)
            throws DataStorageResourceException;


    /**
     * Get selected metadata file of the object from the repository.
     * 
     * @param file
     *            selected metadata file info
     * @param objectVersion
     *            object's content
     * @return input stream to the mets file
     * 
     * @throws DataStorageResourceException
     *             if any data storage related problems occur.
     */
    InputStream getMetadataFile(MetadataFile file, ContentVersion objectVersion)
            throws DataStorageResourceException;


    /**
     * Deletes version folder with all its contents and all the left by empty directories on the path.
     * 
     * @param version
     *            version to be deleted.
     * @throws DataStorageResourceException
     *             if problem with adapter occurs.
     */
    void deleteVersion(ContentVersion version)
            throws DataStorageResourceException;


    /**
     * Deletes digital object folder and all it's contents nd all the left by empty directories on the path.
     * 
     * @param object
     *            object to be deleted.
     * @throws DataStorageResourceException
     *             if problem with adapter occurs.
     */
    void deleteObject(DigitalObject object)
            throws DataStorageResourceException;

}
