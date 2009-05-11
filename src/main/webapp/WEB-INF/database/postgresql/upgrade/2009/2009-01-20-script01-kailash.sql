CREATE TABLE project_featured_listing (
  featured_id SERIAL PRIMARY KEY,
  project_id BIGINT REFERENCES projects(project_id) NOT NULL,
  portlet_key VARCHAR(255),
  featured_date TIMESTAMP(3)
);