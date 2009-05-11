-- Lookup table of contributions
CREATE TABLE lookup_contribution (
	code SERIAL PRIMARY KEY,
	constant VARCHAR(255) NOT NULL,
	description VARCHAR(300) NOT NULL,
	level INTEGER DEFAULT 0,
	enabled BOOLEAN DEFAULT true
);

-- Tracks a user's contribution for each day
CREATE TABLE user_contribution_log (
	record_id BIGSERIAL PRIMARY KEY,
	user_id BIGINT REFERENCES users(user_id) NOT NULL,
	contribution_date TIMESTAMP(3) NOT NULL,
	contribution_id INT REFERENCES lookup_contribution(code) NOT NULL,
	points BIGINT DEFAULT 0 NOT NULL,
	entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP
);

-- Tracks when the job last ran
CREATE TABLE user_contribution_job (
	run_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);

ALTER TABLE users ADD points BIGINT DEFAULT 0 NOT NULL;

-- For reference to use in the future
--CREATE OR REPLACE VIEW user_contribution_by_day AS
--SELECT user_id, date_trunc('day',contribution_date) AS contribution_date, count(points) AS total_points
--FROMÊuser_contribution_log
--GROUP BY user_id, date_trunc('day',contribution_date);

