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
package pl.psnc.synat.wrdz.zmkd.entity.plan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Migration plan.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TP_TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("TRANSFORMATION")
@Table(name = "ZMKD_TRANSFORMATION_PATHS", schema = "darceo")
public class TransformationPath implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -2362243316314555414L;

    /** Identifier (primary key). */
    @Id
    @SequenceGenerator(name = "transformationPathSequenceGenerator", sequenceName = "darceo.ZMKD_TP_ID_SEQ",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transformationPathSequenceGenerator")
    @Column(name = "TP_ID", unique = true, nullable = false)
    private long id;

    /** Execution cost. */
    @Column(name = "TP_EXECUTION_COST")
    private Integer executionCost;

    /** Transformations. */
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "T_TRANSFORMATION_PATH_ID", nullable = false)
    @OrderColumn(name = "T_ORDER_NO", nullable = false)
    @PrivateOwned
    private List<Transformation> transformations = new ArrayList<Transformation>();


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public Integer getExecutionCost() {
        return executionCost;
    }


    public void setExecutionCost(Integer executionCost) {
        this.executionCost = executionCost;
    }


    public List<Transformation> getTransformations() {
        return transformations;
    }


    public void setTransformations(List<Transformation> transformations) {
        this.transformations = transformations;
    }
}
