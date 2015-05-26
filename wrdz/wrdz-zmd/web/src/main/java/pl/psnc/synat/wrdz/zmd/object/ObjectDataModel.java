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

import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;

import org.ajax4jsf.model.DataVisitor;
import org.ajax4jsf.model.ExtendedDataModel;
import org.ajax4jsf.model.Range;
import org.ajax4jsf.model.SequenceRange;
import org.apache.commons.lang.StringUtils;

import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;

/**
 * Digital object data model. Handles pagination of digital objects.
 */
public class ObjectDataModel extends ExtendedDataModel<DigitalObject> {

    /** Object browser. */
    private ObjectBrowser browser;

    /** Object identifiers. If <code>null</code>, browse all objects. */
    private Collection<Long> identifiers;

    /** Object key (index in the array). */
    private Integer key;

    /** Cached objects page. */
    private List<DigitalObject> objects;

    /** Current offset (in objects). */
    private int offset;

    /** Page size. */
    private int limit;

    /** Current object count. */
    private int count = -1;

    /** Identifier filter. */
    private String identifierFilter;

    /** Name filter. */
    private String nameFilter;


    /**
     * Constructor.
     * 
     * @param browser
     *            object browser
     * @param identifiers
     *            object identifiers (can be <code>null</code>)
     */
    public ObjectDataModel(ObjectBrowser browser, Collection<Long> identifiers) {
        this.browser = browser;
        this.identifiers = identifiers;
    }


    /**
     * Sets the value of the identifier filter.
     * 
     * @param identifierFilter
     *            identifier filter value
     */
    public void setIdentifierFilter(String identifierFilter) {
        identifierFilter = StringUtils.trimToNull(identifierFilter);
        if (!StringUtils.equals(this.identifierFilter, identifierFilter)) {
            this.offset = 0;
            this.limit = 0;
            this.count = -1;
        }
        this.identifierFilter = identifierFilter;
    }


    /**
     * Sets the value of the name filter.
     * 
     * @param nameFilter
     *            name filter value
     */
    public void setNameFilter(String nameFilter) {
        nameFilter = StringUtils.trimToNull(nameFilter);
        if (!StringUtils.equals(this.nameFilter, nameFilter)) {
            this.offset = 0;
            this.limit = 0;
            this.count = -1;
        }
        this.nameFilter = nameFilter;
    }


    @Override
    public Integer getRowKey() {
        return key;
    }


    @Override
    public void setRowKey(Object key) {
        this.key = (Integer) key;
    }


    @Override
    public void walk(FacesContext context, DataVisitor visitor, Range range, Object argument) {
        SequenceRange seqRange = (SequenceRange) range;
        if (offset != seqRange.getFirstRow() || limit != seqRange.getRows()) {
            offset = seqRange.getFirstRow();
            limit = seqRange.getRows();

            if (identifiers != null) {
                objects = browser.getDigitalObjects(identifiers, offset / limit, limit, identifierFilter, nameFilter);
            } else {
                objects = browser.getDigitalObjects(offset / limit, limit, identifierFilter, nameFilter);
            }

        }
        if (objects != null) {
            for (int i = 0; i < objects.size(); i++) {
                visitor.process(context, i, argument);
            }
        }
    }


    @Override
    public int getRowCount() {
        if (count < 0) {
            if (identifiers != null) {
                count = (int) browser.countDigitalObjects(identifiers, identifierFilter, nameFilter);
            } else {
                count = (int) browser.countDigitalObjects(identifierFilter, nameFilter);
            }
        }
        return count;
    }


    @Override
    public DigitalObject getRowData() {
        return objects.get(key);
    }


    @Override
    public int getRowIndex() {
        return -1;
    }


    @Override
    public Object getWrappedData() {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean isRowAvailable() {
        return key != null;
    }


    @Override
    public void setRowIndex(int index) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void setWrappedData(Object data) {
        throw new UnsupportedOperationException();
    }
}
