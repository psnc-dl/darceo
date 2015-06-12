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
 * Basic implementation of the {@link QueryModifier}, that stores references to the creators that can modify the same
 * query.
 * 
 * @param <FF>
 *            type of filter factory that manages entities of type T
 * @param <SB>
 *            type of sorter builder that manages entities of type T
 * @param <T>
 *            entity class that will be managed by this query modifier
 * 
 * @see QueryModifier
 */
public class QueryModifierImpl<FF extends GenericQueryFilterFactory<T>, SB extends GenericQuerySorterBuilder<T>, T>
        implements QueryModifier<FF, SB, T> {

    /**
     * Filter factory.
     */
    private final FF filterFactory;

    /**
     * Sorter builder.
     */
    private final SB sorterBuilder;


    /**
     * Constructs new query modifier given the filter factory and sorter builder.
     * 
     * @param filterFactory
     *            filter factory upon which this instance will be build.
     * @param sorterBuilder
     *            sorter builder upon which this instance will be build.
     */
    public QueryModifierImpl(FF filterFactory, SB sorterBuilder) {
        this.filterFactory = filterFactory;
        this.sorterBuilder = sorterBuilder;
    }


    @Override
    public FF getQueryFilterFactory() {
        return filterFactory;
    }


    @Override
    public SB getQuerySorterBuilder() {
        return sorterBuilder;
    }
}
