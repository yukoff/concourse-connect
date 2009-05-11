DROP TABLE user_contribution_job;
ALTER TABLE lookup_contribution ADD run_date DATETIME;
ALTER TABLE lookup_contribution ADD points_awarded INTEGER CONSTRAINT NOT NULL DEFAULT 1;

