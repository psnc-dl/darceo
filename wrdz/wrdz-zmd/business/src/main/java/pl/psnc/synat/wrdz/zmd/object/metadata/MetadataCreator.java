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
package pl.psnc.synat.wrdz.zmd.object.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceRecognitionException;
import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceRecognizer;
import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceRecognizerFactory;
import pl.psnc.synat.wrdz.common.metadata.xmlns.XmlNamespaces;
import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.entity.object.hash.MetadataFileHash;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile;
import pl.psnc.synat.wrdz.zmd.object.hash.HashGenerator;
import pl.psnc.synat.wrdz.zmd.object.helpers.MetadataNamespaceDictionaryBean;
import pl.psnc.synat.wrdz.zmd.output.OutputTask;

/**
 * Provides methods for handling and building metadata information and structures in the database.
 */
abstract class MetadataCreator {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(MetadataCreator.class);

    /**
     * File hash generator.
     */
    @Inject
    private HashGenerator hashGenerator;

    /**
     * ZMD module configuration.
     */
    @Inject
    private ZmdConfiguration zmdConfiguration;

    /**
     * Metadata namespace dictionary bean.
     */
    @EJB
    private MetadataNamespaceDictionaryBean metadataNamespaceDictionaryBean;


    /**
     * Fills {@link MetadataFile} object with data from {@linkplain OutputTask} representing this metadata.
     * 
     * @param metadataFile
     *            metadata file.
     * @param source
     *            metadata file's output task.
     */
    protected void fillWithData(MetadataFile metadataFile, OutputTask source) {
        String[] metadataPath = source.getInnerPath().split("[" + "/" + "]");
        metadataFile.setFilename(metadataPath[metadataPath.length - 1]);
        metadataFile.setObjectFilepath(source.getInnerPath());
        metadataFile.setCachePath(source.getCachePath());
        if (zmdConfiguration.constructMetsMetadata()
                || zmdConfiguration.extractTechnicalMetadata()
                || !zmdConfiguration.getDefaultAdministrativeMetadataType().equals(
                    AdministrativeMetadataScheme.NONE.name())) {
            getAndUpdateMetadataNamespaces(metadataFile, source);
        }
        List<MetadataFileHash> hashes = new ArrayList<MetadataFileHash>();
        hashes.add(hashGenerator.getMetadataFileHash(source, metadataFile));
        metadataFile.setHashes(hashes);
        metadataFile.setSize((new File(source.getCachePath()).length()));
    }


    /**
     * Extracts information about namespaces used in the passed metadata file and update metadata file according these
     * information.
     * 
     * @param metadataFile
     *            metadata file
     * @param outputTask
     *            output task representing metadata file.
     */
    private void getAndUpdateMetadataNamespaces(MetadataFile metadataFile, OutputTask outputTask) {
        NamespaceRecognizer recognizer = NamespaceRecognizerFactory.getInstance().getNamespaceRecognizer();
        XmlNamespaces xmlNamespaces = null;
        try {
            xmlNamespaces = recognizer.getNamespaces(outputTask.getCachePath());
        } catch (NamespaceRecognitionException e) {
            throw new WrdzRuntimeException("Unable to recognize namespaces of the file properly.", e);
        }
        if (xmlNamespaces != null) {
            metadataFile.setUsedNamespaces(metadataNamespaceDictionaryBean.getMetadataNamespaces(xmlNamespaces
                    .getUsedNamespaces()));
            metadataFile.setMainNamespace(metadataNamespaceDictionaryBean.getMetadataNamespace(xmlNamespaces
                    .getMainNamespace()));
        }
    }

}
