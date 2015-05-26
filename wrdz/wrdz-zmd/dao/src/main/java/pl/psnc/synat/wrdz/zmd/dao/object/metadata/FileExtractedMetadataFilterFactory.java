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
package pl.psnc.synat.wrdz.zmd.dao.object.metadata;

import java.util.Set;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileExtractedMetadata;

/**
 * Specified set of filters for queries concerning {@link FileExtractedMetadata} entities.
 */
public interface FileExtractedMetadataFilterFactory extends GenericQueryFilterFactory<FileExtractedMetadata> {

    /**
     * Filters the metadata entities by file they are extracted from.
     * 
     * @param fileIds
     *            IDs of files from which metadata has been extracted.
     * @return current representation of filters set.
     * @throws IllegalArgumentException
     *             if no file ID is given.
     */
    QueryFilter<FileExtractedMetadata> byExtractedFrom(Set<Long> fileIds)
            throws IllegalArgumentException;


    /**
     * Filters the metadata entities by file they are extracted from.
     * 
     * @param fileId
     *            ID of files from which metadata has been extracted.
     * @return current representation of filters set.
     */
    QueryFilter<FileExtractedMetadata> byExtractedFrom(long fileId);


    /**
     * Filters the metadata entities by metadata file name.
     * 
     * @param name
     *            metadata file name
     * @return current representation of filters set.
     */
    QueryFilter<FileExtractedMetadata> byMetadataFileName(String name);


    /**
     * Filters the metadata entities by version they are included in.
     * 
     * @param versionId
     *            ID of versions in which metadata has been included.
     * @return current representation of filters set.
     */
    QueryFilter<FileExtractedMetadata> byIncludedInVersion(long versionId);
}
