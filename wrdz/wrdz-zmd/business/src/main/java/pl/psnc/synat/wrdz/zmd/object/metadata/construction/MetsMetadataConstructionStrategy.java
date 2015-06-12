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
package pl.psnc.synat.wrdz.zmd.object.metadata.construction;

import gov.loc.mets.AmdSecType;
import gov.loc.mets.FileType;
import gov.loc.mets.FileType.FLocat;
import gov.loc.mets.MdSecType.MdRef;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.metadata.mets.MetsConsts;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadata;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataBuilder;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataBuilderFactory;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataProcessingException;
import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.entity.object.ConvertedObject;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier;
import pl.psnc.synat.wrdz.zmd.entity.object.MasterObject;
import pl.psnc.synat.wrdz.zmd.entity.object.OptimizedObject;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataNamespace;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration;
import pl.psnc.synat.wrdz.zmd.entity.types.MetadataType;
import pl.psnc.synat.wrdz.zmd.input.InputFile;

/**
 * Interface for the content version metadata construction strategy in the METS schema.
 */
public abstract class MetsMetadataConstructionStrategy {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(MetsMetadataConstructionStrategy.class);


    /**
     * Constructs content version metadata for the digital object in the METS schema.
     * 
     * @param contentVersion
     *            content version of the digital object
     * @return content version of metadata
     * @throws MetsMetadataProcessingException
     *             when some problem with the construction of metadata occurs
     */
    @SuppressWarnings("rawtypes")
    public MetsMetadata constructMetsMetadata(ContentVersion contentVersion)
            throws MetsMetadataProcessingException {
        logger.debug("Mets construction for: " + contentVersion);

        DigitalObject digitalObject = contentVersion.getObject();
        List<ContentVersion> previousVersions = new ArrayList<ContentVersion>(digitalObject.getVersions());
        ObjectExtractedMetadata previousMetsMetadata = null;
        if (contentVersion.getVersion().equals(1)) {
            previousVersions = new ArrayList<ContentVersion>();
        } else {
            previousVersions.remove(contentVersion);
            Collections.sort(previousVersions, new ContentVersionComparator());
            previousMetsMetadata = previousVersions.get(previousVersions.size() - 1).getExtractedMetadata();
        }
        logger.debug("Previous Mets metadata: " + previousMetsMetadata);

        MetsMetadataBuilder metsBuilder = MetsMetadataBuilderFactory.getInstance().getMetsMetadataBuilder();

        Identifier defaultIdentifier = digitalObject.getDefaultIdentifier();
        metsBuilder.setObjectIdentifier(defaultIdentifier.getIdentifier(), defaultIdentifier.getType().name());
        metsBuilder.setObjectType(digitalObject.getType().name());
        if (previousVersions.size() != 0) {
            metsBuilder.setCreationDate(previousVersions.get(0).getCreatedOn());
            metsBuilder.setModificationDate(contentVersion.getCreatedOn());
        } else {
            metsBuilder.setCreationDate(contentVersion.getCreatedOn());
        }
        metsBuilder.setVersionNumber(contentVersion.getVersion());
        for (Identifier identifier : digitalObject.getIdentifiers()) {
            if (!identifier.equals(defaultIdentifier)) {
                metsBuilder.addAlternativeObjectIdentifier(identifier.getType().name(), identifier.getIdentifier());
            }
        }
        for (ObjectProvidedMetadata providedMetadata : contentVersion.getProvidedMetadata()) {
            buildMetadataForObject(metsBuilder, providedMetadata, previousMetsMetadata);
        }
        for (DataFileVersion file : contentVersion.getFiles()) {
            for (FileProvidedMetadata fileProvidedMetadata : file.getProvidedMetadata()) {
                buildMetadataForFile(metsBuilder, file.getDataFile(), fileProvidedMetadata, previousMetsMetadata);
            }

            for (FileExtractedMetadata fileExtractedMetadata : file.getDataFile().getExtractedMetadata()) {
                buildMetadataForFile(metsBuilder, file.getDataFile(), fileExtractedMetadata, previousMetsMetadata);
            }
        }
        buildObjectOrigin(metsBuilder, getObjectOrigin(digitalObject));
        List<Migration> derivatives = getObjectDerivatives(digitalObject);
        Collections.sort(derivatives, new MigrationComparator());
        Iterator<Migration> it = derivatives.iterator();
        Migration derivative = null;
        if (it.hasNext()) {
            derivative = it.next();
        }
        for (ContentVersion version : previousVersions) {
            derivative = buildDerivativesEarlierThanContentVersion(metsBuilder, derivative, it, version);
            metsBuilder.setObjectModificationEvent(version.getCreatedOn(), version.getVersion(),
                getVersionUpdatedFiles(version));
        }
        derivative = buildDerivativesEarlierThanContentVersion(metsBuilder, derivative, it, contentVersion);
        metsBuilder.setObjectModificationEvent(contentVersion.getCreatedOn(), contentVersion.getVersion(),
            getVersionUpdatedFiles(contentVersion));
        if (derivative != null) {
            buildDerivative(metsBuilder, derivative);
        }
        while (it.hasNext()) {
            buildDerivative(metsBuilder, it.next());
        }
        for (DataFileVersion file : contentVersion.getFiles()) {
            metsBuilder.addDataFile(file.getDataFile().getObjectFilepath(), file.getSequence());
        }
        return metsBuilder.build();
        //return constructMetsMetadata(contentVersion, null);
    }


    /**
     * Constructs content version metadata for the digital object in the METS schema.
     * 
     * @param contentVersion
     *            content version of the digital object
     * @param metsProvidedUri
     *            URI to tmpProvidedMetsFile
     * @param inputFilesSet
     *            set of InputFiles.
     * @return content version of metadata
     * @throws MetsMetadataProcessingException
     *             when some problem with the construction of metadata occurs
     */
    @SuppressWarnings("rawtypes")
    public MetsMetadata constructMetsMetadata(ContentVersion contentVersion, URI metsProvidedUri,
            Set<InputFile> inputFilesSet)
            throws MetsMetadataProcessingException {
        logger.debug("Mets construction for: " + contentVersion);

        //if request not contain tmpProvidedMetsUri then use classic version of metadata construction
        if ((inputFilesSet == null) || (metsProvidedUri == null)) {
            return constructMetsMetadata(contentVersion);
        }

        DigitalObject digitalObject = contentVersion.getObject();
        List<ContentVersion> previousVersions = new ArrayList<ContentVersion>(digitalObject.getVersions());
        ObjectExtractedMetadata previousMetsMetadata = null;
        if (contentVersion.getVersion().equals(1)) {
            previousVersions = new ArrayList<ContentVersion>();
        } else {
            previousVersions.remove(contentVersion);
            Collections.sort(previousVersions, new ContentVersionComparator());
            previousMetsMetadata = previousVersions.get(previousVersions.size() - 1).getExtractedMetadata();
        }
        logger.debug("Previous Mets metadata: " + previousMetsMetadata);

        MetsMetadataBuilder metsBuilder = MetsMetadataBuilderFactory.getInstance().getMetsMetadataBuilder(
            metsProvidedUri);

        //prepare input files <-> metadata files mapping
        HashMap<String, String> metadataFilesMap = getInputFilesMetadataFilesMap(inputFilesSet);
        HashMap<String, String> dataFileMap = metsBuilder.getCurrentDataFileNamesUriMapping();

        //change location of rest of metadata (object provided metatada)
        HashMap<String, String> objectMetadataFilesMap = new HashMap<String, String>();
        for (ObjectProvidedMetadata providedMetadata : contentVersion.getProvidedMetadata()) {
            if (providedMetadata.getFilename() != null) {
                objectMetadataFilesMap.put(providedMetadata.getFilename(), providedMetadata.getObjectFilepath());
            }
        }
        metsBuilder.setObjectMetadata(objectMetadataFilesMap);

        Identifier defaultIdentifier = digitalObject.getDefaultIdentifier();
        metsBuilder.setObjectIdentifier(defaultIdentifier.getIdentifier(), defaultIdentifier.getType().name());
        metsBuilder.setObjectType(digitalObject.getType().name());
        if (previousVersions.size() != 0) {
            metsBuilder.setCreationDate(previousVersions.get(0).getCreatedOn());
            metsBuilder.setModificationDate(contentVersion.getCreatedOn());
        } else {
            metsBuilder.setCreationDate(contentVersion.getCreatedOn());
        }
        metsBuilder.setVersionNumber(contentVersion.getVersion());
        for (Identifier identifier : digitalObject.getIdentifiers()) {
            if (!identifier.equals(defaultIdentifier)) {
                metsBuilder.addAlternativeObjectIdentifier(identifier.getType().name(), identifier.getIdentifier());
            }
        }

        for (DataFileVersion file : contentVersion.getFiles()) {

            if (file.getDataFile().getFilename() == null) {
                continue;
            }

            String dataFileName = file.getDataFile().getFilename();
            String dataFileUri = dataFileMap.get(dataFileName);
            FLocat dataFileLoc = metsBuilder.findFileSectionByURL(dataFileUri);
            if (dataFileLoc == null) {
                continue;
            }
            dataFileLoc.setHref(file.getDataFile().getObjectFilepath());
            dataFileLoc.setLOCTYPE(MetsConsts.METS_METADATA_LOCATION_TYPE_RELATIVE);
            FileType lastFileTypeSec = metsBuilder.getLastFileTypeSection();
            try {
                metsBuilder.addMapping(file.getDataFile().getObjectFilepath(), ((AmdSecType) lastFileTypeSec.getADMID()
                        .get(0)).getID());
            } catch (Exception e) {
                logger.debug("Reading admSection id error: " + e.getMessage() + " || " + e.toString());
            }

            for (FileProvidedMetadata fileProvidedMetadata : file.getProvidedMetadata()) {
                if (fileProvidedMetadata.getFilename() == null) {
                    continue;
                }
                String metaDataFileName = dataFileName + fileProvidedMetadata.getFilename();
                String metadataFileUri = metadataFilesMap.get(metaDataFileName);

                MdRef mdRefSec = metsBuilder.findFileAdmSectionByURL(metadataFileUri);
                if (mdRefSec == null) {
                    continue;
                }
                mdRefSec.setHref(fileProvidedMetadata.getObjectFilepath());
                mdRefSec.setLOCTYPE(MetsConsts.METS_DATA_LOCATION_TYPE_RELATIVE);
            }

            for (FileExtractedMetadata fileExtractedMetadata : file.getDataFile().getExtractedMetadata()) {
                buildMetadataForFile(metsBuilder, file.getDataFile(), fileExtractedMetadata, previousMetsMetadata);
            }
        }

        buildObjectOrigin(metsBuilder, getObjectOrigin(digitalObject));
        List<Migration> derivatives = getObjectDerivatives(digitalObject);
        Collections.sort(derivatives, new MigrationComparator());
        Iterator<Migration> it = derivatives.iterator();
        Migration derivative = null;
        if (it.hasNext()) {
            derivative = it.next();
        }
        for (ContentVersion version : previousVersions) {
            derivative = buildDerivativesEarlierThanContentVersion(metsBuilder, derivative, it, version);
            metsBuilder.setObjectModificationEvent(version.getCreatedOn(), version.getVersion(),
                getVersionUpdatedFiles(version));
        }
        derivative = buildDerivativesEarlierThanContentVersion(metsBuilder, derivative, it, contentVersion);
        metsBuilder.setObjectModificationEvent(contentVersion.getCreatedOn(), contentVersion.getVersion(),
            getVersionUpdatedFiles(contentVersion));
        if (derivative != null) {
            buildDerivative(metsBuilder, derivative);
        }
        while (it.hasNext()) {
            buildDerivative(metsBuilder, it.next());
        }

        return metsBuilder.build();
    }


    /**
     * Builds metadata for object.
     * 
     * @param metsBuilder
     *            builder
     * @param metadataFile
     *            metadata file for object
     * @param previousMetsMetadata
     *            METS metadata for previous version
     */
    protected abstract void buildMetadataForObject(MetsMetadataBuilder metsBuilder,
            pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile metadataFile,
            ObjectExtractedMetadata previousMetsMetadata);


    /**
     * Builds metadata for file.
     * 
     * @param metsBuilder
     *            builder
     * @param dataFile
     *            data file
     * @param metadataFile
     *            metadata file for file
     * @param previousMetsMetadata
     *            METS metadata for previous version
     */
    protected abstract void buildMetadataForFile(MetsMetadataBuilder metsBuilder, DataFile dataFile,
            pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile metadataFile,
            ObjectExtractedMetadata previousMetsMetadata);


    /**
     * Builds (by a builder) an origin of a digital object.
     * 
     * @param metsBuilder
     *            builder
     * @param origin
     *            origin of an object
     */
    @SuppressWarnings("rawtypes")
    private void buildObjectOrigin(MetsMetadataBuilder metsBuilder, Migration origin) {
        if (origin != null) {
            if (origin.getMigrationSource() != null) {
                metsBuilder.setLocalObjectOrigin(origin.getMigrationSource().getDefaultIdentifier().getIdentifier(),
                    origin.getType().name(), origin.getDate(), origin.getInfo());
            } else {
                metsBuilder.setRemoteObjectOrigin(origin.getSourceIdentifier(), origin.getType().name(),
                    origin.getSourceIdentifierResolver(), origin.getDate(), origin.getInfo());
            }
        }
    }


    /**
     * Builds (by a builder) all derivatives which arised before a content version.
     * 
     * @param metsBuilder
     *            builder
     * @param derivative
     *            the first unprocessed derivative of an object
     * @param it
     *            iterator of derivatives
     * @param contentVersion
     *            version of an object
     * @return the first unprocessed derivative
     */
    @SuppressWarnings("rawtypes")
    private Migration buildDerivativesEarlierThanContentVersion(MetsMetadataBuilder metsBuilder, Migration derivative,
            Iterator<Migration> it, ContentVersion contentVersion) {
        while (derivative != null && derivative.getDate() != null
                && derivative.getDate().before(contentVersion.getCreatedOn())) {
            buildDerivative(metsBuilder, derivative);
            if (it.hasNext()) {
                derivative = it.next();
            } else {
                derivative = null;
            }
        }
        return derivative;
    }


    /**
     * Builds (by a builder) a derivative of a digital object.
     * 
     * @param metsBuilder
     *            builder
     * @param derivative
     *            derivative of an object
     */
    @SuppressWarnings("rawtypes")
    private void buildDerivative(MetsMetadataBuilder metsBuilder, Migration derivative) {
        if (derivative.getMigrationResult() != null) {
            metsBuilder.setLocalObjectDerivative(
                derivative.getMigrationResult().getDefaultIdentifier().getIdentifier(), derivative.getType().name(),
                derivative.getDate(), derivative.getInfo());
        } else {
            metsBuilder.setRemoteObjectDerivative(derivative.getResultIdentifier(), derivative.getType().name(),
                derivative.getResultIdentifierResolver(), derivative.getDate(), derivative.getInfo());
        }
    }


    /**
     * Gets object origin.
     * 
     * @param digitalObject
     *            digital object
     * @return object origin
     */
    @SuppressWarnings("rawtypes")
    private Migration getObjectOrigin(DigitalObject digitalObject) {
        Migration origin = null;
        if (digitalObject instanceof MasterObject) {
            origin = ((MasterObject) digitalObject).getTransformedFrom();
        } else if (digitalObject instanceof OptimizedObject) {
            origin = ((OptimizedObject) digitalObject).getOptimizedFrom();
        } else if (digitalObject instanceof ConvertedObject) {
            origin = ((ConvertedObject) digitalObject).getConvertedFrom();
        }
        return origin;
    }


    /**
     * Gets mapping between InputFiles and MetadataFiles names.
     * 
     * @param inputFilesSet
     *            set o finput files.
     * @return mapping between inputFiles names and metadata files
     */
    private HashMap<String, String> getInputFilesMetadataFilesMap(Set<InputFile> inputFilesSet) {
        Iterator<InputFile> it = inputFilesSet.iterator();
        HashMap<String, String> map = new HashMap<String, String>();
        while (it.hasNext()) {
            InputFile inputFile = (InputFile) it.next();
            String inputFileName = inputFile.getDestination();
            Iterator<Entry<String, URI>> it1 = inputFile.getMetadataFilesToAdd().entrySet().iterator();
            while (it1.hasNext()) {
                Entry<String, URI> entry = (Entry<String, URI>) it1.next();
                map.put(inputFileName + entry.getKey(), entry.getValue().toString());
            }

        }
        return map;
    }


    /**
     * Gets object origin.
     * 
     * @param digitalObject
     *            digital object
     * @return object origin
     */
    @SuppressWarnings({ "rawtypes" })
    private List<Migration> getObjectDerivatives(DigitalObject digitalObject) {
        List<Migration> derivatives = new ArrayList<Migration>();
        if (digitalObject instanceof MasterObject) {
            MasterObject masterObject = (MasterObject) digitalObject;
            derivatives.addAll(masterObject.getTransformedTo());
            derivatives.addAll(masterObject.getOptimizedTo());
            derivatives.addAll(masterObject.getConvertedTo());
        } else if (digitalObject instanceof OptimizedObject) {
            OptimizedObject optimizedObject = (OptimizedObject) digitalObject;
            derivatives.addAll(optimizedObject.getOptimizedTo());
            derivatives.addAll(optimizedObject.getConvertedTo());
        } else if (digitalObject instanceof ConvertedObject) {
            ConvertedObject convertedObject = (ConvertedObject) digitalObject;
            derivatives.addAll(convertedObject.getConvertedTo());
        }
        return derivatives;
    }


    /**
     * Gets all new and modified files of version.
     * 
     * @param contentVersion
     *            version
     * @return list of all files updated within version (object relative path of version)
     */
    private List<String> getVersionUpdatedFiles(ContentVersion contentVersion) {
        List<String> result = new ArrayList<String>();
        for (DataFileVersion file : contentVersion.getFiles()) {
            if (contentVersion.equals(getEarliestVersion(file.getDataFile()))) {
                result.add(file.getDataFile().getObjectFilepath());
                for (FileExtractedMetadata fileExtractedMetadata : file.getDataFile().getExtractedMetadata()) {
                    result.add(fileExtractedMetadata.getObjectFilepath());
                }
            }

            for (FileProvidedMetadata fileProvidedMetadata : file.getProvidedMetadata()) {
                if (contentVersion.equals(getEarliestVersion(fileProvidedMetadata, contentVersion))) {
                    result.add(fileProvidedMetadata.getObjectFilepath());
                }
            }
        }
        for (ObjectProvidedMetadata objectProvidedMetadata : contentVersion.getProvidedMetadata()) {
            if (contentVersion.equals(getEarliestVersion(objectProvidedMetadata, contentVersion))) {
                result.add(objectProvidedMetadata.getObjectFilepath());
            }
        }
        // add itself - always new and always one metadata for object version
        result.add("metadata/extracted/mets.xml");

        return result;
    }


    /**
     * Gets the earliest content version of the data file.
     * 
     * @param dataFile
     *            data file
     * @return earliest content version
     */
    private ContentVersion getEarliestVersion(DataFile dataFile) {
        List<ContentVersion> versions = new ArrayList<ContentVersion>();
        for (DataFileVersion version : dataFile.getIncludedIn()) {
            versions.add(version.getContentVersion());
        }
        Collections.sort(versions, new ContentVersionComparator());
        return versions.get(0);
    }


    /**
     * Gets the earliest content version of the object provided metadata file.
     * 
     * @param objectProvidedMetadata
     *            provided metadata
     * @param thisVersion
     *            this version
     * @return earliest content version
     */
    private ContentVersion getEarliestVersion(ObjectProvidedMetadata objectProvidedMetadata, ContentVersion thisVersion) {
        List<ContentVersion> includedIn = objectProvidedMetadata.getProvidedFor();
        Collections.sort(includedIn, new ContentVersionComparator());
        return includedIn.get(0);
    }


    /**
     * Gets the earliest content version of the provided metadata of some data file.
     * 
     * @param fileProvidedMetadata
     *            metadata file
     * @param thisVersion
     *            this version
     * @return earliest content version
     */
    private ContentVersion getEarliestVersion(FileProvidedMetadata fileProvidedMetadata, ContentVersion thisVersion) {
        List<ContentVersion> versions = new ArrayList<ContentVersion>();
        for (DataFileVersion version : fileProvidedMetadata.getProvidedFor()) {
            versions.add(version.getContentVersion());
        }
        Collections.sort(versions, new ContentVersionComparator());
        return versions.get(0);
    }


    /**
     * Returns type of metadata namespace if it is not a null, {@link NamespaceType#UNKNOWN} otherwise.
     * 
     * @param namespace
     *            metadata namespace
     * @return non null namespace type
     */
    protected NamespaceType getNonNullNamespaceType(MetadataNamespace namespace) {
        if (namespace == null) {
            return NamespaceType.UNKNOWN;
        }
        return namespace.getType();
    }


    /**
     * Returns status of metadata based upon their type.
     * 
     * @param type
     *            metadata type
     * @return metadata status
     */
    protected String getMetadataStatus(MetadataType type) {
        switch (type) {
            case FILES_EXTRACTED:
            case OBJECTS_EXTRACTED:
                return "EXTRACTED";
            case FILES_PROVIDED:
            case OBJECTS_PROVIDED:
                return "PROVIDED";
            default:
                return "";
        }
    }


    /**
     * Ascending comparator of content versions of the digital object.
     */
    private class ContentVersionComparator implements Comparator<ContentVersion> {

        @Override
        public int compare(ContentVersion cv1, ContentVersion cv2) {
            return cv1.getVersion().compareTo(cv2.getVersion());
        }

    }


    /**
     * Ascending comparator of migrations of the digital object.
     */
    @SuppressWarnings("rawtypes")
    private class MigrationComparator implements Comparator<Migration> {

        @Override
        public int compare(Migration m1, Migration m2) {
            if ((m1.getDate() == null) && (m2.getDate() == null)) {
                return 0;
            }
            if ((m1.getDate() != null) && (m2.getDate() == null)) {
                return -1;
            }
            if ((m1.getDate() == null) && (m2.getDate() != null)) {
                return 1;
            }
            return m1.getDate().compareTo(m2.getDate());
        }

    }
}
