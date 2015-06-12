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
package pl.psnc.synat.wrdz.zmd.object.metadata.operation;

import java.util.Date;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Creation;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Deletion;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Modification;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;

/**
 * Interface for {@link Operation} objects manager.
 */
@Local
public interface OperationManager {

    /**
     * Creates new {@link Creation} operation entry in the database (calls persist on it).
     * 
     * @param object
     *            object, that was created.
     * @param metadata
     *            metadata to store.
     * @param type
     *            type of stored metadata.
     * @param date
     *            date of operation.
     */
    void createCreationOperation(DigitalObject object, String metadata, NamespaceType type, Date date);


    /**
     * Fetches the database date locking the {@link Operation} entities table in exclusive mode.
     * 
     * @return date fetched from the database.
     */
    Date getSynchronizedDate();


    /**
     * Creates new {@link Modification} operation entry in the database (calls persist on it).
     * 
     * @param object
     *            object, that was modified.
     * @param metadata
     *            metadata to store.
     * @param type
     *            type of stored metadata.
     * @param date
     *            date of operation.
     */
    void createModificationOperation(DigitalObject object, String metadata, NamespaceType type, Date date);


    /**
     * Creates new {@link Deletion} operation entry in the database (calls persist on it).
     * 
     * @param object
     *            object, that was deleted.
     * @param metadata
     *            metadata to store.
     * @param type
     *            type of stored metadata.
     * @param date
     *            date of operation.
     */
    void createDeletionOperation(DigitalObject object, String metadata, NamespaceType type, Date date);

}
