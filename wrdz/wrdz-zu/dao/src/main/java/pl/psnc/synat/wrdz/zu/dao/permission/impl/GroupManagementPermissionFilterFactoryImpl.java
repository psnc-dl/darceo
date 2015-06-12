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
package pl.psnc.synat.wrdz.zu.dao.permission.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zu.dao.permission.GroupManagementPermissionFilterFactory;
import pl.psnc.synat.wrdz.zu.entity.permission.GroupManagementPermission;
import pl.psnc.synat.wrdz.zu.entity.permission.GroupManagementPermission_;
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication_;
import pl.psnc.synat.wrdz.zu.types.ManagementPermissionType;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning
 * {@link GroupManagementPermission}.
 */
public class GroupManagementPermissionFilterFactoryImpl extends
        GenericQueryFilterFactoryImpl<GroupManagementPermission> implements GroupManagementPermissionFilterFactory {

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
    public GroupManagementPermissionFilterFactoryImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<GroupManagementPermission> criteriaQuery, Root<GroupManagementPermission> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<GroupManagementPermission> byGroupIn(List<Long> groups) {
        Predicate predicate = root.get(GroupManagementPermission_.group).get(GroupAuthentication_.id).in(groups);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<GroupManagementPermission> byGroupSingleUser(boolean singleUser) {
        Predicate predicate = criteriaBuilder.equal(
            root.get(GroupManagementPermission_.group).get(GroupAuthentication_.singleUser), singleUser);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<GroupManagementPermission> byPermissionType(ManagementPermissionType permissionType) {
        Predicate predicate = criteriaBuilder.equal(root.get(GroupManagementPermission_.permission), permissionType);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<GroupManagementPermission> byResourceId(Long id) {
        Predicate predicate = null;
        if (id == null) {
            predicate = criteriaBuilder.isNull(root.get(GroupManagementPermission_.resourceId));
        } else {
            predicate = criteriaBuilder.equal(root.get(GroupManagementPermission_.resourceId), id);
        }
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<GroupManagementPermission> byGroupId(Long id) {
        Predicate predicate = criteriaBuilder.equal(
            root.get(GroupManagementPermission_.group).get(GroupAuthentication_.id), id);
        return constructQueryFilter(predicate);
    }

}
