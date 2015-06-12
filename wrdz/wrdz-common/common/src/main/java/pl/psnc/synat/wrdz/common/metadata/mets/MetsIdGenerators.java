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
import gov.loc.mets.MdSecType;
import gov.loc.mets.MdSecType.MdRef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates ids for sections in METS metadata.
 */
public class MetsIdGenerators {

    /**
     * Leading zeros.
     */
    private static final String LEADING_ZEROS = "0000";

    /**
     * Prefix for id of a descriptive section for digital object metadata.
     */
    private static final String OBJECT_DMD_SECTION_ID_PREFIX = "D";

    /**
     * Prefix for id of an administrative section for digital object metadata.
     */
    private static final String OBJECT_ADM_SECTION_ID_PREFIX = "A";

    /**
     * Prefix for id of an administrative section for file metadata.
     */
    private static final String FILE_ADM_SECTION_ID_PREFIX = "F";

    /**
     * Prefix for id of a file group section for digital object data.
     */
    private static final String FILE_GROUP_SECTION_ID_PREFIX = "G";

    /**
     * Prefix for id of a file in a file group section for digital object data.
     */
    private static final String FILE_GROUP_FILE_ID_PREFIX = "F";

    /**
     * Prefix for id of a technical section contained in an administrative section.
     */
    private static final String TECH_ADM_SECTION_ID_PREFIX = "T";

    /**
     * Prefix for id of a source section contained in an administrative section.
     */
    private static final String SOURCE_ADM_SECTION_ID_PREFIX = "S";

    /**
     * Prefix for id of a rights section contained in an administrative section.
     */
    private static final String RIGHTS_ADM_SECTION_ID_PREFIX = "R";

    /**
     * Prefix for id of a digital provenance section contained in an administrative section.
     */
    private static final String DIGIPROV_ADM_SECTION_ID_PREFIX = "P";

    /**
     * Last value for id of a descriptive section for digital object metadata.
     */
    private int odmd;

    /**
     * Last value for id of an administrative section for digital object metadata.
     */
    private int oadm;

    /**
     * Last value for id of an administrative section for file metadata.
     */
    private int fadm;

    /**
     * Last value for id of a file group section for object data files.
     */
    private int fgrp;

    /**
     * Last value for id of a file in a file group section for object data files.
     */
    private int file;

    /**
     * Last value for id of an event in the digital provenance metadata in the PREMIS schema.
     */
    private int pevt;

    /**
     * Map of last values for id of a technical section for an administrative section.
     */
    private Map<String, Integer> tech = new HashMap<String, Integer>();

    /**
     * Map of last values for id of a source section for an administrative section.
     */
    private Map<String, Integer> source = new HashMap<String, Integer>();

    /**
     * Map of last values for id of a rights section for an administrative section.
     */
    private Map<String, Integer> rights = new HashMap<String, Integer>();

    /**
     * Map of last values for id of a digital provenance section for an administrative section.
     */
    private Map<String, Integer> digiprov = new HashMap<String, Integer>();


    /**
     * Default constructor.
     */
    public MetsIdGenerators() {
        odmd = 0;
        oadm = 0;
        fadm = 0;
        fgrp = 0;
        file = 0;
        pevt = 0;
    }


    /**
     * Search for maximal id-s in given admSec and sets proper mappings.
     * 
     * @param admSec
     *            id of administrative section
     **/
    public void fillMaps(AmdSecType admSec) {
        String admId = admSec.getID();
        if (admId == null) {
            return;
        }
        int maxRightsId = 0;
        int maxDigiprovId = 0;
        int maxSourceId = 0;
        int maxTechId = 0;

        if (!rights.containsKey(admId)) {
            List<MdSecType> rightsMD = admSec.getRightsMD();
            if (rightsMD != null) {
                for (MdSecType mdSecType : rightsMD) {
                    MdRef mdRef = mdSecType.getMdRef();
                    if (mdRef != null) {
                        String mdId = mdRef.getID();
                        if (mdId == null) {
                            continue;
                        }
                        if (mdId.startsWith(admId)) {
                            mdId = mdId.replace(admId, "");
                            mdId = mdId.replaceAll("[^\\d]", "");
                            try {
                                int tmpId = Integer.parseInt(mdId);
                                maxRightsId = (tmpId > maxRightsId) ? tmpId : maxRightsId;
                            } catch (NumberFormatException ex) {
                                continue;
                            }
                        }
                    }
                }
            }
            rights.put(admId, maxRightsId);
        }

        if (!digiprov.containsKey(admId)) {
            List<MdSecType> digiprovMD = admSec.getDigiprovMD();
            if (digiprovMD != null) {
                for (MdSecType mdSecType : digiprovMD) {
                    MdRef mdRef = mdSecType.getMdRef();
                    if (mdRef != null) {
                        String mdId = mdRef.getID();
                        if (mdId == null) {
                            continue;
                        }
                        if (mdId.startsWith(admId)) {
                            mdId = mdId.replace(admId, "");
                            mdId = mdId.replaceAll("[^\\d]", "");
                            try {
                                int tmpId = Integer.parseInt(mdId);
                                maxDigiprovId = (tmpId > maxDigiprovId) ? tmpId : maxDigiprovId;
                            } catch (NumberFormatException ex) {
                                continue;
                            }
                        }
                    }
                }
            }
            digiprov.put(admId, maxDigiprovId);
        }

        if (!source.containsKey(admId)) {
            List<MdSecType> sourceMD = admSec.getSourceMD();
            if (sourceMD != null) {
                for (MdSecType mdSecType : sourceMD) {
                    MdRef mdRef = mdSecType.getMdRef();
                    if (mdRef != null) {
                        String mdId = mdRef.getID();
                        if (mdId == null) {
                            continue;
                        }
                        if (mdId.startsWith(admId)) {
                            mdId = mdId.replace(admId, "");
                            mdId = mdId.replaceAll("[^\\d]", "");
                            try {
                                int tmpId = Integer.parseInt(mdId);
                                maxSourceId = (tmpId > maxSourceId) ? tmpId : maxSourceId;
                            } catch (NumberFormatException ex) {
                                continue;
                            }
                        }
                    }
                }
            }
            source.put(admId, maxSourceId);
        }

        if (!tech.containsKey(admId)) {
            List<MdSecType> techMD = admSec.getTechMD();
            if (techMD != null) {
                for (MdSecType mdSecType : techMD) {
                    MdRef mdRef = mdSecType.getMdRef();
                    if (mdRef != null) {
                        String mdId = mdRef.getID();
                        if (mdId == null) {
                            continue;
                        }
                        if (mdId.startsWith(admId)) {
                            mdId = mdId.replace(admId, "");
                            mdId = mdId.replaceAll("[^\\d]", "");
                            try {
                                int tmpId = Integer.parseInt(mdId);
                                maxTechId = (tmpId > maxTechId) ? tmpId : maxTechId;
                            } catch (NumberFormatException ex) {
                                continue;
                            }
                        }
                    }
                }
            }
            tech.put(admId, maxTechId);
        }
    }


    /**
     * Returns non-repeatable id for a descriptive section of digital object metadata.
     * 
     * @return id for a descriptive section.
     */
    String getObjectDmdId() {
        return createId(OBJECT_DMD_SECTION_ID_PREFIX, ++odmd, 4);
    }


    /**
     * Returns non-repeatable id for a administrative section of digital object metadata.
     * 
     * @return id for a descriptive section.
     */
    String getObjectAdmId() {
        String admId = createId(OBJECT_ADM_SECTION_ID_PREFIX, ++oadm, 4);
        tech.put(admId, 0);
        source.put(admId, 0);
        rights.put(admId, 0);
        digiprov.put(admId, 0);
        return admId;
    }


    /**
     * Returns non-repeatable id for a administrative section of digital object metadata.
     * 
     * @return id for a descriptive section.
     */
    String getFileAdmId() {
        String admId = createId(FILE_ADM_SECTION_ID_PREFIX, ++fadm, 4);
        tech.put(admId, 0);
        source.put(admId, 0);
        rights.put(admId, 0);
        digiprov.put(admId, 0);
        return admId;
    }


    /**
     * Returns non-repeatable id for a file group section of digital object metadata.
     * 
     * @return id for a file group section.
     */
    String getFileGrpId() {
        return createId(FILE_GROUP_SECTION_ID_PREFIX, ++fgrp, 2);
    }


    /**
     * Returns non-repeatable id for a file (in the file group section) of digital object.
     * 
     * @param fgrpId
     *            id of file group section
     * @return id for a file (in the file group section).
     */
    String getFileId(String fgrpId) {
        return fgrpId + createId(FILE_GROUP_FILE_ID_PREFIX, ++file, 4);
    }


    /**
     * Returns non-repeatable id for a technical subsection of an administrative section.
     * 
     * @param admId
     *            id of administrative section
     * @return id for a technical section.
     */
    String getTechId(String admId) {
        if (tech.containsKey(admId)) {
            tech.put(admId, tech.get(admId) + 1);
        } else {
            tech.put(admId, 1);
        }
        return admId + createId(TECH_ADM_SECTION_ID_PREFIX, tech.get(admId), 2);
    }


    /**
     * Returns non-repeatable id for a source subsection of an administrative section.
     * 
     * @param admId
     *            id of administrative section
     * @return id for a source section.
     */
    String getSourceId(String admId) {
        if (source.containsKey(admId)) {
            source.put(admId, source.get(admId) + 1);
        } else {
            source.put(admId, 1);
        }
        return admId + createId(SOURCE_ADM_SECTION_ID_PREFIX, source.get(admId), 2);
    }


    /**
     * Returns non-repeatable id for a rights subsection of an administrative section.
     * 
     * @param admId
     *            id of administrative section
     * @return id for a rights section.
     */
    String getRightsId(String admId) {
        if (rights.containsKey(admId)) {
            rights.put(admId, rights.get(admId) + 1);
        } else {
            rights.put(admId, 1);
        }
        return admId + createId(RIGHTS_ADM_SECTION_ID_PREFIX, rights.get(admId), 2);
    }


    /**
     * Returns non-repeatable id for a digital provenance subsection of an administrative section.
     * 
     * @param admId
     *            id of administrative section
     * @return id for a digital provenance section.
     */
    String getDigiprovId(String admId) {
        if (digiprov.containsKey(admId)) {
            digiprov.put(admId, digiprov.get(admId) + 1);
        } else {
            digiprov.put(admId, 1);
        }
        return admId + createId(DIGIPROV_ADM_SECTION_ID_PREFIX, digiprov.get(admId), 2);
    }


    /**
     * Returns non-repeatable id for an event in a digital provenance subsection in the PREMIS schema.
     * 
     * @return id for an event
     */
    String getPremisEventId() {
        return createId("", ++pevt, 4);
    }


    /**
     * Creates id of a section based upon prefix and id number.
     * 
     * @param prefix
     *            prefix
     * @param id
     *            id number
     * @param length
     *            length of id (without prefix)
     * @return id
     */
    private String createId(String prefix, int id, int length) {
        String s = Integer.toString(id);
        return prefix + LEADING_ZEROS.substring(s.length() + LEADING_ZEROS.length() - length) + s;
    }

}
