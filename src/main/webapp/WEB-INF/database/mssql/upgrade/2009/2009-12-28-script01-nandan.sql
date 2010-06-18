UPDATE lookup_project_permission SET default_role = 5 WHERE permission = 'project-profile-activity-add';

UPDATE project_permissions SET userlevel = 5 WHERE permission_id IN (SELECT code FROM lookup_project_permission WHERE permission = 'project-profile-activity-add');
