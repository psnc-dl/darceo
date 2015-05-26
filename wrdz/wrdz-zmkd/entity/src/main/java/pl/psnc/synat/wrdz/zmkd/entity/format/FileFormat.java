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
package pl.psnc.synat.wrdz.zmkd.entity.format;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Represents a file format using bot the UDFR and PRONOM identifiers.
 */
@Entity(name = "ZmkdFileFormat")
@Table(name = "ZMKD_FILE_FORMATS", schema = "darceo")
public class FileFormat implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 2190156075669142995L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "zmkdFileFormatSequenceGenerator", sequenceName = "darceo.ZMKD_FF_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "zmkdFileFormatSequenceGenerator")
    @Column(name = "FF_ID", unique = true, nullable = false)
    private long id;

    /**
     * PUID.
     */
    @Column(name = "FF_PUID", unique = true, nullable = false, length = 50)
    private String puid;

    /**
     * UDFR IRI.
     */
    @Column(name = "FF_UDFR_IRI", nullable = true, unique = true, length = 255)
    private String udfrIri;

    /**
     * Default file extension.
     */
    @Column(name = "FF_EXTENSION", nullable = true, unique = false, length = 16)
    private String extension;

    /**
     * Mimetype.
     */
    @Column(name = "FF_MIMETYPE", nullable = true, unique = false, length = 100)
    private String mimetype;


    /**
     * Default constructor.
     */
    public FileFormat() {
    }


    /**
     * Constructor.
     * 
     * @param puid
     *            PUID
     */
    public FileFormat(String puid) {
        this.puid = puid;
    }


    /**
     * Constructor.
     * 
     * @param puid
     *            PUID
     * @param udfrIri
     *            UDFR IRI
     * @param extension
     *            default extension
     * @param mimetype
     *            mimetype
     */
    public FileFormat(String puid, String udfrIri, String extension, String mimetype) {
        this.puid = puid;
        this.udfrIri = udfrIri;
        this.extension = extension;
        this.mimetype = mimetype;
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getPuid() {
        return puid;
    }


    public void setPuid(String puid) {
        this.puid = puid;
    }


    public String getUdfrIri() {
        return udfrIri;
    }


    public void setUdfrIri(String udfrIri) {
        this.udfrIri = udfrIri;
    }


    public String getExtension() {
        return extension;
    }


    public void setExtension(String extension) {
        this.extension = extension;
    }


    public String getMimetype() {
        return mimetype;
    }


    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((puid == null) ? 0 : puid.hashCode());
        result = prime * result + ((udfrIri == null) ? 0 : udfrIri.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FileFormat other = (FileFormat) obj;
        if (puid == null) {
            if (other.puid != null) {
                return false;
            }
        } else if (!puid.equals(other.puid)) {
            return false;
        }
        if (udfrIri == null) {
            if (other.udfrIri != null) {
                return false;
            }
        } else if (!udfrIri.equals(other.udfrIri)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("FileFormat ");
        sb.append("[puid = ").append(puid);
        sb.append(", udfrIri = ").append(udfrIri);
        sb.append(", extension = ").append(extension);
        sb.append(", mimetype = ").append(mimetype);
        sb.append("]");
        return sb.toString();
    }

}
