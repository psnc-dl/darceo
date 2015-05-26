
-- authentication.RemoteRepository

CREATE SEQUENCE darceo.zmd_rr_id_seq INCREMENT BY 1 START WITH 1000;
  
CREATE TABLE darceo.zmd_remote_repositories
(
  rr_id bigint NOT NULL,
  rr_description VARCHAR(200),
  rr_host VARCHAR(50) NOT NULL,
  rr_port integer,
  rr_protocol VARCHAR(5) NOT NULL,
  CONSTRAINT zmd_remote_repositories_pkey PRIMARY KEY (rr_id),
  CONSTRAINT unq_zmd_remote_repositories_rr_port_rr_host UNIQUE (rr_port, rr_host)
);


-- authentication.RemoteRepositoryAuthentication

CREATE SEQUENCE darceo.zmd_rra_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmd_remote_repository_auth
(
  rra_id bigint NOT NULL,
  rra_passphrase VARCHAR(50),
  rra_password VARCHAR(50),
  rra_priv_key bytea,
  rra_pub_key bytea,
  rra_default boolean NOT NULL,
  rra_username VARCHAR(50),
  rra_rr_id bigint NOT NULL,
  CONSTRAINT zmd_remote_repository_auth_pkey PRIMARY KEY (rra_id),
  CONSTRAINT fk_zmd_remote_repository_auth_rra_rr_id FOREIGN KEY (rra_rr_id)
      REFERENCES darceo.zmd_remote_repositories (rr_id),
  CONSTRAINT unq_zmd_remote_repository_auth_rra_rr_id_rra_username UNIQUE (rra_rr_id, rra_username)
);

CREATE INDEX zmd_remote_repository_auth_rra_rr_id_idx ON darceo.zmd_remote_repository_auth (rra_rr_id);


-- object.content.DataFileFormat
-- object.content.DataFileFormatName

CREATE SEQUENCE darceo.zmd_dff_id_seq INCREMENT BY 1 START WITH 1000;
CREATE SEQUENCE darceo.zmd_dffn_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmd_data_file_formats
(
  dff_id bigint NOT NULL,
  dff_mimetype VARCHAR(50) NOT NULL,
  dff_puid VARCHAR(50),
  dff_type VARCHAR(10) NOT NULL,
  dff_version VARCHAR(50),
  CONSTRAINT zmd_data_file_formats_pkey PRIMARY KEY (dff_id)
);

CREATE TABLE darceo.zmd_data_file_format_names
(
  dffn_id bigint NOT NULL,
  dffn_name character varying(254) NOT NULL,
  CONSTRAINT zmd_data_file_format_names_pkey PRIMARY KEY (dffn_id),
  CONSTRAINT zmd_data_file_format_names_dffn_name_key UNIQUE (dffn_name)
);

CREATE TABLE darceo.zmd_data_file_formats_data_file_format_names
(
  dff_id bigint NOT NULL,
  dffn_id bigint NOT NULL,
  CONSTRAINT zmd_data_file_formats_data_file_format_names_pkey PRIMARY KEY (dff_id, dffn_id),
  CONSTRAINT fk_zmd_data_file_formats_data_file_format_names_dff_id FOREIGN KEY (dff_id)
      REFERENCES darceo.zmd_data_file_formats (dff_id),
  CONSTRAINT fk_zmd_data_file_formats_data_file_format_names_dffn_id FOREIGN KEY (dffn_id)
      REFERENCES darceo.zmd_data_file_format_names (dffn_id)
);

INSERT INTO darceo.zmd_data_file_formats (dff_id, dff_mimetype, dff_type) VALUES (1, 'unrecognized', 'UNKNOWN');


-- object.validation.ValidationWarning

CREATE SEQUENCE darceo.zmd_vw_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmd_validation_warning
(
  vw_id bigint NOT NULL,
  vw_warning character varying(512) NOT NULL,
  CONSTRAINT zmd_validation_warning_pkey PRIMARY KEY (vw_id),
  CONSTRAINT zmd_validation_warning_vw_warning_key UNIQUE (vw_warning)
);

-- object.validation.DataFileValidation

CREATE SEQUENCE darceo.zmd_dfv_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmd_data_file_validation
(
  dfv_id bigint NOT NULL,
  dfn_status character varying(11) NOT NULL,
  CONSTRAINT zmd_data_file_validation_pkey PRIMARY KEY (dfv_id)
);

CREATE TABLE darceo.zmd_data_file_validation_validation_warning
(
  dfv_id bigint NOT NULL,
  vw_id bigint NOT NULL,
  CONSTRAINT zmd_data_file_validation_validation_warning_pkey PRIMARY KEY (dfv_id, vw_id),
  CONSTRAINT fk_zmd_data_file_validation_validation_warning_dfv_id FOREIGN KEY (dfv_id)
      REFERENCES darceo.zmd_data_file_validation (dfv_id),
  CONSTRAINT fk_zmd_data_file_validation_validation_warning_vw_id FOREIGN KEY (vw_id)
      REFERENCES darceo.zmd_validation_warning (vw_id)
);


-- object.content.DataFile

CREATE SEQUENCE darceo.zmd_df_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmd_data_files
(
  df_id bigint NOT NULL,
  df_filename VARCHAR(255) NOT NULL,
  df_repo_path VARCHAR(255) NOT NULL,
  df_obj_path VARCHAR(255) NOT NULL,
  df_format_id bigint NOT NULL,
  df_dfv_id bigint,
  df_size bigint NOT NULL,
  CONSTRAINT zmd_data_files_pkey PRIMARY KEY (df_id),
  CONSTRAINT fk_zmd_data_files_df_format_id FOREIGN KEY (df_format_id)
      REFERENCES darceo.zmd_data_file_formats (dff_id),
  CONSTRAINT fk_zmd_data_files_df_dfv_id FOREIGN KEY (df_dfv_id)
      REFERENCES darceo.zmd_data_file_validation (dfv_id)
);

CREATE INDEX zmd_data_files_df_format_id_idx ON darceo.zmd_data_files (df_format_id);
CREATE INDEX zmd_data_files_df_dfv_id_idx ON darceo.zmd_data_files (df_dfv_id);

-- object.metadata.MetadataNamespace

CREATE SEQUENCE darceo.zmd_mn_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmd_metadata_namespaces
(
  mn_id bigint NOT NULL,
  mn_xmlns VARCHAR(255) NOT NULL,
  mn_schema_location VARCHAR(255),
  mn_type VARCHAR(15) NOT NULL,
  CONSTRAINT zmd_metadata_namespaces_pkey PRIMARY KEY (mn_id)
);


-- object.metadata.MetadataFile

CREATE SEQUENCE darceo.zmd_mf_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmd_metadata_files
(
  mf_id bigint NOT NULL,
  mf_filename VARCHAR(255) NOT NULL,
  mf_repo_path VARCHAR(255) NOT NULL,
  mf_obj_path VARCHAR(255) NOT NULL,
  mf_size bigint NOT NULL,
  mf_type VARCHAR(20),
  mf_main_namespace bigint,
  mf_data_file_id bigint, 
  CONSTRAINT zmd_metadata_files_pkey PRIMARY KEY (mf_id),
  CONSTRAINT fk_zmd_metadata_files_mf_main_namespace FOREIGN KEY (mf_main_namespace)
      REFERENCES darceo.zmd_metadata_namespaces (mn_id),
  CONSTRAINT fk_zmd_metadata_files_mf_data_file_id FOREIGN KEY (mf_data_file_id)
      REFERENCES darceo.zmd_data_files (df_id)
);

CREATE INDEX zmd_metadata_files_mf_main_namespace_idx ON darceo.zmd_metadata_files (mf_main_namespace);
CREATE INDEX zmd_metadata_files_mf_data_file_id ON darceo.zmd_metadata_files (mf_data_file_id);


CREATE TABLE darceo.zmd_metadata_files_metadata_namespaces
(
  mf_id bigint NOT NULL,
  mn_id bigint NOT NULL,
  CONSTRAINT zmd_metadata_files_metadata_namespaces_pkey PRIMARY KEY (mf_id, mn_id),
  CONSTRAINT fk_zmd_metadata_files_metadata_namespaces_mf_id FOREIGN KEY (mf_id)
      REFERENCES darceo.zmd_metadata_files (mf_id),
  CONSTRAINT fk_zmd_metadata_files_metadata_namespaces_mn_id FOREIGN KEY (mn_id)
      REFERENCES darceo.zmd_metadata_namespaces (mn_id)
);


-- object.hash.FileHash

CREATE SEQUENCE darceo.zmd_fh_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmd_file_hashes
(
	fh_id bigint NOT NULL,
	fh_datafile_id bigint,
	fh_metadatafile_id bigint,
	fh_hash_type VARCHAR(10) NOT NULL,
	fh_hash_value VARCHAR(128) NOT NULL,
	fh_type VARCHAR(12) NOT NULL,
	CONSTRAINT zmd_file_hashes_pkey PRIMARY KEY (fh_id),
	CONSTRAINT fk_zmd_file_hashes_fh_datafile_id FOREIGN KEY (fh_datafile_id) 
		REFERENCES darceo.zmd_data_files (df_id),
	CONSTRAINT fk_zmd_file_hashes_fh_metadatafile_id FOREIGN KEY (fh_metadatafile_id) 
		REFERENCES darceo.zmd_metadata_files (mf_id),
	CONSTRAINT unq_zmd_file_hashes_fh_datafile_id_fh_datafile_id_fh_hash_type UNIQUE (fh_datafile_id, fh_metadatafile_id, fh_hash_type)
);

CREATE INDEX zmd_file_hashes_fh_metadatafile_id_idx ON darceo.zmd_file_hashes (fh_metadatafile_id); 


-- object.content.ContentVersion

CREATE SEQUENCE darceo.zmd_cv_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmd_content_versions
(
  cv_id bigint NOT NULL,
  cv_created_on timestamp NOT NULL,
  cv_version integer NOT NULL,
  cv_extracted_metadata_id bigint,
  cv_main_file_id bigint,
  cv_object_id bigint NOT NULL,
  CONSTRAINT zmd_content_versions_pkey PRIMARY KEY (cv_id),
  CONSTRAINT fk_zmd_content_versions_cv_extracted_metadata_id FOREIGN KEY (cv_extracted_metadata_id)
      REFERENCES darceo.zmd_metadata_files (mf_id),
  CONSTRAINT fk_zmd_content_versions_cv_main_file_id FOREIGN KEY (cv_main_file_id)
      REFERENCES darceo.zmd_data_files (df_id),
  CONSTRAINT zmd_content_versions_cv_extracted_metadata_id_key UNIQUE (cv_extracted_metadata_id),
  CONSTRAINT unq_zmd_content_versions_cv_version_cv_object_id UNIQUE (cv_version, cv_object_id)
);

CREATE INDEX zmd_content_versions_cv_main_file_id_idx ON darceo.zmd_content_versions (cv_main_file_id);


CREATE TABLE darceo.zmd_content_versions_provided_metadata_files
(
  cv_id bigint NOT NULL,
  mf_id bigint NOT NULL,
  CONSTRAINT zmd_content_versions_metadata_files_pkey PRIMARY KEY (cv_id, mf_id),
  CONSTRAINT fk_zmd_content_versions_provided_metadata_files_cv_id FOREIGN KEY (cv_id)
      REFERENCES darceo.zmd_content_versions (cv_id),
  CONSTRAINT fk_zmd_content_versions_provided_metadata_files_mf_id FOREIGN KEY (mf_id)
      REFERENCES darceo.zmd_metadata_files (mf_id)
);

-- object.content.DataFileVersion

CREATE SEQUENCE darceo.zmd_dfver_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmd_data_file_versions
(
  dfv_id bigint NOT NULL,
  dfv_content_version_id bigint NOT NULL,
  dfv_data_file_id bigint NOT NULL,
  dfv_sequence int,
  CONSTRAINT zmd_data_file_versions_pkey PRIMARY KEY (dfv_id),
  CONSTRAINT fk_zmd_data_file_versions_dfv_content_version_id FOREIGN KEY (dfv_content_version_id)
      REFERENCES darceo.zmd_content_versions (cv_id),
  CONSTRAINT fk_zmd_data_file_versions_dfv_data_file_id FOREIGN KEY (dfv_data_file_id)
      REFERENCES darceo.zmd_data_files (df_id)
);

CREATE INDEX zmd_data_file_versions_dfv_content_version_id_idx ON darceo.zmd_data_file_versions (dfv_content_version_id);
CREATE INDEX zmd_data_file_versions_dfv_data_file_id_idx ON darceo.zmd_data_file_versions (dfv_data_file_id);

CREATE TABLE darceo.zmd_data_file_versions_metadata_files
(
  dfv_id bigint NOT NULL,
  mf_id bigint NOT NULL,
  CONSTRAINT zmd_data_file_versions_metadata_files_pkey PRIMARY KEY (dfv_id, mf_id),
  CONSTRAINT fk_zmd_data_file_versions_metadata_files_dfv_id FOREIGN KEY (dfv_id)
      REFERENCES darceo.zmd_data_file_versions (dfv_id),
  CONSTRAINT fk_zmd_data_file_versions_metadata_files_mf_id FOREIGN KEY (mf_id)
      REFERENCES darceo.zmd_metadata_files (mf_id)
);


-- object.DigitalObject

CREATE SEQUENCE darceo.zmd_do_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmd_digital_objects
(
  do_id bigint NOT NULL,
  do_type VARCHAR(31),
  do_current_version bigint,
  do_owner_id bigint,
  do_identifier_id bigint,
  do_name VARCHAR(255),
  CONSTRAINT zmd_digital_objects_pkey PRIMARY KEY (do_id),
  CONSTRAINT fk_zmd_digital_objects_do_current_version FOREIGN KEY (do_current_version)
      REFERENCES darceo.zmd_content_versions (cv_id)
);

CREATE INDEX zmd_digital_objects_do_current_version_idx ON darceo.zmd_digital_objects (do_current_version);
CREATE INDEX zmd_digital_objects_do_owner_id_idx ON darceo.zmd_digital_objects (do_owner_id);

ALTER TABLE darceo.zmd_content_versions 
	ADD CONSTRAINT fk_zmd_content_versions_cv_object_id FOREIGN KEY (cv_object_id)
	REFERENCES darceo.zmd_digital_objects (do_id);

CREATE INDEX zmd_content_versions_cv_object_id_idx ON darceo.zmd_content_versions (cv_object_id);


-- object.migration.operation.Operation

CREATE SEQUENCE darceo.zmd_op_id_seq INCREMENT BY 1 START WITH 1000;	
	
CREATE TABLE darceo.zmd_metadata_operations
(
  op_id bigint NOT NULL,
  op_object bigint NOT NULL,
  op_name VARCHAR(15) NOT NULL,
  op_date timestamp NOT NULL,
  op_contents text,
  op_md_type VARCHAR(10) NOT NULL,
  CONSTRAINT zmd_metadata_operations_pkey PRIMARY KEY (op_id),
  CONSTRAINT fk_zmd_metadata_operations_op_object FOREIGN KEY (op_object)
  	  REFERENCES darceo.zmd_digital_objects (do_id)
) TABLESPACE metadata_store;

CREATE INDEX zmd_metadata_operations_op_object_idx ON darceo.zmd_metadata_operations (op_object);

CREATE TABLE darceo.zmd_metadata_operations_metadata_files
(
  momf_op_id bigint NOT NULL,
  momf_mf_id bigint NOT NULL,
  CONSTRAINT zmd_metadata_operations_metadata_files_pkey PRIMARY KEY (momf_op_id,momf_mf_id),
  CONSTRAINT fk_zmd_metadata_operations_metadata_files_momf_op_id FOREIGN KEY (momf_op_id)
  	  REFERENCES darceo.zmd_metadata_operations (op_id),
  CONSTRAINT fk_zmd_metadata_operations_metadata_files_momf_mf_id FOREIGN KEY (momf_mf_id)
  	  REFERENCES darceo.zmd_metadata_files (mf_id)
);
	
-- object.migration.Migration

CREATE SEQUENCE darceo.zmd_m_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmd_migrations
(
  m_id bigint NOT NULL,
  m_type VARCHAR(31) NOT NULL,
  m_date timestamp,
  m_info VARCHAR(512),
  m_result_id VARCHAR(255),
  m_source_id VARCHAR(255),
  m_res_id_resolver VARCHAR(255),
  m_src_id_resolver VARCHAR(255),
  m_source_object_id bigint,
  m_result_object_id bigint,
  CONSTRAINT zmd_migrations_pkey PRIMARY KEY (m_id),
  CONSTRAINT fk_zmd_migrations_m_result_object_id FOREIGN KEY (m_result_object_id)
      REFERENCES darceo.zmd_digital_objects (do_id),
  CONSTRAINT fk_zmd_migrations_m_source_object_id FOREIGN KEY (m_source_object_id)
      REFERENCES darceo.zmd_digital_objects (do_id),
  CONSTRAINT zmd_migrations_m_result_object_id_key UNIQUE (m_result_object_id)
);

CREATE INDEX zmd_migrations_m_source_object_id_idx ON darceo.zmd_migrations (m_source_object_id);


-- object.Identifier

CREATE SEQUENCE darceo.zmd_oi_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmd_identifiers
(
  oi_id bigint NOT NULL,
  oi_identifier VARCHAR(255) NOT NULL,
  oi_active boolean,
  oi_type VARCHAR(10) NOT NULL,
  oi_object_id bigint NOT NULL,
  CONSTRAINT zmd_identifiers_pkey PRIMARY KEY (oi_id),
  CONSTRAINT fk_zmd_identifiers_oi_object_id FOREIGN KEY (oi_object_id)
      REFERENCES darceo.zmd_digital_objects (do_id),
  CONSTRAINT zmd_identifiers_oi_identifier_key UNIQUE (oi_identifier)
);

CREATE INDEX zmd_identifiers_oi_object_id_idx ON darceo.zmd_identifiers (oi_object_id);

ALTER TABLE darceo.zmd_digital_objects 
	ADD CONSTRAINT fk_zmd_digital_objects_do_identifier_id FOREIGN KEY (do_identifier_id)
	REFERENCES darceo.zmd_identifiers (oi_id);

CREATE INDEX zmd_digital_objects_do_identifier_id_idx ON darceo.zmd_digital_objects (do_identifier_id);


-- oai.pmh.ResumptionToken

CREATE TABLE darceo.zmd_resumption_tokens
(
	rt_id VARCHAR(36) NOT NULL,
	rt_from timestamp NOT NULL,
	rt_until timestamp NOT NULL,
	rt_md_prefix VARCHAR(10) NOT NULL,
	rt_set VARCHAR(31),
	rt_type VARCHAR(21) NOT NULL,
	rt_expires timestamp NOT NULL,
	rt_offset integer NOT NULL,
	CONSTRAINT zmd_resumption_tokens_pkey PRIMARY KEY (rt_id)
);
	
	