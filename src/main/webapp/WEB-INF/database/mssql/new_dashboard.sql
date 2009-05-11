
CREATE TABLE lookup_project_portlet (
  portlet_id INT IDENTITY PRIMARY KEY,
  portlet_name VARCHAR(255),
  portlet_description VARCHAR(1024),
  portal_enabled BIT NOT NULL DEFAULT 0,
  project_enabled BIT NOT NULL DEFAULT 0,
  admin_enabled BIT NOT NULL DEFAULT 0,
  enabled BIT NOT NULL DEFAULT 0,
  entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE project_dashboard (
  dashboard_id INT IDENTITY PRIMARY KEY,
  dashboard_name VARCHAR(255),
  dashboard_level INT NOT NULL DEFAULT 1,
  project_id INT REFERENCES projects(project_id),
  portal BIT NOT NULL DEFAULT 0,
  enabled BIT NOT NULL DEFAULT 0,
  entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX proj_dashboard_proj_idx ON project_dashboard(project_id);

CREATE TABLE project_dashboard_page (
  page_id INT IDENTITY PRIMARY KEY,
  dashboard_id INTEGER REFERENCES project_dashboard(dashboard_id) NOT NULL,
  page_name VARCHAR(255),
  page_level INT NOT NULL DEFAULT 1,
  page_alias INT REFERENCES project_dashboard_page(page_id),
  page_design TEXT,
  enabled BIT NOT NULL DEFAULT 0,
  entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE project_dashboard_portlet (
  page_portlet_id INT IDENTITY PRIMARY KEY,
  page_id INT REFERENCES project_dashboard_page(page_id) NOT NULL,
  portlet_id INT REFERENCES lookup_project_portlet(portlet_id) NOT NULL,
  entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX proj_dsh_page_prt_pg_idx ON project_dashboard_portlet(page_id);

CREATE TABLE project_dashboard_portlet_prefs (
  preference_id INT IDENTITY PRIMARY KEY,
  page_portlet_id INT REFERENCES project_dashboard_portlet(page_portlet_id) NOT NULL,
  property_name VARCHAR(1024),
  property_value TEXT,
  entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX proj_dsh_pref_pg_prt_idx ON project_dashboard_portlet_prefs(page_portlet_id);
