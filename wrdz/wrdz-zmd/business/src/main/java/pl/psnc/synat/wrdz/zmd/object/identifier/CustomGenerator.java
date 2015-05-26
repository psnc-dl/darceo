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

import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.dao.object.IdentifierDao;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier;
import pl.psnc.synat.wrdz.zmd.entity.types.IdentifierType;

/**
 * Generator of custom identifiers. Uses singleton pattern.
 */
public final class CustomGenerator implements IdentifierGenerator {

    /**
     * Hidden singleton instance of the class.
     */
    private static CustomGenerator instance;


    /**
     * Hidden default constructor.
     */
    private CustomGenerator() {
    }


    /**
     * Gets the existing instance of the generator of creates new one.
     * 
     * @param configuration
     *            zmd module configuration.
     * @return instance of the generator.
     */
    public static IdentifierGenerator getGenerator(ZmdConfiguration configuration) {
        if (instance == null) {
            synchronized (CustomGenerator.class) {
                if (instance == null) {
                    instance = new CustomGenerator();
                }
            }
        }
        return instance;
    }


    @Override
    public Identifier generateIdentifier(IdentifierDao identifierDao, DigitalObject object, String proposedId) {
        Identifier identifier = new Identifier();
        identifier.setObject(object);
        identifier.setIsActive(true);
        identifier.setType(IdentifierType.CUSTOM);
        identifierDao.persist(identifier);
        if (proposedId == null) {
            identifier.setIdentifier(Long.toString(identifier.getId()));
        } else {
            identifier.setIdentifier(proposedId);
        }
        return identifier;
    }
}
