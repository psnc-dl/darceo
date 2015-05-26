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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.operation.OperationFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject_;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier_;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation_;
import pl.psnc.synat.wrdz.zmd.entity.types.OperationType;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning {@link Operation}.
 */
public class OperationFilterFactoryImpl extends GenericQueryFilterFactoryImpl<Operation> implements
        OperationFilterFactory {

    /**
     * Constructs this factory initializing it with required arguments.
     * 
     * @param criteriaBuilder
     *            reference to {@link CriteriaBuilder} object.
     * @param criteriaQuery
     *            query upon which filters will be build
     * @param root
     *            object representing root type of the entity this filter factory manages
     * @param epoch
     *            builder's epoch
     * 
     * @throws IllegalArgumentException
     *             thrown if one of arguments is <code>null</code>
     * 
     */
    public OperationFilterFactoryImpl(CriteriaBuilder criteriaBuilder, CriteriaQuery<Operation> criteriaQuery,
            Root<Operation> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<Operation> byType(OperationType type) {
        Predicate predicate = criteriaBuilder.equal(root.get(Operation_.operation), type);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<Operation> byDigitalObject(long id) {
        Join<Operation, DigitalObject> objects = root.join(Operation_.object);
        Predicate predicate = criteriaBuilder.equal(objects.get(DigitalObject_.id), id);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<Operation> byDigitalObject(String identifier)
            throws IllegalArgumentException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("Cannot create filter for empty or null identifier value.");
        }
        Join<Operation, DigitalObject> objects = root.join(Operation_.object);
        Join<DigitalObject, Identifier> identifiers = objects.join(DigitalObject_.defaultIdentifier);
        Predicate predicate = criteriaBuilder.equal(identifiers.get(Identifier_.identifier), identifier);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<Operation> byDateBetween(Date start, Date end)
            throws IllegalArgumentException {
        if (start == null && end == null) {
            throw new IllegalArgumentException("Cannot create filter for unspecified beginning and end of range.");
        } else if (start != null && end != null && !start.before(end)) {
            throw new IllegalArgumentException(
                    "Cannot create filter for end date lesser than or equal to beginning date.");
        } else if (start != null) {
            Predicate predicate = criteriaBuilder.greaterThan(root.get(Operation_.date), start);
            if (end != null) {
                predicate = criteriaBuilder.and(criteriaBuilder.lessThan(root.get(Operation_.date), end), predicate);
            }
            return constructQueryFilter(predicate);
        } else {
            return constructQueryFilter(criteriaBuilder.lessThan(root.get(Operation_.date), end));
        }
    }


    @Override
    public QueryFilter<Operation> byMetadataType(NamespaceType type)
            throws IllegalArgumentException {
        if (type == null) {
            throw new IllegalArgumentException("Cannot create filter for unspecified metadata type.");
        }
        Predicate predicate = criteriaBuilder.equal(root.get(Operation_.metadataType), type);
        return constructQueryFilter(predicate);
    }

}
