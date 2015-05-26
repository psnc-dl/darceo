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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.eclipse.persistence.annotations.PrivateOwned;

import pl.psnc.synat.wrdz.zmd.entity.object.migration.Conversion;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Optimization;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Transformation;
import pl.psnc.synat.wrdz.zmd.entity.types.MigrationType;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;

/**
 * An entity representing master digital object, i.e. object that is a result of digitalization or transformation
 * performed on it's parent master object and is stored in a lossless format.
 */
@Entity
@DiscriminatorValue("MASTER")
public class MasterObject extends LosslessObject {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -681349525518473212L;

    /**
     * Transformation that resulted in creation of this object.
     */
    @PrivateOwned
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "result", optional = true, cascade = { CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH })
    private Transformation transformedFrom;

    /**
     * All transformation operations performed on this object.
     */
    @PrivateOwned
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "source", cascade = { CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REFRESH })
    private List<Transformation> transformedTo = new ArrayList<Transformation>();


    /**
     * Creates new instance of master object.
     */
    public MasterObject() {
        super(ObjectType.MASTER);
    }


    public Transformation getTransformedFrom() {
        return transformedFrom;
    }


    public void setTransformedFrom(Transformation transformedFrom) {
        this.transformedFrom = transformedFrom;
    }


    /**
     * Fetches objects create by transformation from this object.
     * 
     * @return list of objects or empty list if none was found.
     */
    public List<Transformation> getTransformedTo() {
        return transformedTo;
    }


    public void setTransformedTo(List<Transformation> transformedTo) {
        this.transformedTo = transformedTo;
    }


    @SuppressWarnings("rawtypes")
    @Override
    public void addDerivative(Migration migration) {
        if (migration.getType() == MigrationType.CONVERSION) {
            getConvertedTo().add((Conversion) migration);
        } else if (migration.getType() == MigrationType.OPTIMIZATION) {
            getOptimizedTo().add((Optimization) migration);
        } else if (migration.getType() == MigrationType.TRANSFORMATION) {
            getTransformedTo().add((Transformation) migration);
        } else {
            throw new IllegalArgumentException("Bad type of migration,  converted object cannot be source of "
                    + migration.getType() + "operation.");
        }
    }


    @SuppressWarnings("rawtypes")
    @Override
    public void addSource(Migration migration) {
        if (migration.getType() == MigrationType.TRANSFORMATION) {
            setTransformedFrom((Transformation) migration);
        } else {
            throw new IllegalArgumentException("Bad type of migration,  converted object cannot be result of "
                    + migration.getType() + "operation.");
        }
    }


    @SuppressWarnings("rawtypes")
    @Override
    public Migration getOrigin() {
        return getTransformedFrom();
    }

}
