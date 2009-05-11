CREATE TABLE project_featured_listing (
  featured_id INT IDENTITY PRIMARY KEY,
  project_id INTEGER REFERENCES projects(project_id) NOT NULL,
  portlet_key VARCHAR(255),
  featured_date DATETIME
);