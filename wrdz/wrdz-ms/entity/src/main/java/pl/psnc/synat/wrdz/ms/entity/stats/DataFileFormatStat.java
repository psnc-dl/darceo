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
 * Represents computed data file format statistics.
 */
@Entity
@Table(name = "MS_DATA_FILE_FORMAT_STATS", schema = "darceo")
public class DataFileFormatStat implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -4064013375543549215L;

    /** Primary id. */
    @Id
    @SequenceGenerator(name = "msDataFileFormatStatSequenceGenerator", sequenceName = "darceo.MS_DFFS_ID_SEQ",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "msDataFileFormatStatSequenceGenerator")
    @Column(name = "DFFS_ID", unique = true, nullable = false)
    private long id;

    /** Data file format PUID. */
    @Column(name = "DFFS_FORMAT_PUID", length = 255, nullable = false)
    private String formatPuid;

    /** Object count. */
    @Column(name = "DFFS_OBJECTS", nullable = false)
    private long objects;

    /** Data file count. */
    @Column(name = "DFFS_DATA_FILES", nullable = false)
    private long dataFiles;

    /** Data size. */
    @Column(name = "DFFS_DATA_SIZE", nullable = false)
    private long dataSize;

    /** User the statistic applies to. */
    @Column(name = "DFFS_USERNAME", length = 255, nullable = true)
    private String username;

    /** The date the statistic was computed on. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DFFS_COMPUTED_ON", nullable = false)
    private Date computedOn;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getFormatPuid() {
        return formatPuid;
    }


    public void setFormatPuid(String formatPuid) {
        this.formatPuid = formatPuid;
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


    public long getDataSize() {
        return dataSize;
    }


    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public Date getComputedOn() {
        return computedOn;
    }


    public void setComputedOn(Date computedOn) {
        this.computedOn = computedOn;
    }
}
