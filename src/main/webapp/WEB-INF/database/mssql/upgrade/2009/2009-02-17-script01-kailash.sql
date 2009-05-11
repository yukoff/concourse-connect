ALTER TABLE ticket ADD link_project_id INTEGER REFERENCES projects(project_id);
ALTER TABLE ticket ADD link_module_id INTEGER;
ALTER TABLE ticket ADD link_item_id INTEGER;