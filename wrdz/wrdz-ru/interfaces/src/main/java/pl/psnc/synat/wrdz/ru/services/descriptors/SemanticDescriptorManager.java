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

import javax.ejb.Local;

import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.exceptions.AccessDeniedException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryCreationException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryDeletionException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryModificationException;
import pl.psnc.synat.wrdz.ru.exceptions.IllegalRegistryOperationException;

/**
 * Interface describing the contract of class managing the {@link SemanticDescriptor} entities.
 */
@Local
public interface SemanticDescriptorManager {

    /**
     * Creates new semantic descriptor and dependent entities tree based on the parsed descriptor.
     * 
     * @param location
     *            semantic descriptor location.
     * @param exposed
     *            whether or not the semantic descriptor is publicly visible for harvesting registries.
     * @return newly created entity.
     * @throws EntryCreationException
     *             should any problems with downloading and parsing the descriptor occur.
     * @throws AccessDeniedException
     *             should user lack the permissions to perform operation.
     */
    SemanticDescriptor createDescriptor(URI location, boolean exposed)
            throws EntryCreationException, AccessDeniedException;


    /**
     * Modifies existing semantic descriptor and dependent entities tree based on the parsed descriptor.
     * 
     * @param descriptor
     *            modified descriptor.
     * @param location
     *            semantic descriptor location.
     * @param exposed
     *            whether or not the semantic descriptor is publicly visible for harvesting registries.
     * @return modified entity.
     * @throws EntryModificationException
     *             should any problem during parsing the descriptor and modification of entities occur.
     * @throws IllegalRegistryOperationException
     *             should modification operation specify nonexistent or deleted schema.
     */
    SemanticDescriptor modifyDescriptor(SemanticDescriptor descriptor, URI location, boolean exposed)
            throws EntryModificationException, IllegalRegistryOperationException;


    /**
     * Returns list of all descriptors stored in the system, including those marked as deleted.
     * 
     * @return complete list of all descriptors.
     */
    List<SemanticDescriptor> retrieveDescriptors();


    /**
     * Marks specified descriptor as deleted removing it subtree (services and technical descriptors).
     * 
     * @param id
     *            primary key value of target entity.
     * @throws IllegalRegistryOperationException
     *             should deletion refer to harvested descriptor.
     * @throws EntryDeletionException
     *             should deletion refer to nonexistent descriptor.
     */
    void deleteDescriptor(long id)
            throws IllegalRegistryOperationException, EntryDeletionException;


    /**
     * Retrieves list of active (undeleted) descriptors filtered by the given parameters values. If parameter is
     * <code>null</code> it is skipped in the filtering.
     * 
     * @param exposed
     *            whether or not the semantic descriptor is publicly visible for harvesting registries.
     * @param local
     *            whether or not the descriptor is defined in the local registry or has been harvested from remote one.
     * @return list of filtered descriptors.
     */
    List<SemanticDescriptor> retrieveActiveDescriptors(Boolean exposed, Boolean local);


    /**
     * Finds active (undeleted) descriptor with specified primary key value.
     * 
     * @param id
     *            primary key value of target entity.
     * @return found descriptor or null if none was found.
     */
    SemanticDescriptor retrieveActiveDescriptor(long id);

}
