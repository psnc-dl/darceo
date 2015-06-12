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
package pl.psnc.synat.wrdz.ms.entity.stats;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Represents computed metadata format statistics.
 */
@Entity
@Table(name = "MS_METADATA_FORMAT_STATS", schema = "darceo")
public class MetadataFormatStat implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -7775240747339646127L;

    /** Primary id. */
    @Id
    @SequenceGenerator(name = "msMetadataFormatStatSequenceGenerator", sequenceName = "darceo.MS_MFS_ID_SEQ",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "msMetadataFormatStatSequenceGenerator")
    @Column(name = "MFS_ID", unique = true, nullable = false)
    private long id;

    /** Metadata format name. */
    @Column(name = "MFS_FORMAT_NAME", length = 255, nullable = false)
    private String formatName;

    /** Object count. */
    @Column(name = "MFS_OBJECTS", nullable = false)
    private long objects;

    /** Data file count. */
    @Column(name = "MFS_DATA_FILES", nullable = false)
    private long dataFiles;

    /** The date the statistic was computed on. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MFS_COMPUTED_ON", nullable = false)
    private Date computedOn;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getFormatName() {
        return formatName;
    }


    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }


    public long getObjects() {
        return objects;
    }


    public void setObjects(long objects) {
        this.objects = objects;
    }


    public long getDataFiles() {
        return dataFiles;
    }


    public void setDataFiles(long dataFiles) {
        this.dataFiles = dataFiles;
    }


    public Date getComputedOn() {
        return computedOn;
    }


    public void setComputedOn(Date computedOn) {
        this.computedOn = computedOn;
    }
}
