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
package pl.psnc.synat.wrdz.zmd.dao.object.metadata.impl;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.GenericQuerySorterBuilder;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.MetadataFileDao;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion_;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile_;

/**
 * Base class for DAO objects handling MetadataFile subclasses.
 * 
 * @param <FF>
 *            filter factory implementing {@link GenericQueryFilterFactory}
 * @param <SB>
 *            sorter builder implementing {@link GenericQuerySorterBuilder}
 * @param <T>
 *            entity class that will be managed by this DAO
 */
public abstract class MetadataFileDaoBean<FF extends GenericQueryFilterFactory<T>, SB extends GenericQuerySorterBuilder<T>, T extends MetadataFile>
        extends ExtendedGenericDaoBean<FF, SB, T, Long> implements MetadataFileDao<FF, SB, T> {

    /**
     * Sets the generic type this DAO will be responsible for.
     * 
     * @param clazz
     *            entity class that will be managed by this DAO
     */
    public MetadataFileDaoBean(Class<T> clazz) {
        super(clazz);
    }


    @Override
    public Map<Long, Long> countAllGroupByOwner() {
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<T> root = query.from(clazz);
        Join<?, ContentVersion> joinVersion = joinVersions(root);
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
        Root<T> root = query.from(clazz);
        Root<DigitalObject> objectRoot = query.from(DigitalObject.class);

        Subquery<T> subquery = query.subquery(clazz);
        Root<T> subqueryRoot = subquery.from(clazz);
        Join<?, ContentVersion> joinVersion = joinVersions(subqueryRoot);
        Join<ContentVersion, DigitalObject> joinObject = joinVersion.join(ContentVersion_.object);
        subquery.where(criteriaBuilder.and(criteriaBuilder.equal(joinObject, objectRoot),
            criteriaBuilder.equal(root, subqueryRoot)));
        subquery.select(subqueryRoot);

        query.where(criteriaBuilder.exists(subquery));
        query.groupBy(objectRoot.get(DigitalObject_.ownerId));
        query.multiselect(objectRoot.get(DigitalObject_.ownerId), criteriaBuilder.sum(root.get(MetadataFile_.size)));

        Map<Long, Long> results = new HashMap<Long, Long>();
        for (Tuple tuple : entityManager.createQuery(query).getResultList()) {
            results.put((Long) tuple.get(0), (Long) tuple.get(1));
        }

        return results;
    }


    /**
     * Joins the metadata file entity root with {@link ContentVersion}.
     * 
     * @param root
     *            metadata file entity root
     * @return join object
     */
    protected abstract Join<?, ContentVersion> joinVersions(Root<T> root);
}
