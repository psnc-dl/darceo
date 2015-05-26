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
import pl.psnc.synat.wrdz.zmd.dao.object.migration.MigrationDao;
import pl.psnc.synat.wrdz.zmd.dao.object.migration.MigrationFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.migration.MigrationSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration;

/**
 * A class managing the persistence of {@link Migration} class. It implements additional operations available for
 * {@link Migration} object (as defined in {@link MigrationDao} ).
 */
@SuppressWarnings("rawtypes")
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class MigrationDaoBean extends
        ExtendedGenericDaoBean<MigrationFilterFactory, MigrationSorterBuilder, Migration, Long> implements MigrationDao {

    /**
     * Creates new instance of MigrationDaoBean.
     */
    public MigrationDaoBean() {
        super(Migration.class);
    }


    @Override
    protected MigrationFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<Migration> criteriaQuery, Root<Migration> root, Long epoch) {
        return new MigrationFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected MigrationSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<Migration> criteriaQuery, Root<Migration> root, Long epoch) {
        return new MigrationSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
