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

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.ru.entity.registries.RemoteRegistry;
import pl.psnc.synat.wrdz.ru.exceptions.UnableToParseFormException;

import com.sun.jersey.core.header.ContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

/**
 * Parses the multipart form extracting registry's information from it and build remote registry entity from those
 * informations.
 */
public class RegistryFormParser {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(RegistryFormParser.class);

    /**
     * Name of the form field storing registry's name.
     */
    private static final String NAME = "name";

    /**
     * Name of the form field storing registry's location.
     */
    private static final String LOCATION = "location";

    /**
     * Name of the form field storing registry's certificate.
     */
    private static final String CERTIFICATE = "certificate";

    /**
     * Name of the form field storing registry's description.
     */
    private static final String DESCRIPTION = "description";

    /**
     * Name of the form field storing information whether the registry can read (harvest) local registry.
     */
    private static final String READ_ENABLED = "read-enabled";

    /**
     * Name of the form field storing information whether the registry is harvested by local registry.
     */
    private static final String HARVESTED = "harvested";

    /**
     * Stores remote registry builder instance.
     */
    private final RemoteRegistryBuilder builder;

    /**
     * Stores the passed multipart form fields map.
     */
    private final Map<String, List<FormDataBodyPart>> fields;


    /**
     * Constructs new parser for adding registry.
     * 
     * @param multipart
     *            multipart form fields.
     */
    public RegistryFormParser(FormDataMultiPart multipart) {
        this.builder = new RemoteRegistryBuilder();
        this.fields = multipart.getFields();
    }


    /**
     * Constructs new parser for registry's modification.
     * 
     * @param modified
     *            modified registry.
     * @param multipart
     *            multipart form fields.
     */
    public RegistryFormParser(RemoteRegistry modified, FormDataMultiPart multipart) {
        this.builder = new RemoteRegistryBuilder(modified);
        this.fields = multipart.getFields();
    }


    /**
     * Constructs remote registry using information extracted from the multipart form.
     * 
     * @param information
     *            information extracted from the multipart form.
     * @return remote registry filled with passed information.
     * @throws UnableToParseFormException
     *             should parsing the form and/or parameters passed in it fail.
     */
    public RemoteRegistry buildRegistryFromInformation(RegistryFormData information)
            throws UnableToParseFormException {
        RemoteRegistry build;
        try {
            build = builder.addName(information.getName()).addLocation(information.getLocation())
                    .addDescription(information.getDescription()).addReadEnabled(information.getReadEnabled())
                    .addHarvested(information.getHarvested()).build();
        } catch (URISyntaxException e) {
            throw new UnableToParseFormException("Manformed URI passed in location param", e);
        }
        return build;
    }


    /**
     * Extracts information about registry from the multipart form.
     * 
     * @return information extracted from the multipart form.
     * @throws UnableToParseFormException
     *             should parsing the form and/or parameters passed in it fail.
     */
    public RegistryFormData getRegistryInformation()
            throws UnableToParseFormException {
        RegistryFormData result = new RegistryFormData();
        for (String field : fields.keySet()) {
            List<FormDataBodyPart> parts = fields.get(field);
            for (FormDataBodyPart part : parts) {
                extractInformation(result, part.getName().toLowerCase(), part);
            }
        }
        return result;
    }


    /**
     * Extracts information from multipart form body part with given field name and puts it into passed
     * {@link RegistryFormData} object.
     * 
     * @param result
     *            registry information into which parsed information is saved.
     * @param field
     *            parsed field name.
     * @param part
     *            parsed multipart form body part.
     * @throws UnableToParseFormException
     *             should parsing the form and/or parameters passed in it fail.
     */
    private void extractInformation(RegistryFormData result, String field, FormDataBodyPart part)
            throws UnableToParseFormException {
        if (field.equals(NAME)) {
            result.setName(getStringFromParam(part.getValue()));
        } else if (field.equals(LOCATION)) {
            result.setLocation(getStringFromParam(part.getValue()));
        } else if (field.equals(CERTIFICATE)) {
            result.setCertificate(extractInputFileSource(part));
        } else if (field.equals(DESCRIPTION)) {
            result.setDescription(getStringFromParam(part.getValue()));
        } else if (field.equals(READ_ENABLED)) {
            result.setReadEnabled(getBooleanFromParam(getStringFromParam(part.getValue())));
        } else if (field.equals(HARVESTED)) {
            result.setHarvested(getBooleanFromParam(getStringFromParam(part.getValue())));
        }
    }


    /**
     * Transforms String into Boolean.
     * 
     * @param value
     *            value from which to extract boolean value.
     * @return boolean representation of String value.
     * @throws UnableToParseFormException
     *             should passed value be invalid boolean value.
     */
    private Boolean getBooleanFromParam(String value)
            throws UnableToParseFormException {
        if (Boolean.TRUE.toString().equals(value)) {
            return Boolean.TRUE;
        } else if (Boolean.FALSE.toString().equals(value)) {
            return Boolean.FALSE;
        } else if (value == null) {
            return null;
        } else {
            throw new UnableToParseFormException("Illegal value of " + value + " for boolean parameter");
        }

    }


    /**
     * Returns trimmed, nonempty string or <code>null</code>.
     * 
     * @param value
     *            value passed through the multipart form.
     * @return same value trimmed from both sides, empty values returned as <code>null</code>.
     */
    private String getStringFromParam(String value) {
        String result = null;
        if (value != null) {
            result = value.trim();
            if (result.isEmpty()) {
                result = null;
            }
        }
        return result;
    }


    /**
     * Extracts certificate from the multipart form.
     * 
     * @param part
     *            parsed multipart form body part.
     * @return extracted certificate or <code>null</code> if empty field was passed.
     * @throws UnableToParseFormException
     *             should passed value be invalid.
     */
    private byte[] extractInputFileSource(FormDataBodyPart part)
            throws UnableToParseFormException {
        ContentDisposition disp = part.getContentDisposition();
        if (disp.getFileName() != null) {
            InputStream is = part.getValueAs(InputStream.class);
            try {
                byte[] certificate = IOUtils.toByteArray(is);
                return certificate.length > 0 ? certificate : null;
            } catch (IOException e) {
                // fall through, error will be thrown nonetheless from the last line.
                logger.debug("IO error while parsing certificate data from multipart form: " + e.toString());
            }
        }
        throw new UnableToParseFormException("Could not parse the certificate information");
    }

}
