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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
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
import pl.psnc.synat.wrdz.mdz.config.MdzConfiguration;
import pl.psnc.synat.wrdz.mdz.entity.format.FileFormat;

/**
 * Default implementation of the file format verifier which contacts UDFR to obtain format information.
 * <p>
 * Format requires migration if:
 * <ul>
 * <li>it has a defined withdrawal date, or
 * <li>it has a defined successor format and was released sufficiently long ago.
 * </ul>
 * How old a format must be to trigger the second condition is defined in the module configuration.
 */
@Stateless
public class FileFormatVerifierBean implements FileFormatVerifier {

    /** URI of the UDFR http endpoint. */
    private static final String UDFR_ENDPOINT = "http://udfr.org/ontowiki/sparql/";

    /** UDFR http endpoint post request parameter containing the sparql query to be executed. */
    private static final String UDFR_POST_PARAM = "query";

    /** Sparql results namespace prefix (to be used in XPath expressions). */
    private static final String NS_PREFIX = "sp";

    /** Sparql results namespace uri. */
    private static final String NS_URI = "http://www.w3.org/2005/sparql-results#";

    /** XPath expression that locates the withdrawal date text nodes. */
    private static final String XPATH_WITHDRAWAL_DATE = "//sp:binding[@name='withdrawalDate']/sp:literal/text()";

    /** XPath expression that locates the release date text nodes. */
    private static final String XPATH_RELEASE_DATE = "//sp:binding[@name='releaseDate']/sp:literal/text()";

    /** XPath expression that locates the result elements. */
    private static final String XPATH_RESULT = "//sp:result";

    /** Date format used for parsing dates returned by UDFR. */
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    /** Query encoding. */
    private static final String ENCODING = "UTF-8";

    /** Path to the file containing the sparql query that retrieves format release/withdrawal dates. */
    private static final String DATES_QUERY_FILE = "/format-dates.sparql";

    /** Path to the file containing the sparql query that retrieves format successors. */
    private static final String SUCCESSORS_QUERY_FILE = "/format-successors.sparql";

    /** Number of milliseconds in a day. */
    private static final int MILLIS_IN_DAY = 86400000;

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FileFormatVerifierBean.class);

    /** The sparql query that retrieves format release/withdrawal dates. */
    private String datesQuery;

    /** The sparql query that retrieves format successors. */
    private String successorsQuery;

    /** How many milliseconds must pass since format's release before it is considered old. */
    private long threshold;

    /** Module configuration. */
    @Inject
    private MdzConfiguration configuration;


    /**
     * Reads the sparql queries from resource files.
     */
    @PostConstruct
    protected void init() {
        InputStream datesInput = null;
        InputStream successorsInput = null;
        try {
            datesInput = getClass().getResourceAsStream(DATES_QUERY_FILE);
            datesQuery = IOUtils.toString(datesInput, ENCODING);
            successorsInput = getClass().getResourceAsStream(SUCCESSORS_QUERY_FILE);
            successorsQuery = IOUtils.toString(successorsInput, ENCODING);
        } catch (IOException e) {
            throw new WrdzRuntimeException("Could not read sparql query files", e);
        } finally {
            IOUtils.closeQuietly(datesInput);
            IOUtils.closeQuietly(successorsInput);
        }
        threshold = (long) configuration.getFormatVerifierThreshold() * MILLIS_IN_DAY;
    }


    @Override
    public boolean isMigrationRequired(FileFormat format) {

        HttpClient client = new DefaultHttpClient();

        Document dates = executeUdfrQuery(getDatesQuery(format.getPuid()), client);

        Date withdrawalDate = extractDate(dates, XPATH_WITHDRAWAL_DATE);
        if (withdrawalDate != null) {
            // format has an expiration date
            return true;
        }

        Date releaseDate = extractDate(dates, XPATH_RELEASE_DATE);
        if (releaseDate != null && System.currentTimeMillis() - releaseDate.getTime() > threshold) {
            Document successors = executeUdfrQuery(getSuccessorsQuery(format.getPuid()), client);
            if (countNodes(successors, XPATH_RESULT) > 0) {
                // format is old and has at least one successor
                return true;
            }
        }

        return false;
    }


    /**
     * Constructs a sparql query that will retrieve release and withdrawal dates of the format with the given puid.
     * 
     * @param puid
     *            format PRONOM database identifier
     * @return sparql query
     */
    private String getDatesQuery(String puid) {
        return String.format(datesQuery, puid);
    }


    /**
     * Constructs a sparql query that will retrieve successors of the format with the given puid.
     * 
     * @param puid
     *            format PRONOM database identifier
     * @return sparql query
     */
    private String getSuccessorsQuery(String puid) {
        return String.format(successorsQuery, puid);
    }


    /**
     * Executes the given sparql query via an http post request to UDFR and returns the parsed xml response.
     * 
     * @param query
     *            sparql query to be executed
     * @param client
     *            configured http client
     * @return parsed UDFR response
     */
    private Document executeUdfrQuery(String query, HttpClient client) {
        HttpPost request = new HttpPost(UDFR_ENDPOINT);

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair(UDFR_POST_PARAM, query));
        try {
            request.setEntity(new UrlEncodedFormEntity(pairs, ENCODING));
        } catch (UnsupportedEncodingException e) {
            // this should not happen - incorrect encoding specified in the constant?
            throw new RuntimeException("Encoding " + ENCODING + " is not supported", e);
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
                    throw new WrdzRuntimeException("Unexpected response from UDFR: " + response.getStatusLine());
            }

            EntityUtils.consume(response.getEntity());

            return result;

        } catch (IOException e) {
            throw new WrdzRuntimeException("Could not connect to UDFR", e);
        } catch (SAXException e) {
            throw new WrdzRuntimeException("Error while parsing UDFR response", e);
        } catch (ParserConfigurationException e) {
            throw new WrdzRuntimeException(e.getMessage(), e);
        }
    }


    /**
     * Returns the nodes from the given xml document that match the given xpath expression.
     * 
     * @param document
     *            xml document to search
     * @param path
     *            xpath expression
     * @return matching nodes
     */
    private NodeList xpath(Document document, String path) {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(new NamespaceContextImpl(NS_PREFIX, NS_URI));

        try {
            return (NodeList) xpath.evaluate(path, document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Incorrect XPath expression", e);
        }
    }


    /**
     * Extracts a date from nodes matching the xpath expression in the given document.
     * 
     * If multiple dates match, the earliest date is returned.
     * 
     * @param document
     *            xml document to search
     * @param xpath
     *            xpath expression
     * @return earliest matching date, or <code>null</code> if no dates were found
     */
    private Date extractDate(Document document, String xpath) {
        NodeList nodes = xpath(document, xpath);

        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        Date result = null;
        for (int i = 0; i < nodes.getLength(); i++) {
            try {
                Date newDate = format.parse(nodes.item(i).getNodeValue());
                if (result == null || result.after(newDate)) {
                    result = newDate;
                }
            } catch (ParseException e) {
                logger.warn("Unparsable date retrieved from UDFR response: {}", nodes.item(i).getNodeValue());
            }
        }

        return result;
    }


    /**
     * Counts the number of nodes that match the given xpath expression in the given document.
     * 
     * @param document
     *            xml document to search
     * @param xpath
     *            xpath expression
     * @return number of matching nodes
     */
    private int countNodes(Document document, String xpath) {
        return xpath(document, xpath).getLength();
    }
}
