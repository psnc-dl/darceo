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

import javax.ejb.Local;

import pl.psnc.synat.wrdz.ru.entity.registries.RemoteRegistry;
import pl.psnc.synat.wrdz.ru.exceptions.HarvestingException;

/**
 * Defines the interface of the bean responsible for harvesting of data from the given registry.
 */
@Local
public interface RegistryHarvester {

    /**
     * Harvests the given registry and saves harvested data into local database.
     * 
     * @param remoteRegistry
     *            harvested registry.
     * @throws HarvestingException
     *             should any problems with harvesting occur.
     */
    void harvestRegistry(RemoteRegistry remoteRegistry)
            throws HarvestingException;

}
