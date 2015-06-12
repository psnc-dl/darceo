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
package pl.psnc.synat.wrdz.ru.synchronisation;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.sra.exception.SemanticRepositoryResourceException;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.SemanticDescriptorDao;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.SemanticDescriptorFilterFactory;
import pl.psnc.synat.wrdz.ru.entity.services.RegistryOperation;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.entity.types.OperationType;
import pl.psnc.synat.wrdz.ru.exceptions.EntryCreationException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryDeletionException;
import pl.psnc.synat.wrdz.ru.exceptions.IllegalRegistryOperationException;
import pl.psnc.synat.wrdz.ru.owl.RdfSemanticDescriptor;
import pl.psnc.synat.wrdz.ru.owl.SemanticRepositoryAccess;
import pl.psnc.synat.wrdz.ru.owl.ServicesConstructor;
import pl.psnc.synat.wrdz.ru.services.DataManipulationServiceManager;
import pl.psnc.synat.wrdz.ru.services.Operation;
import pl.psnc.synat.wrdz.ru.services.RegistryOperationManager;
import pl.psnc.synat.wrdz.ru.services.descriptors.TechnicalDescriptorManager;

/**
 * Bean providing functionality of synchronizing state of the registry with harvested information.
 */
@Singleton
@Startup
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class StateSynchronizerBean implements StateSynchronizer {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(StateSynchronizerBean.class);

    /**
     * Registry operations manager bean.
     */
    @EJB
    private RegistryOperationManager registryOperationManager;

    /**
     * Semantic descriptor DAO.
     */
    @EJB
    private SemanticDescriptorDao semanticDescriptorDao;

    /**
     * OWL ontology manager.
     */
    @EJB
    private ServicesConstructor servicesConstructor;

    /**
     * Provides an access to a semantic repository.
     */
    @EJB
    private SemanticRepositoryAccess semanticRepositoryAccessBean;

    /**
     * Data manipulation services manager bean.
     */
    @EJB
    private DataManipulationServiceManager dataManipulationServiceManager;

    /**
     * Technical descriptors manager bean.
     */
    @EJB
    private TechnicalDescriptorManager technicalDescriptorManager;


    @Override
    public void digestOperation(Operation operation) {
        SemanticDescriptor descriptor = findSemanticDescriptor(operation.getLocation());
        try {
            if (descriptor == null) {
                synchronizeState(operation, null);
            } else {
                synchronizeState(operation, descriptor);
            }
        } catch (WrdzRuntimeException e) {
            logger.error("Exception-rising entry " + operation.getLocation() + " found.", e);
        }
    }


    /**
     * Finds the semantic descriptor associated with the given location.
     * 
     * @param location
     *            location of the descriptor file.
     * @return descriptor found of <code>null</code>.
     */
    private SemanticDescriptor findSemanticDescriptor(String location) {
        SemanticDescriptorFilterFactory filterFactory = semanticDescriptorDao.createQueryModifier()
                .getQueryFilterFactory();
        QueryFilter<SemanticDescriptor> filter = filterFactory.and(filterFactory.byLocationUrl(location),
            filterFactory.byLocation(false));
        return semanticDescriptorDao.findFirstResultBy(filter);
    }


    /**
     * Synchronizes the state of semantic descriptor with the state received in the current harvest.
     * 
     * @param operation
     *            operation received from the current harvest.
     * @param descriptor
     *            modified descriptor.
     * @throws EntryDeletionException
     *             should any problems with clearing stale data occur.
     * @throws EntryCreationException
     *             should any problem with creation of new data structures occur.
     * @throws IllegalRegistryOperationException
     *             should modified descriptor be marked as local and therefore unmodifiable by harvesting.
     */
    private void synchronizeState(Operation operation, SemanticDescriptor descriptor)
            throws EntryDeletionException, EntryCreationException, IllegalRegistryOperationException {
        if (descriptor != null) {
            List<RegistryOperation> operations = registryOperationManager.getOperations(descriptor.getId(),
                operation.getDate());
            if (operations != null && !operations.isEmpty()) {
                return;
            }
            synchronizeDescriptor(operation, descriptor);
        } else {
            importHarvestedDescriptor(operation);
        }

    }


    /**
     * Synchronizes the state of semantic descriptor already registered in the registry by previous harvests with the
     * state received in the current harvest.
     * 
     * @param operation
     *            operation received from the current harvest.
     * @param descriptor
     *            modified descriptor.
     * @throws EntryDeletionException
     *             should any problems with clearing stale data occur.
     * @throws EntryCreationException
     *             should any problem with creation of new data structures occur.
     * @throws IllegalRegistryOperationException
     *             should modified descriptor be marked as local and therefore unmodifiable by harvesting.
     */
    private void synchronizeDescriptor(Operation operation, SemanticDescriptor descriptor)
            throws EntryDeletionException, EntryCreationException, IllegalRegistryOperationException {
        OperationType type = OperationType.valueOf(operation.getType());
        if (type == OperationType.CREATE || type == OperationType.PUBLISH) {
            createDescriptor(operation, descriptor);
        } else if (type == OperationType.UPDATE) {
            updateDescriptor(operation, descriptor);
        } else if (type == OperationType.DELETE || type == OperationType.UNPUBLISH) {
            deleteDescriptor(operation, descriptor);
        }
        registryOperationManager.createOperation(operation, descriptor);
    }


    /**
     * Creates new descriptor from the operation received in the current harvest.
     * 
     * @param operation
     *            operation received from the current harvest.
     * @param descriptor
     *            modified descriptor.
     * @throws EntryCreationException
     *             should any problem with creation of new data structures occur.
     * @throws IllegalRegistryOperationException
     *             should modified descriptor be marked as local and therefore unmodifiable by harvesting.
     */
    private void createDescriptor(Operation operation, SemanticDescriptor descriptor)
            throws EntryCreationException, IllegalRegistryOperationException {
        if (!descriptor.isLocal() && descriptor.isDeleted()) {
            descriptor = buildDescriptor(operation, descriptor);
            descriptor = semanticDescriptorDao.merge(descriptor);
            operation.setType(OperationType.CREATE.name());
            registryOperationManager.createOperation(operation, descriptor);
        } else {
            throw new IllegalRegistryOperationException("Harvesting cannot modify local entity!");
        }

    }


    /**
     * Updates descriptor descriptor with the operation received in the current harvest.
     * 
     * @param operation
     *            operation received from the current harvest.
     * @param descriptor
     *            modified descriptor.
     * @throws EntryDeletionException
     *             should any problems with clearing stale data occur.
     * @throws EntryCreationException
     *             should any problem with creation of new data structures occur.
     * @throws IllegalRegistryOperationException
     *             should modified descriptor be marked as local and therefore unmodifiable by harvesting.
     */
    private void updateDescriptor(Operation operation, SemanticDescriptor descriptor)
            throws EntryDeletionException, EntryCreationException, IllegalRegistryOperationException {
        if (!descriptor.isLocal()) {
            dataManipulationServiceManager.removeServices(descriptor.getDescribedServices());
            technicalDescriptorManager.removeTechnicalDescriptors(descriptor.getTechnicalDescriptors());
            try {
                semanticRepositoryAccessBean.removeSemanticDescrptorRdfData(descriptor.getContext());
            } catch (SemanticRepositoryResourceException e) {
                logger.error("Error while removing RDF data from the semantic repository.", e);
                throw new EntryDeletionException(e);
            }
            descriptor = buildDescriptor(operation, descriptor);
            descriptor = semanticDescriptorDao.merge(descriptor);
            registryOperationManager.createOperation(operation, descriptor);
        } else if (descriptor.isLocal()) {
            throw new IllegalRegistryOperationException("Harvesting cannot modify local entity!");
        }
    }


    /**
     * Deletes descriptor specified by the operation received in the current harvest.
     * 
     * @param operation
     *            operation received from the current harvest.
     * @param descriptor
     *            modified descriptor.
     * @throws EntryDeletionException
     *             should any problem with the deletion of data structures occur.
     * @throws IllegalRegistryOperationException
     *             should modified descriptor be marked as local and therefore unmodifiable by harvesting.
     */
    private void deleteDescriptor(Operation operation, SemanticDescriptor descriptor)
            throws EntryDeletionException, IllegalRegistryOperationException {
        if (!descriptor.isLocal()) {
            dataManipulationServiceManager.removeServices(descriptor.getDescribedServices());
            technicalDescriptorManager.removeTechnicalDescriptors(descriptor.getTechnicalDescriptors());
            try {
                semanticRepositoryAccessBean.removeSemanticDescrptorRdfData(descriptor.getContext());
            } catch (SemanticRepositoryResourceException e) {
                logger.error("Error while removing RDF data from the semantic repository.", e);
                throw new EntryDeletionException(e);
            }
            descriptor.setDeleted(true);
            operation.setType(OperationType.DELETE.name());
            registryOperationManager.createOperation(operation, descriptor);
            semanticDescriptorDao.merge(descriptor);
        } else if (descriptor.isLocal()) {
            throw new IllegalRegistryOperationException("Harvesting cannot delete local entity!");
        }
    }


    /**
     * Imports the semantic descriptor and its the state received in the current harvest into the registry.
     * 
     * @param operation
     *            operation received from the current harvest.
     * @throws EntryCreationException
     *             should any problem with creation of new data structures occur.
     */
    private void importHarvestedDescriptor(Operation operation)
            throws EntryCreationException {
        OperationType type = OperationType.valueOf(operation.getType());
        SemanticDescriptor descriptor = null;
        if (type == OperationType.CREATE || type == OperationType.PUBLISH) {
            descriptor = buildDescriptor(operation, new SemanticDescriptor());
            operation.setType(OperationType.CREATE.name());
        } else if (type == OperationType.UPDATE) {
            descriptor = buildDescriptor(operation, new SemanticDescriptor());
        } else if (type == OperationType.DELETE || type == OperationType.UNPUBLISH) {
            return;
        }
        semanticDescriptorDao.persist(descriptor);
        registryOperationManager.createOperation(operation, descriptor);
    }


    /**
     * Builds semantic descriptor from the given data.
     * 
     * @param operation
     *            operation received from the current harvest.
     * @param descriptor
     *            modified descriptor.
     * @return constructed semantic descriptor.
     * @throws EntryCreationException
     *             should any problem with creation of new data structures occur.
     */
    private SemanticDescriptor buildDescriptor(Operation operation, SemanticDescriptor descriptor)
            throws EntryCreationException {
        RdfSemanticDescriptor semanticRdfData = servicesConstructor.extractInformation(operation.getLocation(),
            descriptor);
        try {
            semanticRepositoryAccessBean.addSemanticDescrptorRdfData(semanticRdfData);
        } catch (SemanticRepositoryResourceException e) {
            logger.error("Error while saving RDF data in the semantic repository.", e);
            throw new EntryCreationException(e);
        }
        descriptor.setExposed(true);
        descriptor.setDeleted(false);
        descriptor.setOrigin(operation.getOrigin());
        return descriptor;
    }

}
