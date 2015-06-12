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
package pl.psnc.synat.wrdz.zmkd.wadl;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.java.dev.wadl._2009._02.Application;

/**
 * JAXB parser of WADL XML files.
 */
public class WadlParser {

    /**
     * Unmarshaller of the WADL XML data.
     */
    private final Unmarshaller unmarshaller;


    /**
     * Default constructor.
     * 
     * @param jaxbContext
     *            JAXB context object
     */
    WadlParser(JAXBContext jaxbContext) {
        try {
            this.unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException ex) {
            throw new RuntimeException("Unable to create JAXB unmarshaler!");
        }
    }


    /**
     * Unmarshalls specified input stream into tree of WADL data.
     * 
     * @param is
     *            stream of xml data
     * @return WADL tree root element
     * @throws JAXBException
     *             in case when unmarshaller error occurs
     */
    public Application unmarshalWadl(InputStream is)
            throws JAXBException {
        Application root = (Application) unmarshaller.unmarshal(is);
        return root;
    }

}
