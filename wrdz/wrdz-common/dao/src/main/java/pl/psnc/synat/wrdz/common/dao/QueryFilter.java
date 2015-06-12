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

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * <p>
 * Interface of a filter, i.e. object containing constraint on value of fields in entity being queried. Conceptually
 * filter is a part of conditional selection (the <code>WHERE</code> statement in query), it limits (narrows) the number
 * of entities being returned by the query.
 * </p>
 * <p>
 * Filters are produced by objects implementing {@link GenericQueryFilterFactory}.
 * </p>
 * 
 * @param <T>
 *            entity class that will be managed by this filter
 */
public interface QueryFilter<T> {

    /**
     * Returns {@link CriteriaQuery} object enabled to perform query to database complaint with specified filter
     * parameters.
     * 
     * @return object ready to perform query to the database complaint with specified filtering parameters.
     */
    CriteriaQuery<T> getCriteria();


    /**
     * Returns logical contents of the filter, i.e. query filtering predicate. Passing this filter as a parameter to DAO
     * methods will result in returning only the results compliant with filter predicate.
     * 
     * @return query filtering predicate contained by this filter.
     */
    Predicate getFilteringPredicate();


    /**
     * Returns the root of the query on which sorting is defined.
     * 
     * @return the root of the query.
     */
    Root<T> getRoot();


    /**
     * Defines the distinctiveness of values in the query result. By default repetitions are present in the results.
     * 
     * @param distinct
     *            if true, then results will contain only distinct values, else values can be repeated
     */
    void setDistinct(boolean distinct);


    /**
     * Returns epoch in which the filter was created.
     * 
     * @return epoch from which the filter originates
     * @see QueryModifier
     */
    Long getEpoch();
}
