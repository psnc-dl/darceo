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
package pl.psnc.synat.wrdz.ru.dao.services.descriptors.impl;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.TechnicalDescriptorSchemeDao;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.TechnicalDescriptorSchemeFilterFactory;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.TechnicalDescriptorSchemeSorterBuilder;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.TechnicalDescriptorScheme;

/**
 * A class managing the persistence of {@link TechnicalDescriptorScheme} class. It implements additional operations
 * available for {@link TechnicalDescriptorScheme} object (as defined in {@link TechnicalDescriptorSchemeDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class TechnicalDescriptorSchemeDaoBean
        extends
        ExtendedGenericDaoBean<TechnicalDescriptorSchemeFilterFactory, TechnicalDescriptorSchemeSorterBuilder, TechnicalDescriptorScheme, Long>
        implements TechnicalDescriptorSchemeDao {

    /**
     * Creates new instance of TechnicalDescriptorSchemeDaoBean.
     */
    public TechnicalDescriptorSchemeDaoBean() {
        super(TechnicalDescriptorScheme.class);
    }


    @Override
    protected TechnicalDescriptorSchemeFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<TechnicalDescriptorScheme> criteriaQuery, Root<TechnicalDescriptorScheme> root, Long epoch) {
        return new TechnicalDescriptorSchemeFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected TechnicalDescriptorSchemeSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<TechnicalDescriptorScheme> criteriaQuery, Root<TechnicalDescriptorScheme> root, Long epoch) {
        return new TechnicalDescriptorSchemeSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
