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
import java.util.Collections;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

/**
 * Base class for all objects used to create sorting objects. Provides basic, common methods implementation for these
 * objects.
 * 
 * @param <T>
 *            entity class that will be managed by this sorter builder
 */
public abstract class GenericQuerySorterBuilderImpl<T> implements GenericQuerySorterBuilder<T> {

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
     * A list of orderings already applied to the query.
     */
    private List<Order> orders;

    /**
     * Initial length of the {@link #orders}.
     */
    private static final int ORDERS_INITIAL_LENGTH = 1;

    /**
     * Empty sorter instance. Stored as static for optimization purposes.
     */
    private static QuerySorter<Object> emptySorter = new QuerySorterImpl<Object>();


    /**
     * Constructs this builder initializing it with required arguments.
     * 
     * @param criteriaBuilder
     *            reference to {@link CriteriaBuilder} object.
     * @param criteriaQuery
     *            query upon which will sorters be build
     * @param root
     *            object representing root type of the entity this sorter builder manages
     * @param epoch
     *            builder's epoch
     * 
     * @throws IllegalArgumentException
     *             thrown if one of arguments is <code>null</code>
     * 
     * @see QueryModifier
     */
    public GenericQuerySorterBuilderImpl(CriteriaBuilder criteriaBuilder, CriteriaQuery<T> criteriaQuery, Root<T> root,
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
    public QuerySorter<T> buildSorter() {
        ensureInitializedOrders();
        Path<?> idExpression = getIdPath();
        List<Order> ordersForSorter;
        if (getExistingOrderExpression(idExpression) != null) {
            ordersForSorter = orders;
        } else {
            ordersForSorter = orderWithIdOrdering(idExpression);
        }
        return new QuerySorterImpl<T>(criteriaQuery, root, Collections.unmodifiableList(ordersForSorter), epoch);
    }


    @SuppressWarnings("unchecked")
    @Override
    public QuerySorter<T> getEmptySorter() {
        return (QuerySorter<T>) emptySorter;
    }


    /**
     * Adds sorting by given parameter and order type (ascending or descending).
     * 
     * @param parameter
     *            parameter by which sorting will be performed
     * @param ascendingly
     *            defines whether (<code>true</code>) or not (<code>false</code>)sorting will be ascending
     */
    protected void addOrdering(Expression<?> parameter, boolean ascendingly) {
        ensureInitializedOrders();
        Order order = getExistingOrderExpression(parameter);
        if (order != null) {
            boolean orderingDiffers = order.isAscending() ^ ascendingly;
            if (orderingDiffers) {
                order.reverse();
                return;
            }
        } else {
            if (ascendingly) {
                orders.add(criteriaBuilder.asc(parameter));
            } else {
                orders.add(criteriaBuilder.desc(parameter));
            }
        }
    }


    /**
     * Returns an {@link Order} instance contained in the {@link #orders} having the given expression or
     * <code>null</code> if none was found.
     * 
     * @param expression
     *            expression being the argument of search.
     * @return instance of the {@link Order} witch matching expression or <code>null</code> if none was found.
     */
    private Order getExistingOrderExpression(Expression<?> expression) {
        for (Order order : orders) {
            if (order.getExpression().equals(expression)) {
                return order;
            }
        }
        return null;
    }


    /**
     * Returns a list of sortings enriched by ascending sorting by primary key (ensures uniform results for repetitive
     * querying). Is useful when performing paginated queries.
     * 
     * @param idExpression
     *            expression containing the id
     * @return copy of list of sortings with ascending sorting by id added to the end
     */
    private List<Order> orderWithIdOrdering(Path<?> idExpression) {
        List<Order> ordersCopy = new ArrayList<Order>(orders);
        ordersCopy.add(criteriaBuilder.asc(idExpression));
        return ordersCopy;
    }


    /**
     * Ensures that the {@link #orders} is initialized.
     */
    private void ensureInitializedOrders() {
        if (orders == null) {
            orders = new ArrayList<Order>(ORDERS_INITIAL_LENGTH);
        }
    }


    /**
     * Children classes should provide object implementing {@link Path} containing path to the primary key of the
     * entity.
     * 
     * @return object with the path to the primary key of the entity
     */
    protected abstract Path<?> getIdPath();
}
