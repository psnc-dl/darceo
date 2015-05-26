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
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.DescriptorSchemeDao;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.DescriptorSchemeFilterFactory;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.DescriptorSchemeSorterBuilder;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.DescriptorScheme;

/**
 * A class managing the persistence of {@link DescriptorScheme} class. It implements additional operations available for
 * {@link DescriptorScheme} object (as defined in {@link DescriptorSchemeDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class DescriptorSchemeDaoBean extends
        ExtendedGenericDaoBean<DescriptorSchemeFilterFactory, DescriptorSchemeSorterBuilder, DescriptorScheme, Long>
        implements DescriptorSchemeDao {

    /**
     * Creates new instance of DescriptorSchemeDaoBean.
     */
    public DescriptorSchemeDaoBean() {
        super(DescriptorScheme.class);
    }


    @Override
    protected DescriptorSchemeFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DescriptorScheme> criteriaQuery, Root<DescriptorScheme> root, Long epoch) {
        return new DescriptorSchemeFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected DescriptorSchemeSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DescriptorScheme> criteriaQuery, Root<DescriptorScheme> root, Long epoch) {
        return new DescriptorSchemeSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
