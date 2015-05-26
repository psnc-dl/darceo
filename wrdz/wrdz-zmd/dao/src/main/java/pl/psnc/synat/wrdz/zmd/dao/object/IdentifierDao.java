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
package pl.psnc.synat.wrdz.zmd.dao.object;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDao;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier;

/**
 * An interface for a class managing the persistence of {@link Identifier} class. It declares additional operations
 * available for {@link Identifier} object apart from basic contract defined in {@link ExtendedGenericDao}.
 */
@Local
public interface IdentifierDao extends
        ExtendedGenericDao<IdentifierFilterFactory, IdentifierSorterBuilder, Identifier, Long> {

    /**
     * Returns the active identifier immediately following the identifier with the given value when entities are sorted
     * according to their id property.
     * 
     * When called with <code>null</code> argument, this method returns the first active identifier.
     * 
     * @param previousIdentifierValue
     *            identifier value preceding the desired one; can be <code>null</code>
     * @return first active identifier with value following the given one, or <code>null</code> if no such identifiers
     *         exist
     */
    Identifier findNextActiveIdentifier(String previousIdentifierValue);


    /**
     * Returns the identifier values of digital objects that:
     * <ul>
     * <li>contain files in a format with the given puid,</li>
     * <li>have not been migrated from (the type of migration checked depends on the type of the object), and</li>
     * <li>are owned by the user with the given id (optional), and</li>
     * <li>their database id is in the given id collection (optional).</li>
     * </ul>
     * 
     * @param puid
     *            data file format puid to look for
     * @param ownerId
     *            digital object owner id; can be <code>null</code>
     * @param objectIds
     *            digital object database identifiers; can be <code>null</code>
     * @return a list of identifier values
     * @see Identifier#getIdentifier()
     */
    List<String> getIdentifiersForMigration(String puid, Long ownerId, Collection<Long> objectIds);
}
