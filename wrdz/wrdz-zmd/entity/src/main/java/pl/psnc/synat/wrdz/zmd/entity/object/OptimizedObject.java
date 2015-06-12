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
package pl.psnc.synat.wrdz.zmd.entity.object;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import org.eclipse.persistence.annotations.PrivateOwned;

import pl.psnc.synat.wrdz.zmd.entity.object.migration.Conversion;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Optimization;
import pl.psnc.synat.wrdz.zmd.entity.types.MigrationType;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;

/**
 * An entity representing optimized digital object, i.e. object that is a result of optimization of master object and is
 * stored in a lossless format. Optimized version of the object is basically a master version with slight modifications
 * of meaningless data e.g. excessive image borders from the scanner.
 */
@Entity
@DiscriminatorValue("OPTIMIZED")
public class OptimizedObject extends LosslessObject {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -520837920283206362L;

    /**
     * Optimization that resulted in creation of this object.
     */
    @PrivateOwned
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "result", optional = true, cascade = { CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH })
    private Optimization optimizedFrom;


    /**
     * Creates new instance of optimized object.
     */
    public OptimizedObject() {
        super(ObjectType.OPTIMIZED);
    }


    public void setOptimizedFrom(Optimization optimizedFrom) {
        this.optimizedFrom = optimizedFrom;
    }


    public Optimization getOptimizedFrom() {
        return optimizedFrom;
    }


    @SuppressWarnings("rawtypes")
    @Override
    public void addDerivative(Migration migration) {
        if (migration.getType() == MigrationType.CONVERSION) {
            getConvertedTo().add((Conversion) migration);
        } else if (migration.getType() == MigrationType.OPTIMIZATION) {
            getOptimizedTo().add((Optimization) migration);
        } else {
            throw new IllegalArgumentException("Bad type of migration,  converted object cannot be source of "
                    + migration.getType() + "operation.");
        }
    }


    @SuppressWarnings("rawtypes")
    @Override
    public void addSource(Migration migration) {
        if (migration.getType() == MigrationType.OPTIMIZATION) {
            setOptimizedFrom((Optimization) migration);
        } else {
            throw new IllegalArgumentException("Bad type of migration,  converted object cannot be result of "
                    + migration.getType() + "operation.");
        }
    }


    @SuppressWarnings("rawtypes")
    @Override
    public Migration getOrigin() {
        return getOptimizedFrom();
    }

}
