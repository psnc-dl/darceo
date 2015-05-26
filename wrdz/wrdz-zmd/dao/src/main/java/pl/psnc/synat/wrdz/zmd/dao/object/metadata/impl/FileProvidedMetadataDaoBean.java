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

import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileProvidedMetadataDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileProvidedMetadataFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileProvidedMetadataSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion_;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileProvidedMetadata_;

/**
 * A class managing the persistence of {@link FileProvidedMetadata} class. It implements additional operations available
 * for {@link FileProvidedMetadata} object (as defined in {@link FileProvidedMetadataDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class FileProvidedMetadataDaoBean extends
        MetadataFileDaoBean<FileProvidedMetadataFilterFactory, FileProvidedMetadataSorterBuilder, FileProvidedMetadata>
        implements FileProvidedMetadataDao {

    /**
     * Creates new instance of FileProvidedMetadataDaoBean.
     */
    public FileProvidedMetadataDaoBean() {
        super(FileProvidedMetadata.class);
    }


    @Override
    protected FileProvidedMetadataFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<FileProvidedMetadata> criteriaQuery, Root<FileProvidedMetadata> root, Long epoch) {
        return new FileProvidedMetadataFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected FileProvidedMetadataSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<FileProvidedMetadata> criteriaQuery, Root<FileProvidedMetadata> root, Long epoch) {
        return new FileProvidedMetadataSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected Join<?, ContentVersion> joinVersions(Root<FileProvidedMetadata> root) {
        return root.join(FileProvidedMetadata_.providedFor).join(DataFileVersion_.contentVersion);
    }
}
