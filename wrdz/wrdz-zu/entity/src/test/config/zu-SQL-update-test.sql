INSERT INTO darceo.zu_users (usr_id, usr_username, usr_org_id, usr_homedir, usr_admin) SELECT 999, 'test', max(org_id), '/home', true FROM darceo.zu_organizations;
INSERT INTO darceo.zu_auth_group (agrp_id, agrp_groupname, agrp_single_user) VALUES (999, 'test', true);
INSERT INTO darceo.zu_auth_user (ausr_id, ausr_primary_group_id, ausr_active, ausr_fisrt_name, ausr_last_name, ausr_display_name, ausr_password_hash)
	SELECT max(usr_id), max(agrp_id), true, 'test', 'test', 'test', 'DWvmmyZHF/LdM2UuISsXMQS0pke3wRrnLpiF8RzTEvs=' FROM darceo.zu_users, darceo.zu_auth_group;
INSERT INTO darceo.zu_auth_group_auth_user (agau_ausr_id, agau_agrp_id)
    SELECT max(usr_id), max(agrp_id) FROM darceo.zu_users, darceo.zu_auth_group;
INSERT INTO darceo.zu_auth_group_auth_user (agau_ausr_id, agau_agrp_id)
    SELECT max(usr_id), 1 FROM darceo.zu_users;
    