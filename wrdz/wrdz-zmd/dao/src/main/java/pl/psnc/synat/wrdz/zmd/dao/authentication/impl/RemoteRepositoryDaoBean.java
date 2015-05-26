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
package pl.psnc.synat.wrdz.zmd.dao.authentication.impl;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.zmd.dao.authentication.RemoteRepositoryDao;
import pl.psnc.synat.wrdz.zmd.dao.authentication.RemoteRepositoryFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.authentication.RemoteRepositorySorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.authentication.RemoteRepository;

/**
 * A class managing the persistence of {@link RemoteRepository} class. It implements additional operations available for
 * {@link RemoteRepository} object (as defined in {@link RemoteRepositoryDao}).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class RemoteRepositoryDaoBean extends
        ExtendedGenericDaoBean<RemoteRepositoryFilterFactory, RemoteRepositorySorterBuilder, RemoteRepository, Long>
        implements RemoteRepositoryDao {

    /**
     * Creates new instance of RemoteRepositoryDaoBean.
     */
    public RemoteRepositoryDaoBean() {
        super(RemoteRepository.class);
    }


    @Override
    protected RemoteRepositoryFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<RemoteRepository> criteriaQuery, Root<RemoteRepository> root, Long epoch) {
        return new RemoteRepositoryFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected RemoteRepositorySorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<RemoteRepository> criteriaQuery, Root<RemoteRepository> root, Long epoch) {
        return new RemoteRepositorySorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
