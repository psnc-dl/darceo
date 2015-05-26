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

import java.util.Collections;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

/**
 * Basic implementation of sorter.
 * 
 * @param <T>
 *            entity class that will be managed by this sorter.
 */
public class QuerySorterImpl<T> implements QuerySorter<T> {

    /**
     * Criteria query being passed to constructor to be used to construct query.
     */
    protected final CriteriaQuery<T> criteriaQuery;

    /**
     * List of orders contained in the query sorter object.
     */
    private final List<Order> orders;

    /**
     * Root entity of the query.
     */
    protected final Root<T> root;

    /**
     * Value of the epoch counter identifying this instance of the class.
     */
    protected final Long epoch;


    /**
     * Constructs empty query sorter, has /**
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
package scope to ensure protection of this constructor and secure it from
     * potentially dangerous use from the code other than in {@link GenericQuerySorterBuilderImpl}.
     */
    QuerySorterImpl() {
        this.criteriaQuery = null;
        this.root = null;
        this.orders = Collections.<Order> emptyList();
        this.epoch = 0L;
    }


    /**
     * Constructor setting up the query sorter with required parameters.
     * 
     * @param criteriaQuery
     *            query upon which will sorter will be build.
     * @param root
     *            object representing root type of the entity this sorter concerns.
     * @param orders
     *            orders upon which this sorter will be constructed.
     * @param epoch
     *            sorter's epoch.
     * 
     * @throws IllegalArgumentException
     *             thrown if one of arguments is <code>null</code>.
     */
    public QuerySorterImpl(CriteriaQuery<T> criteriaQuery, Root<T> root, List<Order> orders, Long epoch)
            throws IllegalArgumentException {
        if (criteriaQuery == null || root == null || orders == null || epoch == null) {
            throw new IllegalArgumentException("Arguments' values must not be null!");
        }
        this.criteriaQuery = criteriaQuery;
        this.root = root;
        this.orders = orders;
        this.epoch = epoch;
    }


    @Override
    public List<Order> getOrders() {
        return orders;
    }


    @Override
    public CriteriaQuery<T> getCriteria() {
        return criteriaQuery;
    }


    @Override
    public Root<T> getRoot() {
        return root;
    }


    @Override
    public Long getEpoch() {
        return epoch;
    }

}
