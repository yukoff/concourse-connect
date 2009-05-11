CREATE TABLE project_message (
  message_id BIGSERIAL PRIMARY KEY,
  subject VARCHAR(255) NOT NULL,
  body TEXT,
  project_id BIGINT NOT NULL REFERENCES projects(project_id),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL
);

CREATE TABLE project_msg_recipients (
  recipient_id BIGSERIAL PRIMARY KEY,
  message_id BIGINT NOT NULL REFERENCES project_message(message_id),
  contact_id BIGINT NOT NULL REFERENCES contacts(contact_id),
  status_id INT NOT NULL DEFAULT 0,
  status VARCHAR(80),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL
);
