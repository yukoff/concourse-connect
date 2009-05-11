/**
 *  PostgreSQL Table Creation
 *
 *@author     matt rajkowski
 *@created
 */
 
CREATE TABLE groups (
  group_id SERIAL PRIMARY KEY,
  group_name VARCHAR(50) NOT NULL,
  enabled BOOLEAN DEFAULT false NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  expiration TIMESTAMP(3) DEFAULT NULL,
  account_size INTEGER DEFAULT NULL
);

CREATE INDEX "groups_idx" ON "groups" USING btree ("group_id");

CREATE TABLE departments (
  code SERIAL PRIMARY KEY,
  group_id INTEGER REFERENCES groups(group_id) NOT NULL,
  description VARCHAR(100) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT false NOT NULL
);

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

CREATE TABLE users (
  user_id BIGSERIAL PRIMARY KEY,
  group_id INTEGER REFERENCES groups(group_id) NOT NULL,
  department_id INTEGER REFERENCES departments(code),
  first_name VARCHAR(50),
  last_name VARCHAR(50),
  username VARCHAR(255) NOT NULL,
  password VARCHAR(50) NOT NULL,
  temporary_password VARCHAR(50),
  company VARCHAR(50),
  email VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT false NOT NULL,
  start_page INTEGER DEFAULT 1 NOT NULL,
  access_personal BOOLEAN DEFAULT false NOT NULL,
  access_enterprise BOOLEAN DEFAULT false NOT NULL,
  access_admin BOOLEAN DEFAULT false NOT NULL,
  access_invite BOOLEAN DEFAULT false NOT NULL,
  access_settings BOOLEAN DEFAULT true NOT NULL,
  access_guest BOOLEAN DEFAULT true NOT NULL,
  access_inbox BOOLEAN DEFAULT false NOT NULL,
  access_resources BOOLEAN DEFAULT false NOT NULL,
  last_login TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  expiration TIMESTAMP(3),
  registered BOOLEAN DEFAULT false NOT NULL,
  account_size INTEGER,
  terms BOOLEAN DEFAULT false NOT NULL,
  timezone VARCHAR(50),
  currency VARCHAR(5),
  language VARCHAR(20),
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby INTEGER DEFAULT 0,
  account_type INTEGER,
  account_type_starts TIMESTAMP(3),
  account_type_ends TIMESTAMP(3),
  access_add_projects BOOLEAN DEFAULT false NOT NULL,
  webdav_access BOOLEAN DEFAULT false NOT NULL,
  webdav_password VARCHAR(255),
  htpasswd VARCHAR(255),
  htpasswd_date TIMESTAMP(3),
  access_contacts_view_all BOOLEAN DEFAULT false NOT NULL,
  access_contacts_edit_all BOOLEAN DEFAULT false NOT NULL,
  watch_forums BOOLEAN DEFAULT false NOT NULL,
  nickname VARCHAR(255),
  salutation INTEGER REFERENCES lookup_title(code),
  profile_project_id INTEGER DEFAULT -1,
  show_profile_to INTEGER DEFAULT 0,
  show_fullname_to INTEGER DEFAULT 0,
  show_email_to INTEGER DEFAULT 0,
  show_gender_to INTEGER DEFAULT 0,
  show_location_to INTEGER DEFAULT 0,
  show_company_to INTEGER DEFAULT 0,
  points BIGINT DEFAULT 0 NOT NULL
);
CREATE UNIQUE INDEX users_uni_idx ON users (email);

CREATE TABLE user_groups (
  user_id BIGINT REFERENCES users(user_id) NOT NULL,
  group_id INTEGER REFERENCES groups(group_id) NOT NULL
);

CREATE TABLE user_log (
  user_id BIGINT REFERENCES users(user_id) NOT NULL,
  log_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  browser VARCHAR(255),
  ip_address VARCHAR(40) NOT NULL
);

CREATE TABLE user_email (
  email_id BIGSERIAL,
  user_id BIGINT REFERENCES users(user_id) NOT NULL,
  email VARCHAR(255) NOT NULL
);
CREATE UNIQUE INDEX user_email_uni_idx ON user_email (email);
CREATE INDEX user_email_usr_idx ON user_email(user_id);

CREATE TABLE user_tag_log (
  user_id BIGINT REFERENCES users(user_id) NOT NULL,
  link_module_id INTEGER NOT NULL,
  link_item_id BIGINT NOT NULL,
  tag VARCHAR(255) NOT NULL,
  tag_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX user_tag_log_idx ON user_tag_log(user_id);
CREATE INDEX user_tag_log_lm_idx ON user_tag_log(link_module_id);
CREATE INDEX user_tag_log_li_idx ON user_tag_log(link_item_id);
CREATE INDEX user_tag_log_tag_idx ON user_tag_log(tag);

CREATE OR REPLACE VIEW user_tag AS
SELECT user_id, tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
GROUP BY user_id, tag;

CREATE OR REPLACE VIEW unique_tag AS
SELECT tag, count(*) AS tag_count, max(tag_date) AS tag_date
FROM user_tag_log
GROUP BY tag;

CREATE TABLE usage_log (
  usage_id BIGSERIAL PRIMARY KEY,
  entered TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby INTEGER,
  action INTEGER NOT NULL,
  record_id INTEGER,
  record_size INTEGER
);

CREATE TABLE database_version (
  version_id SERIAL PRIMARY KEY,
  script_filename VARCHAR(255) NOT NULL,
  script_version VARCHAR(255) NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE contact_us (
  request_id BIGSERIAL PRIMARY KEY,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  email VARCHAR(255) NOT NULL,
  organization VARCHAR(100) NULL,
  description TEXT NOT NULL,
  copied BOOLEAN DEFAULT false NOT NULL,
  browser VARCHAR(255) NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id),
  replied TIMESTAMP(3) NULL,
  repliedby BIGINT REFERENCES users(user_id),
  language VARCHAR(255),
  ip_address VARCHAR(40) NOT NULL,
  job_title VARCHAR(255),
  business_phone VARCHAR(30),
  business_phone_ext VARCHAR(30),
  addrline1 VARCHAR(255),
  addrline2 VARCHAR(255),
  addrline3 VARCHAR(255),
  city VARCHAR(255),
  state VARCHAR(255),
  country VARCHAR(255),
  postalcode VARCHAR(255),
  form_data TEXT
);

CREATE TABLE lookup_number (
  id INTEGER NOT NULL
);

CREATE TABLE contacts (
  contact_id BIGSERIAL PRIMARY KEY,
  is_organization BOOLEAN DEFAULT false NOT NULL,
  salutation VARCHAR(80),
  first_name VARCHAR(80),
  middle_name VARCHAR(80),
  last_name VARCHAR(80),
  suffix_name VARCHAR(80),
  organization VARCHAR(255),
  file_as VARCHAR(512),
  job_title VARCHAR(255),
  role VARCHAR(255),
  email1 VARCHAR(255),
  email2 VARCHAR(255),
  email3 VARCHAR(255),
  home_phone VARCHAR(30),
  home_phone_ext VARCHAR(30),
  home2_phone VARCHAR(30),
  home2_phone_ext VARCHAR(30),
  home_fax VARCHAR(30),
  business_phone VARCHAR(30),
  business_phone_ext VARCHAR(30),
  business2_phone VARCHAR(30),
  business2_phone_ext VARCHAR(30),
  business_fax VARCHAR(30),
  mobile_phone VARCHAR(30),
  pager_number VARCHAR(30),
  car_phone VARCHAR(30),
  radio_phone VARCHAR(30),
  web_page VARCHAR(255),
  nickname VARCHAR(80),
  comments TEXT,
  owner BIGINT REFERENCES users(user_id),
  global BOOLEAN DEFAULT false NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  addrline1 VARCHAR(255),
  addrline2 VARCHAR(255),
  addrline3 VARCHAR(255),
  city VARCHAR(255),
  state VARCHAR(255),
  country VARCHAR(255),
  postalcode VARCHAR(12),
  latitude FLOAT DEFAULT 0,
  longitude FLOAT DEFAULT 0
);

CREATE TABLE contacts_share (
  share_id BIGSERIAL PRIMARY KEY,
  contact_id BIGINT REFERENCES contacts(contact_id),
  shared_from BIGINT REFERENCES users(user_id),
  shared_to BIGINT REFERENCES users(user_id),
  allow_edit BOOLEAN DEFAULT false NOT NULL,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE lookup_authentication_classes (
  code BIGSERIAL PRIMARY KEY,
  login_mode VARCHAR(300),
  login_authenticator VARCHAR(300),
  session_validator VARCHAR(300),
  enabled BOOLEAN DEFAULT false,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE lookup_service (
  code SERIAL PRIMARY KEY,
  description VARCHAR(300) NOT NULL,
  default_item BOOLEAN DEFAULT false,
  level INTEGER DEFAULT 0,
  enabled BOOLEAN DEFAULT true,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Lookup table of contributions
CREATE TABLE lookup_contribution (
	code SERIAL PRIMARY KEY,
	constant VARCHAR(255) NOT NULL,
	description VARCHAR(300) NOT NULL,
	level INTEGER DEFAULT 0,
	enabled BOOLEAN DEFAULT true,
	run_date TIMESTAMP(3),
	points_awarded INTEGER DEFAULT 1 NOT NULL 
);

