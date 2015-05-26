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

import org.apache.log4j.Logger;

/**
 * Factory class of mets metadata parser implementation.
 */
public final class AdmMetadataParserFactory {

    /**
     * Logger instance.
     */
    private static final Logger logger = Logger.getLogger(AdmMetadataParserFactory.class);


    /**
     * Singleton instance holder.
     */
    private static class AdmMetadataParserFactoryHolder {

        /**
         * Singleton instance.
         */
        private static final AdmMetadataParserFactory INSTANCE = new AdmMetadataParserFactory();
    }


    /**
     * JAXBContext instance.
     */
    private final JAXBContext context;


    /**
     * Private constructor.
     */
    private AdmMetadataParserFactory() {
        try {
            context = JAXBContext.newInstance("gov.loc.mets:info.lc.xmlns.premis_v2");
        } catch (Exception ex) {
            logger.error("An error occured while creating JAXBContext element.", ex);
            throw new RuntimeException(ex);
        }
    }


    public static AdmMetadataParserFactory getInstance() {
        return AdmMetadataParserFactoryHolder.INSTANCE;
    }


    /**
     * Returns instance of mets metadata parser.
     * 
     * @return instance of mets parser.
     */
    public AdmMetadataParser getMetsParserInstance() {
        return new DefaultAdmMetadataParser(context);
    }
}
