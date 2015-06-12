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
package pl.psnc.synat.wrdz.zu.dao.user.impl;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.zu.dao.user.UserAuthenticationDao;
import pl.psnc.synat.wrdz.zu.dao.user.UserAuthenticationFilterFactory;
import pl.psnc.synat.wrdz.zu.dao.user.UserAuthenticationSorterBuilder;
import pl.psnc.synat.wrdz.zu.entity.user.User;
import pl.psnc.synat.wrdz.zu.entity.user.UserAuthentication;

/**
 * A class managing the persistence of {@link UserAuthentication} class. It implements additional operations available
 * for {@link UserAuthentication} object (as defined in {@link UserAuthenticationDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class UserAuthenticationDaoBean
        extends
        ExtendedGenericDaoBean<UserAuthenticationFilterFactory, UserAuthenticationSorterBuilder, UserAuthentication, User>
        implements UserAuthenticationDao {

    /**
     * Creates new instance of UserAuthenticationDaoBean.
     */
    public UserAuthenticationDaoBean() {
        super(UserAuthentication.class);
    }


    @Override
    protected UserAuthenticationFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<UserAuthentication> criteriaQuery, Root<UserAuthentication> root, Long epoch) {
        return new UserAuthenticationFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected UserAuthenticationSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<UserAuthentication> criteriaQuery, Root<UserAuthentication> root, Long epoch) {
        return new UserAuthenticationSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
