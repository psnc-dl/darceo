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
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileExtractedMetadataFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile_;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileExtractedMetadata_;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning
 * {@link FileExtractedMetadata}.
 */
public class FileExtractedMetadataFilterFactoryImpl extends GenericQueryFilterFactoryImpl<FileExtractedMetadata>
        implements FileExtractedMetadataFilterFactory {

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
    public FileExtractedMetadataFilterFactoryImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<FileExtractedMetadata> criteriaQuery, Root<FileExtractedMetadata> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<FileExtractedMetadata> byExtractedFrom(Set<Long> fileIds)
            throws IllegalArgumentException {
        if (fileIds.remove(null)) {
            throw new IllegalArgumentException("Cannot perform for null identifier");
        }
        Predicate predicate = root.get(FileExtractedMetadata_.extractedFrom).in(fileIds);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<FileExtractedMetadata> byExtractedFrom(long fileId) {
        Join<FileExtractedMetadata, DataFile> dataFiles = root.join(FileExtractedMetadata_.extractedFrom);
        Predicate predicate = criteriaBuilder.equal(dataFiles.get(DataFile_.id), fileId);

        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<FileExtractedMetadata> byMetadataFileName(String name) {
        Predicate predicate = root.get(FileExtractedMetadata_.objectFilepath).in(name);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<FileExtractedMetadata> byIncludedInVersion(long versionId) {
        Join<DataFile, ContentVersion> versions = root.join(FileExtractedMetadata_.extractedFrom).join(
            DataFile_.mainFileIn);
        Predicate predicate = criteriaBuilder.equal(versions.get(ContentVersion_.id), versionId);
        return constructQueryFilter(predicate);

    }
}
