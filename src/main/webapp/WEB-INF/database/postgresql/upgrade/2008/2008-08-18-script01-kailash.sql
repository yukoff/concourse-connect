ALTER TABLE project_issue_replies_rating ADD project_id BIGINT REFERENCES projects(project_id);
