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
package pl.psnc.synat.wrdz.common.async;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult;
import pl.psnc.synat.wrdz.common.rest.exception.InternalServerErrorException;

/**
 * Base class for all services that need to serves the asynchronous requests.
 */
public abstract class AsyncRequestService {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(AsyncRequestService.class);


    /**
     * Build response from result for asynchronous request.
     * 
     * @param result
     *            result for asynchronous request
     * @return response
     */
    protected Response buildResponse(AsyncRequestResult result) {
        ResponseBuilder builder = Response.status(result.getCode());
        if (result.hasContent()) {
            try {
                File file = new File(getAsyncResponseFolder() + "/" + result.getId());
                FileInputStream response = new FileInputStream(file);
                builder.entity(response);
                long length = file.length();
                if (length > 0) {
                    builder.header(AsyncRequestServiceConsts.HTTP_HEADER_CONTENT_LENGTH, length);
                }
            } catch (FileNotFoundException e) {
                logger.error("File with the response not found!", e);
                throw new InternalServerErrorException();
            }
            builder.type(result.getContentType());
            if (result.getFilename() != null) {
                builder.header(AsyncRequestServiceConsts.HTTP_HEADER_CONTENT_DISPOSITION,
                    AsyncRequestServiceConsts.HTTP_HEADER_CONTENT_DISPOSITION_FILENAME + result.getFilename());
            }
        }
        return builder.build();
    }


    /**
     * Returns root folder with files with results for asynchronous request.
     * 
     * @return root folder with files with results
     */
    protected abstract String getAsyncResponseFolder();

}
