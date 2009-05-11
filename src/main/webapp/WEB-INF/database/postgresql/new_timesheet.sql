-- PostgreSQL

CREATE TABLE timesheet (
  timesheet_id BIGSERIAL PRIMARY KEY,
  user_id BIGINT REFERENCES users(user_id) NOT NULL,
  entry_date TIMESTAMP(3) NOT NULL,
  hours FLOAT DEFAULT 0 NOT NULL,
  start_time TIMESTAMP NULL,
  end_time TIMESTAMP(3) NULL,
  verified BOOLEAN DEFAULT false NOT NULL,
  approved BOOLEAN DEFAULT false NOT NULL,
  approved_by BIGINT REFERENCES users(user_id),
  available BOOLEAN DEFAULT true NOT NULL,
  unavailable BOOLEAN DEFAULT false NOT NULL,
  vacation BOOLEAN DEFAULT false NOT NULL,
  vacation_approved BOOLEAN DEFAULT false NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL
);

CREATE TABLE timesheet_projects (
  id BIGSERIAL PRIMARY KEY,
  timesheet_id BIGINT REFERENCES timesheet(timesheet_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id),
  hours FLOAT DEFAULT 0 NOT NULL,
  start_time TIMESTAMP(3) NULL,
  end_time TIMESTAMP(3) NULL
);


