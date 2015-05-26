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

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.zu.dao.permission.GroupManagementPermissionDao;
import pl.psnc.synat.wrdz.zu.dao.permission.GroupManagementPermissionFilterFactory;
import pl.psnc.synat.wrdz.zu.dao.permission.GroupManagementPermissionSorterBuilder;
import pl.psnc.synat.wrdz.zu.entity.permission.GroupManagementPermission;
import pl.psnc.synat.wrdz.zu.entity.permission.GroupManagementPermission_;
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication_;
import pl.psnc.synat.wrdz.zu.types.ManagementPermissionType;

/**
 * A class managing the persistence of {@link GroupManagementPermission} class. It implements additional operations
 * available for {@link GroupManagementPermission} object (as defined in {@link GroupManagementPermissionDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class GroupManagementPermissionDaoBean
        extends
        ExtendedGenericDaoBean<GroupManagementPermissionFilterFactory, GroupManagementPermissionSorterBuilder, GroupManagementPermission, Long>
        implements GroupManagementPermissionDao {

    /**
     * Creates new instance of GroupManagementPermissionDaoBean.
     */
    public GroupManagementPermissionDaoBean() {
        super(GroupManagementPermission.class);
    }


    @Override
    protected GroupManagementPermissionFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<GroupManagementPermission> criteriaQuery, Root<GroupManagementPermission> root, Long epoch) {
        return new GroupManagementPermissionFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected GroupManagementPermissionSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<GroupManagementPermission> criteriaQuery, Root<GroupManagementPermission> root, Long epoch) {
        return new GroupManagementPermissionSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public List<Long> findGroupsWithPermission(List<Long> groups, ManagementPermissionType permissionType) {
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<GroupManagementPermission> root = criteriaQuery.from(GroupManagementPermission.class);
        Predicate byGroups = root.get(GroupManagementPermission_.group).get(GroupAuthentication_.id).in(groups);
        Predicate byType = criteriaBuilder.equal(root.get(GroupManagementPermission_.permission), permissionType);
        criteriaQuery.where(criteriaBuilder.and(byGroups, byType));
        criteriaQuery.select(root.get(GroupManagementPermission_.resourceId)).distinct(true);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

}
