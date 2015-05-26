
-- user.Organization --

CREATE SEQUENCE darceo.zu_org_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zu_organizations
(
  org_id bigint NOT NULL,
  org_name VARCHAR(255) NOT NULL,
  org_path VARCHAR(255) NOT NULL,
  CONSTRAINT zu_organizations_pkey PRIMARY KEY (org_id),
  CONSTRAINT zu_organizations_org_name_key UNIQUE (org_name)
);

CREATE UNIQUE INDEX idx_zu_organizations_org_name ON darceo.zu_organizations (org_name);

INSERT INTO darceo.zu_organizations (org_id, org_name, org_path) VALUES (1, 'GUESTS', '/');
INSERT INTO darceo.zu_organizations (org_id, org_name, org_path) VALUES (2, 'MODULES', '/');

-- user.User --

CREATE SEQUENCE darceo.zu_usr_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zu_users
(
  usr_id bigint NOT NULL,
  usr_homedir VARCHAR(255) NOT NULL,
  usr_username VARCHAR(255) NOT NULL,
  usr_org_id bigint NOT NULL,
  usr_admin boolean NOT NULL,
  CONSTRAINT zu_users_pkey PRIMARY KEY (usr_id),
  CONSTRAINT fk_zu_users_usr_org_id FOREIGN KEY (usr_org_id)
      REFERENCES darceo.zu_organizations (org_id),
  CONSTRAINT zu_users_usr_username_key UNIQUE (usr_username)
);

CREATE UNIQUE INDEX idx_zu_users_usr_username ON darceo.zu_users (usr_username);
CREATE INDEX zu_users_usr_org_id_idx ON darceo.zu_users (usr_org_id);

INSERT INTO darceo.zu_users (usr_id, usr_username, usr_org_id, usr_homedir, usr_admin) VALUES (1, 'anonymous', 1, '/', 'false');


-- user.GroupAuthentication --

CREATE SEQUENCE darceo.zu_agrp_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zu_auth_group
(
  agrp_id bigint NOT NULL,
  agrp_groupname VARCHAR(64) NOT NULL,
  agrp_single_user boolean NOT NULL,
  CONSTRAINT zu_auth_group_pkey PRIMARY KEY (agrp_id),
  CONSTRAINT zu_auth_group_agrp_groupname_agrp_single_user_key UNIQUE (agrp_groupname, agrp_single_user)
);

CREATE UNIQUE INDEX idx_zu_auth_group_agrp_groupname ON darceo.zu_auth_group (agrp_groupname);

INSERT INTO darceo.zu_auth_group (agrp_id, agrp_groupname, agrp_single_user) VALUES (1, 'auth-users', false);
INSERT INTO darceo.zu_auth_group (agrp_id, agrp_groupname, agrp_single_user) VALUES (2, 'auth-guests', false);
INSERT INTO darceo.zu_auth_group (agrp_id, agrp_groupname, agrp_single_user) VALUES (3, 'auth-modules', false);
INSERT INTO darceo.zu_auth_group (agrp_id, agrp_groupname, agrp_single_user) VALUES (4, 'auth-registries', false);

-- user.UserAuthentication --

CREATE TABLE darceo.zu_auth_user
(
  ausr_id bigint NOT NULL,
  ausr_primary_group_id bigint,
  ausr_active boolean NOT NULL,
  ausr_display_name VARCHAR(127),
  ausr_fisrt_name VARCHAR(31),
  ausr_last_name VARCHAR(31),
  ausr_middle_initial VARCHAR(15),
  ausr_password_hash VARCHAR(64),
  ausr_password_salt DECIMAL(128),
  ausr_certificate text,
  ausr_email VARCHAR(255),
  CONSTRAINT zu_auth_user_pkey PRIMARY KEY (ausr_id),
  CONSTRAINT fk_zu_auth_user_ausr_id FOREIGN KEY (ausr_id)
      REFERENCES darceo.zu_users (usr_id),
  CONSTRAINT fk_zu_auth_user_ausr_primary_group_id FOREIGN KEY (ausr_primary_group_id)
      REFERENCES darceo.zu_auth_group (agrp_id)
);

CREATE INDEX zu_auth_user_ausr_id_idx ON darceo.zu_auth_user (ausr_id);

INSERT INTO darceo.zu_auth_user (ausr_id, ausr_active, ausr_fisrt_name, ausr_middle_initial, ausr_last_name, ausr_display_name, ausr_password_hash)
    VALUES (1, true, null, null, null, 'anonymous', 'Lxg6TmRJOvPzd/dF7aUCNjzT5+9uTSZtREdY3gqF/Mg=');

    
CREATE TABLE darceo.zu_auth_group_auth_user
(
  agau_ausr_id bigint NOT NULL,
  agau_agrp_id bigint NOT NULL,
  CONSTRAINT zu_auth_group_auth_user_pkey PRIMARY KEY (agau_ausr_id,agau_agrp_id),
  CONSTRAINT fk_zu_auth_group_auth_user_agau_ausr_id FOREIGN KEY (agau_ausr_id)
      REFERENCES darceo.zu_auth_user (ausr_id),
  CONSTRAINT fk_zu_auth_group_auth_user_agau_agrp_id FOREIGN KEY (agau_agrp_id)
      REFERENCES darceo.zu_auth_group (agrp_id)
);


INSERT INTO darceo.zu_auth_group_auth_user (agau_ausr_id, agau_agrp_id)
    VALUES (1, 2);

-- JAAS users for JDBC Realm --

CREATE VIEW darceo.jaas_users AS 
 SELECT t.usr_username AS username, s.ausr_password_hash AS password, s.ausr_password_salt AS salt, s.ausr_certificate AS certificate
   FROM darceo.zu_users t
   JOIN darceo.zu_auth_user s ON t.usr_id = s.ausr_id
  WHERE s.ausr_active = true AND (s.ausr_password_hash IS NOT NULL OR s.ausr_certificate IS NOT NULL);


-- JAAS groups for JDBC Realm --

CREATE VIEW darceo.jaas_groups AS 
 SELECT t.usr_username AS username, w.agrp_groupname AS groupname
   FROM darceo.zu_users t
   JOIN darceo.zu_auth_user s ON t.usr_id = s.ausr_id
   JOIN darceo.zu_auth_group_auth_user u ON u.agau_ausr_id = s.ausr_id
   JOIN darceo.zu_auth_group w ON w.agrp_id = u.agau_agrp_id;

   
-- permissions

CREATE SEQUENCE darceo.zu_gp_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE darceo.zu_group_permissions
(
  gp_id bigint NOT NULL,
  gp_ag_id bigint NOT NULL,
  gp_resource_id bigint,
  gp_resource_type VARCHAR(10) NOT NULL,
  gp_permission VARCHAR(15) NOT NULL,
  CONSTRAINT zu_group_permissions_pkey PRIMARY KEY (gp_id),
  CONSTRAINT fk_zu_group_permissions_gp_ag_id FOREIGN KEY (gp_ag_id)
      REFERENCES darceo.zu_auth_group (agrp_id), 
  CONSTRAINT unq_zu_group_permissions_group_resource_permission UNIQUE (gp_ag_id, gp_resource_id, gp_resource_type, gp_permission)
);


-- users 

INSERT INTO darceo.zu_users (usr_id, usr_username, usr_org_id, usr_homedir, usr_admin) VALUES (11, 'ZMKD', 2, '/', true);
	-- 2 means MODULES
INSERT INTO darceo.zu_auth_group (agrp_id, agrp_groupname, agrp_single_user) VALUES (11, 'ZMKD', true);
INSERT INTO darceo.zu_auth_user (ausr_id, ausr_primary_group_id, ausr_active, ausr_fisrt_name, ausr_last_name, ausr_display_name, ausr_password_hash)
	SELECT max(usr_id), 11, true, 'zmkd', 'module', 'zmkd/module', 'tQikeBk63/pHeIONcxSNWuflUVU/20eLKB1nVTQXVsg=' FROM darceo.zu_users;
INSERT INTO darceo.zu_auth_group_auth_user (agau_ausr_id, agau_agrp_id)
    SELECT max(usr_id), 11 FROM darceo.zu_users;
INSERT INTO darceo.zu_auth_group_auth_user (agau_ausr_id, agau_agrp_id)
    SELECT max(usr_id), 3 FROM darceo.zu_users;								
	-- 3 means auth-modules
    
INSERT INTO darceo.zu_users (usr_id, usr_username, usr_org_id, usr_homedir, usr_admin) VALUES (13, 'MDZ', 2, '/', true);
	-- 2 means MODULES
INSERT INTO darceo.zu_auth_group (agrp_id, agrp_groupname, agrp_single_user) VALUES (13, 'MDZ', true);
INSERT INTO darceo.zu_auth_user (ausr_id, ausr_primary_group_id, ausr_active, ausr_fisrt_name, ausr_last_name, ausr_display_name, ausr_password_hash)
	SELECT max(usr_id), 13, true, 'mdz', 'module', 'mdz/module', 'tQikeBk63/pHeIONcxSNWuflUVU/20eLKB1nVTQXVsg=' FROM darceo.zu_users;
INSERT INTO darceo.zu_auth_group_auth_user (agau_ausr_id, agau_agrp_id)
    SELECT max(usr_id), 13 FROM darceo.zu_users;
INSERT INTO darceo.zu_auth_group_auth_user (agau_ausr_id, agau_agrp_id)
    SELECT max(usr_id), 3 FROM darceo.zu_users;
    -- 3 means auth-modules

INSERT INTO darceo.zu_organizations (org_id, org_name, org_path) VALUES (nextval('darceo.zu_org_id_seq'), 'USERS', '/');
INSERT INTO darceo.zu_users (usr_id, usr_username, usr_org_id, usr_homedir, usr_admin) SELECT 33, 'admin', max(org_id), '/', true FROM darceo.zu_organizations;
INSERT INTO darceo.zu_auth_group (agrp_id, agrp_groupname, agrp_single_user) VALUES (33, 'admin', true);
INSERT INTO darceo.zu_auth_user (ausr_id, ausr_primary_group_id, ausr_active, ausr_fisrt_name, ausr_last_name, ausr_display_name, ausr_password_hash)
	SELECT max(usr_id), max(agrp_id), true, 'admin', 'administrator', 'dArceo admin', 'DWvmmyZHF/LdM2UuISsXMQS0pke3wRrnLpiF8RzTEvs=' FROM darceo.zu_users, darceo.zu_auth_group;
INSERT INTO darceo.zu_auth_group_auth_user (agau_ausr_id, agau_agrp_id)
    SELECT max(usr_id), max(agrp_id) FROM darceo.zu_users, darceo.zu_auth_group;
INSERT INTO darceo.zu_auth_group_auth_user (agau_ausr_id, agau_agrp_id)
    SELECT max(usr_id), 1 FROM darceo.zu_users;    
    