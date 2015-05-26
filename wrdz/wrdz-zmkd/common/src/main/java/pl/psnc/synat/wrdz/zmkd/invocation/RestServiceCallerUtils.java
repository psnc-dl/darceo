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
package pl.psnc.synat.wrdz.zmkd.invocation;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.zmkd.service.RequestType;

/**
 * Utility methods for REST service caller.
 */
public final class RestServiceCallerUtils {

    /**
     * Private constructor.
     */
    private RestServiceCallerUtils() {
    }


    /**
     * content type text/plain; charset = UTF-8.
     */
    private static final ContentType TEXTPLAIN = ContentType.create("text/plain", Consts.UTF_8);

    /**
     * Conetent-Disposition header.
     */
    private static final String CONTENT_DISPOSITION = "Content-Disposition";

    /**
     * Conetent-Disposition header filename value.
     */
    private static final String CONTENT_DISPOSITION_FILENAME = "filename=";


    /**
     * Construct request URI and headers of the service based upon passed information.
     * 
     * @param execHopInfo
     *            info how to execute a REST service
     * @return request URI and headers of the service
     * @throws URISyntaxException
     *             when correct URI cannot be constructed
     */
    public static HttpRequestBase constructServiceRequestBase(ExecutionInfo execHopInfo)
            throws URISyntaxException {
        switch (execHopInfo.getMethod()) {
            case GET:
                return new HttpGet(constructServiceURI(execHopInfo.getAddress(), execHopInfo.getTemplateParams(),
                    execHopInfo.getQueryParams()));
            case POST:
                return new HttpPost(constructServiceURI(execHopInfo.getAddress(), execHopInfo.getTemplateParams(),
                    execHopInfo.getQueryParams()));
            case PUT:
                return new HttpPut(constructServiceURI(execHopInfo.getAddress(), execHopInfo.getTemplateParams(),
                    execHopInfo.getQueryParams()));
            default:
                throw new WrdzRuntimeException("Not yet implemented HTTP method: " + execHopInfo.getMethod());
        }
    }


    /**
     * Constructs URI of the service based upon passed information.
     * 
     * @param address
     *            template address of the service
     * @param templateParams
     *            template parameters
     * @param queryParams
     *            query parameters
     * @return URI of the service
     * @throws URISyntaxException
     *             when correct URI cannot be constructed
     */
    private static URI constructServiceURI(String address, List<ExecutionTemplateParam> templateParams,
            List<ExecutionQueryParam> queryParams)
            throws URISyntaxException {
        StringBuffer sb = new StringBuffer(address);
        for (ExecutionTemplateParam templateParam : templateParams) {
            String varParam = "{" + templateParam.getName() + "}";
            int idx = sb.indexOf(varParam);
            if (idx != -1) {
                sb.replace(idx, idx + varParam.length(), templateParam.getValue());
            }
        }
        boolean first = true;
        for (ExecutionQueryParam queryParam : queryParams) {
            if (first) {
                sb.append("?");
                first = false;
            } else {
                sb.append("&");
            }
            if (queryParam.getValues() != null) {
                for (String value : queryParam.getValues()) {
                    sb.append(queryParam.getName()).append("=").append(value);
                }
            } else {
                sb.append(queryParam.getName());
            }
        }
        return new URI(sb.toString());
    }


    /**
     * Construct request entity for the service based upon passed information.
     * 
     * @param execHopInfo
     *            info how to execute a REST service
     * @return request entity for the service
     */
    public static HttpEntity constructServiceRequestEntity(ExecutionInfo execHopInfo) {
        switch (execHopInfo.getRequestType()) {
            case OCTETSTREAM:
                return constructOctetStreamEntity(execHopInfo.getRequestParam());
            case FORMDATA:
                return constructFormDataEntity(execHopInfo.getFormParams());
            case FORMURLENCODED:
                return constructFormUrlEncodedEntity(execHopInfo.getFormParams());
            default:
                throw new WrdzRuntimeException("Not yet implemented request type: " + execHopInfo.getRequestType());
        }
    }


    /**
     * Construct request header for the service based upon passed information.
     * 
     * @param execHopInfo
     *            info how to execute a REST service
     * @return request header for the service
     */
    public static Header constructServiceRequestHeader(ExecutionInfo execHopInfo) {
        if (execHopInfo.getRequestType().equals(RequestType.OCTETSTREAM)) {
            if (execHopInfo.getRequestParam() != null && execHopInfo.getRequestParam().getFileValue() != null
                    && execHopInfo.getRequestParam().getFileValue().getFilename() != null) {
                return new BasicHeader(CONTENT_DISPOSITION, CONTENT_DISPOSITION_FILENAME
                        + execHopInfo.getRequestParam().getFileValue().getFilename());
            }
        }
        return null;
    }


    /**
     * Constructs octet-stream entity based upon the specified value or the file and its mimetype (ie. request
     * parameter).
     * 
     * @param requestParam
     *            request parameter
     * @return request octet-stream entity for the service
     */
    private static HttpEntity constructOctetStreamEntity(ExecutionBodyParam requestParam) {
        if (requestParam == null) {
            return null;
        }
        if (requestParam.getValue() != null) {
            return new StringEntity(requestParam.getValue(), TEXTPLAIN);
        }
        if (requestParam.getFileValue() != null) {
            try {
                ContentType contentType = ContentType.create(requestParam.getFileValue().getMimetype());
                return new FileEntity(requestParam.getFileValue().getFile(), contentType);
            } catch (IllegalArgumentException e) {
                return new FileEntity(requestParam.getFileValue().getFile());
            }
        }
        return null;
    }


    /**
     * Constructs application/form-data entity based upon the specified parameters.
     * 
     * @param formParams
     *            form parameters
     * @return request application/form-data entity for the service
     */
    private static HttpEntity constructFormDataEntity(List<ExecutionFormParam> formParams) {
        MultipartEntity entity = new MultipartEntity();
        try {
            for (ExecutionFormParam formParam : formParams) {
                if (formParam.getValues() != null) {
                    for (String value : formParam.getValues()) {
                        entity.addPart(formParam.getName(), new StringBody(value, Consts.UTF_8));
                    }
                } else if (formParam.getFiles() != null) {
                    for (FileValue fileValue : formParam.getFiles()) {
                        FileBody fileBody = new FileBody(fileValue.getFile(), fileValue.getFilename(),
                                fileValue.getMimetype(), null);
                        entity.addPart(formParam.getName(), fileBody);
                    }
                } else {
                    entity.addPart(formParam.getName(), new StringBody("", Consts.UTF_8));
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new WrdzRuntimeException("The encoding " + Consts.UTF_8 + " is not supported");
        }
        return entity;
    }


    /**
     * Constructs application/x-www-form-urlencoded entity based upon the specified parameters.
     * 
     * @param formParams
     *            form parameters
     * @return request application/x-www-form-urlencoded entity for the service
     */
    private static HttpEntity constructFormUrlEncodedEntity(List<ExecutionFormParam> formParams) {
        List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
        for (ExecutionFormParam formParam : formParams) {
            if (formParam.getValues() != null) {
                for (String value : formParam.getValues()) {
                    BasicNameValuePair pair = new BasicNameValuePair(formParam.getName(), value);
                    pairs.add(pair);
                }
            } else {
                BasicNameValuePair pair = new BasicNameValuePair(formParam.getName(), null);
                pairs.add(pair);
            }
        }
        return new UrlEncodedFormEntity(pairs, Consts.UTF_8);
    }


    /**
     * Retrieves outcome from the HTTP response.
     * 
     * @param response
     *            HTTP response
     * @return outcome
     * @throws IllegalStateException
     *             if content stream cannot be created
     * @throws IOException
     *             if the stream could not be obtained
     */
    public static ExecutionOutcome retrieveOutcome(HttpResponse response)
            throws IllegalStateException, IOException {
        ExecutionOutcome execOutcome = new ExecutionOutcome();
        if (response.getStatusLine() != null) {
            execOutcome.setStatusCode(response.getStatusLine().getStatusCode());
        }
        if (response.getEntity() != null) {
            execOutcome.setContent(response.getEntity().getContent());
            if (response.getEntity().getContentLength() >= 0) {
                execOutcome.setContentLength(response.getEntity().getContentLength());
            }
            if (response.getEntity().getContentType() != null) {
                execOutcome.setContentType(response.getEntity().getContentType().getValue());
            }
        }
        Header[] headers = response.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            execOutcome.addHeader(headers[i].getName(), headers[i].getValue());
        }
        return execOutcome;
    }

}
