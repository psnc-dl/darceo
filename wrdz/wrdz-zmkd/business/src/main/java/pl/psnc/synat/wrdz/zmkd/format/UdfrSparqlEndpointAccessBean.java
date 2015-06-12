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
package pl.psnc.synat.wrdz.zmkd.format;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.common.jaxb.NamespaceContextImpl;
import pl.psnc.synat.wrdz.zmkd.config.SparqlQueries;

/**
 * Provides a sparql access to the UDFR registry.
 * 
 */
@Singleton
@Startup
public class UdfrSparqlEndpointAccessBean {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(UdfrSparqlEndpointAccessBean.class);

    /** URI of the UDFR http endpoint. */
    private static final String UDFR_ENDPOINT = "http://udfr.org/ontowiki/sparql/";

    /** UDFR http endpoint post request parameter containing the sparql query to be executed. */
    private static final String UDFR_POST_PARAM = "query";

    /** Sparql results namespace prefix (to be used in XPath expressions). */
    private static final String NS_PREFIX = "sp";

    /** Sparql results namespace uri. */
    private static final String NS_URI = "http://www.w3.org/2005/sparql-results#";

    /** XPath expression that locates the format UDFR IRI text node. */
    private static final String XPATH_UDFR_IRI = "//sp:binding[@name='fileFormat']/sp:uri/text()";

    /** XPath expression that located the format PUID text node. */
    private static final String XPATH_PUID = "//sp:binding[@name='fileFormatPuid']/sp:literal/text()";

    /** XPath expression that locates the format mimetype text node. */
    private static final String XPATH_MIMETYPE = "//sp:binding[@name='mimetype']/sp:literal/text()";

    /** XPath expression that located the format default extension text node. */
    private static final String XPATH_EXTENSION = "//sp:binding[@name='extension']/sp:literal/text()";

    /** Bean that caches SPARQL queries. */
    @Inject
    private SparqlQueries sparqlQueries;


    /**
     * Retrieves the UDFR format IRI for the given PUID.
     * 
     * @param puid
     *            format PUID
     * @return UDFR format IRI
     * @throws UnrecognizedPuidException
     *             when format PUID does not exist in UDFR
     * @throws UdfrServiceException
     *             when a problem with UDFR communication occurs
     */
    public String getUdfrIriForPuid(String puid)
            throws UnrecognizedPuidException, UdfrServiceException {
        HttpClient client = new DefaultHttpClient();
        Document result = executeUdfrQuery(String.format(sparqlQueries.getFormatIriQuery(), puid), client);

        NodeList nodes = xpath(result, XPATH_UDFR_IRI);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getNodeValue();
        } else {
            throw new UnrecognizedPuidException("The is no UDFR IRI for the given PUID in UDFR", puid);
        }
    }


    /**
     * Retrieves the format PUID for the given UDFR IRI.
     * 
     * @param iri
     *            UDFR format IRI
     * @return format PUID
     * @throws UnrecognizedIriException
     *             when the given UDFR format IRI does not exist in UDFR
     * @throws UdfrServiceException
     *             when a problem with UDFR communication occurs
     */
    public String getPuidForUdfrIri(String iri)
            throws UnrecognizedIriException, UdfrServiceException {
        HttpClient client = new DefaultHttpClient();
        Document result = executeUdfrQuery(String.format(sparqlQueries.getFormatPuidQuery(), iri), client);

        NodeList nodes = xpath(result, XPATH_PUID);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getNodeValue();
        } else {
            throw new UnrecognizedIriException("There is no such format IRI in the UDFR", iri);
        }
    }


    /**
     * Retrieves the format mimetype and default extension for the given UDFR IRI.
     * 
     * @param iri
     *            UDFR format IRI
     * @return mimetype and default extension
     * @throws UdfrServiceException
     *             when a problem with UDFR communication occurs
     */
    public FileFormatExt getMimetypeForUdfrIri(String iri)
            throws UdfrServiceException {
        HttpClient client = new DefaultHttpClient();
        Document result = executeUdfrQuery(String.format(sparqlQueries.getFormatMimetypeQuery(), iri), client);
        FileFormatExt fileFormatExt = new FileFormatExt();
        NodeList nodes = xpath(result, XPATH_MIMETYPE);
        if (nodes.getLength() > 0) {
            fileFormatExt.setMimetype(nodes.item(0).getNodeValue());
        }
        nodes = xpath(result, XPATH_EXTENSION);
        if (nodes.getLength() > 0) {
            fileFormatExt.setExtension(nodes.item(0).getNodeValue());
        }
        return fileFormatExt;
    }


    /**
     * Executes the given sparql query via an http post request to UDFR and returns the parsed xml response.
     * 
     * @param query
     *            sparql query to be executed
     * @param client
     *            configured http client
     * @return parsed UDFR response
     * @throws UdfrServiceException
     *             when the result from UDRF is incorrect or the connection cannot be established.
     */
    private Document executeUdfrQuery(String query, HttpClient client)
            throws UdfrServiceException {
        HttpPost request = new HttpPost(UDFR_ENDPOINT);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair(UDFR_POST_PARAM, query));
        try {
            request.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new WrdzRuntimeException("Encoding UTF-8 is not supported", e);
        }
        try {
            HttpResponse response = client.execute(request);
            Document result;
            switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_OK:
                    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                    domFactory.setNamespaceAware(true);
                    DocumentBuilder builder = domFactory.newDocumentBuilder();
                    result = builder.parse(response.getEntity().getContent());
                    break;
                default:
                    logger.error("Unexpected response from UDFR: " + response.getStatusLine());
                    throw new UdfrServiceException("Unexpected response from UDFR: " + response.getStatusLine());
            }
            EntityUtils.consume(response.getEntity());
            return result;
        } catch (IOException e) {
            logger.error("Could not connect to UDFR", e);
            throw new UdfrServiceException("Could not connect to UDFR", e);
        } catch (SAXException e) {
            logger.error("Could not parse a result from UDFR", e);
            throw new UdfrServiceException("Could not parse the result from UDFR", e);
        } catch (ParserConfigurationException e) {
            throw new WrdzRuntimeException(e.getMessage(), e);
        }
    }


    /**
     * Retrieves the nodes from the given document using the given XPath path.
     * 
     * @param document
     *            the document to retrieve the nodes from
     * @param path
     *            XPath
     * @return list of matching nodes
     */
    private NodeList xpath(Document document, String path) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new NamespaceContextImpl(NS_PREFIX, NS_URI));

        try {
            return (NodeList) xpath.evaluate(path, document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new WrdzRuntimeException("Incorrect XPath expression", e);
        }
    }
}
