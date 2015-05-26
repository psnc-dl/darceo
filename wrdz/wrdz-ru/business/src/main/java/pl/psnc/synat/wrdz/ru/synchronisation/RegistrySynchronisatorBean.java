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
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;

import pl.psnc.synat.wrdz.ru.entity.registries.RemoteRegistry;
import pl.psnc.synat.wrdz.ru.exceptions.HarvestingException;
import pl.psnc.synat.wrdz.ru.registries.RemoteRegistryManager;

/**
 * Singleton bean with registered cron job performing harvesting of the registries marked for harvesting.
 */
@Singleton
@Startup
public class RegistrySynchronisatorBean {

    /**
     * Remote registries manager bean.
     */
    @EJB
    private RemoteRegistryManager remoteRegistryManager;

    /**
     * Registry harvester bean providing easy API for triggering harvest.
     */
    @EJB
    private RegistryHarvester registryHarvester;


    /**
     * Synchronizes registry with remote registries marked for harvesting.
     * 
     * @param timer
     *            EJB timer injected by cron job.
     */
    @Schedule(hour = "1", minute = "0", second = "0", dayOfWeek = "*", persistent = true, info = "Every 1am")
    public void run(Timer timer) {
        List<RemoteRegistry> retrieveRemoteRegistries = remoteRegistryManager.retrieveRemoteRegistries(null, null,
            Boolean.TRUE);
        for (RemoteRegistry remoteRegistry : retrieveRemoteRegistries) {
            try {
                registryHarvester.harvestRegistry(remoteRegistry);
            } catch (HarvestingException exception) {
                handleException(remoteRegistry, exception);
                continue;
            }
        }
    }


    /**
     * Handles the exception that occurred while harvesting remote registry.
     * 
     * @param remoteRegistry
     *            remote registry harvested.
     * @param exception
     *            exception caught.
     */
    private void handleException(RemoteRegistry remoteRegistry, HarvestingException exception) {
    }
}
