ALTER TABLE project_history ADD parent_id BIGINT REFERENCES project_history(history_id);
ALTER TABLE project_history ADD top_id BIGINT REFERENCES project_history(history_id);
ALTER TABLE project_history ADD position INTEGER DEFAULT 0;
ALTER TABLE project_history ADD thread_position INTEGER DEFAULT 0;
ALTER TABLE project_history ADD indent INTEGER DEFAULT 0;
ALTER TABLE project_history ADD child_count INTEGER DEFAULT 0;
ALTER TABLE project_history ADD relative_date TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP NOT NULL;
ALTER TABLE project_history ADD relative_enteredby BIGINT REFERENCES users(user_id);
ALTER TABLE project_history ADD lineage TEXT;

UPDATE project_history SET relative_date = link_start_date, relative_enteredby = enteredby, position = 0, indent = 0, thread_position = 0, lineage = '/';
