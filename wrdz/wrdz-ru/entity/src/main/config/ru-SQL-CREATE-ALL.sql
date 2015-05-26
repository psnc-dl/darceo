
-- registries.RemoteRegistry

CREATE SEQUENCE darceo.ru_reg_id_seq INCREMENT BY 1 START WITH 1000;
  
CREATE TABLE darceo.ru_remote_registries
(
  reg_id bigint NOT NULL,
  reg_name VARCHAR(100),
  reg_location VARCHAR(255) NOT NULL,
  reg_username VARCHAR(255) NOT NULL,
  reg_description VARCHAR(255),
  reg_read_enabled boolean NOT NULL,
  reg_harvested boolean NOT NULL,
  reg_latest_harvest timestamp,
  CONSTRAINT ru_remote_registries_pkey PRIMARY KEY (reg_id),
  CONSTRAINT unq_ru_remote_registries_reg_location UNIQUE (reg_location)
);

-- services.descriptors.DescriptorScheme
-- services.descriptors.TechnicalDescriptorScheme
-- services.descriptors.SemanticDescriptorScheme

CREATE SEQUENCE darceo.ru_ds_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.ru_descriptor_schemes
(
  ds_id bigint NOT NULL,
  ds_name VARCHAR(100) NOT NULL,
  ds_namespace VARCHAR(255) NOT NULL,
  ds_version VARCHAR(50),
  ds_type VARCHAR(10) NOT NULL,
  CONSTRAINT ru_ds_services_pkey PRIMARY KEY (ds_id),
  CONSTRAINT unq_ru_descriptor_types_ds_namespace UNIQUE (ds_namespace),
  CONSTRAINT unq_ru_descriptor_types_ds_name_ds_version UNIQUE (ds_name, ds_version)
);

INSERT INTO darceo.ru_descriptor_schemes(ds_id, ds_name, ds_namespace, ds_version, ds_type)
  VALUES (1, 'WSDL', 'http://www.w3.org/ns/wsdl', '2.0', 'TECHNICAL');

INSERT INTO darceo.ru_descriptor_schemes(ds_id, ds_name, ds_namespace, ds_version, ds_type)
  VALUES (2, 'WADL', 'http://wadl.dev.java.net/2009/02', '1.0', 'TECHNICAL');

INSERT INTO darceo.ru_descriptor_schemes(ds_id, ds_name, ds_namespace, ds_version, ds_type)
  VALUES (3, 'WSDL', 'http://schemas.xmlsoap.org/wsdl/', '1.1', 'TECHNICAL');
  
INSERT INTO darceo.ru_descriptor_schemes(ds_id, ds_name, ds_namespace, ds_version, ds_type)
  VALUES (4, 'OWL-S', 'http://www.daml.org/services/owl-s/1.0/Service.owl', '1.0', 'SEMANTIC');
  
INSERT INTO darceo.ru_descriptor_schemes(ds_id, ds_name, ds_namespace, ds_version, ds_type)
  VALUES (5, 'OWL-S', 'http://www.daml.org/services/owl-s/1.1/Service.owl', '1.1', 'SEMANTIC');
  
INSERT INTO darceo.ru_descriptor_schemes(ds_id, ds_name, ds_namespace, ds_version, ds_type)
  VALUES (6, 'OWL-S', 'http://www.daml.org/services/owl-s/1.2/Service.owl', '1.2', 'SEMANTIC');

-- services.descriptors.SemanticDescriptor

CREATE SEQUENCE darceo.ru_sd_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.ru_sem_descriptors
(
  sd_id bigint NOT NULL,
  sd_ds_id bigint NOT NULL,
  sd_location VARCHAR(255) NOT NULL,
  sd_context VARCHAR(255) NOT NULL,
  sd_is_public boolean NOT NULL,
  sd_origin VARCHAR(1024),
  sd_is_deleted boolean NOT NULL,
  CONSTRAINT ru_sd_services_pkey PRIMARY KEY (sd_id),
  CONSTRAINT fk_ru_sem_descriptors_sd_ds_id FOREIGN KEY (sd_ds_id)
      REFERENCES darceo.ru_descriptor_schemes (ds_id),
  CONSTRAINT unq_ru_sem_descriptors_sd_location UNIQUE (sd_location),
  CONSTRAINT unq_ru_sem_descriptors_sd_context UNIQUE (sd_context)
);

CREATE INDEX ru_sem_descriptors_sd_ds_id_idx ON darceo.ru_sem_descriptors (sd_ds_id);

-- services.descriptors.TechnicalDescriptor

CREATE SEQUENCE darceo.ru_td_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.ru_tech_descriptors
(
  td_id bigint NOT NULL,
  td_ds_id bigint NOT NULL,
  td_location VARCHAR(255) NOT NULL,
  td_sd_id bigint NOT NULL,
  CONSTRAINT ru_td_services_pkey PRIMARY KEY (td_id),
  CONSTRAINT fk_ru_tech_descriptors_td_ds_id FOREIGN KEY (td_ds_id)
      REFERENCES darceo.ru_descriptor_schemes (ds_id),
  CONSTRAINT fk_ru_tech_descriptors_td_sd_id FOREIGN KEY (td_sd_id)
      REFERENCES darceo.ru_sem_descriptors (sd_id),
  CONSTRAINT unq_ru_tech_descriptors_td_location UNIQUE (td_location)
);

CREATE INDEX ru_tech_descriptors_td_ds_id_idx ON darceo.ru_tech_descriptors (td_ds_id);
CREATE INDEX ru_tech_descriptors_td_sd_id_idx ON darceo.ru_tech_descriptors (td_sd_id);

-- services.DataManipulationService

CREATE SEQUENCE darceo.ru_dms_id_seq INCREMENT BY 1 START WITH 1000;
  
CREATE TABLE darceo.ru_dms_services
(
  dms_id bigint NOT NULL,
  dms_iri VARCHAR(255) NOT NULL,
  dms_name VARCHAR(100) NOT NULL,
  dms_location VARCHAR(255) NOT NULL,
  dms_description VARCHAR(255),
  dms_type VARCHAR(25) NOT NULL,
  dms_sd_id bigint NOT NULL,
  dms_td_id bigint NOT NULL,
  CONSTRAINT ru_dms_services_pkey PRIMARY KEY (dms_id),
  CONSTRAINT unq_ru_dms_services_dms_iri UNIQUE (dms_iri),  
  CONSTRAINT fk_ru_dms_services_dms_sd_id FOREIGN KEY (dms_sd_id)
      REFERENCES darceo.ru_sem_descriptors (sd_id),
  CONSTRAINT fk_ru_dms_services_dms_td_id FOREIGN KEY (dms_td_id)
      REFERENCES darceo.ru_tech_descriptors (td_id)
);

CREATE INDEX ru_dms_services_dms_sd_id_idx ON darceo.ru_dms_services (dms_sd_id);
CREATE INDEX ru_dms_services_dms_td_id_idx ON darceo.ru_dms_services (dms_td_id);


-- services.ServiceOperation

CREATE SEQUENCE darceo.ru_so_id_seq INCREMENT BY 1 START WITH 1000;
  
CREATE TABLE darceo.ru_service_operations
(
  so_id bigint NOT NULL,
  so_type VARCHAR(15) NOT NULL,
  so_date timestamp NOT NULL,
  so_sd_id bigint NOT NULL,
  so_is_public boolean NOT NULL,
  CONSTRAINT ru_service_operations_pkey PRIMARY KEY (so_id),
  CONSTRAINT fk_ru_service_operations_so_sd_id FOREIGN KEY (so_sd_id)
      REFERENCES darceo.ru_sem_descriptors (sd_id)
);

CREATE INDEX ru_service_operations_so_id_idx ON darceo.ru_service_operations (so_id);
