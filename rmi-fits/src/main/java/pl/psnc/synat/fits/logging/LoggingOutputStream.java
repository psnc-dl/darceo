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
package pl.psnc.synat.fits.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An OutputStream that writes contents to a Logger upon each call to flush().
 * 
 */
public class LoggingOutputStream extends ByteArrayOutputStream {

    private String lineSeparator;

    private Logger logger;
    private Level level;


    /**
     * Constructor
     * 
     * @param logger
     *            Logger to write to
     * @param level
     *            Level at which to write the log message
     */
    public LoggingOutputStream(Logger logger, Level level) {
        super();
        this.logger = logger;
        this.level = level;
        lineSeparator = System.getProperty("line.separator");
    }


    /**
     * upon flush() write the existing contents of the OutputStream to the logger as a log record.
     * 
     * @throws java.io.IOException
     *             in case of error
     */
    public void flush()
            throws IOException {

        String record;
        synchronized (this) {
            super.flush();
            record = this.toString();
            super.reset();

            if (record.length() == 0 || record.equals(lineSeparator)) {
                // avoid empty records
                return;
            }

            logger.logp(level, "", "", record);
        }
    }

}
