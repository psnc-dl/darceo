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
import pl.psnc.synat.wrdz.zu.dao.user.UserDao;
import pl.psnc.synat.wrdz.zu.dao.user.UserFilterFactory;
import pl.psnc.synat.wrdz.zu.dao.user.UserSorterBuilder;
import pl.psnc.synat.wrdz.zu.entity.user.User;

/**
 * A class managing the persistence of {@link User} class. It implements additional operations available for
 * {@link User} object (as defined in {@link UserDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class UserDaoBean extends ExtendedGenericDaoBean<UserFilterFactory, UserSorterBuilder, User, Long> implements
        UserDao {

    /**
     * Creates new instance of UserDaoBean.
     */
    public UserDaoBean() {
        super(User.class);
    }


    @Override
    protected UserFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<User> criteriaQuery, Root<User> root, Long epoch) {
        return new UserFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected UserSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<User> criteriaQuery, Root<User> root, Long epoch) {
        return new UserSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
