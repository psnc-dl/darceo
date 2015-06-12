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

import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.mdz.dao.format.FileFormatDao;
import pl.psnc.synat.wrdz.mdz.entity.format.FileFormat;
import pl.psnc.synat.wrdz.mdz.message.MdzMessenger;

/**
 * Default file format processor implementation.
 * 
 * <p>
 * The {@link #processAll()} method works by continuously calling the {@link #processOne()} method via a proxy until all
 * formats have been processed or the processing is cancelled using the {@link Future#cancel(boolean)} method.
 * 
 * Each {@link #processOne()} execution happens in a separate transaction to ensure that errors will not cause all
 * previous processing results to be rolled back.
 */
@Stateless
public class FileFormatProcessorBean implements FileFormatProcessor {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FileFormatProcessorBean.class);

    /** Injected session context. */
    @Resource
    private SessionContext ctx;

    /** File format DAO. */
    @EJB
    private FileFormatDao formatDao;

    /** File format verifier, used to evaluate the loss risk. */
    @EJB
    private FileFormatVerifier formatVerifier;

    /** Communications manager. */
    @EJB
    private MdzMessenger messenger;


    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<Void> processAll() {
        logger.info("Format processing started");

        FileFormatProcessor proxy = ctx.getBusinessObject(FileFormatProcessor.class);

        int count = 0;
        boolean finished = false;

        while (!ctx.wasCancelCalled() && !finished) {
            boolean processed = proxy.processOne();
            if (processed) {
                count++;
            }
            finished = !processed;
        }

        logger.info("Format processing stopped: checked {} format(s)", count);

        return new AsyncResult<Void>(null);
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean processOne() {

        FileFormat format = formatDao.get();
        if (format != null) {
            if (formatVerifier.isMigrationRequired(format)) {
                messenger.notifyMigrationRequired(format);
            }
            formatDao.delete(format);
            return true;
        }
        return false;
    }
}
