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
package pl.psnc.synat.wrdz.zmkd.object;

import gov.loc.mets.AmdSecType;
import gov.loc.mets.FileGrpType;
import gov.loc.mets.FileType;
import gov.loc.mets.MdSecType;
import gov.loc.mets.MdSecType.MdRef;
import gov.loc.mets.Mets;
import info.lc.xmlns.premis_v2.FormatComplexType;
import info.lc.xmlns.premis_v2.FormatRegistryComplexType;
import info.lc.xmlns.premis_v2.PremisComplexType;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBElement;

import org.apache.commons.io.IOUtils;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.common.metadata.adm.AdmMetadataParser;
import pl.psnc.synat.wrdz.common.metadata.adm.AdmMetadataParserFactory;
import pl.psnc.synat.wrdz.common.metadata.adm.PremisMetadataExplorer;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;

/**
 * Retrieves digital object information from its extracted METS and PREMIS files.
 */
@Stateless
@LocalBean
public class MetsReader {

    /** Mets parser instance. */
    private final AdmMetadataParser parser;


    /**
     * Default constructor.
     */
    public MetsReader() {
        parser = AdmMetadataParserFactory.getInstance().getMetsParserInstance();
    }


    /**
     * Parses the METS file denoted by the given path and work directory and returns retrieved digital object
     * information.
     * 
     * @param workDir
     *            the directory in which the object's files reside
     * @param metsPath
     *            path to the extracted METS file (relative to the work directory)
     * @return digital object information
     */
    public DigitalObjectInfo parseMets(File workDir, String metsPath) {

        Mets root = getMetsRoot(workDir, metsPath);

        ObjectType type = getType(root);
        List<DataFileInfo> files = getFiles(workDir, root);

        return new DigitalObjectInfo(workDir, type, files);
    }


    /**
     * Reads the METS file denoted by the given path and work directory and returns its root.
     * 
     * @param workDir
     *            the directory in which the object's files reside
     * @param metsPath
     *            path to the METS file (relative to the work directory)
     * @return METS root
     */
    private Mets getMetsRoot(File workDir, String metsPath) {
        InputStream is = null;
        try {
            File metsFile = new File(workDir, metsPath);
            is = new BufferedInputStream(new FileInputStream(metsFile));
            return parser.unmarshalMets(is);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error occure while retrieving data from METS!", ex);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }


    /**
     * Reads the PREMIS file denoted by the given path and work directory and returns its root.
     * 
     * @param workDir
     *            the directory in which the object's files reside
     * @param premisPath
     *            path to the PREMIS file (relative to the work directory)
     * @return PREMIS root
     */
    private PremisComplexType getPremisRoot(File workDir, String premisPath) {
        File premisFile = new File(workDir, premisPath);

        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(premisFile));
            return parser.unmarshallPremis(is);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while retrieving PREMIS data", e);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }


    /**
     * Extracts the digital object type from the given METS.
     * 
     * @param root
     *            METS root
     * @return digital object type
     */
    private ObjectType getType(Mets root) {
        try {
            return ObjectType.valueOf(root.getTYPE());
        } catch (IllegalArgumentException e) {
            throw new WrdzRuntimeException("Unexpected type: " + root.getTYPE());
        } catch (NullPointerException e) {
            throw new WrdzRuntimeException("Digital object has a null type");
        }
    }


    /**
     * Retrieves data file information from the given METS.
     * 
     * @param workDir
     *            the directory in which the object's files reside
     * @param root
     *            METS root
     * @return data file information
     */
    private List<DataFileInfo> getFiles(File workDir, Mets root) {

        List<DataFileInfo> files = new ArrayList<DataFileInfo>();

        for (FileGrpType group : root.getFileSec().getFileGrp()) {
            for (FileType ftype : group.getFile()) {

                if (!ftype.getFLocat().isEmpty()) {
                    String path = ftype.getFLocat().get(0).getHref();

                    String puid = null;
                    for (Object sec : ftype.getADMID()) {
                        if (sec instanceof AmdSecType) {
                            AmdSecType admSec = (AmdSecType) sec;
                            for (MdSecType secType : admSec.getDigiprovMD()) {
                                MdRef mdRef = secType.getMdRef();
                                if (mdRef.getMDTYPE().equals("PREMIS")) {
                                    PremisComplexType premis = getPremisRoot(workDir, mdRef.getHref());
                                    puid = extractPuid(premis);
                                }
                            }
                        }
                    }

                    Integer seq = ftype.getSEQ();

                    files.add(new DataFileInfo(path, puid, seq, new File(workDir, path)));
                }
            }
        }
        return files;
    }


    /**
     * Extracts the PRONOM file format identifier from the given PREMIS.
     * 
     * @param root
     *            PREMIS root
     * @return extracted PRONOM identifier, or <code>null</code> if it could not be found
     */
    private String extractPuid(PremisComplexType root) {

        PremisMetadataExplorer premisExpl = new PremisMetadataExplorer(root);
        FormatComplexType format = premisExpl.getFormatSection();

        for (JAXBElement<?> jaxbEl : format.getContent()) {

            Object val = jaxbEl.getValue();

            if (val instanceof FormatRegistryComplexType) {
                FormatRegistryComplexType formatComplexType = (FormatRegistryComplexType) val;

                // consider only PRONOM format identifier
                if (formatComplexType.getFormatRegistryName().equals("PRONOM")) {
                    return formatComplexType.getFormatRegistryKey();
                }
            }

        }

        return null;
    }
}
