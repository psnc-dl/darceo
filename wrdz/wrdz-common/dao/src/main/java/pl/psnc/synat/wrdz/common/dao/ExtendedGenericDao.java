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
import java.util.List;

/**
 * Interface for DAO beans using filters and sorters produced by {@link GenericQueryFilterFactory} and
 * {@link GenericQuerySorterBuilder} creators . What's important the objects the objects used as parameters for this
 * class methods must originate from the same instance of {@link QueryModifier} - otherwise errors will occur.
 * 
 * @param <FF>
 *            filter factory - class inheriting from {@link GenericQueryFilterFactory}
 * @param <SB>
 *            sorter builder - class inheriting from {@link GenericQuerySorterBuilder}
 * @param <T>
 *            entity class that will be managed by this DAO
 * @param <PK>
 *            {@link Serializable} class representing entity's primary key
 */
public interface ExtendedGenericDao<FF extends GenericQueryFilterFactory<T>, SB extends GenericQuerySorterBuilder<T>, T, PK extends Serializable>
        extends GenericDao<T, PK> {

    /**
     * Creates object containing {@link GenericQueryFilterFactory} and {@link GenericQuerySorterBuilder} creators'
     * instances used for generation and modification of the query.
     * 
     * @return object containing {@link GenericQueryFilterFactory} and {@link GenericQuerySorterBuilder}
     */
    QueryModifier<FF, SB, T> createQueryModifier();


    /**
     * Returns number of entities in compliance with passed filter.
     * 
     * @param filter
     *            criteria of the query
     * @return number of entities fulfilling the criteria
     * @throws IllegalArgumentException
     *             thrown if passed filter is <code>null</code>, ({@link #countAll()} should be used instead.
     */
    Long countBy(QueryFilter<T> filter)
            throws IllegalArgumentException;


    /**
     * Returns list of entities in compliance with passed filter.
     * 
     * @param filter
     *            criteria of the query
     * @param distinct
     *            whether distinct entities
     * @return list of entities found sorted by specified sorters.
     * @throws IllegalArgumentException
     *             thrown if passed filter is <code>null</code>, ({@link #findAll()} should be used instead.
     */
    List<T> findBy(QueryFilter<T> filter, boolean distinct)
            throws IllegalArgumentException;


    /**
     * Returns single entity in compliance with passed filter.
     * 
     * @param filter
     *            criteria of the query
     * @return single entity found sorted by specified sorters.
     * @throws IllegalArgumentException
     *             thrown if passed filter is <code>null</code>, ({@link #findAll()} should be used instead.
     */
    T findSingleResultBy(QueryFilter<T> filter)
            throws IllegalArgumentException;


    /**
     * Returns first entity being in the result of the query in compliance with passed filter.
     * 
     * @param filter
     *            criteria of the query
     * @return first object being the result of query
     * @throws IllegalArgumentException
     *             thrown if any passed parameter is <code>null</code>.
     */
    T findFirstResultBy(QueryFilter<T> filter)
            throws IllegalArgumentException;


    /**
     * Returns list of entities in compliance with passed filter and given sorter.
     * 
     * @param filter
     *            criteria of the query
     * @param sorter
     *            sorters applied to the results
     * @return list of entities found sorted by specified sorters.
     * @throws IllegalArgumentException
     *             thrown if any passed parameter is <code>null</code>.
     */
    List<T> findBy(QueryFilter<T> filter, QuerySorter<T> sorter)
            throws IllegalArgumentException;


    /**
     * Returns part of the list of entities in compliance with passed filter and given sorter. Size and contents of the
     * list is determined by parameters: page size and page offset.
     * 
     * @param filter
     *            criteria of the query
     * @param sorter
     *            sorters applied to the results
     * @param pageSize
     *            size of the 'page' of results
     * @param pageOffset
     *            number of the 'page' of the results
     * @return 'page' containing the results of query
     * @throws IllegalArgumentException
     *             thrown if any passed parameter is <code>null</code>.
     */
    List<T> findPaginatedBy(QueryFilter<T> filter, QuerySorter<T> sorter, int pageSize, int pageOffset)
            throws IllegalArgumentException;


    /**
     * Returns first entity being in the result of the query in compliance with passed filter and given sorter.
     * 
     * @param filter
     *            criteria of the query
     * @param sorter
     *            sorters applied to the results
     * @return first object being the result of query
     * @throws IllegalArgumentException
     *             thrown if any passed parameter is <code>null</code>.
     */
    T findFirstResultBy(QueryFilter<T> filter, QuerySorter<T> sorter)
            throws IllegalArgumentException;


    /**
     * Returns list of all entities sorted in compliance with the given sorter.
     * 
     * @param sorter
     *            sorters applied to the results
     * @return sorted list of all entities
     * @throws IllegalArgumentException
     *             thrown if passed sorter is <code>null</code>, ({@link #findAll()} should be used instead.
     */
    List<T> findAll(QuerySorter<T> sorter)
            throws IllegalArgumentException;

}
