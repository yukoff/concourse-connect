ALTER TABLE project_issue_replies_rating ADD project_id INTEGER REFERENCES projects(project_id);
