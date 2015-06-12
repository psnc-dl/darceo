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
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.SemanticDescriptorSchemeDao;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.SemanticDescriptorSchemeFilterFactory;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.SemanticDescriptorSchemeSorterBuilder;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptorScheme;

/**
 * A class managing the persistence of {@link SemanticDescriptorScheme} class. It implements additional operations
 * available for {@link SemanticDescriptorScheme} object (as defined in {@link SemanticDescriptorSchemeDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class SemanticDescriptorSchemeDaoBean
        extends
        ExtendedGenericDaoBean<SemanticDescriptorSchemeFilterFactory, SemanticDescriptorSchemeSorterBuilder, SemanticDescriptorScheme, Long>
        implements SemanticDescriptorSchemeDao {

    /**
     * Creates new instance of SemanticDescriptorSchemeDaoBean.
     */
    public SemanticDescriptorSchemeDaoBean() {
        super(SemanticDescriptorScheme.class);
    }


    @Override
    protected SemanticDescriptorSchemeFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<SemanticDescriptorScheme> criteriaQuery, Root<SemanticDescriptorScheme> root, Long epoch) {
        return new SemanticDescriptorSchemeFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected SemanticDescriptorSchemeSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<SemanticDescriptorScheme> criteriaQuery, Root<SemanticDescriptorScheme> root, Long epoch) {
        return new SemanticDescriptorSchemeSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
