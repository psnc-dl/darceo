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
import pl.psnc.synat.wrdz.zmd.input.ObjectCreationRequestBuilder;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;
import pl.psnc.synat.wrdz.zmd.object.parser.ObjectManagerRequestHelper;

/**
 * Parser of creation object requests. It parses requests encoded by multipart/form-data,
 * application/x-www-form-urlencoded, and data provided in zip.
 */
public class EntityCreationParser {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(EntityCreationParser.class);

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
    public EntityCreationParser(String root) {
        this.root = root;
        this.filesAdmSecIDs = new HashSet<String>();
        this.uuidGenerator = new UuidGenerator();
    }


    /**
     * Extracts all metadata references for given file AdmSec.
     * 
     * @param requestBuilder
     *            builder
     * @param fileId
     *            file identificator
     * @param admSec
     *            amdSec for given file
     */
    private void extractMetadataFromFileAdmSec(ObjectCreationRequestBuilder requestBuilder, String fileId,
            AmdSecType admSec) {
        List<MdSecType> rightsMD = admSec.getRightsMD();
        if (rightsMD != null) {
            for (MdSecType mdSecType : rightsMD) {
                MdRef mdRef = mdSecType.getMdRef();
                if (mdRef != null) {
                    requestBuilder.addInputFileMetadataFile(fileId, mdRef.getHref());
                }
            }
        }

        List<MdSecType> digiprovMD = admSec.getDigiprovMD();
        if (digiprovMD != null) {
            for (MdSecType mdSecType : digiprovMD) {
                MdRef mdRef = mdSecType.getMdRef();
                if (mdRef != null) {
                    requestBuilder.addInputFileMetadataFile(fileId, mdRef.getHref());
                }
            }
        }

        List<MdSecType> sourceMD = admSec.getSourceMD();
        if (sourceMD != null) {
            for (MdSecType mdSecType : sourceMD) {
                MdRef mdRef = mdSecType.getMdRef();
                if (mdRef != null) {
                    requestBuilder.addInputFileMetadataFile(fileId, mdRef.getHref());
                }
            }
        }

        List<MdSecType> techMD = admSec.getTechMD();
        if (techMD != null) {
            for (MdSecType mdSecType : techMD) {
                MdRef mdRef = mdSecType.getMdRef();
                if (mdRef != null) {
                    requestBuilder.addInputFileMetadataFile(fileId, mdRef.getHref());
                }
            }
        }
    }


    /**
     * Extracts all metadata references for given file AdmSec.
     * 
     * @param requestBuilder
     *            builder
     * @param admSec
     *            file admSec
     */
    private void extractEntityMetadataAdmSec(ObjectCreationRequestBuilder requestBuilder, AmdSecType admSec) {

        List<MdSecType> rightsMD = admSec.getRightsMD();
        if (rightsMD != null) {
            for (MdSecType mdSecType : rightsMD) {
                MdRef mdRef = mdSecType.getMdRef();
                if (mdRef != null) {
                    requestBuilder.addObjectMetadata(mdRef.getHref());
                }
            }
        }

        List<MdSecType> digiprovMD = admSec.getDigiprovMD();
        if (digiprovMD != null) {
            for (MdSecType mdSecType : digiprovMD) {
                MdRef mdRef = mdSecType.getMdRef();
                if (mdRef != null) {
                    requestBuilder.addObjectMetadata(mdRef.getHref());
                }
            }
        }

        List<MdSecType> sourceMD = admSec.getSourceMD();
        if (sourceMD != null) {
            for (MdSecType mdSecType : sourceMD) {
                MdRef mdRef = mdSecType.getMdRef();
                if (mdRef != null) {
                    requestBuilder.addObjectMetadata(mdRef.getHref());
                }
            }
        }

        List<MdSecType> techMD = admSec.getTechMD();
        if (techMD != null) {
            for (MdSecType mdSecType : techMD) {
                MdRef mdRef = mdSecType.getMdRef();
                if (mdRef != null) {
                    requestBuilder.addObjectMetadata(mdRef.getHref());
                }
            }
        }

    }


    /**
     * Extracts info about all files included into IntelectualEntity (DigitalObject).
     * 
     * @param requestBuilder
     *            builder
     * @param metsReader
     *            mets reader
     */
    private void extractAllFilesFromFileSec(ObjectCreationRequestBuilder requestBuilder, MetsMetadataReader metsReader) {
        List<FileType> allFilesFromFileSec = metsReader.getAllFilesFromFileSec();
        for (FileType f : allFilesFromFileSec) {
            requestBuilder.setInputFileSource(f.getID(), f.getFLocat().get(0).getHref());

            String fileId = f.getID();
            List<Object> admIdList = f.getADMID();
            for (Object a : admIdList) {
                if (a instanceof MdSecType) {
                    MdSecType mdSec = (MdSecType) a;
                    MdRef mdRef = mdSec.getMdRef();
                    filesAdmSecIDs.add(mdSec.getID());
                    if (mdRef != null) {
                        requestBuilder.addInputFileMetadataFile(fileId, mdRef.getHref());
                    }
                }
                if (a instanceof AmdSecType) {
                    AmdSecType amdSec = (AmdSecType) a;
                    extractMetadataFromFileAdmSec(requestBuilder, f.getID(), amdSec);
                    filesAdmSecIDs.add(amdSec.getID());
                }
            }

            List<Object> dmdIdList = f.getDMDID();
            for (Object a : dmdIdList) {
                if (a instanceof MdSecType) {
                    MdSecType mdSec = (MdSecType) a;
                    MdRef mdRef = mdSec.getMdRef();
                    filesAdmSecIDs.add(mdSec.getID());
                    if (mdRef != null) {
                        requestBuilder.addInputFileMetadataFile(fileId, mdRef.getHref());
                    }
                }
                if (a instanceof AmdSecType) {
                    AmdSecType amdSec = (AmdSecType) a;
                    extractMetadataFromFileAdmSec(requestBuilder, f.getID(), amdSec);
                    filesAdmSecIDs.add(amdSec.getID());
                }
            }
        }
        List<AmdSecType> allAdmSec = metsReader.getAllAdmSec();
        for (AmdSecType admSec : allAdmSec) {
            if (!filesAdmSecIDs.contains(admSec.getID())) {
                extractEntityMetadataAdmSec(requestBuilder, admSec);
            }
        }
    }


    /**
     * Parses the request encoded by multipart/form-data.
     * 
     * @param payload
     *            request
     * @return internal representation of the request
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    public ObjectCreationRequest parse(InputStream payload)
            throws IncompleteDataException, InvalidDataException {
        ObjectCreationRequestBuilder requestBuilder = new ObjectCreationRequestBuilder();
        MetsMetadataReader metsReader = MetsMetadataReaderFactory.getInstance().getMetsMetadataReader();
        try {
            metsReader.parse(payload);
        } catch (MetsMetadataProcessingException e) {
            logger.error("[METS parsing error!] " + e.toString());
            throw new InvalidDataException("[METS parsing error!] " + e.getMessage());
        }
        requestBuilder.setProposedId(metsReader.getObjectIdentifier());
        if (metsReader.getObjectTypeId() != null) {
            requestBuilder.setObjectType(metsReader.getObjectTypeId());
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

        return requestBuilder.build();
    }

}
