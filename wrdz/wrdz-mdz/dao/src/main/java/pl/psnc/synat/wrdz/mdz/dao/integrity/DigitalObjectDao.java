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
package pl.psnc.synat.wrdz.mdz.dao.integrity;

import java.util.Date;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.common.dao.GenericDao;
import pl.psnc.synat.wrdz.mdz.entity.integrity.DigitalObject;

/**
 * An interface for a class managing the persistence of {@link DigitalObject} class. It declares additional operations
 * available for {@link DigitalObject} object apart from basic contract defined in {@link GenericDao}.
 */
@Local
public interface DigitalObjectDao extends GenericDao<DigitalObject, String> {

    /**
     * Returns the entity that was added last.
     * 
     * @return entity instance, or <code>null</code> if no instance exists
     */
    DigitalObject getLast();


    /**
     * Returns the number of entities that didn't pass verification.
     * 
     * @return the number of entities that are corrupted
     */
    Long countCorrupted();


    /**
     * Removes all entities from the table.
     */
    void deleteAll();


    /**
     * Returns the date when the first object was added.
     * 
     * @return the date when the first object was added
     */
    Date getFirstAdded();


    /**
     * Returns the date when the last object was verified.
     * 
     * @return the date when the last object was verified
     */
    Date getLastVerified();
}
