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
package pl.psnc.synat.wrdz.zmkd.plan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import org.apache.log4j.Logger;

import pl.psnc.synat.wrdz.ru.composition.TransformationType;
import pl.psnc.synat.wrdz.zmkd.entity.plan.ServiceOutcome;
import pl.psnc.synat.wrdz.zmkd.entity.plan.ServiceParameter;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPath;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlan;

/**
 * Backing bean of migration paths view interface.
 */
@ManagedBean
@RequestScoped
public class MigrationPathsBean {

    /**
     * Class logger.
     */
    private static final Logger LOGGER = Logger.getLogger(MigrationPathsBean.class);

    /**
     * Migration plan manager EJB.
     */
    @EJB
    private MigrationPlanManager migrationPlanManager;

    /**
     * Migration plan helper.
     */
    @Inject
    private MigrationPlanHelper migrationPlanHelper;

    /**
     * Request parameter of plan id.
     */
    @ManagedProperty(value = "#{param.planId}")
    private Long migrationPlanId;

    /**
     * Selected migration plan.
     */
    private MigrationPlan migrationPlan;


    /**
     * Basic initialization.
     */
    @PostConstruct
    private void init() {
        refresh();
    }


    /**
     * Load fresh migration plan.
     */
    protected void refresh() {
        try {
            migrationPlan = migrationPlanManager.getMigrationPlanById(migrationPlanId);
        } catch (MigrationPlanNotFoundException ex) {
            LOGGER.warn("Trying to access paths of unexisting migration plan.");
        } catch (Exception ex) {
            LOGGER.error("Unexpected error occured while retrieving migration plan data.", ex);
        }
    }


    /**
     * Return list of migration paths connected to specified migration plan.
     * 
     * @return list of migration paths
     */
    public List<MigrationPath> getMigrationPaths() {

        if (migrationPlan != null) {
            return migrationPlan.getMigrationPaths();
        }
        return new ArrayList<MigrationPath>();

    }


    /**
     * Returns migration plan active path.
     * 
     * @return active path.
     */
    public Long getActivePath() {
        if (migrationPlan != null && migrationPlan.getActivePath() != null) {
            return migrationPlan.getActivePath().getId();
        }
        return null;
    }


    /**
     * Mark specified path as active path of migration plan.
     * 
     * @param pathId
     *            which path to activate.
     * @return outcome.
     */
    public String pathSelectAction(Long pathId) {

        try {
            migrationPlanManager.setActivePath(migrationPlanId, pathId);
        } catch (MigrationPlanNotFoundException e) {
            LOGGER.warn("Trying to activate path in unexisting migration plan.", e);
        } catch (MigrationPlanStateException e) {
            LOGGER.warn("Trying to activate path in migration plan with wrong state.", e);
        } catch (MigrationPathNotFoundException e) {
            LOGGER.warn("Trying to activate unexisting migration path.", e);
        }
        refresh();
        return "paths";
    }


    public void setMigrationPlanId(Long id) {
        this.migrationPlanId = id;
    }


    public Long getMigrationPlanId() {
        return this.migrationPlanId;
    }


    /**
     * Returns possible types of transformation for its parameters and outcomes.
     * 
     * @param parameters
     *            list of types of parameters of the transformation
     * @param outcomes
     *            list of types of outcomes of the transformation
     * @return possible type of transformation
     */
    public List<TransformationType> getPossibleTransformationTypes(List<ServiceParameter> parameters,
            List<ServiceOutcome> outcomes) {
        TransformationType originType = migrationPlanHelper.getOriginTransformationType(parameters, outcomes);
        if (TransformationType.ONE_TO_ONE.equals(originType)) {
            return Arrays.asList(new TransformationType[] { TransformationType.ONE_TO_ONE });
        }
        if (TransformationType.ONE_TO_MANY.equals(originType)) {
            return Arrays.asList(new TransformationType[] { TransformationType.ONE_TO_ONE,
                    TransformationType.ONE_TO_MANY });
        }
        if (TransformationType.MANY_TO_ONE.equals(originType)) {
            return Arrays.asList(new TransformationType[] { TransformationType.ONE_TO_ONE,
                    TransformationType.MANY_TO_ONE });
        }
        return Collections.emptyList();
    }

}
