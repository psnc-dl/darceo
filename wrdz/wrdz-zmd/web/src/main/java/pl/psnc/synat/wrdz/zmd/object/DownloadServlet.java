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
package pl.psnc.synat.wrdz.zmd.object;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import pl.psnc.synat.wrdz.common.async.AsyncReadRequestAlreadyPrepared;
import pl.psnc.synat.wrdz.common.async.AsyncRequestFetcher;
import pl.psnc.synat.wrdz.common.async.AsyncRequestNotFoundException;
import pl.psnc.synat.wrdz.common.config.Configuration;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequest;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult;
import pl.psnc.synat.wrdz.zmd.object.async.ObjectAsyncReadRequestProcessor;
import pl.psnc.synat.wrdz.zmd.object.async.ObjectAsyncRequestEnum;

/**
 * Handles object downloads.
 */
public class DownloadServlet extends HttpServlet {

    /** Serial version UID. */
    private static final long serialVersionUID = -3331172378029612390L;

    /** Request parameter/attribute holding the fetch request identifier. */
    private static final String R_REQUEST_ID = "requestId";

    /** Request parameter/attribute holding the object identifier. */
    private static final String R_OBJECT_ID = "objectId";

    /** Request parameter/attribute holding the object version. */
    private static final String R_VERSION = "version";

    /** Request attribute holding the status of the fetch operation. */
    private static final String R_STATUS = "status";

    /** Information page (for when content is not immediately available). */
    private static final String INFO_PAGE = "/WEB-INF/pages/info.xhtml";

    /** Status constant: ready. */
    private static final String STATUS_READY = "ready";

    /** Status constant: wait for content. */
    private static final String STATUS_IN_PROGRESS = "in progress";

    /** Status constant: error. */
    private static final String STATUS_ERROR = "error";

    /** Http header name: content length. */
    private static final String H_CONTENT_LENGTH = "Content-Length";

    /** Http header name: content disposition. */
    private static final String H_CONTENT_DISPOSITION = "Content-Disposition";

    /** Http header value prefix: content disposition. */
    private static final String H_CONTENT_DISPOSITION_PREFIX = "filename=";

    /** Fetches asynchronous request responses. Duh. */
    @EJB
    protected AsyncRequestFetcher asyncRequestFetcherBean;

    /** Executes fetch requests. */
    @EJB
    private ObjectAsyncReadRequestProcessor objectAsyncReadRequestProcessor;

    /** Configuration. */
    @Inject
    private Configuration config;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String requestId = req.getParameter(R_REQUEST_ID);
        if (requestId != null) {
            checkStatus(requestId, resp);
        } else {
            String objectId = req.getParameter(R_OBJECT_ID);
            Integer version = null;
            if (StringUtils.isNotEmpty(req.getParameter(R_VERSION))) {
                try {
                    version = Integer.valueOf(req.getParameter(R_VERSION));
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            }
            fetchObject(objectId, version, req, resp);
        }
    }


    /**
     * Retrieves the object and copies its data to the given response. If the data is not yet ready, request is
     * forwarded to the info page instead.
     * 
     * @param objectId
     *            object identifier
     * @param version
     *            object version (can be <code>null</code>)
     * @param req
     *            servlet request
     * @param resp
     *            servlet response
     * @throws ServletException
     *             ...
     * @throws IOException
     *             ...
     */
    private void fetchObject(String objectId, Integer version, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String requestId = null;
        try {
            requestId = objectAsyncReadRequestProcessor.processGetObject(objectId, version, true, true);
        } catch (ObjectNotFoundException e) {
            displayInfo(STATUS_ERROR, req, resp);
            return;
        } catch (AsyncReadRequestAlreadyPrepared e) {
            forwardData(e.getResult(), resp);
            return;
        } catch (Exception e) {
            displayInfo(STATUS_ERROR, req, resp);
            return;
        }

        req.setAttribute(R_REQUEST_ID, requestId);
        req.setAttribute(R_OBJECT_ID, objectId);
        req.setAttribute(R_VERSION, version);
        displayInfo(STATUS_IN_PROGRESS, req, resp);
    }


    /**
     * Checks the status of the given fetch controller. Unless an error has occurred, the response is set to 200 OK and
     * contains the status of the requested object fetching process.
     * 
     * @param requestId
     *            fetch request identifier
     * @param resp
     *            servlet response
     * @throws IOException
     *             ...
     * @see #STATUS_READY
     * @see #STATUS_IN_PROGRESS
     */
    private void checkStatus(String requestId, HttpServletResponse resp)
            throws IOException {

        try {
            AsyncRequest request = asyncRequestFetcherBean.getAsyncRequest(requestId);
            if (!request.getType().equals(ObjectAsyncRequestEnum.FETCH_OBJECT.getBaseTypeName())
                    || !request.getSubtype().equals(ObjectAsyncRequestEnum.FETCH_OBJECT.getName())) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } else if (request.isInProgress()) {
                resp.getWriter().write(STATUS_IN_PROGRESS);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else if (request.getResult().getCode() == HttpServletResponse.SC_OK) {
                resp.getWriter().write(STATUS_READY);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.getWriter().write(STATUS_ERROR);
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (AsyncRequestNotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
    }


    /**
     * Forwards the result data to the given response.
     * 
     * @param result
     *            fetch request result
     * @param resp
     *            servlet response
     * @throws IOException
     *             ...
     */
    private void forwardData(AsyncRequestResult result, HttpServletResponse resp)
            throws IOException {

        if (result.hasContent()) {
            InputStream data = null;
            OutputStream output = null;
            try {
                File file = new File(config.getAsyncCacheHome() + "/" + result.getId());

                long length = file.length();
                if (length > 0) {
                    resp.addHeader(H_CONTENT_LENGTH, String.valueOf(length));
                }
                if (result.getFilename() != null) {
                    resp.addHeader(H_CONTENT_DISPOSITION, H_CONTENT_DISPOSITION_PREFIX + result.getFilename());
                }

                data = new FileInputStream(file);
                output = resp.getOutputStream();
                IOUtils.copyLarge(data, output);
            } catch (FileNotFoundException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            } finally {
                IOUtils.closeQuietly(data);
                IOUtils.closeQuietly(output);
            }
        }
    }


    /**
     * Forwards the request to the info page, setting the given status.
     * 
     * @param status
     *            current request status
     * @param req
     *            servlet request
     * @param resp
     *            servlet response
     * @throws ServletException
     *             ...
     * @throws IOException
     *             ...
     * @see #INFO_PAGE
     * @see #R_STATUS
     */
    private void displayInfo(String status, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute(R_STATUS, status);
        req.getRequestDispatcher(INFO_PAGE).forward(req, resp);
    }
}
