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
package pl.psnc.synat.wrdz.mdz.plugin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.mdz.dao.plugin.PluginIterationDao;
import pl.psnc.synat.wrdz.mdz.entity.plugin.PluginIteration;
import pl.psnc.synat.wrdz.mdz.message.MdzMessenger;
import pl.psnc.synat.wrdz.zmd.object.IdentifierBrowser;

/**
 * Handles plugin execution.
 */
@LocalBean
@Singleton
public class PluginExecutor {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(PluginExecutor.class);

    /** Plugin statuses. */
    private Map<String, Future<Void>> statuses = new HashMap<String, Future<Void>>();

    /** Plugin implementations. */
    private Map<String, VerificationPlugin> processors = new HashMap<String, VerificationPlugin>();

    /** Injected session context. */
    @Resource
    private SessionContext ctx;

    /** Identifier browser used to fetch digital object identifiers from ZMD. */
    @EJB(name = "IdentifierBrowser")
    private IdentifierBrowser identifierBrowser;

    /** Plugin iteration DAO. */
    @EJB
    private PluginIterationDao pluginIterationDao;

    /** Communications manager. */
    @EJB
    private MdzMessenger messenger;


    /**
     * Registers a plugin for future use.
     * 
     * @param pluginName
     *            plugin name
     * @param plugin
     *            plugin implementation
     */
    public void registerPlugin(String pluginName, VerificationPlugin plugin) {
        processors.put(pluginName, plugin);
    }


    /**
     * Starts a previously registered plugin if it's not yet running.
     * 
     * @param pluginName
     *            plugin name
     */
    public synchronized void start(String pluginName) {
        if (!processors.containsKey(pluginName)) {
            throw new IllegalStateException("Trying to start an unregistered plugin");
        }

        Future<Void> status = statuses.get(pluginName);
        if (status == null || status.isDone()) {
            PluginExecutor proxy = ctx.getBusinessObject(PluginExecutor.class);
            status = proxy.execute(pluginName);
            statuses.put(pluginName, status);
        }
    }


    /**
     * Stops the given plugin's execution if it's currently in progress.
     * 
     * @param pluginName
     *            plugin name
     */
    public synchronized void stop(String pluginName) {
        Future<Void> status = statuses.get(pluginName);
        if (status != null) {
            status.cancel(true);
        }
    }


    /**
     * Executes the plugin with the given name. Internal use only.
     * 
     * @param pluginName
     *            plugin name
     * @return execution status
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<Void> execute(String pluginName) {

        PluginExecutor proxy = ctx.getBusinessObject(PluginExecutor.class);

        boolean finished = false;
        while (!ctx.wasCancelCalled() && !finished) {
            finished = !proxy.executeOnce(pluginName);
            if (finished) {
                proxy.finishCycle(pluginName);
            }

        }

        return new AsyncResult<Void>(null);
    }


    /**
     * Executes a single iteration of the plugin (verifies a single object). Internal use only.
     * 
     * @param pluginName
     *            plugin name
     * @return <code>true</code> if an iteration was performed, <code>false</code> if there were no objects to check
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean executeOnce(String pluginName) {

        VerificationPlugin plugin = processors.get(pluginName);

        PluginIteration iteration = startIteration(pluginName);
        if (iteration != null) {
            VerificationResult result = plugin.execute(iteration.getObjectIdentifier());
            handleResult(iteration.getObjectIdentifier(), pluginName, result);
            finishIteration(iteration);
            return true;
        } else {
            return false;
        }
    }


    /**
     * Creates and initializes a plugin iteration.
     * 
     * @param pluginName
     *            plugin name
     * @return initialized plugin itetation, or <code>null</code> if there are no more objects to check
     */
    private PluginIteration startIteration(String pluginName) {

        PluginIteration iteration = pluginIterationDao.getLast(pluginName);

        String identifier = iteration != null ? iteration.getObjectIdentifier() : null;
        String nextIdentifier = identifierBrowser.findNextActiveIdentifier(identifier);

        if (nextIdentifier != null) {
            iteration = new PluginIteration();
            iteration.setPluginName(pluginName);
            iteration.setObjectIdentifier(nextIdentifier);
            iteration.setStartedOn(new Date());
        } else {
            iteration = null;
        }

        return iteration;
    }


    /**
     * Persists the plugin iteration information.
     * 
     * @param iteration
     *            plugin iteration information
     */
    private void finishIteration(PluginIteration iteration) {
        iteration.setFinishedOn(new Date());
        pluginIterationDao.persist(iteration);
    }


    /**
     * Finalizes a plugin execution cycle. Internal use only.
     * <p>
     * This method should be executed after the last (newest) object has been verified by the given plugin.
     * 
     * @param pluginName
     *            plugin name
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void finishCycle(String pluginName) {

        long total = pluginIterationDao.countAll(pluginName);
        Date start = pluginIterationDao.getFirstStarted(pluginName);
        Date end = pluginIterationDao.getLastFinished(pluginName);

        String log = String.format("%s: finished plugin cycle [%s - %s] (%d objects)", pluginName, start, end, total);

        logger.info(log);

        pluginIterationDao.deleteAll(pluginName);
    }


    /**
     * Handles verification result.
     * 
     * @param objectIdentifier
     *            object identifier
     * @param pluginName
     *            pluginName
     * @param result
     *            plugin result
     */
    private void handleResult(String objectIdentifier, String pluginName, VerificationResult result) {
        if (result == null) {
            return;
        }

        PluginExecutionReport report = new PluginExecutionReport();
        report.setObjectIdentifier(objectIdentifier);
        report.setPluginName(pluginName);
        report.setDate(new Date());
        report.setResult(result);

        messenger.forwardPluginReport(report);
    }
}
