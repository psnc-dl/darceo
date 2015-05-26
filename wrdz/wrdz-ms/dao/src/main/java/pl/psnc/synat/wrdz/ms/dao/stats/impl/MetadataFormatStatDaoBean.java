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
package pl.psnc.synat.wrdz.ms.dao.stats.impl;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import pl.psnc.synat.wrdz.common.dao.GenericDaoBean;
import pl.psnc.synat.wrdz.ms.dao.stats.MetadataFormatStatDao;
import pl.psnc.synat.wrdz.ms.entity.stats.MetadataFormatStat;

/**
 * Default implementation of {@link MetadataFormatStatDao}.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class MetadataFormatStatDaoBean extends GenericDaoBean<MetadataFormatStat, Long> implements
        MetadataFormatStatDao {

    /**
     * Creates a new instance of MetadataFormatStatDaoBean.
     */
    public MetadataFormatStatDaoBean() {
        super(MetadataFormatStat.class);
    }


    @Override
    public void deleteAll() {
        entityManager.createQuery("delete from MetadataFormatStat").executeUpdate();
        entityManager.flush();
    }
}
