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
package pl.psnc.synat.wrdz.mdz.dao.format.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericDaoBean;
import pl.psnc.synat.wrdz.mdz.dao.format.FileFormatDao;
import pl.psnc.synat.wrdz.mdz.entity.format.FileFormat;

/**
 * A class managing the persistence of {@link FileFormat} class. It implements additional operations available for
 * {@link FileFormat} object (as defined in {@link FileFormatDao}).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class FileFormatDaoBean extends GenericDaoBean<FileFormat, String> implements FileFormatDao {

    /**
     * Creates a new instance of FileFormatDaoBean.
     */
    public FileFormatDaoBean() {
        super(FileFormat.class);
    }


    @Override
    public FileFormat get() {
        CriteriaQuery<FileFormat> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<FileFormat> root = criteriaQuery.from(clazz);
        criteriaQuery.select(root);
        List<FileFormat> results = entityManager.createQuery(criteriaQuery).setMaxResults(1).getResultList();
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }
}
