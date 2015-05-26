
-- permissions

DROP SEQUENCE darceo.zu_gp_id_seq RESTRICT;

ALTER TABLE darceo.zu_group_permissions DROP CONSTRAINT unq_zu_group_permissions_group_resource_permission;
ALTER TABLE darceo.zu_group_permissions DROP CONSTRAINT fk_zu_group_permissions_gp_ag_id;
ALTER TABLE darceo.zu_group_permissions DROP CONSTRAINT zu_group_permissions_pkey;
DROP TABLE darceo.zu_group_permissions;

-- JAAS groups for JDBC Realm --
-- JAAS users for JDBC Realm --

DROP VIEW darceo.jaas_groups;
DROP VIEW darceo.jaas_users; 


-- user.UserAuthentication --

ALTER TABLE darceo.zu_auth_group_auth_user DROP CONSTRAINT fk_zu_auth_group_auth_user_agau_agrp_id;
ALTER TABLE darceo.zu_auth_group_auth_user DROP CONSTRAINT fk_zu_auth_group_auth_user_agau_ausr_id;
ALTER TABLE darceo.zu_auth_group_auth_user DROP CONSTRAINT zu_auth_group_auth_user_pkey;
DROP TABLE darceo.zu_auth_group_auth_user;
    
--DROP INDEX zu_auth_user_ausr_id_idx;

ALTER TABLE darceo.zu_auth_user DROP CONSTRAINT fk_zu_auth_user_ausr_primary_group_id;
ALTER TABLE darceo.zu_auth_user DROP CONSTRAINT fk_zu_auth_user_ausr_id;
ALTER TABLE darceo.zu_auth_user DROP CONSTRAINT zu_auth_user_pkey;
DROP TABLE darceo.zu_auth_user;


-- user.GroupAuthentication --

DROP SEQUENCE darceo.zu_agrp_id_seq RESTRICT;

--DROP INDEX idx_zu_auth_group_agrp_groupname;

ALTER TABLE darceo.zu_auth_group DROP CONSTRAINT zu_auth_group_agrp_groupname_agrp_single_user_key;
ALTER TABLE darceo.zu_auth_group DROP CONSTRAINT zu_auth_group_pkey;
DROP TABLE darceo.zu_auth_group;


-- user.User --

DROP SEQUENCE darceo.zu_usr_id_seq RESTRICT;

--DROP INDEX zu_users_usr_org_id_idx;
--DROP INDEX idx_zu_users_usr_username;

ALTER TABLE darceo.zu_users DROP CONSTRAINT zu_users_usr_username_key;
ALTER TABLE darceo.zu_users DROP CONSTRAINT fk_zu_users_usr_org_id;
ALTER TABLE darceo.zu_users DROP CONSTRAINT zu_users_pkey;
DROP TABLE darceo.zu_users;


-- user.Organization --

DROP SEQUENCE darceo.zu_org_id_seq RESTRICT;

--DROP INDEX idx_zu_organizations_org_name;

ALTER TABLE darceo.zu_organizations DROP CONSTRAINT zu_organizations_org_name_key;
ALTER TABLE darceo.zu_organizations DROP CONSTRAINT zu_organizations_pkey;
DROP TABLE darceo.zu_organizations;
