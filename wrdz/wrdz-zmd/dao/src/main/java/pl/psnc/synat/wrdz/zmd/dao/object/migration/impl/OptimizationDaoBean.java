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
package pl.psnc.synat.wrdz.zmd.dao.object.migration.impl;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.zmd.dao.object.migration.OptimizationDao;
import pl.psnc.synat.wrdz.zmd.dao.object.migration.OptimizationFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.migration.OptimizationSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Optimization;

/**
 * A class managing the persistence of {@link Optimization} class. It implements additional operations available for
 * {@link Optimization} object (as defined in {@link OptimizationDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class OptimizationDaoBean extends
        ExtendedGenericDaoBean<OptimizationFilterFactory, OptimizationSorterBuilder, Optimization, Long> implements
        OptimizationDao {

    /**
     * Creates new instance of OptimizationDaoBean.
     */
    public OptimizationDaoBean() {
        super(Optimization.class);
    }


    @Override
    protected OptimizationFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<Optimization> criteriaQuery, Root<Optimization> root, Long epoch) {
        return new OptimizationFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected OptimizationSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<Optimization> criteriaQuery, Root<Optimization> root, Long epoch) {
        return new OptimizationSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
