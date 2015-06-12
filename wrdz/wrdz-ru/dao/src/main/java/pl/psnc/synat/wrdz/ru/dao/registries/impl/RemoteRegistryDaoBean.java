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
package pl.psnc.synat.wrdz.ru.dao.registries.impl;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.ru.dao.registries.RemoteRegistryDao;
import pl.psnc.synat.wrdz.ru.dao.registries.RemoteRegistryFilterFactory;
import pl.psnc.synat.wrdz.ru.dao.registries.RemoteRegistrySorterBuilder;
import pl.psnc.synat.wrdz.ru.entity.registries.RemoteRegistry;

/**
 * A class managing the persistence of {@link RemoteRegistry} class. It implements additional operations available for
 * {@link RemoteRegistry} object (as defined in {@link RemoteRegistryDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class RemoteRegistryDaoBean extends
        ExtendedGenericDaoBean<RemoteRegistryFilterFactory, RemoteRegistrySorterBuilder, RemoteRegistry, Long>
        implements RemoteRegistryDao {

    /**
     * Creates new instance of RemoteRegistryDaoBean.
     */
    public RemoteRegistryDaoBean() {
        super(RemoteRegistry.class);
    }


    @Override
    protected RemoteRegistryFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<RemoteRegistry> criteriaQuery, Root<RemoteRegistry> root, Long epoch) {
        return new RemoteRegistryFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected RemoteRegistrySorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<RemoteRegistry> criteriaQuery, Root<RemoteRegistry> root, Long epoch) {
        return new RemoteRegistrySorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
