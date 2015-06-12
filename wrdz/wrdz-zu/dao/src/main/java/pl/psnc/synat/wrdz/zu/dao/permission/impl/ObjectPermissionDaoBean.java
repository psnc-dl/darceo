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
import pl.psnc.synat.wrdz.zu.dao.permission.ObjectPermissionDao;
import pl.psnc.synat.wrdz.zu.dao.permission.ObjectPermissionFilterFactory;
import pl.psnc.synat.wrdz.zu.dao.permission.ObjectPermissionSorterBuilder;
import pl.psnc.synat.wrdz.zu.entity.permission.ObjectPermission;
import pl.psnc.synat.wrdz.zu.entity.permission.ObjectPermission_;
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication_;
import pl.psnc.synat.wrdz.zu.types.ObjectPermissionType;

/**
 * A class managing the persistence of {@link ObjectPermission} class. It implements additional operations available for
 * {@link ObjectPermission} object (as defined in {@link ObjectPermissionDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ObjectPermissionDaoBean extends
        ExtendedGenericDaoBean<ObjectPermissionFilterFactory, ObjectPermissionSorterBuilder, ObjectPermission, Long>
        implements ObjectPermissionDao {

    /**
     * Creates new instance of ObjectPermissionDaoBean.
     */
    public ObjectPermissionDaoBean() {
        super(ObjectPermission.class);
    }


    @Override
    protected ObjectPermissionFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ObjectPermission> criteriaQuery, Root<ObjectPermission> root, Long epoch) {
        return new ObjectPermissionFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected ObjectPermissionSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ObjectPermission> criteriaQuery, Root<ObjectPermission> root, Long epoch) {
        return new ObjectPermissionSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public List<Long> findObjectsWithPermission(List<Long> groups, ObjectPermissionType permissionType) {
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<ObjectPermission> root = criteriaQuery.from(ObjectPermission.class);
        Predicate byGroups = root.get(ObjectPermission_.group).get(GroupAuthentication_.id).in(groups);
        Predicate byType = criteriaBuilder.equal(root.get(ObjectPermission_.permission), permissionType);
        criteriaQuery.where(criteriaBuilder.and(byGroups, byType));
        criteriaQuery.select(root.get(ObjectPermission_.resourceId)).distinct(true);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

}
