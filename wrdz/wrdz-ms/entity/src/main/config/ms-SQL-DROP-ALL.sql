
DROP SEQUENCE darceo.ms_mfs_id_seq RESTRICT;

ALTER TABLE darceo.ms_metadata_format_stats DROP CONSTRAINT ms_metadata_format_stats_pkey;
DROP TABLE darceo.ms_metadata_format_stats;

DROP SEQUENCE darceo.ms_dffs_id_seq RESTRICT;

ALTER TABLE darceo.ms_data_file_format_stats DROP CONSTRAINT ms_data_file_format_stats_pkey;
DROP TABLE darceo.ms_data_file_format_stats;

DROP SEQUENCE darceo.ms_bs_id_seq RESTRICT;

ALTER TABLE darceo.ms_basic_stats DROP CONSTRAINT ms_basic_stats_pkey;
DROP TABLE darceo.ms_basic_stats;

DROP SEQUENCE darceo.ms_ne_id_seq RESTRICT;

ALTER TABLE darceo.ms_notify_emails DROP CONSTRAINT ms_notify_emails_pkey;
DROP TABLE darceo.ms_notify_emails;

DROP SEQUENCE darceo.ms_im_id_seq RESTRICT;

ALTER TABLE darceo.ms_internal_messages DROP CONSTRAINT ms_internal_messages_pkey;
DROP TABLE darceo.ms_internal_messages;
