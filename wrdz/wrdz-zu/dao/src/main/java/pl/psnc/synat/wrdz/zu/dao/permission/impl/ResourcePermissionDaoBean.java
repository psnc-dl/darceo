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

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.zu.dao.permission.ResourcePermissionDao;
import pl.psnc.synat.wrdz.zu.dao.permission.ResourcePermissionFilterFactory;
import pl.psnc.synat.wrdz.zu.dao.permission.ResourcePermissionSorterBuilder;
import pl.psnc.synat.wrdz.zu.entity.permission.ResourcePermission;

/**
 * A class managing the persistence of {@link ResourcePermission} class. It implements additional operations available
 * for {@link ResourcePermission} object (as defined in {@link ResourcePermissionDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ResourcePermissionDaoBean
        extends
        ExtendedGenericDaoBean<ResourcePermissionFilterFactory, ResourcePermissionSorterBuilder, ResourcePermission, Long>
        implements ResourcePermissionDao {

    /**
     * Creates new instance of ResourcePermissionDaoBean.
     */
    public ResourcePermissionDaoBean() {
        super(ResourcePermission.class);
    }


    @Override
    protected ResourcePermissionFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ResourcePermission> criteriaQuery, Root<ResourcePermission> root, Long epoch) {
        return new ResourcePermissionFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected ResourcePermissionSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ResourcePermission> criteriaQuery, Root<ResourcePermission> root, Long epoch) {
        return new ResourcePermissionSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
