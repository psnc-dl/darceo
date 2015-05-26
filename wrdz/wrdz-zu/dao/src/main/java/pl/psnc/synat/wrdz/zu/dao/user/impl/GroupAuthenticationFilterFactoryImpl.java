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

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zu.dao.user.GroupAuthenticationFilterFactory;
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication;
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication_;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning {@link GroupAuthentication}
 * .
 */
public class GroupAuthenticationFilterFactoryImpl extends GenericQueryFilterFactoryImpl<GroupAuthentication> implements
        GroupAuthenticationFilterFactory {

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
    public GroupAuthenticationFilterFactoryImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<GroupAuthentication> criteriaQuery, Root<GroupAuthentication> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<GroupAuthentication> byIdentifiers(List<Long> identifiers) {
        Predicate predicate = root.get(GroupAuthentication_.id).in(identifiers);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<GroupAuthentication> byGroupName(String name) {
        Predicate predicate = criteriaBuilder.equal(root.get(GroupAuthentication_.groupname), name);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<GroupAuthentication> bySingleUser(boolean singleUser) {
        Predicate predicate = criteriaBuilder.equal(root.get(GroupAuthentication_.singleUser), singleUser);
        return constructQueryFilter(predicate);
    }
}
