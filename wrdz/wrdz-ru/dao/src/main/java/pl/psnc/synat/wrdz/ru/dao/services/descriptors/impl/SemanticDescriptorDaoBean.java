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
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.SemanticDescriptorDao;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.SemanticDescriptorFilterFactory;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.SemanticDescriptorSorterBuilder;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;

/**
 * A class managing the persistence of {@link SemanticDescriptor} class. It implements additional operations available
 * for {@link SemanticDescriptor} object (as defined in {@link SemanticDescriptorDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class SemanticDescriptorDaoBean
        extends
        ExtendedGenericDaoBean<SemanticDescriptorFilterFactory, SemanticDescriptorSorterBuilder, SemanticDescriptor, Long>
        implements SemanticDescriptorDao {

    /**
     * Creates new instance of SemanticDescriptorDaoBean.
     */
    public SemanticDescriptorDaoBean() {
        super(SemanticDescriptor.class);
    }


    @Override
    protected SemanticDescriptorFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<SemanticDescriptor> criteriaQuery, Root<SemanticDescriptor> root, Long epoch) {
        return new SemanticDescriptorFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected SemanticDescriptorSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<SemanticDescriptor> criteriaQuery, Root<SemanticDescriptor> root, Long epoch) {
        return new SemanticDescriptorSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
