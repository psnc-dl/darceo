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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Represents an e-mail address that will be notified about new messages.
 */
@Entity
@Table(name = "MS_NOTIFY_EMAILS", schema = "darceo")
public class NotifyEmail implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 847032935276635303L;

    /** Primary id. */
    @Id
    @SequenceGenerator(name = "msNotifyEmailSequenceGenerator", sequenceName = "darceo.MS_NE_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "msNotifyEmailSequenceGenerator")
    @Column(name = "NE_ID", unique = true, nullable = false)
    private long id;

    /** Email address. */
    @Column(name = "NE_ADDRESS", length = 255, nullable = false)
    private String address;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address = address;
    }
}
