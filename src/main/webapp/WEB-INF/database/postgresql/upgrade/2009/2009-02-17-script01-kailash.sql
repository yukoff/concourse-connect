ALTER TABLE ticket ADD link_project_id BIGINT REFERENCES projects(project_id);
ALTER TABLE ticket ADD link_module_id BIGINT;
ALTER TABLE ticket ADD link_item_id BIGINT;