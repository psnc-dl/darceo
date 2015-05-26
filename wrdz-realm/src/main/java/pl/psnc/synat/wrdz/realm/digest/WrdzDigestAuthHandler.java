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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;

import pl.psnc.synat.wrdz.realm.WrdzUserRealm;

import com.sun.enterprise.security.auth.digest.api.Password;
import com.sun.enterprise.security.auth.realm.BadRealmException;
import com.sun.logging.LogDomains;

/**
 * Handles authentication calls.
 */
public class WrdzDigestAuthHandler implements WrdzDigestableAuth {

    /**
     * System logger.
     */
    private static final Logger logger = LogDomains.getLogger(WrdzDigestAuthHandler.class, LogDomains.SECURITY_LOGGER);

    /**
     * Array containing hexadecimal symbols in ascending order.
     */
    private static final char[] HEXADECIMAL = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f' };

    /**
     * Digest algorithm name.
     */
    private final String digestAlgorithm;

    /**
     * Object responsible for message digestion..
     */
    private MessageDigest messageDigest = null;

    /**
     * Encoding name.
     */
    private final String encoding;

    /**
     * Character set name.
     */
    private final String charset;

    /**
     * Digest encoding algorithm name.
     */
    private final String digestEncAlgorithm;


    /**
     * Constructs new authentication handler initializing it with realm's properties.
     * 
     * @param properties
     *            realm's properties.
     * @param digestAlgorithm
     *            name of the digest algorithm to use.
     * @throws BadRealmException
     *             should passed digest algorithm be unsupported by the realm.
     */
    public WrdzDigestAuthHandler(Properties properties, String digestAlgorithm)
            throws BadRealmException {
        String receivedEncoding = properties.getProperty(PARAM_ENCODING);
        this.digestAlgorithm = digestAlgorithm;
        charset = properties.getProperty(PARAM_CHARSET);
        if (!NO_DIGEST.equalsIgnoreCase(digestAlgorithm)) {
            try {
                messageDigest = MessageDigest.getInstance(digestAlgorithm);
            } catch (NoSuchAlgorithmException e) {
                throw new BadRealmException(UNSUPPORTED_DIGEST_ALGORITHM + digestAlgorithm);
            }
        }
        if (messageDigest != null && receivedEncoding == null) {
            this.encoding = DEFAULT_ENCODING;
        } else {
            this.encoding = receivedEncoding;
        }
        digestEncAlgorithm = properties.getProperty(PARAM_DIGEST_PASSWORD_ENC_ALGORITHM);
    }


    /**
     * Sets digest-related properties of the specified realm.
     * 
     * @param realm
     *            realm which properties will be set.
     */
    public void setRealmProperties(WrdzUserRealm realm) {
        realm.setProperty(PARAM_DIGEST_ALGORITHM, digestAlgorithm);
        if (encoding != null) {
            realm.setProperty(PARAM_ENCODING, encoding);
        }
        if (charset != null) {
            realm.setProperty(PARAM_CHARSET, charset);
        }
        if (digestEncAlgorithm != null) {
            realm.setProperty(PARAM_DIGEST_PASSWORD_ENC_ALGORITHM, digestEncAlgorithm);
        }
    }


    @Override
    public Password extractPasswordInformation(final char[] password, final byte[] passwordBytes) {
        if (digestEncAlgorithm == null) {
            return new Password() {

                public byte[] getValue() {
                    return passwordBytes;
                }


                public int getType() {
                    return Password.PLAIN_TEXT;
                }


                public String getAlgorithm() {
                    return null;
                }
            };
        } else {
            return new Password() {

                public byte[] getValue() {
                    if (HEX.equals(encoding)) {
                        return hexDecode(password);
                    } else if (BASE64.equals(encoding)) {
                        return base64Decode(password);
                    }
                    return null;
                }


                public int getType() {
                    return Password.HASHED;
                }


                public String getAlgorithm() {
                    return digestEncAlgorithm;
                }
            };
        }
    }


    @Override
    public char[] hashPassword(char[] password, byte[] salt)
            throws CharacterCodingException {
        byte[] bytes = ArrayUtils.addAll(Charset.forName(charset).encode(CharBuffer.wrap(password)).array(), salt);
        if (messageDigest != null) {
            synchronized (messageDigest) {
                messageDigest.reset();
                bytes = messageDigest.digest(bytes);
            }
        }
        if (HEX.equalsIgnoreCase(encoding)) {
            return hexEncode(bytes);
        } else if (BASE64.equalsIgnoreCase(encoding)) {
            return base64Encode(bytes);
        } else { // no encoding specified
            return Charset.forName(charset).decode(ByteBuffer.wrap(bytes)).array();
        }
    }


    @Override
    public boolean comparePasswords(char[] hashedPassword, char[] retrievedPassword) {
        int noOfChars = retrievedPassword.length;
        if (HEX.equalsIgnoreCase(encoding)) {
            for (int i = 0; i < noOfChars; i++) {
                if (!(Character.toLowerCase(retrievedPassword[i]) == Character.toLowerCase(hashedPassword[i]))) {
                    return false;
                }
            }
        } else {
            return Arrays.equals(retrievedPassword, hashedPassword);
        }
        return true;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((charset == null) ? 0 : charset.hashCode());
        result = prime * result + ((digestAlgorithm == null) ? 0 : digestAlgorithm.hashCode());
        result = prime * result + ((digestEncAlgorithm == null) ? 0 : digestEncAlgorithm.hashCode());
        result = prime * result + ((encoding == null) ? 0 : encoding.hashCode());
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
        if (!(obj instanceof WrdzDigestAuthHandler)) {
            return false;
        }
        WrdzDigestAuthHandler other = (WrdzDigestAuthHandler) obj;
        if (charset == null) {
            if (other.charset != null) {
                return false;
            }
        } else if (!charset.equals(other.charset)) {
            return false;
        }
        if (digestAlgorithm == null) {
            if (other.digestAlgorithm != null) {
                return false;
            }
        } else if (!digestAlgorithm.equals(other.digestAlgorithm)) {
            return false;
        }
        if (digestEncAlgorithm == null) {
            if (other.digestEncAlgorithm != null) {
                return false;
            }
        } else if (!digestEncAlgorithm.equals(other.digestEncAlgorithm)) {
            return false;
        }
        if (encoding == null) {
            if (other.encoding != null) {
                return false;
            }
        } else if (!encoding.equals(other.encoding)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WrdzDigestAuthHandler [digestAlgorithm=").append(digestAlgorithm).append(", encoding=")
                .append(encoding).append(", charset=").append(charset).append(", digestEncAlgorithm=")
                .append(digestEncAlgorithm).append("]");
        return builder.toString();
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


    /**
     * Decodes hex encoded array of characters into byte array.
     * 
     * @param encoded
     *            encoded array of hex symbols.
     * @return decoded array of bytes.
     */
    private static byte[] hexDecode(char[] encoded) {
        int len = encoded.length;
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(encoded[i], 16) << 4) + Character.digit(encoded[i + 1], 16));
        }
        return data;
    }


    /**
     * Decodes base64 encoded array of characters into byte array.
     * 
     * @param encoded
     *            base64 encoded array of characters.
     * @return decoded array of bytes.
     */
    private static byte[] base64Decode(char[] encoded) {
        return Base64.decodeBase64(String.valueOf(encoded));
    }

}
