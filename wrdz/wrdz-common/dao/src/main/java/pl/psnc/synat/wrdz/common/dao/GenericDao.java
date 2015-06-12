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
package pl.psnc.synat.wrdz.common.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Interface for DAO beans. Defines basic operations for the entity class it concerns.
 * 
 * @param <T>
 *            entity class that will be managed by this DAO
 * @param <PK>
 *            {@link Serializable} class representing entity's primary key
 */
public interface GenericDao<T, PK extends Serializable> {

    /**
     * Saves the entity's instance state to a database.
     * <p>
     * <b>Method {@link #persist(Object)} must be used for saving new entities (performing <code>INSERT</code> into
     * database. It must not be used to update the entity's state ( <code>UPDATE</code> operation) else it will rise
     * exceptions.</b>
     * <p>
     * Executing {@link #persist(Object)} results in change in passed object's state in persistence context to
     * <i>managed/attached</i>, even if it was <i>detached</i> prior to method execution.
     * 
     * @param entity
     *            entity class instance to be saved into the database
     */
    void persist(T entity);


    /**
     * Saves the entity's instance state to a database.
     * <p>
     * <b>Method can be used for both saving new entities (performing <code>INSERT</code> into database and updating the
     * entity's state ( <code>UPDATE</code> operation). In former case it is strongly recommended to use
     * {@link #persist(Object)} instead</b>
     * <p>
     * Contrary to {@link #persist(Object)}, executing this method does not result in change in passed object's state in
     * persistence context, instead method returns new instance of this object that is <i>managed/attached</i>.
     * 
     * @param entity
     *            entity class instance to be saved into the database
     * @return entity class instance being the result of merge operation
     */
    T merge(T entity);


    /**
     * Removes entity from the database.
     * 
     * @param entity
     *            entity to be removed
     */
    void delete(T entity);


    /**
     * Gets reference to entity's proxy using lazy-initialization mechanism.
     * 
     * @param id
     *            primary key of searched object
     * @return reference to the lazily initialized proxy object
     * @throws DataAccessException
     */
    T getReference(PK id);


    /**
     * Refetches entity's state from the database dropping all unsaved changes made to it.
     * 
     * @param entity
     *            entity to be refetched from the database
     */
    void refresh(T entity);


    /**
     * Detaches the entity.
     * 
     * @param entity
     *            entity to be detached.
     */
    void detach(T entity);


    /**
     * Flushes the entity manager.
     */
    void flush();


    /**
     * Fetches entity with a given primary key value from the database or returns <code>null</code> if no matching
     * entity was found.
     * 
     * @param id
     *            primary key value of the searched entity
     * @return entity with a given primary key value or <code>null</code> if no matching entity was found
     */
    T findById(PK id);


    /**
     * Returns all entities found in the database.
     * 
     * @return all entities found in the database.
     */
    List<T> findAll();


    /**
     * Returns number of entities found in the database.
     * 
     * @return number of entities found in the database.
     */
    Long countAll();


    /**
     * Fetches the current timestamp from the database.
     * 
     * @return current datestamp of database server.
     */
    Date getDatabaseDate();

}
