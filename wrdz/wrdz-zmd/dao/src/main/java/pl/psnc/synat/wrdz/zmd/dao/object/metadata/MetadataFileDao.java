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
package pl.psnc.synat.wrdz.zmd.dao.object.metadata;

import java.util.Map;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDao;
import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.GenericQuerySorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile;

/**
 * Interface for DAO beans handling MetadataFile subclasses.
 * 
 * @param <FF>
 *            filter factory - class inheriting from {@link GenericQueryFilterFactory}
 * @param <SB>
 *            sorter builder - class inheriting from {@link GenericQuerySorterBuilder}
 * @param <T>
 *            entity class that will be managed by this DAO
 */
public interface MetadataFileDao<FF extends GenericQueryFilterFactory<T>, SB extends GenericQuerySorterBuilder<T>, T extends MetadataFile>
        extends ExtendedGenericDao<FF, SB, T, Long> {

    /**
     * Returns the total number of metadata files grouped by their owner id.
     * 
     * @return a <user id, count> map representing total metadata file counts per user
     */
    Map<Long, Long> countAllGroupByOwner();


    /**
     * Returns the total size of metadata files grouped by their owner id.
     * 
     * @return a <user id, count> map representing total metadata file sizes per user
     */
    Map<Long, Long> getSizeGroupByOwner();
}
