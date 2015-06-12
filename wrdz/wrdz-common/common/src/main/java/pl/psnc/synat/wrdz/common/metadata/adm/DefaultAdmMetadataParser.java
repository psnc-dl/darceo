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
package pl.psnc.synat.wrdz.common.metadata.adm;

import gov.loc.mets.Mets;
import info.lc.xmlns.premis_v2.PremisComplexType;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Basic implementation of mets metadata parser.
 */
class DefaultAdmMetadataParser implements AdmMetadataParser {

    /**
     * Unmarshaller of the METS metadata.
     */
    private final Unmarshaller unmarshaller;


    /**
     * Default constructor.
     * 
     * @param jbcontext
     *            JAXB context object
     */
    DefaultAdmMetadataParser(JAXBContext jbcontext) {
        try {
            this.unmarshaller = jbcontext.createUnmarshaller();
        } catch (JAXBException ex) {
            throw new RuntimeException("Unable to create JAXB unmarshaler!");
        }
    }


    /**
     * Unmarshalls specified input stream into tree of mets metadata.
     * 
     * @param is
     *            stream of xml data
     * @return mets tree root element
     * @throws Exception
     *             in case when unmarshaller error occurs
     */
    public Mets unmarshalMets(InputStream is)
            throws Exception {

        Mets root = (Mets) unmarshaller.unmarshal(is);
        return root;
    }


    /**
     * Unmarshalls specified input stream into tree of premis metadata.
     * 
     * @param is
     *            stream of premis xml
     * @return premis root node
     * @throws Exception
     *             in case when unmarshall operation wont succeed.
     */
    public PremisComplexType unmarshallPremis(InputStream is)
            throws Exception {

        JAXBElement<?> root = (JAXBElement<?>) unmarshaller.unmarshal(is);
        PremisComplexType premisRoot = (PremisComplexType) root.getValue();
        return premisRoot;

    }

}
