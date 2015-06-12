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
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion;

/**
 * Specified set of filters for queries concerning {@link DataFileVersion} entities.
 */
public interface DataFileVersionFilterFactory extends GenericQueryFilterFactory<DataFileVersion> {

    /**
     * Filters the entities by the file name.
     * 
     * @param name
     *            name of the file
     * @return current representations of filters set
     */
    QueryFilter<DataFileVersion> byFilename(String name);


    /**
     * Filters the entities by the filenames.
     * 
     * @param names
     *            names of the file
     * @return current representations of filters set
     */
    QueryFilter<DataFileVersion> byFilenames(Collection<String> names);


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
    QueryFilter<DataFileVersion> byContentVersionNo(Long id, Integer number, boolean isMainFile);


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
    QueryFilter<DataFileVersion> byContentVersionNo(Long id, boolean isMainFile);

}
