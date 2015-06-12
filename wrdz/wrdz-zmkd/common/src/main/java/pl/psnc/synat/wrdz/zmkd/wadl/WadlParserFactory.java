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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;

/**
 * Factory class of WADL parsers.
 */
public final class WadlParserFactory {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(WadlParserFactory.class);

    /**
     * The only one instance of the factory.
     */
    private static WadlParserFactory instance = new WadlParserFactory();

    /**
     * JAXB context for the WADL classes.
     */
    private JAXBContext jaxbContext;


    /**
     * Private constructor.
     */
    private WadlParserFactory() {
        try {
            jaxbContext = JAXBContext.newInstance("net.java.dev.wadl._2009._02");
        } catch (JAXBException e) {
            logger.error("JAXB context for WADL creation failed.", e);
            throw new WrdzRuntimeException(e);
        }
    }


    /**
     * Returns the only one instance of this factory.
     * 
     * @return factory
     */
    public static WadlParserFactory getInstance() {
        return instance;
    }


    /**
     * Gets a new WADL parser.
     * 
     * @return new WADL parser
     */
    public WadlParser getWadlParser() {
        return new WadlParser(jaxbContext);
    }

}
