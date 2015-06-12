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

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openarchives.oai.pmh.GetRecordType;
import org.openarchives.oai.pmh.HeaderType;
import org.openarchives.oai.pmh.ListIdentifiersType;
import org.openarchives.oai.pmh.ListRecordsType;
import org.openarchives.oai.pmh.MetadataType;
import org.openarchives.oai.pmh.RecordType;
import org.openarchives.oai.pmh.ResumptionTokenType;
import org.openarchives.oai.pmh.StatusType;
import org.openarchives.oai.pmh.VerbType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.dao.QueryModifier;
import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.dao.oai.ResumptionTokenDao;
import pl.psnc.synat.wrdz.zmd.dao.oai.ResumptionTokenFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectDao;
import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.MetadataNamespaceDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.MetadataNamespaceFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.operation.OperationDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.operation.OperationFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.operation.OperationSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.oai.ResumptionToken;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataNamespace;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;
import pl.psnc.synat.wrdz.zmd.entity.types.OperationType;
import pl.psnc.synat.wrdz.zmd.exception.IllegalOaiPmhTokenException;
import pl.psnc.synat.wrdz.zmd.object.ObjectNotFoundException;

/**
 * Provides OAI-PMH repository functionality for WRDZ platform.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class OaiPmhManagerBean implements OaiPmhManager {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(OaiPmhManagerBean.class);

    /**
     * Digital object DAO.
     */
    @EJB
    private DigitalObjectDao digitalObjectDao;

    /**
     * Operations DAO.
     */
    @EJB
    private OperationDao operationDao;

    /**
     * Metadata namespace DAO.
     */
    @EJB
    private MetadataNamespaceDao metadataNamespaceDao;

    /**
     * Resumption token DAO.
     */
    @EJB
    private ResumptionTokenDao resumptionTokenDao;

    /**
     * ZMD module configuration.
     */
    @Inject
    private ZmdConfiguration configuration;

    /**
     * XML document builder factory.
     */
    private DocumentBuilderFactory documentBuilderFactory;


    /**
     * Initializes bean state constructing document builder factory.
     */
    @PostConstruct
    public void init() {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringElementContentWhitespace(false);
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setExpandEntityReferences(false);
        documentBuilderFactory.setIgnoringComments(false);
        documentBuilderFactory.setNamespaceAware(false);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setCoalescing(false);
    }


    @Override
    public Date getCurrentTime() {
        operationDao.lockTable(false);
        return operationDao.getDatabaseDate();
    }


    @Override
    public GetRecordType getRecord(String identifier, NamespaceType metadataType)
            throws IllegalArgumentException, ObjectNotFoundException {
        if (identifier == null || identifier.trim().isEmpty() || metadataType == null) {
            throw new IllegalArgumentException(
                    "Cannot perform query for null or empty identifier or null metadata type.");
        }
        Operation operation = findRecord(identifier, metadataType);
        if (operation != null) {
            GetRecordType returned = new GetRecordType();
            returned.setRecord(createRecord(operation));
            return returned;
        }
        return null;
    }


    @Override
    public List<MetadataNamespace> getNamespaces(String identifier)
            throws ObjectNotFoundException {
        Long id = null;
        if (identifier != null) {
            DigitalObjectFilterFactory queryFilterFactory = digitalObjectDao.createQueryModifier()
                    .getQueryFilterFactory();
            DigitalObject object = digitalObjectDao.findFirstResultBy(queryFilterFactory.byIdentifier(identifier));
            if (object == null) {
                throw new ObjectNotFoundException("Object with identifier " + identifier + " not found");
            }
            id = object.getId();
        }
        List<NamespaceType> usedNamespaceTypes = operationDao.findUsedNamespaceTypes(id);
        if (usedNamespaceTypes == null || usedNamespaceTypes.isEmpty()) {
            return null;
        }
        MetadataNamespaceFilterFactory filter = metadataNamespaceDao.createQueryModifier().getQueryFilterFactory();
        return metadataNamespaceDao.findBy(filter.byType(usedNamespaceTypes), true);
    }


    @Override
    public ListIdentifiersType listIdentifiers(String resumptionToken)
            throws IllegalOaiPmhTokenException {
        ResumptionToken token = findToken(resumptionToken, VerbType.LIST_IDENTIFIERS.name());
        resumptionTokenDao.delete(token);
        return listIdentifiers(token.getFrom(), token.getUntil(), token.getPrefix(), token.getSet(), token.getOffset());
    }


    @Override
    public ListIdentifiersType listIdentifiers(Date from, Date until, NamespaceType prefix, ObjectType set, int offset) {
        ListIdentifiersType result = null;
        int pageSize = configuration.getOaiIdentifiersPageSize();
        long changedObjects = operationDao.countChangedObjects(from, until, NamespaceType.METS, null);
        if (changedObjects > 0L) {
            ResumptionToken token = null;
            operationDao.lockTable(true);
            if (changedObjects - offset - pageSize > 0) {
                int newOffset = offset + pageSize;
                token = createResumptionToken(from, until, prefix, set, newOffset, VerbType.LIST_IDENTIFIERS.name());
            }
            List<Operation> changes = operationDao.getChanges(from, until, prefix, set, offset, pageSize);
            result = new ListIdentifiersType();
            List<HeaderType> headers = result.getHeader();
            for (Operation operation : changes) {
                headers.add(createHeader(operation));
            }
            if (offset > 0 && token == null || token != null) {
                result.setResumptionToken(createToken(offset, changedObjects, token));
            }
        }
        return result;
    }


    @Override
    public ListRecordsType listRecords(String resumptionToken)
            throws IllegalOaiPmhTokenException {
        ResumptionToken token = findToken(resumptionToken, VerbType.LIST_RECORDS.name());
        resumptionTokenDao.delete(token);
        return listRecords(token.getFrom(), token.getUntil(), token.getPrefix(), token.getSet(), token.getOffset());
    }


    @Override
    public ListRecordsType listRecords(Date from, Date until, NamespaceType prefix, ObjectType set, int offset) {
        ListRecordsType result = null;
        int pageSize = configuration.getOaiRecordsPageSize();
        long changedObjects = operationDao.countChangedObjects(from, until, NamespaceType.METS, null);
        if (changedObjects > 0L) {
            ResumptionToken token = null;
            operationDao.lockTable(true);
            if (changedObjects - offset - pageSize > 0) {
                int newOffset = offset + pageSize;
                token = createResumptionToken(from, until, prefix, set, newOffset, VerbType.LIST_RECORDS.name());
            }
            List<Operation> changes = operationDao.getChanges(from, until, prefix, set, offset, pageSize);
            result = new ListRecordsType();
            List<RecordType> records = result.getRecord();
            for (Operation operation : changes) {
                records.add(createRecord(operation));
            }
            if (offset > 0 && token == null || token != null) {
                result.setResumptionToken(createToken(offset, changedObjects, token));
            }
        }
        return result;
    }


    /**
     * Finds token context in the database object for specified operation type and token value.
     * 
     * @param resumptionToken
     *            resumption token value to be found.
     * @param operationType
     *            type of operation the token is supposed to concern.
     * @return token corresponding found in the database.
     * @throws IllegalOaiPmhTokenException
     *             when specified token for specified operation could not be found.
     */
    @SuppressWarnings("unchecked")
    private ResumptionToken findToken(String resumptionToken, String operationType)
            throws IllegalOaiPmhTokenException {
        ResumptionTokenFilterFactory filterFactory = resumptionTokenDao.createQueryModifier().getQueryFilterFactory();
        QueryFilter<ResumptionToken> filter = filterFactory.and(filterFactory.byId(resumptionToken),
            filterFactory.byType(operationType));
        List<ResumptionToken> tokens = resumptionTokenDao.findBy(filter, false);
        if (tokens == null || tokens.size() != 1) {
            throw new IllegalOaiPmhTokenException("Cannot not find specified token.");
        } else {
            ResumptionToken result = tokens.get(0);
            if (result.getExpirationDate().before(resumptionTokenDao.getDatabaseDate())) {
                resumptionTokenDao.delete(result);
                throw new IllegalOaiPmhTokenException("Cannot not find specified token.");
            }
            return result;
        }
    }


    /**
     * Creates new OAI-PMH token entry from the existing data.
     * 
     * @param offset
     *            offset of the request position of first record returned in unlimited query.
     * @param changedObjects
     *            calculated number of changed objects.
     * @param token
     *            resumption token and its context.
     * @return new OAI-PMH resumption token entry.
     */
    private ResumptionTokenType createToken(int offset, Long changedObjects, ResumptionToken token) {
        ResumptionTokenType result = new ResumptionTokenType();
        result.setCompleteListSize(BigInteger.valueOf(changedObjects));
        result.setCursor(BigInteger.valueOf(offset));
        if (token != null) {
            result.setExpirationDate(token.getExpirationDate());
            result.setValue(token.getId());
        }
        return result;
    }


    /**
     * Creates OAI-PMH header.
     * 
     * @param operation
     *            metadata operation entity.
     * @return header build basing on passed operation entity.
     */
    private HeaderType createHeader(Operation operation) {
        HeaderType result = new HeaderType();
        result.setIdentifier(operation.getObject().getDefaultIdentifier().getIdentifier());
        result.setDatestamp(operation.getDate());
        if (operation.getOperation() == OperationType.DELETION) {
            result.setStatus(StatusType.DELETED);
        }
        result.getSetSpec().add(operation.getObject().getType().name());
        return result;
    }


    /**
     * Creates OAI-PMH record.
     * 
     * @param operation
     *            operation entity.
     * @return record build basing on passed operation entity.
     */
    private RecordType createRecord(Operation operation) {
        RecordType result = new RecordType();
        result.setHeader(createHeader(operation));
        MetadataType metadata = new MetadataType();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("Problem with creation of document builder." + e.toString());
            throw new WrdzRuntimeException(e);
        }

        Document document = null;
        Element element = null;

        try {
            document = documentBuilder.parse(new ByteArrayInputStream(operation.getContents().getBytes("UTF-8")));
        } catch (Exception e) {
            throw new WrdzRuntimeException("Improper XML entry in the database, failed to parse it.", e);
        }
        element = document.getDocumentElement();
        metadata.setAny(element);
        result.setMetadata(metadata);
        return result;
    }


    /**
     * Creates and saves to database new resumption token entity.
     * 
     * @param from
     *            beginning of period by which harvesting is done.
     * @param until
     *            end of period by which harvesting is done.
     * @param prefix
     *            metadata prefix name.
     * @param set
     *            set by which results are filtered.
     * @param offset
     *            request's offset.
     * @param requestType
     *            request type.
     * @return created resumption token.
     */
    private ResumptionToken createResumptionToken(Date from, Date until, NamespaceType prefix, ObjectType set,
            int offset, String requestType) {
        int validFor = configuration.getOaiTokenLongetivity();
        ResumptionToken result = new ResumptionToken();
        result.setId(UUID.randomUUID().toString());
        result.setFrom(from);
        result.setOffset(offset);
        result.setPrefix(prefix);
        result.setUntil(until);
        result.setType(requestType);
        result.setSet(set);
        Calendar cal = new GregorianCalendar();
        cal.setTime(operationDao.getDatabaseDate());
        cal.add(Calendar.DAY_OF_MONTH, validFor);
        result.setExpirationDate(cal.getTime());
        resumptionTokenDao.persist(result);
        return result;
    }


    /**
     * Fetches the specified object's metadata in requested format.
     * 
     * @param identifier
     *            object's identifier.
     * @param metadataType
     *            metadata format.
     * @return object's metadata in requested format or <code>null</code> if none was found.
     * @throws ObjectNotFoundException
     *             if requested object was not found.
     */
    @SuppressWarnings("unchecked")
    private Operation findRecord(String identifier, NamespaceType metadataType)
            throws ObjectNotFoundException {
        DigitalObjectFilterFactory digitalObjectFilterFactory = digitalObjectDao.createQueryModifier()
                .getQueryFilterFactory();
        DigitalObject object = digitalObjectDao.findSingleResultBy(digitalObjectFilterFactory.byIdentifier(identifier));
        if (object == null) {
            throw new ObjectNotFoundException("No object with identifier " + identifier + " could be found.");
        }
        QueryModifier<OperationFilterFactory, OperationSorterBuilder, Operation> queryModifier = operationDao
                .createQueryModifier();
        OperationFilterFactory operationFilterFactory = queryModifier.getQueryFilterFactory();
        QueryFilter<Operation> byDigitalObject = operationFilterFactory.byDigitalObject(object.getId());
        QueryFilter<Operation> byMetadataType = operationFilterFactory.byMetadataType(metadataType);
        OperationSorterBuilder operationSorterBuilder = queryModifier.getQuerySorterBuilder();
        return operationDao.findFirstResultBy(operationFilterFactory.and(byDigitalObject, byMetadataType),
            operationSorterBuilder.byDate(false).buildSorter());
    }

}
