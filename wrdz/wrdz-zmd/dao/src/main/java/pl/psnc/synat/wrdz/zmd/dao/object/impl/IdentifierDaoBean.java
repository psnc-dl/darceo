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
import java.util.Collections;
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
import javax.persistence.metamodel.ListAttribute;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.zmd.dao.object.IdentifierDao;
import pl.psnc.synat.wrdz.zmd.dao.object.IdentifierFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.IdentifierSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.ConvertedObject;
import pl.psnc.synat.wrdz.zmd.entity.object.ConvertedObject_;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject_;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier_;
import pl.psnc.synat.wrdz.zmd.entity.object.MasterObject;
import pl.psnc.synat.wrdz.zmd.entity.object.MasterObject_;
import pl.psnc.synat.wrdz.zmd.entity.object.OptimizedObject;
import pl.psnc.synat.wrdz.zmd.entity.object.OptimizedObject_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileFormat;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileFormat_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile_;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration;

/**
 * A class managing the persistence of {@link Identifier} class. It implements additional operations available for
 * {@link Identifier} object (as defined in {@link IdentifierDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class IdentifierDaoBean extends
        ExtendedGenericDaoBean<IdentifierFilterFactory, IdentifierSorterBuilder, Identifier, Long> implements
        IdentifierDao {

    /**
     * Creates new instance of IdentifierDaoBean.
     */
    public IdentifierDaoBean() {
        super(Identifier.class);
    }


    @Override
    protected IdentifierFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<Identifier> criteriaQuery, Root<Identifier> root, Long epoch) {
        return new IdentifierFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected IdentifierSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<Identifier> criteriaQuery, Root<Identifier> root, Long epoch) {
        return new IdentifierSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public Identifier findNextActiveIdentifier(String previousIdentifierValue) {

        CriteriaQuery<Identifier> query = criteriaBuilder.createQuery(Identifier.class);
        Root<Identifier> root = query.from(Identifier.class);
        query.select(root);
        query.where(criteriaBuilder.notEqual(root.get(Identifier_.isActive), false));
        query.orderBy(criteriaBuilder.asc(root.get(Identifier_.id)));

        if (previousIdentifierValue != null) {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Identifier> subqueryRoot = subquery.from(Identifier.class);
            subquery.select(subqueryRoot.get(Identifier_.id));
            subquery.where(criteriaBuilder.equal(subqueryRoot.get(Identifier_.identifier), previousIdentifierValue));
            query.where(criteriaBuilder.greaterThan(root.get(Identifier_.id), criteriaBuilder.any(subquery)));
        }

        List<Identifier> identifiers = entityManager.createQuery(query).setMaxResults(1).getResultList();
        if (identifiers != null && !identifiers.isEmpty()) {
            return identifiers.get(0);
        }

        return null;
    }


    @Override
    public List<String> getIdentifiersForMigration(String puid, Long ownerId, Collection<Long> objectIds) {

        List<String> identifiers = findIdentifiers(MasterObject.class, MasterObject_.transformedTo, puid, ownerId,
            objectIds);
        identifiers.addAll(findIdentifiers(OptimizedObject.class, OptimizedObject_.optimizedTo, puid, ownerId,
            objectIds));
        identifiers.addAll(findIdentifiers(ConvertedObject.class, ConvertedObject_.convertedTo, puid, ownerId,
            objectIds));

        return identifiers;
    }


    /**
     * Returns the identifiers of digital objects of the given type, whose current version contains at least one data
     * file in a format with the given puid, filtered by the given migration attribute (only those objects for which the
     * given migration attribute returns an empty result will be returned) and optionally filtered by the object owner
     * and the object database id.
     * 
     * @param <T>
     *            type of the digital object
     * @param clazz
     *            class of the digital object
     * @param excludedMigration
     *            migration attribute to check
     * @param puid
     *            data file format puid to look for
     * @param ownerId
     *            digital object owner id; can be <code>null</code>
     * @param objectIds
     *            digital object database identifiers; can be <code>null</code>
     * @return list of digital object identifiers
     */
    private <T extends DigitalObject> List<String> findIdentifiers(Class<T> clazz,
            ListAttribute<? super T, ? extends Migration<?, ?>> excludedMigration, String puid, Long ownerId,
            Collection<Long> objectIds) {

        if (objectIds != null && objectIds.isEmpty()) {
            return Collections.emptyList();
        }

        CriteriaQuery<String> query = criteriaBuilder.createQuery(String.class);

        Root<T> root = query.from(clazz);
        Join<T, Identifier> joinIdentifier = root.join(DigitalObject_.defaultIdentifier);

        Join<T, ContentVersion> joinVersion = root.join(DigitalObject_.currentVersion);
        Join<ContentVersion, DataFileVersion> joinFileVersions = joinVersion.join(ContentVersion_.files);
        Join<DataFileVersion, DataFile> joinFiles = joinFileVersions.join(DataFileVersion_.dataFile);
        Join<DataFile, DataFileFormat> joinFormat = joinFiles.join(DataFile_.format);

        Predicate where = criteriaBuilder.and(criteriaBuilder.equal(joinFormat.get(DataFileFormat_.puid), puid),
            criteriaBuilder.isEmpty(root.<List<?>> get(excludedMigration.getName())));

        if (ownerId != null) {
            where = criteriaBuilder.and(where, criteriaBuilder.equal(root.get(DigitalObject_.ownerId), ownerId));
        }

        if (objectIds != null) {
            where = criteriaBuilder.and(where, root.get(DigitalObject_.id).in(objectIds));
        }

        query.select(joinIdentifier.get(Identifier_.identifier));
        query.distinct(true);
        query.where(where);

        return entityManager.createQuery(query).getResultList();
    }
}
