UPDATE users SET profile_project_id = NULL WHERE profile_project_id = -1;
UPDATE users SET profile_project_id = NULL WHERE profile_project_id IS NOT NULL AND profile_project_id NOT IN (SELECT project_id FROM projects);
ALTER TABLE users ALTER COLUMN profile_project_id SET DEFAULT NULL;
