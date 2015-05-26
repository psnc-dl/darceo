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

import java.io.InputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;
import org.xml.sax.SAXException;

import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlan;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlanStatus;
import pl.psnc.synat.wrdz.zmkd.i18n.ZmkdMessageUtils;
import pl.psnc.synat.wrdz.zmkd.plan.validation.InvalidFormatException;
import pl.psnc.synat.wrdz.zmkd.plan.validation.InvalidObjectException;
import pl.psnc.synat.wrdz.zmkd.plan.validation.InvalidPathException;
import pl.psnc.synat.wrdz.zmkd.plan.validation.NoObjectsException;
import pl.psnc.synat.wrdz.zmkd.plan.validation.NoPathException;
import pl.psnc.synat.wrdz.zmkd.session.SessionBean;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Bean managing operations on migrations plan.
 */
@ManagedBean
@ViewScoped
public class MigrationPlansBean {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(MigrationPlansBean.class);

    /** Path to the MigrationPlan XML schema. */
    private static final String XML_SCHEMA_FILE = "/schemas/migration-plan.xsd";

    /**
     * Migration plan manager providing support for operations on migration plans.
     */
    @EJB
    private MigrationPlanManager migrationPlanManager;

    /**
     * Migration plan processor manager provides support for controlling state of migration plan.
     */
    @EJB
    private MigrationPlanProcessorsManager migrationPlanProcessorManager;

    /** User browser. */
    @EJB(name = "UserBrowser")
    private UserBrowser userBrowser;

    /** User context. */
    @EJB
    private UserContext userContext;

    /**
     * Session bean.
     */
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    /**
     * In case when any of the migration plans is in NEW state, refresh asynchronously list.
     */
    private boolean processingPlans = false;

    /**
     * Delete error message filled in case of exception in delete action.
     */
    private String deleteErrorMessage = "notSet";

    /** JAXB context. */
    private JAXBContext jaxbContext;

    /** MigrationPlan schema. */
    private Schema schema;


    /**
     * 
     * Bean initialization instructions.
     * 
     * @throws JAXBException
     *             if creating an instance of JAXBContext fails
     * @throws SAXException
     *             if creating a schema object fails
     */
    @PostConstruct
    protected void init()
            throws JAXBException, SAXException {

        deleteErrorMessage = "notSet";

        jaxbContext = JAXBContext.newInstance(pl.psnc.darceo.migration.MigrationPlan.class);

        InputStream schemaInput = null;
        try {
            schemaInput = getClass().getResourceAsStream(XML_SCHEMA_FILE);
            schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
                new StreamSource(schemaInput));
        } finally {
            IOUtils.closeQuietly(schemaInput);
        }
    }


    /**
     * Invoke when a new migration plan is uploaded by a user.
     * 
     * @param event
     *            file upload event
     * @throws Exception
     *             when something went wrong
     */
    public void uploadPlanListener(FileUploadEvent event)
            throws Exception {
        UploadedFile file = event.getUploadedFile();
        InputStream is = file.getInputStream();

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(schema);

        pl.psnc.darceo.migration.MigrationPlan plan;
        try {
            plan = (pl.psnc.darceo.migration.MigrationPlan) unmarshaller.unmarshal(is);
        } catch (UnmarshalException e) {
            reportUploadError(ZmkdMessageUtils.getMessage("plans.error.invalid_xml"));
            return;
        } finally {
            IOUtils.closeQuietly(is);
        }

        try {
            migrationPlanManager.createMigrationPlan(plan);
        } catch (InvalidFormatException e) {
            reportUploadError(ZmkdMessageUtils.getMessage("plans.error.invalid_format", e.getPuid()));
        } catch (InvalidObjectException e) {
            reportUploadError(ZmkdMessageUtils.getMessage("plans.error.invalid_object", e.getObjectIdentifier()));
        } catch (InvalidPathException e) {
            reportUploadError(ZmkdMessageUtils.getMessage("plans.error.invalid_path"));
        } catch (NoObjectsException e) {
            reportUploadError(ZmkdMessageUtils.getMessage("plans.error.no_objects"));
        } catch (NoPathException e) {
            reportUploadError(ZmkdMessageUtils.getMessage("plans.error.no_path"));
        }
    }


    /**
     * Informs the user that the migration plan upload failed.
     * 
     * @param message
     *            the error message detailing the problem
     */
    protected void reportUploadError(String message) {
        FacesContext.getCurrentInstance().addMessage("plans-form:add-plans",
            new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }


    /**
     * Method returns filtered list of migration plans.
     * 
     * @return list of migration plans.
     */
    public List<MigrationPlan> getMigrationPlans() {

        List<MigrationPlan> migrationPlans;
        if (userBrowser.isAdmin(userContext.getCallerPrincipalName())) {
            migrationPlans = migrationPlanManager.getMigrationPlans();
        } else {
            migrationPlans = migrationPlanManager.getMigrationPlans(userContext.getCallerPrincipalName());
        }

        processingPlans = false;
        for (MigrationPlan plan : migrationPlans) {
            if (plan.getStatus() == MigrationPlanStatus.NEW) {
                processingPlans = true;
                break;
            }
        }

        return migrationPlans;
    }


    /**
     * Checks whether migration plan can be paused and returns 'true' if thats possible.
     * 
     * @param plan
     *            migration plan to check
     * @return boolean value
     */
    public boolean isPausable(MigrationPlan plan) {
        return plan.getStatus() == MigrationPlanStatus.RUNNING;
    }


    /**
     * Checks whether migration plan can be finished and returns 'true' if thats possible.
     * 
     * @param plan
     *            migration plan to check
     * @return boolean value
     */
    public boolean isFinishable(MigrationPlan plan) {
        return plan.getStatus() == MigrationPlanStatus.RUNNING || plan.getStatus() == MigrationPlanStatus.PAUSED;
    }


    /**
     * Checks whether migration plan can be started and returns 'true' if thats possible.
     * 
     * @param plan
     *            migration plan to check
     * @return boolean value
     */
    public boolean isStartable(MigrationPlan plan) {
        return plan.getStatus() == MigrationPlanStatus.READY || plan.getStatus() == MigrationPlanStatus.PAUSED;
    }


    /**
     * Action of pausing specified migration plan.
     * 
     * @param plan
     *            specified migration plan
     */
    public void pauseAction(MigrationPlan plan) {
        migrationPlanProcessorManager.pause(plan.getId());
    }


    /**
     * Action of starting specified migration plan.
     * 
     * @param plan
     *            specified migration plan
     */
    public void startAction(MigrationPlan plan) {
        migrationPlanProcessorManager.start(plan.getId(), false);
    }


    /**
     * Action of finishing specified migration plan.
     * 
     * @param plan
     *            specified migration plan
     */
    public void finishAction(MigrationPlan plan) {
        migrationPlanProcessorManager.finish(plan.getId());
    }


    /**
     * Action of deleting specified migration plan.
     * 
     * @param plan
     *            migration plan to delete
     */
    public void deleteAction(MigrationPlan plan) {
        try {
            migrationPlanManager.deletePlan(plan.getId());
        } catch (MigrationPlanDeletionException ex) {
            deleteErrorMessage = ex.getMessageId().toString();
        }
    }


    /**
     * XML migration plan file download action. Sets request response as XML stream.
     * 
     * @param plan
     *            plan from which XML file is taken
     */
    public void downloadXML(MigrationPlan plan) {
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
                .getResponse();
        response.setContentType("text/xml");
        response.setHeader("Content-Disposition", "attachment; filename=migration_plan_" + plan.getId() + ".xml");
        response.setContentLength(plan.getXmlFile().length());
        try {
            ServletOutputStream ouputStream = response.getOutputStream();
            ouputStream.write(plan.getXmlFile().getBytes(), 0, plan.getXmlFile().getBytes().length);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }


    /**
     * Action of redirecting to migration items view.
     * 
     * @param plan
     *            items of which plan
     * @return redirect string
     */
    public String viewMigrationItems(MigrationPlan plan) {
        sessionBean.setCurrentMigrationPlanId(plan.getId());
        return "items";
    }


    /**
     * Returns error message identifier in case of error occurrence in delete action.
     * 
     * @return error message id
     */
    public String getDeleteErrorMessage() {
        return deleteErrorMessage;
    }


    /**
     * Returns 'true' value if there`s at least one plan in NEW state.
     * 
     * @return boolean value
     * 
     */
    public boolean isProcessingPlans() {
        return processingPlans;
    }


    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }


    public SessionBean getSessionBean() {
        return sessionBean;
    }
}
