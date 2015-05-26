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
 * General interface of objects creating sorters. Extending interfaces should declare specific methods fore each
 * argument by which entities must be sorted.
 * 
 * @param <T>
 *            entity class that will be managed by this sorter builder
 * 
 * @see QuerySorter
 */
public interface GenericQuerySorterBuilder<T> {

    /**
     * Builds sorter from previously added predicates.
     * 
     * @return sorting object
     */
    QuerySorter<T> buildSorter();


    /**
     * Returns an empty query sorter.
     * 
     * @return empty query sorter.
     */
    QuerySorter<T> getEmptySorter();
}
