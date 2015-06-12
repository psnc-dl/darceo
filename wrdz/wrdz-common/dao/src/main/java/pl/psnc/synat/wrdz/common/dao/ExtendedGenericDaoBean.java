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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Base class for DAO objects using filters and sorters. Implements operations performed on entity it manages with use
 * of classes implementing {@link QueryFilter} and {@link QuerySorter}.
 * 
 * @param <FF>
 *            filter factory implementing {@link GenericQueryFilterFactory}
 * @param <SB>
 *            sorter builder implementing {@link GenericQuerySorterBuilder}
 * @param <T>
 *            entity class that will be managed by this DAO
 * @param <PK>
 *            {@link Serializable} class representing entity's primary key
 */
public abstract class ExtendedGenericDaoBean<FF extends GenericQueryFilterFactory<T>, SB extends GenericQuerySorterBuilder<T>, T, PK extends Serializable>
        extends GenericDaoBean<T, PK> implements ExtendedGenericDao<FF, SB, T, PK> {

    /**
     * Epoch counter ensuring the correct assignment of {@link CriteriaQuery} instances.
     */
    private static final AtomicLong EPOCH_COUNTER = new AtomicLong(Long.MIN_VALUE);


    /**
     * Sets the generic type this DAO will be responsible for.
     * 
     * @param clazz
     *            entity class that will be managed by this DAO
     */
    public ExtendedGenericDaoBean(Class<T> clazz) {
        super(clazz);
    }


    @Override
    public QueryModifier<FF, SB, T> createQueryModifier() {
        Long epoch = EPOCH_COUNTER.getAndIncrement();
        CriteriaQuery<T> cq = criteriaBuilder.createQuery(clazz);
        Root<T> root = cq.from(clazz);
        FF queryFilterFactory = createQueryFilterFactory(criteriaBuilder, cq, root, epoch);
        SB querySorterBuilder = createQuerySorterBuilder(criteriaBuilder, cq, root, epoch);
        return new QueryModifierImpl<FF, SB, T>(queryFilterFactory, querySorterBuilder);
    }


    /**
     * Method implemented by inheriting classes that must return a concrete class extending
     * {@link GenericQueryFilterFactory} using passed parameters.
     * 
     * @param criteriaBuilder
     *            reference to {@link CriteriaBuilder} object
     * @param criteriaQuery
     *            query this filter factory will manage and modify
     * @param root
     *            object representing root type of the entity this sorter manages
     * @param epoch
     *            epoch (token) - all objects taking part in modification of the same query get the same, unique value
     *            of epoch counter. This value is used to identify those objects as balonging to the context of the same
     *            query.
     * 
     * @return concrete implementation filter factory
     * @see QueryModifier
     */
    protected abstract FF createQueryFilterFactory(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> criteriaQuery,
            Root<T> root, Long epoch);


    /**
     * Method implemented by inheriting classes that must return a concrete class extending
     * {@link GenericQuerySorterBuilder} using passed parameters.
     * 
     * @param criteriaBuilder
     *            reference to {@link CriteriaBuilder} object
     * @param criteriaQuery
     *            query this filter factory will manage and modify
     * @param root
     *            object representing root type of the entity this sorter manages
     * @param epoch
     *            epoch (token) - all objects taking part in modification of the same query get the same, unique value
     *            of epoch counter. This value is used to identify those objects as balonging to the context of the same
     *            query.
     * 
     * @return concrete implementation of sorter builder
     * @see QueryModifier
     */
    protected abstract SB createQuerySorterBuilder(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> criteriaQuery,
            Root<T> root, Long epoch);


    @SuppressWarnings({ "unchecked" })
    @Override
    public Long countBy(QueryFilter<T> filter)
            throws IllegalArgumentException {
        if (filter == null) {
            throw new IllegalArgumentException("Argument's value must not be null!");
        }
        Predicate wherePredicate = filter.getFilteringPredicate();
        CriteriaQuery<Long> criteria = (CriteriaQuery<Long>) filter.getCriteria();
        Root<T> root = filter.getRoot();
        List<Order> noOrder = Collections.emptyList();
        criteria.select(criteriaBuilder.count(root)).where(wherePredicate).orderBy(noOrder);
        return entityManager.createQuery(criteria).getSingleResult();
    }


    @Override
    public List<T> findBy(QueryFilter<T> filter, boolean distinct)
            throws IllegalArgumentException {
        if (filter == null) {
            throw new IllegalArgumentException("Argument's value must not be null!");
        }
        CriteriaQuery<T> criteria = prepareCriteriaQuery(filter).distinct(distinct);
        return entityManager.createQuery(criteria).getResultList();
    }


    @Override
    public List<T> findBy(QueryFilter<T> filter, QuerySorter<T> sorter)
            throws IllegalArgumentException {
        checkFilterAndSorter(filter, sorter);
        CriteriaQuery<T> criteria = prepareCriteriaQuery(filter, sorter);
        return entityManager.createQuery(criteria).getResultList();
    }


    @Override
    public List<T> findPaginatedBy(QueryFilter<T> filter, QuerySorter<T> sorter, int pageSize, int pageOffset)
            throws IllegalArgumentException {
        checkFilterAndSorter(filter, sorter);
        CriteriaQuery<T> criteria = prepareCriteriaQuery(filter, sorter);
        return entityManager.createQuery(criteria).setFirstResult(pageOffset * pageSize).setMaxResults(pageSize)
                .getResultList();
    }


    @Override
    public List<T> findAll(QuerySorter<T> sorter)
            throws IllegalArgumentException {
        if (sorter == null) {
            throw new IllegalArgumentException("Argument's value must not be null!");
        }
        if (sorter.getOrders().isEmpty()) {
            return findAll();
        }
        List<Order> orders = sorter.getOrders();
        CriteriaQuery<T> criteriaQuery = sorter.getCriteria();
        criteriaQuery.select(sorter.getRoot()).orderBy(orders);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }


    @Override
    public T findFirstResultBy(QueryFilter<T> filter)
            throws IllegalArgumentException {
        if (filter == null) {
            throw new IllegalArgumentException("Argument's value must not be null!");
        }
        CriteriaQuery<T> criteria = prepareCriteriaQuery(filter);
        T result = null;
        List<T> resultList = entityManager.createQuery(criteria).setMaxResults(1).getResultList();
        if (resultList.size() > 0) {
            result = resultList.get(0);
        }
        return result;
    }


    @Override
    public T findFirstResultBy(QueryFilter<T> filter, QuerySorter<T> sorter)
            throws IllegalArgumentException {
        checkFilterAndSorter(filter, sorter);
        CriteriaQuery<T> criteria = prepareCriteriaQuery(filter, sorter);
        T result = null;
        List<T> resultList = entityManager.createQuery(criteria).setMaxResults(1).getResultList();

        if (resultList.size() > 0) {
            result = resultList.get(0);
        }
        return result;
    }


    @Override
    public T findSingleResultBy(QueryFilter<T> filter)
            throws IllegalArgumentException {
        if (filter == null) {
            throw new IllegalArgumentException("Argument's value must not be null!");
        }
        CriteriaQuery<T> criteria = prepareCriteriaQuery(filter);
        return entityManager.createQuery(criteria).getSingleResult();
    }


    /**
     * Checks if passed parameters are not <code>null</code> and if their epoch is equal.
     * 
     * @param filter
     *            filter to be checked
     * @param sorter
     *            sorter to be checked
     * 
     * @throws IllegalArgumentException
     *             thrown if any of the stated conditions is not satisfied
     */
    private void checkFilterAndSorter(QueryFilter<T> filter, QuerySorter<T> sorter)
            throws IllegalArgumentException {
        if (filter == null || sorter == null) {
            throw new IllegalArgumentException("Argument's value must not be null!");
        }
        boolean sorterNotEmpty = !sorter.getOrders().isEmpty();
        boolean epochsDiffer = !filter.getEpoch().equals(sorter.getEpoch());
        if (sorterNotEmpty && epochsDiffer) {
            throw new IllegalArgumentException(
                    "Filter and sorter were constructed from different calls to the service,"
                            + " use these objects only from the same service call.");
        }
    }


    /**
     * Constructs the query from the passed arguments.
     * 
     * @param filter
     *            filter to be used to construct the query
     * @param sorter
     *            sorter to be used to construct the query
     * @return query constructed using passed arguments
     */
    private CriteriaQuery<T> prepareCriteriaQuery(QueryFilter<T> filter, QuerySorter<T> sorter) {
        Predicate wherePredicate = filter.getFilteringPredicate();
        List<Order> orders = sorter.getOrders();
        CriteriaQuery<T> criteria = filter.getCriteria();
        criteria.select(filter.getRoot()).where(wherePredicate).orderBy(orders);
        return criteria;
    }


    /**
     * Constructs the query from the passed argument.
     * 
     * @param filter
     *            filter to be used to construct the query
     * @return query constructed using passed argument
     */
    private CriteriaQuery<T> prepareCriteriaQuery(QueryFilter<T> filter) {
        Predicate wherePredicate = filter.getFilteringPredicate();
        CriteriaQuery<T> criteria = filter.getCriteria();
        criteria.select(filter.getRoot()).where(wherePredicate);
        return criteria;
    }

}
