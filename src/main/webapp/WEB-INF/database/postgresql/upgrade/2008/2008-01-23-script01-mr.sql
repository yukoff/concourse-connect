CREATE TABLE lookup_task_assigned_priority (
  code SERIAL PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BOOLEAN DEFAULT true
);

ALTER TABLE task ADD assigned_priority INTEGER REFERENCES lookup_task_assigned_priority;
