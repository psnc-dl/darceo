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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formatter which prints only a message as a log record.
 * 
 */
public class MessageFormatter extends Formatter {

    /** Basic log message format. */
    private static final String BASIC_FORMAT = "%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %2$-9s%3$s%n";

    /** Throwable log message format. */
    private static final String THROWABLE_FORMAT = BASIC_FORMAT + "%s%n";


    /**
     * Format the given LogRecord.
     * 
     * @param record
     *            the log record to be formatted.
     * @return a formatted log record
     */
    public String format(LogRecord record) {

        String stackTrace = null;
        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                stackTrace = sw.toString();
            } catch (Exception ex) {
            }
        }

        String format = stackTrace == null ? BASIC_FORMAT : THROWABLE_FORMAT;

        return String.format(format, new Date(record.getMillis()), record.getLevel().getName(), record.getMessage(),
            stackTrace);
    }
}
