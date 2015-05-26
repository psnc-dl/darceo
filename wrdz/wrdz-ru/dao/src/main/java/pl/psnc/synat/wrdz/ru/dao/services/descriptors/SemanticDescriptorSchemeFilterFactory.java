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
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptorScheme;

/**
 * Specified set of filters for queries concerning {@link SemanticDescriptorScheme} entities.
 */
public interface SemanticDescriptorSchemeFilterFactory extends GenericQueryFilterFactory<SemanticDescriptorScheme> {

    /**
     * Filters the semantic descriptors schemes by their names. Can use Java RegExp syntax.
     * 
     * @param name
     *            name of semantic descriptor scheme.
     * @return produced filter.
     */
    QueryFilter<SemanticDescriptorScheme> byName(String name);


    /**
     * Filters the semantic descriptors schemes by their versions. Can use Java RegExp syntax.
     * 
     * @param version
     *            version of semantic descriptor scheme.
     * @return produced filter.
     */
    QueryFilter<SemanticDescriptorScheme> byVersion(String version);


    /**
     * Filters the semantic descriptors schemes by their namespaces location. Can use Java RegExp syntax.
     * 
     * @param namespace
     *            main namespace defining semantic descriptor scheme.
     * @return produced filter.
     */
    QueryFilter<SemanticDescriptorScheme> byNamespace(String namespace);

}
