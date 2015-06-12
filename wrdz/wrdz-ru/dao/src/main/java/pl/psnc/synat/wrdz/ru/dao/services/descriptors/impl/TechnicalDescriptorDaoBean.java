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
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.TechnicalDescriptorDao;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.TechnicalDescriptorFilterFactory;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.TechnicalDescriptorSorterBuilder;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.TechnicalDescriptor;

/**
 * A class managing the persistence of {@link TechnicalDescriptor} class. It implements additional operations available
 * for {@link TechnicalDescriptor} object (as defined in {@link TechnicalDescriptorDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class TechnicalDescriptorDaoBean
        extends
        ExtendedGenericDaoBean<TechnicalDescriptorFilterFactory, TechnicalDescriptorSorterBuilder, TechnicalDescriptor, Long>
        implements TechnicalDescriptorDao {

    /**
     * Creates new instance of TechnicalDescriptorDaoBean.
     */
    public TechnicalDescriptorDaoBean() {
        super(TechnicalDescriptor.class);
    }


    @Override
    protected TechnicalDescriptorFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<TechnicalDescriptor> criteriaQuery, Root<TechnicalDescriptor> root, Long epoch) {
        return new TechnicalDescriptorFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected TechnicalDescriptorSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<TechnicalDescriptor> criteriaQuery, Root<TechnicalDescriptor> root, Long epoch) {
        return new TechnicalDescriptorSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
