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
package pl.psnc.synat.wrdz.zmkd.format;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.zmkd.dao.format.FileFormatDao;
import pl.psnc.synat.wrdz.zmkd.dao.format.FileFormatFilterFactory;
import pl.psnc.synat.wrdz.zmkd.entity.format.FileFormat;

/**
 * Provides access and basic operation on UDFR file format dictionary copied from UDRF database.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class FileFormatDictionaryBean {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FileFormatDictionaryBean.class);

    /** Data file format DAO. */
    @EJB
    private FileFormatDao fileFormatDao;

    /** Provides access to the UDFR registry. */
    @EJB
    private UdfrSparqlEndpointAccessBean udfrSparqlEndpointAccessBean;


    /**
     * Returns the UDFR file format identifier corresponding to the given PUID. The local dictionary is automatically
     * updated via a SPARQL query to UDFR if such identifier is not present.
     * 
     * @param puid
     *            PRONOM identifier
     * @return UDFR IRI
     * @throws UnrecognizedPuidException
     *             when the given PUID does not exist in UDFR
     * @throws UdfrServiceException
     *             when communication with UDFR fails
     */
    public String getUdfrIri(String puid)
            throws UnrecognizedPuidException, UdfrServiceException {
        return findByPuid(puid).getUdfrIri();
    }


    /**
     * Returns the file format entry that corresponds to the given PUID. The local dictionary is automatically updated
     * via a SPARQL query to UDFR if such identifier is not present.
     * 
     * @param puid
     *            PRONOM identifier
     * @return file format dictionary entry
     * @throws UnrecognizedPuidException
     *             when the given PUID does not exist in UDFR
     * @throws UdfrServiceException
     *             when communication with UDFR fails
     */
    public FileFormat findByPuid(String puid)
            throws UnrecognizedPuidException, UdfrServiceException {
        FileFormatFilterFactory factory = fileFormatDao.createQueryModifier().getQueryFilterFactory();
        FileFormat fileFormat = fileFormatDao.findFirstResultBy(factory.byPuid(puid));

        if (fileFormat != null) {
            logger.debug("UDFR IRI fro PUID " + puid + " exists in the local dictionary: " + fileFormat.getUdfrIri());
            return fileFormat;
        }
        logger.debug("UDFR IRI fro PUID " + puid + " does not exist in the local dictionary");
        String udfrIri = udfrSparqlEndpointAccessBean.getUdfrIriForPuid(puid);
        logger.debug("UDFR IRI fro PUID " + puid + " exists in the UDFR registry: " + udfrIri);
        FileFormatExt fileFormatExt = udfrSparqlEndpointAccessBean.getMimetypeForUdfrIri(udfrIri);
        fileFormat = new FileFormat(puid, udfrIri, fileFormatExt.getExtension(), fileFormatExt.getMimetype());
        fileFormatDao.persist(fileFormat);
        return fileFormat;
    }


    /**
     * Returns the file format entry that corresponds to the given PUID. The local dictionary is automatically updated
     * via a SPARQL query to UDFR if such identifier is not present.
     * 
     * @param udfrIri
     *            UDFR IRI
     * @return file format dictionary entry
     * @throws UnrecognizedIriException
     *             when the given IRI does not exist in UDFR
     * @throws UdfrServiceException
     *             when communication with UDFR fails
     */
    public FileFormat findByIri(String udfrIri)
            throws UnrecognizedIriException, UdfrServiceException {
        FileFormatFilterFactory factory = fileFormatDao.createQueryModifier().getQueryFilterFactory();
        FileFormat fileFormat = fileFormatDao.findFirstResultBy(factory.byUdfrIri(udfrIri));
        if (fileFormat != null) {
            return fileFormat;
        }
        String puid = udfrSparqlEndpointAccessBean.getPuidForUdfrIri(udfrIri);
        FileFormatExt fileFormatExt = udfrSparqlEndpointAccessBean.getMimetypeForUdfrIri(udfrIri);
        fileFormat = new FileFormat(puid, udfrIri, fileFormatExt.getExtension(), fileFormatExt.getMimetype());
        fileFormatDao.persist(fileFormat);
        return fileFormat;
    }
}
