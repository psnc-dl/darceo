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
package pl.psnc.synat.wrdz.zmd.download;

import java.util.Arrays;

/**
 * A class representing certificate information for authentication purposes. Contains both user private key and server's
 * public key;
 */
public class CertificateInformation {

    /**
     * Certificate's passphrase.
     */
    private final String passphrase;

    /**
     * Certificate's public key.
     */
    private final byte[] publicKey;

    /**
     * Certificate's private key.
     */
    private final byte[] privateKey;


    /**
     * Constructs new instance with given private key and passphrase to access it.
     * 
     * @param passphrase
     *            certificate's passphrase.
     * @param privateKey
     *            certificate's privateKey.
     */
    public CertificateInformation(String passphrase, byte[] privateKey) {
        this.passphrase = passphrase;
        this.privateKey = privateKey;
        this.publicKey = null;
    }


    /**
     * Constructs new instance with given private key, public key and passphrase to access it.
     * 
     * @param passphrase
     *            certificate's passphrase.
     * @param privateKey
     *            certificate's private key.
     * @param publicKey
     *            certificate's public key.
     */
    public CertificateInformation(String passphrase, byte[] privateKey, byte[] publicKey) {
        this.passphrase = passphrase;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }


    public String getPassphrase() {
        return passphrase;
    }


    public byte[] getPublicKey() {
        return publicKey;
    }


    public byte[] getPrivateKey() {
        return privateKey;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((passphrase == null) ? 0 : passphrase.hashCode());
        result = prime * result + Arrays.hashCode(privateKey);
        result = prime * result + Arrays.hashCode(publicKey);
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CertificateInformation)) {
            return false;
        }
        CertificateInformation other = (CertificateInformation) obj;
        if (passphrase == null) {
            if (other.passphrase != null) {
                return false;
            }
        } else if (!passphrase.equals(other.passphrase)) {
            return false;
        }
        if (!Arrays.equals(privateKey, other.privateKey)) {
            return false;
        }
        if (!Arrays.equals(publicKey, other.publicKey)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "CertificateInformation [passphrase=" + passphrase + ", publicKey=" + Arrays.toString(publicKey)
                + ", privateKey=" + Arrays.toString(privateKey) + "]";
    }

}
