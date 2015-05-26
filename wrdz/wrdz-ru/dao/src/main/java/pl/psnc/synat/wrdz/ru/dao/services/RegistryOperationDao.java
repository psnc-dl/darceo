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
package pl.psnc.synat.wrdz.ru.dao.services;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.persistence.LockModeType;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDao;
import pl.psnc.synat.wrdz.ru.entity.services.RegistryOperation;

/**
 * An interface for a class managing the persistence of {@link RegistryOperation} class. It declares additional
 * operations available for {@link RegistryOperation} object apart from basic contract defined in
 * {@link ExtendedGenericDao}.
 */
@Local
public interface RegistryOperationDao extends
        ExtendedGenericDao<RegistryOperationFilterFactory, RegistryOperationSorterBuilder, RegistryOperation, Long> {

    /**
     * Locks the table containing entity in the manner specified by the parameter.
     * 
     * @param exclusive
     *            if true then {@link LockModeType#PESSIMISTIC_WRITE} is performed, else
     *            {@link LockModeType#PESSIMISTIC_READ} is executed.
     */
    void lockTable(boolean exclusive);


    /**
     * Gets the list of registry operations that happened in the specified time period.
     * 
     * @param from
     *            searched period start date.
     * @param until
     *            searched period end date.
     * @return list of matching entities or empty list if none was matched.
     */
    List<RegistryOperation> getChanges(Date from, Date until);

}
