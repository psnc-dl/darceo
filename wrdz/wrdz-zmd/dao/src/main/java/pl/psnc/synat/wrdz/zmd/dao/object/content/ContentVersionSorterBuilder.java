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

import pl.psnc.synat.wrdz.common.dao.GenericQuerySorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;

/**
 * Defines methods producing sorters for queries concerning {@link ContentVersion} entities.
 */
public interface ContentVersionSorterBuilder extends GenericQuerySorterBuilder<ContentVersion> {

    /**
     * Enables sorting by version number of the associated {@link ContentVersion}.
     * 
     * @param ascendingly
     *            if <code>true</code> the sorting order will be ascending, else it will be descending.
     * @return modified instance of the builder with updated sorters list
     */
    ContentVersionSorterBuilder byVersion(boolean ascendingly);

}
