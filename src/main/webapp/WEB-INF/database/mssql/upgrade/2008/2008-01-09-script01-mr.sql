
CREATE TABLE lookup_task_loe_remaining (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE lookup_task_assigned_to (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  user_id INTEGER NOT NULL REFERENCES users(user_id),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

ALTER TABLE task ADD loe_remaining INTEGER REFERENCES lookup_task_loe_remaining;
ALTER TABLE task ADD assigned_to INTEGER REFERENCES lookup_task_assigned_to;
