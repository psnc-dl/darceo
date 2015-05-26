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
package pl.psnc.synat.wrdz.common.config;

import javax.ejb.ScheduleExpression;

/**
 * Utility methods for handling schedule expressions.
 */
public final class ScheduleUtils {

    /**
     * Constructor.
     */
    private ScheduleUtils() {
        throw new UnsupportedOperationException("No instances");
    }


    /**
     * Creates a copy of the given schedule. Only the hour, minute, day of week, day of month, and month values are
     * copied; other fields are left with their default value.
     * 
     * @param expression
     *            schedule to be cloned
     * @return cloned schedule
     */
    public static ScheduleExpression clone(ScheduleExpression expression) {
        ScheduleExpression clone = new ScheduleExpression();
        clone.hour(expression.getHour());
        clone.minute(expression.getMinute());
        clone.dayOfWeek(expression.getDayOfWeek());
        clone.dayOfMonth(expression.getDayOfMonth());
        clone.month(expression.getMonth());
        return clone;
    }
}
