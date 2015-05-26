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
package pl.psnc.synat.wrdz.zmkd.dao.plan.impl;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationPlanDao;
import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationPlanFilterFactory;
import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationPlanSorterBuilder;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlan;

/**
 * A class managing the persistence of {@link MigrationPlan} class. It implements additional operations available for
 * {@link MigrationPlan} object (as defined in {@link MigrationPlanDao} ).
 * 
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class MigrationPlanDaoBean extends
        ExtendedGenericDaoBean<MigrationPlanFilterFactory, MigrationPlanSorterBuilder, MigrationPlan, Long> implements
        MigrationPlanDao {

    /**
     * Creates new instance of MigrationPlanDaoBean.
     */
    public MigrationPlanDaoBean() {
        super(MigrationPlan.class);
    }


    @Override
    protected MigrationPlanFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<MigrationPlan> criteriaQuery, Root<MigrationPlan> root, Long epoch) {
        return new MigrationPlanFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected MigrationPlanSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<MigrationPlan> criteriaQuery, Root<MigrationPlan> root, Long epoch) {
        return new MigrationPlanSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
