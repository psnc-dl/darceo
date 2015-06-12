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
package pl.psnc.synat.wrdz.zmd.object;

import gov.loc.mets.AmdSecType;
import gov.loc.mets.MdSecType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadata;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataProcessingException;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataReader;
import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataReaderFactory;
import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.common.performance.Profiler;
import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.entity.MetadataSectionsCollection;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;
import pl.psnc.synat.wrdz.zmd.entity.types.OperationType;
import pl.psnc.synat.wrdz.zmd.exception.ArchiverException;
import pl.psnc.synat.wrdz.zmd.input.object.FileFetchingRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectDeletionRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectFetchingRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectVersionDeletionRequest;
import pl.psnc.synat.wrdz.zmd.object.content.ContentFetcher;
import pl.psnc.synat.wrdz.zmd.object.helpers.archivers.ArchiveBuilder;
import pl.psnc.synat.wrdz.zmd.object.helpers.archivers.ZipArchiveBuilder;
import pl.psnc.synat.wrdz.zmd.object.metadata.construction.InlineMetsMetadataConstructionStrategy;
import pl.psnc.synat.wrdz.zmd.object.metadata.construction.MetsMetadataConstructionStrategy;
import pl.psnc.synat.wrdz.zmd.object.metadata.construction.OperationsDateComparator;
import pl.psnc.synat.wrdz.zmd.object.metadata.operation.OperationManager;
import pl.psnc.synat.wrdz.zmd.output.ResultFile;
import pl.psnc.synat.wrdz.zmd.storage.DataStorageException;

/**
 * Bean providing functionalities of CRUD operations on digital objects.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ObjectManagerBean implements ObjectManager {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(MetsMetadataConstructionStrategy.class);

    /**
     * Object creator bean.
     */
    @EJB
    private ObjectCreator objectCreator;

    /**
     * Object modifier bean.
     */
    @EJB
    private ObjectModifier objectModifier;

    /**
     * Object deleter bean.
     */
    @EJB
    private ObjectDeleter objectDeleter;

    /**
     * Content fetcher bean.
     */
    @EJB
    private ContentFetcher contentFetcher;

    /**
     * Object finder bean.
     */
    @EJB
    private ObjectBrowser objectFinder;

    /**
     * Operation's manager.
     */
    @EJB
    private OperationManager operationManager;

    /**
     * ZMD module configuration.
     */
    @Inject
    private ZmdConfiguration zmdConfiguration;


    @Override
    public String createObject(ObjectCreationRequest request)
            throws ObjectCreationException {

        Profiler.profile("Object creation request");

        DigitalObject object = objectCreator.createObject(request);
        try {
            constructOaiPmhMetadata(object, OperationType.CREATION, request.getMetsProvidedURI());
        } catch (MetsMetadataProcessingException e) {
            throw new ObjectCreationException("Unable to extract object metadata", e);
        }
        try {
            if (request.getMigratedFrom() != null) {
                DigitalObject origin = objectFinder.getDigitalObject(request.getMigratedFrom().getIdentifier());
                try {
                    constructOaiPmhMetadata(origin, OperationType.MODIFICATION);
                } catch (MetsMetadataProcessingException e) {
                    throw new ObjectCreationException("Unable to extract object metadata", e);
                }
            }
        } catch (ObjectNotFoundException e) {
            logger.debug("No digital object for specified origin found.");
        }

        clearCachePaths(object.getCurrentVersion());

        Profiler.dump();

        return object.getIdentifiers().get(0).getIdentifier();
    }


    @Override
    public int modifyObject(ObjectModificationRequest request)
            throws ObjectModificationException {

        Profiler.profile("Object modification request");

        DigitalObject target;
        try {
            target = objectFinder.getDigitalObject(request.getIdentifier());
        } catch (ObjectNotFoundException e) {
            throw new ObjectModificationException("Object specified for modification does not exist.", e);
        }
        ContentVersion newVersion = objectModifier.modifyObject(request, target);
        try {
            constructOaiPmhMetadata(target, OperationType.MODIFICATION);
        } catch (MetsMetadataProcessingException e) {
            throw new ObjectModificationException("Unable to extract object metadata", e);
        }
        try {
            if (request.getMigratedFrom() != null) {
                DigitalObject origin = objectFinder.getDigitalObject(request.getMigratedFrom().getIdentifier());
                try {
                    constructOaiPmhMetadata(origin, OperationType.MODIFICATION);
                } catch (MetsMetadataProcessingException e) {
                    throw new ObjectModificationException("Unable to extract object metadata", e);
                }
            }
        } catch (ObjectNotFoundException e) {
            logger.debug("No digital object for specified origin found.");
        }
        clearCachePaths(newVersion);

        Profiler.dump();

        return newVersion.getVersion();
    }


    /**
     * Constructs metadata for OAI-PMH protocol.
     * 
     * @param object
     *            digital object for which metadata are constructed
     * @param operation
     *            operation type
     * @throws MetsMetadataProcessingException
     *             when construction of METS metsdata failed
     */
    private void constructOaiPmhMetadata(DigitalObject object, OperationType operation)
            throws MetsMetadataProcessingException {
        constructOaiPmhMetadata(object, operation, null);
    }


    /**
     * Constructs metadata for OAI-PMH protocol.
     * 
     * @param object
     *            digital object for which metadata are constructed
     * @param operation
     *            operation type
     * @param tmpProvidedMetsFileURI
     *            URI to temporary file storing provided METS
     * @throws MetsMetadataProcessingException
     *             when construction of METS metsdata failed
     */
    private void constructOaiPmhMetadata(DigitalObject object, OperationType operation, URI tmpProvidedMetsFileURI)
            throws MetsMetadataProcessingException {
        MetsMetadata operationMetsMetadata = null;
        if (zmdConfiguration.constructMetsMetadata()) {
            operationMetsMetadata = new InlineMetsMetadataConstructionStrategy().constructMetsMetadata(object
                    .getCurrentVersion());
        }
        Date operationDate = operationManager.getSynchronizedDate();
        logger.debug("operation date " + operationDate);
        if (zmdConfiguration.constructMetsMetadata()) {
            switch (operation) {
                case CREATION:
                    operationManager.createCreationOperation(object, operationMetsMetadata.getXml(),
                        NamespaceType.METS, operationDate);
                    break;
                case MODIFICATION:
                    operationManager.createModificationOperation(object, operationMetsMetadata.getXml(),
                        NamespaceType.METS, operationDate);
                    break;
                default:
            }
        }
        switch (operation) {
            case CREATION:
                operationManager.createCreationOperation(object, createDublinCoreStub(object), NamespaceType.OAI_DC,
                    operationDate);
                break;
            case MODIFICATION:
                operationManager.createModificationOperation(object, createDublinCoreStub(object),
                    NamespaceType.OAI_DC, operationDate);
                break;
            default:
        }
    }


    /**
     * Constructs digital object pseudo-metadata for OAI-PMH harvesters in the DC schema.
     * 
     * @param object
     *            digital object
     * @return pseudo-metadata in the DC schema
     */
    private String createDublinCoreStub(DigitalObject object) {
        StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>\n");
        sb.append("<!DOCTYPE rdf:RDF PUBLIC \"-//DUBLIN CORE//DCMES DTD 2002/07/31//EN\" \"http://dublincore.org/documents/2002/07/31/dcmes-xml/dcmes-xml-dtd.dtd\">");
        sb.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n\txmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n");
        sb.append("\t<rdf:Description>\n");
        sb.append("\t\t<dc:identifier>\n\t\t" + object.getDefaultIdentifier().getIdentifier() + "\n</dc:identifier>\n");
        sb.append("\t</rdf:Description>\n");
        sb.append("</rdf:RDF>\n");
        return sb.toString();
    }


    @Override
    public int deleteVersion(ObjectVersionDeletionRequest request)
            throws ObjectDeletionException {
        if (request.getVersion() > 1) {
            try {
                Profiler.profile("Object version deletion request");

                ContentVersion previousVersion = objectFinder.getObjectsVersion(request.getIdentifier(),
                    request.getVersion() - 1);
                int currentVersion = objectDeleter.deleteContentVersion(previousVersion);
                String previousMetadataContents = null;
                if (zmdConfiguration.constructMetsMetadata()) {
                    previousMetadataContents = getPreviousMetsMetadataContents(previousVersion.getExtractedMetadata()
                            .getMetadataContent());
                }
                Date operationDate = operationManager.getSynchronizedDate();
                if (zmdConfiguration.constructMetsMetadata()) {
                    operationManager.createModificationOperation(previousVersion.getObject(), previousMetadataContents,
                        NamespaceType.METS, operationDate);
                }
                operationManager.createModificationOperation(previousVersion.getObject(),
                    createDublinCoreStub(previousVersion.getObject()), NamespaceType.OAI_DC, operationDate);

                Profiler.dump();

                return currentVersion;
            } catch (ObjectNotFoundException e) {
                throw new ObjectDeletionException("Digital object " + request.getIdentifier() + " version number "
                        + request.getVersion() + " does not exists.");
            }
        } else {
            deleteObject(new ObjectDeletionRequest(request.getIdentifier()));
        }
        return 0;
    }


    /**
     * Fetches the contents of previous object's METS metadata file.
     * 
     * @param operations
     *            list of operations.
     * @return contents of previous object's METS metadata file.
     */
    private String getPreviousMetsMetadataContents(List<Operation> operations) {
        if (operations == null || operations.size() == 0) {
            return null;
        }
        Collections.sort(operations, new OperationsDateComparator());
        Operation result = operations.get(operations.size() - 1);
        if (result.getMetadataType() != NamespaceType.METS) {
            return null;
        }
        return result.getContents();

    }


    @Override
    public void deleteObject(ObjectDeletionRequest request)
            throws ObjectDeletionException {

        Profiler.profile("Object deletion request");

        DigitalObject digitalObject;
        try {
            digitalObject = objectFinder.getDigitalObject(request.getIdentifier());
            Date operationDate = operationManager.getSynchronizedDate();
            if (zmdConfiguration.constructMetsMetadata()) {
                operationManager.createDeletionOperation(digitalObject, null, NamespaceType.METS, operationDate);
            }
            operationManager.createDeletionOperation(digitalObject, null, NamespaceType.OAI_DC, operationDate);
        } catch (ObjectNotFoundException e) {
            throw new ObjectDeletionException("Digital object " + request.getIdentifier() + " does not exists.");
        }
        objectDeleter.deleteDigitalObject(digitalObject);

        Profiler.dump();
    }


    @Override
    public ObjectFiles getFilesList(String identifier, Integer version, Boolean provided, Boolean extracted,
            Boolean hashes, Boolean absolute)
            throws ObjectNotFoundException {
        ContentVersion contentVersion = objectFinder.getObjectsVersion(identifier, version);
        if (contentVersion != null) {
            ObjectFilesFactory objectFilesFactory = new ObjectFilesFactory(contentVersion, provided, extracted, hashes,
                    absolute);
            return objectFilesFactory.produceObjectFiles();
        } else {
            throw new ObjectNotFoundException("Digital object " + identifier + " version number " + version
                    + " does not exists.");
        }
    }


    @Override
    public ObjectHistory getHistory(String identifier, boolean ascending)
            throws ObjectNotFoundException {
        List<ContentVersion> contentVersions = objectFinder.getContentVersions(identifier, ascending);
        if (contentVersions != null && contentVersions.size() > 0) {
            ObjectHistoryBuilder builder = new ObjectHistoryBuilder(contentVersions);
            return builder.buildHistory();
        } else {
            return null;
        }
    }


    @Override
    public ResultFile getObject(ObjectFetchingRequest request)
            throws ObjectNotFoundException, FetchingException {

        Profiler.profile("Object fetch request");

        ContentVersion contentVersion = objectFinder.getObjectsVersion(request.getIdentifier(), request.getVersion());
        try {

            Profiler.start("fetching object content");
            String rootFolder = contentFetcher.fetchEntireObject(contentVersion, request.getProvided(),
                request.getExtracted());
            File root = new File(rootFolder);
            Profiler.stop("fetching object content");

            Profiler.start("preparing zip archive");
            ArchiveBuilder archiveBuilder = new ZipArchiveBuilder(Arrays.asList(root.listFiles()), new File(
                    root.getParent() + File.separator + root.getName() + ".zip"));
            ResultFile result = new ResultFile(archiveBuilder.buildArchive(), createFileName(request.getIdentifier(),
                contentVersion.getVersion()));
            Profiler.stop("preparing zip archive");

            Profiler.dump();

            return result;
        } catch (DataStorageException e) {
            throw new FetchingException("Could not fetch the object from the data store.", e);
        } catch (ArchiverException e) {
            throw new FetchingException("Could not prepare archive with the object's contents.", e);
        }
    }


    @Override
    public ResultFile getContentFiles(FileFetchingRequest request)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException {

        Profiler.profile("Object content fetch request");

        List<DataFile> dataFiles = null;
        ContentVersion contentVersion = objectFinder.getObjectsVersion(request.getIdentifier(), request.getVersion());
        dataFiles = objectFinder.getDataFiles(contentVersion, request.getFiles());
        try {
            Profiler.start("fetching object content");
            String rootFolder = contentFetcher.fetchContentFiles(contentVersion, dataFiles, request.isProvided(),
                request.isExtracted());
            File root = new File(rootFolder);
            Profiler.stop("fetching object content");

            Profiler.start("preparing zip archive");
            ArchiveBuilder archiveBuilder = new ZipArchiveBuilder(Arrays.asList(root.listFiles()), new File(
                    root.getParent() + File.separator + root.getName() + ".zip"));
            ResultFile result = new ResultFile(archiveBuilder.buildArchive(), createFileName(request.getIdentifier(),
                contentVersion.getVersion()));
            Profiler.stop("preparing zip archive");

            Profiler.dump();

            return result;
        } catch (DataStorageException e) {
            throw new FetchingException("Could not fetch files from the data store.", e);
        } catch (ArchiverException e) {
            throw new FetchingException("Could not prepare archive with the files.", e);
        }
    }


    @Override
    public ResultFile getContentFile(String objectId, Integer version, String filePath)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException {

        Profiler.profile("File fetch request");

        List<DataFile> dataFiles = null;
        ContentVersion contentVersion = objectFinder.getObjectsVersion(objectId, version);
        List<String> files = new ArrayList<String>();
        files.add(filePath);
        dataFiles = objectFinder.getDataFiles(contentVersion, files);
        try {
            String rootFolder = contentFetcher.fetchContentFiles(contentVersion, dataFiles, false, false);
            File file = new File(rootFolder + "/" + dataFiles.get(0).getObjectFilepath());

            Profiler.dump();

            return new ResultFile(file, file.getName());
        } catch (DataStorageException e) {
            throw new FetchingException("Could not fetch files from the data store.", e);
        }
    }


    @Override
    public ResultFile getMainFile(FileFetchingRequest request)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException {

        Profiler.profile("Main file fetch request");

        ContentVersion contentVersion = objectFinder.getObjectsVersion(request.getIdentifier(), request.getVersion());
        DataFile mainFile = contentVersion.getMainFile();
        if (mainFile != null) {
            List<DataFile> dataFiles = new ArrayList<DataFile>();
            dataFiles.add(mainFile);
            try {
                Profiler.start("fetching object content");
                File root = new File(contentFetcher.fetchContentFiles(contentVersion, dataFiles, request.isProvided(),
                    request.isExtracted()));
                Profiler.stop("fetching object content");

                Profiler.start("preparing zip archive");
                ArchiveBuilder archiveBuilder = new ZipArchiveBuilder(Arrays.asList(root.listFiles()), new File(
                        root.getParent() + File.separator + root.getName() + ".zip"));
                ResultFile result = new ResultFile(archiveBuilder.buildArchive(), createFileName(
                    request.getIdentifier(), contentVersion.getVersion()));
                Profiler.stop("preparing zip archive");

                Profiler.dump();

                return result;
            } catch (DataStorageException e) {
                throw new FetchingException("Could not fetch files from the data store.", e);
            } catch (ArchiverException e) {
                throw new FetchingException("Could not prepare archive with the files.", e);
            }
        } else {
            throw new FileNotFoundException("Digital object " + request.getIdentifier() + " version number "
                    + contentVersion.getVersion() + " has no main file.");
        }
    }


    @Override
    public ResultFile getMetadata(String identifier, Integer version, boolean provided)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException {

        Profiler.profile("Metadata fetch request");

        ContentVersion contentVersion = objectFinder.getObjectsVersion(identifier, version);
        if (contentVersion.getExtractedMetadata() != null
                || (provided && !contentVersion.getProvidedMetadata().isEmpty())) {
            try {

                Profiler.start("fetching object metadata");
                File root = new File(contentFetcher.fetchMetadataFiles(contentVersion, provided));
                Profiler.stop("fetching object metadata");

                Profiler.start("preparing zip archive");
                ArchiveBuilder archiveBuilder = new ZipArchiveBuilder(Arrays.asList(root.listFiles()), new File(
                        root.getParent() + File.separator + root.getName() + ".zip"));
                ResultFile result = new ResultFile(archiveBuilder.buildArchive(), createFileName(identifier,
                    contentVersion.getVersion()));
                Profiler.stop("preparing zip archive");

                Profiler.dump();

                return result;
            } catch (DataStorageException e) {
                throw new FetchingException("Could not fetch files from the data store.", e);
            } catch (ArchiverException e) {
                throw new FetchingException("Could not prepare archive with the files.", e);
            }
        } else {
            throw new FileNotFoundException("Digital object " + identifier + " version number "
                    + contentVersion.getVersion() + " has no metadata files.");
        }
    }


    @Override
    public String getMetsForObject(String identifier, Integer version)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException {
        Profiler.profile("Metadata reading request");
        String metsContent = "[no content]";
        ContentVersion contentVersion = objectFinder.getObjectsVersion(identifier, version);
        if (contentVersion.getExtractedMetadata() != null || !contentVersion.getProvidedMetadata().isEmpty()) {
            try {

                Profiler.start("reading object metadata");
                InputStream inputStream = contentFetcher.getMetadataFile(contentVersion);
                metsContent = IOUtils.toString(inputStream, "UTF-8");
                Profiler.stop("reading object metadata");
                Profiler.dump();

            } catch (DataStorageException e) {
                throw new FetchingException("Could not read METS file from the data store.", e);
            } catch (IOException e) {
                throw new FetchingException("Could not read METS from the data store.", e);
            }
        } else {
            throw new FileNotFoundException("Digital object " + identifier + " version number "
                    + contentVersion.getVersion() + " has no metadata files.");
        }
        return metsContent;
    }


    @Override
    public MetadataSectionsCollection getMetsMetadataSection(String identifier, String mid)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException {
        Profiler.profile("Metadata reading request");
        MetadataSectionsCollection result = new MetadataSectionsCollection();

        ContentVersion contentVersion = objectFinder.getObjectsVersion(identifier, null);
        if (contentVersion.getExtractedMetadata() != null || !contentVersion.getProvidedMetadata().isEmpty()) {
            try {

                Profiler.start("reading object metadata");
                InputStream inputStream = contentFetcher.getMetadataFile(contentVersion);
                Profiler.stop("reading object metadata");
                Profiler.dump();
                MetsMetadataReader metsReader = MetsMetadataReaderFactory.getInstance().getMetsMetadataReader();
                try {
                    metsReader.parse(inputStream);
                    List<Object> list = metsReader.findFileAdmSectionById(mid);
                    result = parseToMetsMetadataCollection(list);
                } catch (MetsMetadataProcessingException e) {
                    logger.error("[METS parsing error!]", e.toString());
                    throw new FetchingException("Could not parse METS file from the data store.", e);
                }

            } catch (DataStorageException e) {
                throw new FetchingException("Could not read METS file from the data store.", e);
            }
        } else {
            throw new FileNotFoundException("Digital object " + identifier + " version number "
                    + contentVersion.getVersion() + " has no metadata files.");
        }
        return result;
    }


    @Override
    public InputStream getMetadataSectionById(String eid, String mid)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException {
        return getMetadataSectionById(eid, mid, null, null);
    }


    @Override
    public InputStream getMetadataSectionById(String eid, String mid, Integer vid)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException {
        return getMetadataSectionById(eid, mid, vid, null);
    }


    @Override
    public InputStream getMetadataSectionById(String eid, String mid, Integer vid, String fid)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException {
        Profiler.profile("Metadata reading request");

        MdSecType mdSecType = null;
        InputStream result = null;
        ContentVersion contentVersion = objectFinder.getObjectsVersion(eid, vid);
        if (contentVersion.getExtractedMetadata() != null || !contentVersion.getProvidedMetadata().isEmpty()) {
            try {

                Profiler.start("reading object METS file");
                InputStream inputStream = contentFetcher.getMetadataFile(contentVersion);
                Profiler.stop("reading object METS file");
                Profiler.dump();
                MetsMetadataReader metsReader = MetsMetadataReaderFactory.getInstance().getMetsMetadataReader();
                try {
                    metsReader.parse(inputStream);
                    mdSecType = (fid == null) ? metsReader.findMetadataSectionById(mid) : metsReader
                            .findMetadataSectionByFileIdAndMid(fid, mid);
                } catch (MetsMetadataProcessingException e) {
                    logger.error("[METS parsing error!]", e.toString());
                    throw new FetchingException("Could not parse METS file from the data store.", e);
                }
                if (mdSecType == null) {
                    logger.error("[METS section not found.]");
                    throw new FetchingException("Could not find metadata section from METS.");
                }
                if (mdSecType.getMdRef() != null) {
                    MetadataFile file = objectFinder.getMetadataFileFromVersion(mdSecType.getMdRef().getHref(),
                        contentVersion);
                    Profiler.start("reading object metadata");
                    result = contentFetcher.getMetadataFile(file, contentVersion);
                    Profiler.stop("reading object metadata");
                    Profiler.dump();
                } else if (mdSecType.getMdWrap() != null) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    try {
                        metsReader.getMarshaller().marshal(mdSecType.getMdWrap().getXmlData(), bos);
                        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                        result = bis;
                        bis.close();
                    } catch (JAXBException e) {
                        logger.error("[METS parsing error!]", e.toString());
                        throw new FetchingException("Could not marshall metadata section from METS.", e);
                    } catch (IOException e) {
                        logger.error("[METS parsing error!]", e.toString());
                        throw new FetchingException("Could not read metadata section from METS.", e);
                    }
                }

            } catch (DataStorageException e) {
                throw new FetchingException("Could not read METS file from the data store.", e);
            }
        } else {
            throw new FileNotFoundException("Digital object " + eid + " version number " + contentVersion.getVersion()
                    + " has no metadata files.");
        }
        return result;
    }


    @Override
    public Map<String, String> getFilesIdLocationMap(String identifier)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException {
        return getFilesIdLocationMap(identifier, null);
    }


    @Override
    public Map<String, String> getFilesIdLocationMap(String identifier, Integer vid)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException {
        Profiler.profile("Metadata reading request");
        Map<String, String> result = new HashMap<String, String>();

        ContentVersion contentVersion = objectFinder.getObjectsVersion(identifier, vid);
        if (contentVersion.getExtractedMetadata() != null || !contentVersion.getProvidedMetadata().isEmpty()) {
            try {

                Profiler.start("reading object metadata");
                InputStream inputStream = contentFetcher.getMetadataFile(contentVersion);
                Profiler.stop("reading object metadata");
                Profiler.dump();
                MetsMetadataReader metsReader = MetsMetadataReaderFactory.getInstance().getMetsMetadataReader();
                try {
                    metsReader.parse(inputStream);
                    result = metsReader.getFilesLocationMap();
                } catch (MetsMetadataProcessingException e) {
                    logger.error("[METS parsing error!]", e.toString());
                    throw new FetchingException("Could not parse METS file from the data store.", e);
                }

            } catch (DataStorageException e) {
                throw new FetchingException("Could not read METS file from the data store.", e);
            }
        } else {
            throw new FileNotFoundException("Digital object " + identifier + " version number "
                    + contentVersion.getVersion() + " has no metadata files.");
        }
        return result;
    }


    /**
     * Try parse list of objects to mets metadata collection.
     * 
     * @param list
     *            list of objects.
     * @return a String representing the filename.
     */
    private MetadataSectionsCollection parseToMetsMetadataCollection(List<Object> list) {
        Iterator<Object> it = list.iterator();
        List<AmdSecType> amdList = new LinkedList<AmdSecType>();
        List<MdSecType> mdList = new LinkedList<MdSecType>();
        while (it.hasNext()) {
            Object object = (Object) it.next();
            if (object instanceof AmdSecType) {
                amdList.add((AmdSecType) object);
            }
            if (object instanceof MdSecType) {
                mdList.add((MdSecType) object);
            }
        }

        MetadataSectionsCollection result = new MetadataSectionsCollection(amdList, mdList);
        return result;
    }


    /**
     * Creates new filename for specified object's version.
     * 
     * @param identifier
     *            object's identifier.
     * @param versionNumber
     *            version number.
     * @return a String representing the filename.
     */
    private String createFileName(String identifier, Integer versionNumber) {
        return identifier.replace(":", "-") + "_v" + versionNumber + ".zip";
    }


    /**
     * Clears the cache paths from resources stored in JPA cache.
     * 
     * @param version
     *            target to rid of the cache paths.
     */
    private void clearCachePaths(ContentVersion version) {
        if (version.getExtractedMetadata() != null) {
            version.getExtractedMetadata().setCachePath(null);
        }

        for (ObjectProvidedMetadata metadata : version.getProvidedMetadata()) {
            metadata.setCachePath(null);
        }

        for (DataFileVersion file : version.getFiles()) {

            file.getDataFile().setCachePath(null);

            for (FileExtractedMetadata metadata : file.getDataFile().getExtractedMetadata()) {
                metadata.setCachePath(null);
            }

            for (FileProvidedMetadata metadata : file.getProvidedMetadata()) {
                metadata.setCachePath(null);
            }
        }
    }

}
