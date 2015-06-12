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

import javax.ejb.EJB;
import javax.ejb.Stateless;

import pl.psnc.synat.wrdz.ru.dao.services.descriptors.DescriptorSchemeDao;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.DescriptorScheme;
import pl.psnc.synat.wrdz.ru.entity.types.DescriptorType;
import pl.psnc.synat.wrdz.ru.exceptions.EntryCreationException;

/**
 * Class managing {@link DescriptorScheme} entities basic operations.
 */
@Stateless
public class DescriptorSchemeManagerBean implements DescriptorSchemeManager {

    /**
     * Descriptor scheme DAO for persistence operations.
     */
    @EJB
    private DescriptorSchemeDao descriptorSchemeDao;


    @Override
    public DescriptorScheme createScheme(String name, String version, URL namespace, DescriptorType type)
            throws EntryCreationException {
        if (name == null || namespace == null || type == null) {
            throw new EntryCreationException("Cannot create scheme, at least one of the required parameters was null.");
        }
        DescriptorScheme result = type.create(name, namespace);
        result.setVersion(version);
        descriptorSchemeDao.persist(result);
        return result;
    }


    @Override
    public DescriptorScheme modifyScheme(DescriptorScheme scheme, String name, String version, URL namespace) {
        scheme.setName(name);
        scheme.setVersion(version);
        scheme.setNamespace(namespace.toString());
        return null;
    }

}
