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

import java.util.Date;
import java.util.Set;


/**
 * Interface for all builder of administrative metadata.
 */
public interface AdmMetadataBuilder {

    /**
     * Build administrative metadata in some specific schema.
     * 
     * @return administrative metadata
     * @throws AdmMetadataProcessingException
     *             when some problem with building of the XML with premis metadata occurs
     */
    AdmMetadata build()
            throws AdmMetadataProcessingException;


    /**
     * Sets the relative path to the file (in the context of a digital object).
     * 
     * @param filepath
     *            file path to the file in the context of a digital object
     * @return this builder
     */
    AdmMetadataBuilder setFileRelativePath(String filepath);


    /**
     * Sets the hash of the file.
     * 
     * @param digestAlgorithm
     *            hash algorithm
     * @param digest
     *            hash value
     * @return this builder
     */
    AdmMetadataBuilder setFileHash(String digestAlgorithm, String digest);


    /**
     * Sets the size of the file.
     * 
     * @param size
     *            size
     * @return this builder
     */
    AdmMetadataBuilder setFileSize(long size);


    /**
     * Sets the format designation of the file.
     * 
     * @param formatName
     *            format name
     * @param formatVersion
     *            format version
     * @return this builder
     */
    AdmMetadataBuilder setFileFormatDesignation(String formatName, String formatVersion);


    /**
     * Sets the format registry of the file.
     * 
     * @param registryName
     *            registry name
     * @param registryKey
     *            key of the format in the registry
     * @return this builder
     */
    AdmMetadataBuilder setFileFormatRegistry(String registryName, String registryKey);


    /**
     * Sets the creative application info.
     * 
     * @param applicationName
     *            application name
     * @param applicationVersion
     *            application version
     * @param applicationDate
     *            date created by application
     * @return this builder
     */
    AdmMetadataBuilder setCreativeApplication(String applicationName, String applicationVersion, String applicationDate);


    /**
     * Sets the file creation event.
     * 
     * @param date
     *            date of event
     * @return this builder
     */
    AdmMetadataBuilder setFileCreationEvent(Date date);


    /**
     * Sets the file format validation event.
     * 
     * @param date
     *            date of event
     * @param validationResult
     *            result of validation
     * @param validationMessages
     *            messages produced during the validation process
     * @return this builder
     */
    AdmMetadataBuilder setFileFormatValidationEvent(Date date, String validationResult, Set<String> validationMessages,
            int maxMessages);

}
