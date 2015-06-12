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
package pl.psnc.synat.wrdz.ru.dao.services.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.ru.dao.services.DataManipulationServiceDao;
import pl.psnc.synat.wrdz.ru.dao.services.DataManipulationServiceFilterFactory;
import pl.psnc.synat.wrdz.ru.dao.services.DataManipulationServiceSorterBuilder;
import pl.psnc.synat.wrdz.ru.entity.services.DataManipulationService;
import pl.psnc.synat.wrdz.ru.entity.services.DataManipulationService_;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor_;

/**
 * A class managing the persistence of {@link DataManipulationService} class. It implements additional operations
 * available for {@link DataManipulationService} object (as defined in {@link DataManipulationServiceDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class DataManipulationServiceDaoBean
        extends
        ExtendedGenericDaoBean<DataManipulationServiceFilterFactory, DataManipulationServiceSorterBuilder, DataManipulationService, Long>
        implements DataManipulationServiceDao {

    /**
     * Creates new instance of DataManipulationServiceDaoBean.
     */
    public DataManipulationServiceDaoBean() {
        super(DataManipulationService.class);
    }


    @Override
    protected DataManipulationServiceFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DataManipulationService> criteriaQuery, Root<DataManipulationService> root, Long epoch) {
        return new DataManipulationServiceFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected DataManipulationServiceSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DataManipulationService> criteriaQuery, Root<DataManipulationService> root, Long epoch) {
        return new DataManipulationServiceSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public List<DataManipulationService> findActive() {

        CriteriaQuery<DataManipulationService> query = criteriaBuilder.createQuery(clazz);
        Root<DataManipulationService> root = query.from(clazz);
        Join<DataManipulationService, SemanticDescriptor> joinSemantic = root
                .join(DataManipulationService_.semanticDescriptor);

        query.where(criteriaBuilder.equal(joinSemantic.get(SemanticDescriptor_.deleted), false));
        query.orderBy(criteriaBuilder.asc(root.get(DataManipulationService_.name)));
        query.select(root);

        return entityManager.createQuery(query).getResultList();
    }
}
