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

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.dao.QueryModifier;
import pl.psnc.synat.wrdz.ru.dao.registries.RemoteRegistryDao;
import pl.psnc.synat.wrdz.ru.dao.registries.RemoteRegistryFilterFactory;
import pl.psnc.synat.wrdz.ru.dao.registries.RemoteRegistrySorterBuilder;
import pl.psnc.synat.wrdz.ru.entity.registries.RemoteRegistry;
import pl.psnc.synat.wrdz.ru.exceptions.EntryCreationException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryDeletionException;
import pl.psnc.synat.wrdz.ru.exceptions.EntryModificationException;
import pl.psnc.synat.wrdz.ru.exceptions.UserCertificateException;
import pl.psnc.synat.wrdz.ru.users.CertificateInformation;
import pl.psnc.synat.wrdz.zu.user.SystemUserManager;

/**
 * Class managing the {@link RemoteRegistry} entities basic operations.
 */
@Stateless
public class RemoteRegistryManagerBean implements RemoteRegistryManager {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(RemoteRegistryManagerBean.class);

    /** Remote registry DAO for persistence operations. */
    @EJB
    private RemoteRegistryDao remoteRegistryDao;

    /** User manager. */
    @EJB(name = "SystemUserManager")
    private SystemUserManager userManager;


    @Override
    public RemoteRegistry createRemoteRegistry(RemoteRegistry added, String certificate)
            throws EntryCreationException {
        RemoteRegistryFilterFactory filterFactory = remoteRegistryDao.createQueryModifier().getQueryFilterFactory();
        List<RemoteRegistry> list = remoteRegistryDao
                .findBy(filterFactory.byLocationUrl(added.getLocationUrl()), false);
        if (list != null && list.size() > 0) {
            throw new EntryCreationException("Registry with given location already extists, try to modify it.");
        }

        X509Certificate x509Certificate = loadCertificate(certificate);
        CertificateInformation information = CertificateInformation.parseNameString(x509Certificate
                .getSubjectX500Principal().getName());

        String username = information.getDisplayName().replaceAll(" ", "");
        userManager.createSystemUser(username, certificate, information.getDisplayName(),
            information.getOrganizationName());

        added.setUsername(username);

        remoteRegistryDao.persist(added);
        return added;
    }


    @Override
    public RemoteRegistry updateRemoteRegistry(RemoteRegistry modified, String certificate)
            throws EntryModificationException {
        RemoteRegistryFilterFactory filterFactory = remoteRegistryDao.createQueryModifier().getQueryFilterFactory();

        QueryFilter<RemoteRegistry> filter = filterFactory.not(filterFactory.byId(modified.getId()));
        filter = filterFactory.and(filter, filterFactory.byLocationUrl(modified.getLocationUrl()));
        List<RemoteRegistry> list = remoteRegistryDao.findBy(filter, false);
        if (list != null && list.size() > 0) {
            throw new EntryModificationException("Registry with given location already extists, try to modify it.");
        }

        X509Certificate x509Certificate = loadCertificate(certificate);
        CertificateInformation information = CertificateInformation.parseNameString(x509Certificate
                .getSubjectX500Principal().getName());

        String username = information.getDisplayName().replaceAll(" ", "");

        RemoteRegistry registry = remoteRegistryDao.findById(modified.getId());
        if (registry == null) {
            throw new EntryModificationException("Specified entry not found in the database.");
        }

        if (!username.equals(registry.getUsername())) {
            userManager.deleteSystemUser(registry.getUsername());
            userManager.createSystemUser(username, certificate, information.getDisplayName(),
                information.getDisplayName());
        } else {
            userManager.updateSystemUser(username, certificate);
        }

        registry.setName(modified.getName());
        registry.setLocationUrl(modified.getLocationUrl());
        registry.setDescription(modified.getDescription());
        registry.setHarvested(modified.isHarvested());
        registry.setReadEnabled(modified.isReadEnabled());
        registry.setLatestHarvestDate(null);
        registry.setUsername(username);

        return registry;
    }


    @Override
    public List<RemoteRegistry> retrieveRemoteRegistries(String location, Boolean readEnabled, Boolean harvested) {
        QueryModifier<RemoteRegistryFilterFactory, RemoteRegistrySorterBuilder, RemoteRegistry> queryModifier = remoteRegistryDao
                .createQueryModifier();
        RemoteRegistryFilterFactory filterFactory = queryModifier.getQueryFilterFactory();
        RemoteRegistrySorterBuilder sorterBuilder = queryModifier.getQuerySorterBuilder();
        QueryFilter<RemoteRegistry> filter = null;
        if (location != null) {
            filter = filterFactory.and(filter, filterFactory.byLocationUrl(location));
        }
        if (readEnabled != null) {
            filter = filterFactory.and(filter, filterFactory.byReadEnabled(readEnabled));
        }
        if (harvested != null) {
            filter = filterFactory.and(filter, filterFactory.byHarvested(harvested));
        }
        sorterBuilder.byLocationUrl(true);
        if (filter != null) {
            return remoteRegistryDao.findBy(filter, sorterBuilder.buildSorter());
        } else {
            return remoteRegistryDao.findAll(sorterBuilder.buildSorter());
        }
    }


    @Override
    public void deleteRemoteRegistry(long id)
            throws EntryDeletionException {
        RemoteRegistry registry = remoteRegistryDao.findById(id);
        if (registry == null) {
            throw new EntryDeletionException("Specified entry not found in the database.");
        }

        userManager.deleteSystemUser(registry.getUsername());
        remoteRegistryDao.delete(registry);
    }


    @Override
    public RemoteRegistry retrieveRemoteRegistry(long id) {
        return remoteRegistryDao.findById(id);
    }


    @Override
    public String retrieveRemoteRegistryCertificate(String registryUsername) {
        return userManager.getCertificate(registryUsername);
    }


    /**
     * Loads the object representation of the certificate from it's byte representation.
     * 
     * @param certificate
     *            PEM representation of the certificate.
     * @return loaded x509 certificate object.
     */
    private X509Certificate loadCertificate(String certificate) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(Base64
                    .decodeBase64(certificate)));
        } catch (CertificateException e) {
            logger.error("Wrong certificate format or data corrupt.", e);
            throw new UserCertificateException("Wrong certificate format or data corrupt.", e);
        }
    }
}
