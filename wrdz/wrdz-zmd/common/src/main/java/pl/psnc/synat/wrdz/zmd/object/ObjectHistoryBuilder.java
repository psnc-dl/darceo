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
package pl.psnc.synat.wrdz.zmd.object;

import java.util.ArrayList;
import java.util.List;

import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;

/**
 * Builds object's history.
 */
public class ObjectHistoryBuilder {

    /**
     * Ordered list of entities representing content versions of the object.
     */
    private final List<ContentVersion> versions;


    /**
     * Creates new instance of the builder.
     * 
     * @param versions
     *            ordered list of entities representing content versions of the object.
     */
    public ObjectHistoryBuilder(List<ContentVersion> versions) {
        this.versions = new ArrayList<ContentVersion>();
        this.versions.addAll(versions);
    }


    /**
     * Builds the information about the object versions history.
     * 
     * @return information about the object versions history.
     */
    public ObjectHistory buildHistory() {
        if (versions.size() > 0) {
            ObjectHistory history = new ObjectHistory();
            ContentVersions contentVersions = new ContentVersions();
            contentVersions.getVersion().addAll(extractVersionInformation());
            history.setVersions(contentVersions);
            return history;
        } else {
            return null;
        }
    }


    /**
     * Extracts version information from content version entity.
     * 
     * @return extracted information from object's version entity.
     */
    private List<pl.psnc.synat.wrdz.zmd.object.ContentVersion> extractVersionInformation() {
        List<pl.psnc.synat.wrdz.zmd.object.ContentVersion> results = new ArrayList<pl.psnc.synat.wrdz.zmd.object.ContentVersion>();
        for (ContentVersion version : versions) {
            if (version != null) {
                pl.psnc.synat.wrdz.zmd.object.ContentVersion ver = new pl.psnc.synat.wrdz.zmd.object.ContentVersion();
                ver.setCreatedOn(version.getCreatedOn());
                ver.setNumber(version.getVersion());
                results.add(ver);
            }
        }
        return results;
    }
}
