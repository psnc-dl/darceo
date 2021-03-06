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
package pl.psnc.synat.wrdz.common.metadata;

import java.io.Serializable;

/**
 * Interface for all metadata in a standard schema.
 */
public abstract class Metadata implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 565784332410096570L;

    /**
     * Metadata as XML.
     */
    protected final String xml;


    /**
     * Constructs metadata object.
     * 
     * @param xml
     *            xml
     */
    public Metadata(String xml) {
        this.xml = xml;
    }


    /**
     * Returns metadata as XML.
     * 
     * @return metadata as XML
     */
    public String getXml() {
        return xml;
    }


    /**
     * Returns a type of metadata.
     * 
     * @return type of metadata
     */
    public abstract MetadataType getType();

}
