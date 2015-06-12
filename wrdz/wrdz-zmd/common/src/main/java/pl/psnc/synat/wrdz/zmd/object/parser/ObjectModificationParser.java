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
import pl.psnc.synat.wrdz.zmd.input.ObjectModificationRequestBuilder;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;

/**
 * Parser of modification object requests. It parses requests encoded by multipart/form-data,
 * application/x-www-form-urlencoded, and data provided in zip.
 */
public class ObjectModificationParser {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ObjectModificationParser.class);

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
    public ObjectModificationParser(String root) {
        this.root = root;
    }


    /**
     * Parses the request encoded by multipart/form-data.
     * 
     * @param identifier
     *            identifier of the object
     * @param multipart
     *            request
     * @return internal representation of the request
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    public ObjectModificationRequest parse(String identifier, com.sun.jersey.multipart.FormDataMultiPart multipart)
            throws IncompleteDataException, InvalidDataException {
        ObjectModificationRequestBuilder requestBuilder = new ObjectModificationRequestBuilder(identifier);
        Map<String, List<com.sun.jersey.multipart.FormDataBodyPart>> fields = multipart.getFields();
        for (String field : fields.keySet()) {
            List<com.sun.jersey.multipart.FormDataBodyPart> parts = fields.get(field);
            for (com.sun.jersey.multipart.FormDataBodyPart part : parts) {
                String name = part.getName();
                logger.debug("Control name for field " + field + " is " + name + ".");
                if (name.startsWith(ObjectManagerRequestConsts.REQUEST_ADD_FILE_PREFIX)) {
                    if (name.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_SRC_SUFFIX)) {
                        extractInputFileToAddSource(requestBuilder, field, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_DEST_SUFFIX)) {
                        extractInputFileToAddDestination(requestBuilder, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_SEQ_SUFFIX)) {
                        extractInputFileToAddSequence(requestBuilder, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_METADATA_SUFFIX)) {
                        extractInputFileToAddMetadata(requestBuilder, field, part, name);
                    }
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_MOD_FILE_PREFIX)) {
                    if (name.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_SRC_SUFFIX)) {
                        extractInputFileToModifySource(requestBuilder, field, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_DEST_SUFFIX)) {
                        extractInputFileToModifyDestination(requestBuilder, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_SEQ_SUFFIX)) {
                        extractInputFileToModifySequence(requestBuilder, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_MOD_FILE_METADATA_ADD_SUFFIX)) {
                        extractInputFileToModifyMetadataToAdd(requestBuilder, field, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_MOD_FILE_METADATA_MOD_SUFFIX)) {
                        extractInputFileToModifyMetadataToModify(requestBuilder, field, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_MOD_FILE_METADATA_DEL_SUFFIX)) {
                        extractInputFileToModifyMetadataToRemove(requestBuilder, part, name);
                    }
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_DEL_FILE_PREFIX)) {
                    if (name.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_DEST_SUFFIX)) {
                        requestBuilder.addInputFileToRemoveDestination(part.getValue());
                    }
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_ADD_METADATA_PREFIX)) {
                    extractObjectMetadataToAdd(requestBuilder, field, part, name);
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_MOD_METADATA_PREFIX)) {
                    extractObjectMetadataToModify(requestBuilder, field, part, name);
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_DEL_METADATA_PREFIX)) {
                    requestBuilder.addObjectMetadataToRemove(part.getValue());
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_PREFIX)) {
                    if (name.endsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_ID_SUFFIX)) {
                        requestBuilder.setMigratedFromIdentifier(part.getValue());
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_ID_RESOLVER_SUFFIX)) {
                        requestBuilder.setMigratedFromIdentifierResolver(part.getValue());
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_TYPE_SUFFIX)) {
                        requestBuilder.setMigratedFromMigrationType(part.getValue());
                    }
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_ADD_DERIVATIVE_PREFIX)) {
                    if (name.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_SUFFIX)) {
                        extractDerivativeIdentifierToAdd(requestBuilder, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_RESOLVER_SUFFIX)) {
                        extractDerivativeIdentifierResolverToAdd(requestBuilder, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_TYPE_SUFFIX)) {
                        extractDerivativeTypeToAdd(requestBuilder, part, name);
                    }
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_MOD_DERIVATIVE_PREFIX)) {
                    if (name.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_SUFFIX)) {
                        extractDerivativeIdentifierToModify(requestBuilder, part, name);
                    } else if (name.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_RESOLVER_SUFFIX)) {
                        extractDerivativeIdentifierResolverToModify(requestBuilder, part, name);
                    }
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_DEL_DERIVATIVE_PREFIX)) {
                    if (name.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_SUFFIX)) {
                        requestBuilder.addMigratedToToRemoveIdentifier(part.getValue());
                    }
                } else if (name.startsWith(ObjectManagerRequestConsts.REQUEST_MAIN_FILE_PREFIX)) {
                    requestBuilder.setMainFile(part.getValue());
                }
            }
        }
        return requestBuilder.build();
    }


    /**
     * Extracts info about the source of the input data file to add to the object from the request and sets in into the
     * builder.
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
    private void extractInputFileToAddSource(ObjectModificationRequestBuilder requestBuilder, String field,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        com.sun.jersey.core.header.ContentDisposition disp = part.getContentDisposition();
        String key = name.substring(0, name.length() - ObjectManagerRequestConsts.REQUEST_FILE_SRC_SUFFIX.length());
        if (disp.getFileName() == null) {
            logger.debug("Field " + field + " carries an URL to file.");
            requestBuilder.setInputFileToAddSource(key, part.getValue());
        } else {
            logger.debug("Field " + field + " carries a file " + disp.getFileName());
            String filename = disp.getFileName().replace(' ', '_');
            ObjectManagerRequestHelper.savePartAsFile(part, root + "/" + key, filename);
            requestBuilder.setInputFileToAddSource(key, ObjectManagerRequestHelper.makeUri(root + "/" + key, filename));
        }
    }


    /**
     * Extracts info about the destination of the input data file to add to the object from the request and sets in into
     * the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param part
     *            part of the request
     * @param name
     *            name of the part
     */
    private void extractInputFileToAddDestination(ObjectModificationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder.setInputFileToAddDestination(
            name.substring(0, name.length() - ObjectManagerRequestConsts.REQUEST_FILE_DEST_SUFFIX.length()),
            part.getValue());
    }


    /**
     * Extracts info about the sequence of the input data file to add to the object from the request and sets in into
     * the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param part
     *            part of the request
     * @param name
     *            name of the part
     */
    private void extractInputFileToAddSequence(ObjectModificationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder.setInputFileToAddSequence(
            name.substring(0, name.length() - ObjectManagerRequestConsts.REQUEST_FILE_SEQ_SUFFIX.length()),
            part.getValue());
    }


    /**
     * Extracts info about the metadata file of the input data file to add to the object from the request and sets in
     * into the builder.
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
    private void extractInputFileToAddMetadata(ObjectModificationRequestBuilder requestBuilder, String field,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        com.sun.jersey.core.header.ContentDisposition disp = part.getContentDisposition();
        String key = name
                .substring(0, name.length() - ObjectManagerRequestConsts.REQUEST_FILE_METADATA_SUFFIX.length());
        if (disp.getFileName() == null) {
            logger.debug("Field " + field + " carries an URL to file.");
            requestBuilder.addInputFileToAddMetadataFile(key, part.getValue());
        } else {
            logger.debug("Field " + field + " carries a file " + disp.getFileName());
            String filename = disp.getFileName().replace(' ', '_');
            ObjectManagerRequestHelper.savePartAsFile(part, root + "/" + key, filename);
            requestBuilder.addInputFileToAddMetadataFile(key,
                ObjectManagerRequestHelper.makeUri(root + "/" + key, filename));
        }
    }


    /**
     * Extracts info about the source of the input data file to modify in the object from the request and sets in into
     * the builder.
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
    private void extractInputFileToModifySource(ObjectModificationRequestBuilder requestBuilder, String field,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        com.sun.jersey.core.header.ContentDisposition disp = part.getContentDisposition();
        String key = name.substring(0, name.length() - ObjectManagerRequestConsts.REQUEST_FILE_SRC_SUFFIX.length());
        if (disp.getFileName() == null) {
            logger.debug("Field " + field + " carries an URL to file.");
            requestBuilder.setInputFileToModifySource(key, part.getValue());
        } else {
            logger.debug("Field " + field + " carries a file " + disp.getFileName());
            String filename = disp.getFileName().replace(' ', '_');
            ObjectManagerRequestHelper.savePartAsFile(part, root + "/" + key, filename);
            requestBuilder.setInputFileToModifySource(key,
                ObjectManagerRequestHelper.makeUri(root + "/" + key, filename));
        }
    }


    /**
     * Extracts info about the destination of the input data file to modify in the object from the request and sets in
     * into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param part
     *            part of the request
     * @param name
     *            name of the part
     */
    private void extractInputFileToModifyDestination(ObjectModificationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder.setInputFileToModifyDestination(
            name.substring(0, name.length() - ObjectManagerRequestConsts.REQUEST_FILE_DEST_SUFFIX.length()),
            part.getValue());
    }


    /**
     * Extracts info about the sequence of the input data file to modify in the object from the request and sets in into
     * the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param part
     *            part of the request
     * @param name
     *            name of the part
     */
    private void extractInputFileToModifySequence(ObjectModificationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder.setInputFileToModifySequence(
            name.substring(0, name.length() - ObjectManagerRequestConsts.REQUEST_FILE_SEQ_SUFFIX.length()),
            part.getValue());
    }


    /**
     * Extracts info about the metadata file of the input data file to modify to the object from the request and sets in
     * into the builder.
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
    private void extractInputFileToModifyMetadataToAdd(ObjectModificationRequestBuilder requestBuilder, String field,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        com.sun.jersey.core.header.ContentDisposition disp = part.getContentDisposition();
        String key = name.substring(0,
            name.length() - ObjectManagerRequestConsts.REQUEST_MOD_FILE_METADATA_ADD_SUFFIX.length());
        if (disp.getFileName() == null) {
            logger.debug("Field " + field + " carries an URL to file.");
            requestBuilder.addInputFileToModifyMetadataFileToAdd(key, part.getValue());
        } else {
            logger.debug("Field " + field + " carries a file " + disp.getFileName());
            String filename = disp.getFileName().replace(' ', '_');
            ObjectManagerRequestHelper.savePartAsFile(part, root + "/" + key, filename);
            requestBuilder.addInputFileToModifyMetadataFileToAdd(key,
                ObjectManagerRequestHelper.makeUri(root + "/" + key, filename));
        }
    }


    /**
     * Extracts info about the metadata file of the input data file to modify to the object from the request and sets in
     * into the builder.
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
    private void extractInputFileToModifyMetadataToModify(ObjectModificationRequestBuilder requestBuilder,
            String field, com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        com.sun.jersey.core.header.ContentDisposition disp = part.getContentDisposition();
        String key = name.substring(0,
            name.length() - ObjectManagerRequestConsts.REQUEST_MOD_FILE_METADATA_MOD_SUFFIX.length());
        if (disp.getFileName() == null) {
            logger.debug("Field " + field + " carries an URL to file.");
            requestBuilder.addInputFileToModifyMetadataFileToModify(key, part.getValue());
        } else {
            logger.debug("Field " + field + " carries a file " + disp.getFileName());
            String filename = disp.getFileName().replace(' ', '_');
            ObjectManagerRequestHelper.savePartAsFile(part, root + "/" + key, filename);
            requestBuilder.addInputFileToModifyMetadataFileToModify(key,
                ObjectManagerRequestHelper.makeUri(root + "/" + key, filename));
        }
    }


    /**
     * Extracts info about the metadata file of the input data file to modify to the object from the request and sets in
     * into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param part
     *            part of the request
     * @param name
     *            name of the part
     */
    private void extractInputFileToModifyMetadataToRemove(ObjectModificationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder
                .addInputFileToModifyMetadataFileToRemove(
                    name.substring(0,
                        name.length() - ObjectManagerRequestConsts.REQUEST_MOD_FILE_METADATA_DEL_SUFFIX.length()),
                    part.getValue());
    }


    /**
     * Extracts info about the metadata file to add the digital object from the request and sets in into the builder.
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
    private void extractObjectMetadataToAdd(ObjectModificationRequestBuilder requestBuilder, String field,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        com.sun.jersey.core.header.ContentDisposition disp = part.getContentDisposition();
        if (disp.getFileName() == null) {
            logger.debug("Field " + field + " carries an URL to file.");
            requestBuilder.addObjectMetadataToAdd(part.getValue());
        } else {
            logger.debug("Field " + field + " carries a file " + disp.getFileName());
            String filename = disp.getFileName().replace(' ', '_');
            ObjectManagerRequestHelper.savePartAsFile(part, root, filename);
            requestBuilder.addObjectMetadataToAdd(ObjectManagerRequestHelper.makeUri(root, filename));
        }
    }


    /**
     * Extracts info about the metadata file to add the digital object from the request and sets in into the builder.
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
    private void extractObjectMetadataToModify(ObjectModificationRequestBuilder requestBuilder, String field,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        com.sun.jersey.core.header.ContentDisposition disp = part.getContentDisposition();
        if (disp.getFileName() == null) {
            logger.debug("Field " + field + " carries an URL to file.");
            requestBuilder.addObjectMetadataToModify(part.getValue());
        } else {
            logger.debug("Field " + field + " carries a file " + disp.getFileName());
            String filename = disp.getFileName().replace(' ', '_');
            ObjectManagerRequestHelper.savePartAsFile(part, root, filename);
            requestBuilder.addObjectMetadataToModify(ObjectManagerRequestHelper.makeUri(root, filename));
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
    private void extractDerivativeIdentifierToAdd(ObjectModificationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder.setMigratedToToAddIdentifier(
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
    private void extractDerivativeIdentifierResolverToAdd(ObjectModificationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder.setMigratedToToAddIdentifierResolver(name.substring(0, name.length()
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
    private void extractDerivativeTypeToAdd(ObjectModificationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder.setMigratedToToAddMigrationType(
            name.substring(0, name.length() - ObjectManagerRequestConsts.REQUEST_DERIVATIVE_TYPE_SUFFIX.length()),
            part.getValue());
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
    private void extractDerivativeIdentifierToModify(ObjectModificationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder.setMigratedToToModifyIdentifier(
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
    private void extractDerivativeIdentifierResolverToModify(ObjectModificationRequestBuilder requestBuilder,
            com.sun.jersey.multipart.FormDataBodyPart part, String name) {
        requestBuilder.setMigratedToToModifyIdentifierResolver(name.substring(0, name.length()
                - ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_RESOLVER_SUFFIX.length()), part.getValue());
    }


    /**
     * Parses the request encoded by application/x-www-form-urlencoded.
     * 
     * @param identifier
     *            identifier of the object
     * @param params
     *            request
     * @return internal representation of the request
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    public ObjectModificationRequest parse(String identifier, MultivaluedMap<String, String> params)
            throws IncompleteDataException, InvalidDataException {
        ObjectModificationRequestBuilder requestBuilder = new ObjectModificationRequestBuilder(identifier);
        for (String field : params.keySet()) {
            List<String> values = params.get(field);
            for (String value : values) {
                if (field.startsWith(ObjectManagerRequestConsts.REQUEST_ADD_FILE_PREFIX)) {
                    if (field.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_SRC_SUFFIX)) {
                        extractInputFileToAddSource(requestBuilder, field, value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_DEST_SUFFIX)) {
                        extractInputFileToAddDestination(requestBuilder, field, value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_METADATA_SUFFIX)) {
                        extractInputFileToAddMetadata(requestBuilder, field, value);
                    }
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_MOD_FILE_PREFIX)) {
                    if (field.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_SRC_SUFFIX)) {
                        extractInputFileToModifySource(requestBuilder, field, value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_DEST_SUFFIX)) {
                        extractInputFileToModifyDestination(requestBuilder, field, value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_MOD_FILE_METADATA_ADD_SUFFIX)) {
                        extractInputFileToModifyMetadataToAdd(requestBuilder, field, value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_MOD_FILE_METADATA_MOD_SUFFIX)) {
                        extractInputFileToModifyMetadataToModify(requestBuilder, field, value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_MOD_FILE_METADATA_DEL_SUFFIX)) {
                        extractInputFileToModifyMetadataToRemove(requestBuilder, field, value);
                    }
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_DEL_FILE_PREFIX)) {
                    if (field.endsWith(ObjectManagerRequestConsts.REQUEST_FILE_DEST_SUFFIX)) {
                        requestBuilder.addInputFileToRemoveDestination(value);
                    }
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_ADD_METADATA_PREFIX)) {
                    requestBuilder.addObjectMetadataToAdd(value);
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_MOD_METADATA_PREFIX)) {
                    requestBuilder.addObjectMetadataToModify(value);
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_DEL_METADATA_PREFIX)) {
                    requestBuilder.addObjectMetadataToRemove(value);
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_PREFIX)) {
                    if (field.endsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_ID_SUFFIX)) {
                        requestBuilder.setMigratedFromIdentifier(value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_ID_RESOLVER_SUFFIX)) {
                        requestBuilder.setMigratedFromIdentifierResolver(value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_ORIGIN_TYPE_SUFFIX)) {
                        requestBuilder.setMigratedFromMigrationType(value);
                    }
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_ADD_DERIVATIVE_PREFIX)) {
                    if (field.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_SUFFIX)) {
                        extractDerivativeIdentifierToAdd(requestBuilder, field, value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_RESOLVER_SUFFIX)) {
                        extractDerivativeIdentifierResolverToAdd(requestBuilder, field, value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_TYPE_SUFFIX)) {
                        extractDerivativeTypeToAdd(requestBuilder, field, value);
                    }
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_MOD_DERIVATIVE_PREFIX)) {
                    if (field.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_SUFFIX)) {
                        extractDerivativeIdentifierToModify(requestBuilder, field, value);
                    } else if (field.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_RESOLVER_SUFFIX)) {
                        extractDerivativeIdentifierResolverToModify(requestBuilder, field, value);
                    }
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_DEL_DERIVATIVE_PREFIX)) {
                    if (field.endsWith(ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_SUFFIX)) {
                        requestBuilder.addMigratedToToRemoveIdentifier(value);
                    }
                } else if (field.startsWith(ObjectManagerRequestConsts.REQUEST_MAIN_FILE_PREFIX)) {
                    requestBuilder.setMainFile(value);
                }
            }
        }
        return requestBuilder.build();
    }


    /**
     * Extracts info about the source of the input data file to add to the object from the request and sets in into the
     * builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param value
     *            value of the field
     */
    private void extractInputFileToAddSource(ObjectModificationRequestBuilder requestBuilder, String field, String value) {
        requestBuilder.setInputFileToAddSource(
            field.substring(0, field.length() - ObjectManagerRequestConsts.REQUEST_FILE_SRC_SUFFIX.length()), value);
    }


    /**
     * Extracts info about the destination of the input data file to add to the object from the request and sets in into
     * the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param value
     *            value of the field
     */
    private void extractInputFileToAddDestination(ObjectModificationRequestBuilder requestBuilder, String field,
            String value) {
        requestBuilder.setInputFileToAddDestination(
            field.substring(0, field.length() - ObjectManagerRequestConsts.REQUEST_FILE_DEST_SUFFIX.length()), value);
    }


    /**
     * Extracts info about the metadata file of the input data file to add to the object from the request and sets in
     * into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param value
     *            value of the field
     */
    private void extractInputFileToAddMetadata(ObjectModificationRequestBuilder requestBuilder, String field,
            String value) {
        requestBuilder.addInputFileToAddMetadataFile(
            field.substring(0, field.length() - ObjectManagerRequestConsts.REQUEST_FILE_METADATA_SUFFIX.length()),
            value);
    }


    /**
     * Extracts info about the source of the input data file to modify in the object from the request and sets in into
     * the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param value
     *            value of the field
     */
    private void extractInputFileToModifySource(ObjectModificationRequestBuilder requestBuilder, String field,
            String value) {
        requestBuilder.setInputFileToModifySource(
            field.substring(0, field.length() - ObjectManagerRequestConsts.REQUEST_FILE_SRC_SUFFIX.length()), value);
    }


    /**
     * Extracts info about the destination of the input data file to modify in the object from the request and sets in
     * into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param value
     *            value of the field
     */
    private void extractInputFileToModifyDestination(ObjectModificationRequestBuilder requestBuilder, String field,
            String value) {
        requestBuilder.setInputFileToModifyDestination(
            field.substring(0, field.length() - ObjectManagerRequestConsts.REQUEST_FILE_DEST_SUFFIX.length()), value);
    }


    /**
     * Extracts info about the metadata file of the input data file to modify to the object from the request and sets in
     * into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param value
     *            value of the field
     */
    private void extractInputFileToModifyMetadataToAdd(ObjectModificationRequestBuilder requestBuilder, String field,
            String value) {
        requestBuilder.addInputFileToModifyMetadataFileToAdd(
            field.substring(0,
                field.length() - ObjectManagerRequestConsts.REQUEST_MOD_FILE_METADATA_ADD_SUFFIX.length()), value);
    }


    /**
     * Extracts info about the metadata file of the input data file to modify to the object from the request and sets in
     * into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param value
     *            value of the field
     */
    private void extractInputFileToModifyMetadataToModify(ObjectModificationRequestBuilder requestBuilder,
            String field, String value) {
        requestBuilder.addInputFileToModifyMetadataFileToModify(
            field.substring(0,
                field.length() - ObjectManagerRequestConsts.REQUEST_MOD_FILE_METADATA_MOD_SUFFIX.length()), value);
    }


    /**
     * Extracts info about the metadata file of the input data file to modify to the object from the request and sets in
     * into the builder.
     * 
     * @param requestBuilder
     *            builder
     * @param field
     *            name of the field of the request
     * @param value
     *            value of the field
     */
    private void extractInputFileToModifyMetadataToRemove(ObjectModificationRequestBuilder requestBuilder,
            String field, String value) {
        requestBuilder.addInputFileToModifyMetadataFileToRemove(
            field.substring(0,
                field.length() - ObjectManagerRequestConsts.REQUEST_MOD_FILE_METADATA_DEL_SUFFIX.length()), value);
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
    private void extractDerivativeIdentifierToAdd(ObjectModificationRequestBuilder requestBuilder, String field,
            String value) {
        requestBuilder.setMigratedToToAddIdentifier(
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
    private void extractDerivativeIdentifierResolverToAdd(ObjectModificationRequestBuilder requestBuilder,
            String field, String value) {
        requestBuilder.setMigratedToToAddIdentifierResolver(
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
    private void extractDerivativeTypeToAdd(ObjectModificationRequestBuilder requestBuilder, String field, String value) {
        requestBuilder.setMigratedToToAddMigrationType(
            field.substring(0, field.length() - ObjectManagerRequestConsts.REQUEST_DERIVATIVE_TYPE_SUFFIX.length()),
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
    private void extractDerivativeIdentifierToModify(ObjectModificationRequestBuilder requestBuilder, String field,
            String value) {
        requestBuilder.setMigratedToToModifyIdentifier(
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
    private void extractDerivativeIdentifierResolverToModify(ObjectModificationRequestBuilder requestBuilder,
            String field, String value) {
        requestBuilder.setMigratedToToModifyIdentifierResolver(
            field.substring(0,
                field.length() - ObjectManagerRequestConsts.REQUEST_DERIVATIVE_ID_RESOLVER_SUFFIX.length()), value);
    }


    /**
     * Parses the request provided as ZIP.
     * 
     * @param identifier
     *            identifier of the object
     * @param zis
     *            input stream of the zip request
     * @return internal representation of the request
     * @throws IncompleteDataException
     *             when some data are missing
     * @throws InvalidDataException
     *             when some data are invalid
     */
    public ObjectModificationRequest parse(String identifier, ZipInputStream zis)
            throws IncompleteDataException, InvalidDataException {
        ObjectModificationRequestBuilder requestBuilder = new ObjectModificationRequestBuilder(identifier);
        requestBuilder.setDeleteAllContent(true);
        if (!new File(root).mkdir()) {
            logger.error("Root folder " + root + " creation failed.");
            throw new WrdzRuntimeException("Root folder " + root + " creation failed.");
        }
        ZipEntry entry = null;
        try {
            while ((entry = zis.getNextEntry()) != null) {
                logger.debug("unzipping: " + entry.getName() + " - is directory: " + entry.isDirectory());
                if (entry.isDirectory()) {
                    if (!new File(root + "/" + entry.getName()).mkdir()) {
                        logger.error("folder " + entry.getName() + " creation failed.");
                        throw new WrdzRuntimeException("Folder " + entry.getName() + " creation failed.");
                    }
                } else {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(root + "/"
                            + entry.getName()), 1024);
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = zis.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }
                    bos.flush();
                    bos.close();
                    if (entry.getName().toLowerCase().startsWith("metadata/")) {
                        String metadataName = entry.getName().substring(new String("metadata/").length());
                        if (metadataName.contains("/")) {
                            requestBuilder.addInputFileToAddMetadataFile(
                                metadataName.substring(0, metadataName.lastIndexOf('/')),
                                ObjectManagerRequestHelper.makeUri(root, entry.getName()));
                        } else {
                            requestBuilder.addObjectMetadataToAdd(ObjectManagerRequestHelper.makeUri(root,
                                entry.getName()));
                        }
                    } else {
                        String contentName = entry.getName();
                        if (contentName.toLowerCase().startsWith("content/")) {
                            contentName = contentName.substring(new String("content/").length());
                        }
                        requestBuilder.setInputFileToAddSource(contentName,
                            ObjectManagerRequestHelper.makeUri(root, entry.getName()));
                        requestBuilder.setInputFileToAddDestination(contentName, contentName);
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
        return requestBuilder.build();
    }

}
