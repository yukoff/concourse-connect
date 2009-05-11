ALTER TABLE project_private_message ADD link_project_id INT REFERENCES projects(project_id);
