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
package pl.psnc.synat.wrdz.zmd.dao.object.content.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.ListAttribute;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileFormatDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileFormatFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileFormatSorterBuilder;
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
 * A class managing the persistence of {@link DataFileFormat} class. It implements additional operations available for
 * {@link DataFileFormat} object (as defined in {@link DataFileFormatDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class DataFileFormatDaoBean extends
        ExtendedGenericDaoBean<DataFileFormatFilterFactory, DataFileFormatSorterBuilder, DataFileFormat, Long>
        implements DataFileFormatDao {

    /**
     * Creates new instance of DataFileFormatDaoBean.
     */
    public DataFileFormatDaoBean() {
        super(DataFileFormat.class);
    }


    @Override
    protected DataFileFormatFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DataFileFormat> criteriaQuery, Root<DataFileFormat> root, Long epoch) {
        return new DataFileFormatFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected DataFileFormatSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DataFileFormat> criteriaQuery, Root<DataFileFormat> root, Long epoch) {
        return new DataFileFormatSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public Map<DataFileFormat, Map<Long, Long>> countObjectsGroupByOwner() {
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<DataFile> root = query.from(DataFile.class);
        Join<DataFile, DataFileFormat> joinFormat = root.join(DataFile_.format);
        Join<DataFile, DataFileVersion> joinFileVersions = root.join(DataFile_.includedIn);
        Join<DataFileVersion, ContentVersion> joinVersion = joinFileVersions.join(DataFileVersion_.contentVersion);
        Join<ContentVersion, DigitalObject> joinObject = joinVersion.join(ContentVersion_.object);

        query.groupBy(joinFormat, joinObject.get(DigitalObject_.ownerId));

        query.multiselect(joinFormat, joinObject.get(DigitalObject_.ownerId), criteriaBuilder.countDistinct(joinObject));

        Map<DataFileFormat, Map<Long, Long>> results = new HashMap<DataFileFormat, Map<Long, Long>>();
        for (Tuple tuple : entityManager.createQuery(query).getResultList()) {
            DataFileFormat format = (DataFileFormat) tuple.get(0);

            if (!results.containsKey(format)) {
                results.put(format, new HashMap<Long, Long>());
            }

            results.get(format).put((Long) tuple.get(1), (Long) tuple.get(2));
        }

        return results;
    }


    @Override
    public Map<DataFileFormat, Map<Long, Long>> countDataFilesGroupByOwner() {
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<DataFile> root = query.from(DataFile.class);
        Join<DataFile, DataFileFormat> joinFormat = root.join(DataFile_.format);
        Join<DataFile, DataFileVersion> joinFileVersions = root.join(DataFile_.includedIn);
        Join<DataFileVersion, ContentVersion> joinVersion = joinFileVersions.join(DataFileVersion_.contentVersion);
        Join<ContentVersion, DigitalObject> joinObject = joinVersion.join(ContentVersion_.object);

        query.groupBy(joinFormat, joinObject.get(DigitalObject_.ownerId));

        query.multiselect(joinFormat, joinObject.get(DigitalObject_.ownerId), criteriaBuilder.countDistinct(root));

        Map<DataFileFormat, Map<Long, Long>> results = new HashMap<DataFileFormat, Map<Long, Long>>();
        for (Tuple tuple : entityManager.createQuery(query).getResultList()) {
            DataFileFormat format = (DataFileFormat) tuple.get(0);

            if (!results.containsKey(format)) {
                results.put(format, new HashMap<Long, Long>());
            }

            results.get(format).put((Long) tuple.get(1), (Long) tuple.get(2));
        }

        return results;
    }


    @Override
    public Map<DataFileFormat, Map<Long, Long>> getDataFileSizesGroupByOwner() {
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<DataFile> root = query.from(DataFile.class);
        Root<DigitalObject> objectRoot = query.from(DigitalObject.class);
        Join<DataFile, DataFileFormat> joinFormat = root.join(DataFile_.format);

        Subquery<DataFileVersion> subquery = query.subquery(DataFileVersion.class);
        Root<DataFileVersion> subqueryRoot = subquery.from(DataFileVersion.class);
        Join<DataFileVersion, ContentVersion> joinVersion = subqueryRoot.join(DataFileVersion_.contentVersion);
        Join<ContentVersion, DigitalObject> joinObject = joinVersion.join(ContentVersion_.object);
        subquery.where(criteriaBuilder.and(criteriaBuilder.equal(joinObject, objectRoot),
            criteriaBuilder.equal(root, subqueryRoot.get(DataFileVersion_.dataFile))));
        subquery.select(subqueryRoot);

        query.where(criteriaBuilder.exists(subquery));

        query.groupBy(joinFormat, objectRoot.get(DigitalObject_.ownerId));

        query.multiselect(joinFormat, objectRoot.get(DigitalObject_.ownerId),
            criteriaBuilder.sum(root.get(DataFile_.size)));

        Map<DataFileFormat, Map<Long, Long>> results = new HashMap<DataFileFormat, Map<Long, Long>>();
        for (Tuple tuple : entityManager.createQuery(query).getResultList()) {
            DataFileFormat format = (DataFileFormat) tuple.get(0);

            if (!results.containsKey(format)) {
                results.put(format, new HashMap<Long, Long>());
            }

            results.get(format).put((Long) tuple.get(1), (Long) tuple.get(2));
        }

        return results;
    }


    @Override
    public Set<DataFileFormat> findActive(boolean checkMasterObjects, boolean checkOptimizedObjects,
            boolean checkConvertedObjects) {

        Set<DataFileFormat> results = new HashSet<DataFileFormat>();

        if (checkMasterObjects) {
            results.addAll(findActive(MasterObject.class, MasterObject_.transformedTo));
        }

        if (checkOptimizedObjects) {
            results.addAll(findActive(OptimizedObject.class, OptimizedObject_.optimizedTo));
        }

        if (checkConvertedObjects) {
            results.addAll(findActive(ConvertedObject.class, ConvertedObject_.convertedTo));
        }

        return results;
    }


    /**
     * Return active file formats for the given DigitalObject type, filtered by the given migration attribute (only
     * those objects for which the given migration attribute returns an empty result will be checked for formats).
     * 
     * A format is considered active if it is used by a file belonging to the current version of a digital object that
     * has not been migrated from.
     * 
     * @param <T>
     *            type of the digital object
     * @param clazz
     *            class of the digital object
     * @param excludedMigration
     *            migration attribute to check
     * @return list of active file formats
     */
    private <T extends DigitalObject> List<DataFileFormat> findActive(Class<T> clazz,
            ListAttribute<? super T, ? extends Migration<?, ?>> excludedMigration) {

        CriteriaQuery<DataFileFormat> query = criteriaBuilder.createQuery(DataFileFormat.class);

        Root<T> root = query.from(clazz);
        query.where(criteriaBuilder.isEmpty(root.<List<?>> get(excludedMigration.getName())));

        Join<T, ContentVersion> joinVersion = root.join(DigitalObject_.currentVersion);
        Join<ContentVersion, DataFileVersion> joinFileVersions = joinVersion.join(ContentVersion_.files);
        Join<DataFileVersion, DataFile> joinFiles = joinFileVersions.join(DataFileVersion_.dataFile);
        query.select(joinFiles.get(DataFile_.format));

        return entityManager.createQuery(query).getResultList();
    }


    @Override
    public List<DataFileFormat> findAllWithPuidFetchNames() {

        CriteriaQuery<DataFileFormat> query = criteriaBuilder.createQuery(DataFileFormat.class);

        Root<DataFileFormat> root = query.from(clazz);
        root.fetch(DataFileFormat_.names);

        query.where(criteriaBuilder.isNotNull(root.get(DataFileFormat_.puid)));
        query.distinct(true);
        query.select(root);

        return entityManager.createQuery(query).getResultList();
    }


    @Override
    public List<DataFileFormat> findByObjectId(String objectIdentifier) {

        CriteriaQuery<DataFileFormat> query = criteriaBuilder.createQuery(DataFileFormat.class);

        Root<DigitalObject> root = query.from(DigitalObject.class);
        Join<DigitalObject, Identifier> joinIdentifiers = root.join(DigitalObject_.identifiers);
        Join<DigitalObject, ContentVersion> joinVersion = root.join(DigitalObject_.currentVersion);
        Join<ContentVersion, DataFileVersion> joinFileVersions = joinVersion.join(ContentVersion_.files);
        Join<DataFileVersion, DataFile> joinFile = joinFileVersions.join(DataFileVersion_.dataFile);
        Join<DataFile, DataFileFormat> joinFormat = joinFile.join(DataFile_.format);

        query.distinct(true);
        query.select(joinFormat);
        query.where(criteriaBuilder.like(joinIdentifiers.get(Identifier_.identifier), objectIdentifier));

        return entityManager.createQuery(query).getResultList();
    }
}
