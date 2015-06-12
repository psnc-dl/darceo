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
package pl.psnc.synat.wrdz.ru.dao.registries;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.ru.entity.registries.RemoteRegistry;

/**
 * Specified set of filters for queries concerning {@link RemoteRegistry} entities.
 */
public interface RemoteRegistryFilterFactory extends GenericQueryFilterFactory<RemoteRegistry> {

    /**
     * Filters remote registries by their addresses, uses the Java RegExp syntax.
     * 
     * @param location
     *            registry address.
     * @return produced filter.
     */
    QueryFilter<RemoteRegistry> byLocationUrl(String location);


    /**
     * Filters the remote registries by their ability to read local registry.
     * 
     * @param readEnabled
     *            whether or not remote registry can read (harvest) local registry.
     * @return produced filter.
     */
    QueryFilter<RemoteRegistry> byReadEnabled(boolean readEnabled);


    /**
     * Filters the remote registries by the marker specifying whether or not they are harvested by the local registry.
     * 
     * @param harvested
     *            whether or not local registry harvests this remote registry.
     * @return produced filter.
     */
    QueryFilter<RemoteRegistry> byHarvested(boolean harvested);


    /**
     * Filters the remote registries by the primary key value.
     * 
     * @param id
     *            primary key value.
     * @return produced filter.
     */
    QueryFilter<RemoteRegistry> byId(long id);

}
