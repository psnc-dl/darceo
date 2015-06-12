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
package pl.psnc.synat.wrdz.zmd.object.identifier;

import pl.psnc.synat.wrdz.zmd.dao.object.IdentifierDao;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier;

/**
 * Specifies a common interface for object identifier generators.
 */
public interface IdentifierGenerator {

    /**
     * Generates new {@link Identifier} entity for a given digital object.
     * 
     * @param identifierDao
     *            data access object for {@link Identifier} entities.
     * @param object
     *            digital object for which the identifier is generated.
     * @param proposedId
     *            proposed id part of the object identifier
     * @return generated and persisted identifier.
     */
    Identifier generateIdentifier(IdentifierDao identifierDao, DigitalObject object, String proposedId);

}
