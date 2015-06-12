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
package pl.psnc.synat.wrdz.ms.entity.messages;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import pl.psnc.synat.wrdz.ms.types.InternalMessageType;

/**
 * Represents a WRDZ internal message.
 */
@Entity
@Table(name = "MS_INTERNAL_MESSAGES", schema = "darceo")
public class InternalMessage implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -8096808523757148022L;

    /** Primary id. */
    @Id
    @SequenceGenerator(name = "msInternalMessageSequenceGenerator", sequenceName = "darceo.MS_IM_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "msInternalMessageSequenceGenerator")
    @Column(name = "IM_ID", unique = true, nullable = false)
    private long id;

    /** Message origin. */
    @Column(name = "IM_ORIGIN", length = 255, nullable = false)
    private String origin;

    /** The date the message was received on. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "IM_RECEIVED_ON", nullable = false)
    private Date receivedOn;

    /** Message type. */
    @Enumerated(EnumType.STRING)
    @Column(name = "IM_TYPE", length = 35, nullable = false)
    private InternalMessageType type;

    /** Message data. */
    @Column(name = "IM_DATA", length = 65535, nullable = false)
    private String data;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getOrigin() {
        return origin;
    }


    public void setOrigin(String origin) {
        this.origin = origin;
    }


    public Date getReceivedOn() {
        return receivedOn;
    }


    public void setReceivedOn(Date receivedOn) {
        this.receivedOn = receivedOn;
    }


    public InternalMessageType getType() {
        return type;
    }


    public void setType(InternalMessageType type) {
        this.type = type;
    }


    public String getData() {
        return data;
    }


    public void setData(String data) {
        this.data = data;
    }
}
