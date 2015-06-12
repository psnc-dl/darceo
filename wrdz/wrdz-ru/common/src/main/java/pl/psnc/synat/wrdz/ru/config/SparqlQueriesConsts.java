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
package pl.psnc.synat.wrdz.ru.config;

/**
 * Constants for names of variables used in SPARQL queries.
 */
public final class SparqlQueriesConsts {

    /**
     * Private constructor.
     */
    private SparqlQueriesConsts() {
    }


    /************************************************************************************************************/

    /**
     * The name of variable for the service IRI in SPARQL queries.
     */
    public static final String VARIABLE_SERVICE_IRI = "service";

    /**
     * The name of variable for the profile IRI in SPARQL queries.
     */
    public static final String VARIABLE_PROFILE_IRI = "profile";

    /**
     * The name of variable for the service IRI in SPARQL queries.
     */
    public static final String VARIABLE_PROCESS_IRI = "process";

    /**
     * The name of variable for the execution cost of the service in SPARQL queries.
     */
    public static final String VARIABLE_EXECUTION_COST = "cost";

    /**
     * The name of variable for the transformation IRI in SPARQL queries.
     */
    public static final String VARIABLE_TRANSFORMATION_IRI = "transformation";

    /**
     * The name of variable for the input format IRI.
     */
    public static final String VARIABLE_INPUT_FORMAT_IRI = "informat";

    /**
     * The name of variable for the output format IRI.
     */
    public static final String VARIABLE_OUTPUT_FORMAT_IRI = "outformat";

    /**
     * The name of variable for the parameter of the service in its profile.
     */
    public static final String VARIABLE_SERVICE_PARAMETER_IRI = "param";

    /**
     * The name of variable for the parameter type of the service in its process.
     */
    public static final String VARIABLE_SERVICE_PARAMETER_TYPE_IRI = "paramtype";

    /**
     * The name of variable for the previous transformation IRI in SPARQL queries.
     */
    public static final String VARIABLE_PREVIOUS_TRANSFORMATION_IRI = "previous";

    /**
     * The name of variable for the subsequent transformation IRI in SPARQL queries.
     */
    public static final String VARIABLE_SUBSEQUENT_TRANSFORMATION_IRI = "subsequent";

}
