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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zu.dao.user.UserAuthenticationFilterFactory;
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication;
import pl.psnc.synat.wrdz.zu.entity.user.UserAuthentication;
import pl.psnc.synat.wrdz.zu.entity.user.UserAuthentication_;
import pl.psnc.synat.wrdz.zu.entity.user.User_;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning {@link UserAuthentication}.
 * 
 */
public class UserAuthenticationFilterFactoryImpl extends GenericQueryFilterFactoryImpl<UserAuthentication> implements
        UserAuthenticationFilterFactory {

    /**
     * Constructs this factory initializing it with required arguments.
     * 
     * @param criteriaBuilder
     *            reference to {@link CriteriaBuilder} object.
     * @param criteriaQuery
     *            query upon which filters will be build
     * @param root
     *            object representing root type of the entity this filter factory manages
     * @param epoch
     *            builder's epoch
     * 
     * @throws IllegalArgumentException
     *             thrown if one of arguments is <code>null</code>
     * 
     */
    public UserAuthenticationFilterFactoryImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<UserAuthentication> criteriaQuery, Root<UserAuthentication> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<UserAuthentication> byUsername(String username) {
        Predicate predicate = criteriaBuilder.like(root.get(UserAuthentication_.user).get(User_.username), username);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<UserAuthentication> byGroup(GroupAuthentication group) {
        Predicate predicate = criteriaBuilder.isMember(group, (root.get(UserAuthentication_.groups)));
        return constructQueryFilter(predicate);
    }
}
