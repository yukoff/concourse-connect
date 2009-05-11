-- Record project related events
CREATE TABLE project_history (
  history_id BIGSERIAL PRIMARY KEY,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id),
  link_object VARCHAR(255) NOT NULL,
  link_item_id BIGINT,
  link_start_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  description VARCHAR(512),
  enabled BOOLEAN DEFAULT true
);