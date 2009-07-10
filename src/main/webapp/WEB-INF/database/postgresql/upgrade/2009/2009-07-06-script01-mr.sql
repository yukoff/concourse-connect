CREATE TABLE instances (
  instance_id SERIAL PRIMARY KEY,
  domain_name VARCHAR(255) NOT NULL,
  context VARCHAR(255) NOT NULL DEFAULT '/',
  enabled BOOLEAN DEFAULT false NOT NULL
);
CREATE UNIQUE INDEX instances_uni_idx ON instances (domain_name, context);

ALTER TABLE users ADD instance_id INTEGER REFERENCES instances(instance_id);
ALTER TABLE contact_us ADD instance_id INTEGER REFERENCES instances(instance_id);
ALTER TABLE projects ADD instance_id INTEGER REFERENCES instances(instance_id);

CREATE INDEX projects_instanc_idx ON projects(instance_id);
