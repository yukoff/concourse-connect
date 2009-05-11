ALTER TABLE task ADD link_module_id INTEGER;
ALTER TABLE task ADD link_item_id BIGINT;

CREATE INDEX task_lm_idx ON task(link_module_id);
CREATE INDEX task_li_idx ON task(link_item_id);
