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
package pl.psnc.synat.wrdz.zmd.dao.object;

import java.util.Map;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDao;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;

/**
 * An interface for a class managing the persistence of {@link DigitalObject} class. It declares additional operations
 * available for {@link DigitalObject} object apart from basic contract defined in {@link ExtendedGenericDao}.
 */
@Local
public interface DigitalObjectDao extends
        ExtendedGenericDao<DigitalObjectFilterFactory, DigitalObjectSorterBuilder, DigitalObject, Long> {

    /**
     * Checks whether the current version of the object with the given identifier contains data files in the format with
     * the given PUID.
     * 
     * @param identifier
     *            object identifier
     * @param formatPuid
     *            data file format PUID
     * @return <code>true</code> if at least one matching data file exists; <code>false</code> otherwise
     */
    boolean containsDataFiles(String identifier, String formatPuid);


    /**
     * Fetches the object with specified identifier.
     * 
     * @param identifier
     *            public identifier of the object.
     * @return digital object with specified identifier or null.
     */
    DigitalObject getDigitalObject(String identifier);


    /**
     * Returns the total number of objects grouped by their owner id.
     * 
     * @return a <user id, count> map representing object counts per user
     */
    Map<Long, Long> countAllGroupByOwner();
}
