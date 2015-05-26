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
package pl.psnc.synat.wrdz.common.entity.time;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "COM_TIMESTAMP", schema = "darceo")
@NamedQuery(name = "now", query = "SELECT CURRENT_TIMESTAMP FROM TimestampItem ti")
public class TimestampItem {

    private Date date;


    @Id
    @Column(name = "TI_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDate() {
        return date;
    }


    public void setDate(Date date) {
        this.date = date;
    }

}
