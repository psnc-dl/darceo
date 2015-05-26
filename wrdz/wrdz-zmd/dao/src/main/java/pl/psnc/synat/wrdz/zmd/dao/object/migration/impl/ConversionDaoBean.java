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
import pl.psnc.synat.wrdz.zmd.dao.object.migration.ConversionDao;
import pl.psnc.synat.wrdz.zmd.dao.object.migration.ConversionFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.migration.ConversionSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Conversion;

/**
 * A class managing the persistence of {@link Conversion} class. It implements additional operations available for
 * {@link Conversion} object (as defined in {@link ConversionDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ConversionDaoBean extends
        ExtendedGenericDaoBean<ConversionFilterFactory, ConversionSorterBuilder, Conversion, Long> implements
        ConversionDao {

    /**
     * Creates new instance of ConversionDaoBean.
     */
    public ConversionDaoBean() {
        super(Conversion.class);
    }


    @Override
    protected ConversionFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<Conversion> criteriaQuery, Root<Conversion> root, Long epoch) {
        return new ConversionFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected ConversionSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<Conversion> criteriaQuery, Root<Conversion> root, Long epoch) {
        return new ConversionSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
