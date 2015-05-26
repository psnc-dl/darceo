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
import pl.psnc.synat.wrdz.zu.dao.user.OrganizationDao;
import pl.psnc.synat.wrdz.zu.dao.user.OrganizationFilterFactory;
import pl.psnc.synat.wrdz.zu.dao.user.OrganizationSorterBuilder;
import pl.psnc.synat.wrdz.zu.entity.user.Organization;

/**
 * A class managing the persistence of {@link Organization} class. It implements additional operations available for
 * {@link Organization} object (as defined in {@link OrganizationDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class OrganizationDaoBean extends
        ExtendedGenericDaoBean<OrganizationFilterFactory, OrganizationSorterBuilder, Organization, Long> implements
        OrganizationDao {

    /**
     * Creates new instance of OrganizationDaoBean.
     */
    public OrganizationDaoBean() {
        super(Organization.class);
    }


    @Override
    protected OrganizationFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<Organization> criteriaQuery, Root<Organization> root, Long epoch) {
        return new OrganizationFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected OrganizationSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<Organization> criteriaQuery, Root<Organization> root, Long epoch) {
        return new OrganizationSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
