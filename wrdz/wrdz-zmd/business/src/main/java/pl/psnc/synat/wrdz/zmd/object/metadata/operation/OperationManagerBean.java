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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.operation.OperationDao;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Creation;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Deletion;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Modification;

/**
 * Provides useful methods for metadata operation's managing.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class OperationManagerBean implements OperationManager {

    /**
     * Operation DAO.
     */
    @EJB
    private OperationDao operationDao;


    @Override
    public void createCreationOperation(DigitalObject object, String metadata, NamespaceType type, Date date) {
        Creation creation = new Creation();
        creation.setContents(metadata);
        if (object.getCurrentVersion().getExtractedMetadata() != null
                && type == object.getCurrentVersion().getExtractedMetadata().getMainNamespace().getType()) {
            creation.getMetadataFiles().add(object.getCurrentVersion().getExtractedMetadata());
            object.getCurrentVersion().getExtractedMetadata().getMetadataContent().add(creation);
        }
        creation.setMetadataType(type);
        creation.setObject(object);
        if (date == null) {
            date = getSynchronizedDate();
        }
        creation.setDate(date);
        operationDao.persist(creation);
    }


    @Override
    public void createDeletionOperation(DigitalObject object, String metadata, NamespaceType type, Date date) {
        Deletion deletion = new Deletion();
        deletion.setContents(metadata);
        if (object.getCurrentVersion().getExtractedMetadata() != null
                && type == object.getCurrentVersion().getExtractedMetadata().getMainNamespace().getType()) {
            deletion.getMetadataFiles().add(object.getCurrentVersion().getExtractedMetadata());
        }
        deletion.setMetadataType(type);
        deletion.setObject(object);
        if (date == null) {
            date = getSynchronizedDate();
        }
        deletion.setDate(date);
        operationDao.persist(deletion);
    }


    @Override
    public void createModificationOperation(DigitalObject object, String metadata, NamespaceType type, Date date) {
        Modification modification = new Modification();
        modification.setContents(metadata);
        if (object.getCurrentVersion().getExtractedMetadata() != null
                && type == object.getCurrentVersion().getExtractedMetadata().getMainNamespace().getType()) {
            modification.getMetadataFiles().add(object.getCurrentVersion().getExtractedMetadata());
            object.getCurrentVersion().getExtractedMetadata().getMetadataContent().add(modification);
        }
        modification.setMetadataType(type);
        modification.setObject(object);
        if (date == null) {
            date = getSynchronizedDate();
        }
        modification.setDate(date);
        operationDao.persist(modification);
    }


    @Override
    public Date getSynchronizedDate() {
        operationDao.lockTable(true);
        return operationDao.getDatabaseDate();
    }

}
