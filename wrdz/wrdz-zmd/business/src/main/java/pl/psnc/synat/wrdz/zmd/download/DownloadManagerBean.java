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
package pl.psnc.synat.wrdz.zmd.download;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.naming.OperationNotSupportedException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.download.adapters.DownloadAdapter;
import pl.psnc.synat.wrdz.zmd.exception.AmbiguousResultException;
import pl.psnc.synat.wrdz.zmd.exception.DownloadAdapterException;
import pl.psnc.synat.wrdz.zmd.exception.DownloadException;

/**
 * A class responsible for managing the process of downloading of content linked via URL. It ensures that correct
 * {@link DownloadAdapter} is chosen for the protocol indicated in the URL and handles the credentials.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DownloadManagerBean implements DownloadManager {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DownloadManagerBean.class);

    /**
     * Connection helper providing useful methods for managing connection related issues.
     */
    @EJB
    private ConnectionHelper connectionHelper;

    /**
     * ZMD module configuration.
     */
    @Inject
    private ZmdConfiguration zmdConfiguration;

    /**
     * Absolute path to the cache root folder.
     */
    private String cacheHome;


    /**
     * Initializes this EJB.
     */
    @PostConstruct
    public void initialize() {
        cacheHome = zmdConfiguration.getCacheHome();
    }


    @Override
    public List<DownloadTask> downloadFiles(List<DownloadTask> files, String cacheDir)
            throws DownloadException, IllegalArgumentException {
        if (files == null) {
            throw new IllegalArgumentException("Cannot perform operation for null argument!");
        } else if (files.isEmpty()) {
            throw new IllegalArgumentException("Cannot perform operation for empty list!");
        }
        if (cacheDir == null || cacheDir.trim().isEmpty()) {
            throw new IllegalArgumentException("Cannot perform operation for unspecified cache subdir parameter!");
        }
        int itemIndex = 0;
        try {
            for (itemIndex = 0; itemIndex < files.size(); itemIndex++) {
                DownloadTask downloadTask = files.get(itemIndex);
                downloadTask
                        .setCachePath(downloadFromUri(downloadTask.getUri(), cacheDir + downloadTask.getInnerPath()));
            }
        } catch (Exception e) {
            try {
                for (DownloadTask downloadTask : files) {
                    downloadTask.setCachePath(null);
                }
                FileUtils.deleteDirectory(new File(cacheHome + "/" + cacheDir));
            } catch (IOException ioe) {
                logger.debug("Exception raised why cleaning tempo directory: ", ioe);
            }
            throw new DownloadException("Exception downloading resource " + files.get(itemIndex).toString(), e);
        }
        return files;
    }


    @Override
    public String downloadFromUri(URI uri, String relativePath)
            throws DownloadException, IllegalArgumentException {
        if (uri == null) {
            throw new IllegalArgumentException("Cannot download content - given URI is null");
        } else if (!uri.isAbsolute()) {
            throw new IllegalArgumentException("Cannot download content - given URI is not a URL");
        }
        if (relativePath == null || relativePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Cannot download content - given path is empty or null");
        }
        String cachedResourcePath = null;
        if (isLocallyCached(uri)) {
            return (new File(uri.getSchemeSpecificPart())).getAbsolutePath(); // must be getSchemeSpecificPart() because of mounted network drives 
        } else {
            try {
                ConnectionInformation connectionInfo = connectionHelper.getConnectionInformation(uri);
                DownloadAdapter downloadAdapter = getAdapterForProtocolName(uri.getScheme().toUpperCase(),
                    connectionInfo);
                cachedResourcePath = downloadAdapter.downloadFile(uri, relativePath);
            } catch (AmbiguousResultException e) {
                throw new DownloadException("Could not determine connection information to use", e);
            } catch (OperationNotSupportedException e) {
                throw new DownloadException("Cannot download using specified protocol " + uri.getScheme()
                        + ", no compatible adapters present in the system", e);
            } catch (DownloadAdapterException e) {
                throw new DownloadException("An error occured while creating the download adapter", e);
            }
        }
        return cachedResourcePath;
    }


    /**
     * Creates connection adapter appropriate for the given protocol, using provided connection information.
     * 
     * @param protocol
     *            protocol symbol present in the URL.
     * @param connectionInfo
     *            connection information including authentication information.
     * @return adapter suitable for connection to the requested repository.
     * @throws OperationNotSupportedException
     *             if no suitable adapter was found.
     * @throws DownloadAdapterException
     *             if adapter configuration failed.
     */
    private DownloadAdapter getAdapterForProtocolName(String protocol, ConnectionInformation connectionInfo)
            throws OperationNotSupportedException, DownloadAdapterException {
        DownloadAdapterFactory protocolDownloadAdapter = null;
        try {
            protocolDownloadAdapter = DownloadAdapterFactory.valueOf(protocol);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new OperationNotSupportedException("No adapter for the protocol found. Protocol name: " + protocol);
        }
        if (protocolDownloadAdapter != null) {
            return protocolDownloadAdapter.getAdapter(connectionInfo, cacheHome);
        } else {
            return null;
        }
    }


    /**
     * Checks weather the resource is already locally cached/stored on the server that runs the app.
     * 
     * @param uri
     *            resource's URI
     * @return <code>true</code>, if item is stored on local machine, else <code>false</code>
     */
    private boolean isLocallyCached(URI uri) {
        logger.debug("uri of the resource: " + uri);
        String protocol = uri.getScheme();
        if (protocol.toLowerCase().startsWith("file")) {
            // any resource accessible by the file protocol is cached (locally also means by mounted network drives)
            if ((new File(uri.getSchemeSpecificPart())).exists()) {
                return true;
            }
        }
        return false;
    }
}
