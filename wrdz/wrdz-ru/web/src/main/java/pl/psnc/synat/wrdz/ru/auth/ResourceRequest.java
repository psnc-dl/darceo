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
package pl.psnc.synat.wrdz.ru.auth;

/**
 * Describes resource request by pair of strings, one containing name of used HTTP method, second the pathPattern inside
 * the application context accessed by user.
 */
class ResourceRequest {

    /**
     * Pattern of the path inside the application context accessed by user.
     */
    private final String pathPattern;

    /**
     * Name of used HTTP method.
     */
    private final String method;


    /**
     * Constructs new resource request out of specified parameters.
     * 
     * @param pathPattern
     *            pathPattern inside the application context accessed by user.
     * @param method
     *            name of used HTTP method.
     */
    ResourceRequest(String pathPattern, String method) {
        this.pathPattern = pathPattern;
        this.method = method;
    }


    public String getPathPattern() {
        return pathPattern;
    }


    public String getMethod() {
        return method;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result + ((pathPattern == null) ? 0 : pathPattern.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ResourceRequest)) {
            return false;
        }
        ResourceRequest other = (ResourceRequest) obj;
        if (method == null) {
            if (other.method != null) {
                return false;
            }
        } else if (!method.equals(other.method)) {
            return false;
        }
        if (pathPattern == null) {
            if (other.pathPattern != null) {
                return false;
            }
        } else if (!pathPattern.equals(other.pathPattern)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ResourceRequest [pathPattern=").append(pathPattern).append(", method=").append(method)
                .append("]");
        return builder.toString();
    }

}
