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
package pl.psnc.synat.wrdz.zmd.object.migration;

import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration;
import pl.psnc.synat.wrdz.zmd.input.MigrationInformation;
import pl.psnc.synat.wrdz.zmd.input.MigrationInformationUpdate;
import pl.psnc.synat.wrdz.zmd.object.ObjectDerivatives;
import pl.psnc.synat.wrdz.zmd.object.ObjectModificationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectNotFoundException;
import pl.psnc.synat.wrdz.zmd.object.ObjectOrigin;

/**
 * Specifies the interface of the manager of a digital object's migration.
 */
@Local
public interface ObjectMigrationManager {

    /**
     * Creates migration entity using migration information and migration direction data.
     * 
     * @param migrationInfo
     *            contains migration information from the user's request.
     * @param direction
     *            defines the direction of the migration (weather the object mentioned in the migrationInfo is a source
     *            object or a derivative object).
     * @return prepared, unpersisted migration entity.
     */
    @SuppressWarnings({ "rawtypes" })
    Migration createMigration(MigrationInformation migrationInfo, MigrationDirection direction);


    /**
     * Deletes migration information about object origin from the object.
     * 
     * @param object
     *            object to be cleared of origin migration information.
     */
    void deleteMigratedFrom(DigitalObject object);


    /**
     * Modifies information about object's migration from it's origins.
     * 
     * @param object
     *            object which information about migration from its origins is to be modified.
     * @param migrationInfo
     *            modification information and data.
     * @return updated migrated-from entry.
     */
    @SuppressWarnings("rawtypes")
    Migration modifyMigratedFrom(DigitalObject object, MigrationInformation migrationInfo);


    /**
     * Deletes specified object's migrated-to information.
     * 
     * @param object
     *            object which information about migration to its derivatives is to be deleted.
     * @param toDelete
     *            list of identifiers of derivative objects.
     * @throws ObjectModificationException
     *             if any problems with deletion occurs.
     */
    void deleteMigratedTo(DigitalObject object, Set<String> toDelete)
            throws ObjectModificationException;


    /**
     * Modifies object's migrated-to information.
     * 
     * @param object
     *            object which information about migration to its derivatives is to be modified.
     * @param toModify
     *            listo of modification information.
     * @throws ObjectModificationException
     *             if any problems with modification occurs.
     */
    void modifyMigratedTo(DigitalObject object, Set<MigrationInformationUpdate> toModify)
            throws ObjectModificationException;


    /**
     * Gets migration by identifier of the result (remote) object.
     * 
     * @param identifier
     *            identifier of the result object
     * @return migration
     * @throws MigrationNotFoundException
     *             when the any such migration does not exist
     */
    @SuppressWarnings("rawtypes")
    Migration getMigrationByResultIdentifier(String identifier)
            throws MigrationNotFoundException;


    /**
     * Gets migration by identifier of the source (remote) object.
     * 
     * @param identifier
     *            identifier of the source object
     * @return migrations
     * @throws MigrationNotFoundException
     *             when the any such migration does not exist
     */
    @SuppressWarnings("rawtypes")
    List<Migration> getMigrationsBySourceIdentifier(String identifier)
            throws MigrationNotFoundException;


    /**
     * Gets object's origin.
     * 
     * @param identifier
     *            public identifier of the object
     * @return object's origin
     * @throws ObjectNotFoundException
     *             when the object does not exist
     */
    ObjectOrigin getOrigin(String identifier)
            throws ObjectNotFoundException;


    /**
     * Gets object's derivatives.
     * 
     * @param identifier
     *            public identifier of the object
     * @return object's derivatives
     * @throws ObjectNotFoundException
     *             when the object does not exist
     */
    ObjectDerivatives getDerivatives(String identifier)
            throws ObjectNotFoundException;

}
