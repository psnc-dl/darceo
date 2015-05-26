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
package pl.psnc.synat.wrdz.common.metadata.xmlns;

/**
 * Factory of the namespace recognizer factory.
 */
public final class NamespaceRecognizerFactory {

    /**
     * The only one instance of the factory.
     */
    private static NamespaceRecognizerFactory instance = new NamespaceRecognizerFactory();


    /**
     * Private constructor.
     */
    private NamespaceRecognizerFactory() {
    }


    /**
     * Returns the only one instance of this factory.
     * 
     * @return factory
     */
    public static NamespaceRecognizerFactory getInstance() {
        return instance;
    }


    /**
     * Gets a new namespace recognizer.
     * 
     * @return new namespace recognizer
     */
    public NamespaceRecognizer getNamespaceRecognizer() {
        return new NamespaceRecognizer();
    }

}
