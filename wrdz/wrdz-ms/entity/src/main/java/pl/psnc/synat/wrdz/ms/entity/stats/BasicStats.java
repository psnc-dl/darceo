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
 * Represents a computed basic statistic.
 */
@Entity
@Table(name = "MS_BASIC_STATS", schema = "darceo")
public class BasicStats implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -9046554695406810185L;

    /** Primary id. */
    @Id
    @SequenceGenerator(name = "msBasicStatSequenceGenerator", sequenceName = "darceo.MS_BS_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "msBasicStatSequenceGenerator")
    @Column(name = "BS_ID", unique = true, nullable = false)
    private long id;

    /** The number of objects. */
    @Column(name = "BS_OBJECTS", nullable = false)
    private long objects;

    /** The number of data files. */
    @Column(name = "BS_DATA_FILES", nullable = false)
    private long dataFiles;

    /** The total size of data files. */
    @Column(name = "BS_DATA_SIZE", nullable = false)
    private long dataSize;

    /** The number of extracted metadata files. */
    @Column(name = "BS_EXTRACTED_METADATA_FILES", nullable = false)
    private long extractedMetadataFiles;

    /** The total size of extracted metadata files. */
    @Column(name = "BS_EXTRACTED_METADATA_SIZE", nullable = false)
    private long extractedMetadataSize;

    /** The number of provided metadata files. */
    @Column(name = "BS_PROVIDED_METADATA_FILES", nullable = false)
    private long providedMetadataFiles;

    /** The total size of provided metadata files. */
    @Column(name = "BS_PROVIDED_METADATA_SIZE", nullable = false)
    private long providedMetadataSize;

    /** User the statistic applies to. */
    @Column(name = "BS_USERNAME", length = 255, nullable = true)
    private String username;

    /** The date the statistic was computed on. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BS_COMPUTED_ON", nullable = false)
    private Date computedOn;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
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


    public long getExtractedMetadataFiles() {
        return extractedMetadataFiles;
    }


    public void setExtractedMetadataFiles(long extractedMetadataFiles) {
        this.extractedMetadataFiles = extractedMetadataFiles;
    }


    public long getExtractedMetadataSize() {
        return extractedMetadataSize;
    }


    public void setExtractedMetadataSize(long extractedMetadataSize) {
        this.extractedMetadataSize = extractedMetadataSize;
    }


    public long getProvidedMetadataFiles() {
        return providedMetadataFiles;
    }


    public void setProvidedMetadataFiles(long providedMetadataFiles) {
        this.providedMetadataFiles = providedMetadataFiles;
    }


    public long getProvidedMetadataSize() {
        return providedMetadataSize;
    }


    public void setProvidedMetadataSize(long providedMetadataSize) {
        this.providedMetadataSize = providedMetadataSize;
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
