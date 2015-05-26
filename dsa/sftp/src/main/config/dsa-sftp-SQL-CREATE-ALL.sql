
CREATE SEQUENCE darceo.dsa_c_id_seq INCREMENT BY 1 START WITH 1000;
  
CREATE TABLE darceo.dsa_credentials
(
  c_id bigint NOT NULL,
  c_organization VARCHAR(127) NOT NULL,
  c_username VARCHAR(63) NOT NULL,
  c_private_key BYTEA NOT NULL,
  c_public_key BYTEA NOT NULL,
  CONSTRAINT dsa_credentials_pkey PRIMARY KEY (c_id),
  CONSTRAINT unq_dsa_credentials_c_organization UNIQUE (c_organization)
);
