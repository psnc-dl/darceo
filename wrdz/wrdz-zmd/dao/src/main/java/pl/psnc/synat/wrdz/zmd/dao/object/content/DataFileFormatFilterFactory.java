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
package pl.psnc.synat.wrdz.zmd.dao.object.content;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileFormat;

/**
 * Specified set of filters for queries concerning {@link DataFileFormat} entities.
 */
public interface DataFileFormatFilterFactory extends GenericQueryFilterFactory<DataFileFormat> {

    /**
     * Filters the entities by the PUID identifier.
     * 
     * @param puid
     *            identifier
     * @return current representations of filters set
     */
    QueryFilter<DataFileFormat> byPuid(String puid);


    /**
     * Filters the entities by the mime type.
     * 
     * @param mimeType
     *            mime type
     * @return current representations of filters set
     */
    QueryFilter<DataFileFormat> byMimeType(String mimeType);


    /**
     * Filters the entities by the version.
     * 
     * @param version
     *            version
     * @return current representations of filters set
     */
    QueryFilter<DataFileFormat> byVersion(String version);


    /**
     * Filters the entities by id greater or equal to the specified value.
     * 
     * @param minId
     *            minimal value of the identifier.
     * @return current representations of filters set.
     */
    QueryFilter<DataFileFormat> byIdGreaterThan(long minId);

}
