/**
 *  PostgreSQL Table Creation
 *
 *@author     matt rajkowski
 *@created    2005-11-29
 */
 
CREATE TABLE report (
  report_id SERIAL PRIMARY KEY,
  filename VARCHAR(300) NOT NULL,
  type INTEGER DEFAULT 1 NOT NULL,
  title VARCHAR(300) NOT NULL,
  description VARCHAR(1024) NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id),
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id),
  enabled BOOLEAN DEFAULT true,
  custom BOOLEAN DEFAULT false,
  user_report BOOLEAN DEFAULT false,
  admin_report BOOLEAN DEFAULT false
);

CREATE TABLE report_queue (
  queue_id BIGSERIAL PRIMARY KEY,
  report_id INTEGER REFERENCES report(report_id) NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  processed TIMESTAMP(3) NULL DEFAULT NULL,
  status INTEGER DEFAULT 0 NOT NULL,
  filename VARCHAR(256),
  filesize INTEGER DEFAULT -1,
  enabled BOOLEAN DEFAULT true NOT NULL,
  project_id BIGINT REFERENCES projects(project_id),
  send_email BOOLEAN DEFAULT false NOT NULL,
  schedule INTEGER DEFAULT 0 NOT NULL,
  schedule_monday BOOLEAN DEFAULT false NOT NULL,
  schedule_tuesday BOOLEAN DEFAULT false NOT NULL,
  schedule_wednesday BOOLEAN DEFAULT false NOT NULL,
  schedule_thursday BOOLEAN DEFAULT false NOT NULL,
  schedule_friday BOOLEAN DEFAULT false NOT NULL,
  schedule_saturday BOOLEAN DEFAULT false NOT NULL,
  schedule_sunday BOOLEAN DEFAULT false NOT NULL,
  cleanup INTEGER DEFAULT 1 NOT NULL,
  schedule_time TIMESTAMP(3),
  output VARCHAR(20)
);

CREATE TABLE report_criteria (
  criteria_id SERIAL PRIMARY KEY,
  queue_id BIGINT REFERENCES report_queue(queue_id) NOT NULL,
  parameter VARCHAR(255) NOT NULL,
  value TEXT
);
