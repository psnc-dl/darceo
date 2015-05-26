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

import javax.ejb.Stateless;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration;
import pl.psnc.synat.wrdz.zmd.input.MigrationInformation;

/**
 * Bean handling the creation of migration entities.
 */
@Stateless
public class ObjectMigrationBuilderBean implements ObjectMigrationBuilder {

    @SuppressWarnings("rawtypes")
    @Override
    public Migration buildMigration(MigrationInformation migrationInfo, MigrationDirection direction,
            DigitalObject mentionedObject) {
        Migration migration = initializeMigration(migrationInfo, direction, mentionedObject);
        migration.setDate(migrationInfo.getDate());
        String info = migrationInfo.getInfo();
        if (info != null) {
            if (info.length() > 512) {
                migration.setInfo(info.substring(0, 512));
            } else {
                migration.setInfo(info);
            }
        }
        return migration;
    }


    /**
     * Creates new migration object.
     * 
     * @param migrationInfo
     *            migration information object.
     * @param direction
     *            direction of migration.
     * @param object
     *            digital object at the other point of migration path.
     * @return newly created migration.
     */
    @SuppressWarnings("rawtypes")
    private Migration initializeMigration(MigrationInformation migrationInfo, MigrationDirection direction,
            DigitalObject object) {
        switch (direction) {
            case FROM:
                return createMigrationFromSource(migrationInfo, object);
            case TO:
                return createMigrationToResult(migrationInfo, object);
            default:
                throw new WrdzRuntimeException("Unrecognized migration operation.");
        }
    }


    /**
     * Creates new migration from the specified object.
     * 
     * @param migrationInfo
     *            migration information object.
     * @param object
     *            digital object being the source of the migration.
     * @return newly created migration.
     */
    @SuppressWarnings("rawtypes")
    private Migration createMigrationFromSource(MigrationInformation migrationInfo, DigitalObject object) {
        Migration migration = migrationInfo.getType().createNew();
        if (object == null) {
            migration.setSourceIdentifier(migrationInfo.getIdentifier());
            migration.setSourceIdentifierResolver(migrationInfo.getResolver());
        } else {
            migration.setMigrationSource(object);
            object.addDerivative(migration);
        }
        return migration;
    }


    /**
     * Creates new migration to the specified object.
     * 
     * @param migrationInfo
     *            migration information object.
     * @param object
     *            digital object being the result of the migration.
     * @return newly created migration.
     */
    @SuppressWarnings("rawtypes")
    private Migration createMigrationToResult(MigrationInformation migrationInfo, DigitalObject object) {
        Migration migration = migrationInfo.getType().createNew();
        if (object == null) {
            migration.setResultIdentifier(migrationInfo.getIdentifier());
            migration.setResultIdentifierResolver(migrationInfo.getResolver());
        } else {
            migration.setMigrationResult(object);
            object.addSource(migration);
        }
        return migration;
    }

}
