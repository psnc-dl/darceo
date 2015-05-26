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

import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration;
import pl.psnc.synat.wrdz.zmd.object.MigrationType;
import pl.psnc.synat.wrdz.zmd.object.ObjectOrigin;

/**
 * Reverse builder of an origin of an object. Reverse means that this builder builds an origin for remote object that is
 * a migration result of some object in this WRDZ system.
 */
public class ReverseObjectOriginBuilder {

    /**
     * Migration. A seed for the construction of an origin of an object.
     */
    private Migration<?, ?> migration;


    /**
     * Constructs an object's origin builder.
     * 
     * @param migration
     *            seed
     */
    public ReverseObjectOriginBuilder(Migration<?, ?> migration) {
        this.migration = migration;
    }


    /**
     * Build an origin of an object on the basis of the seed.
     * 
     * @return object's origin
     */
    public ObjectOrigin build() {
        if (migration == null) {
            return null;
        }
        ObjectOrigin origin = new ObjectOrigin();
        origin.setIdentifier(migration.getMigrationSource().getDefaultIdentifier().getIdentifier());
        origin.setMigrationType(MigrationType.fromValue(migration.getType().name().toLowerCase()));
        if (migration.getDate() != null) {
            origin.setMigrationDate(migration.getDate());
        }
        if (migration.getInfo() != null) {
            origin.setMigrationInfo(migration.getInfo());
        }
        return origin;
    }

}
