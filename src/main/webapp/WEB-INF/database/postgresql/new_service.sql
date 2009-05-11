/**
 *  PostgreSQL Table Creation
 *
 *@author     mrajkowski
 *@created    January 29, 2006
 *@version    $Id: new_service.sql 4567 2009-04-28 15:46:41Z matt $
 */

CREATE TABLE sync_client (
  client_id BIGSERIAL PRIMARY KEY,
  type VARCHAR(100),
  version VARCHAR(50),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby INTEGER NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby INTEGER NOT NULL,
  anchor TIMESTAMP(3) DEFAULT NULL,
  enabled BOOLEAN DEFAULT false,
  code VARCHAR(255)
);

CREATE TABLE sync_system (
  system_id BIGSERIAL PRIMARY KEY,
  application_name VARCHAR(255),
  enabled BOOLEAN DEFAULT true
);

CREATE TABLE sync_table (
  table_id SERIAL PRIMARY KEY,
  system_id BIGINT REFERENCES sync_system(system_id) NOT NULL,
  element_name VARCHAR(255),
  mapped_class_name VARCHAR(255),
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  create_statement TEXT,
  order_id INTEGER DEFAULT -1,
  sync_item BOOLEAN DEFAULT false,
  object_key VARCHAR(50)
);


-- INSERT INTO sync_client (type, enteredby, modifiedby) VALUES ('Subversion', 1, 1);
-- INSERT INTO sync_system (application_name) VALUES ('ConcourseConnect API');
-- INSERT INTO sync_table (system_id, element_name, mapped_class_name) VALUES (1, 'htpasswd', 'com.concursive.connect.web.modules.login.dao.HTPasswd');
-- INSERT INTO sync_table (system_id, element_name, mapped_class_name) VALUES (1, 'htpasswdList', 'com.concursive.connect.web.modules.login.dao.HTPasswdList');
-- INSERT INTO sync_table (system_id, element_name, mapped_class_name, object_key) VALUES (1, 'project', 'com.concursive.connect.web.modules.profile.dao.Project', 'id');
-- INSERT INTO sync_table (system_id, element_name, mapped_class_name) VALUES (1, 'projectList', 'com.concursive.connect.web.modules.profile.dao.ProjectList');
-- INSERT INTO sync_table (system_id, element_name, mapped_class_name) VALUES (1, 'teamMember', 'com.concursive.connect.web.modules.members.dao.TeamMember');
-- INSERT INTO sync_table (system_id, element_name, mapped_class_name) VALUES (1, 'userList', 'com.concursive.connect.web.modules.login.dao.UserList');
-- INSERT INTO sync_table (system_id, element_name, mapped_class_name, object_key) VALUES (1, 'projectMessage', 'com.concursive.connect.web.modules.communications.dao.ProjectMessage', 'id');
-- INSERT INTO sync_table (system_id, element_name, mapped_class_name, object_key) VALUES (1, 'messageTemplate', 'com.concursive.connect.web.modules.communications.dao.MessageTemplate', 'id');
-- INSERT INTO sync_table (system_id, element_name, mapped_class_name) VALUES (1, 'messageTemplateList', 'com.concursive.connect.web.modules.communications.dao.MessageTemplateList');
-- INSERT INTO sync_table (system_id, element_name, mapped_class_name) VALUES (1, 'projectInvites', 'com.concursive.connect.web.modules.api.services.ProjectProfileInvitesService');
-- INSERT INTO sync_table (system_id, element_name, mapped_class_name) VALUES (1, 'projectCRM', 'com.concursive.connect.web.modules.tools.dao.ProjectCRM');