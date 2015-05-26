
-- ConnectionHelperBeanTest


INSERT INTO darceo.zmd_remote_repositories (rr_id, rr_host, rr_port, rr_protocol) VALUES (nextval('darceo.zmd_rr_id_seq'), 'example.com', 80, 'http');
INSERT INTO darceo.zmd_remote_repository_auth (rra_id, rra_password, rra_default, rra_username, rra_rr_id) SELECT nextval('darceo.zmd_rra_id_seq'), 'passwd', true, 'test', max(rr_id) FROM darceo.zmd_remote_repositories;
INSERT INTO darceo.zmd_remote_repository_auth (rra_id, rra_password, rra_default, rra_username, rra_rr_id) SELECT nextval('darceo.zmd_rra_id_seq'), 'passwd', false, 'tset', max(rr_id) FROM darceo.zmd_remote_repositories;


INSERT INTO darceo.zmd_remote_repositories (rr_id, rr_host, rr_port, rr_protocol) VALUES (nextval('darceo.zmd_rr_id_seq'), 'ambiguous.com', 21, 'ftp');
INSERT INTO darceo.zmd_remote_repository_auth (rra_id, rra_password, rra_default, rra_username, rra_rr_id) SELECT nextval('darceo.zmd_rra_id_seq'), 'passwd', true, 'test', max(rr_id) FROM darceo.zmd_remote_repositories;
INSERT INTO darceo.zmd_remote_repository_auth (rra_id, rra_password, rra_default, rra_username, rra_rr_id) SELECT nextval('darceo.zmd_rra_id_seq'), 'passwd', true, 'tset', max(rr_id) FROM darceo.zmd_remote_repositories;




