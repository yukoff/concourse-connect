UPDATE lookup_project_permission SET default_role = (SELECT code FROM lookup_project_role WHERE level = 25) WHERE permission = 'project-profile-activity-add';

UPDATE project_permissions SET userlevel = (SELECT code FROM lookup_project_role WHERE level = 25) WHERE permission_id IN (SELECT code FROM lookup_project_permission WHERE permission = 'project-profile-activity-add') AND userlevel NOT IN (SELECT code FROM lookup_project_role WHERE level = 25);

UPDATE lookup_project_permission SET permission = 'project-private-messages-delete' WHERE permission = 'project-private_messages-delete';
