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
package pl.psnc.synat.wrdz.zmd.object;

import java.io.InputStream;
import java.util.Map;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.zmd.entity.MetadataSectionsCollection;
import pl.psnc.synat.wrdz.zmd.input.object.FileFetchingRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectDeletionRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectFetchingRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectVersionDeletionRequest;
import pl.psnc.synat.wrdz.zmd.output.ResultFile;

/**
 * Specifies interface of digital object's lifecycle manager, i.e. class, that provides functionality of CRUD operations
 * on digital objects.
 */
@Local
public interface ObjectManager {

    /**
     * Creates new object from the given contents and metadata.
     * 
     * @param request
     *            contains object's content and metadata.
     * @return created object's id
     * @throws ObjectCreationException
     *             if object creation could not completed.
     */
    String createObject(ObjectCreationRequest request)
            throws ObjectCreationException;


    /**
     * Creates new version of the object from the given information.
     * 
     * @param request
     *            contains information about object's new version, such as modifications made and files added.
     * @return number of the version created.
     * @throws ObjectModificationException
     *             if object modification could not be completed
     * @throws ObjectCreationException
     */
    int modifyObject(ObjectModificationRequest request)
            throws ObjectModificationException;


    /**
     * Deletes the version of the object by the given information.
     * 
     * @param request
     *            contains information about object's identifier and version
     * @return number of the current version after deletion
     * @throws ObjectDeletionException
     *             if object deletion could not be completed
     */
    int deleteVersion(ObjectVersionDeletionRequest request)
            throws ObjectDeletionException;


    /**
     * Deletes the object together with its all versions by the given information.
     * 
     * @param request
     *            contains information about object's identifier
     * @throws ObjectDeletionException
     *             if object deletion could not be completed
     */
    void deleteObject(ObjectDeletionRequest request)
            throws ObjectDeletionException;


    /**
     * Gets object's files list from the targeted version or the most current version if none specified. Can also return
     * metadata files information for both provided and extracted metadata.
     * 
     * @param identifier
     *            public identifier of the object
     * @param version
     *            version number, for which to get list of files
     * @param provided
     *            if <code>true</code> it will cause inclusion of provided metadata files in the files list
     * @param extracted
     *            if <code>true</code> it will cause inclusion of extracted metadata files in the files list
     * @param hashes
     *            if <code>true</code> it will cause inclusion of hashes of the files
     * @param absolute
     *            if <code>true</code> it will cause inclusion of absolute paths of the files
     * @return object's file list
     * @throws ObjectNotFoundException
     *             when the object or its version does not exist
     */
    ObjectFiles getFilesList(String identifier, Integer version, Boolean provided, Boolean extracted, Boolean hashes,
            Boolean absolute)
            throws ObjectNotFoundException;


    /**
     * Gets object's versions history.
     * 
     * @param identifier
     *            public identifier of the object
     * @param ascending
     *            defines versions display order, if true, it'll be ascending (from eldest to youngest) otherwise
     *            descending.
     * @return object's versions history ot null in case of deleted object
     * @throws ObjectNotFoundException
     *             when the object does not exist
     */
    ObjectHistory getHistory(String identifier, boolean ascending)
            throws ObjectNotFoundException;


    /**
     * Fetches entire object from the repository.
     * 
     * @param request
     *            object's fetching request.
     * @return file handle to the zipped object.
     * @throws ObjectNotFoundException
     *             if object or it's version could not be found.
     * @throws FetchingException
     *             if any problems with data storage occur.
     */
    ResultFile getObject(ObjectFetchingRequest request)
            throws ObjectNotFoundException, FetchingException;


    /**
     * Fetches a subset of object's contents from the repository.
     * 
     * @param request
     *            object's files fetching request.
     * @return file handle to the zipped contents.
     * @throws FetchingException
     *             if object, it's version or specified files inside the version could not be fetched.
     * @throws ObjectNotFoundException
     *             if object or it's version could not be found.
     * @throws FileNotFoundException
     *             if specified file was not found.
     */
    ResultFile getContentFiles(FileFetchingRequest request)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException;


    /**
     * Fetches the object's main file from the repository.
     * 
     * @param request
     *            object's main file fetching request.
     * @return file handle to the zipped contents.
     * @throws FetchingException
     *             if object, it's version or specified files inside the version could not be fetched.
     * @throws ObjectNotFoundException
     *             if object or it's version could not be found.
     * @throws FileNotFoundException
     *             if object's version contained no main file.
     */
    ResultFile getMainFile(FileFetchingRequest request)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException;


    /**
     * Fetches the object's metadata files from the repository.
     * 
     * @param identifier
     *            object's identifier.
     * @param version
     *            object's version number or null, if most current version is a target.
     * @param provided
     *            if <code>true</code>, enables extraction of provided metadata, otherwise it is skipped.
     * @return file handle to the zipped contents.
     * @throws FetchingException
     *             if object, it's version or specified files inside the version could not be fetched.
     * @throws ObjectNotFoundException
     *             if object or it's version could not be found.
     * @throws FileNotFoundException
     *             if object's version contained no extracted nor provided metadata file.
     */
    ResultFile getMetadata(String identifier, Integer version, boolean provided)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException;


    /**
     * Fetches the specified uncompressed content file of the target digital object.
     * 
     * @param objectId
     *            object's public identifier.
     * @param version
     *            object's version number.
     * @param filePath
     *            path to the file relative to version folder.
     * @return file handle to the cached file.
     * @throws FetchingException
     *             if object, it's version or specified file inside the version could not be fetched.
     * @throws ObjectNotFoundException
     *             if object or it's version could not be found.
     * @throws FileNotFoundException
     *             if specified file was not found.
     */
    ResultFile getContentFile(String objectId, Integer version, String filePath)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException;


    /**
     * Get METS metadata for the object's from the repository.
     * 
     * @param identifier
     *            object's identifier.
     * @param version
     *            object's version number or null, if most current version is a target.
     * @return file handle to the zipped contents.
     * @throws FetchingException
     *             if object, it's version or specified files inside the version could not be fetched.
     * @throws ObjectNotFoundException
     *             if object or it's version could not be found.
     * @throws FileNotFoundException
     *             if object's version contained no extracted nor provided metadata file.
     */
    String getMetsForObject(String identifier, Integer version)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException;


    /**
     * Get METS metadata section for the object's from the repository.
     * 
     * @param identifier
     *            object's identifier.
     * @param mid
     *            metadata section id.
     * @return content of selected metadata section.
     * @throws FetchingException
     *             if object, it's version or specified files inside the version could not be fetched.
     * @throws ObjectNotFoundException
     *             if object or it's version could not be found.
     * @throws FileNotFoundException
     *             if object's version contained no extracted nor provided metadata file.
     */
    MetadataSectionsCollection getMetsMetadataSection(String identifier, String mid)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException;


    /**
     * Get METS metadata section for the object's from the repository.
     * 
     * @param eid
     *            object's identifier.
     * @param mid
     *            metadata section id.
     * @return content of selected metadata section.
     * @throws FetchingException
     *             if object, it's version or specified files inside the version could not be fetched.
     * @throws ObjectNotFoundException
     *             if object or it's version could not be found.
     * @throws FileNotFoundException
     *             if object's version contained no extracted nor provided metadata file.
     */
    InputStream getMetadataSectionById(String eid, String mid)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException;


    /**
     * Get METS metadata section for the object's from the repository.
     * 
     * @param eid
     *            object's identifier.
     * @param mid
     *            metadata section id.
     * @param vid
     *            object version id.
     * @return content of selected metadata section.
     * @throws FetchingException
     *             if object, it's version or specified files inside the version could not be fetched.
     * @throws ObjectNotFoundException
     *             if object or it's version could not be found.
     * @throws FileNotFoundException
     *             if object's version contained no extracted nor provided metadata file.
     */
    InputStream getMetadataSectionById(String eid, String mid, Integer vid)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException;


    /**
     * Get METS metadata section for the object's from the repository.
     * 
     * @param eid
     *            object's identifier.
     * @param mid
     *            metadata section id.
     * @param vid
     *            object version id.
     * @param fid
     *            file id.
     * 
     * @return content of selected metadata section.
     * @throws FetchingException
     *             if object, it's version or specified files inside the version could not be fetched.
     * @throws ObjectNotFoundException
     *             if object or it's version could not be found.
     * @throws FileNotFoundException
     *             if object's version contained no extracted nor provided metadata file.
     */
    InputStream getMetadataSectionById(String eid, String mid, Integer vid, String fid)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException;


    /**
     * Get mapping between files ids and locations.
     * 
     * @param identifier
     *            object's identifier.
     * @return mapping file id <-> file location.
     * @throws FetchingException
     *             if object, it's version or specified files inside the version could not be fetched.
     * @throws ObjectNotFoundException
     *             if object or it's version could not be found.
     * @throws FileNotFoundException
     *             if object's version contained no extracted nor provided metadata file.
     */
    Map<String, String> getFilesIdLocationMap(String identifier)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException;


    /**
     * Get mapping between files ids and locations.
     * 
     * @param identifier
     *            object's identifier.
     * @param vid
     *            version id.
     * @return mapping file id <-> file location.
     * @throws FetchingException
     *             if object, it's version or specified files inside the version could not be fetched.
     * @throws ObjectNotFoundException
     *             if object or it's version could not be found.
     * @throws FileNotFoundException
     *             if object's version contained no extracted nor provided metadata file.
     */
    Map<String, String> getFilesIdLocationMap(String identifier, Integer vid)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException;
}
