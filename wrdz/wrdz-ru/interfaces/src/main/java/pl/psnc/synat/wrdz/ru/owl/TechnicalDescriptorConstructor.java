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

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import pl.psnc.synat.wrdz.ru.entity.services.DataManipulationService;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.TechnicalDescriptor;
import pl.psnc.synat.wrdz.ru.exceptions.EntryCreationException;

/**
 * Interface describing technical descriptor constructor classes contract.
 */
@Local
public interface TechnicalDescriptorConstructor {

    /**
     * Constructs new technical descriptor subtree and fills it with with information extracted from the ontology.
     * 
     * @param ontology
     *            parsed ontology.
     * @param technicalDesc
     *            technical descriptor individual from the ontology.
     * @param semantic
     *            semantic descriptor that links to the constructed tehcnical descriptor.
     * @param dataManipulationService
     *            data manipulation service described by constructed descriptor.
     * @return constructed technical descriptor filled with extracted information.
     * @throws EntryCreationException
     *             should any problems with extracting information and constructing technical descriptor subtree occur.
     */
    TechnicalDescriptor construct(OWLOntology ontology, OWLIndividual technicalDesc, SemanticDescriptor semantic,
            DataManipulationService dataManipulationService)
            throws EntryCreationException;

}
