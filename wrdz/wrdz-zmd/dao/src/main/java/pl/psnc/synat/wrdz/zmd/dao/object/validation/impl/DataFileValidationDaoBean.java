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
package pl.psnc.synat.wrdz.zmd.dao.object.validation.impl;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.zmd.dao.object.validation.DataFileValidationDao;
import pl.psnc.synat.wrdz.zmd.dao.object.validation.DataFileValidationFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.validation.DataFileValidationSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.validation.DataFileValidation;

/**
 * A class managing the persistence of {@link DataFileValidation} class. It implements additional operations available
 * for {@link DataFileValidation} object (as defined in {@link DataFileValidationDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class DataFileValidationDaoBean
        extends
        ExtendedGenericDaoBean<DataFileValidationFilterFactory, DataFileValidationSorterBuilder, DataFileValidation, Long>
        implements DataFileValidationDao {

    /**
     * Creates new instance of DataFileValidationDaoBean.
     */
    public DataFileValidationDaoBean() {
        super(DataFileValidation.class);
    }


    @Override
    protected DataFileValidationFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DataFileValidation> criteriaQuery, Root<DataFileValidation> root, Long epoch) {
        return new DataFileValidationFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected DataFileValidationSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DataFileValidation> criteriaQuery, Root<DataFileValidation> root, Long epoch) {
        return new DataFileValidationSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
