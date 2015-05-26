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

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.zmd.dao.object.metadata.ObjectProvidedMetadataDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.ObjectProvidedMetadataFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.ObjectProvidedMetadataSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectProvidedMetadata_;

/**
 * A class managing the persistence of {@link ObjectProvidedMetadata} class. It implements additional operations
 * available for {@link ObjectProvidedMetadata} object (as defined in {@link ObjectProvidedMetadataDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ObjectProvidedMetadataDaoBean
        extends
        MetadataFileDaoBean<ObjectProvidedMetadataFilterFactory, ObjectProvidedMetadataSorterBuilder, ObjectProvidedMetadata>
        implements ObjectProvidedMetadataDao {

    /**
     * Creates new instance of ObjectProvidedMetadataDaoBean.
     */
    public ObjectProvidedMetadataDaoBean() {
        super(ObjectProvidedMetadata.class);
    }


    @Override
    protected ObjectProvidedMetadataFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ObjectProvidedMetadata> criteriaQuery, Root<ObjectProvidedMetadata> root, Long epoch) {
        return new ObjectProvidedMetadataFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected ObjectProvidedMetadataSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ObjectProvidedMetadata> criteriaQuery, Root<ObjectProvidedMetadata> root, Long epoch) {
        return new ObjectProvidedMetadataSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected Join<ObjectProvidedMetadata, ContentVersion> joinVersions(Root<ObjectProvidedMetadata> root) {
        return root.join(ObjectProvidedMetadata_.providedFor);
    }
}
