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
package pl.psnc.synat.wrdz.zmd.config;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
import pl.psnc.synat.wrdz.zmd.entity.types.HashType;
import pl.psnc.synat.wrdz.zmd.entity.types.IdentifierType;

/**
 * Singleton of the ZMD configuration of the WRDZ application.
 */
@Singleton
@Default
public class ZmdConfiguration {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ZmdConfiguration.class);

    /** Configuration file. */
    private static final String CONFIG_FILE = "zmd-wrdz-config.xml";

    /** Configuration object. */
    private org.apache.commons.configuration.Configuration config;


    /**
     * Creates the configuration object form the {@value #CONFIG_FILE} file.
     */
    @PostConstruct
    public void init() {
        try {
            config = new XMLConfiguration(CONFIG_FILE);
            checkCacheConfiguration();
        } catch (ConfigurationException e) {
            logger.error("There was a problem with loading the configuration.", e);
            throw new WrdzConfigurationError(e);
        }
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
     * Returns the path to the cache home directory.
     * 
     * @return path to the cache home directory
     */
    public String getCacheHome() {
        String cacheHome = StringEncoder.decodePath(config.getString("cache.home"));
        return (new File(cacheHome)).getAbsolutePath();
    }


    /**
     * Returns the number of hours after which item in cache is considered stale and ready to be removed.
     * 
     * @return number of hours after which item in cache is considered stale.
     */
    public Integer getCacheStaleAfter() {
        return (new Integer(config.getString("cache.stale-after")));
    }


    /**
     * Returns the default type for object identifiers.
     * 
     * @return default type for object identifiers
     */
    public IdentifierType getDefaultIdentifierType() {
        return IdentifierType.valueOf(config.getString("identifier.default-type").toUpperCase());
    }


    /**
     * Returns the domain for object identifiers.
     * 
     * @return domain for object identifiers
     */
    public String getObjectIdentifierDomain() {
        return config.getString("identifier.oi-domain");
    }


    /**
     * Returns the default hash type used for calculating file's hashes.
     * 
     * @return the default hash type used for calculating file's hashes
     */
    public HashType getDefaultHashType() {
        return HashType.valueOf(config.getString("metadata.administrative.hashing-algorithm").toUpperCase());
    }


    /**
     * Returns info whether construct METS metadata.
     * 
     * @return whether construct METS metadata
     */
    public boolean constructMetsMetadata() {
        return config.getBoolean("metadata.mets.construction");
    }


    /**
     * Returns the default administrative metadata type used for building these metadata.
     * 
     * @return the default administrative metadata type used for building these metadata
     */
    public String getDefaultAdministrativeMetadataType() {
        return config.getString("metadata.administrative.type").toUpperCase();
    }


    /**
     * Returns the max number of validation messages that can be save in administrative metadata.
     * 
     * @return the max number of validation messages
     */
    public int getMaxValidationMessages() {
        return config.getInt("metadata.administrative.max-validation-messages", 0);
    }


    /**
     * Returns info whether extract technical metadata.
     * 
     * @return whether extract technical metadata
     */
    public boolean extractTechnicalMetadata() {
        return config.getBoolean("metadata.technical.extraction");
    }


    /**
     * Gets the name of OAI-PMH repository name.
     * 
     * @return name of the OAI-PMH repository.
     * @throws WrdzConfigurationError
     *             when configuration is missing this entry.
     */
    public String getOaiRepositoryName()
            throws WrdzConfigurationError {
        String result = config.getString("oai-pmh.repository-name");
        if (result == null) {
            throw new WrdzConfigurationError("Required property OAI-PMH repository name was not found in config.");
        }
        return result;
    }


    /**
     * Gets earliest date in OAI-PMH repository.
     * 
     * @return earliest date in OAI-PMH repository.
     * @throws WrdzConfigurationError
     *             when configuration is missing this entry.
     */
    public Date getOaiEarliestDatestamp()
            throws WrdzConfigurationError {
        String date = config.getString("oai-pmh.earliest-datestamp");
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        if (date == null) {
            throw new WrdzConfigurationError("Required property OAI-PMH earliest datestamp was not found in config.");
        }
        try {
            return (Date) formatter.parse(date);
        } catch (ParseException e) {
            throw new WrdzConfigurationError(
                    "Required property OAI-PMH earliest datestamp is not expressed in format YYYY-MM-DDThh:mm:ssZ");
        }
    }


    /**
     * Gets email addresses of administrators exposed by OAI-PMH.
     * 
     * @return list of administrator's email addresses.
     * @throws WrdzConfigurationError
     *             when configuration is missing this entry.
     */
    @SuppressWarnings("unchecked")
    public List<String> getOaiAdminEmail()
            throws WrdzConfigurationError {
        List<String> result = config.getList("oai-pmh.admin-email");
        if (result == null || result.size() < 1) {
            throw new WrdzConfigurationError("Required property OAI-PMH administrator email was not found in config.");
        }
        return result;
    }


    public int getOaiTokenLongetivity() {
        return config.getInt("oai-pmh.token-longetivity");
    }


    public int getOaiIdentifiersPageSize() {
        return config.getInt("oai-pmh.identifiers-page-size");
    }


    public int getOaiRecordsPageSize() {
        return config.getInt("oai-pmh.records-page-size");
    }


    public boolean saveInRepository() {
        return config.getBoolean("data.locally", true);
    }

}
