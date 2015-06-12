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
package pl.psnc.synat.wrdz.ru.registries;

import java.net.URI;
import java.net.URISyntaxException;

import pl.psnc.synat.wrdz.ru.entity.registries.RemoteRegistry;

/**
 * Helps to construct new remote registry.
 */
public class RemoteRegistryBuilder {

    /**
     * Constructed remote registry.
     */
    private final RemoteRegistry registry;


    /**
     * Creates new instance of {@link RemoteRegistryBuilder}.
     */
    public RemoteRegistryBuilder() {
        registry = new RemoteRegistry();
    }


    /**
     * Creates new instance of {@link RemoteRegistryBuilder}.
     * 
     * @param registry
     *            constructed registry.
     */
    public RemoteRegistryBuilder(RemoteRegistry registry) {
        if (registry == null) {
            registry = new RemoteRegistry();
        }
        this.registry = registry;
    }


    /**
     * Constructs the remote registry.
     * 
     * @return built remote registry.
     */
    public RemoteRegistry build() {
        return registry;
    }


    /**
     * Adds name to registry.
     * 
     * @param name
     *            name of the registry.
     * @return current representation of the builder.
     */
    public RemoteRegistryBuilder addName(String name) {
        if (name != null) {
            registry.setName(name);
        }
        return this;
    }


    /**
     * Adds location to registry.
     * 
     * @param location
     *            location of the registry.
     * @return current representation of the builder.
     * @throws URISyntaxException
     *             if string contents cannot be parsed into URI.
     */
    public RemoteRegistryBuilder addLocation(String location)
            throws URISyntaxException {
        if (location != null) {
            registry.setLocationUrl(new URI(location).toString());
        }
        return this;
    }


    /**
     * Adds username to registry.
     * 
     * @param username
     *            username of the registry.
     * @return current representation of the builder.
     */
    public RemoteRegistryBuilder addUsername(String username) {
        if (username != null) {
            registry.setUsername(username);
        }
        return this;
    }


    /**
     * Adds description to registry.
     * 
     * @param description
     *            description of the registry.
     * @return current representation of the builder.
     */
    public RemoteRegistryBuilder addDescription(String description) {
        if (description != null) {
            registry.setDescription(description);
        }
        return this;
    }


    /**
     * Adds marker indicating ability to read local registry to remote registry.
     * 
     * @param readEnabled
     *            whether the registry is able to read this registry or not.
     * @return current representation of the builder.
     */
    public RemoteRegistryBuilder addReadEnabled(Boolean readEnabled) {
        if (readEnabled != null) {
            registry.setReadEnabled(readEnabled);
        }
        return this;
    }


    /**
     * Adds marker indicating local registry harvests remote registry.
     * 
     * @param harvested
     *            whether the registry is harvested by this registry or not.
     * @return current representation of the builder.
     */
    public RemoteRegistryBuilder addHarvested(Boolean harvested) {
        if (harvested != null) {
            registry.setHarvested(harvested);
        }
        return this;
    }

}
