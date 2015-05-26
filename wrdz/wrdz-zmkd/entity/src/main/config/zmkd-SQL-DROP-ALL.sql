
-- plan.MigartionItemLog

DROP SEQUENCE darceo.zmkd_mil_id_seq RESTRICT;
  
--DROP INDEX zmkd_migration_item_logs_mil_migration_plan_id_idx;

ALTER TABLE darceo.zmkd_migration_item_logs DROP CONSTRAINT fk_zmkd_migration_item_logs_mil_migration_plan_id;
ALTER TABLE darceo.zmkd_migration_item_logs DROP CONSTRAINT unq_zmkd_migration_item_logs_mil_request_id;
ALTER TABLE darceo.zmkd_migration_item_logs DROP CONSTRAINT zmkd_migration_item_logs_pkey;
DROP TABLE darceo.zmkd_migration_item_logs;


-- plan.ServiceOutcome

DROP SEQUENCE darceo.zmkd_so_id_seq RESTRICT;

--DROP INDEX zmkd_service_outcomes_so_service_id_idx;

ALTER TABLE darceo.zmkd_service_outcomes DROP CONSTRAINT fk_zmkd_service_outcomes_so_service_id;
ALTER TABLE darceo.zmkd_service_outcomes DROP CONSTRAINT zmkd_service_outcomes_pkey;
DROP TABLE darceo.zmkd_service_outcomes;


-- plan.ServiceParameter

DROP SEQUENCE darceo.zmkd_sp_id_seq RESTRICT;

--DROP INDEX zmkd_service_parameters_sp_service_id_idx;

ALTER TABLE darceo.zmkd_service_parameters DROP CONSTRAINT fk_zmkd_service_parameters_sp_service_id;
ALTER TABLE darceo.zmkd_service_parameters DROP CONSTRAINT zmkd_service_parameters_pkey;
DROP TABLE darceo.zmkd_service_parameters;


-- plan.Service, plan.Transformation, plan.Delivery

DROP SEQUENCE darceo.zmkd_s_id_seq RESTRICT;

ALTER TABLE darceo.zmkd_deliveries_input_file_formats DROP CONSTRAINT fk_zmkd_deliveries_imput_file_formats_iff_id;
ALTER TABLE darceo.zmkd_deliveries_input_file_formats DROP CONSTRAINT fk_zmkd_deliveries_imput_file_formats_d_id;
ALTER TABLE darceo.zmkd_deliveries_input_file_formats DROP CONSTRAINT zmkd_deliveries_imput_file_formats_pkey;
DROP TABLE darceo.zmkd_deliveries_input_file_formats;

--DROP INDEX zmkd_services_d_delivery_plan_id_idx;
--DROP INDEX zmkd_services_t_transformation_path_id_idx;
--DROP INDEX zmkd_services_t_output_file_format_id_idx;
--DROP INDEX zmkd_services_t_input_file_format_id_idx;

ALTER TABLE darceo.zmkd_services DROP CONSTRAINT fk_zmkd_services_d_delivery_plan_id;
ALTER TABLE darceo.zmkd_services DROP CONSTRAINT fk_zmkd_services_t_transformation_path_id;
ALTER TABLE darceo.zmkd_services DROP CONSTRAINT fk_zmkd_services_t_output_file_format_id;
ALTER TABLE darceo.zmkd_services DROP CONSTRAINT fk_zmkd_services_t_input_file_format_id;
ALTER TABLE darceo.zmkd_services DROP CONSTRAINT zmkd_services_pkey;
DROP TABLE darceo.zmkd_services;


-- plan.TransformationPath, plan.MigrationPath, plan.ConversionPath

DROP SEQUENCE darceo.zmkd_tp_id_seq RESTRICT;

--DROP INDEX zmkd_migration_plans_mp_active_path_id_idx;

ALTER TABLE darceo.zmkd_migration_plans DROP CONSTRAINT fk_zmkd_migration_plans_mp_active_path_id;

--DROP INDEX zmkd_transformation_paths_cp_delivery_plan_id_idx;
--DROP INDEX zmkd_transformation_paths_mp_migration_plan_id_idx;

ALTER TABLE darceo.zmkd_transformation_paths DROP CONSTRAINT fk_zmkd_transformation_paths_cp_delivery_plan_id;
ALTER TABLE darceo.zmkd_transformation_paths DROP CONSTRAINT fk_zmkd_transformation_paths_mp_migration_plan_id;
ALTER TABLE darceo.zmkd_transformation_paths DROP CONSTRAINT zmkd_transformation_paths_pkey;
DROP TABLE darceo.zmkd_transformation_paths;


-- plan.DeliveryPlan

ALTER TABLE darceo.zmkd_delivery_plans DROP CONSTRAINT zmkd_delivery_plans_pkey;
DROP TABLE darceo.zmkd_delivery_plans;


-- plan.MigrationPlan

DROP SEQUENCE darceo.zmkd_mp_id_seq RESTRICT;

--DROP INDEX zmkd_migration_plans_mp_output_file_format_idx
--DROP INDEX zmkd_migration_plans_mp_input_file_format_idx;

ALTER TABLE darceo.zmkd_migration_plans DROP CONSTRAINT fk_zmkd_migration_plans_mp_output_file_format;
ALTER TABLE darceo.zmkd_migration_plans DROP CONSTRAINT fk_zmkd_migration_plans_mp_input_file_format;
ALTER TABLE darceo.zmkd_migration_plans DROP CONSTRAINT zmkd_migration_plans_pkey;
DROP TABLE darceo.zmkd_migration_plans;


-- format.FileFormat

DROP SEQUENCE darceo.zmkd_ff_id_seq RESTRICT;

ALTER TABLE darceo.zmkd_file_formats DROP CONSTRAINT unq_zmkd_file_formats_ff_udfr_iri;
ALTER TABLE darceo.zmkd_file_formats DROP CONSTRAINT unq_zmkd_file_formats_ff_puid;
ALTER TABLE darceo.zmkd_file_formats DROP CONSTRAINT zmkd_file_formats_pkey;
DROP TABLE darceo.zmkd_file_formats;
