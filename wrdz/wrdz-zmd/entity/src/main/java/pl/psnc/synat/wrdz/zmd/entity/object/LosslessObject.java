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
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import org.eclipse.persistence.annotations.PrivateOwned;

import pl.psnc.synat.wrdz.zmd.entity.object.migration.Optimization;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;

/**
 * An abstract entity representing digital object that contains data i lossless format (either a master copy or
 * optimized copy).
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DO_TYPE", discriminatorType = DiscriminatorType.STRING)
public abstract class LosslessObject extends DigitalObject {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -6448331007529506008L;

    /**
     * All optimization operations performed on this object.
     */
    @PrivateOwned
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "source", cascade = { CascadeType.MERGE, CascadeType.PERSIST,
            CascadeType.REFRESH })
    protected List<Optimization> optimizedTo = new ArrayList<Optimization>();


    /**
     * Creates new instance of lossless object.
     * 
     * @param type
     *            Object's type.
     */
    public LosslessObject(ObjectType type) {
        super(type);
    }


    /**
     * Fetches objects create by optimization from this object.
     * 
     * @return list of objects or empty list if none was found.
     */
    public List<Optimization> getOptimizedTo() {
        return optimizedTo;
    }


    public void setOptimizedTo(List<Optimization> optimizedTo) {
        this.optimizedTo = optimizedTo;
    }

}
