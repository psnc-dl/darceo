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
package pl.psnc.synat.wrdz.zmkd.config;

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
import pl.psnc.synat.wrdz.common.utility.StringEncoder;

/**
 * Singleton of the ZMKD configuration of the WRDZ application.
 */
@Singleton
@Default
public class ZmkdConfiguration {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ZmkdConfiguration.class);

    /** Configuration file. */
    private static final String CONFIG_FILE = "zmkd-wrdz-config.xml";

    /** Configuration object. */
    private org.apache.commons.configuration.Configuration config;

    /** URL used to fetch objects from ZMD. */
    private String zmdObjectUrl;

    /** Cache home directory. */
    private String zmkdCachePath;


    /**
     * Creates the configuration object form the {@value #CONFIG_FILE} file.
     */
    @PostConstruct
    public void init() {
        try {
            config = new XMLConfiguration(CONFIG_FILE);
            zmdObjectUrl = config.getString("migration-plan.zmd-object-url");
            zmkdCachePath = (new File(getCacheHome())).getAbsolutePath();
            checkCacheConfiguration();
        } catch (ConfigurationException e) {
            logger.error("There was a problem with loading the configuration.", e);
            throw new WrdzConfigurationError(e);
        }
    }


    /**
     * Returns the path to the cache home directory.
     * 
     * @return path to the cache home directory
     */
    private String getCacheHome() {
        String cacheHome = StringEncoder.decodePath(config.getString("cache.home"));
        return (new File(cacheHome)).getAbsolutePath();
    }


    /**
     * Checks the cache configuration validity.
     */
    private void checkCacheConfiguration() {
        File cacheRoot = null;
        try {
            cacheRoot = new File(getCacheHome());
        } catch (NoSuchElementException nsee) {
            throw new WrdzConfigurationError("Cannot start application without configuring the cache folder!!!");
        }
        if (!(cacheRoot.exists() && cacheRoot.isDirectory())) {
            throw new WrdzConfigurationError("Cache home " + cacheRoot.getAbsolutePath()
                    + " does not exist or is not a directory");
        } else {
            if (!(cacheRoot.canRead() && cacheRoot.canWrite())) {
                throw new WrdzConfigurationError("Insufficient priviledges on cache home "
                        + cacheRoot.getAbsolutePath() + " - has to be readable and writeable!");
            }
        }
    }


    /**
     * Returns the base object URL that can be used to create an object in ZMD.
     * 
     * @return URL
     */
    public String getZmdObjectUrl() {
        if (zmdObjectUrl == null) {
            throw new IllegalStateException("ZMD object url is not set");
        }
        return zmdObjectUrl;
    }


    /**
     * Returns an URL that can be used to fetch the object with the given identifier.
     * 
     * @param objectIdentifier
     *            identifier of the digital object
     * 
     * @return URL
     */
    public String getZmdObjectUrl(String objectIdentifier) {
        if (zmdObjectUrl == null) {
            throw new IllegalStateException("ZMD object url is not set");
        }
        return zmdObjectUrl + "/" + objectIdentifier;
    }


    /**
     * Returns the absolute path to the working directory.
     * 
     * @param subfolder
     *            subfolder
     * @return absolute path to the working directory
     */
    public String getWorkingDirectory(String subfolder) {
        return zmkdCachePath + "/" + subfolder;
    }


    /**
     * Returns info whether resume migration plans after AS restart.
     * 
     * @return whether resume migration plans
     */
    public boolean resumePlans() {
        return config.getBoolean("migration-plan.resume", true);
    }

}
