/**
 *  PostgreSQL Table Creation
 *
 *@author     ananth
 *@created
 */

CREATE TABLE project_webcast (
  webcast_id BIGSERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  inappropriate_count INTEGER DEFAULT 0
);
CREATE INDEX project_webcast_pid_idx ON project_webcast(project_id);

CREATE TABLE project_webcast_rating (
  rating_id BIGSERIAL PRIMARY KEY,
  webcast_id BIGINT NOT NULL REFERENCES project_webcast(webcast_id),
  rating INTEGER NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  project_id BIGINT REFERENCES projects(project_id),
  inappropriate BOOLEAN DEFAULT FALSE
);
CREATE INDEX project_webcast_rtg_idx ON project_webcast_rating(webcast_id);