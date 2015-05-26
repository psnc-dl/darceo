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
package pl.psnc.synat.wrdz.ms;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.richfaces.event.ItemChangeEvent;

import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.ms.config.MdzConfigurationBean;
import pl.psnc.synat.wrdz.ms.services.DataManipulationServiceBean;
import pl.psnc.synat.wrdz.ms.stats.GeneralBasicStatisticsBean;
import pl.psnc.synat.wrdz.ms.stats.GeneralDataFileFormatBean;
import pl.psnc.synat.wrdz.ms.stats.GeneralMetadataFormatBean;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Bean handling the general WRDZ information.
 */
@ManagedBean
@ViewScoped
public class GeneralBean implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 915801867030782474L;

    /** User browser. */
    @EJB(name = "UserBrowser")
    private UserBrowser userBrowser;

    /** User context. */
    @EJB
    private UserContext userContext;

    /** Currently chosen menu item. */
    private Item current = Item.BASIC;

    /** Available menu items. */
    private final List<Item> items = Collections.unmodifiableList(Arrays.asList(Item.BASIC, Item.METADATA,
        Item.DATAFILE, Item.SERVICES, Item.MDZ_CONFIG));

    /** Basic statistics bean. */
    @ManagedProperty(value = "#{generalBasicStatisticsBean}")
    private GeneralBasicStatisticsBean statisticsBean;

    /** Metadata format bean. */
    @ManagedProperty(value = "#{generalMetadataFormatBean}")
    private GeneralMetadataFormatBean metadataBean;

    /** Data file format bean. */
    @ManagedProperty(value = "#{generalDataFileFormatBean}")
    private GeneralDataFileFormatBean dataFileBean;

    /** Data manipulation service bean. */
    @ManagedProperty(value = "#{dataManipulationServiceBean}")
    private DataManipulationServiceBean serviceBean;

    /** MDZ configuration bean. */
    @ManagedProperty(value = "#{mdzConfigurationBean}")
    private MdzConfigurationBean mdzConfigBean;


    /**
     * Checks if the current user has admin access rights.
     * 
     * @throws IOException
     *             if redirect to the error page fails
     */
    public void checkRights()
            throws IOException {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (!userBrowser.isAdmin(userContext.getCallerPrincipalName())) {
                FacesContext ctx = FacesContext.getCurrentInstance();
                ctx.getApplication().getNavigationHandler().handleNavigation(ctx, null, "forbidden.xhtml");
            }
        }
    }


    public Item getCurrent() {
        return current;
    }


    public List<Item> getItems() {
        return items;
    }


    public GeneralBasicStatisticsBean getStatisticsBean() {
        return statisticsBean;
    }


    public void setStatisticsBean(GeneralBasicStatisticsBean statisticsBean) {
        this.statisticsBean = statisticsBean;
    }


    public GeneralMetadataFormatBean getMetadataBean() {
        return metadataBean;
    }


    public void setMetadataBean(GeneralMetadataFormatBean metadataBean) {
        this.metadataBean = metadataBean;
    }


    public GeneralDataFileFormatBean getDataFileBean() {
        return dataFileBean;
    }


    public void setDataFileBean(GeneralDataFileFormatBean dataFileBean) {
        this.dataFileBean = dataFileBean;
    }


    public DataManipulationServiceBean getServiceBean() {
        return serviceBean;
    }


    public void setServiceBean(DataManipulationServiceBean serviceBean) {
        this.serviceBean = serviceBean;
    }


    public MdzConfigurationBean getMdzConfigBean() {
        return mdzConfigBean;
    }


    public void setMdzConfigBean(MdzConfigurationBean mdzConfigBean) {
        this.mdzConfigBean = mdzConfigBean;
    }


    /**
     * Switches the currently displayed view.
     * 
     * @param event
     *            panelMenu change event
     */
    public void switchView(ItemChangeEvent event) {
        current = Item.valueOf(event.getNewItemName());

        switch (current) {
            case BASIC:
                statisticsBean.refresh();
                break;
            case METADATA:
                metadataBean.refresh();
                break;
            case DATAFILE:
                dataFileBean.refresh();
                break;
            case SERVICES:
                serviceBean.refresh();
                break;
            case MDZ_CONFIG:
                mdzConfigBean.refresh();
                break;
            default:
                // ignore
        }
    }


    /**
     * Menu items.
     */
    public enum Item {

        /** Basic statistics. */
        BASIC,

        /** Metadata formats. */
        METADATA,

        /** Data file formats. */
        DATAFILE,

        /** Data manipulation services. */
        SERVICES,

        /** MDZ configuration. */
        MDZ_CONFIG;
    }
}
