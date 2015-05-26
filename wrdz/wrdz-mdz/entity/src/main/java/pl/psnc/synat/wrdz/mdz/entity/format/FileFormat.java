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
package pl.psnc.synat.wrdz.mdz.entity.format;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a single file format that requires loss risk assessment.
 */
@Entity
@Table(name = "MDZ_FILE_FORMATS", schema = "darceo")
public class FileFormat implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -2370192939566952123L;

    /** Format's identifier in the PRONOM database (primary key). */
    @Id
    @Column(name = "FF_PUID", length = 50, unique = true, nullable = false)
    private String puid;


    public String getPuid() {
        return puid;
    }


    public void setPuid(String puid) {
        this.puid = puid;
    }
}
