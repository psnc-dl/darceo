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
package pl.psnc.synat.wrdz.zmkd.invocation;

/**
 * Constants for rest service invocation functionality.
 */
public final class InvocationConsts {

    /**
     * Private constructor.
     */
    private InvocationConsts() {
    }


    /** File type IRI. */
    public static final String FILE_TYPE = "http://darceo.psnc.pl/ontologies/dArceoFile.owl#File";

    /** File bundle IRI. */
    public static final String FILE_BUNDLE_TYPE = "http://darceo.psnc.pl/ontologies/dArceoFile.owl#FileBundle";

    /** Client location IRI. */
    public static final String CLIENT_LOCATION_TYPE = "http://darceo.psnc.pl/ontologies/dArceoDelivery.owl#ClientLocation";

    /** Application/zip mimetype. */
    public static final String MIMETYPE_APPLICATION_ZIP = "application/zip";

}
