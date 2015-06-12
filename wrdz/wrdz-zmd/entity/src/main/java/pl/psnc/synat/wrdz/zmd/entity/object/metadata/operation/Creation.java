/**
 * 
 */
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
package pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;

/**
 * Entity representing creation of digital object's metadata.
 */
@Entity
@DiscriminatorValue("CREATION")
public class Creation extends Operation {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -8137349847593223998L;


    /**
     * Default constructor for class.
     */
    public Creation() {
        super();
    }


    /**
     * Creates new instance of the creation operation for the specified object's metadata.
     * 
     * @param object
     *            digital object on which operation has been performed.
     */
    public Creation(DigitalObject object) {
        super(object);
    }


    /**
     * Creates new instance of the creation operation for the specified object with specified operation date.
     * 
     * @param object
     *            digital object on which operation has been performed.
     * @param date
     *            date of operation.
     */
    public Creation(DigitalObject object, Date date) {
        super(object, date);
    }

}
