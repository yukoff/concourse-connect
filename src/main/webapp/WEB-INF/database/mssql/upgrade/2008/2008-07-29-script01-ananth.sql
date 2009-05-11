CREATE TABLE project_message_template (
  template_id INT IDENTITY PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  title VARCHAR(255) NOT NULL,
  subject VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BIT DEFAULT 0,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP
);
