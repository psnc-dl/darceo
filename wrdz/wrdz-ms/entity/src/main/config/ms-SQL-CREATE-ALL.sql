CREATE SEQUENCE darceo.ms_im_id_seq INCREMENT BY 1 START WITH 1000;
  
CREATE TABLE darceo.ms_internal_messages
(
  im_id bigint NOT NULL,
  im_origin VARCHAR(255) NOT NULL,
  im_received_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  im_type VARCHAR(35) NOT NULL,
  im_data TEXT NOT NULL,
  CONSTRAINT ms_internal_messages_pkey PRIMARY KEY (im_id)
);


CREATE SEQUENCE darceo.ms_ne_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.ms_notify_emails
(
  ne_id bigint NOT NULL,
  ne_address VARCHAR(255) NOT NULL,
  CONSTRAINT ms_notify_emails_pkey PRIMARY KEY (ne_id)
);


CREATE SEQUENCE darceo.ms_bs_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.ms_basic_stats
(
  bs_id bigint NOT NULL,
  bs_objects bigint NOT NULL,
  bs_data_files bigint NOT NULL,
  bs_data_size bigint NOT NULL,
  bs_extracted_metadata_files bigint NOT NULL,
  bs_extracted_metadata_size bigint NOT NULL,
  bs_provided_metadata_files bigint NOT NULL,
  bs_provided_metadata_size bigint NOT NULL,
  bs_username VARCHAR(255),
  bs_computed_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT ms_basic_stats_pkey PRIMARY KEY (bs_id)
);


CREATE SEQUENCE darceo.ms_dffs_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.ms_data_file_format_stats
(
  dffs_id bigint NOT NULL,
  dffs_format_puid VARCHAR(255) NOT NULL,
  dffs_objects bigint NOT NULL,
  dffs_data_files bigint NOT NULL,
  dffs_data_size bigint NOT NULL,
  dffs_username VARCHAR(255),
  dffs_computed_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT ms_data_file_format_stats_pkey PRIMARY KEY (dffs_id)
);


CREATE SEQUENCE darceo.ms_mfs_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.ms_metadata_format_stats
(
  mfs_id bigint NOT NULL,
  mfs_format_name VARCHAR(255) NOT NULL,
  mfs_objects bigint NOT NULL,
  mfs_data_files bigint NOT NULL,
  mfs_computed_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT ms_metadata_format_stats_pkey PRIMARY KEY (mfs_id)
);
