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
package pl.psnc.synat.sra.concurrent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages locks on semantic repositories connections.
 * 
 */
public final class SemanticRepositoryLockManager {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SemanticRepositoryLockManager.class);

    /**
     * The only one instance of semantic repository connections lock manager.
     */
    private static SemanticRepositoryLockManager instance;

    /**
     * List of locks hold by transactions.
     */
    private List<SemanticRepositoryLock> repoLocks;

    /**
     * Lock to list of node locks.
     */
    private Lock lock;

    /**
     * Condition which threads are waiting for.
     */
    private Condition freeRepos;


    /**
     * Creates semantic repository lock manager.
     */
    private SemanticRepositoryLockManager() {
        repoLocks = new LinkedList<SemanticRepositoryLock>();
        lock = new ReentrantLock();
        freeRepos = lock.newCondition();
    }


    /**
     * Retrieves the only one instance of semantic repository connections lock manager.
     * 
     * @return instance of semantic repository connections resource manager
     */
    public static SemanticRepositoryLockManager getInstance() {
        if (instance == null) {
            instance = new SemanticRepositoryLockManager();
        }
        return instance;
    }


    /**
     * Lock the semantic repository to read.
     * 
     */
    public void lockToRead() {
        lock.lock();
        try {
            Thread me = Thread.currentThread();
            SemanticRepositoryLock repoLock = getExistingRepoLock(me);
            if (repoLock == null) {
                repoLock = new SemanticRepositoryLock(me, LockMode.TO_READ);
                repoLocks.add(repoLock);
            }
            while (!isFreeToRead(repoLock)) {
                try {
                    freeRepos.await();
                } catch (Exception e) {
                    logger.info("Exception occurred while waiting for repos.", e);
                }
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * Lock the semantic repository to write.
     * 
     */
    public void lockToWrite() {
        lock.lock();
        try {
            Thread me = Thread.currentThread();
            SemanticRepositoryLock repoLock = getExistingRepoLock(me);
            if (repoLock == null) {
                repoLock = new SemanticRepositoryLock(me, LockMode.TO_WRITE);
                repoLocks.add(repoLock);
            } else {
                if (repoLock.getMode() == LockMode.TO_READ) {
                    logger.error("Attempt to lock to write when there is the read lock");
                    throw new IllegalArgumentException("Attempt to lock to write when there is the read lock");
                }
            }
            while (!isFreeToWrite(repoLock)) {
                try {
                    freeRepos.await();
                } catch (Exception e) {
                    logger.info("Exception occurred while waiting for nodes.", e);
                }
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * Unlock the resources held by current thread to read.
     * 
     */
    public void unlockFromRead() {
        lock.lock();
        try {
            Thread me = Thread.currentThread();
            SemanticRepositoryLock repoLock = getExistingRepoLockToRead(me);
            if (repoLock != null) {
                repoLocks.remove(repoLock);
                freeRepos.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * Unlock all resources held by current thread. It should be one only.
     */
    public void unlockAll() {
        lock.lock();
        try {
            Thread me = Thread.currentThread();
            Iterator<SemanticRepositoryLock> it = repoLocks.iterator();
            while (it.hasNext()) {
                SemanticRepositoryLock repoLock = it.next();
                if (repoLock.getThread() == me) {
                    it.remove();
                }
            }
            freeRepos.signalAll();
        } finally {
            lock.unlock();
        }
    }


    /**
     * Gets existing repository lock for thread.
     * 
     * @param thread
     *            thread
     * @return lock or null if it does not exist
     */
    private SemanticRepositoryLock getExistingRepoLock(Thread thread) {
        for (SemanticRepositoryLock repoLock : repoLocks) {
            if (repoLock.getThread() == thread) {
                return repoLock;
            }
        }
        return null;
    }


    /**
     * Gets existing lock to read for thread.
     * 
     * @param thread
     *            thread
     * @return repo lock or null if it does not exist
     */
    private SemanticRepositoryLock getExistingRepoLockToRead(Thread thread) {
        for (SemanticRepositoryLock repoLock : repoLocks) {
            if (repoLock.getThread() == thread && repoLock.getMode() == LockMode.TO_READ) {
                return repoLock;
            }
        }
        return null;
    }


    /**
     * Checks if repository is free to read. There are no writers before.
     * 
     * @param repo
     *            repo
     * @return whether is it free to read
     */
    private boolean isFreeToRead(SemanticRepositoryLock repo) {
        for (SemanticRepositoryLock repoLock : repoLocks) {
            if (repoLock == repo) {
                // there was no writer before - repository is free to read
                return true;
            } else {
                if (repoLock.getThread() != repo.getThread() && repoLock.getMode() == LockMode.TO_WRITE) {
                    return false;
                }
            }
        }
        logger.error("Repo " + repo + " is not on the list of repos");
        throw new IllegalArgumentException("Repo is not on the list of repos");
    }


    /**
     * Checks if repo is free to write. There is no readers and writes before.
     * 
     * @param repo
     *            repo
     * @return whether is it free to write
     */
    private boolean isFreeToWrite(SemanticRepositoryLock repo) {
        for (SemanticRepositoryLock repoLock : repoLocks) {
            if (repoLock == repo) {
                // there was no other locks before - node is free to write
                return true;
            } else {
                if (repoLock.getThread() != repo.getThread()) {
                    return false;
                }
            }
        }
        logger.error("Repo " + repo + " is not on the list of repos");
        throw new IllegalArgumentException("Repo is not on the list of repos");
    }

}
