CREATE TABLE user_email (
  email_id INT IDENTITY PRIMARY KEY,
  user_id INTEGER REFERENCES users(user_id) NOT NULL,
  email VARCHAR(255) NOT NULL
);
CREATE UNIQUE INDEX user_email_uni_idx ON user_email (email);
CREATE INDEX user_email_usr_idx ON user_email(user_id);

ALTER TABLE projects ADD keywords VARCHAR(255);
ALTER TABLE projects ADD profile BIT DEFAULT 0 NOT NULL;
