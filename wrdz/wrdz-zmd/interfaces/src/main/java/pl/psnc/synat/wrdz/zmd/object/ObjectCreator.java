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
package pl.psnc.synat.wrdz.zmd.object;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;

/**
 * Specifies interface of object creation helper, i.e. class, that provides functionality of creation of digital
 * objects.
 */
@Local
public interface ObjectCreator {

    /**
     * Creates new digital object.
     * 
     * @param request
     *            object's creation request.
     * @return created object.
     * @throws ObjectCreationException
     *             if object's creation fails.
     */
    DigitalObject createObject(ObjectCreationRequest request)
            throws ObjectCreationException;

}
