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
package pl.psnc.synat.wrdz.zmkd.ddr;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.apache.devicemap.simpleddr.DDRService;
import org.apache.devicemap.simpleddr.VocabularyService;
import org.apache.devicemap.simpleddr.model.ODDRHTTPEvidence;
import org.w3c.ddr.simple.Evidence;
import org.w3c.ddr.simple.PropertyRef;
import org.w3c.ddr.simple.PropertyValue;
import org.w3c.ddr.simple.PropertyValues;
import org.w3c.ddr.simple.Service;
import org.w3c.ddr.simple.ServiceFactory;
import org.w3c.ddr.simple.exception.InitializationException;
import org.w3c.ddr.simple.exception.NameException;
import org.w3c.ddr.simple.exception.ValueException;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.zmkd.ddr.DeviceInfo.DeviceInfoBuilder;

/**
 * Default implementation of DDRHelper.
 */
@Singleton
@Lock(LockType.READ)
public class DDRHelperBean implements DDRHelper {

    /** Identification service. */
    private Service identificationService;

    /** Vendor. */
    private PropertyRef vendorRef;

    /** Model. */
    private PropertyRef modelRef;

    /** Display width. */
    private PropertyRef displayWidthRef;

    /** Display height. */
    private PropertyRef displayHeightRef;

    /** Cookie support. */
    private PropertyRef cookieSupportRef;

    /** Image format support. */
    private PropertyRef imageFormatSupportRef;

    /** All property references. */
    private PropertyRef[] propertyRefs;


    /**
     * Initializes the identification service and creates all property references.
     */
    @PostConstruct
    protected void init() {

        try {
            Properties properties = new Properties();
            
            ClassLoader cl = DDRHelperBean.class.getClassLoader();
            		
            InputStream deviceBuilderStream = cl.getResourceAsStream("devicedata/BuilderDataSource.xml");
            properties.put(DDRService.ODDR_UA_DEVICE_BUILDER_STREAM_PROP, deviceBuilderStream);
            
            InputStream deviceDatasourceStream = cl.getResourceAsStream("devicedata/DeviceDataSource.xml");
            properties.put(DDRService.ODDR_UA_DEVICE_DATASOURCE_STREAM_PROP, deviceDatasourceStream);
            
            InputStream[] deviceBuilderPatchStreams = new InputStream[1];
            deviceBuilderPatchStreams[0] = cl.getResourceAsStream("devicedata/BuilderDataSourcePatch.xml");
            properties.put(DDRService.ODDR_UA_DEVICE_BUILDER_PATCH_STREAMS_PROP, deviceBuilderPatchStreams);
            
            InputStream[] deviceDatasourcePatchStreams = new InputStream[1];
            deviceDatasourcePatchStreams[0] = cl.getResourceAsStream("devicedata/DeviceDataSourcePatch.xml");
       		properties.put(DDRService.ODDR_UA_DEVICE_DATASOURCE_PATCH_STREAMS_PROP, deviceDatasourcePatchStreams);
            
            InputStream browserDatasourceStream = cl.getResourceAsStream("devicedata/BrowserDataSource.xml");
            properties.put(DDRService.ODDR_UA_BROWSER_DATASOURCE_STREAM_PROP, browserDatasourceStream);
            
            InputStream operatingSystemDatasourceStream = cl.getResourceAsStream("devicedata/OperatingSystemDataSource.xml");
            properties.put(DDRService.ODDR_UA_OPERATINGSYSTEM_DATASOURCE_STREAM_PROP, operatingSystemDatasourceStream);
            
            InputStream coreVocabularyStream = cl.getResourceAsStream("devicedata/coreVocabulary.xml");
            properties.put(VocabularyService.DDR_CORE_VOCABULARY_STREAM_PROP, coreVocabularyStream);
            
            InputStream[] oddrVocabularyStreams = new InputStream[1];
            oddrVocabularyStreams[0] = cl.getResourceAsStream("devicedata/oddrVocabulary.xml");
            properties.put(VocabularyService.ODDR_VOCABULARY_STREAM_PROP, oddrVocabularyStreams);
            
            properties.put(DDRService.ODDR_VOCABULARY_IRI, "http://www.openddr.org/oddr-vocabulary");
            properties.put(DDRService.ODDR_THRESHOLD_PROP, 95);

            identificationService = ServiceFactory.newService("org.apache.devicemap.simpleddr.DDRService",
                properties.getProperty(DDRService.ODDR_VOCABULARY_IRI), properties);

            vendorRef = identificationService.newPropertyRef("vendor");
            modelRef = identificationService.newPropertyRef("model");
            displayWidthRef = identificationService.newPropertyRef("displayWidth");
            displayHeightRef = identificationService.newPropertyRef("displayHeight");
            cookieSupportRef = identificationService.newPropertyRef("cookieSupport");
            imageFormatSupportRef = identificationService.newPropertyRef("imageFormatSupport");

            propertyRefs = new PropertyRef[] { vendorRef, modelRef, displayWidthRef, displayHeightRef,
                    cookieSupportRef, imageFormatSupportRef };

        } catch (InitializationException e) {
            throw new WrdzRuntimeException("Error while initializing oddr identification service", e);
        } catch (NameException e) {
            throw new WrdzRuntimeException("Incorrect oddr property name", e);
        }
    }


    @Override
    public DeviceInfo getInfo(String userAgent) {

        Evidence evidence = new ODDRHTTPEvidence();
        evidence.put("User-Agent", userAgent);

        DeviceInfoBuilder builder = new DeviceInfoBuilder();

        try {
            PropertyValues propertyValues = identificationService.getPropertyValues(evidence, propertyRefs);
            PropertyValue vendor = propertyValues.getValue(vendorRef);
            PropertyValue model = propertyValues.getValue(modelRef);
            PropertyValue displayWidth = propertyValues.getValue(displayWidthRef);
            PropertyValue displayHeight = propertyValues.getValue(displayHeightRef);
            PropertyValue cookieSupport = propertyValues.getValue(cookieSupportRef);
            PropertyValue imageFormatSupport = propertyValues.getValue(imageFormatSupportRef);

            if (vendor.exists()) {
                builder.vendor(vendor.getString());
            }
            if (model.exists()) {
                builder.model(model.getString());
            }
            if (displayWidth.exists()) {
                builder.displayWidth(displayWidth.getInteger());
            }
            if (displayHeight.exists()) {
                builder.displayHeight(displayHeight.getInteger());
            }
            if (cookieSupport.exists()) {
                builder.cookieSupport(cookieSupport.getBoolean());
            }
            Set<String> image = new HashSet<String>();
            if (imageFormatSupport.exists()) {
                image.addAll(Arrays.asList(imageFormatSupport.getEnumeration()));
            }
            if (!image.isEmpty()) {
                builder.imageFormatSupport(image);
            }
        } catch (NameException ex) {
            throw new WrdzRuntimeException(ex.getMessage(), ex);
        } catch (ValueException ex) {
            throw new WrdzRuntimeException(ex.getMessage(), ex);
        }

        return builder.build();
    }
}
