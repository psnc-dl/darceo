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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.DatatypeConverter;

/**
 * Converter of the xs:date to java.util.Date object.
 */
public final class XsDateConverter {

    /**
     * Private constructor.
     */
    private XsDateConverter() {
    }


    /**
     * Parses xs:date and converts its to java.util.Date.
     * 
     * @param date
     *            date in xs:date format
     * @return date
     */
    public static Date parseDate(String date) {
        return DatatypeConverter.parseDate(date).getTime();
    }


    /**
     * Prints date in xs:date format.
     * 
     * @param date
     *            date
     * @return date in xs:date format
     */
    public static String printDate(Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        return DatatypeConverter.printDate(cal);
    }

}
