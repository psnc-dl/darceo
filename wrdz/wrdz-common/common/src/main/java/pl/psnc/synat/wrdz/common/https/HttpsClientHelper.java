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
package pl.psnc.synat.wrdz.common.https;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.config.Configuration;
import pl.psnc.synat.wrdz.common.config.WrdzModule;
import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;

/**
 * Provides methods accessing to Https clients that can authenticate via HTTPS BASIC.
 */
@Singleton
@Default
public class HttpsClientHelper {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(HttpsClientHelper.class);

    /**
     * Secret Key.
     */
    private static final String SECRET = "DARCEOWRDZDARCEO";

    /**
     * Configuration.
     */
    @Inject
    private Configuration config;

    /**
     * Map of HTTPS clients for WRDZ modules.
     */
    private Map<WrdzModule, DefaultHttpClient> httpsClients;


    /**
     * Init method.
     */
    @PostConstruct
    public void init() {
        httpsClients = new HashMap<WrdzModule, DefaultHttpClient>();
    }


    /**
     * Gets HTTPS client that can authenticate in WRDZ modules.
     * 
     * @param module
     *            module that wants to be authenticated
     * @return HTTPS client
     */
    public synchronized HttpClient getHttpsClient(WrdzModule module) {
        DefaultHttpClient httpClient = httpsClients.get(module);
        if (httpClient == null) {
            logger.debug("HTTPS client for module " + module.name() + " is not yet initialized");
            try {
                SSLSocketFactory socketFactory;
                if (config.getHttpsVerifyHostname()) {
                    socketFactory = new SSLSocketFactory(new TrustAllStrategy());
                } else {
                    socketFactory = new SSLSocketFactory(new TrustAllStrategy(),
                            SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                }
                Scheme scheme = new Scheme("https", 443, socketFactory);
                PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
                connectionManager.getSchemeRegistry().register(scheme);

                String cipher = config.getModulesPassword();
                byte[] key = SECRET.getBytes("utf-8");
                Cipher c = Cipher.getInstance("AES");
                SecretKeySpec k = new SecretKeySpec(key, "AES");
                c.init(Cipher.DECRYPT_MODE, k);
                byte[] decrypted = c.doFinal(Base64.decodeBase64(cipher));
                String password = new String(decrypted, "utf-8");
                UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(module.name(), password);

                httpClient = new DefaultHttpClient(connectionManager);
                httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
                httpsClients.put(module, httpClient);
            } catch (Exception e) {
                throw new WrdzRuntimeException(e.getMessage(), e);
            }
        }
        return httpClient;
    }


    /**
     * Stores response entity in specified folder. The folder is created if it doesn't exist.
     * 
     * @param folder
     *            folder where to save entity
     * @param entity
     *            entity
     * @param headerContentDisposition
     *            Content-Disposition header
     * @return file reference to the saved file
     * @throws IOException
     *             when some IO problem occurred
     */
    public File storeResponseEntity(String folder, HttpEntity entity, Header headerContentDisposition)
            throws IOException {
        File file = new File(folder);
        file.mkdir();
        return storeResponseEntity(file, entity, headerContentDisposition);
    }


    /**
     * Stores the response entity in the specified folder.
     * 
     * @param folder
     *            folder where to save entity
     * @param entity
     *            entity
     * @param contentDisposition
     *            Content-Disposition header
     * @return file reference to the saved file
     * @throws IOException
     *             when some IO problem occurred
     */
    public File storeResponseEntity(File folder, HttpEntity entity, Header contentDisposition)
            throws IOException {
        String filename = contentDisposition.getValue().replaceFirst("filename=", "");
        File file = new File(folder, filename);
        try {
            FileOutputStream stream = new FileOutputStream(file);
            try {
                entity.writeTo(stream);
            } finally {
                IOUtils.closeQuietly(stream);
            }
        } catch (IOException e) {
            file.delete();
            throw e;
        } finally {
            EntityUtils.consumeQuietly(entity);
        }
        return file;
    }

}
