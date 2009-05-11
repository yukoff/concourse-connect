ALTER TABLE project_wiki ADD template_id INTEGER REFERENCES project_wiki_template(template_id);
