/**
 *  MSSQL Table Creation
 *
 *@author     matt rajkowski
 *@created
 */
 
CREATE TABLE instances (
  instance_id INT IDENTITY PRIMARY KEY,
  domain_name VARCHAR(255) NOT NULL,
  context VARCHAR(255) NOT NULL DEFAULT '/',
  enabled BIT DEFAULT 0 NOT NULL,
  title VARCHAR(255) NOT NULL 
);
CREATE UNIQUE INDEX instances_uni_idx ON instances (domain_name, context);

CREATE TABLE groups (
  group_id INT IDENTITY PRIMARY KEY ,
  group_name VARCHAR(50) NOT NULL ,
  enabled BIT DEFAULT 0 NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  modified DATETIME DEFAULT CURRENT_TIMESTAMP,
  expiration DATETIME DEFAULT NULL,
  account_size INTEGER DEFAULT NULL
);

CREATE INDEX "groups_idx" ON "groups" ("group_id");

CREATE TABLE departments (
  code INT IDENTITY PRIMARY KEY,
  group_id INTEGER REFERENCES groups(group_id) NOT NULL,
  description VARCHAR(100) NOT NULL,
  default_item BIT DEFAULT 0,
  level INTEGER DEFAULT 0,
  enabled BIT DEFAULT 0 NOT NULL
);

CREATE TABLE lookup_title (
  code INT IDENTITY PRIMARY KEY,
  description VARCHAR(300) NOT NULL,
  default_item BIT DEFAULT 0,
  level INTEGER DEFAULT 0,
  enabled BIT DEFAULT 1,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modified DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  gender VARCHAR(1)
);

CREATE TABLE users (
  user_id INT IDENTITY PRIMARY KEY,
  group_id INTEGER REFERENCES groups(group_id) NOT NULL,
  department_id INTEGER REFERENCES departments(code),
  first_name VARCHAR(50),
  last_name VARCHAR(50),
  username VARCHAR(255) NOT NULL,
  password VARCHAR(50) NOT NULL,
  temporary_password VARCHAR(50),
  company VARCHAR(50),
  email VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ,
  enteredby INTEGER DEFAULT 0,
  enabled BIT DEFAULT 0 NOT NULL,
  start_page INTEGER DEFAULT 1 NOT NULL,
  access_personal BIT DEFAULT 0 NOT NULL,
  access_enterprise BIT DEFAULT 0 NOT NULL,
  access_admin BIT DEFAULT 0 NOT NULL,
  access_invite BIT DEFAULT 0 NOT NULL,
  access_settings BIT DEFAULT 1 NOT NULL,
  access_guest BIT DEFAULT 1 NOT NULL,
  access_inbox BIT DEFAULT 0 NOT NULL,
  access_resources BIT DEFAULT 0 NOT NULL,
  last_login DATETIME DEFAULT CURRENT_TIMESTAMP,
  expiration DATETIME,
  registered BIT DEFAULT 0 NOT NULL,
  account_size INTEGER,
  terms BIT DEFAULT 0 NOT NULL,
  timezone VARCHAR(50),
  currency VARCHAR(5),
  language VARCHAR(20),
  modified DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby INTEGER DEFAULT 0,
  account_type INTEGER,
  account_type_starts DATETIME,
  account_type_ends DATETIME,
  access_add_projects BIT DEFAULT 0 NOT NULL,
  webdav_access BIT DEFAULT 0 NOT NULL,
  webdav_password VARCHAR(255),
  htpasswd VARCHAR(255),
  htpasswd_date DATETIME,
  access_contacts_view_all BIT DEFAULT 0 NOT NULL,
  access_contacts_edit_all BIT DEFAULT 0 NOT NULL,
  watch_forums BIT DEFAULT 0 NOT NULL,
  nickname VARCHAR(255),
  salutation INTEGER REFERENCES lookup_title(code),
  profile_project_id INTEGER DEFAULT -1,
  show_profile_to INTEGER DEFAULT 0,
  show_fullname_to INTEGER DEFAULT 0,
  show_email_to INTEGER DEFAULT 0,
  show_gender_to INTEGER DEFAULT 0,
  show_location_to INTEGER DEFAULT 0,
  show_company_to INTEGER DEFAULT 0,
  points INTEGER DEFAULT 0 NOT NULL,
  instance_id INTEGER REFERENCES instances(instance_id)
);
CREATE UNIQUE INDEX users_uni_idx ON users (email);

CREATE TABLE user_groups (
  user_id INTEGER REFERENCES users(user_id) NOT NULL,
  group_id INTEGER REFERENCES groups(group_id) NOT NULL
);

CREATE TABLE user_log (
  user_id INTEGER REFERENCES users(user_id) NOT NULL,
  log_date DATETIME DEFAULT CURRENT_TIMESTAMP,
  browser VARCHAR(255),
  ip_address VARCHAR(40) NOT NULL
);

CREATE TABLE user_email (
  email_id INT IDENTITY PRIMARY KEY,
  user_id INTEGER REFERENCES users(user_id) NOT NULL,
  email VARCHAR(255) NOT NULL
);
CREATE UNIQUE INDEX user_email_uni_idx ON user_email (email);
CREATE INDEX user_email_usr_idx ON user_email(user_id);

CREATE TABLE user_tag_log (
  user_id INTEGER NOT NULL REFERENCES users(user_id),
  link_module_id INTEGER NOT NULL,
  link_item_id INTEGER NOT NULL,
  tag VARCHAR(255) NOT NULL,
  tag_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL
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
  usage_id INT IDENTITY PRIMARY KEY,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby INTEGER,
  action INTEGER NOT NULL,
  record_id INTEGER,
  record_size INTEGER
);

CREATE TABLE database_version (
  version_id INT IDENTITY PRIMARY KEY,
  script_filename VARCHAR(255) NOT NULL,
  script_version VARCHAR(255) NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE contact_us (
  request_id INT IDENTITY PRIMARY KEY,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  email VARCHAR(255) NOT NULL,
  organization VARCHAR(100) NULL,
  description TEXT NOT NULL,
  copied BIT DEFAULT 0 NOT NULL,
  browser VARCHAR(255) NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby INTEGER REFERENCES users(user_id),
  replied DATETIME NULL,
  repliedby INTEGER REFERENCES users(user_id),
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
  form_data TEXT,
  instance_id INTEGER REFERENCES instances(instance_id)
);

CREATE TABLE lookup_number (
  id INTEGER NOT NULL
);

CREATE TABLE contacts (
  contact_id INT IDENTITY PRIMARY KEY,
  is_organization BIT DEFAULT 0 NOT NULL,
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
  owner INTEGER REFERENCES users(user_id),
  global BIT DEFAULT 0 NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  modified DATETIME DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER REFERENCES users(user_id) NOT NULL,
  addrline1 VARCHAR(255),
  addrline2 VARCHAR(255),
  addrline3 VARCHAR(255),
  city VARCHAR(255),
  state VARCHAR(255),
  country VARCHAR(255),
  postalcode VARCHAR(255),
  latitude FLOAT DEFAULT 0,
  longitude FLOAT DEFAULT 0
);

CREATE TABLE contacts_share (
  share_id INT IDENTITY PRIMARY KEY,
  contact_id INTEGER REFERENCES contacts(contact_id),
  shared_from INTEGER REFERENCES users(user_id),
  shared_to INTEGER REFERENCES users(user_id),
  allow_edit BIT DEFAULT 0 NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  modified DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE lookup_authentication_classes (
  code INT IDENTITY PRIMARY KEY,
  login_mode VARCHAR(300),
  login_authenticator VARCHAR(300),
  session_validator VARCHAR(300),
  enabled BIT DEFAULT 0,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  modified DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE lookup_service (
  code INT IDENTITY PRIMARY KEY,
  description VARCHAR(300) NOT NULL,
  default_item BIT DEFAULT 0,
  level INTEGER DEFAULT 0,
  enabled BIT DEFAULT 1,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modified DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Lookup table of contributions
CREATE TABLE lookup_contribution (
	code INT IDENTITY PRIMARY KEY,
	constant VARCHAR(255) NOT NULL,
	description VARCHAR(300) NOT NULL,
	level INTEGER DEFAULT 0,
	enabled BIT DEFAULT 1,
	run_date DATETIME,
	points_awarded INTEGER DEFAULT 1 NOT NULL
);

