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
package pl.psnc.synat.wrdz.mdz.entity.plugin;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Represents a single plugin iteration.
 */
@Entity
@Table(name = "MDZ_PLUGIN_ITERATIONS", schema = "darceo")
public class PluginIteration implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 830098720563022025L;

    /** Identifier. */
    @Id
    @SequenceGenerator(name = "pluginIterationSequenceGenerator", sequenceName = "darceo.MDZ_PI_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pluginIterationSequenceGenerator")
    @Column(name = "PI_ID", unique = true, nullable = false)
    private long id;

    /** Plugin name. */
    @Column(name = "PI_PLUGIN_NAME", length = 255, nullable = false)
    private String pluginName;

    /** Object identifier. */
    @Column(name = "PI_OBJECT_IDENTIFIER", length = 255, nullable = false)
    private String objectIdentifier;

    /** Date when this iteration started. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PI_STARTED_ON", nullable = false)
    private Date startedOn;

    /** Date when this iteration finished. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "PI_FINISHED_ON", nullable = false)
    private Date finishedOn;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getPluginName() {
        return pluginName;
    }


    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }


    public String getObjectIdentifier() {
        return objectIdentifier;
    }


    public void setObjectIdentifier(String objectIdentifier) {
        this.objectIdentifier = objectIdentifier;
    }


    public Date getStartedOn() {
        return startedOn;
    }


    public void setStartedOn(Date startedOn) {
        this.startedOn = startedOn;
    }


    public Date getFinishedOn() {
        return finishedOn;
    }


    public void setFinishedOn(Date finishedOn) {
        this.finishedOn = finishedOn;
    }
}
