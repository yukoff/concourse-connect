ALTER TABLE project_private_message ADD link_project_id BIGINT REFERENCES projects(project_id);
