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
package pl.psnc.synat.wrdz.ru.owl;

import javax.ejb.Local;

import org.semanticweb.owlapi.model.IRI;

import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.exceptions.EntryCreationException;

/**
 * Interface services descriptor constructor classes contract.
 */
@Local
public interface ServicesConstructor {

    /**
     * Name of the profile type representing migration services.
     */
    String MIGRATION = "DataMigrationProfile";

    /**
     * Name of the profile type representing conversion services.
     */
    String CONVERSION = "DataConversionProfile";

    /**
     * Name of the profile type representing advanced data delivery services.
     */
    String ADV_DELIVERY = "AdvancedDataDeliveryProfile";

    /**
     * IRI of the service class in OWL-S service ontology.
     */
    IRI SERVICE_CLASS_IRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Service.owl#Service");

    /**
     * IRI of the <code>supports</code> property of the service class, containing link to grounding, in OWL-S service
     * ontology.
     */
    IRI SUPPORTS_PROPERTY_IRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Service.owl#supports");

    /**
     * IRI of the <code>presents</code> property of the service class, containing link to profile, in OWL-S service
     * ontology.
     */
    IRI PRESENTS_PROPERTY_IRI = IRI.create("http://www.daml.org/services/owl-s/1.2/Service.owl#presents");


    /**
     * Extracts information about services and descriptors from the semantic descriptor. Updates the entity for semantic
     * constructor and returns info about RDF data that make up this descriptor.
     * 
     * @param descriptorUri
     *            semantic descriptor URI.
     * @param semantic
     *            semantic descriptor constructed.
     * @return info about RDF data that make up the semantic descriptor
     * @throws EntryCreationException
     *             should any problem with semantic descriptor accessing and parsing or information extraction and
     *             construction occur.
     */
    RdfSemanticDescriptor extractInformation(String descriptorUri, SemanticDescriptor semantic)
            throws EntryCreationException;

}
