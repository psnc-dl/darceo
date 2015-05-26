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
import pl.psnc.synat.wrdz.zmd.dao.object.validation.ValidationWarningDao;
import pl.psnc.synat.wrdz.zmd.dao.object.validation.ValidationWarningFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.validation.ValidationWarningSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.validation.ValidationWarning;

/**
 * A class managing the persistence of {@link ValidationWarning} class. It implements additional operations available
 * for {@link ValidationWarning} object (as defined in {@link ValidationWarningDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ValidationWarningDaoBean extends
        ExtendedGenericDaoBean<ValidationWarningFilterFactory, ValidationWarningSorterBuilder, ValidationWarning, Long>
        implements ValidationWarningDao {

    /**
     * Creates new instance of ValidationWarningDaoBean.
     */
    public ValidationWarningDaoBean() {
        super(ValidationWarning.class);
    }


    @Override
    protected ValidationWarningFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ValidationWarning> criteriaQuery, Root<ValidationWarning> root, Long epoch) {
        return new ValidationWarningFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected ValidationWarningSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ValidationWarning> criteriaQuery, Root<ValidationWarning> root, Long epoch) {
        return new ValidationWarningSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
