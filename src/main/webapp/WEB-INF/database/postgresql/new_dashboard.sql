/**
 *  PostgreSQL Table Creation
 *
 *@author     matt rajkowski
 *@created
 */

CREATE TABLE lookup_project_portlet (
  portlet_id SERIAL PRIMARY KEY,
  portlet_name VARCHAR(255),
  portlet_description VARCHAR(1024),
  portal_enabled BOOLEAN DEFAULT false NOT NULL,
  project_enabled BOOLEAN DEFAULT false NOT NULL,
  admin_enabled BOOLEAN DEFAULT false NOT NULL,
  enabled BOOLEAN DEFAULT false NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE project_dashboard (
  dashboard_id BIGSERIAL PRIMARY KEY,
  dashboard_name VARCHAR(255),
  dashboard_level INTEGER DEFAULT 1 NOT NULL,
  project_id BIGINT REFERENCES projects(project_id),
  portal BOOLEAN DEFAULT false NOT NULL,
  enabled BOOLEAN DEFAULT false NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX proj_dashboard_proj_idx ON project_dashboard(project_id);

CREATE TABLE project_dashboard_page (
  page_id BIGSERIAL PRIMARY KEY,
  dashboard_id BIGINT REFERENCES project_dashboard(dashboard_id) NOT NULL,
  page_name VARCHAR(255),
  page_level INTEGER DEFAULT 1 NOT NULL,
  page_alias INTEGER REFERENCES project_dashboard_page(page_id),
  page_design TEXT,
  enabled BOOLEAN DEFAULT false NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE project_dashboard_portlet (
  page_portlet_id BIGSERIAL PRIMARY KEY,
  page_id BIGINT REFERENCES project_dashboard_page(page_id) NOT NULL,
  portlet_id BIGINT REFERENCES lookup_project_portlet(portlet_id) NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX proj_dsh_page_prt_pg_idx ON project_dashboard_portlet(page_id);

CREATE TABLE project_dashboard_portlet_prefs (
  preference_id BIGSERIAL PRIMARY KEY,
  page_portlet_id BIGINT REFERENCES project_dashboard_portlet(page_portlet_id) NOT NULL,
  property_name VARCHAR(1024),
  property_value TEXT,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX proj_dsh_pref_pg_prt_idx ON project_dashboard_portlet_prefs(page_portlet_id);
