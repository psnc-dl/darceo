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

import java.util.Date;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;

/**
 * Specified set of filters for queries concerning {@link ContentVersion} entities.
 */
public interface ContentVersionFilterFactory extends GenericQueryFilterFactory<ContentVersion> {

    /**
     * Filters the entities by the creation date matching all created before the given date.
     * 
     * @param date
     *            reference date
     * @param inclusive
     *            if <code>true</code> will cause referenced date to be included in searched boundary, else it would
     *            exclude it.
     * @return current representations of filters set
     */
    QueryFilter<ContentVersion> byCreatedBefore(Date date, boolean inclusive);


    /**
     * Filters the entities by the creation date matching all created after the given date.
     * 
     * @param date
     *            reference date
     * @param inclusive
     *            if <code>true</code> will cause referenced date to be included in searched boundary, else it would
     *            exclude it.
     * @return current representations of filters set
     */
    QueryFilter<ContentVersion> byCreatedAfter(Date date, boolean inclusive);


    /**
     * Filters the entities by the digital object that is versioned, object being represented by its identifier (primary
     * key value).
     * 
     * @param id
     *            identifier (primary key value) of digital object being versioned
     * @return current representations of filters set
     */
    QueryFilter<ContentVersion> byObjectId(Long id);


    /**
     * Filters the entities by the version number.
     * 
     * @param versionNo
     *            number of version
     * @return current representations of filters set
     */
    QueryFilter<ContentVersion> byVersion(Integer versionNo);


    /**
     * Filters the entities by the version number being greater than passed one.
     * 
     * @param versionNo
     *            number of version
     * @return current representations of filters set
     */
    QueryFilter<ContentVersion> byVersionNewerThan(Integer versionNo);

}
