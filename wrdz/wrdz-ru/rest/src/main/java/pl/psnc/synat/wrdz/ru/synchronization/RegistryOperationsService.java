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
package pl.psnc.synat.wrdz.ru.synchronization;

import java.util.Date;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import pl.psnc.synat.wrdz.common.rest.exception.BadRequestException;
import pl.psnc.synat.wrdz.ru.exceptions.HarvestingException;
import pl.psnc.synat.wrdz.ru.services.Operations;
import pl.psnc.synat.wrdz.ru.services.RegistryOperationManager;

/**
 * Managed bean exposing the RESTful API form harvesting the repository.
 */
@Path("/synchronisation/")
@ManagedBean
public class RegistryOperationsService {

    /**
     * Registry operation manager.
     */
    @EJB
    private RegistryOperationManager registryOperationManager;


    /**
     * Fetches the list of changes on the semantic descriptors local to this registry.
     * 
     * @param from
     *            date marking the beginning of period by which results are filtered.
     * @return list of fetched operations matching the specified date constraint.
     */
    @GET
    @Path("changes")
    @Produces(MediaType.APPLICATION_XML)
    public Operations serveHarvestingInformation(@QueryParam("from") Date from) {
        try {
            return registryOperationManager.getOperations(from);
        } catch (HarvestingException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

}
