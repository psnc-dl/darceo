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

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.apache.commons.io.IOUtils;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;

/**
 * Provides an access to SPARQL queries used in the RU module.
 */
@Singleton
public class SparqlQueries {

    /**
     * Path to the file containing the sparql query that retrieves atomic transformations.
     */
    private static final String ATOMIC_TRANSFORMATIONS_QUERY_FILE = "/sparqls/atomic-transformations-by-formats.sparql";

    /**
     * Path to the file containing the sparql query that retrieves composite transformations of the length 2.
     */
    private static final String COMPOSITE_2_TRANSFORMATIONS_QUERY_FILE = "/sparqls/composite-2-transformations-by-formats.sparql";

    /**
     * Path to the file containing the sparql query that retrieves composite transformations of the length 3.
     */
    private static final String COMPOSITE_3_TRANSFORMATIONS_QUERY_FILE = "/sparqls/composite-3-transformations-by-formats.sparql";

    /**
     * Path to the file containing the sparql query that the execution cost of a service.
     */
    private static final String SERVICE_EXECUTION_COST_QUERY_FILE = "/sparqls/service-execution-cost.sparql";

    /**
     * Path to the file containing the sparql query that retrieves service parameters.
     */
    private static final String SERVICE_PARAMETERS_QUERY_FILE = "/sparqls/service-parameters.sparql";

    /**
     * Path to the file containing the sparql query that retrieves service outcomes.
     */
    private static final String SERVICE_OUTCOMES_QUERY_FILE = "/sparqls/service-outcomes.sparql";

    /**
     * Path to the file containing the sparql query that retrieves bundle type.
     */
    private static final String BUNDLE_TYPE_QUERY_FILE = "/sparqls/bundle-type.sparql";

    /**
     * Path to the file containing the sparql query that retrieves service file formats.
     */
    private static final String SERVICE_FORMATS_QUERY_FILE = "/sparqls/service-formats.sparql";

    /**
     * Path to the file containing the sparql query that retrieves file format from given class.
     */
    private static final String CLASS_FORMAT_QUERY_FILE = "/sparqls/class-format.sparql";

    /**
     * Path to the file containing the sparql query that retrieves WADL nodes of the service method.
     */
    private static final String WADL_SERVICE_METHOD_QUERY_FILE = "/sparqls/wadl-service-method.sparql";

    /**
     * Path to the file containing the sparql query that retrieves WADL nodes of the service parameters.
     */
    private static final String WADL_SERVICE_PARAMETERS_QUERY_FILE = "/sparqls/wadl-service-parameters.sparql";

    /**
     * Path to the file containing the sparql query that retrieves WADL nodes of the service outcomes.
     */
    private static final String WADL_SERVICE_OUTCOMES_QUERY_FILE = "/sparqls/wadl-service-outcomes.sparql";

    /**
     * Path to the file containing the sparql query that retrieves advanced delivery services.
     */
    private static final String ADVANCED_DELIVERY_SERVICES_QUERY_FILE = "/sparqls/advanced-delivery-services.sparql";

    /**
     * Path to the file containing the sparql query that retrieves input formats of an advanced delivery service.
     */
    private static final String ADVANCED_DELIVERY_FORMATS_QUERY_FILE = "/sparqls/advanced-delivery-formats.sparql";

    /**
     * Sparql query that retrieves atomic transformations.
     */
    private String atomicTransformationsQuery;

    /**
     * Sparql query that retrieves composite transformations of the length 2.
     */
    private String composite2TransformationsQuery;

    /**
     * Sparql query that retrieves composite transformations of the length 3.
     */
    private String composite3TransformationsQuery;

    /**
     * Sparql query that retrieves the execution cost of a service.
     */
    private String serviceExecutionCostQuery;

    /**
     * Sparql query that retrieves service parameters.
     */
    private String serviceParametersQuery;

    /**
     * Sparql query that retrieves service outcomes.
     */
    private String serviceOutcomesQuery;

    /**
     * Sparql query that retrieves bundle type.
     */
    private String bundleTypeQuery;

    /**
     * Sparql query that retrieves service formats.
     */
    private String serviceFormatsQuery;

    /**
     * Sparql query that retrieves file format from given class.
     */
    private String classFormatQuery;

    /**
     * Sparql query that retrieves WADL nodes of the service method.
     */
    private String wadlServiceMethodQuery;

    /**
     * Sparql query that retrieves WADL nodes of the service parameters.
     */
    private String wadlServiceParametersQuery;

    /**
     * Sparql query that retrieves WADL nodes of the service outcomes.
     */
    private String wadlServiceOutcomesQuery;

    /**
     * Sparql query that retrieves advanced delivery services.
     */
    private String advancedDeliveryServicesQuery;

    /**
     * Sparql query that retrieves input formats of an advanced delivery service.
     */
    private String advancedDeliveryFormatsQuery;


    /**
     * Reads the sparql queries from resource files.
     */
    @PostConstruct
    protected void init() {
        atomicTransformationsQuery = readSparqlQuery(ATOMIC_TRANSFORMATIONS_QUERY_FILE);
        composite2TransformationsQuery = readSparqlQuery(COMPOSITE_2_TRANSFORMATIONS_QUERY_FILE);
        composite3TransformationsQuery = readSparqlQuery(COMPOSITE_3_TRANSFORMATIONS_QUERY_FILE);
        serviceExecutionCostQuery = readSparqlQuery(SERVICE_EXECUTION_COST_QUERY_FILE);
        serviceParametersQuery = readSparqlQuery(SERVICE_PARAMETERS_QUERY_FILE);
        serviceOutcomesQuery = readSparqlQuery(SERVICE_OUTCOMES_QUERY_FILE);
        bundleTypeQuery = readSparqlQuery(BUNDLE_TYPE_QUERY_FILE);
        serviceFormatsQuery = readSparqlQuery(SERVICE_FORMATS_QUERY_FILE);
        classFormatQuery = readSparqlQuery(CLASS_FORMAT_QUERY_FILE);
        wadlServiceMethodQuery = readSparqlQuery(WADL_SERVICE_METHOD_QUERY_FILE);
        wadlServiceParametersQuery = readSparqlQuery(WADL_SERVICE_PARAMETERS_QUERY_FILE);
        wadlServiceOutcomesQuery = readSparqlQuery(WADL_SERVICE_OUTCOMES_QUERY_FILE);
        advancedDeliveryServicesQuery = readSparqlQuery(ADVANCED_DELIVERY_SERVICES_QUERY_FILE);
        advancedDeliveryFormatsQuery = readSparqlQuery(ADVANCED_DELIVERY_FORMATS_QUERY_FILE);
    }


    /**
     * Reads a SPARQL query from a given file.
     * 
     * @param queryFilePath
     *            path to a file with query
     * @return query as string
     */
    private String readSparqlQuery(String queryFilePath) {
        InputStream queryInput = null;
        try {
            queryInput = getClass().getResourceAsStream(queryFilePath);
            return IOUtils.toString(queryInput, "UTF-8");
        } catch (IOException e) {
            throw new WrdzRuntimeException("Could not read sparql query files", e);
        } finally {
            IOUtils.closeQuietly(queryInput);
        }
    }


    public String getAtomicTransformationsQuery() {
        return atomicTransformationsQuery;
    }


    public String getComposite2TransformationsQuery() {
        return composite2TransformationsQuery;
    }


    public String getComposite3TransformationsQuery() {
        return composite3TransformationsQuery;
    }


    public String getServiceExecutionCostQuery() {
        return serviceExecutionCostQuery;
    }


    public String getServiceParametersQuery() {
        return serviceParametersQuery;
    }


    public String getServiceOutcomesQuery() {
        return serviceOutcomesQuery;
    }


    public String getBundleTypeQuery() {
        return bundleTypeQuery;
    }


    public String getServiceFormatsQuery() {
        return serviceFormatsQuery;
    }


    public String getClassFormatQuery() {
        return classFormatQuery;
    }


    public String getWadlServiceMethodQuery() {
        return wadlServiceMethodQuery;
    }


    public String getWadlServiceParametersQuery() {
        return wadlServiceParametersQuery;
    }


    public String getWadlServiceOutcomesQuery() {
        return wadlServiceOutcomesQuery;
    }


    public String getAdvancedDeliveryServicesQuery() {
        return advancedDeliveryServicesQuery;
    }


    public String getAdvancedDeliveryFormatsQuery() {
        return advancedDeliveryFormatsQuery;
    }

}
