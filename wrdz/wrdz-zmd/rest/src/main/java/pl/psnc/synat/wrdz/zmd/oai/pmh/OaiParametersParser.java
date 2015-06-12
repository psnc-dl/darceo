/**
 * 
 */
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.ws.rs.core.MultivaluedMap;

import org.openarchives.oai.pmh.VerbType;

import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;
import pl.psnc.synat.wrdz.zmd.exception.AmbiguousResultException;
import pl.psnc.synat.wrdz.zmd.exception.IllegalOaiPmhArgumentException;
import pl.psnc.synat.wrdz.zmd.exception.IllegalOaiPmhVerbException;
import pl.psnc.synat.wrdz.zmd.exception.UnsupportedMetadataFormatException;
import pl.psnc.synat.wrdz.zmd.oai.pmh.validation.constraints.ValidTokenizableQuery;
import pl.psnc.synat.wrdz.zmd.oai.pmh.validation.groups.GetRecord;
import pl.psnc.synat.wrdz.zmd.oai.pmh.validation.groups.Identify;
import pl.psnc.synat.wrdz.zmd.oai.pmh.validation.groups.ListIdentifiers;
import pl.psnc.synat.wrdz.zmd.oai.pmh.validation.groups.ListMetadataFormats;
import pl.psnc.synat.wrdz.zmd.oai.pmh.validation.groups.ListRecords;
import pl.psnc.synat.wrdz.zmd.oai.pmh.validation.groups.ListSets;

/**
 * Class that parses URL query parameters puuting them into OAI-PMH parameters.
 */
@ValidTokenizableQuery(groups = { ListIdentifiers.class, ListRecords.class })
public class OaiParametersParser {

    /**
     * OAI_PMH <code>verb</code> param name, specifying the OAI_PMH operation.
     */
    private static final String OAIPMH_VERB = "verb";

    /**
     * OAI_PMH <code>resumptionToken</code> param name, specifying the token to next part of chunked query.
     */
    private static final String OAIPMH_RESUMPTION_TOKEN = "resumptionToken";

    /**
     * OAI_PMH <code>metadataPrefix</code> param name, specifying the prefix of metadata type to search for.
     */
    private static final String OAIPMH_METADATA_PREFIX = "metadataPrefix";

    /**
     * OAI_PMH <code>identifier</code> param name, specifying the identifier of the digital object.
     */
    private static final String OAIPMH_IDENTIFIER = "identifier";

    /**
     * OAI_PMH <code>from</code> param name, specifying the beginning of the time period by which to harvest the
     * repository.
     */
    private static final String OAIPMH_FROM = "from";

    /**
     * OAI_PMH <code>until</code> param name, specifying the end of the time period by which to harvest the repository.
     */
    private static final String OAIPMH_UNTIL = "until";

    /**
     * OAI_PMH <code>set</code> param name, specifying the name of the set that harvested objects should belong to.
     */
    private static final String OAIPMH_SET = "set";

    /**
     * Request URI.
     */
    private final String uri;

    /**
     * OAI operation type specified in the request.
     */
    @NotNull(groups = { GetRecord.class, Identify.class, ListIdentifiers.class, ListMetadataFormats.class,
            ListRecords.class, ListSets.class })
    private final VerbType verb;

    /**
     * The beginning of the time period by which to harvest the repository.
     */
    @Null(groups = { GetRecord.class, Identify.class, ListMetadataFormats.class, ListSets.class })
    private Date from;

    /**
     * The end of the time period by which to harvest the repository.
     */
    @Null(groups = { GetRecord.class, Identify.class, ListMetadataFormats.class, ListSets.class })
    private Date until;

    /**
     * The identifier of the digital object.
     */
    @NotNull(groups = { GetRecord.class })
    @Null(groups = { Identify.class, ListIdentifiers.class, ListRecords.class, ListSets.class })
    private final String identifier;

    /**
     * The token to next part of chunked query.
     */
    @Null(groups = { GetRecord.class, Identify.class, ListMetadataFormats.class })
    private final String resumptionToken;

    /**
     * The prefix of metadata type to search for.
     */
    @NotNull(groups = { GetRecord.class })
    @Null(groups = { Identify.class, ListMetadataFormats.class, ListSets.class })
    private final NamespaceType metadataPrefix;

    /**
     * The name of the set that harvested objects should belong to.
     */
    @Null(groups = { GetRecord.class, Identify.class, ListMetadataFormats.class, ListSets.class })
    private final ObjectType set;


    /**
     * Parses the parameters map into object model.
     * 
     * @param queryParameters
     *            parameters received in the query.
     * @param uri
     *            request's URI.
     * @throws AmbiguousResultException
     *             when query contains more than one param with the same name.
     * @throws IllegalOaiPmhArgumentException
     *             when OAI_PMH operation has illegal argument syntax.
     * @throws IllegalOaiPmhVerbException
     *             when OAI_PMH operation misses verb argument.
     * @throws UnsupportedMetadataFormatException
     *             when OAI_PMH operation request unsupported metadata type.
     */
    public OaiParametersParser(MultivaluedMap<String, String> queryParameters, String uri)
            throws AmbiguousResultException, IllegalOaiPmhArgumentException, IllegalOaiPmhVerbException,
            UnsupportedMetadataFormatException {
        try {
            this.verb = getVerb(getParameter(queryParameters, OAIPMH_VERB));
        } catch (IllegalOaiPmhArgumentException e) {
            throw new IllegalOaiPmhVerbException(e.getMessage(), e);
        } catch (AmbiguousResultException e) {
            throw new IllegalOaiPmhVerbException(e.getMessage(), e);
        }
        this.uri = uri;
        this.from = getDate(getParameter(queryParameters, OAIPMH_FROM));
        this.until = getDate(getParameter(queryParameters, OAIPMH_UNTIL));
        this.identifier = getParameter(queryParameters, OAIPMH_IDENTIFIER);
        this.resumptionToken = getParameter(queryParameters, OAIPMH_RESUMPTION_TOKEN);
        this.metadataPrefix = getMetadataPrefix(getParameter(queryParameters, OAIPMH_METADATA_PREFIX));
        this.set = getSet(getParameter(queryParameters, OAIPMH_SET));
        if (queryParameters.keySet().size() > 0) {
            throw new IllegalOaiPmhArgumentException("Unknown parameter(s) in the query.");
        }
    }


    public String getUri() {
        return uri;
    }


    public VerbType getVerb() {
        return verb;
    }


    public Date getFrom() {
        return from;
    }


    public void setFrom(Date from) {
        this.from = from;
    }


    public Date getUntil() {
        return until;
    }


    public void setUntil(Date until) {
        this.until = until;
    }


    public String getIdentifier() {
        return identifier;
    }


    public String getResumptionToken() {
        return resumptionToken;
    }


    public NamespaceType getMetadataPrefix() {
        return metadataPrefix;
    }


    public ObjectType getSet() {
        return set;
    }


    /**
     * Gets the {@link VerbType} which name corresponds to the passed parameter.
     * 
     * @param verb
     *            OAI-PMH verb value.
     * @return OAI-PMH verb object.
     * @throws IllegalOaiPmhVerbException
     *             if given verb value is not supported.
     */
    private VerbType getVerb(String verb)
            throws IllegalOaiPmhVerbException {
        try {
            return VerbType.fromValue(verb);
        } catch (IllegalArgumentException e) {
            throw new IllegalOaiPmhVerbException("Incorrect verb value.", e);
        } catch (NullPointerException e) {
            throw new IllegalOaiPmhVerbException("Parameter verb is required!", e);
        }
    }


    /**
     * Gets the date from the string containing date in OAI-PMH compliant date format.
     * 
     * @param date
     *            string containing date in OAI-PMH compliant date format.
     * @return extracted date.
     * @throws IllegalOaiPmhArgumentException
     *             if the date format was invalid.
     */
    private Date getDate(String date)
            throws IllegalOaiPmhArgumentException {
        if (date != null) {
            DateFormat formatter;
            try {
                if (date.length() == 10) {
                    formatter = new SimpleDateFormat("yyyy-MM-dd");
                } else {
                    formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
                }
                return (Date) formatter.parse(date);
            } catch (ParseException e) {
                throw new IllegalOaiPmhArgumentException(
                        "Illegal date format, should adhere to OAI-PMH UTC date format specyfication.", e);
            }
        }
        return null;
    }


    /**
     * Fetches the parameter value from the map of query parameters.
     * 
     * @param queryParameters
     *            map containing parameters of the query.
     * @param paramName
     *            name of the parameter.
     * @return value of the specified parameter.
     * @throws AmbiguousResultException
     *             when more than one value is assigned to the parameter name.
     * @throws IllegalOaiPmhArgumentException
     *             if parameter value is either null or an empty string.
     */
    private String getParameter(MultivaluedMap<String, String> queryParameters, String paramName)
            throws AmbiguousResultException, IllegalOaiPmhArgumentException {
        List<String> list = queryParameters.get(paramName);
        if (list != null && list.size() > 0) {
            if (list.size() > 1) {
                throw new AmbiguousResultException("Ambigious values for parameter " + paramName
                        + " were found. Max of one value is allowed.");
            }
            String result = list.get(0);
            if (result != null && !result.isEmpty()) {
                queryParameters.remove(paramName);
                return result;
            } else {
                throw new IllegalOaiPmhArgumentException("Null or empty value of parameter is not allowed.");
            }

        }
        queryParameters.remove(paramName);
        return null;
    }


    /**
     * Gets the {@link NamespaceType} which name corresponds to the passed parameter.
     * 
     * @param metadataPrefix
     *            OAI-PMH metadataPrefix value.
     * @return corresponding namespace type.
     * @throws UnsupportedMetadataFormatException
     *             when parameter refers to the unsupported metadata namespace.
     */
    private NamespaceType getMetadataPrefix(String metadataPrefix)
            throws UnsupportedMetadataFormatException {
        if (metadataPrefix == null) {
            return null;
        }
        if (metadataPrefix.toUpperCase().equals(NamespaceType.METS.toString())) {
            return NamespaceType.METS;
        } else if (metadataPrefix.toUpperCase().equals(NamespaceType.OAI_DC.toString())) {
            return NamespaceType.OAI_DC;
        } else {
            throw new UnsupportedMetadataFormatException("Metadata prefix named " + metadataPrefix
                    + " is not supported.");
        }
    }


    /**
     * Gets the {@link ObjectType} corresponding to the specified set name.
     * 
     * @param set
     *            name of the set (type of object).
     * @return corresponding object type.
     * @throws IllegalOaiPmhArgumentException
     *             when specified set does not exist in the repository.
     */
    private ObjectType getSet(String set)
            throws IllegalOaiPmhArgumentException {
        if (set == null) {
            return null;
        }
        try {
            return ObjectType.valueOf(set.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalOaiPmhArgumentException("Set name " + set + " is invalid.", e);
        }
    }
}
