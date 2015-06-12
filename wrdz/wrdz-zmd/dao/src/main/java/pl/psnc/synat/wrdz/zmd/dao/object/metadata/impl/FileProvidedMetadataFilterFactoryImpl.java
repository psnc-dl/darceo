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
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileProvidedMetadataFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile_;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileProvidedMetadata_;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning
 * {@link FileProvidedMetadata}.
 */
public class FileProvidedMetadataFilterFactoryImpl extends GenericQueryFilterFactoryImpl<FileProvidedMetadata>
        implements FileProvidedMetadataFilterFactory {

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
    public FileProvidedMetadataFilterFactoryImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<FileProvidedMetadata> criteriaQuery, Root<FileProvidedMetadata> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<FileProvidedMetadata> byProvidedFor(Set<Long> fileIds)
            throws IllegalArgumentException {
        if (fileIds.remove(null)) {
            throw new IllegalArgumentException("Cannot perform for null identifier");
        }
        Join<?, DataFile> dataFiles = root.join(FileProvidedMetadata_.providedFor).join(DataFileVersion_.dataFile);
        Predicate predicate = dataFiles.get(DataFile_.id).in(fileIds);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<FileProvidedMetadata> byProvidedFor(long fileId) {
        Join<?, DataFile> dataFiles = root.join(FileProvidedMetadata_.providedFor).join(DataFileVersion_.dataFile);
        Predicate predicate = criteriaBuilder.equal(dataFiles.get(DataFile_.id), fileId);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<FileProvidedMetadata> byIncludedInVersion(long versionId) {
        Join<?, ContentVersion> versions = root.join(FileProvidedMetadata_.providedFor).join(
            DataFileVersion_.contentVersion);
        Predicate predicate = criteriaBuilder.equal(versions.get(ContentVersion_.id), versionId);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<FileProvidedMetadata> byMetadataFileName(String name) {
        Predicate predicate = criteriaBuilder.equal(root.get(FileProvidedMetadata_.filename), name);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<FileProvidedMetadata> byMetadataFileNameAndVersion(String name, long versionId) {
        Predicate predicate1 = criteriaBuilder.equal(root.get(FileProvidedMetadata_.filename), name);
        Join<?, ContentVersion> versions = root.join(FileProvidedMetadata_.providedFor).join(
            DataFileVersion_.contentVersion);
        Predicate predicate2 = criteriaBuilder.equal(versions.get(ContentVersion_.id), versionId);
        Predicate predicate = criteriaBuilder.and(predicate1, predicate2);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<FileProvidedMetadata> byMetadataFileNameAndFileIdAndVersion(String name, long versionId,
            long fileId) {
        Predicate predicate1 = criteriaBuilder.equal(root.get(FileProvidedMetadata_.filename), name);
        Join<?, ContentVersion> versions = root.join(FileProvidedMetadata_.providedFor).join(
            DataFileVersion_.contentVersion);
        Predicate predicate2 = criteriaBuilder.equal(versions.get(ContentVersion_.id), versionId);
        Join<?, DataFile> dataFiles = root.join(FileProvidedMetadata_.providedFor).join(DataFileVersion_.dataFile);
        Predicate predicate3 = criteriaBuilder.equal(dataFiles.get(DataFile_.id), fileId);
        Predicate predicate4 = criteriaBuilder.and(predicate1, predicate2);
        Predicate predicate = criteriaBuilder.and(predicate4, predicate3);
        return constructQueryFilter(predicate);
    }
}
