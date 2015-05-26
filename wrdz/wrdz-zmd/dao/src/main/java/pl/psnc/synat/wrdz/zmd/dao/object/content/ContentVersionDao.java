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

import javax.ejb.Local;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDao;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;

/**
 * An interface for a class managing the persistence of {@link ContentVersion} class. It declares additional operations
 * available for {@link ContentVersion} object apart from basic contract defined in {@link ExtendedGenericDao}.
 */
@Local
public interface ContentVersionDao extends
        ExtendedGenericDao<ContentVersionFilterFactory, ContentVersionSorterBuilder, ContentVersion, Long> {

    /**
     * Fetches specified content version of the target digital object.
     * 
     * @param id
     *            target digital object's entity primary key value.
     * @param versionNo
     *            target digital object's version number.
     * @return matching content version or <code>null</code> if it wasn't found.
     */
    public ContentVersion getContentVersion(long id, int versionNo);

}
