INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 82, 'Advertisements');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 13, 10, 4, 'project-ads-view', 'View advertisements');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 13, 20, 2, 'project-ads-add', 'Create and modify advertisements');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 13, 30, 1, 'project-ads-admin', 'Administrate advertisements');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 84, 'Classifieds');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 14, 10, 4, 'project-classifieds-view', 'View classifieds');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 14, 20, 2, 'project-classifieds-add', 'Create and modify classifieds');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 14, 30, 1, 'project-classifieds-admin', 'Administrate classifieds');

INSERT INTO lookup_project_permission_category (group_id, level, description) VALUES (1, 86, 'Badges');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 15, 10, 4, 'project-badges-view', 'View badges');
INSERT INTO lookup_project_permission (group_id, category_id, level, default_role, permission, description) VALUES (1, 15, 30, 1, 'project-badges-admin', 'Administrate badges');
