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
package pl.psnc.synat.wrdz.ru.entity.services.descriptors;

import java.net.URL;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * 
 * Descriptor scheme identifying kind of semantic descriptor, e.g. OWL-S etc.
 */
@Entity
@DiscriminatorValue("SEMANTIC")
public class SemanticDescriptorScheme extends DescriptorScheme {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 358896285858976338L;


    /**
     * Constructs new, empty semantic descriptor scheme.
     */
    public SemanticDescriptorScheme() {
        super();
    }


    /**
     * Constructs new semantic descriptor scheme using specified name and namespace location.
     * 
     * @param name
     *            name of the scheme.
     * @param namespace
     *            namespace location.
     */
    public SemanticDescriptorScheme(String name, URL namespace) {
        super(name, namespace);
    }

}
