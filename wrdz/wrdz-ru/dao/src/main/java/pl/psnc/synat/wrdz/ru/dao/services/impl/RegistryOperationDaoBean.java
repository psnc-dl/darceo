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

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.ru.dao.services.RegistryOperationDao;
import pl.psnc.synat.wrdz.ru.dao.services.RegistryOperationFilterFactory;
import pl.psnc.synat.wrdz.ru.dao.services.RegistryOperationSorterBuilder;
import pl.psnc.synat.wrdz.ru.entity.services.RegistryOperation;
import pl.psnc.synat.wrdz.ru.entity.services.RegistryOperation_;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor_;

/**
 * A class managing the persistence of {@link RegistryOperation} class. It implements additional operations available
 * for {@link RegistryOperation} object (as defined in {@link RegistryOperationDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class RegistryOperationDaoBean extends
        ExtendedGenericDaoBean<RegistryOperationFilterFactory, RegistryOperationSorterBuilder, RegistryOperation, Long>
        implements RegistryOperationDao {

    /**
     * Creates new instance of RegistryOperationDaoBean.
     */
    public RegistryOperationDaoBean() {
        super(RegistryOperation.class);
    }


    @Override
    protected RegistryOperationFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<RegistryOperation> criteriaQuery, Root<RegistryOperation> root, Long epoch) {
        return new RegistryOperationFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected RegistryOperationSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<RegistryOperation> criteriaQuery, Root<RegistryOperation> root, Long epoch) {
        return new RegistryOperationSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public void lockTable(boolean exclusive) {
        if (exclusive) {
            entityManager.createNativeQuery("LOCK TABLE ru_service_operations IN EXCLUSIVE MODE;").executeUpdate();
        } else {
            entityManager.createNativeQuery("LOCK TABLE ru_service_operations IN SHARE MODE; ").executeUpdate();
        }
    }


    @Override
    public List<RegistryOperation> getChanges(Date from, Date until) {
        CriteriaQuery<RegistryOperation> query = criteriaBuilder.createQuery(RegistryOperation.class);
        Root<RegistryOperation> root = query.from(clazz);
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<RegistryOperation> subqueryRoot = subquery.from(clazz);
        subquery = subquery.select(criteriaBuilder.max(subqueryRoot.get(RegistryOperation_.id)));
        Predicate subqueryPredicate = criteriaBuilder.isTrue(subqueryRoot.get(RegistryOperation_.exposed));
        subqueryPredicate = criteriaBuilder.and(subqueryPredicate,
            criteriaBuilder.lessThanOrEqualTo(subqueryRoot.get(RegistryOperation_.date), until));
        if (from != null) {
            subqueryPredicate = criteriaBuilder.and(subqueryPredicate,
                criteriaBuilder.greaterThanOrEqualTo(subqueryRoot.get(RegistryOperation_.date), from));
        }
        subquery = subquery.where(subqueryPredicate);
        subquery = subquery.groupBy(subqueryRoot.get(RegistryOperation_.target).get(SemanticDescriptor_.id));
        query = query.where(root.get(RegistryOperation_.id).in(subquery));
        query.orderBy(criteriaBuilder.asc(root.get(RegistryOperation_.date)));
        return entityManager.createQuery(query).getResultList();
    }

}
