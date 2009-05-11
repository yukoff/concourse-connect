CREATE TABLE project_list_template (
  template_id INT IDENTITY PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  list_names TEXT NOT NULL,
  enabled BIT DEFAULT 1,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP
);
