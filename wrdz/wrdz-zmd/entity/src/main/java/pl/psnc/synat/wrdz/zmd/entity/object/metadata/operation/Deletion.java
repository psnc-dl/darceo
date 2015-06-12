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
 * Entity representing modification of digital object.
 */
@Entity
@DiscriminatorValue("DELETION")
public class Deletion extends Operation {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -5680747680446786721L;


    /**
     * Default constructor for class.
     */
    public Deletion() {
        super();
    }


    /**
     * Creates new instance of the modification operation for the specified object.
     * 
     * @param object
     *            digital object on which operation has been performed.
     */
    public Deletion(DigitalObject object) {
        super(object);
    }


    /**
     * Creates new instance of the modification operation for the specified object with specified operation date.
     * 
     * @param object
     *            digital object on which operation has been performed.
     * @param date
     *            date of operation.
     */
    public Deletion(DigitalObject object, Date date) {
        super(object, date);
    }

}
