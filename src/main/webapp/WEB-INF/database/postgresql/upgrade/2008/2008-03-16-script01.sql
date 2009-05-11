-- tested under 8.1.3
ALTER TABLE project_requirements ADD read_only BOOLEAN DEFAULT false NOT NULL;
UPDATE project_requirements SET read_only = false;

ALTER TABLE project_assignments ADD responsible VARCHAR(255);

DELETE FROM sync_table;

ALTER TABLE project_ticket_count ADD id SERIAL PRIMARY KEY;

ALTER TABLE ticketlink_project ADD id SERIAL PRIMARY KEY;
