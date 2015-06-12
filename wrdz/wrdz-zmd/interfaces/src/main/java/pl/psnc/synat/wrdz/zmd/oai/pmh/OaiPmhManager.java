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
package pl.psnc.synat.wrdz.zmd.oai.pmh;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import org.openarchives.oai.pmh.GetRecordType;
import org.openarchives.oai.pmh.ListIdentifiersType;
import org.openarchives.oai.pmh.ListRecordsType;

import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataNamespace;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;
import pl.psnc.synat.wrdz.zmd.exception.IllegalOaiPmhTokenException;
import pl.psnc.synat.wrdz.zmd.object.ObjectNotFoundException;

/**
 * Specifies the interface for classes that manage the process of sharing metadata through OAI_PMH protocol.
 */
@Local
public interface OaiPmhManager {

    /**
     * Returns current database datestamp creating lock on the {@link Operation} entities table.
     * 
     * @return current database timestamp.
     */
    Date getCurrentTime();


    /**
     * Implements functionality of GetRecord operation specified by the OAI-PMH protocol.
     * 
     * @param identifier
     *            digital object's public identifier.
     * @param metadataType
     *            metadata type to look for.
     * @return record entry for OAI-PMH response or <code>null</code> if no metadata for given arguments was found.
     * @throws IllegalArgumentException
     *             when either {@code identifier} or {@code metadataType} is {@code null} or empty.
     * @throws ObjectNotFoundException
     *             if object with specified identifier was not found.
     */
    GetRecordType getRecord(String identifier, NamespaceType metadataType)
            throws IllegalArgumentException, ObjectNotFoundException;


    /**
     * Gets the namespaces available for a digital obejct with specified identifier.
     * 
     * @param identifier
     *            digital object's identifier.
     * @return list of available namespaces.
     * @throws ObjectNotFoundException
     *             when object was not found in the repository.
     */
    List<MetadataNamespace> getNamespaces(String identifier)
            throws ObjectNotFoundException;


    /**
     * Performs operations to fetch and parse ListIdentifiers operation requested data.
     * 
     * @param from
     *            beginning of the period to be harvested.
     * @param until
     *            end of the period to be harvested.
     * @param prefix
     *            name of the namespace to be harvested.
     * @param set
     *            name of the set to be harvested. If {@code null} then all sets are harvested.
     * @param offset
     *            page offset value.
     * @return results of the OAI-PMH ListIdentifiers operation.
     */
    ListIdentifiersType listIdentifiers(Date from, Date until, NamespaceType prefix, ObjectType set, int offset);


    /**
     * Performs operations to fetch and parse ListIdentifiers operation requested data.
     * 
     * @param resumptionToken
     *            resumption token marking next page of query results.
     * @return next page of query results.
     * @throws IllegalOaiPmhTokenException
     *             when specified token for ListIdentifiers OAI-PMH operation was not found in the database.
     */
    ListIdentifiersType listIdentifiers(String resumptionToken)
            throws IllegalOaiPmhTokenException;


    /**
     * Performs operations to fetch and parse ListRecords operation requested data.
     * 
     * @param resumptionToken
     *            resumption token marking next page of query results.
     * @return next page of query results.
     * @throws IllegalOaiPmhTokenException
     *             when specified token for ListRecords OAI-PMH operation was not found in the database.
     */
    ListRecordsType listRecords(String resumptionToken)
            throws IllegalOaiPmhTokenException;


    /**
     * Performs operations to fetch and parse ListRecords operation requested data.
     * 
     * @param from
     *            beginning of the period to be harvested.
     * @param until
     *            end of the period to be harvested.
     * @param prefix
     *            name of the namespace to be harvested.
     * @param set
     *            name of the set to be harvested. If {@code null} then all sets are harvested.
     * @param offset
     *            page offset value.
     * @return results of the OAI-PMH ListRecords operation.
     */
    ListRecordsType listRecords(Date from, Date until, NamespaceType prefix, ObjectType set, int offset);

}
