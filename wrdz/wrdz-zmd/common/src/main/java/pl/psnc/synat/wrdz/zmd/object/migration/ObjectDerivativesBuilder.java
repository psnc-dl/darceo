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

import java.util.ArrayList;
import java.util.List;

import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration;
import pl.psnc.synat.wrdz.zmd.object.ObjectDerivatives;

/**
 * Builder of derivatives of an object.
 */
public class ObjectDerivativesBuilder {

    /**
     * Migration. A seed for the construction of derivatives of an object.
     */
    private List<Migration<?, ?>> migrations;


    /**
     * Constructs an abject's derivatives builder.
     * 
     * @param list
     *            seed
     */
    public ObjectDerivativesBuilder(List<Migration<?, ?>> list) {
        this.migrations = new ArrayList<Migration<?, ?>>();
        if (list != null) {
            this.migrations.addAll(list);
        }
    }


    /**
     * Adds migrations to the seed.
     * 
     * @param migrations
     *            migrations
     * @return this builder
     */
    public ObjectDerivativesBuilder addMigrations(List<Migration<?, ?>> migrations) {
        if (migrations != null) {
            this.migrations.addAll(migrations);
        }
        return this;
    }


    /**
     * Build derivatives of an object on the basis of the seed.
     * 
     * @return object's derivatives
     */
    public ObjectDerivatives build() {
        if (migrations.isEmpty()) {
            return null;
        }
        ObjectDerivatives derivatives = new ObjectDerivatives();
        for (Migration<?, ?> migration : migrations) {
            derivatives.getObjectDerivative().add(new ObjectDerivativeBuilder(migration).build());
        }
        return derivatives;
    }

}
