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

import info.lc.xmlns.premis_v2.EventComplexType;
import info.lc.xmlns.premis_v2.File;
import info.lc.xmlns.premis_v2.FormatComplexType;
import info.lc.xmlns.premis_v2.ObjectCharacteristicsComplexType;
import info.lc.xmlns.premis_v2.ObjectComplexType;
import info.lc.xmlns.premis_v2.PremisComplexType;

import java.util.List;

/**
 * Premis metadata explorer class.
 */
public class PremisMetadataExplorer {

    /**
     * Premis root element.
     */
    private final PremisComplexType premis;


    /**
     * Default constructor.
     * 
     * @param premis
     *            premis root element
     */
    public PremisMetadataExplorer(PremisComplexType premis) {
        this.premis = premis;
    }


    /**
     * Gets an object (of the file type) section from premis metadata.
     * 
     * @return object section
     */
    public File getFileObjectSection() {
        List<ObjectComplexType> objects = premis.getObject();
        for (ObjectComplexType object : objects) {
            if (object instanceof File) {
                return (File) object;
            }
        }
        File objectSection = new File();
        objects.add(objectSection);
        return objectSection;
    }


    /**
     * Gets an object characteristic section from premis metadata.
     * 
     * @return object characteristic section
     */
    public ObjectCharacteristicsComplexType getObjectCharacteristicsSection() {
        File objectSection = getFileObjectSection();
        List<ObjectCharacteristicsComplexType> objectCharacteristics = objectSection.getObjectCharacteristics();
        if (objectCharacteristics.size() == 0) {
            ObjectCharacteristicsComplexType objectCharacteristicsSection = new ObjectCharacteristicsComplexType();
            objectCharacteristics.add(objectCharacteristicsSection);
            return objectCharacteristicsSection;
        } else {
            return objectCharacteristics.get(0);
        }
    }


    /**
     * Gets a format section from premis metadata.
     * 
     * @return format section
     */
    public FormatComplexType getFormatSection() {
        ObjectCharacteristicsComplexType objectCharacteristicsSection = getObjectCharacteristicsSection();
        List<FormatComplexType> formats = objectCharacteristicsSection.getFormat();
        if (formats.size() == 0) {
            FormatComplexType formatSection = new FormatComplexType();
            formats.add(formatSection);
            return formatSection;
        } else {
            return formats.get(0);
        }
    }


    /**
     * Gets an event with the file creation result.
     * 
     * @return event section
     */
    public EventComplexType getFileCreationEventSection() {
        List<EventComplexType> events = premis.getEvent();
        for (EventComplexType event : events) {
            if (event.getEventType().equals(PremisConsts.PREMIS_CREATION_EVENT_TYPE)) {
                return event;
            }
        }
        EventComplexType eventSection = new EventComplexType();
        eventSection.setEventType(PremisConsts.PREMIS_CREATION_EVENT_TYPE);
        events.add(eventSection);
        return eventSection;
    }


    /**
     * Gets an event with the validation process result.
     * 
     * @return event section
     */
    public EventComplexType getFormatValidationEventSection() {
        List<EventComplexType> events = premis.getEvent();
        for (EventComplexType event : events) {
            if (event.getEventType().equals(PremisConsts.PREMIS_VALIDATION_EVENT_TYPE)) {
                return event;
            }
        }
        EventComplexType eventSection = new EventComplexType();
        eventSection.setEventType(PremisConsts.PREMIS_VALIDATION_EVENT_TYPE);
        events.add(eventSection);
        return eventSection;
    }

}
