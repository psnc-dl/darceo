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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory of metadata reader in the METS schema.
 */
public final class MetsMetadataReaderFactory {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(MetsMetadataReaderFactory.class);

    /**
     * The only one instance of the factory.
     */
    private static MetsMetadataReaderFactory instance = new MetsMetadataReaderFactory();

    /**
     * JAXB context for the mets classes.
     */
    private final JAXBContext jaxbContext;


    /**
     * Private constructor.
     */
    private MetsMetadataReaderFactory() {
        try {
            jaxbContext = JAXBContext.newInstance("gov.loc.mets:info.lc.xmlns.premis_v2");
        } catch (JAXBException e) {
            logger.error("JAXB context for METS creation failed.", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Returns the only one instance of this factory.
     * 
     * @return factory
     */
    public static MetsMetadataReaderFactory getInstance() {
        return instance;
    }


    /**
     * Gets a new METS (and PREMIS) metadata reader.
     * 
     * @return new METS (and PREMIS) metadata reader
     */
    public MetsMetadataReader getMetsMetadataReader() {
        return new MetsMetadataReader(jaxbContext);
    }

}
