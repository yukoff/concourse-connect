/*
 *   Postgresql
 *   $Id Exp$
 *
 *   Table: task
 *   Sequence: task_task_id_seq
 */

CREATE TABLE task (
  task_id BIGSERIAL PRIMARY KEY,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby INTEGER DEFAULT -1,
  modified TIMESTAMP(3),
  modifiedby INTEGER DEFAULT -1,
  owner INTEGER DEFAULT -1,
  enabled BOOLEAN DEFAULT false,
  subject VARCHAR(255),
  categories VARCHAR(500),
  start_date TIMESTAMP(3),
  due_date TIMESTAMP(3),
  date_completed TIMESTAMP(3),
  importance INTEGER DEFAULT -1,
  complete BOOLEAN DEFAULT false,
  is_recurring BOOLEAN DEFAULT false,
  sensitivity INTEGER DEFAULT -1,
  team_task BOOLEAN DEFAULT false,
  reminder_set BOOLEAN DEFAULT false,
  reminder_time TIMESTAMP(3),
  body TEXT
);
  
