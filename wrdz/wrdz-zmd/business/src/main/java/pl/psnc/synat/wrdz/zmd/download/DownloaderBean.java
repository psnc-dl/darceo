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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.zmd.exception.DownloadException;
import pl.psnc.synat.wrdz.zmd.input.InputFile;
import pl.psnc.synat.wrdz.zmd.input.InputFileUpdate;
import pl.psnc.synat.wrdz.zmd.object.ObjectStructure;
import pl.psnc.synat.wrdz.zmd.output.OutputFile;
import pl.psnc.synat.wrdz.zmd.output.OutputFileUpdate;

/**
 * A class providing methods for preparing the content for object adding and modification operations that include data
 * fetching.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DownloaderBean implements Downloader {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DownloaderBean.class);

    /**
     * Download manager bean.
     */
    @EJB
    private DownloadManager downloadManager;


    @Override
    public List<OutputFile> downloadFilesToCache(Set<InputFile> inputFiles, String cacheDir)
            throws DownloadException, IllegalArgumentException {
        if (inputFiles == null) {
            throw new IllegalArgumentException("Cannot perform operation for null argument!");
        } else if (inputFiles.isEmpty()) {
            throw new IllegalArgumentException("Cannot perform operation for empty list!");
        }
        List<OutputFile> results = new ArrayList<OutputFile>();
        List<DownloadTask> downloads = new ArrayList<DownloadTask>();
        for (InputFile inputFile : inputFiles) {
            results.add(downloadInputFile(cacheDir, downloads, inputFile));
        }
        return results;
    }


    @Override
    public List<OutputFileUpdate> downloadUpdatedFilesToCache(Set<InputFileUpdate> inputFiles, String cacheDir)
            throws DownloadException, IllegalArgumentException {
        if (inputFiles == null) {
            throw new IllegalArgumentException("Cannot perform operation for null argument!");
        } else if (inputFiles.isEmpty()) {
            throw new IllegalArgumentException("Cannot perform operation for empty list!");
        }
        List<OutputFileUpdate> results = new ArrayList<OutputFileUpdate>();
        List<DownloadTask> downloads = new ArrayList<DownloadTask>();
        for (InputFileUpdate inputFile : inputFiles) {
            results.add(downloadInputFileUpdate(cacheDir, downloads, inputFile));
        }
        return results;
    }


    @Override
    public List<DownloadTask> downloadMetadataToCache(String contentPath, Map<String, URI> resources, String cacheDir)
            throws DownloadException, IllegalArgumentException {
        if (resources == null) {
            throw new IllegalArgumentException("Cannot perform operation for null argument!");
        } else if (resources.isEmpty()) {
            throw new IllegalArgumentException("Cannot perform operation for empty list!");
        }
        List<DownloadTask> downloads = new ArrayList<DownloadTask>();
        for (Entry<String, URI> entry : resources.entrySet()) {
            downloads.add(new DownloadTask(entry.getValue(), ObjectStructure.getPathForProvidedMetadata(contentPath,
                entry.getKey()), entry.getKey()));
        }
        return downloadManager.downloadFiles(downloads, cacheDir);
    }


    /**
     * Downloads input file with it's metadata into download cache.
     * 
     * @param cacheDir
     *            path to the cache directory.
     * @param downloads
     *            list of download tasks to perform by downloader.
     * @param inputFile
     *            information about resources to be downloaded.
     * @return object describing locations of downloaded files in the cache.
     * @throws DownloadException
     *             should any download problems occur.
     */
    private OutputFile downloadInputFile(String cacheDir, List<DownloadTask> downloads, InputFile inputFile)
            throws DownloadException {
        OutputFile result = null;
        logger.debug("Started downloading content file " + inputFile.getSource() + " into cache.");
        String innerPath = ObjectStructure.getPathForContent(inputFile.getDestination());
        downloads.add(new DownloadTask(inputFile.getSource(), innerPath, ObjectStructure
                .getFilenameForContent(inputFile.getDestination())));
        DownloadTask file = downloadManager.downloadFiles(downloads, cacheDir).get(0);
        logger.debug("Content file " + inputFile.getSource() + " cached at " + file.getCachePath());
        Map<String, URI> metadata = inputFile.getMetadataFilesToAdd();
        if (metadata == null || metadata.isEmpty()) {
            logger.debug("No provided metadata for content file " + inputFile.getSource());
            result = new OutputFile(file, inputFile.getSequence(), null);
        } else {
            logger.debug("Started downloading provided metadata for content file " + inputFile.getSource());
            result = new OutputFile(file, inputFile.getSequence(), downloadMetadataToCache(inputFile.getDestination(),
                metadata, cacheDir));
            logger.debug("Finished downloading provided metadata for content file " + inputFile.getSource());
        }
        downloads.clear();
        return result;
    }


    /**
     * Downloads input file update with it's metadata and other modifications into download cache.
     * 
     * @param cacheDir
     *            path to the cache directory.
     * @param downloads
     *            list of download tasks to perform by downloader.
     * @param inputFile
     *            information about resources to be downloaded.
     * @return object describing locations of downloaded files in the cache.
     * @throws DownloadException
     *             should any download problems occur.
     */
    private OutputFileUpdate downloadInputFileUpdate(String cacheDir, List<DownloadTask> downloads,
            InputFileUpdate inputFile)
            throws DownloadException {
        DownloadTask file = null;
        List<DownloadTask> addedMetadata = null;
        List<DownloadTask> modifiedMetadata = null;
        logger.debug("Started downloading content file update" + inputFile.getSource() + " into cache.");
        String innerPath = ObjectStructure.getPathForContent(inputFile.getDestination());
        String filename = ObjectStructure.getFilenameForContent(inputFile.getDestination());
        if (inputFile.getSource() != null) {
            downloads.add(new DownloadTask(inputFile.getSource(), innerPath, filename));
            file = downloadManager.downloadFiles(downloads, cacheDir).get(0);
            logger.debug("Updated content file " + inputFile.getSource() + " cached at " + file.getCachePath());
        } else {
            file = new DownloadTask(null, innerPath, filename);
        }
        Map<String, URI> metadataToAdd = inputFile.getMetadataFilesToAdd();
        if (metadataToAdd == null || metadataToAdd.isEmpty()) {
            logger.debug("No added provided metadata for content file " + inputFile.getSource());
        } else {
            logger.debug("Started downloading added provided metadata for content file " + inputFile.getSource());
            addedMetadata = downloadMetadataToCache(inputFile.getDestination(), metadataToAdd, cacheDir);
            logger.debug("Finished downloading added provided metadata for content file " + inputFile.getSource());
        }
        Map<String, URI> metadataToModify = inputFile.getMetadataFilesToModify();
        if (metadataToModify == null || metadataToModify.isEmpty()) {
            logger.debug("No updated provided metadata for content file " + inputFile.getSource());
        } else {
            logger.debug("Started downloading updated provided metadata for content file " + inputFile.getSource());
            modifiedMetadata = downloadMetadataToCache(inputFile.getDestination(), metadataToModify, cacheDir);
            logger.debug("Finished downloading updated provided metadata for content file " + inputFile.getSource());
        }
        downloads.clear();
        return new OutputFileUpdate(file, inputFile.getSequence(), addedMetadata, modifiedMetadata,
                inputFile.getMetadataFilesToRemove());
    }

}
