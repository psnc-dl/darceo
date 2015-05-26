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
package pl.psnc.synat.wrdz.ru.entity.types;

import java.net.URL;

import pl.psnc.synat.wrdz.ru.entity.services.descriptors.DescriptorScheme;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptorScheme;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.TechnicalDescriptorScheme;

/**
 * Descriptor types.
 */
public enum DescriptorType {

    /**
     * Technical descriptor.
     */
    TECHNICAL {

        @Override
        public TechnicalDescriptorScheme create(String name, URL namespace) {
            return new TechnicalDescriptorScheme(name, namespace);
        }
    },
    /**
     * Semantic descriptor.
     */
    SEMANTIC {

        @Override
        public SemanticDescriptorScheme create(String name, URL namespace) {
            return new SemanticDescriptorScheme(name, namespace);
        }
    };

    /**
     * Creates new instance of a concrete subtype of abstract class {@link DescriptorScheme} described by the enum
     * constant.
     * 
     * @param name
     *            name of the scheme.
     * @param namespace
     *            namespace of the scheme.
     * @return new instance of the specific type of descriptor scheme.
     */
    public abstract DescriptorScheme create(String name, URL namespace);

}
