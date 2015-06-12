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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;
import pl.psnc.synat.wrdz.zmd.entity.types.OperationType;

@XmlRootElement(name = "lifecyclestates-list", namespace = "http://scape-project.eu/model")
/**
 * Class for storing information about states of entity.
 * @author tomhof
 *
 */
public class LifeCycleStatesCollection {

    @XmlElement(name = "lifecyclestate", namespace = "http://scape-project.eu/model")
    private List<LifeCycleState> states;


    public LifeCycleStatesCollection() {
        super();
        this.states = new LinkedList<LifeCycleState>();
    }


    public LifeCycleStatesCollection(List<LifeCycleState> s) {
        super();
        this.states = s;
    }


    public List<LifeCycleState> getLifeCycleStates() {
        return states;
    }


    public void parseOperationsToLifeCycleStates(List<Operation> operations, String eid) {
        if (states == null) {
            states = new LinkedList<LifeCycleState>();
        }
        if (states.size() > 0) {
            states.clear();
        }

        if ((operations == null) || (operations.size() == 0)) {
            LifeCycleState lcs = new LifeCycleState();
            String stateInfo = "[NOT FOUND]";
            String details = "Operation " + stateInfo + ".";
            String state = "OTHER";
            lcs.setEntityDetails(details);
            lcs.setEntityId(eid);
            lcs.setEntityState(state);
            states.add(lcs);
            return;
        }
        Iterator<Operation> it = operations.iterator();
        while (it.hasNext()) {
            Operation o = (Operation) it.next();
            LifeCycleState lcs = new LifeCycleState();
            String stateInfo = o.getOperation().name();
            String details = "Operation " + stateInfo + " finished at " + o.getDate().toString();
            String state = o.getOperation().equals(OperationType.DELETION) ? "OTHER" : "INGESTED";
            lcs.setEntityDetails(details);
            lcs.setEntityId(eid);
            lcs.setEntityState(state);
            states.add(lcs);
        }
    }
}

/**
 * Life-cycle state description.
 * 
 * @author tomhof
 * 
 */
class LifeCycleState {

    @XmlAttribute(name = "id")
    private String eId;

    @XmlAttribute(name = "state")
    private String s;

    @XmlElement(name = "details")
    private String det;


    public String getEntityId() {
        return eId;
    }


    public void setEntityId(String id) {
        this.eId = id;
    }


    public String getEntityState() {
        return s;
    }


    public void setEntityState(String s) {
        this.s = s;
    }


    public String getEntityDetails() {
        return det;
    }


    public void setEntityDetails(String d) {
        this.det = d;
    }
}
