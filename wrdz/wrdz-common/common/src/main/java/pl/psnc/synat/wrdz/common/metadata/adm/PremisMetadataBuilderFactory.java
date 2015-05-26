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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory of an administrative metadata builder in the PREMIS schema.
 */
public final class PremisMetadataBuilderFactory {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(PremisMetadataBuilderFactory.class);

    /**
     * The only one instance of the factory.
     */
    private static PremisMetadataBuilderFactory instance = new PremisMetadataBuilderFactory();

    /**
     * JAXB context for the premis classes.
     */
    private JAXBContext jaxbContext;


    /**
     * Private constructor.
     */
    private PremisMetadataBuilderFactory() {
        try {
            jaxbContext = JAXBContext.newInstance("info.lc.xmlns.premis_v2");
        } catch (JAXBException e) {
            logger.error("JAXB context for PREMIS creation failed.", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Returns the only one instance of this factory.
     * 
     * @return factory
     */
    public static PremisMetadataBuilderFactory getInstance() {
        return instance;
    }


    /**
     * Gets a new premis metadata builder.
     * 
     * @return new premis metadata builder
     */
    public PremisMetadataBuilder getPremisMetadataBuilder() {
        return new PremisMetadataBuilder(jaxbContext);
    }

}
