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
package pl.psnc.synat.dsa.sftp.util;

import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.exception.DataStorageResourceException;
import pl.psnc.synat.dsa.sftp.config.Credential;

/**
 * Provides static utility methods for configuration saved in a database.
 * 
 */
public final class ConfigUtil {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);

    /**
     * Entity manager factory.
     */
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("postgres-dsa");

    /**
     * Secret to decrypt keys.
     */
    private static byte[] s = new byte[] { (byte) 0xbd, (byte) 0x4c, (byte) 0x14, (byte) 0x9d, (byte) 0x61,
            (byte) 0x87, (byte) 0xc5, (byte) 0x51, (byte) 0xa7, (byte) 0xd1, (byte) 0x3e, (byte) 0x4c, (byte) 0x5a,
            (byte) 0x2f, (byte) 0x76, (byte) 0xc6 };


    /**
     * Private constructor.
     */
    private ConfigUtil() {
    }


    /**
     * Gets from database a random credential for the organization.
     * 
     * @param organization
     *            organization
     * @return credential
     * @throws DataStorageResourceException
     *             when there is no credential for given organization
     */
    @SuppressWarnings("unchecked")
    public static Credential getCredentialForOrganization(String organization)
            throws DataStorageResourceException {
        logger.debug("organization: " + organization);
        EntityManager em = emf.createEntityManager();
        try {
            Query query = em.createNamedQuery("Credential.findByOrganization");
            query.setParameter("organization", organization);
            List<Credential> results = query.getResultList();
            if ((results == null) || (results.size() == 0)) {
                throw new DataStorageResourceException("There is no credential for organization " + organization);
            }
            Credential credential = results.get(0);
            logger.debug("credential username: " + credential.getUsername());
            em.detach(credential);
            return credential;
        } finally {
            em.close();
        }
    }


    /**
     * Decrypt data (keys).
     * 
     * @param data
     *            encrypted key
     * @return original key
     * @throws DataStorageResourceException
     *             when some problem with decryption occurred
     */
    public static byte[] decrypt(byte[] data)
            throws DataStorageResourceException {
        SecretKeySpec skeySpec = new SecretKeySpec(s, "AES");
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
        } catch (Exception e) {
            throw new DataStorageResourceException("There is some problem with an AES implementation.", e);
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        } catch (Exception e) {
            throw new DataStorageResourceException("There is some problem with the key to AES.", e);
        }
        try {
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new DataStorageResourceException("There is some problem with decrypting a key by AES.", e);
        }
    }

}
