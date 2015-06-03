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
package pl.psnc.synat.dsa;

import java.util.ArrayList;
import java.util.List;

import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sender of events associated with a managed connection.
 * 
 */
public class DataStorageConnectionEventSender {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DataStorageConnectionEventSender.class);

    /**
     * Listeners.
     */
    private List<ConnectionEventListener> listeners;

    /**
     * Managed connection.
     */
    private DataStorageManagedConnection managedConnection;


    /**
     * Creates connection event sender for managed connection.
     * 
     * @param managedConnection
     *            managed connection
     */
    public DataStorageConnectionEventSender(DataStorageManagedConnection managedConnection) {
        listeners = new ArrayList<ConnectionEventListener>();
        this.managedConnection = managedConnection;
    }


    /**
     * Add the listener.
     * 
     * @param listener
     *            listener
     */
    public void addConnectorListener(ConnectionEventListener listener) {
        listeners.add(listener);
    }


    /**
     * Remove the listener.
     * 
     * @param listener
     *            listener
     */
    public void removeConnectorListener(ConnectionEventListener listener) {
        listeners.remove(listener);
    }


    /**
     * Sends a event to application server by its listeners.
     * 
     * @param eventType
     *            event type
     * @param ex
     *            exception associated with the event
     * @param connection
     *            connection associated with the event
     */
    public void sendEvent(int eventType, Exception ex, AbstractDataStorageConnection connection) {
        ConnectionEvent event = null;
        if (ex != null) {
            event = new ConnectionEvent(managedConnection, eventType, ex);
        } else {
            event = new ConnectionEvent(managedConnection, eventType);
        }
        if (connection != null) {
            event.setConnectionHandle(connection);
        }
        for (ConnectionEventListener listener : listeners) {
            switch (event.getId()) {
                case ConnectionEvent.CONNECTION_CLOSED:
                    logger.debug("connection closed");
                    logger.debug("connection: " + connection);
                    listener.connectionClosed(event);
                    break;
                case ConnectionEvent.CONNECTION_ERROR_OCCURRED:
                    logger.debug("connection error occurred");
                    logger.debug("connection: " + connection);
                    listener.connectionErrorOccurred(event);
                    break;
                case ConnectionEvent.LOCAL_TRANSACTION_STARTED:
                case ConnectionEvent.LOCAL_TRANSACTION_COMMITTED:
                case ConnectionEvent.LOCAL_TRANSACTION_ROLLEDBACK:
                    logger.error("local transactions are not supported - event type: " + eventType);
                    break;
                default:
                    logger.error("event " + event + " is not recognized");
                    break;
            }
        }
    }

}
