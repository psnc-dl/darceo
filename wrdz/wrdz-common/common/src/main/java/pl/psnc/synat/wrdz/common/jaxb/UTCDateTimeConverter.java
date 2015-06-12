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
package pl.psnc.synat.wrdz.common.jaxb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

/**
 * Converter of the xs:dateTime to java.util.Date object.
 */
public final class UTCDateTimeConverter {

    /**
     * Private constructor.
     */
    private UTCDateTimeConverter() {
    }


    /**
     * Parses xs:dateTime and converts its to java.util.Date.
     * 
     * @param date
     *            date in xs:dateTime format
     * @return date
     */
    public static Date parseDateTime(String date) {
        return DatatypeConverter.parseDate(date).getTime();
    }


    /**
     * Prints date in xs:dateTime format.
     * 
     * @param date
     *            date
     * @return date in xs:dateTime format
     */
    public static String printDateTime(Date date) {
        if (date == null) {
            return null;
        }
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        return formatter.format(date);
    }

}
