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
package pl.psnc.synat.wrdz.zmd.download.adapters;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import pl.psnc.synat.wrdz.zmd.download.ConnectionInformation;
import pl.psnc.synat.wrdz.zmd.exception.DownloadAdapterException;

/**
 * An adapter class which provides functionality of handling content downloading via HTTP protocol.
 */
public class HttpDownloadAdapter extends AbstractDownloadAdapter implements DownloadAdapter {

    /**
     * Authorization scope for the adapter.
     */
    private final AuthScope authScope;

    /**
     * Credentials for the connection.
     */
    private final UsernamePasswordCredentials usernamePasswordCredentials;


    /**
     * Constructs new HTTP adapter using provided information.
     * 
     * @param connectionInfo
     *            connection information for SFTP protocol.
     * @param cacheHome
     *            cache root folder.
     */
    public HttpDownloadAdapter(ConnectionInformation connectionInfo, String cacheHome) {
        super(connectionInfo, cacheHome);
        authScope = new AuthScope(connectionInfo.getHost(), connectionInfo.getPort() == null ? -1
                : connectionInfo.getPort());
        String password = connectionInfo.getPassword();
        if (password != null && !password.isEmpty()) {
            usernamePasswordCredentials = new UsernamePasswordCredentials(connectionInfo.getUsername(), password);
        } else {
            usernamePasswordCredentials = null;
        }
    }


    @Override
    public String downloadFile(URI uri, String relativePath)
            throws DownloadAdapterException {
        String cachedFilePath = getResourceCachePath(relativePath);
        checkDestinationExistence(cachedFilePath);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        ReadableByteChannel rbc = null;
        FileOutputStream output = null;
        try {
            if (usernamePasswordCredentials != null) {
                httpclient.getCredentialsProvider().setCredentials(authScope, usernamePasswordCredentials);
            }
            HttpResponse response = httpclient.execute(new HttpGet(uri));
            HttpEntity entity = response.getEntity();
            if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                rbc = Channels.newChannel(entity.getContent());
                output = new FileOutputStream(cachedFilePath);
                output.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                EntityUtils.consume(entity);
            } else {
                EntityUtils.consume(entity);
                throw new DownloadAdapterException("Http error code or empty content was returned instead of resource.");
            }

        } catch (IOException e) {
            throw new DownloadAdapterException("Exception caught while downloading file contents to the cache.", e);
        } finally {
            httpclient.getConnectionManager().shutdown();
            try {
                if (rbc != null) {
                    rbc.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                throw new DownloadAdapterException("Exception caught while closing input/output streams.", e);
            }
        }
        return cachedFilePath;
    }
}
