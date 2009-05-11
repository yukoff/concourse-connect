ALTER TABLE project_dashboard_page DROP COLUMN group_id;

DROP TABLE project_dashboard_group;

DELETE FROM project_dashboard_portlet_prefs;
DELETE FROM project_dashboard_portlet;
DELETE FROM project_dashboard_page;
DELETE FROM lookup_project_portlet;

ALTER TABLE project_dashboard_page ADD dashboard_id INTEGER REFERENCES project_dashboard(dashboard_id) NOT NULL;

CREATE INDEX task_trelid_idx ON task(target_release);
CREATE INDEX task_statusid_idx ON task(status);
