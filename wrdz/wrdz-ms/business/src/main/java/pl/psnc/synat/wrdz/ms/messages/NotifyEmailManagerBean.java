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
package pl.psnc.synat.wrdz.ms.messages;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import pl.psnc.synat.wrdz.ms.dao.messages.NotifyEmailDao;
import pl.psnc.synat.wrdz.ms.entity.messages.NotifyEmail;

/**
 * Default implementation of the {@link NotifyEmailManager}.
 */
@Stateless
public class NotifyEmailManagerBean implements NotifyEmailManager {

    /** Notify email DAO. */
    @EJB
    private NotifyEmailDao emailDao;


    @Override
    public List<NotifyEmail> getEmails() {
        return emailDao.findAll();
    }


    @Override
    public NotifyEmail saveEmail(String emailAddress) {
        if (emailAddress != null) {
            NotifyEmail email = new NotifyEmail();
            email.setAddress(emailAddress);
            emailDao.persist(email);
            return email;
        }
        return null;
    }


    @Override
    public void deleteEmail(long emailId) {
        NotifyEmail email = emailDao.getReference(emailId);
        if (email != null) {
            emailDao.delete(email);
        }
    }

}
