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
package pl.psnc.synat.wrdz.common.metadata.xmlns;

import java.io.CharConversionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * XML namespace recognizer.
 */
public class NamespaceRecognizer {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(NamespaceRecognizer.class);

    /**
     * Namespaces' URI that are excluded from recognition.
     */
    public static final List<String> EXCLUDED_URIS = Arrays.asList(new String[] { "http://www.w3.org/1999/xlink",
            "http://www.w3.org/2001/XMLSchema-instance" });

    /**
     * XML parser.
     */
    private XMLReader parser;

    /**
     * Handler of namespaces.
     */
    private NamespaceHandler handler;


    /**
     * Constructor of the namespace recognizer.
     */
    public NamespaceRecognizer() {
        try {
            parser = XMLReaderFactory.createXMLReader();
            parser.setFeature("http://xml.org/sax/features/validation", false);
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (SAXException e) {
            logger.error("XML Reeader creation failed.", e);
            throw new RuntimeException(e);
        }
        handler = new NamespaceHandler();
        parser.setContentHandler(handler);
    }


    /**
     * Recognizes and returns nemaspaces used in the XML file specified by the path.
     * 
     * @param path
     *            path to the XML file.
     * @return list of namespaces
     * @throws NamespaceRecognitionException
     *             when some problem during recognition occurs
     */
    public XmlNamespaces getNamespaces(String path)
            throws NamespaceRecognitionException {
        InputStream in = null;
        try {
            in = new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            logger.error("The specified file does not exist", e);
            throw new NamespaceRecognitionException(e);
        }
        InputSource source = new InputSource(in);
        try {
            parser.parse(source);
        } catch (CharConversionException e) {
            logger.error("Problem with char conversion - probably it is a binary file.");
            logger.error("File: " + path + ". " + e.toString());
            return null;
        } catch (SAXException e) {
            logger.error("Problem with parsing the XML file - probably this file is malformed");
            logger.error("File: " + path + ". " + e.toString());
            return null;
        } catch (IOException e) {
            logger.error("Some IO problem occurs during parsing the XML file", e);
            throw new NamespaceRecognitionException(e);
        }
        try {
            in.close();
        } catch (IOException e) {
            logger.error("Some IO problem occurs during closing the XML file", e);
            throw new NamespaceRecognitionException(e);
        }
        XmlNamespaces xmlNamespaces = new XmlNamespaces();
        List<Namespace> namespaces = new ArrayList<Namespace>();
        for (String uri : handler.getLocations().keySet()) {
            String location = handler.getLocations().get(uri);
            if (location == null) {
                location = DefaultNamespaceDictionary.getInstance().getDefaultSchemaLocation(uri);
            }
            NamespaceType type = DefaultNamespaceDictionary.getInstance().getTypeOfNamespace(uri);
            namespaces.add(new Namespace(uri, location, type));
            if (uri.equals(handler.getMainUri())) {
                type = specializeType(type, handler.getRootElement());
                xmlNamespaces.setMainNamespace(new Namespace(uri, location, type));
            }
        }
        xmlNamespaces.setUsedNamespaces(namespaces);
        return xmlNamespaces;
    }


    /**
     * Specialize a type of metadata based upon the name of the root element.
     * 
     * @param type
     *            recognized general type
     * @param rootElement
     *            root element
     * @return specialized type
     */
    private NamespaceType specializeType(NamespaceType type, String rootElement) {
        if (!type.name().equalsIgnoreCase(rootElement)) {
            if (type.equals(NamespaceType.PREMIS)) {
                if ("object".equalsIgnoreCase(rootElement)) {
                    return NamespaceType.PREMIS_OBJECT;
                } else if ("agent".equalsIgnoreCase(rootElement)) {
                    return NamespaceType.PREMIS_AGENT;
                } else if ("event".equalsIgnoreCase(rootElement)) {
                    return NamespaceType.PREMIS_EVENT;
                } else if ("rights".equalsIgnoreCase(rootElement)) {
                    return NamespaceType.PREMIS_RIGHTS;
                }
            }
        }
        return type;
    }


    /**
     * Handler of the namespaces used by SAX parser.
     */
    private class NamespaceHandler extends DefaultHandler {

        /**
         * Map of URIs and prefixes used in a XML file.
         */
        private Map<String, String> prefixes = new HashMap<String, String>();

        /**
         * Map of URIs and its schema locations.
         */
        private Map<String, String> locations = new HashMap<String, String>();

        /**
         * URI of the root element in a XML file.
         */
        private String mainUri;

        /**
         * Root element of a XML file.
         */
        private String rootElement;

        /**
         * Flag which says whether the first element of a XML file was processed.
         */
        private boolean firstElementProcessed = false;


        @Override
        public void startPrefixMapping(String prefix, String uri)
                throws SAXException {
            logger.debug("prefix: " + prefix);
            logger.debug("uri: " + uri);
            if (!EXCLUDED_URIS.contains(uri)) {
                prefixes.put(uri, prefix);
                locations.put(uri, null);
            }
        }


        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts)
                throws SAXException {
            if (!firstElementProcessed) {
                firstElementProcessed = true;
                rootElement = localName;
                mainUri = uri;
                logger.debug("schemaLocation: "
                        + atts.getValue("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"));
            }
        }


        public Map<String, String> getLocations() {
            return locations;
        }


        public String getMainUri() {
            return mainUri;
        }


        public String getRootElement() {
            return rootElement;
        }

    }

}
