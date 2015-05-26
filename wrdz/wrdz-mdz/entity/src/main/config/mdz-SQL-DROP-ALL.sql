
DROP SEQUENCE darceo.mdz_pi_id_seq RESTRICT;

ALTER TABLE darceo.mdz_plugin_iterations DROP CONSTRAINT mdz_plugin_iterations_pkey;
DROP TABLE darceo.mdz_plugin_iterations;

ALTER TABLE darceo.mdz_digital_objects DROP CONSTRAINT mdz_digital_objects_pkey;
DROP TABLE darceo.mdz_digital_objects;

ALTER TABLE darceo.mdz_file_formats DROP CONSTRAINT mdz_file_formats_pkey;
DROP TABLE darceo.mdz_file_formats;
