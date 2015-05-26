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
package pl.psnc.synat.wrdz.zmd.object;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

/**
 * Provides access to the identifiers of stored digital objects.
 */
@Remote
public interface IdentifierBrowser {

    /**
     * Returns the active identifier immediately following the given identifier.
     * 
     * When called with <code>null</code> argument, this method returns the first active identifier.
     * 
     * @param previousIdentifier
     *            identifier preceding the desired one; can be <code>null</code>
     * @return first active identifier following the given one, or <code>null</code> if no such identifiers exist
     */
    String findNextActiveIdentifier(String previousIdentifier);


    /**
     * Returns the identifier values of digital objects that:
     * <ul>
     * <li>contain files in a format with the given puid,</li>
     * <li>have not been migrated from (the type of migration checked depends on the type of the object), and</li>
     * <li>are owned by the given user (optional),</li>
     * <li>their database id is in the given id collection (optional).</li>
     * </ul>
     * 
     * @param puid
     *            data file format puid to look for
     * @param owner
     *            digital object owner username; can be <code>null</code>
     * @param objectIds
     *            digital object database identifiers; can be <code>null</code>
     * @return a list of identifier values
     */
    List<String> getIdentifiersForMigration(String puid, String owner, Collection<Long> objectIds);


    /**
     * Returns the database id of the digital object with the given identifier.
     * 
     * @param identifier
     *            object identifier
     * @return database id, or <code>null</code> if the given identifier does not exist
     */
    Long getObjectId(String identifier);
}
