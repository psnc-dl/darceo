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
package pl.psnc.synat.wrdz.mdz.integrity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.zmd.dto.object.FileHashDto;
import pl.psnc.synat.wrdz.zmd.entity.types.HashType;
import pl.psnc.synat.wrdz.zmd.object.FileHashBrowser;
import pl.psnc.synat.wrdz.zmd.object.ObjectNotFoundException;

/**
 * Default implementation of {@link IntegrityVerifier}.
 */
@Stateless
public class IntegrityVerifierBean implements IntegrityVerifier {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(IntegrityVerifierBean.class);

    /** Hash browser used to fetch file hashes from ZMD. */
    @EJB(name = "FileHashBrowser")
    private FileHashBrowser hashBrowser;


    @Override
    public boolean isCorrupted(String identifier, File file) {
        ZipFile zip = null;
        try {
            zip = new ZipFile(file);

            List<FileHashDto> fileHashes = hashBrowser.getFileHashes(identifier);
            for (FileHashDto fileHash : fileHashes) {

                ZipEntry entry = zip.getEntry(fileHash.getObjectFilepath());
                if (entry == null) {
                    // file not found in the archive
                    return true;
                }

                String calculatedHash;

                InputStream stream = zip.getInputStream(entry);
                try {
                    calculatedHash = calculateHash(stream, fileHash.getHashType());
                } finally {
                    IOUtils.closeQuietly(stream);
                }

                if (!calculatedHash.equals(fileHash.getHashValue())) {
                    return true;
                }
            }

        } catch (ZipException e) {
            throw new WrdzRuntimeException("Given file is not a valid zip archive", e);
        } catch (NoSuchAlgorithmException e) {
            throw new WrdzRuntimeException("Unable to locate appropriate hashing algorithm classes", e);
        } catch (ObjectNotFoundException e) {
            throw new WrdzRuntimeException("Digital object not found in ZMD", e);
        } catch (IOException e) {
            throw new WrdzRuntimeException("Error while verifying object integrity", e);
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (IOException e) {
                    logger.warn("Could not close the zip file", e);
                }
            }
        }
        return false;
    }


    /**
     * Calculates the hash value for data from the given stream using the given hash type.
     * 
     * @param stream
     *            data to be hashed
     * @param type
     *            type of hash to calculate
     * @return calculated hash value
     * @throws NoSuchAlgorithmException
     *             if classes for the given type's hashing algorithm could not be found
     * @throws IOException
     *             if there are any problems with the data stream
     */
    private String calculateHash(InputStream stream, HashType type)
            throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(type.getAlgorithmName());
        byte[] dataBytes = new byte[1024];
        int nread = 0;
        while ((nread = stream.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }
        return type.toHexFormat(md.digest());
    }
}
