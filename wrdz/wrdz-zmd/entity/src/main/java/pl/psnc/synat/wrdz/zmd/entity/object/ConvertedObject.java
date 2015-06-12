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
import pl.psnc.synat.wrdz.zmd.entity.types.MigrationType;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;

/**
 * An entity representing converted digital object, i.e. object that is a presentation version and uses lossy
 * compression of data.
 */
@Entity
@DiscriminatorValue("CONVERTED")
public class ConvertedObject extends DigitalObject {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 6193565313853872248L;

    /**
     * Conversion that resulted in creation of this object.
     */
    @PrivateOwned
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "result", optional = true, cascade = { CascadeType.ALL })
    private Conversion convertedFrom;


    /**
     * Creates new instance of converted object.
     */
    public ConvertedObject() {
        super(ObjectType.CONVERTED);
    }


    public void setConvertedFrom(Conversion convertedFrom) {
        this.convertedFrom = convertedFrom;
    }


    public Conversion getConvertedFrom() {
        return convertedFrom;
    }


    @SuppressWarnings("rawtypes")
    @Override
    public Migration getOrigin() {
        return getConvertedFrom();
    }


    @SuppressWarnings("rawtypes")
    @Override
    public void addDerivative(Migration migration) {
        if (migration.getType() == MigrationType.CONVERSION) {
            getConvertedTo().add((Conversion) migration);
        } else {
            throw new IllegalArgumentException("Bad type of migration,  converted object cannot be source of "
                    + migration.getType() + "operation.");
        }

    }


    @SuppressWarnings("rawtypes")
    @Override
    public void addSource(Migration migration) {
        if (migration.getType() == MigrationType.CONVERSION) {
            setConvertedFrom((Conversion) migration);
        } else {
            throw new IllegalArgumentException("Bad type of migration,  converted object cannot be result of "
                    + migration.getType() + "operation.");
        }
    }

}
