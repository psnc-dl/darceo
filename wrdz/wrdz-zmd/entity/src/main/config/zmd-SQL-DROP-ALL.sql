
-- oai.pmh.ResumptionToken
ALTER TABLE darceo.zmd_resumption_tokens DROP CONSTRAINT zmd_resumption_tokens_pkey;
DROP TABLE darceo.zmd_resumption_tokens;


-- object.Identifier

DROP SEQUENCE darceo.zmd_oi_id_seq RESTRICT;

--DROP INDEX zmd_digital_objects_do_identifier_id_idx;

ALTER TABLE darceo.zmd_digital_objects DROP CONSTRAINT fk_zmd_digital_objects_do_identifier_id;

--DROP INDEX zmd_identifiers_oi_object_id_idx;

ALTER TABLE darceo.zmd_identifiers DROP CONSTRAINT zmd_identifiers_oi_identifier_key;
ALTER TABLE darceo.zmd_identifiers DROP CONSTRAINT fk_zmd_identifiers_oi_object_id;
ALTER TABLE darceo.zmd_identifiers DROP CONSTRAINT zmd_identifiers_pkey;
DROP TABLE darceo.zmd_identifiers;


-- object.migration.Migration

DROP SEQUENCE darceo.zmd_m_id_seq RESTRICT;

--DROP INDEX zmd_migrations_m_source_object_id_idx;

ALTER TABLE darceo.zmd_migrations DROP CONSTRAINT zmd_migrations_m_result_object_id_key;
ALTER TABLE darceo.zmd_migrations DROP CONSTRAINT fk_zmd_migrations_m_source_object_id;
ALTER TABLE darceo.zmd_migrations DROP CONSTRAINT fk_zmd_migrations_m_result_object_id;
ALTER TABLE darceo.zmd_migrations DROP CONSTRAINT zmd_migrations_pkey;
DROP TABLE darceo.zmd_migrations;


-- object.migration.operation.Operation

DROP SEQUENCE darceo.zmd_op_id_seq RESTRICT;

ALTER TABLE darceo.zmd_metadata_operations_metadata_files DROP CONSTRAINT fk_zmd_metadata_operations_metadata_files_momf_mf_id;
ALTER TABLE darceo.zmd_metadata_operations_metadata_files DROP CONSTRAINT fk_zmd_metadata_operations_metadata_files_momf_op_id;
ALTER TABLE darceo.zmd_metadata_operations_metadata_files DROP CONSTRAINT zmd_metadata_operations_metadata_files_pkey;
DROP TABLE darceo.zmd_metadata_operations_metadata_files;

--DROP INDEX zmd_metadata_operations_op_object_idx;

ALTER TABLE darceo.zmd_metadata_operations DROP CONSTRAINT fk_zmd_metadata_operations_op_object;
ALTER TABLE darceo.zmd_metadata_operations DROP CONSTRAINT zmd_metadata_operations_pkey;
DROP TABLE darceo.zmd_metadata_operations;


-- object.DigitalObject

DROP SEQUENCE darceo.zmd_do_id_seq RESTRICT;

--DROP INDEX zmd_content_versions_cv_object_id_idx;

ALTER TABLE darceo.zmd_content_versions DROP CONSTRAINT fk_zmd_content_versions_cv_object_id;

--DROP INDEX zmd_digital_objects_do_owner_id_idx;
--DROP INDEX zmd_digital_objects_do_current_version_idx;

ALTER TABLE darceo.zmd_digital_objects DROP CONSTRAINT fk_zmd_digital_objects_do_current_version;
ALTER TABLE darceo.zmd_digital_objects DROP CONSTRAINT zmd_digital_objects_pkey;
DROP TABLE darceo.zmd_digital_objects;


-- object.content.DataFileVersion

DROP SEQUENCE darceo.zmd_dfver_id_seq RESTRICT;

ALTER TABLE darceo.zmd_data_file_versions_metadata_files DROP CONSTRAINT fk_zmd_data_file_versions_metadata_files_mf_id;
ALTER TABLE darceo.zmd_data_file_versions_metadata_files DROP CONSTRAINT fk_zmd_data_file_versions_metadata_files_dfv_id;
ALTER TABLE darceo.zmd_data_file_versions_metadata_files DROP CONSTRAINT zmd_data_file_versions_metadata_files_pkey;
DROP TABLE darceo.zmd_data_file_versions_metadata_files;

--DROP INDEX zmd_data_file_versions_dfv_data_file_id_idx;
--DROP INDEX zmd_data_file_versions_dfv_content_version_id_idx;

ALTER TABLE darceo.zmd_data_file_versions DROP CONSTRAINT fk_zmd_data_file_versions_dfv_data_file_id;
ALTER TABLE darceo.zmd_data_file_versions DROP CONSTRAINT fk_zmd_data_file_versions_dfv_content_version_id;
ALTER TABLE darceo.zmd_data_file_versions DROP CONSTRAINT zmd_data_file_versions_pkey;
DROP TABLE darceo.zmd_data_file_versions;


-- object.content.ContentVersion

DROP SEQUENCE darceo.zmd_cv_id_seq RESTRICT;

ALTER TABLE darceo.zmd_content_versions_provided_metadata_files DROP CONSTRAINT fk_zmd_content_versions_provided_metadata_files_mf_id;
ALTER TABLE darceo.zmd_content_versions_provided_metadata_files DROP CONSTRAINT fk_zmd_content_versions_provided_metadata_files_cv_id;
ALTER TABLE darceo.zmd_content_versions_provided_metadata_files DROP CONSTRAINT zmd_content_versions_metadata_files_pkey;
DROP TABLE darceo.zmd_content_versions_provided_metadata_files;

--DROP INDEX zmd_content_versions_cv_main_file_id_idx;

ALTER TABLE darceo.zmd_content_versions DROP CONSTRAINT unq_zmd_content_versions_cv_version_cv_object_id;
ALTER TABLE darceo.zmd_content_versions DROP CONSTRAINT zmd_content_versions_cv_extracted_metadata_id_key;
ALTER TABLE darceo.zmd_content_versions DROP CONSTRAINT fk_zmd_content_versions_cv_main_file_id;
ALTER TABLE darceo.zmd_content_versions DROP CONSTRAINT fk_zmd_content_versions_cv_extracted_metadata_id;
ALTER TABLE darceo.zmd_content_versions DROP CONSTRAINT zmd_content_versions_pkey;
DROP TABLE darceo.zmd_content_versions;


-- object.hash.FileHash

DROP SEQUENCE darceo.zmd_fh_id_seq RESTRICT;

--DROP INDEX zmd_file_hashes_fh_metadatafile_id_idx;

ALTER TABLE darceo.zmd_file_hashes DROP CONSTRAINT unq_zmd_file_hashes_fh_datafile_id_fh_datafile_id_fh_hash_type;
ALTER TABLE darceo.zmd_file_hashes DROP CONSTRAINT fk_zmd_file_hashes_fh_metadatafile_id;
ALTER TABLE darceo.zmd_file_hashes DROP CONSTRAINT fk_zmd_file_hashes_fh_datafile_id;
ALTER TABLE darceo.zmd_file_hashes DROP CONSTRAINT zmd_file_hashes_pkey;
DROP TABLE darceo.zmd_file_hashes;


-- object.metadata.MetadataFile

DROP SEQUENCE darceo.zmd_mf_id_seq RESTRICT;

ALTER TABLE darceo.zmd_metadata_files_metadata_namespaces DROP CONSTRAINT fk_zmd_metadata_files_metadata_namespaces_mn_id;
ALTER TABLE darceo.zmd_metadata_files_metadata_namespaces DROP CONSTRAINT fk_zmd_metadata_files_metadata_namespaces_mf_id;
ALTER TABLE darceo.zmd_metadata_files_metadata_namespaces DROP CONSTRAINT zmd_metadata_files_metadata_namespaces_pkey;
DROP TABLE darceo.zmd_metadata_files_metadata_namespaces;

--DROP INDEX zmd_metadata_files_mf_data_file_id;
--DROP INDEX zmd_metadata_files_mf_main_namespace_idx;

ALTER TABLE darceo.zmd_metadata_files DROP CONSTRAINT fk_zmd_metadata_files_mf_data_file_id;
ALTER TABLE darceo.zmd_metadata_files DROP CONSTRAINT fk_zmd_metadata_files_mf_main_namespace;
ALTER TABLE darceo.zmd_metadata_files DROP CONSTRAINT zmd_metadata_files_pkey;
DROP TABLE darceo.zmd_metadata_files;

-- object.metadata.MetadataNamespace

DROP SEQUENCE darceo.zmd_mn_id_seq RESTRICT;

ALTER TABLE darceo.zmd_metadata_namespaces DROP CONSTRAINT zmd_metadata_namespaces_pkey;
DROP TABLE darceo.zmd_metadata_namespaces;


-- object.content.DataFile

DROP SEQUENCE darceo.zmd_df_id_seq RESTRICT;

--DROP INDEX zmd_data_files_df_dfv_id_idx;
--DROP INDEX zmd_data_files_df_format_id_idx;

ALTER TABLE darceo.zmd_data_files DROP CONSTRAINT fk_zmd_data_files_df_dfv_id;
ALTER TABLE darceo.zmd_data_files DROP CONSTRAINT fk_zmd_data_files_df_format_id;
ALTER TABLE darceo.zmd_data_files DROP CONSTRAINT zmd_data_files_pkey;
DROP TABLE darceo.zmd_data_files;


-- object.validation.DataFileValidation

DROP SEQUENCE darceo.zmd_dfv_id_seq RESTRICT;

ALTER TABLE darceo.zmd_data_file_validation_validation_warning DROP CONSTRAINT fk_zmd_data_file_validation_validation_warning_vw_id;
ALTER TABLE darceo.zmd_data_file_validation_validation_warning DROP CONSTRAINT fk_zmd_data_file_validation_validation_warning_dfv_id;
ALTER TABLE darceo.zmd_data_file_validation_validation_warning DROP CONSTRAINT zmd_data_file_validation_validation_warning_pkey;
DROP TABLE darceo.zmd_data_file_validation_validation_warning;

ALTER TABLE darceo.zmd_data_file_validation DROP CONSTRAINT zmd_data_file_validation_pkey;
DROP TABLE darceo.zmd_data_file_validation;


-- object.validation.ValidationWarning

DROP SEQUENCE darceo.zmd_vw_id_seq RESTRICT;

ALTER TABLE darceo.zmd_validation_warning DROP CONSTRAINT zmd_validation_warning_vw_warning_key;
ALTER TABLE darceo.zmd_validation_warning DROP CONSTRAINT zmd_validation_warning_pkey;
DROP TABLE darceo.zmd_validation_warning;


-- object.content.DataFileFormat
-- object.content.DataFileFormatName

DROP SEQUENCE darceo.zmd_dff_id_seq RESTRICT;
DROP SEQUENCE darceo.zmd_dffn_id_seq RESTRICT;

ALTER TABLE darceo.zmd_data_file_formats_data_file_format_names DROP CONSTRAINT fk_zmd_data_file_formats_data_file_format_names_dffn_id;
ALTER TABLE darceo.zmd_data_file_formats_data_file_format_names DROP CONSTRAINT fk_zmd_data_file_formats_data_file_format_names_dff_id;
ALTER TABLE darceo.zmd_data_file_formats_data_file_format_names DROP CONSTRAINT zmd_data_file_formats_data_file_format_names_pkey;
DROP TABLE darceo.zmd_data_file_formats_data_file_format_names;

ALTER TABLE darceo.zmd_data_file_format_names DROP CONSTRAINT zmd_data_file_format_names_dffn_name_key;
ALTER TABLE darceo.zmd_data_file_format_names DROP CONSTRAINT zmd_data_file_format_names_pkey;
DROP TABLE darceo.zmd_data_file_format_names;

ALTER TABLE darceo.zmd_data_file_formats DROP CONSTRAINT zmd_data_file_formats_pkey;
DROP TABLE darceo.zmd_data_file_formats;


-- authentication.RemoteRepositoryAuthentication

DROP SEQUENCE darceo.zmd_rra_id_seq RESTRICT;

--DROP INDEX zmd_remote_repository_auth_rra_rr_id_idx;

ALTER TABLE darceo.zmd_remote_repository_auth DROP CONSTRAINT unq_zmd_remote_repository_auth_rra_rr_id_rra_username;
ALTER TABLE darceo.zmd_remote_repository_auth DROP CONSTRAINT fk_zmd_remote_repository_auth_rra_rr_id;
ALTER TABLE darceo.zmd_remote_repository_auth DROP CONSTRAINT zmd_remote_repository_auth_pkey;
DROP TABLE darceo.zmd_remote_repository_auth;


-- authentication.RemoteRepository

DROP SEQUENCE darceo.zmd_rr_id_seq RESTRICT;
  
ALTER TABLE darceo.zmd_remote_repositories DROP CONSTRAINT unq_zmd_remote_repositories_rr_port_rr_host;
ALTER TABLE darceo.zmd_remote_repositories DROP CONSTRAINT zmd_remote_repositories_pkey;
DROP TABLE darceo.zmd_remote_repositories;
