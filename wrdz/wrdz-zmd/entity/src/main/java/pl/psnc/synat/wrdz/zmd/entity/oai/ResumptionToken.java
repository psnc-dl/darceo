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
package pl.psnc.synat.wrdz.zmd.entity.oai;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;

/**
 * OAI-PMH operations resumption token entity.
 */
@Entity
@Table(name = "ZMD_RESUMPTION_TOKENS", schema = "darceo")
public class ResumptionToken implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1894267163085188668L;

    /**
     * Token's value, being entity's ID.
     */
    @Id
    @Column(name = "RT_ID", unique = true, nullable = false)
    private String id;

    /**
     * Lower bound of period being harvested.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "RT_FROM", nullable = false)
    private Date from;

    /**
     * Upper bound of period being harvested.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "RT_UNTIL", nullable = false)
    private Date until;

    /**
     * Metadata prefix.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "RT_MD_PREFIX", nullable = false)
    private NamespaceType prefix;

    /**
     * Specified set.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "RT_SET")
    private ObjectType set;

    /**
     * Request type, having value of either <code>LIST_IDENTIFIERS</code> or <code>LIST_RECORDS</code>.
     */
    @Column(name = "RT_TYPE", nullable = false)
    private String type;

    /**
     * Token's expiration date.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "RT_EXPIRES", nullable = false)
    private Date expirationDate;

    /**
     * Request offset.
     */
    @Column(name = "RT_OFFSET", nullable = false)
    private int offset;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public Date getFrom() {
        return from;
    }


    public void setFrom(Date from) {
        this.from = from;
    }


    public Date getUntil() {
        return until;
    }


    public void setUntil(Date until) {
        this.until = until;
    }


    public NamespaceType getPrefix() {
        return prefix;
    }


    public void setPrefix(NamespaceType prefix) {
        this.prefix = prefix;
    }


    public ObjectType getSet() {
        return set;
    }


    public void setSet(ObjectType set) {
        this.set = set;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public Date getExpirationDate() {
        return expirationDate;
    }


    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }


    public int getOffset() {
        return offset;
    }


    public void setOffset(int offset) {
        this.offset = offset;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expirationDate == null) ? 0 : expirationDate.hashCode());
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (int) (offset ^ (offset >>> 32));
        result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
        result = prime * result + ((set == null) ? 0 : set.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((until == null) ? 0 : until.hashCode());
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
        if (!(obj instanceof ResumptionToken)) {
            return false;
        }
        ResumptionToken other = (ResumptionToken) obj;
        if (expirationDate == null) {
            if (other.expirationDate != null) {
                return false;
            }
        } else if (!expirationDate.equals(other.expirationDate)) {
            return false;
        }
        if (from == null) {
            if (other.from != null) {
                return false;
            }
        } else if (!from.equals(other.from)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (offset != other.offset) {
            return false;
        }
        if (prefix != other.prefix) {
            return false;
        }
        if (set != other.set) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        if (until == null) {
            if (other.until != null) {
                return false;
            }
        } else if (!until.equals(other.until)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "ResumptionToken [id=" + id + ", from=" + from + ", until=" + until + ", prefix=" + prefix + ", set="
                + set + ", type=" + type + ", expirationDate=" + expirationDate + ", offset=" + offset + "]";
    }

}
