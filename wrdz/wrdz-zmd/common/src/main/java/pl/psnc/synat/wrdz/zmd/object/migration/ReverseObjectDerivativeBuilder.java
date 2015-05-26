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
import pl.psnc.synat.wrdz.zmd.object.ObjectDerivative;

/**
 * Reverse builder of a derivative of an object. Reverse means that this builder builds a derivative for remote object
 * that is a migration source for some object in this WRDZ system.
 */
public class ReverseObjectDerivativeBuilder {

    /**
     * Migration. A seed for the construction of a derivative of an object.
     */
    private Migration<?, ?> migration;


    /**
     * Constructs an object's derivative builder.
     * 
     * @param migration
     *            seed
     */
    public ReverseObjectDerivativeBuilder(Migration<?, ?> migration) {
        this.migration = migration;
    }


    /**
     * Build a derivative of an object on the basis of the seed.
     * 
     * @return object's derivative
     */
    public ObjectDerivative build() {
        if (migration == null) {
            return null;
        }
        ObjectDerivative derivative = new ObjectDerivative();
        derivative.setIdentifier(migration.getMigrationResult().getDefaultIdentifier().getIdentifier());
        derivative.setMigrationType(MigrationType.fromValue(migration.getType().name().toLowerCase()));
        if (migration.getDate() != null) {
            derivative.setMigrationDate(migration.getDate());
        }
        if (migration.getInfo() != null) {
            derivative.setMigrationInfo(migration.getInfo());
        }
        return derivative;
    }

}
