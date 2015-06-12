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

import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.ObjectProvidedMetadataFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion_;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectProvidedMetadata_;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning
 * {@link ObjectProvidedMetadata}.
 */
public class ObjectProvidedMetadataFilterFactoryImpl extends GenericQueryFilterFactoryImpl<ObjectProvidedMetadata>
        implements ObjectProvidedMetadataFilterFactory {

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
    public ObjectProvidedMetadataFilterFactoryImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ObjectProvidedMetadata> criteriaQuery, Root<ObjectProvidedMetadata> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<ObjectProvidedMetadata> byIncludedInVersions(Set<Long> versionIds)
            throws IllegalArgumentException {
        if (versionIds.remove(null)) {
            throw new IllegalArgumentException("Cannot perform for null identifier");
        }
        Join<ObjectProvidedMetadata, ContentVersion> versions = root.join(ObjectProvidedMetadata_.providedFor);
        Predicate predicate = versions.get(ContentVersion_.id).in(versionIds);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<ObjectProvidedMetadata> byIncludedInVersion(long versionId) {
        Join<ObjectProvidedMetadata, ContentVersion> versions = root.join(ObjectProvidedMetadata_.providedFor);
        Predicate predicate = criteriaBuilder.equal(versions.get(ContentVersion_.id), versionId);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<ObjectProvidedMetadata> byIncludedInVersionNo(int versionNo) {
        Join<ObjectProvidedMetadata, ContentVersion> versions = root.join(ObjectProvidedMetadata_.providedFor);
        Predicate predicate = criteriaBuilder.equal(versions.get(ContentVersion_.version), versionNo);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<ObjectProvidedMetadata> byMetadataFileName(String name) {
        Predicate predicate = criteriaBuilder.equal(root.get(ObjectProvidedMetadata_.filename), name);
        return constructQueryFilter(predicate);
    }

}
