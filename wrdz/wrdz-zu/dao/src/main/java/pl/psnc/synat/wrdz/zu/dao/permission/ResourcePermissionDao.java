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
package pl.psnc.synat.wrdz.zu.dao.permission;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDao;
import pl.psnc.synat.wrdz.zu.entity.permission.ResourcePermission;

/**
 * An interface for a class managing the persistence of {@link ResourcePermission} class. It declares additional
 * operations available for {@link ResourcePermission} object apart from basic contract defined in
 * {@link ExtendedGenericDao}.
 */
@Local
public interface ResourcePermissionDao extends
        ExtendedGenericDao<ResourcePermissionFilterFactory, ResourcePermissionSorterBuilder, ResourcePermission, Long> {

}
