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
package pl.psnc.synat.wrdz.ru.services.descriptors;

import java.net.URL;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.ru.entity.services.descriptors.DescriptorScheme;
import pl.psnc.synat.wrdz.ru.entity.types.DescriptorType;
import pl.psnc.synat.wrdz.ru.exceptions.EntryCreationException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryModificationException;

/**
 * Interface describing contract of classes managing {@link DescriptorScheme} entities.
 */
@Local
public interface DescriptorSchemeManager {

    /**
     * Creates new descriptor scheme using passed data.
     * 
     * @param name
     *            name of the scheme.
     * @param version
     *            scheme version.
     * @param namespace
     *            namespace location.
     * @param type
     *            scheme type.
     * @return newly created scheme.
     * @throws EntryCreationException
     *             should creation fail.
     */
    DescriptorScheme createScheme(String name, String version, URL namespace, DescriptorType type)
            throws EntryCreationException;


    /**
     * Modifies the existing scheme using passed data.
     * 
     * @param scheme
     *            modified scheme.
     * @param name
     *            name of the scheme.
     * @param version
     *            scheme version.
     * @param namespace
     *            namespace location.
     * @return modified scheme.
     * @throws EntryModificationException
     *             should modification fail.
     */
    DescriptorScheme modifyScheme(DescriptorScheme scheme, String name, String version, URL namespace)
            throws EntryModificationException;

}
