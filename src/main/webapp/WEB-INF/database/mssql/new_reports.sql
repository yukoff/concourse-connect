/**
 *  MSSQL Table Creation
 *
 *@author     matt rajkowski
 *@created    2005-11-29
 */
 
CREATE TABLE report (
  report_id INT IDENTITY PRIMARY KEY,
  filename VARCHAR(300) NOT NULL,
  type INTEGER NOT NULL DEFAULT 1,
  title VARCHAR(300) NOT NULL,
  description VARCHAR(1024) NOT NULL,
  entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  enteredby INT REFERENCES users(user_id),
  modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modifiedby INT REFERENCES users(user_id),
  enabled BIT DEFAULT 1,
  custom BIT DEFAULT 0,
  user_report BIT DEFAULT 0,
  admin_report BIT DEFAULT 0
);

CREATE TABLE report_queue (
  queue_id INT IDENTITY PRIMARY KEY,
  report_id INTEGER NOT NULL REFERENCES report(report_id),
  entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  enteredby INT NOT NULL REFERENCES users(user_id),
  processed DATETIME NULL DEFAULT NULL,
  status INT NOT NULL DEFAULT 0,
  filename VARCHAR(256),
  filesize INT DEFAULT -1,
  enabled BIT DEFAULT 1,
  project_id INTEGER REFERENCES projects(project_id),
  send_email BIT NOT NULL DEFAULT 0,
  schedule INTEGER NOT NULL DEFAULT 0,
  schedule_monday BIT NOT NULL DEFAULT 0,
  schedule_tuesday BIT NOT NULL DEFAULT 0,
  schedule_wednesday BIT NOT NULL DEFAULT 0,
  schedule_thursday BIT NOT NULL DEFAULT 0,
  schedule_friday BIT NOT NULL DEFAULT 0,
  schedule_saturday BIT NOT NULL DEFAULT 0,
  schedule_sunday BIT NOT NULL DEFAULT 0,
  cleanup INT NOT NULL DEFAULT 1,
  schedule_time DATETIME,
  output VARCHAR(20)
);

CREATE TABLE report_criteria (
  criteria_id INT IDENTITY PRIMARY KEY,
  queue_id INTEGER NOT NULL REFERENCES report_queue(queue_id),
  parameter VARCHAR(255) NOT NULL,
  value TEXT
);
