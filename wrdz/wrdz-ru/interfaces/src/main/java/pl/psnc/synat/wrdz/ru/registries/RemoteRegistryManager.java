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
package pl.psnc.synat.wrdz.ru.registries;

import java.util.List;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.ru.entity.registries.RemoteRegistry;
import pl.psnc.synat.wrdz.ru.exceptions.EntryCreationException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryDeletionException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryModificationException;

/**
 * Interface describing the contract of class managing the {@link RemoteRegistry} entities.
 */
@Local
public interface RemoteRegistryManager {

    /**
     * Accepts and persists new remote registry.
     * 
     * @param added
     *            remote registry to be added.
     * @param certificate
     *            registry certificate
     * @return newly created remote registry.
     * @throws EntryCreationException
     *             should added registry have location already present in database.
     */
    RemoteRegistry createRemoteRegistry(RemoteRegistry added, String certificate)
            throws EntryCreationException;


    /**
     * Accepts the modifications in the specified remote registry.
     * 
     * @param modified
     *            remote registry to be modified.
     * @param certificate
     *            registry certificate
     * @return modified remote registry.
     * @throws EntryModificationException
     *             should modified registry have location already present in database.
     */
    RemoteRegistry updateRemoteRegistry(RemoteRegistry modified, String certificate)
            throws EntryModificationException;


    /**
     * Retrieves list of registries constrained by the parameters. If parameter any parameter is <code>null</code> it is
     * skipped.
     * 
     * @param location
     *            remote registry address.
     * @param readEnabled
     *            whether or not remote registry can read (harvest) local registry.
     * @param harvested
     *            whether or not local registry harvests this remote registry.
     * @return fetched registries.
     */
    List<RemoteRegistry> retrieveRemoteRegistries(String location, Boolean readEnabled, Boolean harvested);


    /**
     * Deletes a remote registry with specified primary key value.
     * 
     * @param id
     *            primary key value of the deleted entity.
     * @throws EntryDeletionException
     *             should any problems with registry deletion occur.
     */
    void deleteRemoteRegistry(long id)
            throws EntryDeletionException;


    /**
     * Fetches a remote registry with specified primary key value.
     * 
     * @param id
     *            primary key value of the entity.
     * @return retrieved entity or <code>null</code> if none was found.
     */
    RemoteRegistry retrieveRemoteRegistry(long id);


    /**
     * Retrieves the certificate of the user representing the certificate.
     * 
     * @param registryUsername
     *            name of the user representing the registry.
     * @return user's certificate.
     */
    String retrieveRemoteRegistryCertificate(String registryUsername);

}
