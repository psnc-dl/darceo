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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Base class for objects used to create filters. Implements basic logical operations used for combining multitude of
 * filters into one.
 * 
 * @param <T>
 * 
 *            entity class that will be managed by this filter factory
 */
public abstract class GenericQueryFilterFactoryImpl<T> implements GenericQueryFilterFactory<T> {

    /**
     * Criteria builder being passed to constructor to be used to construct query arguments.
     */
    protected final CriteriaBuilder criteriaBuilder;

    /**
     * Criteria query being passed to constructor to be used to construct query.
     */
    protected final CriteriaQuery<T> criteriaQuery;

    /**
     * Root entity of the query.
     */
    protected final Root<T> root;

    /**
     * Value of the epoch counter identifying this instance of the class.
     */
    protected final Long epoch;


    /**
     * Constructor setting up the filter factory with required parameters.
     * 
     * @param criteriaBuilder
     *            reference to the {@link CriteriaBuilder} object
     * @param criteriaQuery
     *            query upon which will be build filters returned by this object and its children.
     * @param root
     *            object representing root type of the entity this factory manages
     * @param epoch
     *            factory's epoch
     * 
     * @throws IllegalArgumentException
     *             thrown if one of arguments is <code>null</code>
     * 
     * @see QueryModifier
     */
    public GenericQueryFilterFactoryImpl(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> criteriaQuery, Root<T> root,
            Long epoch)
            throws IllegalArgumentException {
        if (criteriaBuilder == null || criteriaQuery == null || root == null) {
            throw new IllegalArgumentException("Arguments' values must not be null!");
        }
        this.criteriaBuilder = criteriaBuilder;
        this.criteriaQuery = criteriaQuery;
        this.root = root;
        this.epoch = epoch;
    }


    @Override
    public QueryFilter<T> and(Iterable<QueryFilter<T>> filters) {
        List<Predicate> list = getFilteringPredicates(filters);
        Predicate pred = criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
        return constructQueryFilter(pred);
    }


    @Override
    public QueryFilter<T> or(Iterable<QueryFilter<T>> filters) {
        List<Predicate> list = getFilteringPredicates(filters);
        Predicate pred = criteriaBuilder.or(list.toArray(new Predicate[list.size()]));
        return constructQueryFilter(pred);
    }


    /**
     * Creates a list of predicates from the {@link Iterable} collection of {@link QueryFilter}.
     * 
     * @param filters
     *            an {@link Iterable} collection of {@link QueryFilter}
     * @return list of predicates for the query.
     */
    private List<Predicate> getFilteringPredicates(Iterable<QueryFilter<T>> filters) {
        List<Predicate> list = new ArrayList<Predicate>();
        for (QueryFilter<T> filter : filters) {
            if (filter != null) {
                list.add(filter.getFilteringPredicate());
            }
        }
        return list;
    }


    /**
     * Creates a list of predicates from the array of {@link QueryFilter}.
     * 
     * @param filters
     *            an array of {@link QueryFilter}
     * @return list of predicates for the query.
     */
    private List<Predicate> getFilteringPredicates(QueryFilter<T>[] filters) {
        List<Predicate> list = new ArrayList<Predicate>();
        for (QueryFilter<T> filter : filters) {
            if (filter != null) {
                list.add(filter.getFilteringPredicate());
            }
        }
        return list;
    }


    @Override
    public QueryFilter<T> not(QueryFilter<T> filter) {
        Predicate pred = criteriaBuilder.not(filter.getFilteringPredicate());
        return constructQueryFilter(pred);
    }


    @Override
    public QueryFilter<T> and(QueryFilter<T> filter1, QueryFilter<T> filter2, QueryFilter<T>... filters) {
        Predicate pred = criteriaBuilder.and(filter1.getFilteringPredicate(), filter2.getFilteringPredicate());
        if (filters.length > 0) {
            List<Predicate> list = getFilteringPredicates(filters);
            pred = criteriaBuilder.and(pred, criteriaBuilder.and(list.toArray(new Predicate[list.size()])));
        }
        return constructQueryFilter(pred);
    }


    @Override
    public QueryFilter<T> or(QueryFilter<T> filter1, QueryFilter<T> filter2, QueryFilter<T>... filters) {
        Predicate pred = criteriaBuilder.or(filter1.getFilteringPredicate(), filter2.getFilteringPredicate());
        if (filters.length > 0) {
            List<Predicate> list = getFilteringPredicates(filters);
            pred = criteriaBuilder.or(pred, criteriaBuilder.and(list.toArray(new Predicate[list.size()])));
        }
        return constructQueryFilter(pred);
    }


    /**
     * Constructs {@link QueryFilter} from the given predicate.
     * 
     * @param predicate
     *            predicate from which to create {@link QueryFilter}
     * @return new instance of {@link QueryFilter} representing given predicate
     */
    protected QueryFilter<T> constructQueryFilter(Predicate predicate) {
        return new QueryFilterImpl<T>(criteriaQuery, root, predicate, epoch);
    }

}
