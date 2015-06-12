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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.richfaces.event.ItemChangeEvent;

import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.ms.stats.UserBasicStatisticsBean;
import pl.psnc.synat.wrdz.ms.stats.UserDataFileFormatBean;
import pl.psnc.synat.wrdz.zu.dto.user.UserDto;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Bean handling the user-specific information.
 */
@ManagedBean
@ViewScoped
public class UserBean implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -5159500838263315552L;

    /** Currently chosen menu item. */
    private Item currentItem = Item.BASIC;

    /** Available menu items. */
    private final List<Item> items = Collections.unmodifiableList(Arrays.asList(Item.BASIC, Item.DATAFILE));

    /** Currently chosen user. */
    private String currentUser;

    /** Available users. */
    private List<String> users;

    /** User browser. */
    @EJB(name = "UserBrowser")
    private UserBrowser userBrowser;

    /** User context. */
    @EJB
    private UserContext userContext;

    /** Basic statistics bean. */
    @ManagedProperty(value = "#{userBasicStatisticsBean}")
    private UserBasicStatisticsBean statisticsBean;

    /** Data file format bean. */
    @ManagedProperty(value = "#{userDataFileFormatBean}")
    private UserDataFileFormatBean dataFileBean;


    /**
     * Post-construct initializer. Fetches users.
     */
    @PostConstruct
    protected void init() {
        List<String> options = new ArrayList<String>();
        if (userBrowser.isAdmin(userContext.getCallerPrincipalName())) {
            for (UserDto user : userBrowser.getUsers()) {
                options.add(user.getUsername());
                if (currentUser == null) {
                    currentUser = user.getUsername();
                }
            }
        } else {
            currentUser = userContext.getCallerPrincipalName();
            options.add(currentUser);
        }
        this.users = Collections.unmodifiableList(options);
        refreshView();
    }


    public Item getCurrentItem() {
        return currentItem;
    }


    public List<Item> getItems() {
        return items;
    }


    public String getCurrentUser() {
        return currentUser;
    }


    /**
     * Sets the currently chosen user and refreshes the view.
     * 
     * @param currentUser
     *            currently chosen user
     */
    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
        refreshView();
    }


    public List<String> getUsers() {
        return users;
    }


    public UserBasicStatisticsBean getStatisticsBean() {
        return statisticsBean;
    }


    public void setStatisticsBean(UserBasicStatisticsBean statisticsBean) {
        this.statisticsBean = statisticsBean;
    }


    public UserDataFileFormatBean getDataFileBean() {
        return dataFileBean;
    }


    public void setDataFileBean(UserDataFileFormatBean dataFileBean) {
        this.dataFileBean = dataFileBean;
    }


    /**
     * Switches the currently displayed view.
     * 
     * @param event
     *            panelMenu change event
     */
    public void switchView(ItemChangeEvent event) {
        currentItem = Item.valueOf(event.getNewItemName());
        refreshView();
    }


    /**
     * Refreshes the currently displayed view.
     */
    private void refreshView() {
        switch (currentItem) {
            case BASIC:
                statisticsBean.setUsername(currentUser);
                statisticsBean.refresh();
                break;
            case DATAFILE:
                dataFileBean.setUsername(currentUser);
                dataFileBean.refresh();
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

        /** Data file formats. */
        DATAFILE;
    }
}
