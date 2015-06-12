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
import java.util.List;

import javax.ejb.EJB;

import org.openarchives.oai.pmh.MetadataFormatType;
import org.openarchives.oai.pmh.OAIPMHerrorType;
import org.openarchives.oai.pmh.OAIPMHerrorcodeType;
import org.openarchives.oai.pmh.OaiPmh;
import org.openarchives.oai.pmh.RequestType;

import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataNamespace;

/**
 * Class offering methods for basic response creation operations.
 */
public class OaiPmhResponseCreator {

    /**
     * OAI-PMH manager backing bean.
     */
    @EJB
    private OaiPmhManager oaiPmhManager;


    /**
     * Creates error response.
     * 
     * @param requestUri
     *            uri of the request.
     * @param message
     *            error message.
     * @param errorType
     *            error type.
     * @return constructed OAI-PMH error response.
     */
    public OaiPmh createError(String requestUri, String message, OAIPMHerrorcodeType errorType) {
        OaiPmh returned = new OaiPmh();
        RequestType request = new RequestType();
        request.setValue(requestUri);
        returned.setRequest(request);
        List<OAIPMHerrorType> errors = returned.getError();
        OAIPMHerrorType error = new OAIPMHerrorType();
        error.setCode(errorType);
        error.setValue(message);
        errors.add(error);
        returned.setResponseDate(oaiPmhManager.getCurrentTime());
        return returned;
    }


    /**
     * Creates empty response.
     * 
     * @param query
     *            OAI-PMH query.
     * @return constructed OAI-PMH empty response.
     */
    public OaiPmh createEmptyResponse(OaiParametersParser query) {
        OaiPmh result = new OaiPmh();
        RequestType request = new RequestType();
        request.setValue(query.getUri());
        request.setVerb(query.getVerb());
        request.setFrom(query.getFrom());
        request.setIdentifier(query.getIdentifier());
        request.setMetadataPrefix(query.getMetadataPrefix() == null ? null : query.getMetadataPrefix().toString());
        request.setResumptionToken(query.getResumptionToken());
        request.setSet(query.getSet() == null ? null : query.getSet().toString());
        request.setUntil(query.getUntil());
        result.setRequest(request);
        result.setResponseDate(oaiPmhManager.getCurrentTime());
        return result;
    }


    /**
     * Transforms datamodel representation of metadata namespaces to OAI-PMH format.
     * 
     * @param namespaces
     *            list of namespaces to transtofm.
     * @return list of OAI-PMH metadata formats.
     */
    public List<MetadataFormatType> transformMetadataFormats(List<MetadataNamespace> namespaces) {
        List<MetadataFormatType> result = new ArrayList<MetadataFormatType>();
        for (MetadataNamespace namespace : namespaces) {
            MetadataFormatType type = new MetadataFormatType();
            type.setMetadataNamespace(namespace.getXmlns());
            type.setMetadataPrefix(namespace.getType().toString());
            type.setSchema(namespace.getSchemaLocation());
            result.add(type);
        }
        return result;
    }

}
