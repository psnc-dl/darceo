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
package pl.psnc.synat.wrdz.ru.registries;

import java.io.IOException;

import javax.ejb.EJB;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.ru.entity.registries.RemoteRegistry;

/**
 * Bean responsible for handling certificate downolad.
 */
@ManagedBean
@ApplicationScoped
public class CertificateBean {

    /**
     * Remote registry manager providing support for remote registry operations.
     */
    @EJB
    private transient RemoteRegistryManager remoteRegistryManager;

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(CertificateBean.class);


    /**
     * Handles download of the specified registry's certificate.
     * 
     * @param registry
     *            registry which certificate is to be downloaded.
     */
    public void downloadCertificate(RemoteRegistry registry) {
        final HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance()
                .getExternalContext().getResponse();
        response.setHeader("Content-Disposition", "attachment;filename=" + registry.getName() + ".der");
        try {
            String pemCertificate = remoteRegistryManager.retrieveRemoteRegistryCertificate(registry.getUsername());
            byte[] derCertificate = Base64.decodeBase64(pemCertificate);
            response.getOutputStream().write(derCertificate);
            response.setContentLength(derCertificate.length);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        FacesContext.getCurrentInstance().responseComplete();
    }

}
