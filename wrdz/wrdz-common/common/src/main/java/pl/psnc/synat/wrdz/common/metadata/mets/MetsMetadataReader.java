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

import gov.loc.mets.AmdSecType;
import gov.loc.mets.FileType;
import gov.loc.mets.FileType.FLocat;
import gov.loc.mets.MdSecType;
import gov.loc.mets.Mets;
import gov.loc.mets.MetsType.FileSec.FileGrp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reader of METS metadata of a digital object.
 */
public class MetsMetadataReader {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(MetsMetadataReader.class);

    /**
     * Unmarshaller of the METS metadata.
     */
    private final Unmarshaller unmarshaller;

    /**
     * Marshaller of the METS metadata.
     */
    private final Marshaller marshaller;

    /**
     * Mets root object.
     */
    private Mets mets;


    /**
     * Constructs reader of METS metadata.
     * 
     * @param jaxbContext
     *            JAXB context for the METS classes
     */
    public MetsMetadataReader(JAXBContext jaxbContext) {
        try {
            this.unmarshaller = jaxbContext.createUnmarshaller();
            this.marshaller = jaxbContext.createMarshaller();
        } catch (JAXBException e) {
            logger.error("JAXB - METS unmarshaller creation failed.", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Parses METS metadata and returns this reader with loaded metadata.
     * 
     * @param metsMetadata
     *            METS metadata
     * @return this reader
     * @throws MetsMetadataProcessingException
     *             when some error occurs while unmarshalling
     */
    public MetsMetadataReader parse(String metsMetadata)
            throws MetsMetadataProcessingException {
        try {
            mets = (Mets) unmarshaller.unmarshal(new StringReader(metsMetadata));
        } catch (Exception e) {
            throw new MetsMetadataProcessingException(e);
        }
        return this;
    }


    /**
     * Parses METS metadata and returns this reader with loaded metadata.
     * 
     * @param metsMetadata
     *            METS metadata
     * @return this reader
     * @throws MetsMetadataProcessingException
     *             when some error occurs while unmarshalling
     */
    public MetsMetadataReader parse(InputStream metsMetadata)
            throws MetsMetadataProcessingException {
        try {
            mets = (Mets) unmarshaller.unmarshal(metsMetadata);
        } catch (Exception e) {
            logger.error("Problem with reading previuos version of METS metadata." + e.toString());
            throw new MetsMetadataProcessingException(e);
        } finally {
            try {
                metsMetadata.close();
            } catch (IOException e) {
                logger.error("Problem with reading previuos version of METS metadata." + e.toString());
                throw new MetsMetadataProcessingException(e);
            }
        }

        return this;
    }


    /**
     * Parses METS metadata and returns this reader with loaded metadata.
     * 
     * @param metsMetadataFile
     *            file with METS metadata
     * @return this reader
     * @throws MetsMetadataProcessingException
     *             when some error occurs while unmarshalling
     */
    public MetsMetadataReader parse(File metsMetadataFile)
            throws MetsMetadataProcessingException {
        try {
            parse(new FileInputStream(metsMetadataFile));
            //mets = (Mets) unmarshaller.unmarshal(metsMetadataFile);
        } catch (Exception e) {
            throw new MetsMetadataProcessingException(e);
        }
        return this;
    }


    /**
     * Write METS metadata into file.
     * 
     * @param fileFullName
     *            full path + name of METS metadata file
     * @return this reader
     * @throws MetsMetadataProcessingException
     *             when some error occurs while unmarshalling
     */
    public MetsMetadataReader writeAsFile(String fileFullName)
            throws MetsMetadataProcessingException {
        File tmpFile = new File(fileFullName);
        try {
            tmpFile.createNewFile();
        } catch (IOException e1) {
            throw new MetsMetadataProcessingException(e1);
        }
        try {
            marshaller.marshal(mets, tmpFile);
        } catch (Exception e) {
            tmpFile.delete();
            throw new MetsMetadataProcessingException(e);
        }
        return this;
    }


    /**
     * Gets the main identifier of a digital object.
     * 
     * @return object identifier
     */
    public String getObjectIdentifier() {
        return mets.getOBJID();
    }


    /**
     * Gets marshaller for object metadata.
     * 
     * @return object identifier
     */
    public Marshaller getMarshaller() {
        return marshaller;
    }


    /**
     * Gets ObjectType.
     * 
     * @return objectTypeID
     */
    public String getObjectTypeId() {
        return mets.getTYPE();
    }


    /**
     * Gets all admSections.
     * 
     * @return list of AdmSections
     */
    public List<AmdSecType> getAllAdmSec() {
        if (mets.getAmdSec() != null) {
            return mets.getAmdSec();
        } else {
            return new ArrayList<AmdSecType>();
        }
    }


    /**
     * Gets ObjectType.
     * 
     * @param amdId
     *            id of metadata section
     * @return objectTypeID
     */
    public AmdSecType getAdmId(String amdId) {
        Iterator<AmdSecType> it = mets.getAmdSec().iterator();
        while (it.hasNext()) {
            AmdSecType amdSec = (AmdSecType) it.next();
            if (amdSec.getID().equals(amdId)) {
                return amdSec;
            }
        }
        return new AmdSecType();
    }


    /**
     * Gets fileSec.
     * 
     * @return fileSec
     */
    public List<FileType> getAllFilesFromFileSec() {
        List<FileType> allFilesList = new ArrayList<FileType>();
        List<FileGrp> fileGrp = mets.getFileSec().getFileGrp();
        Iterator<FileGrp> it = fileGrp.iterator();
        while (it.hasNext()) {
            FileGrp grp = (FileGrp) it.next();
            List<FileType> fileList = grp.getFile();
            allFilesList.addAll(fileList);
        }
        return allFilesList;
    }


    /**
     * Gets files location map.
     * 
     * @return fileLocMap
     */
    public Map<String, String> getFilesLocationMap() {

        Map<String, String> fileLocMap = new HashMap<String, String>();
        List<FileType> allFilesList = getAllFilesFromFileSec();
        Iterator<FileType> it = allFilesList.iterator();
        while (it.hasNext()) {
            FileType fileType = (FileType) it.next();
            if (fileType.getFLocat().size() > 0) {
                FLocat fLoc = fileType.getFLocat().get(0);
                fileLocMap.put(fileType.getID(), fLoc.getHref());
            }
        }
        return fileLocMap;
    }


    /**
     * Checks if there is any section in METS metadata.
     * 
     * @return if METS metadata are empty
     */
    public boolean isEmpty() {
        return (mets.getMetsHdr() == null && mets.getDmdSec().isEmpty() && mets.getAmdSec().isEmpty()
                && mets.getFileSec() == null && mets.getStructMap().isEmpty() && mets.getStructLink() == null && mets
                .getBehaviorSec().isEmpty());
    }


    /**
     * Gets the mets file.
     * 
     * @return mets
     */
    public Mets getMets() {
        return mets;
    }


    /**
     * Gets selected metadata section from METS.
     * 
     * @throws MetsMetadataProcessingException
     *             exception
     * @param id
     *            metadata section id
     * @return MdSecType section
     * 
     */
    public List<Object> findFileAdmSectionById(String id)
            throws MetsMetadataProcessingException {
        Iterator<AmdSecType> it = mets.getAmdSec().iterator();
        List<Object> result = new LinkedList<Object>();
        while (it.hasNext()) {
            AmdSecType amdSecType = (AmdSecType) it.next();
            if (amdSecType.getID().equals(id)) {
                result.add(amdSecType);
                break;
            }

            Iterator<MdSecType> it1 = amdSecType.getTechMD().iterator();
            while (it1.hasNext()) {
                MdSecType mdSecType = (MdSecType) it1.next();
                if ((mdSecType == null) || (mdSecType.getID() == null)) {
                    continue;
                }
                if (mdSecType.getID().equals(id)) {
                    result.add(mdSecType);
                    break;
                }
            }
            Iterator<MdSecType> it2 = amdSecType.getSourceMD().iterator();
            while (it2.hasNext()) {
                MdSecType mdSecType = (MdSecType) it2.next();
                if ((mdSecType == null) || (mdSecType.getID() == null)) {
                    continue;
                }
                if (mdSecType.getID().equals(id)) {
                    result.add(mdSecType);
                    break;
                }
            }
            Iterator<MdSecType> it3 = amdSecType.getRightsMD().iterator();
            while (it3.hasNext()) {
                MdSecType mdSecType = (MdSecType) it3.next();
                if ((mdSecType == null) || (mdSecType.getID() == null)) {
                    continue;
                }
                if (mdSecType.getID().equals(id)) {
                    result.add(mdSecType);
                    break;
                }
            }
            Iterator<MdSecType> it4 = amdSecType.getDigiprovMD().iterator();
            while (it4.hasNext()) {
                MdSecType mdSecType = (MdSecType) it4.next();
                if ((mdSecType == null) || (mdSecType.getID() == null)) {
                    continue;
                }
                if (mdSecType.getID().equals(id)) {
                    result.add(mdSecType);
                    break;
                }
            }
        }

        return result;
    }


    /**
     * Gets selected metadata section from METS.
     * 
     * @param fid
     *            file id
     * @param mid
     *            metadata section id
     * @return MdSecType section
     * 
     */
    public MdSecType findMetadataSectionByFileIdAndMid(String fid, String mid) {

        List<FileType> allFilesList = getAllFilesFromFileSec();
        List<Object> metadataSections = new LinkedList<Object>();

        Iterator<FileType> it = allFilesList.iterator();
        while (it.hasNext()) {
            FileType fileType = (FileType) it.next();
            if (fileType.getID().equals(fid)) {
                metadataSections.addAll(fileType.getADMID());
                metadataSections.addAll(fileType.getDMDID());
            }
        }

        Iterator<Object> it1 = metadataSections.iterator();

        while (it1.hasNext()) {
            Object o = it1.next();

            AmdSecType amdSecType = (AmdSecType) o;

            Iterator<MdSecType> it2 = amdSecType.getTechMD().iterator();
            while (it2.hasNext()) {
                MdSecType mdSecType = (MdSecType) it2.next();
                if ((mdSecType == null) || (mdSecType.getID() == null)) {
                    continue;
                }
                if (mdSecType.getID().equals(mid)) {
                    return mdSecType;
                }
            }
            Iterator<MdSecType> it3 = amdSecType.getSourceMD().iterator();
            while (it3.hasNext()) {
                MdSecType mdSecType = (MdSecType) it3.next();
                if ((mdSecType == null) || (mdSecType.getID() == null)) {
                    continue;
                }
                if (mdSecType.getID().equals(mid)) {
                    return mdSecType;
                }
            }
            Iterator<MdSecType> it4 = amdSecType.getRightsMD().iterator();
            while (it4.hasNext()) {
                MdSecType mdSecType = (MdSecType) it4.next();
                if ((mdSecType == null) || (mdSecType.getID() == null)) {
                    continue;
                }
                if (mdSecType.getID().equals(mid)) {
                    return mdSecType;
                }
            }
            Iterator<MdSecType> it5 = amdSecType.getDigiprovMD().iterator();
            while (it5.hasNext()) {
                MdSecType mdSecType = (MdSecType) it5.next();
                if ((mdSecType == null) || (mdSecType.getID() == null)) {
                    continue;
                }
                if (mdSecType.getID().equals(mid)) {
                    return mdSecType;
                }
            }
        }

        return null;
    }


    /**
     * Gets selected metadata section from METS.
     * 
     * @param id
     *            id of metadata section
     * @return MdSecType section
     * @throws MetsMetadataProcessingException
     *             exception
     */
    public MdSecType findMetadataSectionById(String id)
            throws MetsMetadataProcessingException {
        Iterator<AmdSecType> it = mets.getAmdSec().iterator();
        while (it.hasNext()) {
            AmdSecType amdSecType = (AmdSecType) it.next();
            if (amdSecType.getID().equals(id)) {
                return null;
            }

            Iterator<MdSecType> it1 = amdSecType.getTechMD().iterator();
            while (it1.hasNext()) {
                MdSecType mdSecType = (MdSecType) it1.next();
                if ((mdSecType == null) || (mdSecType.getID() == null)) {
                    continue;
                }
                if (mdSecType.getID().equals(id)) {
                    return mdSecType;
                }
            }
            Iterator<MdSecType> it2 = amdSecType.getSourceMD().iterator();
            while (it2.hasNext()) {
                MdSecType mdSecType = (MdSecType) it2.next();
                if ((mdSecType == null) || (mdSecType.getID() == null)) {
                    continue;
                }
                if (mdSecType.getID().equals(id)) {
                    return mdSecType;
                }
            }
            Iterator<MdSecType> it3 = amdSecType.getRightsMD().iterator();
            while (it3.hasNext()) {
                MdSecType mdSecType = (MdSecType) it3.next();
                if ((mdSecType == null) || (mdSecType.getID() == null)) {
                    continue;
                }
                if (mdSecType.getID().equals(id)) {
                    return mdSecType;
                }
            }
            Iterator<MdSecType> it4 = amdSecType.getDigiprovMD().iterator();
            while (it4.hasNext()) {
                MdSecType mdSecType = (MdSecType) it4.next();
                if ((mdSecType == null) || (mdSecType.getID() == null)) {
                    continue;
                }
                if (mdSecType.getID().equals(id)) {
                    return mdSecType;
                }
            }
        }

        return null;
    }
}
