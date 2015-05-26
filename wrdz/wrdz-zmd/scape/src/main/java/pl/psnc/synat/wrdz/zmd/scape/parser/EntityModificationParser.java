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
package pl.psnc.synat.wrdz.zmd.scape.parser;

import gov.loc.mets.AmdSecType;
import gov.loc.mets.FileType;
import gov.loc.mets.MdSecType;
import gov.loc.mets.MdSecType.MdRef;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataProcessingException;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataReader;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataReaderFactory;
import pl.psnc.synat.wrdz.common.utility.UuidGenerator;
import pl.psnc.synat.wrdz.zmd.input.IncompleteDataException;
import pl.psnc.synat.wrdz.zmd.input.InvalidDataException;
import pl.psnc.synat.wrdz.zmd.input.ObjectModificationRequestBuilder;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;
import pl.psnc.synat.wrdz.zmd.object.parser.ObjectManagerRequestHelper;

/**
 * Parser of modification entity requests. It parses requests encoded by application/xml (in METS format).
 */
public class EntityModificationParser {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(EntityModificationParser.class);

    /**
     * Root folder for saving files provided in the request.
     */
    private String root;

    /**
     * Set of AdmSecIDs extracted from entity files.
     */
    private Set<String> filesAdmSecIDs;

    /**
     * UID generator utility.
     */
    private UuidGenerator uuidGenerator;


    /**
     * Constructs parser.
     * 
     * @param root
     *            root folder
     */
    public EntityModificationParser(String root) {
        this.root = root;
        this.filesAdmSecIDs = new HashSet<String>();
        this.uuidGenerator = new UuidGenerator();
    }


    /**
     * Parses the request encoded by application/xml.
     * 
     * @param payload
     *            request containing mets content
     * @param identifier
     *            the id of modified entity
     * @return internal representation of the request
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    public ObjectModificationRequest parse(InputStream payload, String identifier)
            throws IncompleteDataException, InvalidDataException {
        ObjectModificationRequestBuilder requestBuilder = new ObjectModificationRequestBuilder(identifier);

        MetsMetadataReader metsReader = MetsMetadataReaderFactory.getInstance().getMetsMetadataReader();
        try {
            metsReader.parse(payload);
        } catch (MetsMetadataProcessingException e) {
            logger.error("[METS parsing error!] " + e.toString());
            throw new InvalidDataException("[METS parsing error!] " + e.getMessage());
        }
        extractAllFilesFromFileSec(requestBuilder, metsReader);

        String cacheDir = uuidGenerator.generateCacheFolderName() + "/";
        cacheDir = root + cacheDir;
        String fileName = uuidGenerator.generateRandomFileName() + ".xml";
        String fullPath = cacheDir + fileName;
        File tmpDir = new File(cacheDir);
        tmpDir.mkdir();
        try {
            metsReader.writeAsFile(fullPath);
            URI metsTmpUri = URI.create(ObjectManagerRequestHelper.makeUri(cacheDir, fileName));
            requestBuilder.setProvidedMetsURI(metsTmpUri);
        } catch (MetsMetadataProcessingException e) {
            tmpDir.delete();
            logger.error("[Temp file for METS provided - writing error!] " + e.toString());
            throw new InvalidDataException("[Temp file for METS provided - writing error!] " + e.getMessage());
        }
        requestBuilder.setDeleteAllContent(true);
        return requestBuilder.build();
    }


    /**
     * Parses the request encoded by application/xml.
     * 
     * @param payload
     *            request containing mets content
     * @param identifier
     *            the id of modified entity
     * @param metadataId
     *            the id of modified metadata section
     * @return internal representation of the request
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    public ObjectModificationRequest parse(InputStream payload, String identifier, String metadataId)
            throws IncompleteDataException, InvalidDataException {
        ObjectModificationRequestBuilder requestBuilder = new ObjectModificationRequestBuilder(identifier);

        MetsMetadataReader metsReader = MetsMetadataReaderFactory.getInstance().getMetsMetadataReader();
        try {
            metsReader.parse(payload);
        } catch (MetsMetadataProcessingException e) {
            logger.error("[METS parsing error!] " + e.toString());
            throw new InvalidDataException("[METS parsing error!] " + e.getMessage());
        }
        extractAllFilesFromFileSec(requestBuilder, metsReader, metadataId);

        String cacheDir = uuidGenerator.generateCacheFolderName() + "/";
        cacheDir = root + cacheDir;
        String fileName = uuidGenerator.generateRandomFileName() + ".xml";
        String fullPath = cacheDir + fileName;
        File tmpDir = new File(cacheDir);
        tmpDir.mkdir();
        try {
            metsReader.writeAsFile(fullPath);
            URI metsTmpUri = URI.create(ObjectManagerRequestHelper.makeUri(cacheDir, fileName));
            requestBuilder.setProvidedMetsURI(metsTmpUri);
        } catch (MetsMetadataProcessingException e) {
            tmpDir.delete();
            logger.error("[Temp file for METS provided - writing error!] " + e.toString());
            throw new InvalidDataException("[Temp file for METS provided - writing error!] " + e.getMessage());
        }
        requestBuilder.setDeleteAllContent(false);
        return requestBuilder.build();
    }


    /**
     * Extracts all metadata references for given file AdmSec.
     * 
     * @param requestBuilder
     *            builder
     * @param fileId
     *            file id
     * @param admSec
     *            admSec for extraction
     */
    private void extractMetadataFromFileAdmSec(ObjectModificationRequestBuilder requestBuilder, String fileId,
            AmdSecType admSec) {
        extractMetadataFromFileAdmSec(requestBuilder, fileId, admSec, false, null);
    }


    /**
     * Extracts all metadata references for given file AdmSec.
     * 
     * @param requestBuilder
     *            builder
     * @param fileId
     *            file id
     * @param admSec
     *            admSec for extraction
     * @param search
     *            determines if the method is search for only one selected metadata section
     * @param mid
     *            id of modified metadata section
     * @return true if section was found
     */
    private boolean extractMetadataFromFileAdmSec(ObjectModificationRequestBuilder requestBuilder, String fileId,
            AmdSecType admSec, boolean search, String mid) {
        List<MdSecType> rightsMD = admSec.getRightsMD();
        if (rightsMD != null) {
            for (MdSecType mdSecType : rightsMD) {
                MdRef mdRef = mdSecType.getMdRef();
                if (mdRef != null) {
                    if (mid != null) {
                        if (search) {
                            if (mid.equals(mdSecType.getID())) {
                                requestBuilder.addInputFileToModifyMetadataFileToModify(fileId, mdRef.getHref());
                                return true;
                            } else {
                                continue;
                            }
                        } else {
                            requestBuilder.addInputFileToModifyMetadataFileToModify(fileId, mdRef.getHref());
                        }
                    } else {
                        requestBuilder.addInputFileToAddMetadataFile(fileId, mdRef.getHref());
                    }
                }
            }
        }

        List<MdSecType> digiprovMD = admSec.getDigiprovMD();
        if (digiprovMD != null) {
            for (MdSecType mdSecType : digiprovMD) {
                MdRef mdRef = mdSecType.getMdRef();
                if (mdRef != null) {
                    if (mid != null) {
                        if (search) {
                            if (mid.equals(mdSecType.getID())) {
                                requestBuilder.addInputFileToModifyMetadataFileToModify(fileId, mdRef.getHref());
                                return true;
                            } else {
                                continue;
                            }
                        } else {
                            requestBuilder.addInputFileToModifyMetadataFileToModify(fileId, mdRef.getHref());
                        }
                    } else {
                        requestBuilder.addInputFileToAddMetadataFile(fileId, mdRef.getHref());
                    }
                }
            }
        }

        List<MdSecType> sourceMD = admSec.getSourceMD();
        if (sourceMD != null) {
            for (MdSecType mdSecType : sourceMD) {
                MdRef mdRef = mdSecType.getMdRef();
                if (mdRef != null) {
                    if (mid != null) {
                        if (search) {
                            if (mid.equals(mdSecType.getID())) {
                                requestBuilder.addInputFileToModifyMetadataFileToModify(fileId, mdRef.getHref());
                                return true;
                            } else {
                                continue;
                            }
                        } else {
                            requestBuilder.addInputFileToModifyMetadataFileToModify(fileId, mdRef.getHref());
                        }
                    } else {
                        requestBuilder.addInputFileToAddMetadataFile(fileId, mdRef.getHref());
                    }
                }
            }
        }

        List<MdSecType> techMD = admSec.getTechMD();
        if (techMD != null) {
            for (MdSecType mdSecType : techMD) {
                MdRef mdRef = mdSecType.getMdRef();
                if (mdRef != null) {
                    if (mid != null) {
                        if (search) {
                            if (mid.equals(mdSecType.getID())) {
                                requestBuilder.addInputFileToModifyMetadataFileToModify(fileId, mdRef.getHref());
                                return true;
                            } else {
                                continue;
                            }
                        } else {
                            requestBuilder.addInputFileToModifyMetadataFileToModify(fileId, mdRef.getHref());
                        }
                    } else {
                        requestBuilder.addInputFileToAddMetadataFile(fileId, mdRef.getHref());
                    }
                }
            }
        }

        return false;
    }


    /**
     * Extracts all metadata references for given file AdmSec.
     * 
     * @param requestBuilder
     *            builder
     * @param admSec
     *            admSec to extract
     */
    private void extractEntityMetadataAdmSec(ObjectModificationRequestBuilder requestBuilder, AmdSecType admSec) {
        extractEntityMetadataAdmSec(requestBuilder, admSec, false, null);
    }


    /**
     * Extracts all metadata references for given file AdmSec.
     * 
     * @param requestBuilder
     *            builder
     * @param admSec
     *            admSec to extract
     * @param search
     *            determines if the method is search for only one selected metadata section
     * @param mid
     *            id of modified metadata section
     * @return true if section was found
     */
    private boolean extractEntityMetadataAdmSec(ObjectModificationRequestBuilder requestBuilder, AmdSecType admSec,
            boolean search, String mid) {

        List<MdSecType> rightsMD = admSec.getRightsMD();
        if (rightsMD != null) {
            for (MdSecType mdSecType : rightsMD) {
                MdRef mdRef = mdSecType.getMdRef();
                if (mdRef != null) {
                    if (mid != null) {
                        if (search) {
                            if (mid.equals(mdSecType.getID())) {
                                requestBuilder.addObjectMetadataToModify(mdRef.getHref());
                                return true;
                            } else {
                                continue;
                            }
                        } else {
                            requestBuilder.addObjectMetadataToModify(mdRef.getHref());
                        }
                    } else {
                        requestBuilder.addObjectMetadataToAdd(mdRef.getHref());
                    }
                }
            }
        }

        List<MdSecType> digiprovMD = admSec.getDigiprovMD();
        if (digiprovMD != null) {
            for (MdSecType mdSecType : digiprovMD) {
                MdRef mdRef = mdSecType.getMdRef();
                if (mdRef != null) {
                    if (mid != null) {
                        if (search) {
                            if (mid.equals(mdSecType.getID())) {
                                requestBuilder.addObjectMetadataToModify(mdRef.getHref());
                                return true;
                            } else {
                                continue;
                            }
                        } else {
                            requestBuilder.addObjectMetadataToModify(mdRef.getHref());
                        }
                    } else {
                        requestBuilder.addObjectMetadataToAdd(mdRef.getHref());
                    }
                }
            }
        }

        List<MdSecType> sourceMD = admSec.getSourceMD();
        if (sourceMD != null) {
            for (MdSecType mdSecType : sourceMD) {
                MdRef mdRef = mdSecType.getMdRef();
                if (mdRef != null) {
                    if (mid != null) {
                        if (search) {
                            if (mid.equals(mdSecType.getID())) {
                                requestBuilder.addObjectMetadataToModify(mdRef.getHref());
                                return true;
                            } else {
                                continue;
                            }
                        } else {
                            requestBuilder.addObjectMetadataToModify(mdRef.getHref());
                        }
                    } else {
                        requestBuilder.addObjectMetadataToAdd(mdRef.getHref());
                    }
                }
            }
        }

        List<MdSecType> techMD = admSec.getTechMD();
        if (techMD != null) {
            for (MdSecType mdSecType : techMD) {
                MdRef mdRef = mdSecType.getMdRef();
                if (mdRef != null) {
                    if (mid != null) {
                        if (search) {
                            if (mid.equals(mdSecType.getID())) {
                                requestBuilder.addObjectMetadataToModify(mdRef.getHref());
                                return true;
                            } else {
                                continue;
                            }
                        } else {
                            requestBuilder.addObjectMetadataToModify(mdRef.getHref());
                        }
                    } else {
                        requestBuilder.addObjectMetadataToAdd(mdRef.getHref());
                    }
                }
            }
        }
        return false;
    }


    /**
     * Extracts info about all files included into IntelectualEntity (DigitalObject).
     * 
     * @param requestBuilder
     *            builder
     * @param metsReader
     *            mets reader
     */
    private void extractAllFilesFromFileSec(ObjectModificationRequestBuilder requestBuilder,
            MetsMetadataReader metsReader) {
        extractAllFilesFromFileSec(requestBuilder, metsReader, null);
    }


    /**
     * Extracts info about all files included into IntelectualEntity (DigitalObject).
     * 
     * @param requestBuilder
     *            builder
     * @param metsReader
     *            mets reader
     */
    private void extractAllFilesFromFileSec(ObjectModificationRequestBuilder requestBuilder,
            MetsMetadataReader metsReader, String mid) {
        boolean search = (mid != null);
        boolean found = false;
        List<FileType> allFilesFromFileSec = metsReader.getAllFilesFromFileSec();
        for (FileType f : allFilesFromFileSec) {
            requestBuilder.setInputFileToAddSource(f.getID(), f.getFLocat().get(0).getHref());

            String fileId = f.getID();
            List<Object> admIdList = f.getADMID();
            for (Object a : admIdList) {
                if (a instanceof MdSecType) {
                    MdSecType mdSec = (MdSecType) a;
                    MdRef mdRef = mdSec.getMdRef();
                    filesAdmSecIDs.add(mdSec.getID());

                    if (search) {
                        found = mid.equals(mdSec.getID());
                        if (found) {
                            requestBuilder.addInputFileToModifyMetadataFileToModify(fileId, mdRef.getHref());
                            return;
                        }
                    } else {
                        if (mdRef != null) {
                            requestBuilder.addInputFileToAddMetadataFile(fileId, mdRef.getHref());
                        }
                    }
                }
                if (a instanceof AmdSecType) {
                    AmdSecType amdSec = (AmdSecType) a;
                    if ((amdSec == null) || (amdSec.getID() == null)) {
                        continue;
                    }

                    if (search) {
                        found = mid.equals(amdSec.getID());
                        if (found) {
                            search = extractMetadataFromFileAdmSec(requestBuilder, f.getID(), amdSec, false, mid);
                            return;
                        }

                    } else {
                        extractMetadataFromFileAdmSec(requestBuilder, f.getID(), amdSec);
                    }
                    filesAdmSecIDs.add(amdSec.getID());
                }
            }

            List<Object> dmdIdList = f.getDMDID();
            for (Object a : dmdIdList) {
                if (a instanceof MdSecType) {
                    MdSecType mdSec = (MdSecType) a;
                    MdRef mdRef = mdSec.getMdRef();
                    filesAdmSecIDs.add(mdSec.getID());
                    if (search) {
                        found = mid.equals(mdSec.getID());
                        if (found) {
                            requestBuilder.addInputFileToModifyMetadataFileToModify(fileId, mdRef.getHref());
                            return;
                        }
                    } else {
                        if (mdRef != null) {
                            requestBuilder.addInputFileToAddMetadataFile(fileId, mdRef.getHref());
                        }
                    }
                }
                if (a instanceof AmdSecType) {
                    AmdSecType amdSec = (AmdSecType) a;
                    if ((amdSec == null) || (amdSec.getID() == null)) {
                        continue;
                    }
                    if (search) {
                        found = mid.equals(amdSec.getID());
                        if (found) {
                            search = extractMetadataFromFileAdmSec(requestBuilder, f.getID(), amdSec, false, mid);
                            return;
                        }

                    } else {
                        extractMetadataFromFileAdmSec(requestBuilder, f.getID(), amdSec);
                    }
                    filesAdmSecIDs.add(amdSec.getID());
                }
            }
        }
        List<AmdSecType> allAdmSec = metsReader.getAllAdmSec();
        for (AmdSecType admSec : allAdmSec) {
            if (!filesAdmSecIDs.contains(admSec.getID())) {
                if (search) {
                    found = mid.equals(admSec.getID());
                    if (found) {
                        search = extractEntityMetadataAdmSec(requestBuilder, admSec, false, mid);
                        return;
                    }

                } else {
                    extractEntityMetadataAdmSec(requestBuilder, admSec);
                }
            }
        }
    }
}
