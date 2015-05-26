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
package pl.psnc.synat.fits.tech;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.metadata.tech.TechMetadata;
import edu.harvard.hul.ois.fits.exceptions.FitsConfigurationException;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.exceptions.FitsToolException;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContent;

/**
 * Abstract class for any technical metadata builder.
 * 
 */
public abstract class TechMetadataBuilder {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(TechMetadataBuilder.class);

    /**
     * XML content return by FITS. A seed for the construction of technical metadata.
     */
    protected final XmlContent xml;


    /**
     * Constructs technical metadata builder.
     * 
     * @param xml
     *            seed
     */
    public TechMetadataBuilder(XmlContent xml) {
        this.xml = xml;
    }


    /**
     * Constructs metadata XML in some standard schema XML. The method based upon methods provided by FITS.
     * 
     * @return metadata in XML format
     * @throws FitsException
     *             when construction of a XML failed
     */
    protected String constructStandardSchemaXml()
            throws FitsException {
        xml.setRoot(true);
        // create an xml output factory
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        // and pretty print transformer
        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (Exception e) {
            logger.error("error creating a pretty print transformer", e);
            throw new FitsConfigurationException("error creating a pretty print transformer", e);
        }
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        ByteArrayOutputStream xmlOutStream = new ByteArrayOutputStream();
        OutputStream xsltOutStream = new ByteArrayOutputStream();
        try {
            // send standard xml to the output stream
            XMLStreamWriter sw = xmlOutputFactory.createXMLStreamWriter(xmlOutStream);
            xml.output(sw);
            // convert output stream to byte array and read back in as inputstream
            Source source = new StreamSource(new ByteArrayInputStream(xmlOutStream.toByteArray()));
            Result rstream = new StreamResult(xsltOutStream);
            // apply the xslt
            transformer.transform(source, rstream);
            // and return a XML as string
            return xsltOutStream.toString();
        } catch (Exception e) {
            logger.error("error converting output to a standard schema format");
            throw new FitsToolException("error converting output to a standard schema format", e);
        } finally {
            try {
                xmlOutStream.close();
                xsltOutStream.close();
            } catch (IOException e) {
                logger.error("error closing streams", e);
            }
        }
    }


    /**
     * Builds <code>TechMetadata</code> object based upon data returned by FITS.
     * 
     * @return technical matadata object.
     * @throws FitsException
     *             when construction of a XML failed
     */
    public abstract TechMetadata build()
            throws FitsException;

}
