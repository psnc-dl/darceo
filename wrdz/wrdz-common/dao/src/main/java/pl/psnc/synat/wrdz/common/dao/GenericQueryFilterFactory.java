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

/**
 * 
 * General interface for objects creating filters, i.e. constraints on field values in entities. Defines basic logical
 * operations used for collapsing many filters into one.
 * 
 * @param <T>
 *            entity class that will be managed by this filter factory
 * 
 * @see QueryFilter
 */
public interface GenericQueryFilterFactory<T> {

    /**
     * Creates filter being a conjunction of passed filters.
     * 
     * @param filters
     *            filters to be conjuncted
     * @return conjunction of passed filters
     */
    QueryFilter<T> and(Iterable<QueryFilter<T>> filters);


    /**
     * Creates filter being a disjunction of passed filters.
     * 
     * @param filters
     *            filters to be disjuncted
     * @return disjunction of passed filters
     */
    QueryFilter<T> or(Iterable<QueryFilter<T>> filters);


    /**
     * Creates filter being a negation of passed filter.
     * 
     * @param filter
     *            filter to be negated
     * @return negation of passed filter
     */
    QueryFilter<T> not(QueryFilter<T> filter);


    /**
     * Creates filter being a conjunction of passed filters.
     * 
     * @param filter1
     *            filter being the first parameter of conjunction
     * @param filter2
     *            filter being the second parameter of conjunction
     * @param filters
     *            filters being the remaining parameters of conjunction
     * 
     * @return conjunction of passed filters
     */
    QueryFilter<T> and(QueryFilter<T> filter1, QueryFilter<T> filter2, QueryFilter<T>... filters);


    /**
     * Creates filter being a disjunction of passed filters.
     * 
     * @param filter1
     *            filter being the first parameter of disjunction
     * @param filter2
     *            filter being the second parameter of disjunction
     * @param filters
     *            filters being the remaining parameters of disjunction
     * 
     * @return disjunction of passed filters
     */
    QueryFilter<T> or(QueryFilter<T> filter1, QueryFilter<T> filter2, QueryFilter<T>... filters);

}
