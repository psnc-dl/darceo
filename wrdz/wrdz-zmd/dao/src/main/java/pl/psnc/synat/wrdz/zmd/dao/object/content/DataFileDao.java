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

import java.util.Map;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDao;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;

/**
 * An interface for a class managing the persistence of {@link DataFile} class. It declares additional operations
 * available for {@link DataFile} object apart from basic contract defined in {@link ExtendedGenericDao}.
 */
@Local
public interface DataFileDao extends ExtendedGenericDao<DataFileFilterFactory, DataFileSorterBuilder, DataFile, Long> {

    /**
     * Returns the total number of data files grouped by their owner id.
     * 
     * @return a <user id, count> map representing data file counts per user
     */
    Map<Long, Long> countAllGroupByOwner();


    /**
     * Returns the total size of data files grouped by their owner id.
     * 
     * @return a <user id, count> map representing total data file sizes per user
     */
    Map<Long, Long> getSizeGroupByOwner();


    /**
     * Returns the specified file form the specified content version.
     * 
     * @param path
     *            object-relative path to file.
     * @param version
     *            content version to search in.
     * @return found data file or null.
     */
    DataFile getDataFileFromVersion(String path, ContentVersion version);


    /**
     * Returns the specified file form the specified content version and object path.
     * 
     * @param path
     *            object-relative path to file.
     * @param version
     *            content version to search in.
     * @return found data file or null.
     */
    DataFile getDataFileFromPathAndVersion(String path, ContentVersion version);

}
