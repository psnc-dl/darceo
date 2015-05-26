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
package pl.psnc.synat.wrdz.zmd.entity;

import gov.loc.mets.AmdSecType;
import gov.loc.mets.MdSecType;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "metadata-list", namespace = "http://scape-project.eu/model")
/**
 * For internal use. This is needed for mapping in between scape model and mets model
 * should probably done using an XmlAdapter, but time is of the essence atm...
 * @author fasseg
 *
 */
public class MetadataSectionsCollection {

    @XmlElement(name = "amdSecType", namespace = "http://www.loc.gov/METS/")
    private final List<AmdSecType> amdSections;

    @XmlElement(name = "mdSecType", namespace = "http://www.loc.gov/METS/")
    private final List<MdSecType> mdSections;


    public MetadataSectionsCollection() {
        super();
        this.amdSections = null;
        this.mdSections = null;
    }


    public MetadataSectionsCollection(List<AmdSecType> amdSections, List<MdSecType> mdSections) {
        super();
        this.amdSections = amdSections;
        this.mdSections = mdSections;
    }


    public List<AmdSecType> getAmdSections() {
        return amdSections;
    }


    public List<MdSecType> getMdSections() {
        return mdSections;
    }
}
