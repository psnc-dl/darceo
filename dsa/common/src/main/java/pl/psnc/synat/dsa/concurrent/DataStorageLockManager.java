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
package pl.psnc.synat.dsa.concurrent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.DataStorageClient;
import pl.psnc.synat.dsa.util.FilenameUtils;

/**
 * Manages locks on data storage's resources.
 * 
 */
public final class DataStorageLockManager {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DataStorageLockManager.class);

    /**
     * The only one instance of data storage lock manager.
     */
    private static DataStorageLockManager instance;

    /**
     * List of locks hold by transactions.
     */
    private List<DataStorageNodeLock> nodeLocks;

    /**
     * Lock to list of node locks.
     */
    private Lock lock;

    /**
     * Condition which threads are waiting for.
     */
    private Condition freeNodes;


    /**
     * Creates data storage lock manager.
     */
    private DataStorageLockManager() {
        nodeLocks = new LinkedList<DataStorageNodeLock>();
        lock = new ReentrantLock();
        freeNodes = lock.newCondition();
    }


    /**
     * Retrieves the only one instance of data storage lock manager.
     * 
     * @return instance of data storage resource manager
     */
    public static DataStorageLockManager getInstance() {
        if (instance == null) {
            instance = new DataStorageLockManager();
        }
        return instance;
    }


    /**
     * Lock the resource to read.
     * 
     * @param path
     *            path of the resource
     */
    public void lockToRead(String path) {
        lock.lock();
        try {
            Thread me = Thread.currentThread();
            DataStorageNodeLock nodeLock = getExistingNodeLock(me, path);
            if (nodeLock == null) {
                nodeLock = new DataStorageNodeLock(me, path, LockMode.TO_READ);
                nodeLocks.add(nodeLock);
            }
            while (!isFreeToRead(nodeLock)) {
                try {
                    freeNodes.await();
                } catch (Exception e) {
                    logger.info("Exception occurred while waiting for nodes.", e);
                }
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * Lock the resource to delete a path (file or directory).
     * 
     * @param path
     *            path of the resource
     */
    public void lockToDelete(String path) {
        lock.lock();
        try {
            Thread me = Thread.currentThread();
            DataStorageNodeLock nodeLock = getExistingNodeLock(me, path);
            if (nodeLock == null) {
                nodeLock = new DataStorageNodeLock(me, path, LockMode.TO_DELETE);
                nodeLocks.add(nodeLock);
            } else {
                if (nodeLock.getMode() == LockMode.TO_READ) {
                    logger.error("Attempt to lock to delete the " + path + " when there is the read lock on "
                            + nodeLock.getPath());
                    throw new IllegalArgumentException("Attempt to lock to delete when there is the read lock");
                }
            }
            while (!isFreeToWrite(nodeLock)) {
                try {
                    freeNodes.await();
                } catch (Exception e) {
                    logger.info("Exception occurred while waiting for nodes.", e);
                }
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * Lock the resource to create a path (file or directory). The method locks the path if at least one of the clients
     * has to create it.
     * 
     * @param path
     *            path of the resource
     * @param clients
     *            clients to data storages.
     */
    public void lockToCreate(String path, List<DataStorageClient> clients) {
        lock.lock();
        try {
            List<String> parts = FilenameUtils.splitPath(path);
            StringBuilder parentPath = new StringBuilder();
            search_first_non_existing_path: for (String part : parts) {
                parentPath.append(part);
                for (DataStorageClient client : clients) {
                    if (!client.exists(parentPath.toString())) {
                        break search_first_non_existing_path;
                    }
                }
                parentPath.append("/");
            }
            String pathToLock = parentPath.toString();
            for (DataStorageNodeLock nodeLock : nodeLocks) {
                if (nodeLock.getMode() == LockMode.TO_DELETE && nodeLock.contains(pathToLock)) {
                    pathToLock = nodeLock.getPath();
                }
            }
            Thread me = Thread.currentThread();
            DataStorageNodeLock nodeLock = getExistingNodeLock(me, pathToLock);
            if (nodeLock == null) {
                nodeLock = new DataStorageNodeLock(me, pathToLock, LockMode.TO_CREATE);
                nodeLocks.add(nodeLock);
            } else {
                if (nodeLock.getMode() == LockMode.TO_READ) {
                    logger.error("Attempt to lock to create " + pathToLock + " when there is the read lock on "
                            + nodeLock.getPath());
                    throw new IllegalArgumentException("Attempt to lock to create when there is the read lock");
                }
            }
            while (!isFreeToWrite(nodeLock)) {
                try {
                    freeNodes.await();
                } catch (Exception e) {
                    logger.info("Exception occurred while waiting for nodes.", e);
                }
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * Unlock all resources held by current thread.
     */
    public void unlockAll() {
        lock.lock();
        try {
            Thread me = Thread.currentThread();
            Iterator<DataStorageNodeLock> it = nodeLocks.iterator();
            while (it.hasNext()) {
                DataStorageNodeLock nodeLock = it.next();
                if (nodeLock.getThread() == me) {
                    it.remove();
                }
            }
            freeNodes.signalAll();
        } finally {
            lock.unlock();
        }
    }


    /**
     * Gets existing node lock for thread and path. If there is a node lock above the path, then this node lock covers
     * this path - it is returned.
     * 
     * @param thread
     *            thread
     * @param path
     *            path
     * @return node lock or null if it does not exist
     */
    private DataStorageNodeLock getExistingNodeLock(Thread thread, String path) {
        for (DataStorageNodeLock nodeLock : nodeLocks) {
            if (nodeLock.getThread() == thread && nodeLock.contains(path)) {
                return nodeLock;
            }
        }
        return null;
    }


    /**
     * Checks if node is free to read. There are no writers above the node.
     * 
     * @param node
     *            node
     * @return whether is it free to read
     */
    private boolean isFreeToRead(DataStorageNodeLock node) {
        for (DataStorageNodeLock nodeLock : nodeLocks) {
            if (nodeLock == node) {
                // there was no writer before - node is free to read
                return true;
            } else {
                if (nodeLock.getThread() != node.getThread() && nodeLock.getMode() != LockMode.TO_READ) {
                    if (node.contains(nodeLock.getPath())) {
                        return false;
                    }
                    if (nodeLock.contains(node.getPath())) {
                        return false;
                    }
                }
            }
        }
        logger.error("Node " + node + " is not on the list of nodes");
        throw new IllegalArgumentException("Node is not on the list of nodes");
    }


    /**
     * Checks if node is free to write. There is no readers and writes above and under the node.
     * 
     * @param node
     *            node
     * @return whether is it free to write
     */
    private boolean isFreeToWrite(DataStorageNodeLock node) {
        for (DataStorageNodeLock nodeLock : nodeLocks) {
            if (nodeLock == node) {
                // there was no other locks before - node is free to write
                return true;
            } else {
                if (nodeLock.getThread() != node.getThread()) {
                    if (node.contains(nodeLock.getPath())) {
                        return false;
                    }
                    if (nodeLock.contains(node.getPath())) {
                        return false;
                    }
                }
            }
        }
        logger.error("Node " + node + " is not on the list of nodes");
        throw new IllegalArgumentException("Node is not on the list of nodes");
    }

}
