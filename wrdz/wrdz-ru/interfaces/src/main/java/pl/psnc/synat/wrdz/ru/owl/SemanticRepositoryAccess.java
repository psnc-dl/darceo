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

import java.util.List;

import javax.ejb.Local;

import pl.psnc.synat.sra.exception.SemanticRepositoryResourceException;
import pl.psnc.synat.wrdz.ru.composition.AdvancedDelivery;
import pl.psnc.synat.wrdz.ru.composition.TransformationChain;
import pl.psnc.synat.wrdz.ru.grounding.WadlNodes;

/**
 * Interface to a semantic repository.
 */
@Local
public interface SemanticRepositoryAccess {

    /**
     * Adds to the semantic repository all RDF data that make up a semantic descriptor of a service.
     * 
     * @param semanticRdfData
     *            RDF data of semantic descriptor
     * @throws SemanticRepositoryResourceException
     *             if any semantic repository related problems occur
     */
    void addSemanticDescrptorRdfData(RdfSemanticDescriptor semanticRdfData)
            throws SemanticRepositoryResourceException;


    /**
     * Removes from the semantic repository all RDF data which belong to the context (uniquely determined by the
     * semantic descriptor).
     * 
     * @param context
     *            context to clear
     * @throws SemanticRepositoryResourceException
     *             if any semantic repository related problems occur
     */
    void removeSemanticDescrptorRdfData(String context)
            throws SemanticRepositoryResourceException;


    /**
     * Verifies parameters (file formats) of the transformation chain.
     * 
     * @param chain
     *            transformation chain that requires verification
     * @param inputFormatIri
     *            UDFR IRI of the format of the given chain's input file
     * @param outputFormatIri
     *            UDFR IRI of the format of the given chain's output file
     * @return <code>true</code> if the transformation chain is valid or <code>false</code> otherwise
     * @throws SemanticRepositoryResourceException
     *             if any semantic repository related problems occur
     */
    boolean verifyServiceChainParameters(TransformationChain chain, String inputFormatIri, String outputFormatIri)
            throws SemanticRepositoryResourceException;


    /**
     * Finds all transformation chains that transform an inputFormatIri to an outputFormatIri.
     * 
     * @param inputFormatIri
     *            UDFR IRI of an input format
     * @param outputFormatIri
     *            UDFR IRI of an output format
     * @return list of transformation chains
     * @throws SemanticRepositoryResourceException
     *             if any semantic repository related problems occur
     */
    List<TransformationChain> getTransfromationChainsByFormats(String inputFormatIri, String outputFormatIri)
            throws SemanticRepositoryResourceException;


    /**
     * Finds all important nodes in WADL concerning the service.
     * 
     * @param serviceIri
     *            IRI of the service
     * @return identifiers of nodes in WADL or null if there are no any
     * @throws SemanticRepositoryResourceException
     *             if any semantic repository related problems occur
     */
    WadlNodes getWadlNodesForService(String serviceIri)
            throws SemanticRepositoryResourceException;


    /**
     * Finds all advanced delivery services and file formats that they can deliver.
     * 
     * @return list of advanced delivery serices
     * @throws SemanticRepositoryResourceException
     *             if any semantic repository related problems occur
     */
    List<AdvancedDelivery> getAdvancedDeliverySerices()
            throws SemanticRepositoryResourceException;

}
