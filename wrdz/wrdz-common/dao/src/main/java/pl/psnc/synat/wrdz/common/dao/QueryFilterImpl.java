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
 * Basic implementation of a filter.
 * 
 * @param <T>
 *            entity class that will be managed by this filter.
 */
public class QueryFilterImpl<T> implements QueryFilter<T> {

    /**
     * Criteria query being passed to constructor to be used to construct query.
     */
    protected final CriteriaQuery<T> criteriaQuery;

    /**
     * Root entity of the query.
     */
    protected final Root<T> root;

    /**
     * The predicate upon which this object was constructed.
     */
    private final Predicate filteringPredicate;

    /**
     * Value of the epoch counter identifying this instance of the class.
     */
    protected final Long epoch;


    /**
     * Constructor setting up the query filter with required parameters.
     * 
     * @param criteriaQuery
     *            query upon which will filter will be build.
     * @param root
     *            object representing root type of the entity this filter concerns.
     * @param predicate
     *            predicate upon which this filter will be constructed.
     * @param epoch
     *            filter's epoch.
     * 
     * @throws IllegalArgumentException
     *             thrown if one of arguments is <code>null</code>.
     */
    public QueryFilterImpl(CriteriaQuery<T> criteriaQuery, Root<T> root, Predicate predicate, Long epoch)
            throws IllegalArgumentException {
        if (criteriaQuery == null || root == null || predicate == null || epoch == null) {
            throw new IllegalArgumentException("Arguments' values must not be null!");
        }
        this.criteriaQuery = criteriaQuery;
        this.root = root;
        this.filteringPredicate = predicate;
        this.epoch = epoch;
    }


    @Override
    public Predicate getFilteringPredicate() {
        return filteringPredicate;
    }


    @Override
    public Root<T> getRoot() {
        return root;
    }


    @Override
    public CriteriaQuery<T> getCriteria() {
        return criteriaQuery;
    }


    @Override
    public Long getEpoch() {
        return epoch;
    }


    @Override
    public void setDistinct(boolean distinct) {
        criteriaQuery.distinct(distinct);
    }

}
