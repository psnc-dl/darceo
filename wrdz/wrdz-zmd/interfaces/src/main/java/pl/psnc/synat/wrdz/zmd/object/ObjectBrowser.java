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

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile;

/**
 * Describes interface of {@link ObjectManager} helper bean.
 */
@Local
public interface ObjectBrowser {

    /**
     * Fetches the object with specified identifier.
     * 
     * @param identifier
     *            public identifier of the object.
     * @return digital object with specified identifier.
     * @throws ObjectNotFoundException
     *             thrown if no object was found.
     */
    DigitalObject getDigitalObject(String identifier)
            throws ObjectNotFoundException;


    /**
     * Fetches the content (METS) of the entity with specified identifier.
     * 
     * @param identifier
     *            public identifier of the entity.
     * @return METS representation of entity with specified identifier.
     * @throws ObjectNotFoundException
     *             thrown if no object was found.
     */
    String getEntityContent(Long identifier)
            throws ObjectNotFoundException;


    /**
     * Fetches a single page of undeleted objects matching the criteria.
     * 
     * @param pageOffset
     *            how many pages to skip
     * @param pageSize
     *            how many objects per page
     * @param identifier
     *            identifier filter value (optional)
     * @param name
     *            name filter value (optional)
     * @return a list of digital objects
     */
    List<DigitalObject> getDigitalObjects(int pageOffset, int pageSize, String identifier, String name);


    /**
     * Counts the total number of undeleted objects matching the criteria.
     * 
     * @param identifier
     *            identifier filter value (optional)
     * @param name
     *            name filter value (optional)
     * @return the number of undeleted objects
     */
    long countDigitalObjects(String identifier, String name);


    /**
     * Fetches a single page of objects with the specified identifiers that match the criteria.
     * 
     * @param ids
     *            object identifiers (database primary keys)
     * @param pageOffset
     *            how many pages to skip
     * @param pageSize
     *            how many objects per page
     * @param identifier
     *            identifier filter value (optional)
     * @param name
     *            name filter value (optional)
     * @return a list of digital objects
     */
    List<DigitalObject> getDigitalObjects(Collection<Long> ids, int pageOffset, int pageSize, String identifier,
            String name);


    /**
     * Counts the total number of objects with the specified identifiers that match the criteria.
     * 
     * @param ids
     *            object identifiers (database primary keys)
     * @param identifier
     *            identifier filter value (optional)
     * @param name
     *            name filter value (optional)
     * @return the number of undeleted objects
     */
    long countDigitalObjects(Collection<Long> ids, String identifier, String name);


    /**
     * Fetches the target content version of the target digital object.
     * 
     * @param identifier
     *            object's public identifier.
     * @param versionNo
     *            object's version number.
     * @return content version of object with specified number or the most current version if the number have not been
     *         specified.
     * @throws ObjectNotFoundException
     *             if either digital object or version within digital object have not been not found.
     */
    ContentVersion getObjectsVersion(String identifier, Integer versionNo)
            throws ObjectNotFoundException;


    /**
     * Fetches the ordered version history of the target digital object.
     * 
     * @param identifier
     *            object's public identifier.
     * @param ascending
     *            determines whether or not versions should be in eldest-first (<code>true</code>) order of newest-first
     *            order(<code>false</code>).
     * @return ordered list of content versions of the specified object or (empty list in case of deleted object)
     * @throws ObjectNotFoundException
     *             if no object with specified identifier was found in the database.
     */
    List<ContentVersion> getContentVersions(String identifier, boolean ascending)
            throws ObjectNotFoundException;


    /**
     * Checks whether or not the specified content version contains specified file and returns it.
     * 
     * @param path
     *            object-relative path to file.
     * @param version
     *            content version to search in.
     * @return found data file.
     * @throws FileNotFoundException
     *             if no such data file was found within the version.
     */
    DataFile getDataFileFromVersion(String path, ContentVersion version)
            throws FileNotFoundException;


    /**
     * Fetches data files specified on the list from the database.
     * 
     * @param version
     *            version's number.
     * @param files
     *            list of version-related paths to object's files.
     * @return list of fetched files.
     * @throws FileNotFoundException
     *             should any files on the list not be found.
     */
    List<DataFile> getDataFiles(ContentVersion version, List<String> files)
            throws FileNotFoundException;


    /**
     * Checks whether or not the specified content version contains specified metadata file and returns it.
     * 
     * @param path
     *            object-relative path to file.
     * @param version
     *            content version to search in.
     * @return found metadata file.
     * @throws FileNotFoundException
     *             if no such data file was found within the version.
     */
    MetadataFile getMetadataFileFromVersion(String path, ContentVersion version)
            throws FileNotFoundException;
}
