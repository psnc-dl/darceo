CREATE TABLE darceo.mdz_file_formats
(
  ff_puid VARCHAR(50) NOT NULL,
  CONSTRAINT mdz_file_formats_pkey PRIMARY KEY (ff_puid)
);

CREATE TABLE darceo.mdz_digital_objects
(
  do_identifier VARCHAR(255) NOT NULL,
  do_added_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  do_verified_on TIMESTAMP WITHOUT TIME ZONE,
  do_correct BOOLEAN,
  CONSTRAINT mdz_digital_objects_pkey PRIMARY KEY (do_identifier)
);


CREATE SEQUENCE darceo.mdz_pi_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.mdz_plugin_iterations
(
  pi_id bigint NOT NULL,
  pi_plugin_name VARCHAR(255) NOT NULL,
  pi_object_identifier VARCHAR(255) NOT NULL,
  pi_started_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  pi_finished_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT mdz_plugin_iterations_pkey PRIMARY KEY (pi_id)
);
