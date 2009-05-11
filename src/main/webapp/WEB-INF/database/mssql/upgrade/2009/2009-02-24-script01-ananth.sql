-- Record project related events
CREATE TABLE project_history (
  history_id INT IDENTITY PRIMARY KEY,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  project_id INTEGER REFERENCES projects(project_id),
  link_object VARCHAR(255) NOT NULL,
  link_item_id INTEGER,
  link_start_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  description VARCHAR(512),
  enabled BIT DEFAULT 1
);