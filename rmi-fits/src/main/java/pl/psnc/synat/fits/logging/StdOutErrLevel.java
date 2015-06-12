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

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.util.logging.Level;

/**
 * Class defining 2 new Logging levels, one for STDOUT, one for STDERR, used when multiplexing STDOUT and STDERR into
 * the same rolling log file via the Java Logging APIs.
 */
public class StdOutErrLevel extends Level {

    /**
     * Private constructor
     */
    private StdOutErrLevel(String name, int value) {
        super(name, value);
    }


    /**
     * Level for STDOUT activity.
     */
    public static Level STDOUT = new StdOutErrLevel("STDOUT", Level.INFO.intValue() + 53);
    /**
     * Level for STDERR activity
     */
    public static Level STDERR = new StdOutErrLevel("STDERR", Level.INFO.intValue() + 54);


    /**
     * Method to avoid creating duplicate instances when deserializing the object.
     * 
     * @return the singleton instance of this <code>Level</code> value in this classloader
     * @throws ObjectStreamException
     *             If unable to deserialize
     */
    protected Object readResolve()
            throws ObjectStreamException {
        if (this.intValue() == STDOUT.intValue())
            return STDOUT;
        if (this.intValue() == STDERR.intValue())
            return STDERR;
        throw new InvalidObjectException("Unknown instance :" + this);
    }

}
