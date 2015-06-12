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
package pl.psnc.synat.wrdz.zmkd.invocation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.common.https.TrustAllStrategy;
import pl.psnc.synat.wrdz.zmkd.service.ServiceOutcomeInfo;

/**
 * Caller of REST services.
 */
@Default
@Singleton
public class RestServiceCaller {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(RestServiceCaller.class);


    /**
     * Invoke a service according to the execution info.
     * 
     * In case of success, the entity stream is not closed.
     * 
     * @param execInfo
     *            info how to execute a REST service
     * @return execution outcome
     * @throws InvalidHttpRequestException
     *             when constructed request is invalid
     * @throws InvalidHttpResponseException
     *             when response is invalid
     */
    public ExecutionOutcome invoke(ExecutionInfo execInfo)
            throws InvalidHttpRequestException, InvalidHttpResponseException {
        HttpClient httpClient = getHttpClient();
        HttpRequestBase request = null;
        try {
            request = RestServiceCallerUtils.constructServiceRequestBase(execInfo);
        } catch (URISyntaxException e) {
            logger.error("Incorrect URI of the service.", e);
            throw new InvalidHttpRequestException(e);
        }
        if (request instanceof HttpEntityEnclosingRequestBase) {
            HttpEntity entity = RestServiceCallerUtils.constructServiceRequestEntity(execInfo);
            if (entity != null) {
                ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
            }
            Header header = RestServiceCallerUtils.constructServiceRequestHeader(execInfo);
            if (header != null) {
                request.setHeader(header);
            }
        }

        HttpParams params = new BasicHttpParams();
        params.setParameter("http.protocol.handle-redirects", false);
        request.setParams(params);

        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (ClientProtocolException e) {
            logger.error("Incorrect protocol.", e);
            throw new InvalidHttpResponseException(e);
        } catch (IOException e) {
            logger.error("IO error during execution.", e);
            throw new InvalidHttpResponseException(e);
        }
        try {
            return RestServiceCallerUtils.retrieveOutcome(response);
        } catch (IllegalStateException e) {
            logger.error("Cannot retrived the stream with the content.", e);
            EntityUtils.consumeQuietly(response.getEntity());
            throw new InvalidHttpResponseException(e);
        } catch (IOException e) {
            logger.error("IO error when retrieving content.", e);
            EntityUtils.consumeQuietly(response.getEntity());
            throw new InvalidHttpResponseException(e);
        }
        // remember to close the entity stream after read it.  
    }


    /**
     * Validates the outcome according to the description of the service, and returns the matched.
     * 
     * @param execOutcome
     *            execution outcome
     * @param servOutcomeInfos
     *            description of the outcomes in OWL-S and WADL description
     * @return description of the service outcomes that fits to the execution outcome
     * @throws UnexpectedHttpResponseException
     *             when retrieved HTTP response is different than this described in OWL-S and WADL.
     */
    public List<ServiceOutcomeInfo> validateOutcome(ExecutionOutcome execOutcome,
            List<ServiceOutcomeInfo> servOutcomeInfos)
            throws UnexpectedHttpResponseException {
        List<ServiceOutcomeInfo> matchedOutcomeInfos = new ArrayList<ServiceOutcomeInfo>();
        for (ServiceOutcomeInfo servOutcomeInfo : servOutcomeInfos) {
            if (servOutcomeInfo.getStatuses().contains(execOutcome.getStatusCode())) {
                switch (servOutcomeInfo.getStyle()) {
                    case BODY:
                        if (validateContent(execOutcome, servOutcomeInfo)) {
                            matchedOutcomeInfos.add(servOutcomeInfo);
                        }
                        break;
                    case HEADER:
                        if (validateHeader(execOutcome, servOutcomeInfo)) {
                            matchedOutcomeInfos.add(servOutcomeInfo);
                        }
                        break;
                    default:
                        throw new WrdzRuntimeException("Unknown the style of the outcome: "
                                + servOutcomeInfo.getStyle());
                }
            }
        }
        if (matchedOutcomeInfos.size() > 0) {
            return matchedOutcomeInfos;
        } else {
            throw new UnexpectedHttpResponseException("None of transformation outcome fits to execution outcome.",
                    execOutcome.getStatusCode(), execOutcome.getContentType());
        }
    }


    /**
     * Validate the content.
     * 
     * @param execOutcome
     *            execution outcome
     * @param servOutcomeInfo
     *            description of the outcome in OWL-S and WADL description
     * @return whether they match
     * @throws UnexpectedHttpResponseException
     *             when mimetype of retrieved HTTP response is different than this described in OWL-S and WADL.
     */
    private boolean validateContent(ExecutionOutcome execOutcome, ServiceOutcomeInfo servOutcomeInfo)
            throws UnexpectedHttpResponseException {
        if (execOutcome.getContentType() == null || execOutcome.getContentType().equals("application/octet-stream")) {
            return true; // also generic mimetype in the result is OK
        }
        if (execOutcome.getContentType().startsWith("text/plain") && servOutcomeInfo.getTechnicalTypes() != null) {
            return true; // some parameter as a whole body is OK  
        }
        for (Entry<String, String> possibleMimetype : servOutcomeInfo.getMimetypes().entrySet()) {
            if (execOutcome.getContentType().startsWith(possibleMimetype.getValue())) {
                return true; // otherwise, check the content type
            }
        }
        throw new UnexpectedHttpResponseException("Actual mimetype does not fit to service description mimetypes "
                + servOutcomeInfo.getMimetypes().values(), execOutcome.getStatusCode(), execOutcome.getContentType());
    }


    /**
     * Validate headers.
     * 
     * @param execOutcome
     *            execution outcome
     * @param servOutcomeInfo
     *            description of the outcome in OWL-S and WADL description
     * @return whether they match
     */
    private boolean validateHeader(ExecutionOutcome execOutcome, ServiceOutcomeInfo servOutcomeInfo) {
        return true;
    }


    /**
     * Gets client that can use HTTP or HTTPS protocol accepting all servers certificates.
     * 
     * @return HTTP client
     */
    private HttpClient getHttpClient() {
        Scheme httpScheme = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
        Scheme httpsScheme = null;
        try {
            httpsScheme = new Scheme("https", 443, new SSLSocketFactory(new TrustAllStrategy(),
                    SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
        } catch (KeyManagementException e) {
            throw new WrdzRuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            throw new WrdzRuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new WrdzRuntimeException(e);
        } catch (KeyStoreException e) {
            throw new WrdzRuntimeException(e);
        }

        ClientConnectionManager connectionManager = new BasicClientConnectionManager();
        connectionManager.getSchemeRegistry().register(httpScheme);
        connectionManager.getSchemeRegistry().register(httpsScheme);

        return new DefaultHttpClient(connectionManager);
    }

}
