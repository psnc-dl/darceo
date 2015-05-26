
DROP SEQUENCE darceo.dsa_c_id_seq RESTRICT;

ALTER TABLE darceo.dsa_credentials DROP CONSTRAINT unq_dsa_credentials_c_organization;
ALTER TABLE darceo.dsa_credentials DROP CONSTRAINT dsa_credentials_pkey;
DROP TABLE darceo.dsa_credentials;
