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
package pl.psnc.synat.wrdz.zmd.dao.object.impl;

import java.util.Collection;
import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject_;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion_;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation_;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning {@link DigitalObject}.
 */
public class DigitalObjectFilterFactoryImpl extends GenericQueryFilterFactoryImpl<DigitalObject> implements
        DigitalObjectFilterFactory {

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
    public DigitalObjectFilterFactoryImpl(CriteriaBuilder criteriaBuilder, CriteriaQuery<DigitalObject> criteriaQuery,
            Root<DigitalObject> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<DigitalObject> byType(ObjectType type) {
        Predicate predicate = criteriaBuilder.equal(root.get(DigitalObject_.type), type);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<DigitalObject> byIdentifier(String identifier) {
        Join<DigitalObject, Identifier> identifiers = root.join(DigitalObject_.identifiers);
        Predicate predicate = criteriaBuilder.like(identifiers.get(Identifier_.identifier), identifier);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<DigitalObject> byVersion(Integer version) {
        Join<DigitalObject, ContentVersion> versions = root.join(DigitalObject_.versions);
        Predicate predicate = criteriaBuilder.equal(versions.get(ContentVersion_.version), version);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<DigitalObject> byOwner(long ownerId) {
        Predicate predicate = criteriaBuilder.equal(root.get(DigitalObject_.ownerId), ownerId);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<DigitalObject> havingOperationsPerformedBetween(Date start, Date end)
            throws IllegalArgumentException {
        if (start == null && end == null) {
            throw new IllegalArgumentException("Cannot create filter for unspecified beginning and end of range.");
        } else if (start != null && end != null && start.before(end)) {
            throw new IllegalArgumentException(
                    "Cannot create filter for end date lesser than or equal to beginning date.");
        }
        Join<DigitalObject, Operation> operations = root.join(DigitalObject_.operations);
        criteriaQuery.distinct(true);
        if (start != null) {
            Predicate predicate = criteriaBuilder.greaterThan(operations.get(Operation_.date), start);
            if (end != null) {
                predicate = criteriaBuilder.and(criteriaBuilder.lessThan(operations.get(Operation_.date), end),
                    predicate);
            }
            return constructQueryFilter(predicate);
        } else {
            return constructQueryFilter(criteriaBuilder.lessThan(operations.get(Operation_.date), end));
        }
    }


    @Override
    public QueryFilter<DigitalObject> byCurrentVersionState(boolean notEmpty) {
        if (notEmpty) {
            return constructQueryFilter(criteriaBuilder.isNotNull(root.get(DigitalObject_.currentVersion)));
        } else {
            return constructQueryFilter(criteriaBuilder.isNull(root.get(DigitalObject_.currentVersion)));
        }
    }


    @Override
    public QueryFilter<DigitalObject> byIds(Collection<Long> ids) {
        return constructQueryFilter(root.get(DigitalObject_.id).in(ids));
    }


    @Override
    public QueryFilter<DigitalObject> byName(String name) {
        return constructQueryFilter(criteriaBuilder.like(root.get(DigitalObject_.name), name));
    }
}
