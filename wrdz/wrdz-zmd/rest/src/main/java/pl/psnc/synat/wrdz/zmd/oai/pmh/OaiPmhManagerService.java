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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.openarchives.oai.pmh.DeletedRecordType;
import org.openarchives.oai.pmh.GetRecordType;
import org.openarchives.oai.pmh.GranularityType;
import org.openarchives.oai.pmh.IdentifyType;
import org.openarchives.oai.pmh.ListIdentifiersType;
import org.openarchives.oai.pmh.ListMetadataFormatsType;
import org.openarchives.oai.pmh.ListRecordsType;
import org.openarchives.oai.pmh.ListSetsType;
import org.openarchives.oai.pmh.MetadataFormatType;
import org.openarchives.oai.pmh.OAIPMHerrorcodeType;
import org.openarchives.oai.pmh.OaiPmh;
import org.openarchives.oai.pmh.SetType;
import org.openarchives.oai.pmh.VerbType;

import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataNamespace;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;
import pl.psnc.synat.wrdz.zmd.exception.AmbiguousResultException;
import pl.psnc.synat.wrdz.zmd.exception.IllegalOaiPmhArgumentException;
import pl.psnc.synat.wrdz.zmd.exception.IllegalOaiPmhTokenException;
import pl.psnc.synat.wrdz.zmd.exception.IllegalOaiPmhVerbException;
import pl.psnc.synat.wrdz.zmd.exception.UnsupportedMetadataFormatException;
import pl.psnc.synat.wrdz.zmd.oai.pmh.validation.groups.GetRecord;
import pl.psnc.synat.wrdz.zmd.oai.pmh.validation.groups.Identify;
import pl.psnc.synat.wrdz.zmd.oai.pmh.validation.groups.ListIdentifiers;
import pl.psnc.synat.wrdz.zmd.oai.pmh.validation.groups.ListMetadataFormats;
import pl.psnc.synat.wrdz.zmd.oai.pmh.validation.groups.ListRecords;
import pl.psnc.synat.wrdz.zmd.oai.pmh.validation.groups.ListSets;
import pl.psnc.synat.wrdz.zmd.object.ObjectNotFoundException;

/**
 * Service exposing OAI-PMH functionality over the HTTP.
 */
@Path("/oai-pmh/")
@ManagedBean
public class OaiPmhManagerService {

    /**
     * The available set types.
     */
    private static final List<SetType> AVAILABLE_SETS = getSets();

    /**
     * OAI-PMH manager backing bean.
     */
    @EJB
    private OaiPmhManager oaiPmhManager;

    /**
     * Validator object.
     */
    @Inject
    private Validator validator;

    /**
     * OAI-PMH OAI-PMH response creator.
     */
    @Inject
    private OaiPmhResponseCreator responseCreator;

    /**
     * ZMD module configuration.
     */
    @Inject
    private ZmdConfiguration zmdConfiguration;


    /**
     * Method exposing OAI-PMH functionality over the HTTP.
     * 
     * @param uriInfo
     *            URI information.
     * @return OAI-PMH response.
     */
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_XML)
    public OaiPmh oaiOperation(@Context UriInfo uriInfo) {
        OaiParametersParser query = null;
        try {
            query = new OaiParametersParser(uriInfo.getQueryParameters(), uriInfo.getAbsolutePath().toString());
        } catch (AmbiguousResultException e) {
            return responseCreator.createError(uriInfo.getAbsolutePath().toString(), e.getMessage(),
                OAIPMHerrorcodeType.BAD_ARGUMENT);
        } catch (IllegalOaiPmhArgumentException e) {
            return responseCreator.createError(uriInfo.getAbsolutePath().toString(), e.getMessage(),
                OAIPMHerrorcodeType.BAD_ARGUMENT);
        } catch (IllegalOaiPmhVerbException e) {
            return responseCreator.createError(uriInfo.getAbsolutePath().toString(), e.getMessage(),
                OAIPMHerrorcodeType.BAD_VERB);
        } catch (UnsupportedMetadataFormatException e) {
            return responseCreator.createError(uriInfo.getAbsolutePath().toString(), e.getMessage(),
                OAIPMHerrorcodeType.CANNOT_DISSEMINATE_FORMAT);
        }
        VerbType verb = query.getVerb();
        switch (verb) {
            case GET_RECORD:
                return getRecord(query);
            case IDENTIFY:
                return identify(query);
            case LIST_SETS:
                return listSets(query);
            case LIST_METADATA_FORMATS:
                return listMetadataFormats(query);
            case LIST_IDENTIFIERS:
                return listIdentifiers(query);
            case LIST_RECORDS:
                return listRecords(query);
            default:
                return null;
        }
    }


    /**
     * OAI-PMH GetRecord query. For more details on the query see the description <a
     * href="http://www.openarchives.org/OAI/openarchivesprotocol.html#GetRecord">here</a>.
     * 
     * @param query
     *            query parameters.
     * @return OAI-PMH response.
     */
    private OaiPmh getRecord(OaiParametersParser query) {
        OaiPmh result = validateQuery(query, GetRecord.class);
        if (result == null) {
            GetRecordType record = null;
            try {
                record = oaiPmhManager.getRecord(query.getIdentifier(), query.getMetadataPrefix());
            } catch (ObjectNotFoundException e) {
                return responseCreator.createError(query.getUri(), "No item with specified identifier exists.",
                    OAIPMHerrorcodeType.ID_DOES_NOT_EXIST);
            }
            if (record == null) {
                return responseCreator.createError(query.getUri(), "No item with specified identifier exists.",
                    OAIPMHerrorcodeType.ID_DOES_NOT_EXIST);
            }
            result = responseCreator.createEmptyResponse(query);
            result.setGetRecord(record);
        }
        return result;
    }


    /**
     * OAI-PMH Identify query. For more details on the query see the description <a
     * href="http://www.openarchives.org/OAI/openarchivesprotocol.html#Identify">here</a>.
     * 
     * @param query
     *            query parameters.
     * @return OAI-PMH response.
     */
    private OaiPmh identify(OaiParametersParser query) {
        OaiPmh result = validateQuery(query, Identify.class);
        if (result == null) {
            result = responseCreator.createEmptyResponse(query);
            IdentifyType identify = new IdentifyType();
            identify.setRepositoryName(zmdConfiguration.getOaiRepositoryName());
            identify.setBaseURL(query.getUri());
            identify.setEarliestDatestamp(zmdConfiguration.getOaiEarliestDatestamp());
            identify.setGranularity(GranularityType.YYYY_MM_DD_THH_MM_SS_Z);
            identify.setDeletedRecord(DeletedRecordType.PERSISTENT);
            identify.setProtocolVersion("2.0");
            identify.getAdminEmail().addAll(zmdConfiguration.getOaiAdminEmail());
            result.setIdentify(identify);
        }
        return result;
    }


    /**
     * OAI-PMH ListMetadataFormats query. For more details on the query see the description <a
     * href="http://www.openarchives.org/OAI/openarchivesprotocol.html#ListMetadataFormats">here</a>.
     * 
     * @param query
     *            query parameters.
     * @return OAI-PMH response.
     */
    private OaiPmh listMetadataFormats(OaiParametersParser query) {
        OaiPmh result = validateQuery(query, ListMetadataFormats.class);
        if (result == null) {
            List<MetadataNamespace> namespaces = null;
            try {
                namespaces = oaiPmhManager.getNamespaces(query.getIdentifier());
            } catch (ObjectNotFoundException e) {
                return responseCreator.createError(query.getUri(), "No item with specified identifier exists.",
                    OAIPMHerrorcodeType.ID_DOES_NOT_EXIST);
            }
            if (namespaces == null || namespaces.isEmpty()) {
                return responseCreator.createError(query.getUri(), "No metadata formats found.",
                    OAIPMHerrorcodeType.NO_METADATA_FORMATS);
            }
            result = responseCreator.createEmptyResponse(query);
            ListMetadataFormatsType metadataFormats = new ListMetadataFormatsType();
            List<MetadataFormatType> metadataFormat = metadataFormats.getMetadataFormat();
            metadataFormat.addAll(responseCreator.transformMetadataFormats(namespaces));
            result.setListMetadataFormats(metadataFormats);
        }
        return result;
    }


    /**
     * OAI-PMH ListSets query. For more details on the query see the description <a
     * href="http://www.openarchives.org/OAI/openarchivesprotocol.html#ListSets">here</a>.
     * 
     * @param query
     *            query parameters.
     * @return OAI-PMH response.
     */
    private OaiPmh listSets(OaiParametersParser query) {
        OaiPmh result = validateQuery(query, ListSets.class);
        if (result == null) {
            if (query.getResumptionToken() != null) {
                return responseCreator.createError(query.getUri(), "Bad resumption token.",
                    OAIPMHerrorcodeType.BAD_RESUMPTION_TOKEN);
            }
            result = responseCreator.createEmptyResponse(query);
            ListSetsType sets = new ListSetsType();
            List<SetType> set = sets.getSet();
            set.addAll(AVAILABLE_SETS);
            result.setListSets(sets);
        }
        return result;
    }


    /**
     * OAI-PMH ListRecords query. For more details on the query see the description <a
     * href="http://www.openarchives.org/OAI/openarchivesprotocol.html#ListRecords">here</a>.
     * 
     * @param query
     *            query parameters.
     * @return OAI-PMH response.
     */
    private OaiPmh listRecords(OaiParametersParser query) {
        OaiPmh result = validateQuery(query, ListRecords.class);
        if (result == null) {
            ListRecordsType listRecords = null;
            if (query.getResumptionToken() != null) {
                try {
                    listRecords = oaiPmhManager.listRecords(query.getResumptionToken());
                } catch (IllegalOaiPmhTokenException e) {
                    return responseCreator.createError(query.getUri(), "Bad resumption token.",
                        OAIPMHerrorcodeType.BAD_RESUMPTION_TOKEN);
                }
            } else {
                NamespaceType metadataPrefix = query.getMetadataPrefix();
                if (metadataPrefix != NamespaceType.OAI_DC && metadataPrefix != NamespaceType.METS) {
                    return responseCreator.createError(query.getUri(),
                        "Repository does not provide access to specified " + metadataPrefix + " metadata.",
                        OAIPMHerrorcodeType.CANNOT_DISSEMINATE_FORMAT);
                }
                listRecords = oaiPmhManager.listRecords(query.getFrom(), query.getUntil(), query.getMetadataPrefix(),
                    query.getSet(), 0);
            }
            if (listRecords == null) {
                return responseCreator.createError(query.getUri(), "Bad resumption token.",
                    OAIPMHerrorcodeType.NO_RECORDS_MATCH);
            }
            result = responseCreator.createEmptyResponse(query);
            result.setListRecords(listRecords);
        }
        return result;
    }


    /**
     * OAI-PMH ListIdentifiers query. For more details on the query see the description <a
     * href="http://www.openarchives.org/OAI/openarchivesprotocol.html#ListIdentifiers">here</a>.
     * 
     * @param query
     *            query parameters.
     * @return OAI-PMH response.
     */
    private OaiPmh listIdentifiers(OaiParametersParser query) {
        OaiPmh result = validateQuery(query, ListIdentifiers.class);
        if (result == null) {
            ListIdentifiersType listIdentifiers = null;
            if (query.getResumptionToken() != null) {
                try {
                    listIdentifiers = oaiPmhManager.listIdentifiers(query.getResumptionToken());
                } catch (IllegalOaiPmhTokenException e) {
                    return responseCreator.createError(query.getUri(), "Bad resumption token.",
                        OAIPMHerrorcodeType.BAD_RESUMPTION_TOKEN);
                }
            } else {
                NamespaceType metadataPrefix = query.getMetadataPrefix();
                if (metadataPrefix != NamespaceType.OAI_DC && metadataPrefix != NamespaceType.METS) {
                    return responseCreator.createError(query.getUri(),
                        "Repository does not provide access to specified " + metadataPrefix + " metadata.",
                        OAIPMHerrorcodeType.CANNOT_DISSEMINATE_FORMAT);
                }
                if (checkAndFillMissingDateBoundaries(query)) {
                    listIdentifiers = oaiPmhManager.listIdentifiers(query.getFrom(), query.getUntil(),
                        query.getMetadataPrefix(), query.getSet(), 0);
                } else {
                    return responseCreator.createError(query.getUri(),
                        "Illegal date boundary specified, lowe bound before earliest date or/and upper bound from the future. "
                                + metadataPrefix + " metadata.", OAIPMHerrorcodeType.BAD_ARGUMENT);
                }
            }
            if (listIdentifiers == null) {
                return responseCreator.createError(query.getUri(), "Bad resumption token.",
                    OAIPMHerrorcodeType.NO_RECORDS_MATCH);
            }
            result = responseCreator.createEmptyResponse(query);
            result.setListIdentifiers(listIdentifiers);
        }
        return result;
    }


    /**
     * Adds default values of date period boundaries if either of ends is not present and checks the validity of that
     * boundaries.
     * 
     * @param query
     *            query parameters.
     * @return OAI-PMH response.
     */
    private boolean checkAndFillMissingDateBoundaries(OaiParametersParser query) {
        Date from = query.getFrom();
        Date until = query.getUntil();
        Date earliestDate = zmdConfiguration.getOaiEarliestDatestamp();
        Date currentTime = oaiPmhManager.getCurrentTime();
        if (from == null) {
            query.setFrom(earliestDate);
        } else if (from.before(earliestDate)) {
            return false;
        }
        if (until == null) {
            query.setUntil(currentTime);
        } else if (until.after(currentTime)) {
            return false;
        }
        return true;
    }


    /**
     * Validates query due to specified validation group.
     * 
     * @param query
     *            query parameters.
     * @param group
     *            validation group.
     * @return OAI-PMH error response, if validation failed, or <code>null</code>.
     */
    @SuppressWarnings("rawtypes")
    private OaiPmh validateQuery(OaiParametersParser query, Class group) {
        Set<ConstraintViolation<OaiParametersParser>> validate = validator.validate(query, group);
        if (validate.size() > 0) {
            return responseCreator.createError(query.getUri(), "Illegal arguments found for this verb.",
                OAIPMHerrorcodeType.BAD_ARGUMENT);
        }
        return null;
    }


    /**
     * Produces list of supported sets.
     * 
     * @return list of sets available in the repository.
     */
    public static List<SetType> getSets() {
        List<SetType> result = new ArrayList<SetType>();
        for (ObjectType set : ObjectType.values()) {
            SetType temp = new SetType();
            temp.setSetName(set.toString());
            temp.setSetSpec("Collection of digital objects in " + set.toString().toLowerCase() + " format.");
            result.add(temp);
        }
        return result;
    }

}
