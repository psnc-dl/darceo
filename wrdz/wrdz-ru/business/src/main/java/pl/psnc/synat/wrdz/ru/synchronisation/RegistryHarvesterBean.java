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

import java.net.URI;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.ru.dao.registries.RemoteRegistryDao;
import pl.psnc.synat.wrdz.ru.entity.registries.RemoteRegistry;
import pl.psnc.synat.wrdz.ru.exceptions.HarvestingException;
import pl.psnc.synat.wrdz.ru.services.Operation;
import pl.psnc.synat.wrdz.ru.services.Operations;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

/**
 * Bean providing the functionality of harvesting the given registry.
 */
@Singleton
@Startup
public class RegistryHarvesterBean implements RegistryHarvester {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(RegistryHarvesterBean.class);

    /**
     * State synchronizer for synchronizing operations state.
     */
    @EJB
    private StateSynchronizer stateSynchronizer;

    /**
     * Remote registries DAO.
     */
    @EJB
    private RemoteRegistryDao remoteRegistryDao;


    @Override
    public void harvestRegistry(RemoteRegistry remoteRegistry)
            throws HarvestingException {
        logger.debug("Started harvesting registry under location " + remoteRegistry.getLocationUrl());
        Operations operations = null;
        try {
            operations = getOperationsList(remoteRegistry);
        } catch (UniformInterfaceException exception) {
            logger.error("Failed harvesting registry under location " + remoteRegistry.getLocationUrl()
                    + "with exception: ", exception);
            throw new HarvestingException("Could not access repository at " + remoteRegistry.getLocationUrl(),
                    exception);
        }
        remoteRegistry.setLatestHarvestDate(operations.getTime());
        List<Operation> operationsList = operations.getOperations();
        int count = 0;
        for (Operation operation : operationsList) {
            try {
                stateSynchronizer.digestOperation(operation);
            } catch (EJBException exception) {
                continue;
            }
            count++;
        }
        updateHarvestDate(remoteRegistry, operations.getTime());
        logger.debug("Finished harvesting " + count + " descriptors from registry under location "
                + remoteRegistry.getLocationUrl());
    }


    /**
     * Fetches the operations list (list of changes) from the remote repository.
     * 
     * @param remoteRegistry
     *            harvested registry.
     * @return list of operations fetched from the registry.
     * @throws UniformInterfaceException
     *             if remote repository's reply have unexpected format.
     */
    private Operations getOperationsList(RemoteRegistry remoteRegistry)
            throws UniformInterfaceException {
        ClientConfig config = new DefaultClientConfig();
        config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
            new HTTPSProperties(getHostnameVerifier(), getSSLContext()));
        Client client = Client.create(config);
        client.addFilter(new HTTPBasicAuthFilter("test", "test"));
        WebResource service = client.resource(getURI(remoteRegistry));
        service.addFilter(new HTTPBasicAuthFilter("test", "test"));
        if (remoteRegistry.getLatestHarvestDate() != null) {
            service = service.queryParam("from", remoteRegistry.getLatestHarvestDate().toString());
        }
        return service.accept(MediaType.APPLICATION_XML).get(Operations.class);
    }


    /**
     * Retrieves the registry's URI.
     * 
     * @param remoteRegistry
     *            harvested registry.
     * @return retrieved URI.
     */
    private URI getURI(RemoteRegistry remoteRegistry) {
        return UriBuilder.fromUri(remoteRegistry.getLocationUrl()).build();
    }


    /**
     * Creates new host name verifier.
     * 
     * @return host name verifier.
     */
    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {

            @Override
            public boolean verify(String hostname, javax.net.ssl.SSLSession sslSession) {
                return true;
            }
        };
    }


    /**
     * Creates new SSL context.
     * 
     * @return SSL context.
     */
    private SSLContext getSSLContext() {
        SSLContext ctx = null;
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }


            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }


            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        } catch (java.security.GeneralSecurityException ex) {
            logger.error("Security exception error while checking SSL certificate.", ex);
        }
        return ctx;
    }


    /**
     * Updates registry's last harvest date.
     * 
     * @param remoteRegistry
     *            modified registry.
     * @param date
     *            date of the harvest.
     */
    private void updateHarvestDate(RemoteRegistry remoteRegistry, Date date) {
        remoteRegistry.setLatestHarvestDate(date);
        remoteRegistryDao.merge(remoteRegistry);
    }

}
