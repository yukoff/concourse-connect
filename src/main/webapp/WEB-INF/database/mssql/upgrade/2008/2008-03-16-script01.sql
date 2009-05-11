ALTER TABLE project_requirements ADD read_only BIT DEFAULT 0;
UPDATE project_requirements SET read_only = 0;

ALTER TABLE project_assignments ADD responsible VARCHAR(255);

DELETE FROM sync_table;

ALTER TABLE project_ticket_count ADD id INT IDENTITY PRIMARY KEY;

ALTER TABLE ticketlink_project ADD id INT IDENTITY PRIMARY KEY;
