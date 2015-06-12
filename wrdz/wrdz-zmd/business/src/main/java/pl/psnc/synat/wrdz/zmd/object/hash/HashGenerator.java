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
package pl.psnc.synat.wrdz.zmd.object.hash;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.common.performance.Profiler;
import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.hash.DataFileHash;
import pl.psnc.synat.wrdz.zmd.entity.object.hash.MetadataFileHash;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile;
import pl.psnc.synat.wrdz.zmd.entity.types.HashType;
import pl.psnc.synat.wrdz.zmd.output.OutputTask;

/**
 * Generates hashes for object's content files.
 */
public class HashGenerator implements Serializable {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(HashGenerator.class);

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -4861796216021116652L;

    /**
     * Module configuration.
     */
    @Inject
    private ZmdConfiguration zmdConfiguration;

    /**
     * Default hash type read from the configuration.
     */
    private HashType defaultHashType;


    /**
     * Initializes the generator.
     */
    @PostConstruct
    public void initialize() {
        defaultHashType = zmdConfiguration.getDefaultHashType();
    }


    /**
     * Produces new {@link DataFileHash} object containing the hash information about the given file.
     * 
     * @param source
     *            cached data file handle.
     * @param file
     *            data file entity to associate with this hash.
     * @return constructed and already one-side associated hash entity.
     */
    public DataFileHash getDataFileHash(OutputTask source, DataFile file) {
        logger.debug("generating hash for data file " + source);
        DataFileHash hash = new DataFileHash();
        hash.setDataFile(file);
        hash.setHashType(defaultHashType);
        try {
            hash.setHashValue(getHashForFile(source.getCachePath()));
        } catch (NoSuchAlgorithmException e) {
            throw new WrdzRuntimeException("Unable to locate appropriate hashing algorithm classes.", e);
        } catch (IOException e) {
            throw new WrdzRuntimeException("Unable to reach cached file or read it properly.", e);
        }
        return hash;
    }


    /**
     * Produces new {@link MetadataFileHash} object containing the hash information about the given file.
     * 
     * @param source
     *            cached metadata file handle.
     * @param file
     *            metadata file entity to associate with this hash.
     * @return constructed and already one-side associated hash entity.
     */
    public MetadataFileHash getMetadataFileHash(OutputTask source, MetadataFile file) {
        logger.debug("generating hash for metadata file " + source);
        MetadataFileHash hash = new MetadataFileHash();
        hash.setMetadataFile(file);
        hash.setHashType(defaultHashType);
        try {
            hash.setHashValue(getHashForFile(source.getCachePath()));
        } catch (NoSuchAlgorithmException e) {
            throw new WrdzRuntimeException("Unable to locate appropriate hashing algorithm classes.", e);
        } catch (IOException e) {
            throw new WrdzRuntimeException("Unable to reach cached file or read it properly.", e);
        }
        return hash;
    }


    /**
     * Extracts the hash of the file and returns it in a hexadecimal format.
     * 
     * @param path
     *            path to the cached file.
     * @return returns generated hash.
     * @throws NoSuchAlgorithmException
     *             if no suitable algorithm was found (i.e. configuration states use of unhandled hash type)
     * @throws IOException
     *             if problems with reading file from the given path occur.
     */
    private String getHashForFile(String path)
            throws NoSuchAlgorithmException, IOException {

        Profiler.start("hash generation");
        try {
            MessageDigest md = MessageDigest.getInstance(defaultHashType.getAlgorithmName());
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(path);
                byte[] dataBytes = new byte[1024];
                int nread = 0;
                while ((nread = fis.read(dataBytes)) != -1) {
                    md.update(dataBytes, 0, nread);
                }
            } catch (Exception e) {
                logger.error("exception thrown while generating hash", e);
            } finally {
                IOUtils.closeQuietly(fis);
            }

            return defaultHashType.toHexFormat(md.digest());
        } finally {
            Profiler.stop("hash generation");
        }
    }

}
