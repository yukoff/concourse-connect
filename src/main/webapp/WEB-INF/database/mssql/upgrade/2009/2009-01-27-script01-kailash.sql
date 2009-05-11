-- Lookup table of contributions
CREATE TABLE lookup_contribution (
	code INT IDENTITY PRIMARY KEY,
	constant VARCHAR(255) NOT NULL,
	description VARCHAR(300) NOT NULL,
	level INTEGER DEFAULT 0,
	enabled BIT DEFAULT 1
);

-- Tracks a user's contribution for each day
CREATE TABLE user_contribution_log (
	record_id INT IDENTITY PRIMARY KEY,
	user_id INTEGER REFERENCES users(user_id) NOT NULL,
	contribution_date DATETIME NOT NULL,
	contribution_id INTEGER REFERENCES lookup_contribution(code) NOT NULL,
	points INTEGER DEFAULT 0 NOT NULL,
	entered DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tracks when the job last ran
CREATE TABLE user_contribution_job (
  run_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL
);

ALTER TABLE users ADD points INTEGER DEFAULT 0 NOT NULL;
