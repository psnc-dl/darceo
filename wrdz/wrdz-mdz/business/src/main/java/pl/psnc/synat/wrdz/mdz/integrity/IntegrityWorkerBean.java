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
package pl.psnc.synat.wrdz.mdz.integrity;

import java.util.concurrent.Future;

import javax.ejb.Singleton;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default integrity worker implementation.
 * 
 * Manages the processing performed by the {@link IntegrityProcessor} by providing means to start, stop and restart it
 * when required and ensures that only one processor is running at any given time.
 */
@Singleton
public class IntegrityWorkerBean implements IntegrityWorker {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(IntegrityWorkerBean.class);

    /** Processor instance. */
    @Inject
    private IntegrityProcessor processor;

    /** Future object returned by the last called processor method. */
    private Future<Void> processingResult;

    /** Whether the worker is currently active. */
    private boolean active;


    @Override
    public synchronized void activate() {
        logger.info("Activating integrity worker");
        active = true;
        start();
    }


    @Override
    public synchronized void deactivate() {
        logger.info("Deactivating integrity worker");
        active = false;
        stop();
    }


    @Override
    public synchronized void start() {
        if (active && (processingResult == null || processingResult.isDone())) {
            processor.clearWait();
            processingResult = processor.processAll();
        }
    }


    @Override
    public synchronized void stop() {
        if (processingResult != null) {
            processingResult.cancel(true);
        }
    }


    @Override
    public synchronized void notifyObjectAvailable(String objectIdentifier) {
        if (active && processor.isWaitingFor(objectIdentifier)) {
            processingResult = processor.processAll();
        }
    }
}
