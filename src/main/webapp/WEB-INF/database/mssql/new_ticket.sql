/**
 *  MSSQL Table Creation
 *
 *@author     matt rajkowski
 *@created    March 25, 2003
 *@version    $Id: new_ticket.sql 4004 2009-02-18 13:17:09Z kailash $
 */
 
CREATE TABLE ticket_level (
  code INT IDENTITY PRIMARY KEY,
  description VARCHAR(300) NOT NULL UNIQUE,
  default_item BIT DEFAULT 0,
  level INT DEFAULT 0,
  enabled BIT DEFAULT 1
);


CREATE TABLE ticket_severity (
  code INT IDENTITY PRIMARY KEY,
  description VARCHAR(300) NOT NULL UNIQUE,
  style text NOT NULL DEFAULT '',
  default_item BIT DEFAULT 0,
  level INTEGER DEFAULT 0,
  enabled BIT DEFAULT 1
);


CREATE TABLE lookup_ticketsource (
  code INT IDENTITY PRIMARY KEY,
  description VARCHAR(300) NOT NULL UNIQUE,
  default_item BIT DEFAULT 0,
  level INTEGER DEFAULT 0,
  enabled BIT DEFAULT 1
);


CREATE TABLE lookup_ticket_status (
  code INT IDENTITY PRIMARY KEY,
  description VARCHAR(300) NOT NULL UNIQUE,
  default_item BIT DEFAULT 0,
  level INTEGER DEFAULT 0,
  enabled BIT DEFAULT 1
);


CREATE TABLE ticket_priority (
  code INT IDENTITY PRIMARY KEY,
  description VARCHAR(300) NOT NULL UNIQUE,
  style text NOT NULL DEFAULT '',
  default_item BIT DEFAULT 0,
  level INTEGER DEFAULT 0,
  enabled BIT DEFAULT 1
);


CREATE TABLE ticket_category ( 
  id INT IDENTITY PRIMARY KEY,
  cat_level int  NOT NULL DEFAULT 0,
  parent_cat_code int  NOT NULL,
  description VARCHAR(300) NOT NULL,
  full_description text NOT NULL DEFAULT '',
  default_item BIT DEFAULT 0,
  level INTEGER DEFAULT 0,
  enabled BIT DEFAULT 1,
  project_id INT REFERENCES projects(project_id)
);

CREATE TABLE ticket_cause (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE ticket_resolution (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE ticket_defect (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE ticket_escalation (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE ticket_state (
  code INT IDENTITY PRIMARY KEY,
  project_id INTEGER NOT NULL REFERENCES projects(project_id),
  item_name VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP,
  level INTEGER NOT NULL DEFAULT 0,
  enabled BIT DEFAULT 1
);

CREATE TABLE ticket (
  ticketid INT IDENTITY PRIMARY KEY,
  org_id INT, 
  contact_id INT REFERENCES users(user_id), 
  problem TEXT NOT NULL,
  entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  enteredby INT NOT NULL REFERENCES users(user_id),
  modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modifiedby INT NOT NULL REFERENCES users(user_id),
  closed DATETIME,
  pri_code INT REFERENCES ticket_priority(code), 
  level_code INT REFERENCES ticket_level(code),
  department_code INT REFERENCES departments(code),
  source_code INT REFERENCES lookup_ticketsource(code), 
  cat_code INT,
  subcat_code1 INT,
  subcat_code2 INT,
  subcat_code3 INT,
  assigned_to INT REFERENCES users(user_id),
  comment TEXT,
  solution TEXT,
  scode INT REFERENCES ticket_severity(code),
  critical DATETIME,
  notified DATETIME,
  custom_data TEXT,
  location VARCHAR(256),
  assigned_date DATETIME,
  est_resolution_date DATETIME,
  resolution_date DATETIME,
  cause TEXT,
  key_count INT NOT NULL,
  status_id INTEGER REFERENCES lookup_ticket_status(code),
  cause_id INTEGER REFERENCES ticket_cause(code),
  resolution_id INTEGER REFERENCES ticket_resolution(code),
  defect_id INTEGER REFERENCES ticket_defect(code),
  escalation_id INTEGER REFERENCES ticket_escalation(code),
  state_id INTEGER REFERENCES ticket_state(code),
  related_id INTEGER,
  ready_for_close BIT DEFAULT 0 NOT NULL,
  read_count INTEGER DEFAULT 0 NOT NULL,
  read_date DATETIME,
  link_project_id INTEGER REFERENCES projects(project_id),
  link_module_id INTEGER,
  link_item_id INTEGER
);

CREATE INDEX "ticket_cidx" ON "ticket" ("assigned_to", "closed");
CREATE INDEX "ticketlist_entered" ON "ticket" (entered);
CREATE INDEX ticketlist_assigned ON ticket (assigned_to);
CREATE INDEX ticketlist_closed ON ticket (closed);

CREATE TABLE project_ticket_count (
  id INT IDENTITY PRIMARY KEY,
  project_id INT UNIQUE NOT NULL REFERENCES projects(project_id),
  key_count INT NOT NULL DEFAULT 0
);

CREATE TABLE ticket_view (
  ticketid INTEGER NOT NULL REFERENCES ticket(ticketid),
  user_id INTEGER NULL REFERENCES users(user_id),
  view_date DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL
);
CREATE INDEX ticket_vw_idx on ticket_view(ticketid);


CREATE TABLE ticketlog (
  id INT IDENTITY PRIMARY KEY,
  ticketid INT REFERENCES ticket(ticketid),
  assigned_to INT REFERENCES users(user_id),
  comment TEXT,
  closed BIT,
  pri_code INT REFERENCES ticket_priority(code),
  level_code INT,
  department_code INT REFERENCES departments(code),
  cat_code INT,
  scode INT REFERENCES ticket_severity(code),
  entered DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  enteredby INT NOT NULL REFERENCES users(user_id),
  modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modifiedby INT NOT NULL REFERENCES users(user_id)
);

CREATE TABLE ticketlink_project (
  id INT IDENTITY PRIMARY KEY,
  ticket_id INT NOT NULL REFERENCES ticket(ticketid),
  project_id INT NOT NULL REFERENCES projects(project_id)
);
CREATE INDEX ticketlink_project_idx ON ticketlink_project(ticket_id);
CREATE INDEX ticketlink_projectid_idx ON ticketlink_project(project_id);

CREATE TABLE ticket_contacts (
  id INT IDENTITY PRIMARY KEY,
  ticketid INT REFERENCES ticket(ticketid),
  user_id INT REFERENCES users(user_id),
  contact_id INT REFERENCES contacts(contact_id),
  contact_name VARCHAR(255),
  contact_email VARCHAR(255),
  entered DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby INT REFERENCES users(user_id) NOT NULL
);
