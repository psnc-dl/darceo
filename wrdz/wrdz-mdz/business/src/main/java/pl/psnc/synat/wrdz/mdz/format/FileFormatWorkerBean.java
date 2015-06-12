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
package pl.psnc.synat.wrdz.mdz.format;

import java.util.concurrent.Future;

import javax.ejb.Singleton;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.mdz.config.MdzConfiguration;

/**
 * Default implementation of the file format worker.
 * 
 * Manages the processing performed by the {@link FileFormatProcessor} by providing means to start and stop it when
 * required and ensures that only one processor is running at any given time.
 * 
 * <p>
 * This worker can be configured to be always active, in which case it can be started at any time as long as it is not
 * yet running. Keep in mind that calling {@link #activate()} and {@link #deactivate()} will still to attempt start and
 * stop the worker, respectively.
 * 
 * @see MdzConfiguration#getFormatWorkerAlwaysActive()
 */
@Singleton
public class FileFormatWorkerBean implements FileFormatWorker {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FileFormatWorkerBean.class);

    /** Processor instance. */
    @Inject
    private FileFormatProcessor processor;

    /** Module configuration. */
    @Inject
    private MdzConfiguration configuration;

    /** Future object returned by the last called processor method. */
    private Future<Void> processingResult;

    /** Whether the worker is currently active. */
    private boolean active;


    @Override
    public synchronized void activate() {
        logger.info("Activating format worker");
        active = true;
        start();
    }


    @Override
    public synchronized void deactivate() {
        logger.info("Deactivating format worker");
        active = false;
        stop();
    }


    @Override
    public synchronized void start() {
        boolean on = active || configuration.getFormatWorkerAlwaysActive();
        if (on && (processingResult == null || processingResult.isDone())) {
            processingResult = processor.processAll();
        }
    }


    @Override
    public synchronized void stop() {
        if (processingResult != null) {
            processingResult.cancel(true);
        }
    }
}
