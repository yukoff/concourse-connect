/*
 *   Postgresql
 *   $Id Exp$
 *
 *   Table: appointment
 *   Sequence: appointment_appointment_id_seq
 */

CREATE TABLE appointment (
  appointment_id BIGSERIAL PRIMARY KEY,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby INTEGER DEFAULT -1,
  modified TIMESTAMP(3),
  modifiedby INTEGER DEFAULT -1,
  owner INTEGER DEFAULT -1,
  enabled BOOLEAN DEFAULT false,
  subject VARCHAR(255),
  location VARCHAR(255),
  categories VARCHAR(500),
  start_date TIMESTAMP(3),
  duration INTEGER DEFAULT -1,
  end_date TIMESTAMP(3),
  all_day_event BOOLEAN DEFAULT false,
  is_recurring BOOLEAN DEFAULT false,
  meeting_status INTEGER DEFAULT -1,
  sensitivity INTEGER DEFAULT -1,
  busy_status INTEGER DEFAULT -1,
  reminder_set BOOLEAN DEFAULT false,
  reminder_minutes INTEGER DEFAULT -1,
  recipients VARCHAR(2000),
  body TEXT
);


