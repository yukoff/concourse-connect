ALTER TABLE ticket ADD COLUMN link_project_id BIGINT REFERENCES projects(project_id);
ALTER TABLE ticket ADD COLUMN link_module_id BIGINT;
ALTER TABLE ticket ADD COLUMN link_item_id BIGINT;