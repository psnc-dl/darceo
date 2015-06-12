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
package pl.psnc.synat.wrdz.common.metadata.mets;

import java.net.URI;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory of metadata builder in the METS schema.
 */
public final class MetsMetadataBuilderFactory {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(MetsMetadataBuilderFactory.class);

    /**
     * The only one instance of the factory.
     */
    private static MetsMetadataBuilderFactory instance = new MetsMetadataBuilderFactory();

    /**
     * JAXB context for the mets classes.
     */
    private final JAXBContext jaxbContext;

    /**
     * Date type factory for converting dates into XML Gregorian Calendar instances.
     */
    private final DatatypeFactory datatypeFactory;

    /**
     * Document builder factory for parsing XML to DOM.
     */
    private final DocumentBuilderFactory domBuilderFactory;


    /**
     * Private constructor.
     */
    private MetsMetadataBuilderFactory() {
        try {
            jaxbContext = JAXBContext.newInstance("gov.loc.mets:info.lc.xmlns.premis_v2");
        } catch (JAXBException e) {
            logger.error("JAXB context for METS creation failed.", e);
            throw new RuntimeException(e);
        }
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            logger.error("Problem with creation of DatatypeFactory instance.", e);
            throw new RuntimeException(e);
        }
        domBuilderFactory = DocumentBuilderFactory.newInstance();
        domBuilderFactory.setIgnoringElementContentWhitespace(false);
        domBuilderFactory.setValidating(false);
        domBuilderFactory.setExpandEntityReferences(false);
        domBuilderFactory.setIgnoringComments(false);
        domBuilderFactory.setNamespaceAware(false);
        domBuilderFactory.setXIncludeAware(false);
        domBuilderFactory.setCoalescing(false);
    }


    /**
     * Returns the only one instance of this factory.
     * 
     * @return factory
     */
    public static MetsMetadataBuilderFactory getInstance() {
        return instance;
    }


    /**
     * Gets a new METS (and PREMIS) metadata builder.
     * 
     * @return new METS (and PREMIS) metadata builder
     */
    public MetsMetadataBuilder getMetsMetadataBuilder() {
        return new MetsMetadataBuilder(jaxbContext, datatypeFactory, domBuilderFactory);
    }


    /**
     * Gets a new METS (and PREMIS) metadata builder.
     * 
     * @param tmpMetsFileUri
     *            URI to tmpMets file.
     * @return new METS (and PREMIS) metadata builder
     */
    public MetsMetadataBuilder getMetsMetadataBuilder(URI tmpMetsFileUri) {
        if (tmpMetsFileUri == null) {
            return new MetsMetadataBuilder(jaxbContext, datatypeFactory, domBuilderFactory);
        } else {
            return new MetsMetadataBuilder(jaxbContext, datatypeFactory, domBuilderFactory, tmpMetsFileUri);
        }
    }

}
