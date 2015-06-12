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
package pl.psnc.synat.wrdz.zu.authentication;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.zu.config.ZuConfiguration;

/**
 * Authentication helper methods.
 */
public class AuthenticationHelper {

    /**
     * Array containing hexadecimal symbols in ascending order.
     */
    private static final char[] HEXADECIMAL = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };

    /**
     * Hexadecimal encoding type.
     */
    private static final String HEX = "hex";

    /**
     * Base64 encoding type.
     */
    private static final String BASE64 = "base64";

    /** ZU configuration. */
    @Inject
    private ZuConfiguration configuration;


    /**
     * Hashes the user's password with the salt.
     * 
     * @param password
     *            user's password.
     * @param salt
     *            salt for a user
     * @return hashed user's password.
     * @throws CharacterCodingException
     *             when a character encoding error occurs
     */
    public char[] hashPassword(char[] password, byte[] salt)
            throws CharacterCodingException {
        byte[] bytes = ArrayUtils.addAll(
            Charset.forName(configuration.getPasswordCharset()).encode(CharBuffer.wrap(password)).array(), salt);
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(configuration.getPasswordDigestAlgorithm());
            messageDigest.reset();
            bytes = messageDigest.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new WrdzRuntimeException("Unsupported digest algorithm " + configuration.getPasswordDigestAlgorithm());
        }
        if (HEX.equalsIgnoreCase(configuration.getPasswordEncoding())) {
            return hexEncode(bytes);
        } else if (BASE64.equalsIgnoreCase(configuration.getPasswordEncoding())) {
            return base64Encode(bytes);
        } else { // no encoding specified
            return Charset.forName(configuration.getPasswordCharset()).decode(ByteBuffer.wrap(bytes)).array();
        }
    }


    /**
     * Encodes byte array into hex encoded array of characters.
     * 
     * @param decoded
     *            array of bytes to encode.
     * @return encoded array of hex symbols.
     */
    private char[] hexEncode(byte[] decoded) {
        StringBuilder sb = new StringBuilder(2 * decoded.length);
        for (int i = 0; i < decoded.length; i++) {
            int low = (int) (decoded[i] & 0x0f);
            int high = (int) ((decoded[i] & 0xf0) >> 4);
            sb.append(HEXADECIMAL[high]);
            sb.append(HEXADECIMAL[low]);
        }
        char[] result = new char[sb.length()];
        sb.getChars(0, sb.length(), result, 0);
        return result;
    }


    /**
     * Encodes byte array into base64 encoded array of characters.
     * 
     * @param decoded
     *            array of bytes to encode.
     * @return base64 encoded array of characters.
     */
    private char[] base64Encode(byte[] decoded) {
        return Base64.encodeBase64String(decoded).toCharArray();
    }
}
