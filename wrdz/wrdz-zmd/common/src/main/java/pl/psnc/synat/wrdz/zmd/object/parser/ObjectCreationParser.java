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
package pl.psnc.synat.wrdz.zmd.object.parser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.zmd.input.IncompleteDataException;
import pl.psnc.synat.wrdz.zmd.input.InvalidDataException;
import pl.psnc.synat.wrdz.zmd.input.ObjectCreationRequestBuilder;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;

/**
 * Parser of creation object requests. It parses requests encoded by multipart/form-data,
 * application/x-www-form-urlencoded, and data provided in zip.
 */
public class ObjectCreationParser {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ObjectCreationParser.class);

    /**
     * Root folder for saving files provided in the request.
     */
    private String root;


    /**
     * Constructs parser.
     * 
     * @param root
     *            root folder
     */
    public ObjectCreationParser(String root) {
        this.root = root;
    }


    /**
     * Parses the request encoded by multipart/form-data.
     * 
     * @param multipart
     *            request
     * @return internal representation of the request
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    public ObjectCreationRequest parse(com.sun.jersey.multipart.FormDataMultiPart multipart)
            throws IncompleteDataException, InvalidDataException {
        ObjectCreationRequestBuilder requestBuilder = new ObjectCreationRequestBuilder();
        Map<String, List<com.sun.jersey.multipart.FormDataBodyPart>> fields = multipart.getFields();
        for (String field : fields.keySet()) {
            List<com.sun.jersey.multipart.FormDataBodyPart> parts = fields.get(field);
            for (com.sun.jersey.multipart.FormDataBodyPart part : parts) {
                String name = part.getName();
                logger.debug("Control name for field " + field + " is " + name + ".");
                if (name.startsWith(ObjectManagerRequestConsts.REQUEST_PROPOSED_ID_PREFIX)) {
                    requestBuilder.setProposedId(part.getValue());
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_FILE_PREFIX)) {
                    if (name.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_SRC_SUFFIX)) {
                        extractInputFileSource(requestBuilder, field, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_DEST_SUFFIX)) {
                        extractInputFileDestination(requestBuilder, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_SEQ_SUFFIX)) {
                        extractInputFileSequence(requestBuilder, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_METADATA_SUFFIX)) {
                        extractInputFileMetadata(requestBuilder, field, part, name);
                    }
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_METADATA_PREFIX)) {
                    extractObjectMetadata(requestBuilder, field, part, name);
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_OBJECT_TYPE_PREFIX)) {
                    requestBuilder.setObjectType(part.getValue());
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_PREFIX)) {
                    if (name.endsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_ID_SUFFIX)) {
                        requestBuilder.setMigratedFromIdentifier(part.getValue());
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_ID_RESOLVER_SUFFIX)) {
                        requestBuilder.setMigratedFromIdentifierResolver(part.getValue());
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_TYPE_SUFFIX)) {
                        requestBuilder.setMigratedFromMigrationType(part.getValue());
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_DATE_SUFFIX)) {
                        requestBuilder.setMigratedFromMigrationDate(part.getValue());
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_INFO_SUFFIX)) {
                        requestBuilder.setMigratedFromMigrationInfo(part.getValue());
                    }
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_PREFIX)) {
                    if (name.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_SUFFIX)) {
                        extractDerivativeIdentifier(requestBuilder, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_RESOLVER_SUFFIX)) {
                        extractDerivativeIdentifierResolver(requestBuilder, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_TYPE_SUFFIX)) {
                        extractDerivativeType(requestBuilder, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_DATE_SUFFIX)) {
                        extractDerivativeDate(requestBuilder, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_INFO_SUFFIX)) {
                        extractDerivativeInfo(requestBuilder, part, name);
                    }
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_MAIN_FILE_PREFIX)) {
                    requestBuilder.setMainFile(part.getValue());
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_NAME_PREFIX)) {
                    requestBuilder.setName(part.getValue());
                }
            }
        }
        return requestBuilder.build();
    }


    /**
     * Extracts info about the source of the input data file from the request and sets in into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param part
     *            part of the request
     * @param name
     *            name of the part
     */
    private void extractInputFileSource(ObjectCreationRequestBuilder requestBuilder, String field,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        com.sun.jersey.core.header.ContentDisposition disp = part.getContentDisposition();
        String key = name.substring(0, name.length() - ObjectManagerRequestConsts.REQUEST_FILE_SRC_SUFFIX.length());
        if (disp.getFileName() == null) {
            logger.debug("Field " + field + " carries an URL to file.");
            requestBuilder.setInputFileSource(key, part.getValue());
        } else {
            logger.debug("Field " + field + " carries a file " + disp.getFileName());
            String filename = disp.getFileName().replace(' ', '_');
            ObjectManagerRequestHelper.savePartAsFile(part, root + "/" + key, filename);
            requestBuilder.setInputFileSource(key, ObjectManagerRequestHelper.makeUri(root + "/" + key, filename));
        }
    }


    /**
     * Extracts info about the destination of the input data file from the request and sets in into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param part
     *            part of the request
     * @param name
     *            name of the part
     */
    private void extractInputFileDestination(ObjectCreationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder.setInputFileDestination(
            name.substring(0, name.length() - ObjectManagerRequestConsts.REQUEST_FILE_DEST_SUFFIX.length()),
            part.getValue());
    }


    /**
     * Extracts info about the sequence position of the input data file from the request and sets in into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param part
     *            part of the request
     * @param name
     *            name of the part
     */
    private void extractInputFileSequence(ObjectCreationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder.setInputFileSequence(
            name.substring(0, name.length() - ObjectManagerRequestConsts.REQUEST_FILE_SEQ_SUFFIX.length()),
            part.getValue());
    }


    /**
     * Extracts info about the metadata file of the input data file from the request and sets in into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param part
     *            part of the request
     * @param name
     *            name of the part
     */
    private void extractInputFileMetadata(ObjectCreationRequestBuilder requestBuilder, String field,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        com.sun.jersey.core.header.ContentDisposition disp = part.getContentDisposition();
        String key = name
                .substring(0, name.length() - ObjectManagerRequestConsts.REQUEST_FILE_METADATA_SUFFIX.length());
        if (disp.getFileName() == null) {
            logger.debug("Field " + field + " carries an URL to file.");
            requestBuilder.addInputFileMetadataFile(key, part.getValue());
        } else {
            logger.debug("Field " + field + " carries a file " + disp.getFileName());
            String filename = disp.getFileName().replace(' ', '_');
            ObjectManagerRequestHelper.savePartAsFile(part, root + "/" + key, filename);
            requestBuilder
                    .addInputFileMetadataFile(key, ObjectManagerRequestHelper.makeUri(root + "/" + key, filename));
        }
    }


    /**
     * Extracts info about the metadata file of the digital object from the request and sets in into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param part
     *            part of the request
     * @param name
     *            name of the part
     */
    private void extractObjectMetadata(ObjectCreationRequestBuilder requestBuilder, String field,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        com.sun.jersey.core.header.ContentDisposition disp = part.getContentDisposition();
        if (disp.getFileName() == null) {
            logger.debug("Field " + field + " carries an URL to file.");
            requestBuilder.addObjectMetadata(part.getValue());
        } else {
            logger.debug("Field " + field + " carries a file " + disp.getFileName());
            String filename = disp.getFileName().replace(' ', '_');
            ObjectManagerRequestHelper.savePartAsFile(part, root, filename);
            requestBuilder.addObjectMetadata(ObjectManagerRequestHelper.makeUri(root, filename));
        }
    }


    /**
     * Extracts info about the identifier of the derivative of the digital object from the request and sets in into the
     * builder.
     * 
     * @param requestBuilder
     *            builder
     * @param part
     *            part of the request
     * @param name
     *            name of the part
     */
    private void extractDerivativeIdentifier(ObjectCreationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder.setMigratedToIdentifier(
            name.substring(0, name.length() - ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_SUFFIX.length()),
            part.getValue());
    }


    /**
     * Extracts info about the identifier resolver of the derivative of the digital object from the request and sets in
     * into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param part
     *            part of the request
     * @param name
     *            name of the part
     */
    private void extractDerivativeIdentifierResolver(ObjectCreationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder.setMigratedToIdentifierResolver(name.substring(0, name.length()
                - ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_RESOLVER_SUFFIX.length()), part.getValue());
    }


    /**
     * Extracts info about the migration type of the derivative of the digital object from the request and sets in into
     * the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param part
     *            part of the request
     * @param name
     *            name of the part
     */
    private void extractDerivativeType(ObjectCreationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder.setMigratedToMigrationType(
            name.substring(0, name.length() - ObjectManagerRequestConsts.REQUEST_DERIVATIVE_TYPE_SUFFIX.length()),
            part.getValue());
    }


    /**
     * Extracts info about the migration date of the derivative of the digital object from the request and sets in into
     * the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param part
     *            part of the request
     * @param name
     *            name of the part
     */
    private void extractDerivativeDate(ObjectCreationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder.setMigratedToMigrationDate(
            name.substring(0, name.length() - ObjectManagerRequestConsts.REQUEST_DERIVATIVE_DATE_SUFFIX.length()),
            part.getValue());
    }


    /**
     * Extracts info about the migration info of the derivative of the digital object from the request and sets in into
     * the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param part
     *            part of the request
     * @param name
     *            name of the part
     */
    private void extractDerivativeInfo(ObjectCreationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder.setMigratedToMigrationInfo(
            name.substring(0, name.length() - ObjectManagerRequestConsts.REQUEST_DERIVATIVE_INFO_SUFFIX.length()),
            part.getValue());
    }


    /**
     * Parses the request encoded by application/x-www-form-urlencoded.
     * 
     * @param params
     *            request
     * @return internal representation of the request
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    public ObjectCreationRequest parse(MultivaluedMap<String, String> params)
            throws IncompleteDataException, InvalidDataException {
        ObjectCreationRequestBuilder requestBuilder = new ObjectCreationRequestBuilder();
        for (String field : params.keySet()) {
            List<String> values = params.get(field);
            for (String value : values) {
                if (field.startsWith(ObjectManagerRequestConsts.REQUEST_PROPOSED_ID_PREFIX)) {
                    requestBuilder.setProposedId(value);
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_FILE_PREFIX)) {
                    if (field.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_SRC_SUFFIX)) {
                        extractInputFileSource(requestBuilder, field, value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_DEST_SUFFIX)) {
                        extractInputFileDestination(requestBuilder, field, value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_METADATA_SUFFIX)) {
                        extractInputFileMetadata(requestBuilder, field, value);
                    }
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_METADATA_PREFIX)) {
                    requestBuilder.addObjectMetadata(value);
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_OBJECT_TYPE_PREFIX)) {
                    requestBuilder.setObjectType(value);
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_PREFIX)) {
                    if (field.endsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_ID_SUFFIX)) {
                        requestBuilder.setMigratedFromIdentifier(value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_ID_RESOLVER_SUFFIX)) {
                        requestBuilder.setMigratedFromIdentifierResolver(value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_TYPE_SUFFIX)) {
                        requestBuilder.setMigratedFromMigrationType(value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_DATE_SUFFIX)) {
                        requestBuilder.setMigratedFromMigrationDate(value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_INFO_SUFFIX)) {
                        requestBuilder.setMigratedFromMigrationInfo(value);
                    }
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_PREFIX)) {
                    if (field.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_SUFFIX)) {
                        extractDerivativeIdentifier(requestBuilder, field, value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_RESOLVER_SUFFIX)) {
                        extractDerivativeIdentifierResolver(requestBuilder, field, value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_TYPE_SUFFIX)) {
                        extractDerivativeType(requestBuilder, field, value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_DATE_SUFFIX)) {
                        extractDerivativeDate(requestBuilder, field, value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_INFO_SUFFIX)) {
                        extractDerivativeInfo(requestBuilder, field, value);
                    }
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_MAIN_FILE_PREFIX)) {
                    requestBuilder.setMainFile(value);
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_NAME_PREFIX)) {
                    requestBuilder.setName(value);
                }
            }
        }
        return requestBuilder.build();
    }


    /**
     * Extracts info about the source of the input data file from the request and sets in into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param value
     *            value of the field
     */
    private void extractInputFileSource(ObjectCreationRequestBuilder requestBuilder, String field, String value) {
        requestBuilder.setInputFileSource(
            field.substring(0, field.length() - ObjectManagerRequestConsts.REQUEST_FILE_SRC_SUFFIX.length()), value);
    }


    /**
     * Extracts info about the destination of the input data file from the request and sets in into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param value
     *            value of the field
     */
    private void extractInputFileDestination(ObjectCreationRequestBuilder requestBuilder, String field, String value) {
        requestBuilder.setInputFileDestination(
            field.substring(0, field.length() - ObjectManagerRequestConsts.REQUEST_FILE_DEST_SUFFIX.length()), value);
    }


    /**
     * Extracts info about the metadata file of the input data file from the request and sets in into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param value
     *            value of the field
     */
    private void extractInputFileMetadata(ObjectCreationRequestBuilder requestBuilder, String field, String value) {
        requestBuilder.addInputFileMetadataFile(
            field.substring(0, field.length() - ObjectManagerRequestConsts.REQUEST_FILE_METADATA_SUFFIX.length()),
            value);
    }


    /**
     * Extracts info about the identifier of the derivative of the digital object from the request and sets in into the
     * builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param value
     *            value of the field
     */
    private void extractDerivativeIdentifier(ObjectCreationRequestBuilder requestBuilder, String field, String value) {
        requestBuilder.setMigratedToIdentifier(
            field.substring(0, field.length() - ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_SUFFIX.length()),
            value);
    }


    /**
     * Extracts info about the identifier resolver of the derivative of the digital object from the request and sets in
     * into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param value
     *            value of the field
     */
    private void extractDerivativeIdentifierResolver(ObjectCreationRequestBuilder requestBuilder, String field,
            String value) {
        requestBuilder.setMigratedToIdentifierResolver(
            field.substring(0,
                field.length() - ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_RESOLVER_SUFFIX.length()), value);
    }


    /**
     * Extracts info about the migration type of the derivative of the digital object from the request and sets in into
     * the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param value
     *            value of the field
     */
    private void extractDerivativeType(ObjectCreationRequestBuilder requestBuilder, String field, String value) {
        requestBuilder.setMigratedToMigrationType(
            field.substring(0, field.length() - ObjectManagerRequestConsts.REQUEST_DERIVATIVE_TYPE_SUFFIX.length()),
            value);
    }


    /**
     * Extracts info about the migration date of the derivative of the digital object from the request and sets in into
     * the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param value
     *            value of the field
     */
    private void extractDerivativeDate(ObjectCreationRequestBuilder requestBuilder, String field, String value) {
        requestBuilder.setMigratedToMigrationDate(
            field.substring(0, field.length() - ObjectManagerRequestConsts.REQUEST_DERIVATIVE_DATE_SUFFIX.length()),
            value);
    }


    /**
     * Extracts info about the migration info of the derivative of the digital object from the request and sets in into
     * the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param value
     *            value of the field
     */
    private void extractDerivativeInfo(ObjectCreationRequestBuilder requestBuilder, String field, String value) {
        requestBuilder.setMigratedToMigrationInfo(
            field.substring(0, field.length() - ObjectManagerRequestConsts.REQUEST_DERIVATIVE_INFO_SUFFIX.length()),
            value);
    }


    /**
     * Parses the request provided as ZIP.
     * 
     * @param zis
     *            input stream of the zip request
     * @param name
     *            object name (can be <code>null</code>)
     * @return internal representation of the request
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    public ObjectCreationRequest parse(ZipInputStream zis, String name)
            throws IncompleteDataException, InvalidDataException {
        ObjectCreationRequestBuilder requestBuilder = new ObjectCreationRequestBuilder();
        if (!new File(root).mkdir()) {
            logger.error("Root folder " + root + " creation failed.");
            throw new WrdzRuntimeException("Root folder " + root + " creation failed.");
        }
        byte[] buffer = new byte[1024];
        ZipEntry entry = null;
        try {
            while ((entry = zis.getNextEntry()) != null) {
                logger.debug("root: " + root + " unzipping: " + entry.getName() + " - is directory: "
                        + entry.isDirectory());
                if (entry.isDirectory()) {
                    if (!new File(root + "/" + entry.getName()).mkdir()) {
                        logger.error("folder " + entry.getName() + " creation failed.");
                        throw new WrdzRuntimeException("Folder " + entry.getName() + " creation failed.");
                    }
                } else {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(root + "/"
                            + entry.getName()), 1024);
                    int len;
                    while ((len = zis.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }
                    bos.flush();
                    bos.close();
                    if (entry.getName().toLowerCase().startsWith("metadata/")) {
                        String metadataName = entry.getName().substring(new String("metadata/").length());
                        if (metadataName.contains("/")) {
                            requestBuilder.addInputFileMetadataFile(
                                metadataName.substring(0, metadataName.lastIndexOf('/')),
                                ObjectManagerRequestHelper.makeUri(root, entry.getName()));
                        } else {
                            requestBuilder.addObjectMetadata(ObjectManagerRequestHelper.makeUri(root, entry.getName()));
                        }
                    } else {
                        String contentName = entry.getName();
                        if (contentName.toLowerCase().startsWith("content/")) {
                            contentName = contentName.substring(new String("content/").length());
                        }
                        requestBuilder.setInputFileSource(contentName,
                            ObjectManagerRequestHelper.makeUri(root, entry.getName()));
                        requestBuilder.setInputFileDestination(contentName, contentName);
                    }
                }
                zis.closeEntry();
            }
            zis.close();
        } catch (FileNotFoundException e) {
            logger.error("Saving the file " + entry.getName() + " failed!");
            throw new WrdzRuntimeException(e.getMessage());
        } catch (IOException e) {
            logger.error("Uncorrect zip file.", e);
            FileUtils.deleteQuietly(new File(root));
            throw new InvalidDataException(e.getMessage());
        }
        requestBuilder.setName(name);
        return requestBuilder.build();
    }

}
