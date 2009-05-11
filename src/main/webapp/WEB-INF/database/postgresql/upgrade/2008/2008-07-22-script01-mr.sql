CREATE INDEX user_tag_log_lm_idx ON user_tag_log(link_module_id);
CREATE INDEX user_tag_log_li_idx ON user_tag_log(link_item_id);
CREATE INDEX user_tag_log_tag_idx ON user_tag_log(tag);

ALTER TABLE project_team ADD tools BOOLEAN DEFAULT false NOT NULL;

CREATE TABLE lookup_title (
  code SERIAL PRIMARY KEY,
  description VARCHAR(300) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  gender VARCHAR(1)
);

ALTER TABLE users ADD salutation INTEGER REFERENCES lookup_title(code);
ALTER TABLE users ADD profile_project_id INTEGER DEFAULT -1;
ALTER TABLE users ADD show_profile_to INTEGER DEFAULT 0;
ALTER TABLE users ADD show_fullname_to INTEGER DEFAULT 0;
ALTER TABLE users ADD show_email_to INTEGER DEFAULT 0;
ALTER TABLE users ADD show_gender_to INTEGER DEFAULT 0;
ALTER TABLE users ADD show_location_to INTEGER DEFAULT 0;
ALTER TABLE users ADD show_company_to INTEGER DEFAULT 0;
