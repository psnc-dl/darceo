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
package pl.psnc.synat.wrdz.zmd.dao.authentication.impl;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.dao.authentication.RemoteRepositoryAuthenticationFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.authentication.RemoteRepository;
import pl.psnc.synat.wrdz.zmd.entity.authentication.RemoteRepositoryAuthentication;
import pl.psnc.synat.wrdz.zmd.entity.authentication.RemoteRepositoryAuthentication_;
import pl.psnc.synat.wrdz.zmd.entity.authentication.RemoteRepository_;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning
 * {@link RemoteRepositoryAuthentication}.
 */
public class RemoteRepositoryAuthenticationFilterFactoryImpl extends
        GenericQueryFilterFactoryImpl<RemoteRepositoryAuthentication> implements
        RemoteRepositoryAuthenticationFilterFactory {

    /**
     * Constructs this factory initializing it with required arguments.
     * 
     * @param criteriaBuilder
     *            reference to {@link CriteriaBuilder} object.
     * @param criteriaQuery
     *            query upon which filters will be build
     * @param root
     *            object representing root type of the entity this filter factory manages
     * @param epoch
     *            builder's epoch
     * 
     * @throws IllegalArgumentException
     *             thrown if one of arguments is <code>null</code>
     * 
     */
    public RemoteRepositoryAuthenticationFilterFactoryImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<RemoteRepositoryAuthentication> criteriaQuery, Root<RemoteRepositoryAuthentication> root,
            Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<RemoteRepositoryAuthentication> byRemoteRepositoryId(Long remoteRepositoryId) {
        Predicate predicate = criteriaBuilder.equal(
            root.get(RemoteRepositoryAuthentication_.remoteRepository).get(RemoteRepository_.id), remoteRepositoryId);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<RemoteRepositoryAuthentication> byRemoteRepositoryProtocol(String protocol) {
        Join<RemoteRepositoryAuthentication, RemoteRepository> remoteRepository = root
                .join(RemoteRepositoryAuthentication_.remoteRepository);
        Predicate predicate = criteriaBuilder.like(remoteRepository.get(RemoteRepository_.protocol), protocol);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<RemoteRepositoryAuthentication> byRemoteRepositoryHost(String host) {
        Join<RemoteRepositoryAuthentication, RemoteRepository> remoteRepository = root
                .join(RemoteRepositoryAuthentication_.remoteRepository);
        Predicate predicate = criteriaBuilder.like(remoteRepository.get(RemoteRepository_.host), host);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<RemoteRepositoryAuthentication> byRemoteRepositoryPort(Integer port) {
        Join<RemoteRepositoryAuthentication, RemoteRepository> remoteRepository = root
                .join(RemoteRepositoryAuthentication_.remoteRepository);
        Predicate predicate = criteriaBuilder.equal(remoteRepository.get(RemoteRepository_.port), port);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<RemoteRepositoryAuthentication> byUsername(String username) {
        Predicate predicate = criteriaBuilder.like(root.get(RemoteRepositoryAuthentication_.username), username);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<RemoteRepositoryAuthentication> byUsedByDefault(Boolean usedByDefault) {
        Predicate predicate = criteriaBuilder.equal(root.get(RemoteRepositoryAuthentication_.usedByDefault),
            usedByDefault);
        return constructQueryFilter(predicate);
    }

}
