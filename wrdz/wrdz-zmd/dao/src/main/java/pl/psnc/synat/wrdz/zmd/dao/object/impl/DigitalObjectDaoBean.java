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

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectDao;
import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject_;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileFormat;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileFormat_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile_;

/**
 * A class managing the persistence of {@link DigitalObject} class. It implements additional operations available for
 * {@link DigitalObject} object (as defined in {@link DigitalObjectDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class DigitalObjectDaoBean extends
        ExtendedGenericDaoBean<DigitalObjectFilterFactory, DigitalObjectSorterBuilder, DigitalObject, Long> implements
        DigitalObjectDao {

    /**
     * Creates new instance of DigitalObjectDaoBean.
     */
    public DigitalObjectDaoBean() {
        super(DigitalObject.class);
    }


    @Override
    protected DigitalObjectFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DigitalObject> criteriaQuery, Root<DigitalObject> root, Long epoch) {
        return new DigitalObjectFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected DigitalObjectSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DigitalObject> criteriaQuery, Root<DigitalObject> root, Long epoch) {
        return new DigitalObjectSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public boolean containsDataFiles(String identifier, String formatPuid) {
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<DigitalObject> root = query.from(DigitalObject.class);

        Join<DigitalObject, Identifier> joinIdentifier = root.join(DigitalObject_.defaultIdentifier);
        Join<DigitalObject, ContentVersion> joinVersion = root.join(DigitalObject_.currentVersion);
        Join<ContentVersion, DataFileVersion> joinFileVersions = joinVersion.join(ContentVersion_.files);
        Join<DataFileVersion, DataFile> joinFiles = joinFileVersions.join(DataFileVersion_.dataFile);
        Join<DataFile, DataFileFormat> joinFormat = joinFiles.join(DataFile_.format);

        query.where(criteriaBuilder.and(criteriaBuilder.equal(joinIdentifier.get(Identifier_.identifier), identifier),
            criteriaBuilder.equal(joinFormat.get(DataFileFormat_.puid), formatPuid)));

        query.select(criteriaBuilder.count(root));

        return entityManager.createQuery(query).getSingleResult() > 0;
    }


    @Override
    public Map<Long, Long> countAllGroupByOwner() {
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<DigitalObject> root = query.from(clazz);
        query.groupBy(root.get(DigitalObject_.ownerId));
        query.multiselect(root.get(DigitalObject_.ownerId), criteriaBuilder.countDistinct(root));

        Map<Long, Long> results = new HashMap<Long, Long>();
        for (Tuple tuple : entityManager.createQuery(query).getResultList()) {
            results.put((Long) tuple.get(0), (Long) tuple.get(1));
        }

        return results;
    }


    @Override
    public DigitalObject getDigitalObject(String identifier) {
        DigitalObjectFilterFactory queryFilterFactory = createQueryModifier().getQueryFilterFactory();
        return findFirstResultBy(queryFilterFactory.and(queryFilterFactory.byIdentifier(identifier),
            queryFilterFactory.byCurrentVersionState(true)));
    }
}
