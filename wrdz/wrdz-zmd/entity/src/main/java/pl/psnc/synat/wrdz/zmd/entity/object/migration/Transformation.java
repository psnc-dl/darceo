/**
 * 
 */
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
package pl.psnc.synat.wrdz.zmd.entity.object.migration;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import pl.psnc.synat.wrdz.zmd.entity.object.MasterObject;
import pl.psnc.synat.wrdz.zmd.entity.types.MigrationType;

/**
 * An entity representing operation of transformation, i.e. lossless transmutation of master digital object into new
 * object of type {@link MasterObject}.
 */
@Entity
@DiscriminatorValue("TRANSFORMATION")
public class Transformation extends Migration<MasterObject, MasterObject> {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 5760703719654579131L;

    /**
     * Source object being transformed.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "M_SOURCE_OBJECT_ID", nullable = true, updatable = false, insertable = false)
    private MasterObject source;

    /**
     * Result of transformation.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "M_RESULT_OBJECT_ID", nullable = true, unique = true, updatable = false, insertable = false)
    private MasterObject result;


    /**
     * Creates new instance of transformation operation.
     */
    public Transformation() {
        super(MigrationType.TRANSFORMATION);
    }


    public MasterObject getSource() {
        return source;
    }


    public void setSource(MasterObject source) {
        this.source = source;
    }


    public MasterObject getResult() {
        return result;
    }


    public void setResult(MasterObject result) {
        this.result = result;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int res = 1;
        res = prime * res + (int) (id ^ (id >>> 32));
        res = prime * res + ((resultIdentifier == null) ? 0 : resultIdentifier.hashCode());
        res = prime * res + ((sourceIdentifier == null) ? 0 : sourceIdentifier.hashCode());
        res = prime * res + ((resultIdentifierResolver == null) ? 0 : resultIdentifierResolver.hashCode());
        res = prime * res + ((sourceIdentifierResolver == null) ? 0 : sourceIdentifierResolver.hashCode());
        res = prime * res + ((type == null) ? 0 : type.hashCode());
        res = prime * res + ((source == null) ? 0 : (int) (source.getId() ^ (source.getId() >>> 32)));
        res = prime * res + ((this.result == null) ? 0 : (int) (this.result.getId() ^ (this.result.getId() >>> 32)));
        return res;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Conversion)) {
            return false;
        }
        Conversion other = (Conversion) obj;
        if (id != other.getId()) {
            return false;
        }
        if (type != other.getType()) {
            return false;
        }
        if (resultIdentifier == null) {
            if (other.getResultIdentifier() != null) {
                return false;
            }
        } else if (!resultIdentifier.equals(other.getResultIdentifier())) {
            return false;
        }
        if (sourceIdentifier == null) {
            if (other.getSourceIdentifier() != null) {
                return false;
            }
        } else if (!sourceIdentifier.equals(other.getSourceIdentifier())) {
            return false;
        }
        if (resultIdentifierResolver == null) {
            if (other.getResultIdentifierResolver() != null) {
                return false;
            }
        } else if (!resultIdentifierResolver.equals(other.getResultIdentifierResolver())) {
            return false;
        }
        if (sourceIdentifierResolver == null) {
            if (other.getSourceIdentifierResolver() != null) {
                return false;
            }
        } else if (!sourceIdentifierResolver.equals(other.getSourceIdentifierResolver())) {
            return false;
        }
        if (source == null) {
            if (other.getSource() != null) {
                return false;
            }
        } else if (other.getSource() != null) {
            if (source.getId() != other.getSource().getId()) {
                return false;
            }
        } else {
            return false;
        }
        if (result == null) {
            if (other.getResult() != null) {
                return false;
            }
        } else if (other.getResult() != null) {
            if (result.getId() != other.getResult().getId()) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Conversion [id=" + id);
        if (source == null) {
            sb.append(", source=null");
        } else {
            sb.append(", source=[id=" + source.getId() + "]");
        }
        sb.append(", sourceIdentifier=" + sourceIdentifier);
        sb.append(", sourceIdentifierResolver=" + sourceIdentifierResolver);
        if (result == null) {
            sb.append(", result=null");
        } else {
            sb.append(", result=[id=" + result.getId() + "]");
        }
        sb.append(", resultIdentifier=" + resultIdentifier + ", resultIdentifierResolver=" + resultIdentifierResolver
                + ", type=" + type + "]");
        return sb.toString();
    }

}
