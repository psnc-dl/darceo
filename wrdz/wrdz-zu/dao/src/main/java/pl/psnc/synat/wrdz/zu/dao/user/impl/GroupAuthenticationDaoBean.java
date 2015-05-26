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
import pl.psnc.synat.wrdz.zu.dao.user.GroupAuthenticationDao;
import pl.psnc.synat.wrdz.zu.dao.user.GroupAuthenticationFilterFactory;
import pl.psnc.synat.wrdz.zu.dao.user.GroupAuthenticationSorterBuilder;
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication;

/**
 * A class managing the persistence of {@link GroupAuthentication} class. It implements additional operations available
 * for {@link GroupAuthentication} object (as defined in {@link GroupAuthenticationDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class GroupAuthenticationDaoBean
        extends
        ExtendedGenericDaoBean<GroupAuthenticationFilterFactory, GroupAuthenticationSorterBuilder, GroupAuthentication, Long>
        implements GroupAuthenticationDao {

    /**
     * Creates new instance of GroupAuthenticationDaoBean.
     */
    public GroupAuthenticationDaoBean() {
        super(GroupAuthentication.class);
    }


    @Override
    protected GroupAuthenticationFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<GroupAuthentication> criteriaQuery, Root<GroupAuthentication> root, Long epoch) {
        return new GroupAuthenticationFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected GroupAuthenticationSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<GroupAuthentication> criteriaQuery, Root<GroupAuthentication> root, Long epoch) {
        return new GroupAuthenticationSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
