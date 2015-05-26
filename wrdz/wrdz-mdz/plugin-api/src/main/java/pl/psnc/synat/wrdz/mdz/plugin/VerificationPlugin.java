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
package pl.psnc.synat.wrdz.mdz.plugin;

/**
 * An interface for plugins that perform verification of stored digital objects.
 */
public interface VerificationPlugin {

    /**
     * Verifies a single digital object.
     * <p>
     * This method should return a not-<code>null</code> result only if the object did not pass the verification process
     * and someone (e.g. the object's owner or the system administrator) should be notified about this fact. If the
     * object passed the verification process successfully, return <code>null</code> instead.
     * 
     * @param objectIdentifier
     *            stored digital object's identifier
     * @return a result, or <code>null</code> if there is nothing to report
     */
    VerificationResult execute(String objectIdentifier);
}
