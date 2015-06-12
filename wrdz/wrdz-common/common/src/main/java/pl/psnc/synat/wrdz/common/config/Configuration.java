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
package pl.psnc.synat.wrdz.common.config;

import java.io.File;
import java.util.NoSuchElementException;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Singleton;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.exception.WrdzConfigurationError;
import pl.psnc.synat.wrdz.common.performance.Profiler;
import pl.psnc.synat.wrdz.common.utility.StringEncoder;

/**
 * Singleton of the common configuration of the WRDZ application.
 */
@Singleton
@Default
public class Configuration {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    /** Configuration file. */
    private static final String CONFIG_FILE = "wrdz-config.xml";

    /** Configuration file element path for {@link #profilerEnabled} (absolute). */
    private static final String PROFILER_ENABLED = "profiler.enabled";

    /** Configuration object. */
    private org.apache.commons.configuration.Configuration config;

    /** Whether the profiler is enabled. */
    private boolean profilerEnabled;


    /**
     * Creates the configuration object form the {@value #CONFIG_FILE} file.
     */
    @PostConstruct
    public void init() {
        try {
            config = new XMLConfiguration(CONFIG_FILE);
            checkAsyncCacheConfiguration();
            profilerEnabled = config.getBoolean(PROFILER_ENABLED);
            if (profilerEnabled) {
                Profiler.enable();
            }
        } catch (ConfigurationException e) {
            logger.error("There was a problem with loading the configuration.", e);
            throw new WrdzConfigurationError(e);
        }
    }


    /**
     * Checks the cache for asynchronous request processing configuration validity.
     */
    private void checkAsyncCacheConfiguration() {
        File asyncCacheRoot = null;
        try {
            asyncCacheRoot = new File(getAsyncCacheHome());
        } catch (NoSuchElementException nsee) {
            throw new WrdzConfigurationError(
                    "Cannot start application without configuring the cache folder for asynchronous request processing!!!");
        }
        if (!(asyncCacheRoot.exists() && asyncCacheRoot.isDirectory())) {
            throw new WrdzConfigurationError("Cache home for asynchronous request processing "
                    + asyncCacheRoot.getAbsolutePath() + " does not exist or is not a directory");
        } else {
            if (!(asyncCacheRoot.canRead() && asyncCacheRoot.canWrite())) {
                throw new WrdzConfigurationError(
                        "Insufficient priviledges on cache home for asynchronous request processing "
                                + asyncCacheRoot.getAbsolutePath() + " - has to be readable and writeable!");
            }
        }
    }


    /**
     * Returns the default locale for WRDZ application.
     * 
     * @return system default locale
     */
    public String getSystemDefaultLocale() {
        return config.getString("locale");
    }


    /**
     * Returns the path to the cache home directory for prepared responses for asynchronous requests.
     * 
     * @return path to the cache home directory for prepared responses for asynchronous requests
     */
    public String getAsyncCacheHome() {
        String asyncCacheHome = StringEncoder.decodePath(config.getString("async.cache-home"));
        return (new File(asyncCacheHome)).getAbsolutePath();
    }


    /**
     * Returns the period (in days) after which responses for asynchronous request will be removed.
     * 
     * @return period (in days) after which responses for asynchronous request will be removed
     */
    public int getAsyncCleaningPeriod() {
        return config.getInt("async.cleaning-period");
    }


    /**
     * Whether to verify the hostname for https connections.
     * 
     * @return <code>true</code> if hostname should be verified
     */
    public boolean getHttpsVerifyHostname() {
        return config.getBoolean("https.verify-hostname");
    }


    public String getModulesPassword() {
        return config.getString("https.modules-password");
    }


    public boolean isProfilerEnabled() {
        return profilerEnabled;
    }

}
