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
package pl.psnc.synat.wrdz.ru.users;

/**
 * Class handling extraction of information from the certificate name string.
 */
public final class CertificateInformation {

    /**
     * Certificate owner's.
     */
    private final String displayName;

    /**
     * Name of the organization certificate owner works for.
     */
    private final String organizationName;


    /**
     * Creates new certificate information based upon passed name string.
     * 
     * @param nameString
     *            name string of the certificate's subject.
     */
    private CertificateInformation(String nameString) {
        String[] info = nameString.split(",");
        displayName = extractCommonName(info[1]);
        organizationName = extractOrganizationName(info[2]);
    }


    /**
     * Parses name string extracting certificate information out of it.
     * 
     * @param nameString
     *            name string of the certificate's subject.
     * @return new instance of certificate information based upon passed name string.
     */
    public static CertificateInformation parseNameString(String nameString) {
        return new CertificateInformation(nameString);
    }


    public String getDisplayName() {
        return displayName;
    }


    public String getOrganizationName() {
        return organizationName;
    }


    /**
     * Extracts common name out of the common name part of the name string.
     * 
     * @param commonName
     *            common name part of the name string.
     * @return subject's common name.
     */
    private static String extractCommonName(String commonName) {
        return commonName.substring(3);
    }


    /**
     * Extracts organization's name out of the organization part of the name string.
     * 
     * @param organization
     *            organization part of the name string.
     * @return subject's organization's name.
     */
    private static String extractOrganizationName(String organization) {
        return organization.substring(2);
    }

}
