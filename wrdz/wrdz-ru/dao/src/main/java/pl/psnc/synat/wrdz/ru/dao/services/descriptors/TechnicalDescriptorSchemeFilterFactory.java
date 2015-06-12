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
package pl.psnc.synat.wrdz.ru.dao.services.descriptors;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.TechnicalDescriptorScheme;

/**
 * Specified set of filters for queries concerning {@link TechnicalDescriptorScheme} entities.
 */
public interface TechnicalDescriptorSchemeFilterFactory extends GenericQueryFilterFactory<TechnicalDescriptorScheme> {

    /**
     * Constructs new filter filtering by name of the descriptor scheme.
     * 
     * @param name
     *            name of the scheme.
     * @return constructed filter.
     */
    QueryFilter<TechnicalDescriptorScheme> byName(String name);


    /**
     * Constructs new filter filtering by version of the descriptor scheme.
     * 
     * @param version
     *            version of the scheme.
     * @return constructed filter.
     */
    QueryFilter<TechnicalDescriptorScheme> byVersion(String version);

}
