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
package pl.psnc.synat.wrdz.zmkd.invocation;


/**
 * Class for values for parameter being all body of the request.
 */
public class ExecutionBodyParam {

    /** Value of the parameter - in case of string. */
    private String value;

    /** Value of the parameter - in case of file to send. */
    private FileValue fileValue;


    public void setValue(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }


    public void setFileValue(FileValue fileValue) {
        this.fileValue = fileValue;
    }


    public FileValue getFileValue() {
        return fileValue;
    }

}
