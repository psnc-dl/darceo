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
package pl.psnc.synat.wrdz.zmd.entity.object.hash;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import pl.psnc.synat.wrdz.zmd.entity.types.HashType;

/**
 * Represents hashes for files.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "FH_TYPE", discriminatorType = DiscriminatorType.STRING)
@Table(name = "ZMD_FILE_HASHES", schema = "darceo", uniqueConstraints = { @UniqueConstraint(columnNames = {
        "FH_METADATAFILE", "FH_DATAFILE", "FH_HASH_TYPE" }) })
public abstract class FileHash implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -5347414124910737690L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "fileHashSequenceGenerator", sequenceName = "darceo.ZMD_FH_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fileHashSequenceGenerator")
    @Column(name = "FH_ID", unique = true, nullable = false)
    protected long id;

    /**
     * Type of hash / hashing algorithm.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "FH_HASH_TYPE", length = 10, nullable = false)
    protected HashType hashType;

    /**
     * Hash value.
     */
    @Column(name = "FH_HASH_VALUE", length = 128, nullable = false)
    protected String hashValue;

    /**
     * Entity type.
     */
    @Column(name = "FH_TYPE", length = 12, nullable = false)
    protected String type;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public HashType getHashType() {
        return hashType;
    }


    public void setHashType(HashType hashType) {
        this.hashType = hashType;
    }


    public String getHashValue() {
        return hashValue;
    }


    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }


    protected String getType() {
        return type;
    }


    protected void setType(String type) {
        this.type = type;
    }

}
