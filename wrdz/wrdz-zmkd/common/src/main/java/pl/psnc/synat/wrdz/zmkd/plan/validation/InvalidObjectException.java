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
package pl.psnc.synat.wrdz.zmkd.plan.validation;

/**
 * Indicates that the migration plan cannot be applied to the object with the specified identifier.
 * 
 * An object is considered invalid if it doesn't exist, or if it doesn't contain files in the migration plan's input
 * format.
 */
public class InvalidObjectException extends MigrationPlanValidationException {

    /** Serial version UID. */
    private static final long serialVersionUID = -7255685344102794290L;

    /** Object identifier. */
    private final String objectIdentifier;


    /**
     * Constructs a new instance with the given detail message and object identifier.
     * 
     * @param message
     *            the detail message
     * @param objectIdentifier
     *            the invalid object's identifier
     */
    public InvalidObjectException(String message, String objectIdentifier) {
        super(message);
        this.objectIdentifier = objectIdentifier;
    }


    public String getObjectIdentifier() {
        return objectIdentifier;
    }
}
