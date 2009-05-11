ALTER TABLE projects ADD projecttextid VARCHAR(100);
--UPDATE projects SET projecttextid = 'project_' || project_id;
--ALTER TABLE projects ALTER COLUMN projecttextid SET NOT NULL;
--ALTER TABLE projects ADD CONSTRAINT projects_projecttextid_key UNIQUE(projecttextid);
