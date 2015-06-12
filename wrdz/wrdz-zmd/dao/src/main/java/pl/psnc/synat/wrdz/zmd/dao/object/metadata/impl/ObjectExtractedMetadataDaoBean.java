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

import pl.psnc.synat.wrdz.zmd.dao.object.metadata.ObjectExtractedMetadataDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.ObjectExtractedMetadataFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.ObjectExtractedMetadataSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectExtractedMetadata_;

/**
 * A class managing the persistence of {@link ObjectExtractedMetadata} class. It implements additional operations
 * available for {@link ObjectExtractedMetadata} object (as defined in {@link ObjectExtractedMetadataDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ObjectExtractedMetadataDaoBean
        extends
        MetadataFileDaoBean<ObjectExtractedMetadataFilterFactory, ObjectExtractedMetadataSorterBuilder, ObjectExtractedMetadata>
        implements ObjectExtractedMetadataDao {

    /**
     * Creates new instance of ObjectExtractedMetadataDaoBean.
     */
    public ObjectExtractedMetadataDaoBean() {
        super(ObjectExtractedMetadata.class);
    }


    @Override
    protected ObjectExtractedMetadataFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ObjectExtractedMetadata> criteriaQuery, Root<ObjectExtractedMetadata> root, Long epoch) {
        return new ObjectExtractedMetadataFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected ObjectExtractedMetadataSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ObjectExtractedMetadata> criteriaQuery, Root<ObjectExtractedMetadata> root, Long epoch) {
        return new ObjectExtractedMetadataSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected Join<ObjectExtractedMetadata, ContentVersion> joinVersions(Root<ObjectExtractedMetadata> root) {
        return root.join(ObjectExtractedMetadata_.extractedFrom);
    }
}
