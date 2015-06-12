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
package pl.psnc.synat.wrdz.zmd.object;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectDao;
import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectFilterFactory;
import pl.psnc.synat.wrdz.zmd.dto.object.FileHashDto;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.hash.DataFileHash;
import pl.psnc.synat.wrdz.zmd.entity.object.hash.MetadataFileHash;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile;

/**
 * Default implementation of {@link FileHashBrowser}.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class FileHashBrowserBean implements FileHashBrowser {

    /** Digital object DAO. */
    @EJB
    private DigitalObjectDao objectDao;


    @Override
    public List<FileHashDto> getFileHashes(String identifier)
            throws ObjectNotFoundException {
        DigitalObjectFilterFactory queryFilterFactory = objectDao.createQueryModifier().getQueryFilterFactory();

        @SuppressWarnings("unchecked")
        DigitalObject object = objectDao.findFirstResultBy(queryFilterFactory.and(
            queryFilterFactory.byIdentifier(identifier), queryFilterFactory.byCurrentVersionState(true)));

        if (object == null) {
            throw new ObjectNotFoundException("Object with identifier " + identifier + " does not exist");
        }

        List<FileHashDto> results = new ArrayList<FileHashDto>();

        for (DataFileVersion fileVersion : object.getCurrentVersion().getFiles()) {
            DataFile file = fileVersion.getDataFile();

            // data file hashes
            for (DataFileHash hash : file.getHashes()) {
                results.add(new FileHashDto(file.getObjectFilepath(), hash.getHashType(), hash.getHashValue()));
            }

            // extracted metadata files
            for (MetadataFile metadata : file.getExtractedMetadata()) {
                for (MetadataFileHash hash : metadata.getHashes()) {
                    results.add(new FileHashDto(metadata.getObjectFilepath(), hash.getHashType(), hash.getHashValue()));
                }
            }

            // provided metadata files
            for (MetadataFile metadata : fileVersion.getProvidedMetadata()) {
                for (MetadataFileHash hash : metadata.getHashes()) {
                    results.add(new FileHashDto(metadata.getObjectFilepath(), hash.getHashType(), hash.getHashValue()));
                }
            }
        }

        return results;
    }
}
