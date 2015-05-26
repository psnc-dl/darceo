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
package pl.psnc.synat.wrdz.common.metadata.adm;

import info.lc.xmlns.premis_v2.CreatingApplicationComplexType;
import info.lc.xmlns.premis_v2.EventComplexType;
import info.lc.xmlns.premis_v2.EventOutcomeDetailComplexType;
import info.lc.xmlns.premis_v2.EventOutcomeInformationComplexType;
import info.lc.xmlns.premis_v2.File;
import info.lc.xmlns.premis_v2.FixityComplexType;
import info.lc.xmlns.premis_v2.FormatComplexType;
import info.lc.xmlns.premis_v2.FormatDesignationComplexType;
import info.lc.xmlns.premis_v2.FormatRegistryComplexType;
import info.lc.xmlns.premis_v2.ObjectCharacteristicsComplexType;
import info.lc.xmlns.premis_v2.ObjectIdentifierComplexType;
import info.lc.xmlns.premis_v2.PremisComplexType;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Builder of administrative metadata of a file in the PREMIS schema.
 */
public class PremisMetadataBuilder implements AdmMetadataBuilder {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(PremisMetadataBuilder.class);

    /**
     * Date formatter compatible with the xs:dateTime format.
     */
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Marshaller of the PREMIS metadata.
     */
    private final Marshaller marshaller;

    /**
     * Premis root object.
     */
    private PremisComplexType premis;

    /**
     * Premis structure explorer object.
     */
    private PremisMetadataExplorer premisExplorer;


    /**
     * Constructs builder of administrative metadata in the PREMIS schema.
     * 
     * @param jaxbContext
     *            JAXB context for the PREMIS classes
     */
    public PremisMetadataBuilder(JAXBContext jaxbContext) {
        try {
            this.marshaller = jaxbContext.createMarshaller();
            this.marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            this.marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            this.marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, PremisConsts.PREMIS_NAMESPACE_URI + " "
                    + PremisConsts.PREMIS_SCHEMA_LOCATION);
        } catch (JAXBException e) {
            logger.error("JAXB - PREMIS marshaller creation failed.", e);
            throw new RuntimeException(e);
        }
        premis = new PremisComplexType();
        premis.setVersion(PremisConsts.PREMIS_VERSION);
        premisExplorer = new PremisMetadataExplorer(premis);
    }


    @Override
    public AdmMetadata build()
            throws AdmMetadataProcessingException {
        StringWriter xmlWriter = new StringWriter();
        xmlWriter.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        try {
            marshaller.marshal(new JAXBElement<PremisComplexType>(new QName(PremisConsts.PREMIS_NAMESPACE_URI,
                    "premis", PremisConsts.PREMIS_PREFIX), PremisComplexType.class, premis), xmlWriter);
        } catch (JAXBException e) {
            logger.error("Building XML with metadata failed.", e);
            throw new AdmMetadataProcessingException(e);
        }
        return new PremisMetadata(xmlWriter.toString());
    }


    @Override
    public PremisMetadataBuilder setFileRelativePath(String filepath) {
        ObjectIdentifierComplexType objectIdentifierSection = new ObjectIdentifierComplexType();
        objectIdentifierSection.setObjectIdentifierType(PremisConsts.PREMIS_FILE_IDENTIFIER);
        objectIdentifierSection.setObjectIdentifierValue(filepath);
        File objectSection = premisExplorer.getFileObjectSection();
        objectSection.getObjectIdentifier().add(objectIdentifierSection);
        return this;
    }


    @Override
    public PremisMetadataBuilder setFileHash(String digestAlgorithm, String digest) {
        FixityComplexType fixitySection = new FixityComplexType();
        fixitySection.setMessageDigestAlgorithm(digestAlgorithm);
        fixitySection.setMessageDigest(digest);
        ObjectCharacteristicsComplexType objectCharacteristicsSection = premisExplorer
                .getObjectCharacteristicsSection();
        objectCharacteristicsSection.getFixity().add(fixitySection);
        return this;
    }


    @Override
    public PremisMetadataBuilder setFileSize(long size) {
        ObjectCharacteristicsComplexType objectCharacteristicsSection = premisExplorer
                .getObjectCharacteristicsSection();
        objectCharacteristicsSection.setSize(size);
        return this;
    }


    @Override
    public PremisMetadataBuilder setFileFormatDesignation(String formatName, String formatVersion) {
        FormatDesignationComplexType formatDesignationSection = new FormatDesignationComplexType();
        formatDesignationSection.setFormatName(formatName);
        if (formatVersion != null) {
            formatDesignationSection.setFormatVersion(formatVersion);
        }
        FormatComplexType formatSection = premisExplorer.getFormatSection();
        formatSection.getContent().add(
            new JAXBElement<FormatDesignationComplexType>(new QName(PremisConsts.PREMIS_NAMESPACE_URI,
                    "formatDesignation", PremisConsts.PREMIS_PREFIX), FormatDesignationComplexType.class,
                    formatDesignationSection));
        return this;
    }


    @Override
    public PremisMetadataBuilder setFileFormatRegistry(String registryName, String registryKey) {
        FormatRegistryComplexType formatRegistrySection = new FormatRegistryComplexType();
        formatRegistrySection.setFormatRegistryName(registryName);
        formatRegistrySection.setFormatRegistryKey(registryKey);
        formatRegistrySection.setFormatRegistryRole("specification");
        FormatComplexType formatSection = premisExplorer.getFormatSection();
        formatSection.getContent().add(
            new JAXBElement<FormatRegistryComplexType>(new QName(PremisConsts.PREMIS_NAMESPACE_URI, "formatRegistry",
                    PremisConsts.PREMIS_PREFIX), FormatRegistryComplexType.class, formatRegistrySection));
        return this;
    }


    @Override
    public PremisMetadataBuilder setCreativeApplication(String applicationName, String applicationVersion,
            String applicationDate) {
        CreatingApplicationComplexType creativeApplicationSection = new CreatingApplicationComplexType();
        creativeApplicationSection.getContent().add(
            new JAXBElement<String>(new QName(PremisConsts.PREMIS_NAMESPACE_URI, "creatingApplicationName",
                    PremisConsts.PREMIS_PREFIX), String.class, applicationName));
        if (applicationVersion != null) {
            creativeApplicationSection.getContent().add(
                new JAXBElement<String>(new QName(PremisConsts.PREMIS_NAMESPACE_URI, "creatingApplicationVersion",
                        PremisConsts.PREMIS_PREFIX), String.class, applicationVersion));
        }
        if (applicationDate != null) {
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, Locale.US);
            try {
                Date date = dateFormatter.parse(applicationDate);
                creativeApplicationSection.getContent().add(
                    new JAXBElement<String>(new QName(PremisConsts.PREMIS_NAMESPACE_URI, "dateCreatedByApplication",
                            PremisConsts.PREMIS_PREFIX), String.class, DATE_FORMATTER.format(date)));
            } catch (ParseException e) {
                logger.info("Unrecognized date created by application:" + applicationDate);
                creativeApplicationSection.getContent().add(
                    new JAXBElement<String>(new QName(PremisConsts.PREMIS_NAMESPACE_URI, "dateCreatedByApplication",
                            PremisConsts.PREMIS_PREFIX), String.class, applicationDate));
            }
        }
        ObjectCharacteristicsComplexType objectCharacteristicsSection = premisExplorer
                .getObjectCharacteristicsSection();
        objectCharacteristicsSection.getCreatingApplication().add(creativeApplicationSection);
        return this;
    }


    @Override
    public PremisMetadataBuilder setFileCreationEvent(Date date) {
        EventOutcomeInformationComplexType eventOutcomeInformation = new EventOutcomeInformationComplexType();
        eventOutcomeInformation.getContent().add(
            new JAXBElement<String>(new QName(PremisConsts.PREMIS_NAMESPACE_URI, "eventOutcome",
                    PremisConsts.PREMIS_PREFIX), String.class, "successful"));
        EventComplexType eventSection = premisExplorer.getFileCreationEventSection();
        eventSection.setEventDateTime(DATE_FORMATTER.format(date));
        eventSection.getEventOutcomeInformation().add(eventOutcomeInformation);
        return this;
    }


    @Override
    public PremisMetadataBuilder setFileFormatValidationEvent(Date date, String validationResult,
            Set<String> validationMessages, int maxMessages) {
        EventOutcomeInformationComplexType eventOutcomeInformation = new EventOutcomeInformationComplexType();
        eventOutcomeInformation.getContent().add(
            new JAXBElement<String>(new QName(PremisConsts.PREMIS_NAMESPACE_URI, "eventOutcome",
                    PremisConsts.PREMIS_PREFIX), String.class, validationResult));
        if (validationMessages != null && validationMessages.size() > 0) {
            int i = 0;
            for (String message : validationMessages) {
                EventOutcomeDetailComplexType eventOutcomeDetail = new EventOutcomeDetailComplexType();
                eventOutcomeDetail.setEventOutcomeDetailNote(message);
                eventOutcomeInformation.getContent().add(
                    new JAXBElement<EventOutcomeDetailComplexType>(new QName(PremisConsts.PREMIS_NAMESPACE_URI,
                            "eventOutcomeDetail", PremisConsts.PREMIS_PREFIX), EventOutcomeDetailComplexType.class,
                            eventOutcomeDetail));
                i++;
                if (i == maxMessages) {
                    int remaining = validationMessages.size() - i;
                    if (remaining > 0) {
                        logger.debug("Only " + i + " messages are set. Other omitted.");
                        eventOutcomeDetail = new EventOutcomeDetailComplexType();
                        eventOutcomeDetail.setEventOutcomeDetailNote("The remaining " + remaining
                                + " similar messages have been omitted.");
                        eventOutcomeInformation.getContent().add(
                            new JAXBElement<EventOutcomeDetailComplexType>(new QName(PremisConsts.PREMIS_NAMESPACE_URI,
                                    "eventOutcomeDetail", PremisConsts.PREMIS_PREFIX),
                                    EventOutcomeDetailComplexType.class, eventOutcomeDetail));
                        break;
                    }
                }
            }
        }
        EventComplexType eventSection = premisExplorer.getFormatValidationEventSection();
        eventSection.setEventDateTime(DATE_FORMATTER.format(date));
        eventSection.getEventOutcomeInformation().add(eventOutcomeInformation);
        return this;
    }

}
