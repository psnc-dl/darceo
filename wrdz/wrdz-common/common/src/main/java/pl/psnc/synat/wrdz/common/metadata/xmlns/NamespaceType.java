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
package pl.psnc.synat.wrdz.common.metadata.xmlns;

/**
 * Enum representing type of the namespace of metadata.
 */
public enum NamespaceType {
    /**
     * Dublin Core metadata namespace.
     */
    OAI_DC,
    /**
     * Dublin Core metadata namespace.
     */
    DC,
    /**
     * Dublin Core Terms metadata namespace.
     */
    DCTERMS,
    /**
     * MARC 21 record.
     */
    MARC,
    /**
     * MARC 21 record in XML schema.
     */
    MARCXML,
    /**
     * Metadata Object Description Schema.
     */
    MODS,
    /**
     * Interoperability Metadata Standard for Electronic Theses and Dissertations.
     */
    ETDMS,
    /**
     * PLMET metadata namespace.
     */
    PLMET,
    /**
     * Open Archives Initiative Object Reuse and Exchange metadata namespace.
     */
    OAIORE,
    /**
     * Atom Syndication Format metadata namespace.
     */
    ATOM,
    /**
     * Resource Description Framework metadata namespace.
     */
    RDF,
    /**
     * Web Ontology Languag metadata namespace.
     */
    OWL,
    /**
     * Metadata Encoding and Transmission Standard metadata namespace.
     */
    METS,
    /**
     * METS Rights Declaration Schema.
     */
    METSRIGHTS,
    /**
     * PREMIS metadata namespace.
     */
    PREMIS,
    /**
     * PREMIS object entity metadata namespace.
     */
    PREMIS_OBJECT,
    /**
     * PREMIS agent entity metadata namespace.
     */
    PREMIS_AGENT,
    /**
     * PREMIS rights entity metadata namespace.
     */
    PREMIS_RIGHTS,
    /**
     * PREMIS event entity metadata namespace.
     */
    PREMIS_EVENT,
    /**
     * Technical metadata for text namespace.
     */
    TEXTMD,
    /**
     * Technical metadata for documents namespace.
     */
    DOCUMENTMD,
    /**
     * NISO technical metadata for digital still images.
     */
    NISOIMG,
    /**
     * Metadata for digital still images in XML metadata namespace.
     */
    MIX,
    /**
     * Audio Engineering Society standard for audio metadata namespace (AES57, formerly AES-X098B).
     */
    AES57,
    /**
     * Technical metadata for video in old schema VMD.
     */
    VMD,
    /**
     * Technical metadata for video (VideoMD under review).
     */
    VIDEOMD,
    /**
     * unknown.
     */
    UNKNOWN;
}
