 
-- format.FileFormat

CREATE SEQUENCE darceo.zmkd_ff_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmkd_file_formats
(
  ff_id bigint NOT NULL,
  ff_puid VARCHAR(50),
  ff_udfr_iri VARCHAR(255),
  ff_extension VARCHAR(16),
  ff_mimetype VARCHAR(100),
  CONSTRAINT zmkd_file_formats_pkey PRIMARY KEY (ff_id),
  CONSTRAINT unq_zmkd_file_formats_ff_puid UNIQUE (ff_puid),
  CONSTRAINT unq_zmkd_file_formats_ff_udfr_iri UNIQUE (ff_udfr_iri)
);

-- plan.MigrationPlan

CREATE SEQUENCE darceo.zmkd_mp_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmkd_migration_plans
(  
  mp_id bigint NOT NULL,
  mp_active_path_id bigint,
  mp_status VARCHAR(20) NOT NULL,
  mp_awaiting_object VARCHAR(255),
  mp_awaiting_service VARCHAR(255),
  mp_xml_file text,
  mp_input_file_format bigint,
  mp_output_file_format bigint,
  mp_owner_id bigint NOT NULL,
  CONSTRAINT zmkd_migration_plans_pkey PRIMARY KEY (mp_id),
  CONSTRAINT fk_zmkd_migration_plans_mp_input_file_format FOREIGN KEY (mp_input_file_format)
  	  REFERENCES darceo.zmkd_file_formats (ff_id),
  CONSTRAINT fk_zmkd_migration_plans_mp_output_file_format FOREIGN KEY (mp_output_file_format)
  	  REFERENCES darceo.zmkd_file_formats (ff_id)
);

CREATE INDEX zmkd_migration_plans_mp_input_file_format_idx ON darceo.zmkd_migration_plans (mp_input_file_format);
CREATE INDEX zmkd_migration_plans_mp_output_file_format_idx ON darceo.zmkd_migration_plans (mp_output_file_format);

-- plan.DeliveryPlan

CREATE TABLE darceo.zmkd_delivery_plans
(  
  dp_id VARCHAR(63) NOT NULL,
  dp_object_identifier VARCHAR(255) NOT NULL,
  dp_status VARCHAR(20) NOT NULL,
  CONSTRAINT zmkd_delivery_plans_pkey PRIMARY KEY (dp_id)
);

-- plan.TransformationPath, plan.MigrationPath, plan.ConversionPath

CREATE SEQUENCE darceo.zmkd_tp_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmkd_transformation_paths
(
  tp_id bigint NOT NULL,
  tp_type VARCHAR(31) NOT NULL,
  tp_execution_cost int,
  mp_migration_plan_id bigint,
  cp_delivery_plan_id VARCHAR(63),
  CONSTRAINT zmkd_transformation_paths_pkey PRIMARY KEY (tp_id),
  CONSTRAINT fk_zmkd_transformation_paths_mp_migration_plan_id FOREIGN KEY (mp_migration_plan_id)
  	  REFERENCES darceo.zmkd_migration_plans (mp_id),
  CONSTRAINT fk_zmkd_transformation_paths_cp_delivery_plan_id FOREIGN KEY (cp_delivery_plan_id)
  	  REFERENCES darceo.zmkd_delivery_plans (dp_id)
);

CREATE INDEX zmkd_transformation_paths_mp_migration_plan_id_idx ON darceo.zmkd_transformation_paths (mp_migration_plan_id);
CREATE INDEX zmkd_transformation_paths_cp_delivery_plan_id_idx ON darceo.zmkd_transformation_paths (cp_delivery_plan_id);

ALTER TABLE darceo.zmkd_migration_plans 
	ADD CONSTRAINT fk_zmkd_migration_plans_mp_active_path_id FOREIGN KEY (mp_active_path_id)
	REFERENCES darceo.zmkd_transformation_paths (tp_id);

CREATE INDEX zmkd_migration_plans_mp_active_path_id_idx ON darceo.zmkd_migration_plans (mp_active_path_id);

-- plan.Service, plan.Transformation, plan.Delivery

CREATE SEQUENCE darceo.zmkd_s_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmkd_services
(
  s_id bigint NOT NULL,
  s_type VARCHAR(31) NOT NULL,
  s_ontology_iri VARCHAR(255) NOT NULL,
  s_service_iri VARCHAR(255) NOT NULL,
  s_service_name VARCHAR(100) NOT NULL,
  t_type VARCHAR(20),
  t_class VARCHAR(20),
  t_order_no INTEGER,
  t_input_file_format_id bigint,
  t_output_file_format_id bigint,
  t_transformation_path_id bigint,
  d_client_location VARCHAR(255),
  d_delivery_plan_id VARCHAR(63),
  d_execution_cost int,
  CONSTRAINT zmkd_services_pkey PRIMARY KEY (s_id),
  CONSTRAINT fk_zmkd_services_t_input_file_format_id FOREIGN KEY (t_input_file_format_id)
      REFERENCES darceo.zmkd_file_formats (ff_id),
  CONSTRAINT fk_zmkd_services_t_output_file_format_id FOREIGN KEY (t_output_file_format_id)
     REFERENCES darceo.zmkd_file_formats (ff_id),
  CONSTRAINT fk_zmkd_services_t_transformation_path_id FOREIGN KEY (t_transformation_path_id)
     REFERENCES darceo.zmkd_transformation_paths (tp_id),
  CONSTRAINT fk_zmkd_services_d_delivery_plan_id FOREIGN KEY (d_delivery_plan_id)
     REFERENCES darceo.zmkd_delivery_plans (dp_id)
);

CREATE INDEX zmkd_services_t_input_file_format_id_idx ON darceo.zmkd_services (t_input_file_format_id);
CREATE INDEX zmkd_services_t_output_file_format_id_idx ON darceo.zmkd_services (t_output_file_format_id);
CREATE INDEX zmkd_services_t_transformation_path_id_idx ON darceo.zmkd_services (t_transformation_path_id);
CREATE INDEX zmkd_services_d_delivery_plan_id_idx ON darceo.zmkd_services (d_delivery_plan_id);


CREATE TABLE darceo.zmkd_deliveries_input_file_formats
(
  d_id bigint NOT NULL,
  iff_id bigint NOT NULL,
  CONSTRAINT zmkd_deliveries_imput_file_formats_pkey PRIMARY KEY (d_id, iff_id),
  CONSTRAINT fk_zmkd_deliveries_imput_file_formats_d_id FOREIGN KEY (d_id)
      REFERENCES darceo.zmkd_services (s_id),
  CONSTRAINT fk_zmkd_deliveries_imput_file_formats_iff_id FOREIGN KEY (iff_id)
      REFERENCES darceo.zmkd_file_formats (ff_id)
);


-- plan.ServiceParameter

CREATE SEQUENCE darceo.zmkd_sp_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmkd_service_parameters 
( 
  sp_id bigint NOT NULL,
  sp_name VARCHAR(255) NOT NULL,
  sp_type VARCHAR(255) NOT NULL,
  sp_bundle_type VARCHAR(255),
  sp_value VARCHAR(255),
  sp_service_id bigint NOT NULL,
  CONSTRAINT zmkd_service_parameters_pkey PRIMARY KEY (sp_id),
  CONSTRAINT fk_zmkd_service_parameters_sp_service_id FOREIGN KEY (sp_service_id)
  	  REFERENCES darceo.zmkd_services (s_id)
);

CREATE INDEX zmkd_service_parameters_sp_service_id_idx ON darceo.zmkd_service_parameters (sp_service_id);

-- plan.ServiceOutcome

CREATE SEQUENCE darceo.zmkd_so_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmkd_service_outcomes 
( 
  so_id bigint NOT NULL,
  so_name VARCHAR(255) NOT NULL,
  so_type VARCHAR(255) NOT NULL,
  so_bundle_type VARCHAR(255),
  so_service_id bigint NOT NULL,
  CONSTRAINT zmkd_service_outcomes_pkey PRIMARY KEY (so_id),
  CONSTRAINT fk_zmkd_service_outcomes_so_service_id FOREIGN KEY (so_service_id)
  	  REFERENCES darceo.zmkd_services (s_id)
);

CREATE INDEX zmkd_service_outcomes_so_service_id_idx ON darceo.zmkd_service_outcomes (so_service_id);

-- plan.MigartionItemLog

CREATE SEQUENCE darceo.zmkd_mil_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zmkd_migration_item_logs
(
  mil_id bigint NOT NULL,
  mil_object_identifier VARCHAR(255) NOT NULL,
  mil_status VARCHAR(20) NOT NULL,
  mil_started_on TIMESTAMP,
  mil_ended_on TIMESTAMP,
  mil_error_message_params VARCHAR(1024),
  mil_migration_plan_id bigint NOT NULL,
  mil_request_id VARCHAR(255),
  CONSTRAINT zmkd_migration_item_logs_pkey PRIMARY KEY (mil_id),
  CONSTRAINT unq_zmkd_migration_item_logs_mil_request_id UNIQUE (mil_request_id),
  CONSTRAINT fk_zmkd_migration_item_logs_mil_migration_plan_id FOREIGN KEY (mil_migration_plan_id)
  	  REFERENCES darceo.zmkd_migration_plans (mp_id)
);

CREATE INDEX zmkd_migration_item_logs_mil_migration_plan_id_idx ON darceo.zmkd_migration_item_logs (mil_migration_plan_id);
