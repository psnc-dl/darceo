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
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * A simple generic DAO object handling the basic operations on entities with given primary key type.
 * 
 * @param <T>
 *            type of entity to be handled.
 * @param <PK>
 *            type of primary key of the given entity.
 */
public class GenericDaoBean<T, PK extends Serializable> implements GenericDao<T, PK> {

    /**
     * Entity manager injected into this DAO to be used for persistence operations.
     */
    protected EntityManager entityManager;

    /**
     * Criteria builder being constructed from the injected {@link #entityManager} to be used to construct queries.
     */
    protected CriteriaBuilder criteriaBuilder;

    /**
     * Class type representing entity managed by this DAO.
     */
    protected Class<T> clazz;


    /**
     * Constructor that sets the class the instance will be responsible for.
     * 
     * @param clazz
     *            class for which the instance will be responsible.
     */
    public GenericDaoBean(Class<T> clazz) {
        this.clazz = clazz;
    }


    /**
     * Method for setting instance of {@link EntityManager} and setup of dependent fields associated with operations on
     * entities.
     * 
     * @param entityManager
     *            {@link EntityManager} instance to be used for operations on entities.
     */
    @PersistenceContext(unitName = "postgres")
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }


    @Override
    public void persist(T entity) {
        entityManager.persist(entity);
    }


    @Override
    public T merge(T entity) {
        return entityManager.merge(entity);
    }


    @Override
    public void delete(T entity) {
        entityManager.remove(entity);
    }


    @Override
    public void flush() {
        entityManager.flush();
    }


    @Override
    public T getReference(PK id) {
        return entityManager.getReference(clazz, id);
    }


    @Override
    public void refresh(T entity) {
        entityManager.refresh(entity);
    }


    @Override
    public void detach(T entity) {
        entityManager.detach(entity);
    }


    @Override
    public T findById(PK id) {
        return entityManager.find(clazz, id);
    }


    @Override
    public List<T> findAll() {
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);
        criteriaQuery.select(root);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }


    @Override
    public Long countAll() {
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> root = criteriaQuery.from(clazz);
        criteriaQuery.select(criteriaBuilder.count(root));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }


    @Override
    public Date getDatabaseDate() {
        Timestamp now = entityManager.createNamedQuery("now", Timestamp.class).getSingleResult();
        return new Date(now.getTime());
    }

}
