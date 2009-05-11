DROP TABLE user_contribution_job;
ALTER TABLE lookup_contribution ADD run_date TIMESTAMP(3);
ALTER TABLE lookup_contribution ADD COLUMN points_awarded INTEGER NOT NULL;
ALTER TABLE lookup_contribution ALTER COLUMN points_awarded SET DEFAULT 1;