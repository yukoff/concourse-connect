
CREATE TABLE lookup_project_language (
  id SERIAL PRIMARY KEY,
  language_name VARCHAR(200) NOT NULL,
  language_locale VARCHAR(12),
  entered TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  enabled BOOLEAN DEFAULT true NOT NULL,
  default_item BOOLEAN DEFAULT false NOT NULL
);
CREATE INDEX look_proj_lang_lan_nm ON lookup_project_language(language_name);
CREATE INDEX look_proj_lang_lan_lo ON lookup_project_language(language_locale);

CREATE TABLE project_language_team (
  id SERIAL PRIMARY KEY,
  member_id INTEGER REFERENCES users(user_id) NOT NULL,
  language_id INTEGER REFERENCES lookup_project_language NOT NULL,
  entered TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX proj_lan_team_mem_id ON project_language_team(member_id);
CREATE INDEX proj_lan_team_lan_id ON project_language_team(language_id);

ALTER TABLE projects ADD language_id INT REFERENCES lookup_project_language;
