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

import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileExtractedMetadataDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileExtractedMetadataFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileExtractedMetadataSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile_;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileExtractedMetadata_;

/**
 * A class managing the persistence of {@link FileExtractedMetadata} class. It implements additional operations
 * available for {@link FileExtractedMetadata} object (as defined in {@link FileExtractedMetadataDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class FileExtractedMetadataDaoBean
        extends
        MetadataFileDaoBean<FileExtractedMetadataFilterFactory, FileExtractedMetadataSorterBuilder, FileExtractedMetadata>
        implements FileExtractedMetadataDao {

    /**
     * Creates new instance of FileExtractedMetadataDaoBean.
     */
    public FileExtractedMetadataDaoBean() {
        super(FileExtractedMetadata.class);
    }


    @Override
    protected FileExtractedMetadataFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<FileExtractedMetadata> criteriaQuery, Root<FileExtractedMetadata> root, Long epoch) {
        return new FileExtractedMetadataFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected FileExtractedMetadataSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<FileExtractedMetadata> criteriaQuery, Root<FileExtractedMetadata> root, Long epoch) {
        return new FileExtractedMetadataSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected Join<?, ContentVersion> joinVersions(Root<FileExtractedMetadata> root) {
        return root.join(FileExtractedMetadata_.extractedFrom).join(DataFile_.includedIn)
                .join(DataFileVersion_.contentVersion);
    }
}
