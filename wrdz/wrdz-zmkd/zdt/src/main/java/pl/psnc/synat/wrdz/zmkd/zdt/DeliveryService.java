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
package pl.psnc.synat.wrdz.zmkd.zdt;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.zmd.object.ObjectChecker;
import pl.psnc.synat.wrdz.zmkd.config.ZdtConfiguration;
import pl.psnc.synat.wrdz.zmkd.ddr.ClientCapabilities;
import pl.psnc.synat.wrdz.zmkd.ddr.DDRHelper;
import pl.psnc.synat.wrdz.zmkd.ddr.DeviceInfo;
import pl.psnc.synat.wrdz.zmkd.entity.plan.DeliveryPlan;
import pl.psnc.synat.wrdz.zmkd.entity.plan.DeliveryPlanStatus;
import pl.psnc.synat.wrdz.zmkd.plan.DeliveryPlanExecutor;
import pl.psnc.synat.wrdz.zmkd.plan.DeliveryPlanManager;

import com.sun.jersey.api.view.Viewable;

/**
 * Advanced content delivery service.
 */
@Path("/")
@ManagedBean
public class DeliveryService {
	
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DeliveryService.class);

    /** Path to the jsp that handles plugin and javascript detection. */
    private static final String DETECT_PATH = "/WEB-INF/jsp/detect.jsp";

    /** Path to the jsp that handles delivery plan choice. */
    private static final String CHOICE_PATH = "/WEB-INF/jsp/choice.jsp";

    /** Path to the jsp that informs about the processing status. */
    private static final String STATUS_PATH = "/WEB-INF/jsp/status.jsp";

    /** DDR helper. */
    @EJB
    private DDRHelper ddrHelper;

    /** Object checker. */
    @EJB(name = "ObjectChecker")
    private ObjectChecker objectChecker;

    /** Delivery plan manager. */
    @EJB
    private DeliveryPlanManager deliveryPlanManager;

    /** Delivery plan executor. */
    @EJB
    private DeliveryPlanExecutor planExecutor;

    /** Configuration. */
    @Inject
    private ZdtConfiguration config;

    /** Delivery plan cache. */
    @Inject
    private DeliveryPlanCache planCache;

    /** Uri info. */
    @Context
    private UriInfo uriInfo;


    /**
     * Handles the object display requests.
     * 
     * @param objectId
     *            requested object's identifier
     * @param userAgent
     *            user agent header
     * @param js
     *            whether javascript is available in the client's browser
     * @param plugins
     *            gathered information about installed plugins
     * @return 200 OK forwarding the request to jsp, or 404 NOT FOUND if the object could not be found
     */
    @GET
    @Path("object/{objectId}")
    public Response viewObject(@PathParam("objectId") String objectId, @HeaderParam("User-Agent") String userAgent,
            @QueryParam("js") Boolean js, @QueryParam("plugins") List<String> plugins) {

        if (!objectChecker.checkIfDigitalObjectExists(objectId)) {
            return Response.status(Status.NOT_FOUND).build();
        }

        if (js == null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("objectId", objectId);
            params.put("url", uriInfo.getAbsolutePath());
            params.put("baseUrl", uriInfo.getBaseUri());
            return Response.ok(new Viewable(DETECT_PATH, params)).build();
        }

        logger.debug("user agent: " + userAgent);
        DeviceInfo info = ddrHelper.getInfo(userAgent);
        logger.debug("info: " + info.toString());

        ClientCapabilities capabilities = new ClientCapabilities(config, info, js, plugins);

        List<DeliveryPlan> plans = deliveryPlanManager.generateDeliveryPlans(capabilities, objectId);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("plans", plans);

        if (!plans.isEmpty()) {
            String groupId = UUID.randomUUID().toString();
            planCache.put(groupId, plans);
            params.put("url", uriInfo.getBaseUriBuilder().path("plan/{groupId}").build(groupId));
        }

        return Response.ok(new Viewable(CHOICE_PATH, params)).build();
    }


    /**
     * Executes the delivery plan that resides in the session cache at the given index within the group with the given
     * id.
     * 
     * @param groupId
     *            id of the plan group inside the cache
     * @param index
     *            index of the plan within the group
     * @return 303 SEE OTHER redirecting to status check, or 400 BAD REQUEST if a negative index was given, or 404 NOT
     *         FOUND if the plan could not be found
     */
    @POST
    @Path("plan/{groupId}/{index}")
    public Response executePlan(@PathParam("groupId") String groupId, @PathParam("index") int index) {

        if (index < 0) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        List<DeliveryPlan> plans = planCache.fetch(groupId);
        if (plans == null || plans.size() <= index) {
            return Response.status(Status.NOT_FOUND).build();
        }

        DeliveryPlan plan = plans.get(index);
        String planId = deliveryPlanManager.saveDeliveryPlan(plan);
        planExecutor.start(planId);

        return Response.seeOther(uriInfo.getBaseUriBuilder().path("request/{planId}").build(planId)).build();
    }


    /**
     * Checks the given plan's execution status.
     * 
     * @param planId
     *            plan id
     * @return 200 OK forwarding the request to jsp, or 404 NOT FOUND if the matching plan could not be found
     */
    @GET
    @Path("request/{planId}")
    public Response checkStatus(@PathParam("planId") String planId) {

        DeliveryPlan plan = deliveryPlanManager.getDeliveryPlan(planId);

        if (plan == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        if (plan.getStatus() == DeliveryPlanStatus.COMPLETED) {
            return Response.seeOther(URI.create(plan.getDelivery().getClientLocation())).build();
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("status", plan.getStatus());
        params.put("url", uriInfo.getAbsolutePath());
        return Response.ok(new Viewable(STATUS_PATH, params)).build();
    }
}
