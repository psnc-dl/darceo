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
package pl.psnc.synat.wrdz.mdz.format;

import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.mdz.config.MdzConfiguration;
import pl.psnc.synat.wrdz.mdz.dao.format.FileFormatDao;
import pl.psnc.synat.wrdz.mdz.entity.format.FileFormat;
import pl.psnc.synat.wrdz.zmd.format.DataFileFormatBrowser;

/**
 * Default implementation of the file format work initializer.
 * 
 * Populates the database with active file formats fetched from ZMD. Which types of digital objects are taken into
 * account when searching for active formats depends on the module configuration.
 * 
 * @see DataFileFormatBrowser#getActiveFormatPuids(boolean, boolean, boolean)
 * @see MdzConfiguration
 */
@Singleton
public class FileFormatWorkInitializerBean implements FileFormatWorkInitializer {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FileFormatWorkInitializerBean.class);

    /** Format browser used to fetch formats from ZMD. */
    @EJB(name = "DataFileFormatBrowser")
    private DataFileFormatBrowser formatBrowser;

    /** File format DAO. */
    @EJB
    private FileFormatDao formatDao;

    /** Module configuration. */
    @Inject
    private MdzConfiguration configuration;


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public synchronized void initializeWork() {

        if (formatDao.countAll() > 0) {
            logger.info("Previous cycle not finished, skipping work initialization");
        } else {
            logger.info("Work initialization started");

            boolean analyzeMaster = configuration.getAnalyzeMasterObjectFileFormats();
            boolean analyzeOptimized = configuration.getAnalyzeOptimizedObjectFileFormats();
            boolean analyzeConverted = configuration.getAnalyzeConvertedObjectFileFormats();

            Set<String> puids = formatBrowser.getActiveFormatPuids(analyzeMaster, analyzeOptimized, analyzeConverted);

            int count = 0;
            for (String puid : puids) {
                if (puid != null) {
                    FileFormat format = new FileFormat();
                    format.setPuid(puid);
                    formatDao.persist(format);
                    count++;

                    // flush periodically
                    if (count % 100 == 0) {
                        formatDao.flush();
                    }
                }
            }

            logger.info("Work initialization finished: imported {} format(s)", count);
        }
    }
}
