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
package pl.psnc.synat.wrdz.ms.dao.messages.impl;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import pl.psnc.synat.wrdz.common.dao.GenericDaoBean;
import pl.psnc.synat.wrdz.ms.dao.messages.NotifyEmailDao;
import pl.psnc.synat.wrdz.ms.entity.messages.NotifyEmail;

/**
 * Default implementation of {@link NotifyEmailDao}.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class NotifyEmailDaoBean extends GenericDaoBean<NotifyEmail, Long> implements NotifyEmailDao {

    /**
     * Creates a new instance of NotifyEmailDaoBean.
     */
    public NotifyEmailDaoBean() {
        super(NotifyEmail.class);
    }
}
