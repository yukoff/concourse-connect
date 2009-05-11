/**
 *  MSSQL Table Creation
 *
 *@author     a mathur
 *@created    September 2, 2002
 *@version    $Id: new_task.sql 3140 2008-09-08 08:05:04Z lbittner@centriccrm.com $
 */
 
CREATE TABLE lookup_task_priority (
  code INT IDENTITY PRIMARY KEY,
  description VARCHAR(50) NOT NULL,
  default_item BIT DEFAULT 0,
  level INTEGER DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE lookup_task_loe (
  code INT IDENTITY PRIMARY KEY,
  description VARCHAR(50) NOT NULL,
  default_item BIT DEFAULT 0,
  level INTEGER DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE lookup_task_category (
  code INT IDENTITY PRIMARY KEY,
  description VARCHAR(255) NOT NULL,
  default_item BIT DEFAULT 0,
  level INTEGER DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE lookup_task_functional_area (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE lookup_task_status (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE lookup_task_value (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE lookup_task_complexity (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE lookup_task_release (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE lookup_task_sprint (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE lookup_task_loe_remaining (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE lookup_task_assigned_to (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  user_id INTEGER NOT NULL REFERENCES users(user_id),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE lookup_task_assigned_priority (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE task (
  task_id INT IDENTITY PRIMARY KEY,
  entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  enteredby INT NOT NULL REFERENCES users(user_id),
  priority INTEGER NOT NULL REFERENCES lookup_task_priority,
  description VARCHAR(255),
  duedate DATETIME,
  reminderid INT,
  notes TEXT,
  sharing INT NOT NULL,
  complete BIT DEFAULT 0 NOT NULL,
  enabled BIT DEFAULT 0 NOT NULL,
  modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modifiedby INT REFERENCES users(user_id),
  estimatedloe FLOAT,
  estimatedloetype INTEGER REFERENCES lookup_task_loe,
  owner INTEGER REFERENCES users(user_id),
  completedate DATETIME,
  category_id INTEGER REFERENCES lookup_task_category,
  rating_count INTEGER DEFAULT 0 NOT NULL,
  rating_value INTEGER DEFAULT 0 NOT NULL,
  rating_avg FLOAT DEFAULT 0 NOT NULL,
  functional_area INTEGER REFERENCES lookup_task_functional_area,
  status INTEGER REFERENCES lookup_task_status,
  business_value INTEGER REFERENCES lookup_task_value,
  complexity INTEGER REFERENCES lookup_task_complexity,
  target_release INTEGER REFERENCES lookup_task_release,
  target_sprint INTEGER REFERENCES lookup_task_sprint,
  loe_remaining INTEGER REFERENCES lookup_task_loe_remaining,
  assigned_to INTEGER REFERENCES lookup_task_assigned_to,
  assigned_priority INTEGER REFERENCES lookup_task_assigned_priority,
  link_module_id INTEGER,
  link_item_id INTEGER
);
CREATE INDEX task_catid_idx ON task(category_id);
CREATE INDEX task_trelid_idx ON task(target_release);
CREATE INDEX task_statusid_idx ON task(status);
CREATE INDEX task_lm_idx ON task(link_module_id);
CREATE INDEX task_li_idx ON task(link_item_id);

CREATE TABLE tasklog (
  id INT IDENTITY PRIMARY KEY,
  task_id INT REFERENCES task(task_id),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby INT REFERENCES users(user_id) NOT NULL,
  modified DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby INT REFERENCES users(user_id) NOT NULL,
  priority INTEGER REFERENCES lookup_task_priority NOT NULL,
  duedate DATETIME,
  complete BOOLEAN DEFAULT 0 NOT NULL,
  estimatedloe FLOAT,
  estimatedloetype INTEGER REFERENCES lookup_task_loe,
  owner INTEGER REFERENCES users(user_id),
  completedate DATETIME,
  category_id INTEGER REFERENCES lookup_task_category,
  functional_area INTEGER REFERENCES lookup_task_functional_area,
  status INTEGER REFERENCES lookup_task_status,
  business_value INTEGER REFERENCES lookup_task_value,
  complexity INTEGER REFERENCES lookup_task_complexity,
  target_release INTEGER REFERENCES lookup_task_release,
  target_sprint INTEGER REFERENCES lookup_task_sprint,
  loe_remaining INTEGER REFERENCES lookup_task_loe_remaining,
  assigned_to INTEGER REFERENCES lookup_task_assigned_to,
  assigned_priority INTEGER REFERENCES lookup_task_assigned_priority
);
CREATE INDEX tasklog_taskid_idx ON tasklog(task_id);


CREATE TABLE tasklink_contact (
  task_id INT NOT NULL REFERENCES task,
  contact_id INT NOT NULL,
  notes TEXT
);

CREATE TABLE tasklink_ticket (
  task_id INT NOT NULL REFERENCES task,
  ticket_id INT NOT NULL
);

CREATE TABLE tasklink_project (
  task_id INT NOT NULL REFERENCES task,
  project_id INT NOT NULL REFERENCES projects(project_id)
);

CREATE TABLE taskcategory_project (
  category_id INTEGER NOT NULL REFERENCES lookup_task_category,
  project_id INTEGER NOT NULL REFERENCES projects(project_id)
);

CREATE TABLE taskcategorylink_news (
  news_id INTEGER NOT NULL REFERENCES project_news(news_id),
  category_id INTEGER NOT NULL REFERENCES lookup_task_category
);

CREATE TABLE task_rating (
  rating_id INT IDENTITY PRIMARY KEY,
  task_id INTEGER NOT NULL REFERENCES task(task_id),
  rating INTEGER NOT NULL,
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  enteredby INTEGER NOT NULL REFERENCES users(user_id),
  project_id INTEGER REFERENCES projects(project_id)
);
CREATE UNIQUE INDEX task_rating_uni_idx ON task_rating (task_id, enteredby);
CREATE INDEX project_task_rtg_idx on task_rating(task_id);
CREATE INDEX project_task_rtge_idx on task_rating(enteredby);
