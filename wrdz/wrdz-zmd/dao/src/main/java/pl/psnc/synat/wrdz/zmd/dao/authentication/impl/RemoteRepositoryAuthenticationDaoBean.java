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
import pl.psnc.synat.wrdz.zmd.dao.authentication.RemoteRepositoryAuthenticationDao;
import pl.psnc.synat.wrdz.zmd.dao.authentication.RemoteRepositoryAuthenticationFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.authentication.RemoteRepositoryAuthenticationSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.authentication.RemoteRepositoryAuthentication;

/**
 * A class managing the persistence of {@link RemoteRepositoryAuthentication} class. It implements additional operations
 * available for {@link RemoteRepositoryAuthentication} object (as defined in {@link RemoteRepositoryAuthenticationDao}
 * ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class RemoteRepositoryAuthenticationDaoBean
        extends
        ExtendedGenericDaoBean<RemoteRepositoryAuthenticationFilterFactory, RemoteRepositoryAuthenticationSorterBuilder, RemoteRepositoryAuthentication, Long>
        implements RemoteRepositoryAuthenticationDao {

    /**
     * Creates new instance of RemoteRepositoryAuthenticationDaoBean.
     */
    public RemoteRepositoryAuthenticationDaoBean() {
        super(RemoteRepositoryAuthentication.class);
    }


    @Override
    protected RemoteRepositoryAuthenticationFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<RemoteRepositoryAuthentication> criteriaQuery, Root<RemoteRepositoryAuthentication> root,
            Long epoch) {
        return new RemoteRepositoryAuthenticationFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected RemoteRepositoryAuthenticationSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<RemoteRepositoryAuthentication> criteriaQuery, Root<RemoteRepositoryAuthentication> root,
            Long epoch) {
        return new RemoteRepositoryAuthenticationSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
