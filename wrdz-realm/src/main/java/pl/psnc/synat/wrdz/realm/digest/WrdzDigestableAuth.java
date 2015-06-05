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
package pl.psnc.synat.wrdz.realm.digest;

import java.nio.charset.CharacterCodingException;

import com.sun.enterprise.security.auth.digest.api.Password;

/**
 * Interface for authentication using HTTP digest, provides convenient methods for performing such authentication.
 */
public interface WrdzDigestableAuth {

    /**
     * Name of the parameter carrying name of the digest algorithm.
     */
    String PARAM_DIGEST_ALGORITHM = "digest-algorithm";

    /**
     * Value of the parameter carrying specifying no digest algorithm.
     */
    String NO_DIGEST = "none";

    /**
     * Message for unsupported digest algorithm.
     */
    String UNSUPPORTED_DIGEST_ALGORITHM = "WrdzUserRealm does not support digest algorithm ";

    /**
     * Name of the parameter carrying the charset.
     */
    String PARAM_CHARSET = "charset";

    /**
     * Name of the parameter carrying the encoding.
     */
    String PARAM_ENCODING = "encoding";

    /**
     * Hexadecimal encoding type.
     */
    String HEX = "hex";

    /**
     * Base64 encoding type.
     */
    String BASE64 = "base64";

    /**
     * Default encoding type.
     */
    String DEFAULT_ENCODING = HEX;

    /**
     * Name of the parameter carrying the password encoding algorithm.
     */
    String PARAM_DIGEST_PASSWORD_ENC_ALGORITHM = "digestrealm-password-enc-algorithm";


    /**
     * Extracts password information from the given representations.
     * 
     * @param password
     *            encoded representation of the password.
     * @param passwordBytes
     *            byte representation of the password.
     * @return object representing user's password.
     */
    Password extractPasswordInformation(final char[] password, final byte[] passwordBytes);


    /**
     * Hashes the user's password with the salt.
     * 
     * @param password
     *            user's password.
     * @param salt
     *            salt
     * @return hashed user's password.
     * @throws CharacterCodingException
     *             should character encoding cause any problems.
     */
    char[] hashPassword(char[] password, byte[] salt)
            throws CharacterCodingException;


    /**
     * Compares two passwords' hashes.
     * 
     * @param hashedPassword
     *            hashed password passed as login parameter.
     * @param retrievedPassword
     *            hashed password stored in the user's realm.
     * @return indicator whether or not both hashes are equal.
     */
    boolean comparePasswords(char[] hashedPassword, char[] retrievedPassword);

}
