ALTER TABLE contacts ADD latitude FLOAT DEFAULT 0;
ALTER TABLE contacts ADD longitude FLOAT DEFAULT 0;

ALTER TABLE lookup_project_category ADD logo_id INTEGER REFERENCES project_files(item_id);

ALTER TABLE projects ADD logo_id INTEGER REFERENCES project_files(item_id);
ALTER TABLE projects ADD dashboard_description VARCHAR(255);
ALTER TABLE projects ADD news_description VARCHAR(255);
ALTER TABLE projects ADD calendar_description VARCHAR(255);
ALTER TABLE projects ADD wiki_description VARCHAR(255);
ALTER TABLE projects ADD discussion_description VARCHAR(255);
ALTER TABLE projects ADD documents_description VARCHAR(255);
ALTER TABLE projects ADD lists_description VARCHAR(255);
ALTER TABLE projects ADD plan_description VARCHAR(255);
ALTER TABLE projects ADD tickets_description VARCHAR(255);
ALTER TABLE projects ADD team_description VARCHAR(255);
ALTER TABLE projects ADD concursive_crm_url VARCHAR(255);
ALTER TABLE projects ADD concursive_crm_domain VARCHAR(255);
ALTER TABLE projects ADD concursive_crm_code VARCHAR(255);
ALTER TABLE projects ADD concursive_crm_client VARCHAR(255);
ALTER TABLE projects ADD email1 VARCHAR(255);
ALTER TABLE projects ADD email2 VARCHAR(255);
ALTER TABLE projects ADD email3 VARCHAR(255);
ALTER TABLE projects ADD home_phone VARCHAR(30);
ALTER TABLE projects ADD home_phone_ext VARCHAR(30);
ALTER TABLE projects ADD home2_phone VARCHAR(30);
ALTER TABLE projects ADD home2_phone_ext VARCHAR(30);
ALTER TABLE projects ADD home_fax VARCHAR(30);
ALTER TABLE projects ADD business_phone VARCHAR(30);
ALTER TABLE projects ADD business_phone_ext VARCHAR(30);
ALTER TABLE projects ADD business2_phone VARCHAR(30);
ALTER TABLE projects ADD business2_phone_ext VARCHAR(30);
ALTER TABLE projects ADD business_fax VARCHAR(30);
ALTER TABLE projects ADD mobile_phone VARCHAR(30);
ALTER TABLE projects ADD pager_number VARCHAR(30);
ALTER TABLE projects ADD car_phone VARCHAR(30);
ALTER TABLE projects ADD radio_phone VARCHAR(30);
ALTER TABLE projects ADD web_page VARCHAR(255);
ALTER TABLE projects ADD address_to VARCHAR(255);
ALTER TABLE projects ADD addrline1 VARCHAR(255);
ALTER TABLE projects ADD addrline2 VARCHAR(255);
ALTER TABLE projects ADD addrline3 VARCHAR(255);
ALTER TABLE projects ADD city VARCHAR(255);
ALTER TABLE projects ADD state VARCHAR(255);
ALTER TABLE projects ADD country VARCHAR(255);
ALTER TABLE projects ADD postalcode VARCHAR(255);
ALTER TABLE projects ADD latitude FLOAT DEFAULT 0;
ALTER TABLE projects ADD longitude FLOAT DEFAULT 0;

CREATE TABLE projects_comment (
  comment_id INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  comment TEXT NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  modified DATETIME DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER REFERENCES users(user_id) NOT NULL,
  closed DATETIME,
  closedby INTEGER REFERENCES users(user_id)
);
CREATE INDEX projects_comment_prj_idx ON projects_comment(project_id);

DROP INDEX prj_files_com_itid;
DROP TABLE project_files_comments;

CREATE TABLE project_files_comment (
  comment_id INT IDENTITY PRIMARY KEY,
  item_id INTEGER NOT NULL REFERENCES project_files(item_id),
  comment TEXT NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER REFERENCES users(user_id) NOT NULL,
  modified DATETIME DEFAULT CURRENT_TIMESTAMP,
  modifiedby INTEGER REFERENCES users(user_id) NOT NULL,
  closed DATETIME,
  closedby INTEGER REFERENCES users(user_id)
);
CREATE INDEX project_files_comment_item_idx ON project_files_comment(item_id);

CREATE TABLE project_files_rating (
  item_id INTEGER NOT NULL REFERENCES project_files(item_id),
  rating INTEGER NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id)
);
CREATE INDEX project_files_rtg_idx ON project_files_rating(item_id);
CREATE INDEX project_files_rtg_rtg_idx ON project_files_rating(rating);

CREATE TABLE project_wiki_template (
  template_id INT IDENTITY PRIMARY KEY,
  project_category_id INTEGER REFERENCES lookup_project_category(code),
  title VARCHAR(255) NOT NULL,
  content TEXT NOT NULL,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP
);
