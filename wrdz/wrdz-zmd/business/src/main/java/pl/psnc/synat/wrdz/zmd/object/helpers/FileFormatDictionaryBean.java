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
package pl.psnc.synat.wrdz.zmd.object.helpers;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.metadata.tech.ExtractedMetadata;
import pl.psnc.synat.wrdz.common.metadata.tech.FileFormat;
import pl.psnc.synat.wrdz.common.metadata.tech.FileType;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileFormatDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileFormatFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileFormatNameDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileFormatNameFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileFormat;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileFormatName;

/**
 * Provides access and basic operation on file format dictionary.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class FileFormatDictionaryBean {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FileFormatDictionaryBean.class);

    /**
     * Id of the entity denoting the system unrecognized file format.
     */
    private static final long UNRECOGNIZED_FORMAT_ID = 1;

    /**
     * Id of the entity denoting the system unrecognized file format.
     */
    private static final String UNRECOGNIZED_FORMAT_NAME = "unrecognized";

    /**
     * Data file format DAO.
     */
    @EJB
    private DataFileFormatDao dataFileFormatDao;

    /**
     * Data file format DAO.
     */
    @EJB
    private DataFileFormatNameDao dataFileFormatNameDao;


    /**
     * Checks if there is a file format in the dictionary which corresponds to the format extracted from a file and if
     * not it creates it. At the end it returns this corresponding file format from dictionary.
     * 
     * @param extractedMetadata
     *            extracted metadata
     * @return corresponding file format from the dictionary
     */
    public DataFileFormat getFileFormat(ExtractedMetadata extractedMetadata) {
        if (extractedMetadata == null || extractedMetadata.getFileFormat() == null) {
            return dataFileFormatDao.findById(UNRECOGNIZED_FORMAT_ID);
        }
        FileFormat fileFormat = extractedMetadata.getFileFormat();
        DataFileFormat dataFileFormat = null;
        if (fileFormat.getPuid() != null && fileFormat.getPuid().length() > 0) {
            DataFileFormatFilterFactory dffQueryFilterFactory = dataFileFormatDao.createQueryModifier()
                    .getQueryFilterFactory();
            dataFileFormat = dataFileFormatDao.findFirstResultBy(dffQueryFilterFactory.byPuid(fileFormat.getPuid()));
            if (dataFileFormat != null) {
                logger.debug("data file format for puid " + fileFormat.getPuid() + " exists: "
                        + dataFileFormat.toString());
                if ((dataFileFormat.getMimeType().equals(UNRECOGNIZED_FORMAT_NAME))
                        && (fileFormat.getMimeType() != null)) {
                    dataFileFormat.setMimeType(fileFormat.getMimeType());
                }
                if (dataFileFormat.getType() == FileType.UNKNOWN) {
                    dataFileFormat.setType(fileFormat.getFileType());
                }
                mergeFormatNames(fileFormat, dataFileFormat);
                return dataFileFormatDao.merge(dataFileFormat);
            } else {
                logger.debug("data file format for puid " + fileFormat.getPuid() + " does not exist.");
                return createDataFileFormat(fileFormat);
            }
        } else {
            DataFileFormatFilterFactory dffQueryFilterFactory = dataFileFormatDao.createQueryModifier()
                    .getQueryFilterFactory();
            dataFileFormat = dataFileFormatDao.findFirstResultBy(dffQueryFilterFactory.and(
                dffQueryFilterFactory.byMimeType(fileFormat.getMimeType()),
                dffQueryFilterFactory.byVersion(fileFormat.getFormatVersion())));
            if (dataFileFormat != null) {
                logger.debug("data file format for mime type " + fileFormat.getMimeType() + " and version "
                        + fileFormat.getFormatVersion() + " exists: " + dataFileFormat.toString());
                if (dataFileFormat.getType() == FileType.UNKNOWN) {
                    dataFileFormat.setType(fileFormat.getFileType());
                }
                mergeFormatNames(fileFormat, dataFileFormat);
                return dataFileFormatDao.merge(dataFileFormat);
            } else {
                logger.debug("data file format for mime type " + fileFormat.getMimeType() + " and version "
                        + fileFormat.getFormatVersion() + " does not exist.");
                return createDataFileFormat(fileFormat);
            }
        }
    }


    /**
     * Merges format names recognized by the specialistic tool with those which are currently in database and set them
     * to the data file format object.
     * 
     * @param fileFormat
     *            file format recognized by the specialistic tool
     * @param dataFileFormat
     *            data file format in which format names have to be merged
     */
    private void mergeFormatNames(FileFormat fileFormat, DataFileFormat dataFileFormat) {
        List<DataFileFormatName> namesToAdd = new ArrayList<DataFileFormatName>();
        nextname: for (String formatName : fileFormat.getNames()) {
            for (DataFileFormatName dataFileFormatName : dataFileFormat.getNames()) {
                if (formatName.equals(dataFileFormatName.getName())) {
                    continue nextname;
                }
            }
            DataFileFormatNameFilterFactory dffnQueryFilterFactory = dataFileFormatNameDao.createQueryModifier()
                    .getQueryFilterFactory();
            DataFileFormatName existingDataFileFormatName = dataFileFormatNameDao
                    .findFirstResultBy(dffnQueryFilterFactory.byName(formatName));
            if (existingDataFileFormatName != null) {
                namesToAdd.add(existingDataFileFormatName);
            } else {
                DataFileFormatName newDataFileFormatName = new DataFileFormatName();
                newDataFileFormatName.setName(formatName);
                dataFileFormatNameDao.persist(newDataFileFormatName);
                namesToAdd.add(newDataFileFormatName);
            }
        }
        dataFileFormat.addNames(namesToAdd);
    }


    /**
     * Creates new data file format entity based upon file format identification object derived from extracted metadata.
     * 
     * @param fileFormat
     *            file format derived from extracted metadata.
     * @return new date file format entity
     */
    private DataFileFormat createDataFileFormat(FileFormat fileFormat) {
        DataFileFormat dataFileFormat = new DataFileFormat();
        dataFileFormat.setPuid(fileFormat.getPuid());
        if (fileFormat.getMimeType() != null) {
            dataFileFormat.setMimeType(fileFormat.getMimeType());
        } else {
            dataFileFormat.setMimeType(UNRECOGNIZED_FORMAT_NAME);
        }
        if (fileFormat.getPuid() == null || fileFormat.getPuid().length() == 0) {
            dataFileFormat.setVersion(fileFormat.getFormatVersion());
        }
        dataFileFormat.setType(FileType.valueOf(fileFormat.getFileType().name()));
        mergeFormatNames(fileFormat, dataFileFormat);
        dataFileFormatDao.persist(dataFileFormat);
        return dataFileFormat;
    }

}
