-- User emails to track profile activity
CREATE TABLE email_updates_queue (
  queue_id BIGSERIAL PRIMARY KEY,
  entered TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  enteredby BIGINT REFERENCES users(user_id) NOT NULL,
  modified TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL,
  modifiedby BIGINT REFERENCES users(user_id) NOT NULL,
  enabled BOOLEAN DEFAULT true NOT NULL,
  -- how often to send the email
  schedule_often BOOLEAN DEFAULT false NOT NULL,
  schedule_daily BOOLEAN DEFAULT false NOT NULL,
  schedule_weekly BOOLEAN DEFAULT false NOT NULL,
  schedule_monthly BOOLEAN DEFAULT false NOT NULL,
  -- which day of the week to send the email on
  schedule_monday BOOLEAN DEFAULT false NOT NULL,
  schedule_tuesday BOOLEAN DEFAULT false NOT NULL,
  schedule_wednesday BOOLEAN DEFAULT false NOT NULL,
  schedule_thursday BOOLEAN DEFAULT false NOT NULL,
  schedule_friday BOOLEAN DEFAULT false NOT NULL,
  schedule_saturday BOOLEAN DEFAULT false NOT NULL,
  schedule_sunday BOOLEAN DEFAULT false NOT NULL,
  -- the calculated next run time
  schedule_time TIMESTAMP(3),
  status INTEGER DEFAULT 0 NOT NULL,
  processed TIMESTAMP(3) DEFAULT NULL
);

CREATE UNIQUE INDEX email_upd_uni_idx ON email_updates_queue
  (enteredby, schedule_often, schedule_daily, schedule_weekly, schedule_monthly);