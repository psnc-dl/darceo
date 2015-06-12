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
import gov.loc.mets.MdSecType.MdRef;
import gov.loc.mets.MdSecType.MdWrap;
import gov.loc.mets.MdSecType.MdWrap.XmlData;
import gov.loc.mets.Mets;
import gov.loc.mets.MetsType.FileSec;
import gov.loc.mets.MetsType.FileSec.FileGrp;
import gov.loc.mets.MetsType.MetsHdr;
import gov.loc.mets.MetsType.MetsHdr.AltRecordID;
import info.lc.xmlns.premis_v2.EventComplexType;
import info.lc.xmlns.premis_v2.EventIdentifierComplexType;
import info.lc.xmlns.premis_v2.EventOutcomeDetailComplexType;
import info.lc.xmlns.premis_v2.EventOutcomeInformationComplexType;
import info.lc.xmlns.premis_v2.ObjectComplexType;
import info.lc.xmlns.premis_v2.ObjectIdentifierComplexType;
import info.lc.xmlns.premis_v2.PremisComplexType;
import info.lc.xmlns.premis_v2.RelatedEventIdentificationComplexType;
import info.lc.xmlns.premis_v2.RelatedObjectIdentificationComplexType;
import info.lc.xmlns.premis_v2.RelationshipComplexType;
import info.lc.xmlns.premis_v2.Representation;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import pl.psnc.synat.wrdz.common.metadata.adm.PremisConsts;
import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;

/**
 * Builder of metadata of a digital object in the METS schema with the usage of the PREMIS schema (in a scope of
 * provenance of an object).
 */
public class MetsMetadataBuilder {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(MetsMetadataBuilder.class);

    /**
     * Date formatter compatible with the xs:dateTime format.
     */
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Date type factory for converting dates into XML Gregorian Calendar instances.
     */
    private final DatatypeFactory datatypeFactory;

    /**
     * Marshaller of the METS metadata.
     */
    private final Marshaller marshaller;

    /**
     * Unmarshaller of the METS metadata.
     */
    private final Unmarshaller unmarshaller;

    /**
     * Document builder factory for parsing XML to DOM.
     */
    private final DocumentBuilderFactory domBuilderFactory;

    /**
     * Generators of ids for METS metadata sections.
     */
    private MetsIdGenerators idGenerators;

    /**
     * Mets root object.
     */
    private Mets mets;

    /**
     * Id of the object administrative section with digital provenance metadata in the PREMIS schema.
     */
    private String premisAdmId;

    /**
     * Id of the object digital provenance section in the PREMIS schema in the administrative section at identifier
     * <code>premisAdmId</code>.
     */
    private String premisDpId;

    /**
     * Id of the file group section for a content of a digital object.
     */
    private String contentFgId;

    /**
     * Map of data files and its ids with administrative sections of metadata.
     */
    private Map<String, String> dataFileMap;

    /**
     * Last found FileTypeSec.
     */
    private FileType lastFileTypeSec;


    /**
     * Constructs builder of METS metadata.
     * 
     * @param jaxbContext
     *            JAXB context for the METS classes
     * @param datatypeFactory
     *            factory for converting dates into XML Gregorian Calendar instances.
     * @param domBuilderFactory
     *            factory for parsing XML to DOM.
     */
    public MetsMetadataBuilder(JAXBContext jaxbContext, DatatypeFactory datatypeFactory,
            DocumentBuilderFactory domBuilderFactory) {
        try {
            this.marshaller = jaxbContext.createMarshaller();
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            this.marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            this.marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, MetsConsts.METS_NAMESPACE_URI + " "
                    + MetsConsts.METS_SCHEMA_LOCATION);
        } catch (JAXBException e) {
            logger.error("JAXB - METS marshaller creation failed.", e);
            throw new RuntimeException(e);
        }
        try {
            this.unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            logger.error("JAXB - METS unmarshaller creation failed.", e);
            throw new RuntimeException(e);
        }
        this.datatypeFactory = datatypeFactory;
        this.domBuilderFactory = domBuilderFactory;
        this.idGenerators = new MetsIdGenerators();
        this.premisAdmId = null;
        this.premisDpId = null;
        this.dataFileMap = new HashMap<String, String>();
        this.mets = new Mets();
    }


    /**
     * Constructs builder of METS metadata.
     * 
     * @param jaxbContext
     *            JAXB context for the METS classes
     * @param datatypeFactory
     *            factory for converting dates into XML Gregorian Calendar instances.
     * @param domBuilderFactory
     *            factory for parsing XML to DOM.
     * @param tmpMetsFileUri
     *            URI to tmpMets file.
     */
    public MetsMetadataBuilder(JAXBContext jaxbContext, DatatypeFactory datatypeFactory,
            DocumentBuilderFactory domBuilderFactory, URI tmpMetsFileUri) {
        try {
            this.marshaller = jaxbContext.createMarshaller();
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            this.marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            this.marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, MetsConsts.METS_NAMESPACE_URI + " "
                    + MetsConsts.METS_SCHEMA_LOCATION);
        } catch (JAXBException e) {
            logger.error("JAXB - METS marshaller creation failed.", e);
            throw new RuntimeException(e);
        }
        try {
            this.unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            logger.error("JAXB - METS unmarshaller creation failed.", e);
            throw new RuntimeException(e);
        }
        this.datatypeFactory = datatypeFactory;
        this.domBuilderFactory = domBuilderFactory;
        this.idGenerators = new MetsIdGenerators();
        this.premisAdmId = null;
        this.premisDpId = null;
        this.dataFileMap = new HashMap<String, String>();

        try {
            File tmpMetsFile = new File(tmpMetsFileUri.getPath());
            this.mets = (Mets) unmarshaller.unmarshal(tmpMetsFile);
        } catch (JAXBException e) {
            logger.error("JAXB - METS unmarshaller - tmpMets file unmarshalling failed.", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Build METS metadata.
     * 
     * @return METS metadata
     * @throws MetsMetadataProcessingException
     *             when some problem with building of the XML with METS metadata occurs
     */
    public MetsMetadata build()
            throws MetsMetadataProcessingException {
        StringWriter xmlWriter = new StringWriter();
        xmlWriter.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        try {
            marshaller.marshal(mets, xmlWriter);
        } catch (JAXBException e) {
            logger.error("Building XML with metadata failed.", e);
            throw new MetsMetadataProcessingException(e);
        }
        return new MetsMetadata(xmlWriter.toString());
    }


    /**
     * Sets the main identifier of a digital object.
     * 
     * @param identifier
     *            identifier
     * @param type
     *            identifier type
     * @return this builder
     */
    public MetsMetadataBuilder setObjectIdentifier(String identifier, String type) {
        mets.setOBJID(identifier);
        Representation objSec = getPremisRepresentationSection();
        ObjectIdentifierComplexType objectIdSec = new ObjectIdentifierComplexType();
        objectIdSec.setObjectIdentifierType(type);
        objectIdSec.setObjectIdentifierValue(identifier);
        objSec.getObjectIdentifier().add(objectIdSec);
        return this;
    }


    /**
     * Sets the digital object type.
     * 
     * @param type
     *            type of an object
     * @return this builder
     */
    public MetsMetadataBuilder setObjectType(String type) {
        mets.setTYPE(type);
        return this;
    }


    /**
     * Sets the version number of an object.
     * 
     * @param version
     *            version number
     * @return this builder
     */
    public MetsMetadataBuilder setVersionNumber(Integer version) {
        MetsHdr header = getHeaderSection();
        header.setRECORDSTATUS(MetsConsts.METS_OBJECT_VERSION_STATUS + version.toString());
        return this;
    }


    /**
     * Adds an alternative identifier of a digital object.
     * 
     * @param type
     *            type of an alternative identifier
     * @param identifier
     *            alternative identifier
     * @return this builder
     */
    public MetsMetadataBuilder addAlternativeObjectIdentifier(String type, String identifier) {
        MetsHdr header = getHeaderSection();
        AltRecordID altIdentifier = new AltRecordID();
        altIdentifier.setTYPE(type);
        altIdentifier.setValue(identifier);
        header.getAltRecordID().add(altIdentifier);
        return this;
    }


    /**
     * Sets an object creation date.
     * 
     * @param date
     *            object creation date
     * @return this builder
     */
    public MetsMetadataBuilder setCreationDate(Date date) {
        MetsHdr header = getHeaderSection();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(date.getTime());
        header.setCREATEDATE(datatypeFactory.newXMLGregorianCalendar(gc));
        return this;
    }


    /**
     * Sets an object modification date.
     * 
     * @param date
     *            object modification date
     * @return this builder
     */
    public MetsMetadataBuilder setModificationDate(Date date) {
        MetsHdr header = getHeaderSection();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(date.getTime());
        header.setLASTMODDATE(datatypeFactory.newXMLGregorianCalendar(gc));
        return this;
    }


    /**
     * Sets the object creation or modification event.
     * 
     * @param date
     *            date of event
     * @param version
     *            version number
     * @param updatedFiles
     *            list of files updated within this version
     * @return this builder
     */
    public MetsMetadataBuilder setObjectModificationEvent(Date date, Integer version, List<String> updatedFiles) {
        EventOutcomeInformationComplexType eventOutcomeInformation = new EventOutcomeInformationComplexType();
        eventOutcomeInformation.getContent().add(
            new JAXBElement<String>(new QName(PremisConsts.PREMIS_NAMESPACE_URI, "eventOutcome",
                    PremisConsts.PREMIS_PREFIX), String.class, "successfully saved"));
        if (updatedFiles != null && updatedFiles.size() > 0) {
            for (String file : updatedFiles) {
                EventOutcomeDetailComplexType eventOutcomeDetail = new EventOutcomeDetailComplexType();
                eventOutcomeDetail.setEventOutcomeDetailNote(file);
                eventOutcomeInformation.getContent().add(
                    new JAXBElement<EventOutcomeDetailComplexType>(new QName(PremisConsts.PREMIS_NAMESPACE_URI,
                            "eventOutcomeDetail", PremisConsts.PREMIS_PREFIX), EventOutcomeDetailComplexType.class,
                            eventOutcomeDetail));
            }
        }
        EventComplexType evtSec = getNewPremisEventSection();
        if (version.equals(1)) {
            evtSec.setEventType(MetsConsts.PREMIS_CREATION_EVENT_TYPE);
        } else {
            evtSec.setEventType(MetsConsts.PREMIS_MODIFICATION_EVENT_TYPE);
        }
        evtSec.setEventDetail(MetsConsts.METS_OBJECT_VERSION_STATUS + version.toString());
        evtSec.setEventDateTime(DATE_FORMATTER.format(date));
        evtSec.getEventOutcomeInformation().add(eventOutcomeInformation);
        return this;
    }


    /**
     * Sets an object origin. The source object is in the same local repository.
     * 
     * @param identifier
     *            identifier of the source object
     * @param migrationType
     *            type of migration
     * @param date
     *            date of migration
     * @param info
     *            info about migration
     * @return this builder
     */
    public MetsMetadataBuilder setLocalObjectOrigin(String identifier, String migrationType, Date date, String info) {
        RelatedObjectIdentificationComplexType sourceId = new RelatedObjectIdentificationComplexType();
        sourceId.setRelatedObjectIdentifierType("local system identifier");
        sourceId.setRelatedObjectIdentifierValue(identifier);
        return setObjectOrigin(sourceId, migrationType, date, info);
    }


    /**
     * Sets an object origin. The source object is in some other remote repository.
     * 
     * @param identifier
     *            identifier of the source object
     * @param identifierResolver
     *            URL of an identifier resolver
     * @param migrationType
     *            type of migration
     * @param date
     *            date of migration
     * @param info
     *            info about migration
     * @return this builder
     */
    public MetsMetadataBuilder setRemoteObjectOrigin(String identifier, String migrationType,
            String identifierResolver, Date date, String info) {
        RelatedObjectIdentificationComplexType sourceId = new RelatedObjectIdentificationComplexType();
        String sourceIdType = "remote system identifier";
        if (identifierResolver != null) {
            sourceIdType += " (identifier resolver: " + identifierResolver + ")";
        }
        sourceId.setRelatedObjectIdentifierType(sourceIdType);
        sourceId.setRelatedObjectIdentifierValue(identifier);
        return setObjectOrigin(sourceId, migrationType, date, info);
    }


    /**
     * Sets the object origin.
     * 
     * @param sourceId
     *            source id section with filled identifier.
     * @param migrationType
     *            type of migration
     * @param date
     *            date of migration
     * @param info
     *            info about migration
     * @return this builder
     */
    private MetsMetadataBuilder setObjectOrigin(RelatedObjectIdentificationComplexType sourceId, String migrationType,
            Date date, String info) {
        Representation premisObjSec = getPremisRepresentationSection();
        RelationshipComplexType relationship = new RelationshipComplexType();
        relationship.setRelationshipType(MetsConsts.PREMIS_RELATIONSHIP_DERIVATION_TYPE);
        relationship.setRelationshipSubType(MetsConsts.PREMIS_RELATIONSHIP_HAS_SOURCE_SUBTYPE);
        relationship.getRelatedObjectIdentification().add(sourceId);
        premisObjSec.getRelationship().add(relationship);
        EventComplexType evtSec = getNewPremisEventSection();
        evtSec.setEventType(migrationType);
        if (date != null) {
            evtSec.setEventDateTime(DATE_FORMATTER.format(date));
        }
        if (info != null) {
            EventOutcomeInformationComplexType eventOutcomeInformation = new EventOutcomeInformationComplexType();
            eventOutcomeInformation.getContent().add(
                new JAXBElement<String>(new QName(PremisConsts.PREMIS_NAMESPACE_URI, "eventOutcome",
                        PremisConsts.PREMIS_PREFIX), String.class, info));
            evtSec.getEventOutcomeInformation().add(eventOutcomeInformation);
        }
        RelatedEventIdentificationComplexType evtIdf = new RelatedEventIdentificationComplexType();
        evtIdf.setRelatedEventIdentifierType(evtSec.getEventIdentifier().getEventIdentifierType());
        evtIdf.setRelatedEventIdentifierValue(evtSec.getEventIdentifier().getEventIdentifierValue());
        relationship.getRelatedEventIdentification().add(evtIdf);
        return this;
    }


    /**
     * Sets an object derivative. The result object is in the same local repository.
     * 
     * @param identifier
     *            identifier of the result object
     * @param migrationType
     *            type of migration
     * @param date
     *            date of migration
     * @param info
     *            info about migration
     * @return this builder
     */
    public MetsMetadataBuilder setLocalObjectDerivative(String identifier, String migrationType, Date date, String info) {
        RelatedObjectIdentificationComplexType resultId = new RelatedObjectIdentificationComplexType();
        resultId.setRelatedObjectIdentifierType("local system identifier");
        resultId.setRelatedObjectIdentifierValue(identifier);
        return setObjectDerivative(resultId, migrationType, date, info);
    }


    /**
     * Sets an object derivative. The result object is in some other remote repository.
     * 
     * @param identifier
     *            identifier of the result object
     * @param identifierResolver
     *            URL of an identifier resolver
     * @param migrationType
     *            type of migration
     * @param date
     *            date of migration
     * @param info
     *            info about migration
     * @return this builder
     */
    public MetsMetadataBuilder setRemoteObjectDerivative(String identifier, String migrationType,
            String identifierResolver, Date date, String info) {
        RelatedObjectIdentificationComplexType resultId = new RelatedObjectIdentificationComplexType();
        String resultIdType = "remote system identifier";
        if (identifierResolver != null) {
            resultIdType += " (identifier resolver: " + identifierResolver + ")";
        }
        resultId.setRelatedObjectIdentifierType(resultIdType);
        resultId.setRelatedObjectIdentifierValue(identifier);
        return setObjectDerivative(resultId, migrationType, date, info);
    }


    /**
     * Sets the object derivative.
     * 
     * @param resultId
     *            result id section with filled identifier.
     * @param migrationType
     *            type of migration
     * @param date
     *            date of migration
     * @param info
     *            info about migration
     * @return this builder
     */
    private MetsMetadataBuilder setObjectDerivative(RelatedObjectIdentificationComplexType resultId,
            String migrationType, Date date, String info) {
        Representation premisObjSec = getPremisRepresentationSection();
        RelationshipComplexType relationship = new RelationshipComplexType();
        relationship.setRelationshipType(MetsConsts.PREMIS_RELATIONSHIP_DERIVATION_TYPE);
        relationship.setRelationshipSubType(MetsConsts.PREMIS_RELATIONSHIP_IS_SOURCE_OF_SUBTYPE);
        relationship.getRelatedObjectIdentification().add(resultId);
        premisObjSec.getRelationship().add(relationship);
        EventComplexType evtSec = getNewPremisEventSection();
        evtSec.setEventType(migrationType);
        if (date != null) {
            evtSec.setEventDateTime(DATE_FORMATTER.format(date));
        }
        if (info != null) {
            EventOutcomeInformationComplexType eventOutcomeInformation = new EventOutcomeInformationComplexType();
            eventOutcomeInformation.getContent().add(
                new JAXBElement<String>(new QName(PremisConsts.PREMIS_NAMESPACE_URI, "eventOutcome",
                        PremisConsts.PREMIS_PREFIX), String.class, info));
            evtSec.getEventOutcomeInformation().add(eventOutcomeInformation);
        }
        RelatedEventIdentificationComplexType evtIdf = new RelatedEventIdentificationComplexType();
        evtIdf.setRelatedEventIdentifierType(evtSec.getEventIdentifier().getEventIdentifierType());
        evtIdf.setRelatedEventIdentifierValue(evtSec.getEventIdentifier().getEventIdentifierValue());
        relationship.getRelatedEventIdentification().add(evtIdf);
        return this;
    }


    /**
     * Adds gloabal metadata for digital object (as reference to a file with metadata in the context of digital object).
     * 
     * @param status
     *            status of metadata (EXTRACTED or PROVIDED)
     * @param type
     *            namespace type (= metadata type)
     * @param metadataPath
     *            path to the file with metadata in the context of digital object
     * @param metadataLabel
     *            filename with metadata
     * @return this builder
     */
    public MetsMetadataBuilder addMetadataForObjectByLink(String status, NamespaceType type, String metadataPath,
            String metadataLabel) {
        MdSecType mdSec = getNewMdSecForObject(status, type);
        mdSec.setMdRef(getNewMdRefSection(type, metadataPath, metadataLabel));
        return this;
    }


    /**
     * Adds gloabal metadata for digital object (inline in METS metadata). These metadata are read from the file.
     * 
     * @param status
     *            status of metadata (EXTRACTED or PROVIDED)
     * @param type
     *            namespace type (= metadata type)
     * @param metadataFile
     *            file with the xml to inline
     * @param metadataLabel
     *            filename with metadata
     * @return this builder
     */
    public MetsMetadataBuilder addMetadataForObjectInline(String status, NamespaceType type, File metadataFile,
            String metadataLabel) {
        MdSecType mdSec = getNewMdSecForObject(status, type);
        mdSec.setMdWrap(getNewMdWrapSection(type, metadataFile, metadataLabel));
        return this;
    }


    /**
     * Adds gloabal metadata for digital object (inline in METS metadata). These metadata should be extracted from
     * passed previous METS metadata
     * 
     * @param status
     *            status of metadata (EXTRACTED or PROVIDED)
     * @param type
     *            namespace type (= metadata type)
     * @param previousMetsMetadata
     *            previous METS metadata
     * @param metadataLabel
     *            filename with metadata
     * @return this builder
     */
    public MetsMetadataBuilder addMetadataForObjectInline(String status, NamespaceType type,
            String previousMetsMetadata, String metadataLabel) {
        MdSecType mdSec = getNewMdSecForObject(status, type);
        mdSec.setMdWrap(getNewMdWrapSection(type, previousMetsMetadata, null, metadataLabel, status));
        return this;
    }


    /**
     * Gets new proper metadata section for object metadata of corresponding type.
     * 
     * @param status
     *            status of metadata (EXTRACTED or PROVIDED)
     * @param type
     *            namespace type (= metadata type)
     * @return new proper metadata section
     */
    private MdSecType getNewMdSecForObject(String status, NamespaceType type) {
        MdSecType mdSec = null;
        switch (type) {
            case DC:
            case DCTERMS:
            case MARC:
            case MARCXML:
            case MODS:
            case ETDMS:
            case PLMET:
            case OAIORE:
            case ATOM:
            case RDF:
            case OWL:
                mdSec = getNewObjectDescriptiveSection();
                break;
            case TEXTMD:
            case DOCUMENTMD:
            case NISOIMG:
            case MIX:
            case AES57:
            case VMD:
            case VIDEOMD:
                mdSec = getNewObjectTechnicalSection();
                break;
            case METS:
            case UNKNOWN:
                mdSec = getNewObjectSourceSection();
                break;
            case METSRIGHTS:
            case PREMIS_RIGHTS:
                mdSec = getNewObjectRightsSection();
                break;
            case PREMIS:
            case PREMIS_OBJECT:
            case PREMIS_EVENT:
            case PREMIS_AGENT:
                mdSec = getNewObjectDigiprovSection();
                break;
            default:
                throw new RuntimeException("Unexpected type of metadata.");
        }
        mdSec.setSTATUS(status);
        return mdSec;
    }


    /**
     * Adds metadata for a file of digital object (as reference to a file with metadata in the context of digital
     * object).
     * 
     * @param datafilePath
     *            path to the data file in the context of digital object
     * @param status
     *            status of metadata (EXTRACTED or PROVIDED)
     * @param type
     *            namespace type (= metadata type)
     * @param metadataPath
     *            path to the file with metadata in the context of digital object
     * @param metadataLabel
     *            filename with metadata
     * @return this builder
     */
    public MetsMetadataBuilder addMetadataForFileByLink(String datafilePath, String status, NamespaceType type,
            String metadataPath, String metadataLabel) {
        MdSecType mdSec = getNewMdSecForFile(datafilePath, status, type);
        mdSec.setMdRef(getNewMdRefSection(type, metadataPath, metadataLabel));
        return this;
    }


    /**
     * Adds metadata for a file of digital object (inline in METS metadata). These metadata are read from the file.
     * 
     * @param datafilePath
     *            path to the data file in the context of digital object
     * @param status
     *            status of metadata (EXTRACTED or PROVIDED)
     * @param type
     *            namespace type (= metadata type)
     * @param metadataFile
     *            file with the xml to inline
     * @param metadataLabel
     *            filename with metadata
     * @return this builder
     */
    public MetsMetadataBuilder addMetadataForFileInline(String datafilePath, String status, NamespaceType type,
            File metadataFile, String metadataLabel) {
        MdSecType mdSec = getNewMdSecForFile(datafilePath, status, type);
        mdSec.setMdWrap(getNewMdWrapSection(type, metadataFile, metadataLabel));
        return this;
    }


    /**
     * Adds metadata for a file of digital object (inline in METS metadata). These metadata should be extracted from
     * passed previous METS metadata
     * 
     * @param datafilePath
     *            path to the data file in the context of digital object
     * @param status
     *            status of metadata (EXTRACTED or PROVIDED)
     * @param type
     *            namespace type (= metadata type)
     * @param previousMetsMetadata
     *            previous METS metadata
     * @param metadataLabel
     *            filename with metadata
     * @return this builder
     */
    public MetsMetadataBuilder addMetadataForFileInline(String datafilePath, String status, NamespaceType type,
            String previousMetsMetadata, String metadataLabel) {
        MdSecType mdSec = getNewMdSecForFile(datafilePath, status, type);
        mdSec.setMdWrap(getNewMdWrapSection(type, previousMetsMetadata, datafilePath, metadataLabel, status));
        return this;
    }


    /**
     * Gets new proper metadata section for file metadata of corresponding type.
     * 
     * @param datafilePath
     *            path to the data file in the context of digital object
     * @param status
     *            status of metadata (EXTRACTED or PROVIDED)
     * @param type
     *            namespace type (= metadata type)
     * @return new proper metadata section
     */
    private MdSecType getNewMdSecForFile(String datafilePath, String status, NamespaceType type) {
        MdSecType mdSec = null;
        switch (type) {
            case TEXTMD:
            case DOCUMENTMD:
            case NISOIMG:
            case MIX:
            case AES57:
            case VMD:
            case VIDEOMD:
                mdSec = getNewFileTechnicalSection(datafilePath);
                break;
            case DC:
            case DCTERMS:
            case MARC:
            case MARCXML:
            case MODS:
            case ETDMS:
            case PLMET:
            case OAIORE:
            case ATOM:
            case RDF:
            case OWL:
            case METS:
            case UNKNOWN:
                mdSec = getNewFileSourceSection(datafilePath);
                break;
            case METSRIGHTS:
            case PREMIS_RIGHTS:
                mdSec = getNewFileRightsSection(datafilePath);
                break;
            case PREMIS:
            case PREMIS_OBJECT:
            case PREMIS_EVENT:
            case PREMIS_AGENT:
                mdSec = getNewFileDigiprovSection(datafilePath);
                break;
            default:
                throw new RuntimeException("Unexpected type of metadata.");
        }
        mdSec.setSTATUS(status);
        return mdSec;
    }


    /**
     * Gets new MdRef section.
     * 
     * @param type
     *            namespace type (= metadata type)
     * @param metadataPath
     *            path to the file with metadata in the context of digital object
     * @param metadataLabel
     *            filename with metadata
     * @return new MdRef section
     */
    private MdRef getNewMdRefSection(NamespaceType type, String metadataPath, String metadataLabel) {
        MdRef mdRef = new MdRef();
        mdRef.setLABEL(metadataLabel);
        mdRef.setHref(metadataPath);
        mdRef.setLOCTYPE(MetsConsts.METS_METADATA_LOCATION_TYPE_RELATIVE);
        if (type.equals(NamespaceType.UNKNOWN)) {
            mdRef.setMDTYPE(MetsConsts.METS_METADATA_TYPE_OTHER);
        } else {
            mdRef.setMDTYPE(type.name().replace('_', ':'));
        }
        return mdRef;
    }


    /**
     * Gets new MdWrap section.
     * 
     * @param type
     *            namespace type (= metadata type)
     * @param metadataFile
     *            file with the xml
     * @param metadataLabel
     *            filename with metadata
     * @return new MdWrap section
     */
    private MdWrap getNewMdWrapSection(NamespaceType type, File metadataFile, String metadataLabel) {
        MdWrap mdWrap = new MdWrap();
        mdWrap.setLABEL(metadataLabel);
        if (type.equals(NamespaceType.UNKNOWN)) {
            mdWrap.setMDTYPE(MetsConsts.METS_METADATA_TYPE_OTHER);
        } else {
            mdWrap.setMDTYPE(type.name().replace('_', ':'));
        }
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = domBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("Problem with creation of document builder." + e.toString());
            throw new RuntimeException(e);
        }

        Document document = null;
        Element element = null;
        try {
            document = documentBuilder.parse(new InputSource(new FileInputStream(metadataFile)));
            element = document.getDocumentElement();
            clearWhitespacesBetweenElements(element);
            XmlData xmlData = new XmlData();
            xmlData.getAny().add(element);
            mdWrap.setXmlData(xmlData);
        } catch (Exception e) {
            logger.error("Reading metadata as a XML failed. Trying as binary data." + e.toString());
            try {
                mdWrap.setBinData(IOUtils.toByteArray(new FileInputStream(metadataFile)));
            } catch (Exception f) {
                logger.error("Problem with reading metadata as binary data." + f.toString());
                throw new RuntimeException(f);
            }
        }
        return mdWrap;
    }


    /**
     * Gets new MdWrap section.
     * 
     * @param type
     *            namespace type (= metadata type)
     * @param previousMetsMetadata
     *            previous METS metadata
     * @param datafilePath
     *            object ralative path of data file to which metadata belongs or null in the case of object metadata
     * @param metadataLabel
     *            filename with metadata
     * @param metadataStatus
     *            status of metadata (EXTRACTED or PROVIDED)
     * @return new MdWrap section
     */
    private MdWrap getNewMdWrapSection(NamespaceType type, String previousMetsMetadata, String datafilePath,
            String metadataLabel, String metadataStatus) {
        String metadataType = type.equals(NamespaceType.UNKNOWN) ? MetsConsts.METS_METADATA_TYPE_OTHER : type.name()
                .replace('_', ':');
        Mets prevMets = null;
        try {
            prevMets = (Mets) unmarshaller.unmarshal(new StringReader(previousMetsMetadata));
        } catch (Exception e) {
            logger.error("Problem with reading previuos version of METS metadata." + e.toString());
            throw new RuntimeException(e);
        }
        if (datafilePath == null) {
            for (MdSecType dmdSec : prevMets.getDmdSec()) {
                if (matchMdSection(dmdSec, metadataStatus, metadataLabel, metadataType)) {
                    return dmdSec.getMdWrap();
                }
            }
            for (AmdSecType admSec : prevMets.getAmdSec()) {
                for (MdSecType techSec : admSec.getTechMD()) {
                    if (matchMdSection(techSec, metadataStatus, metadataLabel, metadataType)) {
                        return techSec.getMdWrap();
                    }
                }
                for (MdSecType sourceSec : admSec.getSourceMD()) {
                    if (matchMdSection(sourceSec, metadataStatus, metadataLabel, metadataType)) {
                        return sourceSec.getMdWrap();
                    }
                }
                for (MdSecType rightsSec : admSec.getRightsMD()) {
                    if (matchMdSection(rightsSec, metadataStatus, metadataLabel, metadataType)) {
                        return rightsSec.getMdWrap();
                    }
                }
                for (MdSecType digiprovSec : admSec.getDigiprovMD()) {
                    if (matchMdSection(digiprovSec, metadataStatus, metadataLabel, metadataType)) {
                        return digiprovSec.getMdWrap();
                    }
                }
            }
        } else {
            for (FileGrp fileGrp : prevMets.getFileSec().getFileGrp()) {
                for (FileType file : fileGrp.getFile()) {
                    if (file.getFLocat().size() == 1) {
                        if (file.getFLocat().get(0).getHref().equals(datafilePath)) {
                            if (file.getADMID().size() == 1) {
                                AmdSecType admSec = (AmdSecType) file.getADMID().get(0);
                                for (MdSecType techSec : admSec.getTechMD()) {
                                    if (matchMdSection(techSec, metadataStatus, metadataLabel, metadataType)) {
                                        return techSec.getMdWrap();
                                    }
                                }
                                for (MdSecType sourceSec : admSec.getSourceMD()) {
                                    if (matchMdSection(sourceSec, metadataStatus, metadataLabel, metadataType)) {
                                        return sourceSec.getMdWrap();
                                    }
                                }
                                for (MdSecType rightsSec : admSec.getRightsMD()) {
                                    if (matchMdSection(rightsSec, metadataStatus, metadataLabel, metadataType)) {
                                        return rightsSec.getMdWrap();
                                    }
                                }
                                for (MdSecType digiprovSec : admSec.getDigiprovMD()) {
                                    if (matchMdSection(digiprovSec, metadataStatus, metadataLabel, metadataType)) {
                                        return digiprovSec.getMdWrap();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        throw new RuntimeException("No previus metadata found in previuos version of METS metadata");
    }


    /**
     * Match existing metadata section with metadata attributes.
     * 
     * @param mdSec
     *            existing metadata section
     * @param metadataStatus
     *            status of metadata (EXTRACTED or PROVIDED)
     * @param metadataLabel
     *            filename with metadata
     * @param metadataType
     *            metadata type
     * @return whether metadata section matches to metadata attributes
     */
    private boolean matchMdSection(MdSecType mdSec, String metadataStatus, String metadataLabel, String metadataType) {
        if (mdSec.getSTATUS().equals(metadataStatus)) {
            if (mdSec.getMdWrap().getLABEL().equals(metadataLabel)
                    && mdSec.getMdWrap().getMDTYPE().equals(metadataType)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Clears whitespaces between elements in XML document node. JAXB cannot do it because it does not known all context
     * - we don't restrict aou system to specific XML metadata namespaces.
     * 
     * @param node
     *            node for which clear whitespaces
     */
    private void clearWhitespacesBetweenElements(Node node) {
        if (node.hasChildNodes()) {
            NodeList childNodes = node.getChildNodes();
            if (!(childNodes.getLength() == 1 && childNodes.item(0).getNodeType() != Node.ELEMENT_NODE)) {
                for (int i = 0; i < childNodes.getLength(); i++) {
                    clearWhitespacesBetweenElements(childNodes.item(i));
                }
            }
        } else {
            node.setNodeValue("");
        }
    }


    /**
     * Adds data file of digital object.
     * 
     * @param datafilePath
     *            path to the data file in the context of digital object
     * @param seq
     *            sequence property value; can be <code>null</code>
     * @return this builder
     */
    public MetsMetadataBuilder addDataFile(String datafilePath, Integer seq) {
        FileGrp fileGrpSec = getContentFileGroupSection();
        FileType file = new FileType();
        file.setID(idGenerators.getFileId(contentFgId));
        file.setSEQ(seq);
        if (dataFileMap.containsKey(datafilePath)) {
            String admFileId = dataFileMap.get(datafilePath);
            for (AmdSecType admSec : mets.getAmdSec()) {
                if (admSec.getID().equals(admFileId)) {
                    file.getADMID().add(admSec);
                    break;
                }
            }
        }
        FLocat fLocat = new FLocat();
        fLocat.setHref(datafilePath);
        fLocat.setLOCTYPE(MetsConsts.METS_DATA_LOCATION_TYPE_RELATIVE);
        file.getFLocat().add(fLocat);
        fileGrpSec.getFile().add(file);
        return this;
    }


    /**
     * Gets the header section of mets metadata.
     * 
     * @return header section
     */
    private MetsHdr getHeaderSection() {
        MetsHdr header = mets.getMetsHdr();
        if (header != null) {
            return header;
        }
        header = new MetsHdr();
        mets.setMetsHdr(header);
        return header;
    }


    /**
     * Gets a new descriptive section of mets metadata (object metadata).
     * 
     * @return descriptive section
     */
    private MdSecType getNewObjectDescriptiveSection() {
        MdSecType dmdSec = new MdSecType();
        dmdSec.setID(idGenerators.getObjectDmdId());
        mets.getDmdSec().add(dmdSec);
        return dmdSec;
    }


    /**
     * Gets a new technical section in the a administrative section of mets metadata (object metadata).
     * 
     * @return technical section
     */
    private MdSecType getNewObjectTechnicalSection() {
        AmdSecType admSec = getNewObjectAdministrativeSection();
        MdSecType techSec = new MdSecType();
        techSec.setID(idGenerators.getTechId(admSec.getID()));
        admSec.getTechMD().add(techSec);
        return techSec;
    }


    /**
     * Gets a new source section in the a administrative section of mets metadata (object metadata).
     * 
     * @return source section
     */
    private MdSecType getNewObjectSourceSection() {
        AmdSecType admSec = getNewObjectAdministrativeSection();
        MdSecType sourceSec = new MdSecType();
        sourceSec.setID(idGenerators.getSourceId(admSec.getID()));
        admSec.getSourceMD().add(sourceSec);
        return sourceSec;
    }


    /**
     * Gets a new rights section in the a administrative section of mets metadata (object metadata).
     * 
     * @return source section
     */
    private MdSecType getNewObjectRightsSection() {
        AmdSecType admSec = getNewObjectAdministrativeSection();
        MdSecType rightsSec = new MdSecType();
        rightsSec.setID(idGenerators.getRightsId(admSec.getID()));
        admSec.getRightsMD().add(rightsSec);
        return rightsSec;
    }


    /**
     * Gets a new digital provenance section in the a administrative section of mets metadata (object metadata).
     * 
     * @return digital provenance
     */
    private MdSecType getNewObjectDigiprovSection() {
        AmdSecType admSec = getNewObjectAdministrativeSection();
        MdSecType digiprovSec = new MdSecType();
        digiprovSec.setID(idGenerators.getDigiprovId(admSec.getID()));
        admSec.getDigiprovMD().add(digiprovSec);
        return digiprovSec;
    }


    /**
     * Gets a new administrative section of mets metadata (object metadata).
     * 
     * @return descriptive section
     */
    private AmdSecType getNewObjectAdministrativeSection() {
        AmdSecType admSec = new AmdSecType();
        admSec.setID(idGenerators.getObjectAdmId());
        mets.getAmdSec().add(admSec);
        return admSec;
    }


    /**
     * Gets a new technical section in the a administrative section for specified data file.
     * 
     * @param datafilePath
     *            object relative path to datafile
     * @return technical section
     */
    private MdSecType getNewFileTechnicalSection(String datafilePath) {
        AmdSecType admSec = getFileAdministrativeSection(datafilePath);
        MdSecType techSec = new MdSecType();
        techSec.setID(idGenerators.getTechId(admSec.getID()));
        admSec.getTechMD().add(techSec);
        return techSec;
    }


    /**
     * Gets a new source section in the a administrative section for specified data file.
     * 
     * @param datafilePath
     *            object relative path to datafile
     * @return source section
     */
    private MdSecType getNewFileSourceSection(String datafilePath) {
        AmdSecType admSec = getFileAdministrativeSection(datafilePath);
        MdSecType sourceSec = new MdSecType();
        sourceSec.setID(idGenerators.getSourceId(admSec.getID()));
        admSec.getSourceMD().add(sourceSec);
        return sourceSec;
    }


    /**
     * Gets a new rights section in the a administrative section for specified data file.
     * 
     * @param datafilePath
     *            object relative path to datafile
     * @return rights section
     */
    private MdSecType getNewFileRightsSection(String datafilePath) {
        AmdSecType admSec = getFileAdministrativeSection(datafilePath);
        MdSecType rightsSec = new MdSecType();
        rightsSec.setID(idGenerators.getRightsId(admSec.getID()));
        admSec.getRightsMD().add(rightsSec);
        return rightsSec;
    }


    /**
     * Gets a new digital provenance section in the a administrative section for specified data file.
     * 
     * @param datafilePath
     *            object relative path to datafile
     * @return digital provenance section
     */
    private MdSecType getNewFileDigiprovSection(String datafilePath) {
        AmdSecType admSec = getFileAdministrativeSection(datafilePath);
        MdSecType digiprovSec = new MdSecType();
        digiprovSec.setID(idGenerators.getDigiprovId(admSec.getID()));
        admSec.getDigiprovMD().add(digiprovSec);
        return digiprovSec;
    }


    /**
     * Gets (and creates if necessary) an administrative section for the specific file.
     * 
     * @param datafilePath
     *            object relative path to datafile
     * @return administrative section
     */
    private AmdSecType getFileAdministrativeSection(String datafilePath) {
        if (dataFileMap.containsKey(datafilePath)) {
            String amdId = dataFileMap.get(datafilePath);
            for (AmdSecType admSec : mets.getAmdSec()) {
                if (admSec.getID().equals(amdId)) {
                    idGenerators.fillMaps(admSec);
                    return admSec;
                }
            }
        }
        AmdSecType admSec = new AmdSecType();
        String amdId = idGenerators.getFileAdmId();
        dataFileMap.put(datafilePath, amdId);
        admSec.setID(amdId);
        mets.getAmdSec().add(admSec);
        return admSec;
    }


    /**
     * Gets an object (of the representation type) section from PREMIS metadata.
     * 
     * @return object section from PREMIS metadata
     */
    private Representation getPremisRepresentationSection() {
        PremisComplexType premisDpSec = getPremisDigiprovMetadata();
        List<ObjectComplexType> objects = premisDpSec.getObject();
        for (ObjectComplexType objSec : objects) {
            if (objSec instanceof Representation) {
                return (Representation) objSec;
            }
        }
        Representation objSec = new Representation();
        objects.add(objSec);
        return objSec;
    }


    /**
     * Gets new event section from PREMIS metadata.
     * 
     * @return new event section from PREMIS metadata
     */
    private EventComplexType getNewPremisEventSection() {
        PremisComplexType premisDpSec = getPremisDigiprovMetadata();
        EventComplexType evtSec = new EventComplexType();
        EventIdentifierComplexType evtId = new EventIdentifierComplexType();
        evtId.setEventIdentifierType(MetsConsts.PREMIS_EVENT_TYPE);
        evtId.setEventIdentifierValue(idGenerators.getPremisEventId());
        evtSec.setEventIdentifier(evtId);
        premisDpSec.getEvent().add(evtSec);
        return evtSec;
    }


    /**
     * Gets the object digital provenance metadata in the PREMIS schema.
     * 
     * @return digital provenance metadata in the PREMIS schema
     */
    @SuppressWarnings("unchecked")
    private PremisComplexType getPremisDigiprovMetadata() {
        MdSecType premisDpSec = getPremisDigiprovSection();
        if (premisDpSec.getMdWrap() == null) {
            PremisComplexType premis = new PremisComplexType();
            premis.setVersion(PremisConsts.PREMIS_VERSION);
            JAXBElement<PremisComplexType> jaxbPremis = new JAXBElement<PremisComplexType>(new QName(
                    PremisConsts.PREMIS_NAMESPACE_URI, "premis", PremisConsts.PREMIS_PREFIX), PremisComplexType.class,
                    premis);
            XmlData premisData = new XmlData();
            premisData.getAny().add(jaxbPremis);
            MdWrap mdWrap = new MdWrap();
            mdWrap.setXmlData(premisData);
            mdWrap.setMDTYPE(NamespaceType.PREMIS.name());
            mdWrap.setLABEL("premis.xml");
            premisDpSec.setMdWrap(mdWrap);
        }
        return ((JAXBElement<PremisComplexType>) premisDpSec.getMdWrap().getXmlData().getAny().get(0)).getValue();
    }


    /**
     * Gets the object digital provenance section with PREMIS metadata in the administrative section with these
     * metadata.
     * 
     * @return digital provenance section with PREMIS metadata
     */
    private MdSecType getPremisDigiprovSection() {
        AmdSecType admSec = getPremisAdministrativeSection();
        for (MdSecType mdSec : admSec.getDigiprovMD()) {
            if (mdSec.getID().equals(premisDpId)) {
                return mdSec;
            }
        }
        MdSecType digiprovSec = new MdSecType();
        premisDpId = idGenerators.getDigiprovId(admSec.getID());
        digiprovSec.setID(premisDpId);
        digiprovSec.setSTATUS("CONSTRUCTED");
        admSec.getDigiprovMD().add(digiprovSec);
        return digiprovSec;
    }


    /**
     * Gets the object administrative section with PREMIS metadata about the object.
     * 
     * @return administrative section with PREMIS metadata
     */
    private AmdSecType getPremisAdministrativeSection() {
        for (AmdSecType amdSec : mets.getAmdSec()) {
            if (amdSec.getID().equals(premisAdmId)) {
                return amdSec;
            }
        }
        AmdSecType admSec = new AmdSecType();
        premisAdmId = idGenerators.getObjectAdmId();
        admSec.setID(premisAdmId);
        mets.getAmdSec().add(admSec);
        return admSec;
    }


    /**
     * Gets the file group section for data files of a digital object.
     * 
     * @return data files group section
     */
    private FileGrp getContentFileGroupSection() {
        FileSec fileSec = getFileSection();
        for (FileGrp fileGrpSec : fileSec.getFileGrp()) {
            if (fileGrpSec.getID().equals(contentFgId)) {
                return fileGrpSec;
            }
        }
        FileGrp fileGrpSec = new FileGrp();
        contentFgId = idGenerators.getFileGrpId();
        fileGrpSec.setID(contentFgId);
        fileSec.getFileGrp().add(fileGrpSec);
        return fileGrpSec;
    }


    /**
     * Gets the file section of METS metadata.
     * 
     * @return file section
     */
    private FileSec getFileSection() {
        FileSec fileSec = mets.getFileSec();
        if (fileSec != null) {
            return fileSec;
        }
        fileSec = new FileSec();
        mets.setFileSec(fileSec);
        return fileSec;
    }


    /**
     * Gets the file location section of METS metadata.
     * 
     * @return file location section
     */
    public FLocat findFileSectionByURL(String url) {
        FileSec fileSec = getFileSection();
        Iterator<FileGrp> it = fileSec.getFileGrp().iterator();
        while (it.hasNext()) {
            FileGrp fileGrp = (FileGrp) it.next();
            Iterator<FileType> it1 = fileGrp.getFile().iterator();
            while (it1.hasNext()) {
                FileType fileType = (FileType) it1.next();
                Iterator<FLocat> it2 = fileType.getFLocat().iterator();
                while (it2.hasNext()) {
                    FLocat locat = (FLocat) it2.next();
                    if (locat.getHref().equals(url)) {
                        lastFileTypeSec = fileType;
                        return locat;
                    }
                }
            }
        }
        lastFileTypeSec = null;
        return null;
    }


    /**
     * Gets last FileTypeSec founded by findFileSectionByURL().
     * 
     * @return file type section
     */
    public FileType getLastFileTypeSection() {
        return lastFileTypeSec;
    }


    /**
     * Gets the file location section of METS metadata.
     * 
     * @param url
     *            url to metadata file
     * @return MdRef section
     */
    public MdRef findFileAdmSectionByURL(String url) {
        Iterator<AmdSecType> it = mets.getAmdSec().iterator();
        while (it.hasNext()) {
            AmdSecType amdSecType = (AmdSecType) it.next();
            Iterator<MdSecType> it1 = amdSecType.getTechMD().iterator();
            while (it1.hasNext()) {
                MdSecType mdSecType = (MdSecType) it1.next();
                MdRef mdRef = mdSecType.getMdRef();
                if ((mdRef == null) || (mdRef.getHref() == null)) {
                    continue;
                }
                if (mdRef.getHref().equals(url)) {
                    return mdRef;
                }
            }
            Iterator<MdSecType> it2 = amdSecType.getSourceMD().iterator();
            while (it2.hasNext()) {
                MdSecType mdSecType = (MdSecType) it2.next();
                MdRef mdRef = mdSecType.getMdRef();
                if ((mdRef == null) || (mdRef.getHref() == null)) {
                    continue;
                }
                if (mdRef.getHref().equals(url)) {
                    return mdRef;
                }
            }
            Iterator<MdSecType> it3 = amdSecType.getRightsMD().iterator();
            while (it3.hasNext()) {
                MdSecType mdSecType = (MdSecType) it3.next();
                MdRef mdRef = mdSecType.getMdRef();
                if ((mdRef == null) || (mdRef.getHref() == null)) {
                    continue;
                }
                if (mdRef.getHref().equals(url)) {
                    return mdRef;
                }
            }
            Iterator<MdSecType> it4 = amdSecType.getDigiprovMD().iterator();
            while (it4.hasNext()) {
                MdSecType mdSecType = (MdSecType) it4.next();
                MdRef mdRef = mdSecType.getMdRef();
                if ((mdRef == null) || (mdRef.getHref() == null)) {
                    continue;
                }
                if (mdRef.getHref().equals(url)) {
                    return mdRef;
                }
            }
        }

        return null;
    }


    /**
     * Get current mapping DataFiles names and uri's .
     * 
     * @return mapping
     */
    public HashMap<String, String> getCurrentDataFileNamesUriMapping() {
        FileSec fileSec = getFileSection();
        HashMap<String, String> map = new HashMap<String, String>();

        Iterator<FileGrp> it = fileSec.getFileGrp().iterator();
        while (it.hasNext()) {
            FileGrp fileGrp = (FileGrp) it.next();
            Iterator<FileType> it1 = fileGrp.getFile().iterator();
            while (it1.hasNext()) {
                FileType fileType = (FileType) it1.next();
                Iterator<FLocat> it2 = fileType.getFLocat().iterator();
                while (it2.hasNext()) {
                    FLocat locat = (FLocat) it2.next();
                    URI uri = URI.create(locat.getHref());
                    String dataFileName = "";
                    try {
                        dataFileName = (new File(uri.toURL().getFile())).getName();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    map.put(dataFileName, locat.getHref());
                }
            }
        }
        return map;
    }


    /**
     * Set current mapping DataFiles names and uri's .
     * 
     * @param objectMetadataFilesMap
     *            new mapping
     */
    public void setObjectMetadata(HashMap<String, String> objectMetadataFilesMap) {
        Iterator<AmdSecType> it = mets.getAmdSec().iterator();
        while (it.hasNext()) {
            AmdSecType amdSecType = (AmdSecType) it.next();
            if (dataFileMap.containsValue(amdSecType.getID())) {
                continue;
            }
            Iterator<MdSecType> it1 = amdSecType.getTechMD().iterator();
            while (it1.hasNext()) {
                MdSecType mdSecType = (MdSecType) it1.next();
                MdRef mdRef = mdSecType.getMdRef();
                URI uri = URI.create(mdRef.getHref());
                String dataFileName = "";
                try {
                    dataFileName = (new File(uri.toURL().getFile())).getName();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                String fpath = objectMetadataFilesMap.get(dataFileName);
                if (fpath == null) {
                    continue;
                }
                mdRef.setHref(fpath);
                mdRef.setLOCTYPE(MetsConsts.METS_METADATA_LOCATION_TYPE_RELATIVE);
            }
            Iterator<MdSecType> it2 = amdSecType.getSourceMD().iterator();
            while (it2.hasNext()) {
                MdSecType mdSecType = (MdSecType) it2.next();
                MdRef mdRef = mdSecType.getMdRef();
                URI uri = URI.create(mdRef.getHref());
                String dataFileName = "";
                try {
                    dataFileName = (new File(uri.toURL().getFile())).getName();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                String fpath = objectMetadataFilesMap.get(dataFileName);
                if (fpath == null) {
                    continue;
                }
                mdRef.setHref(fpath);
                mdRef.setLOCTYPE(MetsConsts.METS_METADATA_LOCATION_TYPE_RELATIVE);
            }
            Iterator<MdSecType> it3 = amdSecType.getRightsMD().iterator();
            while (it3.hasNext()) {
                MdSecType mdSecType = (MdSecType) it3.next();
                MdRef mdRef = mdSecType.getMdRef();
                URI uri = URI.create(mdRef.getHref());
                String dataFileName = "";
                try {
                    dataFileName = (new File(uri.toURL().getFile())).getName();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                String fpath = objectMetadataFilesMap.get(dataFileName);
                if (fpath == null) {
                    continue;
                }
                mdRef.setHref(fpath);
                mdRef.setLOCTYPE(MetsConsts.METS_METADATA_LOCATION_TYPE_RELATIVE);
            }
            Iterator<MdSecType> it4 = amdSecType.getDigiprovMD().iterator();
            while (it4.hasNext()) {
                MdSecType mdSecType = (MdSecType) it4.next();
                MdRef mdRef = mdSecType.getMdRef();
                URI uri = URI.create(mdRef.getHref());
                String dataFileName = "";
                try {
                    dataFileName = (new File(uri.toURL().getFile())).getName();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                String fpath = objectMetadataFilesMap.get(dataFileName);
                if (fpath == null) {
                    continue;
                }
                mdRef.setHref(fpath);
                mdRef.setLOCTYPE(MetsConsts.METS_METADATA_LOCATION_TYPE_RELATIVE);
            }
        }
    }


    /**
     * Add new mapping.
     */
    public void addMapping(String dataFilePath, String amdSecId) {
        if (!dataFileMap.containsKey(dataFilePath)) {
            dataFileMap.put(dataFilePath, amdSecId);
        }
    }
}
