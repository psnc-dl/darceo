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
package pl.psnc.synat.wrdz.ru.services.descriptors;

import java.net.URI;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.sra.exception.SemanticRepositoryResourceException;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.dao.QueryModifier;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.SemanticDescriptorDao;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.SemanticDescriptorFilterFactory;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.SemanticDescriptorSorterBuilder;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.exceptions.EntryCreationException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryDeletionException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryModificationException;
import pl.psnc.synat.wrdz.ru.exceptions.IllegalRegistryOperationException;
import pl.psnc.synat.wrdz.ru.owl.RdfSemanticDescriptor;
import pl.psnc.synat.wrdz.ru.owl.SemanticRepositoryAccess;
import pl.psnc.synat.wrdz.ru.owl.ServicesConstructor;
import pl.psnc.synat.wrdz.ru.services.DataManipulationServiceManager;
import pl.psnc.synat.wrdz.ru.services.RegistryOperationManager;

/**
 * Class managing the {@link SemanticDescriptor} entities basic operations.
 */
@Stateless
public class SemanticDescriptorManagerBean implements SemanticDescriptorManager {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SemanticDescriptorManagerBean.class);

    /**
     * Registry operation manager.
     */
    @EJB
    private RegistryOperationManager registryOperationManager;

    /**
     * Semantic descriptor DAO for persistence operations.
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
     * Data manipulation service manager bean.
     */
    @EJB
    private DataManipulationServiceManager dataManipulationServiceManager;

    /**
     * Technical descriptor manager bean.
     */
    @EJB
    private TechnicalDescriptorManager technicalDescriptorManager;


    @Override
    public SemanticDescriptor createDescriptor(URI location, boolean exposed)
            throws EntryCreationException {
        if (location == null) {
            throw new EntryCreationException("Descriptor location URI not specified.");
        }
        checkIfLocationExists(location);
        SemanticDescriptor result = createDescriptor(getDescriptor(location), location, exposed);
        if (result.getId() == 0) {
            semanticDescriptorDao.persist(result);
        } else {
            result = semanticDescriptorDao.merge(result);
        }
        registryOperationManager.createCreationOperation(result);
        return result;
    }


    @Override
    public SemanticDescriptor modifyDescriptor(SemanticDescriptor descriptor, URI location, boolean exposed)
            throws EntryModificationException, IllegalRegistryOperationException {
        checkModificationValidity(descriptor);
        try {
            descriptor = semanticDescriptorDao.findById(descriptor.getId());
            descriptor = parseDescriptorModifications(descriptor, location, exposed);
        } catch (EntryDeletionException e) {
            throw new EntryModificationException(e.getMessage(), e);
        } catch (EntryCreationException e) {
            throw new EntryModificationException(e.getMessage(), e);
        }
        return descriptor;
    }


    @Override
    public SemanticDescriptor retrieveActiveDescriptor(long id) {
        SemanticDescriptor descriptor = semanticDescriptorDao.findById(id);
        if (descriptor != null && !descriptor.isDeleted()) {
            return descriptor;
        }
        return null;
    }


    @Override
    public List<SemanticDescriptor> retrieveDescriptors() {
        return semanticDescriptorDao.findAll();
    }


    @Override
    public List<SemanticDescriptor> retrieveActiveDescriptors(Boolean exposed, Boolean local) {
        QueryModifier<SemanticDescriptorFilterFactory, SemanticDescriptorSorterBuilder, SemanticDescriptor> queryModifier = semanticDescriptorDao
                .createQueryModifier();
        SemanticDescriptorFilterFactory filterFactory = queryModifier.getQueryFilterFactory();
        SemanticDescriptorSorterBuilder sorterBuilder = queryModifier.getQuerySorterBuilder();
        QueryFilter<SemanticDescriptor> filter = filterFactory.byDeleted(false);
        if (exposed != null) {
            filter = filterFactory.and(filter, filterFactory.byVisibility(exposed));
        }
        if (local != null) {
            filter = filterFactory.and(filter, filterFactory.byLocation(local));
        }
        sorterBuilder.byLocationUrl(true);
        return semanticDescriptorDao.findBy(filter, sorterBuilder.buildSorter());
    }


    @Override
    public void deleteDescriptor(long id)
            throws IllegalRegistryOperationException, EntryDeletionException {
        SemanticDescriptor descriptor = semanticDescriptorDao.findById(id);
        if (descriptor != null && !descriptor.isDeleted()) {
            if (descriptor.isLocal()) {
                removeSemanticDescriptorData(descriptor);
                descriptor.setDeleted(true);
                registryOperationManager.createDeleteOperation(descriptor);
                semanticDescriptorDao.merge(descriptor);
            } else if (!descriptor.isLocal()) {
                throw new IllegalRegistryOperationException("Cannot delete harvested entity!");
            }
        } else {
            throw new EntryDeletionException("No entry with id " + id + " found.");
        }
    }


    /**
     * Checks if descriptor under specified location is already in the registry, if so it throws a runtime exception.
     * 
     * @param location
     *            location of the semantic descriptor.
     * @throws EntryCreationException
     *             should descriptor with specified location be already defined.
     */
    private void checkIfLocationExists(URI location)
            throws EntryCreationException {
        SemanticDescriptorFilterFactory filterFactory = semanticDescriptorDao.createQueryModifier()
                .getQueryFilterFactory();
        QueryFilter<SemanticDescriptor> filter = filterFactory.and(filterFactory.byLocationUrl(location.toString()),
            filterFactory.byDeleted(false));
        SemanticDescriptor descriptor = semanticDescriptorDao.findFirstResultBy(filter);
        if (descriptor != null) {
            throw new EntryCreationException(
                    "Semantic descriptor with specified location already exists, please modify it directly.");
        }
    }


    /**
     * Finds the descriptor with specified location.
     * 
     * @param location
     *            location of the semantic descriptor.
     * @return found semantic descriptor or <code>null</code> if none was found.
     */
    private SemanticDescriptor getDescriptor(URI location) {
        SemanticDescriptorFilterFactory filterFactory = semanticDescriptorDao.createQueryModifier()
                .getQueryFilterFactory();
        QueryFilter<SemanticDescriptor> filter = filterFactory.and(filterFactory.byLocationUrl(location.toString()),
            filterFactory.byDeleted(true));
        SemanticDescriptor descriptor = semanticDescriptorDao.findFirstResultBy(filter);
        if (descriptor != null) {
            return descriptor;
        }
        return new SemanticDescriptor();
    }


    /**
     * Creates new semantic descriptor and substructure.
     * 
     * @param descriptor
     *            semantic descriptor being created.
     * @param location
     *            location of semantic descriptor.
     * @param exposed
     *            whether or not this descriptor describes public services.
     * @return newly created semantic descriptor.
     * @throws EntryCreationException
     *             should any problem with parsing descriptor or creating entries occur.
     */
    private SemanticDescriptor createDescriptor(SemanticDescriptor descriptor, URI location, boolean exposed)
            throws EntryCreationException {
        RdfSemanticDescriptor semanticRdfData = servicesConstructor.extractInformation(location.toString(), descriptor);
        try {
            semanticRepositoryAccessBean.addSemanticDescrptorRdfData(semanticRdfData);
        } catch (SemanticRepositoryResourceException e) {
            logger.error("Error while saving RDF data in the semantic repository.", e);
            throw new EntryCreationException(e);
        }
        descriptor.setExposed(exposed);
        descriptor.setDeleted(false);
        descriptor.setOrigin(null);
        return descriptor;
    }


    /**
     * Validates the specified modification operation.
     * 
     * @param descriptor
     *            semantic descriptor being modified.
     * @throws EntryModificationException
     *             if no such entry was found.
     * @throws IllegalRegistryOperationException
     *             if specified entry is unmodifiable, i.e. is in the deleted state or is not locally defined.
     */
    private void checkModificationValidity(SemanticDescriptor descriptor)
            throws EntryModificationException, IllegalRegistryOperationException {
        if (descriptor == null) {
            throw new EntryModificationException("No entry found.");
        } else if (!descriptor.isLocal() || descriptor.isDeleted()) {
            throw new IllegalRegistryOperationException("Cannot modify entity!");
        }
    }


    /**
     * Modifies semantic descriptor and substructure.
     * 
     * @param descriptor
     *            semantic descriptor being modified.
     * @param location
     *            new location of semantic descriptor.
     * @param exposed
     *            whether or not this descriptor describes public services.
     * @return modified semantic descriptor.
     * @throws EntryCreationException
     *             should any problem with parsing descriptor or creating entries occur.
     * @throws EntryDeletionException
     *             should deletion refer to nonexistent descriptor.
     * @throws IllegalRegistryOperationException
     *             should deletion refer to harvested descriptor.
     */
    private SemanticDescriptor parseDescriptorModifications(SemanticDescriptor descriptor, URI location, boolean exposed)
            throws EntryCreationException, EntryDeletionException, IllegalRegistryOperationException {
        boolean wasExposed = descriptor.isExposed();
        if (location == null) {
            descriptor.setExposed(exposed);
            semanticDescriptorDao.merge(descriptor);
            registryOperationManager.createModificationOperation(descriptor, exposed, wasExposed);
        } else if (descriptor.getLocationUrl().equals(location.toString())) {
            removeSemanticDescriptorData(descriptor);
            descriptor = createDescriptor(descriptor, location, exposed);
            semanticDescriptorDao.merge(descriptor);
            registryOperationManager.createModificationOperation(descriptor, exposed, wasExposed);
        } else {
            deleteDescriptor(descriptor.getId());
            descriptor = createDescriptor(location, exposed);
        }
        return descriptor;
    }


    /**
     * Removes all data associated with the passed semantic descriptor.
     * 
     * @param descriptor
     *            semantic descriptor
     * @throws EntryDeletionException
     *             when removing failed
     */
    private void removeSemanticDescriptorData(SemanticDescriptor descriptor)
            throws EntryDeletionException {
        dataManipulationServiceManager.removeServices(descriptor.getDescribedServices());
        technicalDescriptorManager.removeTechnicalDescriptors(descriptor.getTechnicalDescriptors());
        try {
            semanticRepositoryAccessBean.removeSemanticDescrptorRdfData(descriptor.getContext());
        } catch (SemanticRepositoryResourceException e) {
            logger.error("Error while removing RDF data from the semantic repository.", e);
            throw new EntryDeletionException(e);
        }
    }
}
