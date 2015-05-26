﻿/**
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
package pl.psnc.synat.wrdz.zmd.dao.object.content.impl;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQuerySorterBuilderImpl;
import pl.psnc.synat.wrdz.zmd.dao.object.content.ContentVersionSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion_;

/**
 * Class providing easy access to creation of ordering arguments for the queries concerning {@link ContentVersion}.
 */
public class ContentVersionSorterBuilderImpl extends GenericQuerySorterBuilderImpl<ContentVersion> implements
        ContentVersionSorterBuilder {

    /**
     * Constructor initializing the class for proper work with queries.
     * 
     * @param criteriaBuilder
     *            reference to {@link CriteriaBuilder} object.
     * @param criteriaQuery
     *            query upon which sorters will be build
     * @param root
     *            object representing root type of the entity this sorter builder manages
     * @param epoch
     *            value of the epoch counter associated with the instance of this class
     * @throws IllegalArgumentException
     *             thrown when any of the arguments is null
     */
    public ContentVersionSorterBuilderImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ContentVersion> criteriaQuery, Root<ContentVersion> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected Path<?> getIdPath() {
        return root.get(ContentVersion_.id);
    }


    @Override
    public ContentVersionSorterBuilder byVersion(boolean ascendingly) {
        addOrdering(root.get(ContentVersion_.version), ascendingly);
        return this;
    }

}
