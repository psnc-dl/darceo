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
package pl.psnc.synat.wrdz.ru.services;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.ru.config.RuConfiguration;
import pl.psnc.synat.wrdz.ru.dao.services.RegistryOperationDao;
import pl.psnc.synat.wrdz.ru.dao.services.RegistryOperationFilterFactory;
import pl.psnc.synat.wrdz.ru.entity.services.RegistryOperation;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.entity.types.OperationType;
import pl.psnc.synat.wrdz.ru.exceptions.HarvestingException;

/**
 * Class managing {@link RegistryOperation} entities basic operations.
 */
@Stateless
public class RegistryOperationManagerBean implements RegistryOperationManager {

    /**
     * Registry operation DAO for persistence operations.
     */
    @EJB
    private RegistryOperationDao registryOperationDao;

    /**
     * Services registry configuration singleton POJO.
     */
    @Inject
    private RuConfiguration ruConfiguration;


    @Override
    public RegistryOperation createOperation(Operation operation, SemanticDescriptor target) {
        RegistryOperation result = new RegistryOperation();
        result.setTarget(target);
        result.setType(OperationType.valueOf(operation.getType()));
        result.setDate(operation.getDate());
        result.setExposed(true);
        registryOperationDao.persist(result);
        target.getOperations().add(result);
        return result;
    }


    @Override
    public RegistryOperation createDeleteOperation(SemanticDescriptor target) {
        RegistryOperation deletion = new RegistryOperation();
        deletion.setTarget(target);
        deletion.setType(OperationType.DELETE);
        deletion.setDate(getDatabaseTime());
        deletion.setExposed(target.isExposed());
        registryOperationDao.persist(deletion);
        target.getOperations().add(deletion);
        return deletion;
    }


    @Override
    public RegistryOperation createModificationOperation(SemanticDescriptor target, OperationType changedVisibility) {
        registryOperationDao.lockTable(true);
        if (changedVisibility != null) {
            createChangedVisibilityOperation(target, changedVisibility);
        }
        RegistryOperation update = new RegistryOperation();
        update.setTarget(target);
        update.setType(OperationType.UPDATE);
        update.setDate(registryOperationDao.getDatabaseDate());
        update.setExposed(target.isExposed());
        registryOperationDao.persist(update);
        target.getOperations().add(update);
        return update;
    }


    @Override
    public RegistryOperation createCreationOperation(SemanticDescriptor target) {
        RegistryOperation modification = new RegistryOperation();
        modification.setTarget(target);
        modification.setType(OperationType.CREATE);
        modification.setDate(getDatabaseTime());
        modification.setExposed(target.isExposed());
        registryOperationDao.persist(modification);
        target.getOperations().add(modification);
        return modification;
    }


    @Override
    public Date getDatabaseTime() {
        registryOperationDao.lockTable(true);
        return registryOperationDao.getDatabaseDate();
    }


    @Override
    public Operations getOperations(Date from)
            throws HarvestingException {
        List<RegistryOperation> registryOperations = null;
        Date until = getDatabaseTime();
        if (from == null || until.after(from)) {
            registryOperations = registryOperationDao.getChanges(from, until);
            Operations result = new Operations();
            result.setTime(until);
            List<Operation> operations = result.getOperations();
            for (RegistryOperation registryOperation : registryOperations) {
                Operation operation = new Operation();
                operation.setType(registryOperation.getType().name());
                operation.setLocation(registryOperation.getTarget().getLocationUrl());
                operation.setDate(registryOperation.getDate());
                operation.setOrigin(fetchOrigin(registryOperation.getTarget()));
                operations.add(operation);
            }
            return result;
        }
        throw new HarvestingException("Cannot start harvesting from th date in future.");
    }


    @Override
    public List<RegistryOperation> getOperations(long descriptorId, Date from) {
        RegistryOperationFilterFactory filterFactory = registryOperationDao.createQueryModifier()
                .getQueryFilterFactory();
        QueryFilter<RegistryOperation> filter = filterFactory.and(filterFactory.bySemanticDescriptorId(descriptorId),
            filterFactory.byDateFrom(from));
        return registryOperationDao.findBy(filter, true);
    }


    @Override
    public void createModificationOperation(SemanticDescriptor descriptor, boolean exposed, boolean wasExposed) {
        if (exposed && !wasExposed) {
            createModificationOperation(descriptor, OperationType.PUBLISH);
        } else if (!exposed && wasExposed) {
            createModificationOperation(descriptor, OperationType.UNPUBLISH);
        } else {
            createModificationOperation(descriptor, null);
        }
    }


    /**
     * Creates new visibility change operation.
     * 
     * @param target
     *            target descriptor.
     * @param changedVisibility
     *            kind of visibility change.
     * @return created registry operation.
     */
    private RegistryOperation createChangedVisibilityOperation(SemanticDescriptor target,
            OperationType changedVisibility) {
        RegistryOperation update = new RegistryOperation();
        update.setTarget(target);
        update.setType(changedVisibility);
        update.setDate(registryOperationDao.getDatabaseDate());
        update.setExposed(true);
        registryOperationDao.persist(update);
        target.getOperations().add(update);
        return update;
    }


    /**
     * Extracts origin of the semantic descriptor.
     * 
     * @param target
     *            target semantic descriptor.
     * @return extracted origin.
     */
    private String fetchOrigin(SemanticDescriptor target) {
        String origin = target.getOrigin();
        if (origin != null && !origin.trim().isEmpty()) {
            return origin;
        } else {
            return ruConfiguration.getCertificateInfo();
        }
    }
}
