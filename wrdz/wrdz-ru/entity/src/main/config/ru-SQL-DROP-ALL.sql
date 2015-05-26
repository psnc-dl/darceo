
-- services.ServiceOperation

DROP SEQUENCE darceo.ru_so_id_seq RESTRICT;
  
--DROP INDEX ru_service_operations_so_id_idx;

ALTER TABLE darceo.ru_service_operations DROP CONSTRAINT fk_ru_service_operations_so_sd_id;
ALTER TABLE darceo.ru_service_operations DROP CONSTRAINT ru_service_operations_pkey;
DROP TABLE darceo.ru_service_operations;


-- services.DataManipulationService

DROP SEQUENCE darceo.ru_dms_id_seq RESTRICT;

--DROP INDEX ru_dms_services_dms_td_id_idx;
--DROP INDEX ru_dms_services_dms_sd_id_idx;

ALTER TABLE darceo.ru_dms_services DROP CONSTRAINT fk_ru_dms_services_dms_td_id;
ALTER TABLE darceo.ru_dms_services DROP CONSTRAINT fk_ru_dms_services_dms_sd_id;
ALTER TABLE darceo.ru_dms_services DROP CONSTRAINT unq_ru_dms_services_dms_iri;
ALTER TABLE darceo.ru_dms_services DROP CONSTRAINT ru_dms_services_pkey;
DROP TABLE darceo.ru_dms_services;


-- services.descriptors.TechnicalDescriptor

DROP SEQUENCE darceo.ru_td_id_seq RESTRICT;

--DROP INDEX ru_tech_descriptors_td_sd_id_idx;
--DROP INDEX ru_tech_descriptors_td_ds_id_idx;

ALTER TABLE darceo.ru_tech_descriptors DROP CONSTRAINT unq_ru_tech_descriptors_td_location;
ALTER TABLE darceo.ru_tech_descriptors DROP CONSTRAINT fk_ru_tech_descriptors_td_sd_id;
ALTER TABLE darceo.ru_tech_descriptors DROP CONSTRAINT fk_ru_tech_descriptors_td_ds_id;
ALTER TABLE darceo.ru_tech_descriptors DROP CONSTRAINT ru_td_services_pkey;
DROP TABLE darceo.ru_tech_descriptors;


-- services.descriptors.SemanticDescriptor

DROP SEQUENCE darceo.ru_sd_id_seq RESTRICT;

--DROP INDEX ru_sem_descriptors_sd_ds_id_idx;

ALTER TABLE darceo.ru_sem_descriptors DROP CONSTRAINT unq_ru_sem_descriptors_sd_context;
ALTER TABLE darceo.ru_sem_descriptors DROP CONSTRAINT unq_ru_sem_descriptors_sd_location;
ALTER TABLE darceo.ru_sem_descriptors DROP CONSTRAINT fk_ru_sem_descriptors_sd_ds_id;
ALTER TABLE darceo.ru_sem_descriptors DROP CONSTRAINT ru_sd_services_pkey;
DROP TABLE darceo.ru_sem_descriptors;

-- services.descriptors.DescriptorScheme
-- services.descriptors.TechnicalDescriptorScheme
-- services.descriptors.SemanticDescriptorScheme

DROP SEQUENCE darceo.ru_ds_id_seq RESTRICT;

ALTER TABLE darceo.ru_descriptor_schemes DROP CONSTRAINT unq_ru_descriptor_types_ds_name_ds_version;
ALTER TABLE darceo.ru_descriptor_schemes DROP CONSTRAINT unq_ru_descriptor_types_ds_namespace;
ALTER TABLE darceo.ru_descriptor_schemes DROP CONSTRAINT ru_ds_services_pkey;
DROP TABLE darceo.ru_descriptor_schemes;


-- registries.RemoteRegistry

DROP SEQUENCE darceo.ru_reg_id_seq RESTRICT;

ALTER TABLE darceo.ru_remote_registries DROP CONSTRAINT unq_ru_remote_registries_reg_location;
ALTER TABLE darceo.ru_remote_registries DROP CONSTRAINT ru_remote_registries_pkey;
DROP TABLE darceo.ru_remote_registries;
