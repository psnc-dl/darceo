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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationPlanFilterFactory;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlan;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlanStatus;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlan_;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning {@link MigrationPlan}.
 * 
 */
public class MigrationPlanFilterFactoryImpl extends GenericQueryFilterFactoryImpl<MigrationPlan> implements
        MigrationPlanFilterFactory {

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
    public MigrationPlanFilterFactoryImpl(CriteriaBuilder criteriaBuilder, CriteriaQuery<MigrationPlan> criteriaQuery,
            Root<MigrationPlan> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<MigrationPlan> byOwnerId(Long ownerId) {
        Predicate predicate = criteriaBuilder.equal(root.get(MigrationPlan_.ownerId), ownerId);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<MigrationPlan> byStatus(MigrationPlanStatus status) {
        Predicate predicate = criteriaBuilder.equal(root.get(MigrationPlan_.status), status);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<MigrationPlan> byObjectIdentifierAwaited(String objectIdentifier) {
        Predicate predicate = criteriaBuilder.equal(root.get(MigrationPlan_.objectIdentifierAwaited), objectIdentifier);
        return constructQueryFilter(predicate);
    }

}
