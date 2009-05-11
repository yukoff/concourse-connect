CREATE TABLE project_message_template (
  template_id BIGSERIAL PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  title VARCHAR(255) NOT NULL,
  subject VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  level INTEGER DEFAULT 0 NOT NULL,
  enabled BOOLEAN DEFAULT true,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP
);
