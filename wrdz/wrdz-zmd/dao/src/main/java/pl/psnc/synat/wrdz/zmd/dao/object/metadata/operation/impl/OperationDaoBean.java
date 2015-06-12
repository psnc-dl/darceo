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
package pl.psnc.synat.wrdz.zmd.dao.object.metadata.operation.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.operation.OperationDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.operation.OperationFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.operation.OperationSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject_;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation_;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;

/**
 * A class managing the persistence of {@link Operation} class. It implements additional operations available for
 * {@link Operation} object (as defined in {@link OperationDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class OperationDaoBean extends
        ExtendedGenericDaoBean<OperationFilterFactory, OperationSorterBuilder, Operation, Long> implements OperationDao {

    /**
     * Creates new instance of OperationDaoBean.
     */
    public OperationDaoBean() {
        super(Operation.class);
    }


    @Override
    protected OperationFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<Operation> criteriaQuery, Root<Operation> root, Long epoch) {
        return new OperationFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected OperationSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<Operation> criteriaQuery, Root<Operation> root, Long epoch) {
        return new OperationSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public void lockTable(boolean exclusive) {
        if (exclusive) {
            entityManager.createNativeQuery("LOCK TABLE darceo.zmd_metadata_operations IN EXCLUSIVE MODE")
                    .executeUpdate();
        } else {
            entityManager.createNativeQuery("LOCK TABLE darceo.zmd_metadata_operations IN SHARE MODE").executeUpdate();
        }
    }


    @Override
    public List<NamespaceType> findUsedNamespaceTypes(Long objectId) {
        CriteriaQuery<NamespaceType> query = criteriaBuilder.createQuery(NamespaceType.class);
        Root<Operation> root = query.from(clazz);
        query = query.select(root.get(Operation_.metadataType));
        query = query.distinct(true);
        if (objectId != null) {
            query = query.where(criteriaBuilder.equal(root.get(Operation_.object).get(DigitalObject_.id), objectId));
        }
        return entityManager.createQuery(query).getResultList();
    }


    @Override
    public Operation getOperationForObject(Long objectId) {
        CriteriaQuery<Operation> query = criteriaBuilder.createQuery(Operation.class);
        Root<Operation> root = query.from(clazz);

        query = query.distinct(true);
        Predicate predicate = criteriaBuilder.equal(root.get(Operation_.object).get(DigitalObject_.id), objectId);
        if (objectId != null) {
            query = query.where(predicate);
        }
        return entityManager.createQuery(query).getSingleResult();
    }


    @Override
    public Long countChangedObjects(Date from, Date until, NamespaceType prefix, ObjectType set) {
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<Operation> root = query.from(clazz);
        Join<Operation, DigitalObject> operationToObject = root.join(Operation_.object);
        query = query.distinct(true).select(criteriaBuilder.countDistinct(operationToObject.get(DigitalObject_.id)));
        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(Operation_.date), from);
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get(Operation_.date), until));
        predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(Operation_.metadataType), prefix));
        if (set != null) {
            predicate = criteriaBuilder.and(predicate,
                criteriaBuilder.equal(operationToObject.get(DigitalObject_.type), set));
        }
        query = query.where(predicate);
        return entityManager.createQuery(query).getSingleResult();
    }


    @Override
    public List<Operation> getChanges(Date from, Date until, NamespaceType prefix, ObjectType set, int offset,
            int pageSize) {
        CriteriaQuery<Operation> query = criteriaBuilder.createQuery(Operation.class);
        Root<Operation> root = query.from(clazz);
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Operation> subqueryRoot = subquery.from(clazz);
        subquery = subquery.select(criteriaBuilder.max(subqueryRoot.get(Operation_.id)));
        Predicate subqueryPredicate = criteriaBuilder.greaterThanOrEqualTo(subqueryRoot.get(Operation_.date), from);
        subqueryPredicate = criteriaBuilder.and(subqueryPredicate,
            criteriaBuilder.lessThanOrEqualTo(subqueryRoot.get(Operation_.date), until));
        subqueryPredicate = criteriaBuilder.and(subqueryPredicate,
            criteriaBuilder.equal(subqueryRoot.get(Operation_.metadataType), prefix));
        if (set != null) {
            Join<Operation, DigitalObject> operationToObject = subqueryRoot.join(Operation_.object);
            subqueryPredicate = criteriaBuilder.and(subqueryPredicate,
                criteriaBuilder.equal(operationToObject.get(DigitalObject_.type), set));
        }
        subquery = subquery.where(subqueryPredicate);
        subquery = subquery.groupBy(subqueryRoot.get(Operation_.object).get(DigitalObject_.id));
        query = query.where(root.get(Operation_.id).in(subquery));
        query.orderBy(criteriaBuilder.asc(root.get(Operation_.id)));
        return entityManager.createQuery(query).setFirstResult(offset).setMaxResults(pageSize).getResultList();
    }
}
