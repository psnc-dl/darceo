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
import java.util.LinkedList;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile_;

/**
 * A class managing the persistence of {@link DataFile} class. It implements additional operations available for
 * {@link DataFile} object (as defined in {@link DataFileDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class DataFileDaoBean extends
        ExtendedGenericDaoBean<DataFileFilterFactory, DataFileSorterBuilder, DataFile, Long> implements DataFileDao {

    /**
     * Creates new instance of DataFileDaoBean.
     */
    public DataFileDaoBean() {
        super(DataFile.class);
    }


    @Override
    protected DataFileFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DataFile> criteriaQuery, Root<DataFile> root, Long epoch) {
        return new DataFileFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected DataFileSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DataFile> criteriaQuery, Root<DataFile> root, Long epoch) {
        return new DataFileSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public Map<Long, Long> countAllGroupByOwner() {
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<DataFile> root = query.from(clazz);
        Join<DataFile, DataFileVersion> joinFileVersions = root.join(DataFile_.includedIn);
        Join<DataFileVersion, ContentVersion> joinVersion = joinFileVersions.join(DataFileVersion_.contentVersion);
        Join<ContentVersion, DigitalObject> joinObject = joinVersion.join(ContentVersion_.object);

        query.groupBy(joinObject.get(DigitalObject_.ownerId));
        query.multiselect(joinObject.get(DigitalObject_.ownerId), criteriaBuilder.countDistinct(root));

        Map<Long, Long> results = new HashMap<Long, Long>();
        for (Tuple tuple : entityManager.createQuery(query).getResultList()) {
            results.put((Long) tuple.get(0), (Long) tuple.get(1));
        }

        return results;
    }


    @Override
    public Map<Long, Long> getSizeGroupByOwner() {
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<DataFile> root = query.from(clazz);
        Root<DigitalObject> objectRoot = query.from(DigitalObject.class);

        Subquery<DataFileVersion> subquery = query.subquery(DataFileVersion.class);
        Root<DataFileVersion> subqueryRoot = subquery.from(DataFileVersion.class);
        Join<DataFileVersion, ContentVersion> joinVersion = subqueryRoot.join(DataFileVersion_.contentVersion);
        Join<ContentVersion, DigitalObject> joinObject = joinVersion.join(ContentVersion_.object);
        subquery.where(criteriaBuilder.and(criteriaBuilder.equal(joinObject, objectRoot),
            criteriaBuilder.equal(root, subqueryRoot.get(DataFileVersion_.dataFile))));
        subquery.select(subqueryRoot);

        query.where(criteriaBuilder.exists(subquery));
        query.groupBy(objectRoot.get(DigitalObject_.ownerId));
        query.multiselect(objectRoot.get(DigitalObject_.ownerId), criteriaBuilder.sum(root.get(DataFile_.size)));

        Map<Long, Long> results = new HashMap<Long, Long>();
        for (Tuple tuple : entityManager.createQuery(query).getResultList()) {
            results.put((Long) tuple.get(0), (Long) tuple.get(1));
        }

        return results;
    }


    @Override
    public DataFile getDataFileFromVersion(String path, ContentVersion version) {
        DataFileFilterFactory filterFactory = createQueryModifier().getQueryFilterFactory();
        QueryFilter<DataFile> filter = filterFactory.and(filterFactory.byFilename(path),
            filterFactory.byContentVersionNo(version.getId(), false));
        return findFirstResultBy(filter);
    }


    @Override
    public DataFile getDataFileFromPathAndVersion(String path, ContentVersion version) {
        DataFileFilterFactory filterFactory = createQueryModifier().getQueryFilterFactory();
        LinkedList<String> pathes = new LinkedList<String>();
        pathes.add(path);
        QueryFilter<DataFile> filter = filterFactory.and(filterFactory.byObjectFilePath(pathes),
            filterFactory.byContentVersionNo(version.getId(), false));
        return findFirstResultBy(filter);
    }
}
