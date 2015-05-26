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
package pl.psnc.synat.wrdz.zmd.object;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectDao;
import pl.psnc.synat.wrdz.zmd.dao.object.IdentifierDao;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier;
import pl.psnc.synat.wrdz.zu.dto.user.UserDto;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Default implementation of {@link IdentifierBrowser}.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class IdentifierBrowserBean implements IdentifierBrowser {

    /** Identifier DAO. */
    @EJB
    private IdentifierDao identifierDao;

    /**
     * Digital object DAO.
     */
    @EJB
    private DigitalObjectDao digitalObjectDao;

    /** User browser. */
    @EJB(name = "UserBrowser")
    private UserBrowser userBrowser;


    @Override
    public String findNextActiveIdentifier(String previousIdentifier) {
        Identifier identifier = identifierDao.findNextActiveIdentifier(previousIdentifier);
        if (identifier != null) {
            return identifier.getIdentifier();
        } else {
            return null;
        }
    }


    @Override
    public List<String> getIdentifiersForMigration(String puid, String username, Collection<Long> objectIds) {
        Long ownerId = null;
        if (username != null) {
            UserDto owner = userBrowser.getUser(username);
            if (owner != null) {
                ownerId = owner.getId();
            } else {
                return Collections.emptyList();
            }
        }

        if (objectIds != null && objectIds.isEmpty()) {
            return Collections.emptyList();
        }

        return identifierDao.getIdentifiersForMigration(puid, ownerId, objectIds);
    }


    @Override
    public Long getObjectId(String identifier) {
        DigitalObject object = digitalObjectDao.getDigitalObject(identifier);
        if (object != null) {
            return object.getId();
        }
        return null;
    }
}
