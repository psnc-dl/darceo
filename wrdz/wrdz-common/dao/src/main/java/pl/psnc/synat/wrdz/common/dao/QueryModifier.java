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
 * <p>
 * This interface defines the contract of classes allowing to modify given query.
 * </p>
 * <p>
 * To ensure that all parameters are derived from and concern the same query it is crucial to create them at the same
 * time using the same objects (factories and builders) parameterized by the same persistence context dependent objects.
 * This interface defines the structures that defines this contract.
 * </p>
 * <p>
 * Following the concept presented in above paragraph, creators returned by this interface must be marked bu some unique
 * value indicating their common origin. This is done by the epoch counter - creators connected by common origin get the
 * same, unique value which is also used to mark the objects created by them. Thus it is easy to check if the parts of
 * the query belong to the same query (marked by epoch value).
 * </p>
 * 
 * @param <FF>
 *            type of filter factory that manages entities of type T
 * @param <SB>
 *            type of sorter builder that manages entities of type T
 * @param <T>
 *            entity class that will be managed by this query modifier
 */
public interface QueryModifier<FF extends GenericQueryFilterFactory<T>, SB extends GenericQuerySorterBuilder<T>, T> {

    /**
     * Returns underlying filter factory.
     * 
     * @return filter factory.
     */
    FF getQueryFilterFactory();


    /**
     * Returns underlying sorter builder.
     * 
     * @return sorter builder.
     */
    SB getQuerySorterBuilder();
}
