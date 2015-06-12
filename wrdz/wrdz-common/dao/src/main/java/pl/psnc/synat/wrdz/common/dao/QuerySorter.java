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
package pl.psnc.synat.wrdz.common.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

/**
 * <p>
 * Interface of a sorter, i.e. object including parameters by which sorting is performed.
 * </p>
 * <p>
 * Sorters are build by {@link GenericQuerySorterBuilder}.
 * </p>
 * 
 * @param <T>
 *            entity class that will be managed by this sorter
 */
public interface QuerySorter<T> {

    /**
     * Returns {@link CriteriaQuery} enabled to perform query to database compliant with specified sorting parameters.
     * 
     * @return object ready to perform query to the database complaint with specified sorting parameters.
     */
    CriteriaQuery<T> getCriteria();


    /**
     * Returns logical structure of sorter, i.e. list of objects containing parameters of sorting.
     * 
     * @return list of parameters by which the sorting will be performed
     */
    List<Order> getOrders();


    /**
     * Returns the root of the query on which sorting is defined.
     * 
     * @return the root of the query.
     */
    Root<T> getRoot();


    /**
     * Returns epoch in which the sorter was created.
     * 
     * @return epoch from which the sorter originates
     * @see QueryModifier
     */
    Long getEpoch();

}
