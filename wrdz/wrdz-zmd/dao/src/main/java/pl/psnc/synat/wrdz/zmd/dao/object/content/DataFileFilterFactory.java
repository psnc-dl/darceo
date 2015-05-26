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
package pl.psnc.synat.wrdz.zmd.dao.object.content;

import java.util.Collection;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.metadata.tech.FileType;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;

/**
 * Specified set of filters for queries concerning {@link DataFile} entities.
 */
public interface DataFileFilterFactory extends GenericQueryFilterFactory<DataFile> {

    /**
     * Filters the entities by the file name.
     * 
     * @param name
     *            name of the file
     * @return current representations of filters set
     */
    QueryFilter<DataFile> byFilename(String name);


    /**
     * Filters the entities by the filenames.
     * 
     * @param names
     *            names of the file
     * @return current representations of filters set
     */
    QueryFilter<DataFile> byFilenames(Collection<String> names);


    /**
     * Filters the entities by the object file path.
     * 
     * @param path
     *            object file path
     * @return current representations of filters set
     */
    QueryFilter<DataFile> byObjectFilePath(Collection<String> pathes);


    /**
     * Filters the entities by the number of the version they belong to.
     * 
     * @param number
     *            number of the version
     * @param id
     *            identifier (primary key value) of the digital object
     * @param isMainFile
     *            if <code>true</code> then files being main files for the version will be searched, otherwise files
     *            belonging to the version will be returned.
     * @return current representations of filters set
     */
    QueryFilter<DataFile> byContentVersionNo(Long id, Integer number, boolean isMainFile);


    /**
     * Filters the entities by the number of the version they belong to.
     * 
     * @param id
     *            identifier (primary key value) of the content version.
     * @param isMainFile
     *            if <code>true</code> then files being main files for the version will be searched, otherwise files
     *            belonging to the version will be returned.
     * @return current representations of filters set
     */
    QueryFilter<DataFile> byContentVersionNo(Long id, boolean isMainFile);


    /**
     * Filters the entities by the digital object they belong to.
     * 
     * @param id
     *            identifier (primary key value) of the digital object
     * @return current representations of filters set
     */
    QueryFilter<DataFile> byDigitalObject(Long id);


    /**
     * Filters the entities by the identifiers of digital object they belong to. Can use exact string match or a regexp
     * pattern.
     * 
     * @param identifier
     *            identifier of the digital object or a regexp pattern for that identifier to match
     * @return current representations of filters set
     */
    QueryFilter<DataFile> byDigitalObjectIdentifier(String identifier);


    /**
     * Filters the entities by their file format.
     * 
     * @param id
     *            identifier (primary key value) of the file format
     * @return current representations of filters set
     */
    QueryFilter<DataFile> byDataFileFormat(Long id);


    /**
     * Filters the entities by their file type.
     * 
     * @param type
     *            type of file
     * @return current representations of filters set
     */
    QueryFilter<DataFile> byFileType(FileType type);

}
