CREATE TABLE project_message (
  message_id INT IDENTITY PRIMARY KEY,
  subject VARCHAR(255) NOT NULL,
  body TEXT,
  project_id INTEGER REFERENCES projects(project_id) NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL
);

CREATE TABLE project_msg_recipients (
  recipient_id INT IDENTITY PRIMARY KEY,
  message_id INTEGER NOT NULL REFERENCES project_message(message_id),
  contact_id INTEGER NOT NULL REFERENCES contacts(contact_id),
  status_id INT NOT NULL DEFAULT 0,
  status VARCHAR(80),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL
);