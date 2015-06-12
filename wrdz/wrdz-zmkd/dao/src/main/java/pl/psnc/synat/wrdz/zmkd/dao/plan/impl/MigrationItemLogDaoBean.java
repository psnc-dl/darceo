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
import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationItemLogDao;
import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationItemLogFilterFactory;
import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationItemLogSorterBuilder;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationItemLog;

/**
 * A class managing the persistence of {@link MigrationItemLog} class. It implements additional operations available for
 * {@link MigrationItemLog} object (as defined in {@link MigrationItemLogDao} ).
 * 
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class MigrationItemLogDaoBean extends
        ExtendedGenericDaoBean<MigrationItemLogFilterFactory, MigrationItemLogSorterBuilder, MigrationItemLog, Long>
        implements MigrationItemLogDao {

    /**
     * Creates new instance of MigrationItemLogDaoBean.
     */
    public MigrationItemLogDaoBean() {
        super(MigrationItemLog.class);
    }


    @Override
    protected MigrationItemLogFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<MigrationItemLog> criteriaQuery, Root<MigrationItemLog> root, Long epoch) {
        return new MigrationItemLogFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected MigrationItemLogSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<MigrationItemLog> criteriaQuery, Root<MigrationItemLog> root, Long epoch) {
        return new MigrationItemLogSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
