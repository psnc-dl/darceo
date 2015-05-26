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
package pl.psnc.synat.sra.owlim;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;

import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapterInternalException;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RepositoryInfo;
import org.openrdf.repository.manager.RepositoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.sra.SemanticRepositoryResourceAdapterImpl;

/**
 * Resource adapter for OWLIM-style semantic repository.
 * 
 */
public class OwlimSemanticRepositoryResourceAdapter extends SemanticRepositoryResourceAdapterImpl {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(OwlimSemanticRepositoryResourceAdapter.class);

    /**
     * Name of a repository.
     */
    private String repositoryName;

    /**
     * OWLIM repository manager.
     */
    private RepositoryManager repositoryManager;

    /**
     * OWLIM repository.
     */
    private Repository repository;


    @Override
    public void start(BootstrapContext ctx)
            throws ResourceAdapterInternalException {
        logger.debug("OwlimSemanticRepositoryResourceAdapter started");
        logger.debug("BootstrapContext:" + ctx);
        String owlimRepoFolder = System.getenv("OWLIM_DATA");
        if (owlimRepoFolder == null) {
            throw new ResourceAdapterInternalException("Environment variable OWLIM_DATA is not set at all.");
        }
        System.setProperty("info.aduna.platform.appdata.basedir", owlimRepoFolder);
        repositoryManager = new LocalRepositoryManager(new File(owlimRepoFolder));
        try {
            repositoryManager.initialize();
        } catch (RepositoryException e) {
            logger.error("It seems that the environment variable OWLIM_DATA is not set correctly", e);
            throw new ResourceAdapterInternalException("Environment variable OWLIM_DATA is not set correctly.", e);
        }
        try {
            logger.debug("OWLIM Repository Manager Location: " + repositoryManager.getLocation());
        } catch (MalformedURLException e) {
            logger.error("It seems that the environment variable OWLIM_DATA is not set correctly", e);
            throw new ResourceAdapterInternalException("Environment variable OWLIM_DATA is not set correctly.", e);
        }
        try {
            logger.debug("repository name " + repositoryName);
            repository = repositoryManager.getRepository(repositoryName);
        } catch (RepositoryConfigException e) {
            logger.error("It seems that " + repositoryName
                    + " OWLIM repository does not exists or the environment variable "
                    + "OWLIM_DATA is not set correctly.", e);
            throw new ResourceAdapterInternalException("It seems that " + repositoryName
                    + " OWLIM repository does not exists or "
                    + "the environment variable OWLIM_DATA is not set correctly.", e);
        } catch (RepositoryException e) {
            logger.error("Error getting the OWLIM repository from the repository manager.", e);
            throw new ResourceAdapterInternalException(
                    "Error getting the OWLIM repository from the repository manager.", e);
        }
        if (repository == null) {
            logger.debug("It seems that " + repositoryName
                    + " OWLIM repository does not exists. Accessible repositories are as follows: ");
            Collection<RepositoryInfo> allRepositoryInfos;
            try {
                allRepositoryInfos = repositoryManager.getAllRepositoryInfos();
                for (RepositoryInfo repositoryInfo : allRepositoryInfos) {
                    logger.debug("- " + repositoryInfo.getId());
                }
            } catch (RepositoryException e) {
                logger.error("Error getting the OWLIM repositories from the repository manager.", e);
            }
            throw new ResourceAdapterInternalException("It seems that " + repositoryName
                    + " OWLIM repository does not exists.");
        }
    }


    @Override
    public void stop() {
        logger.debug("OwlimSemanticRepositoryResourceAdapter stopped");
        if (repository != null) {
            try {
                repository.shutDown();
            } catch (RepositoryException e) {
                logger.error("Error while shutting down the OWLIM repository.", e);
            }
        }
        repositoryManager.shutDown();
    }


    public String getRepositoryName() {
        return repositoryName;
    }


    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }


    public RepositoryManager getRepositoryManager() {
        return repositoryManager;
    }


    public Repository getRepository() {
        return repository;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("OwlimSemanticRepositoryResourceAdapter ");
        sb.append("[hashCode = ").append(hashCode());
        sb.append("[repositoryManager = ").append(repositoryManager);
        sb.append("]");
        return sb.toString();
    }

}
