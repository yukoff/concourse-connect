UPDATE projects SET language_id = 1 WHERE portal = true AND closedate IS NULL;

UPDATE project_news SET portal_key = 'index.shtml' WHERE subject = 'Home' AND classification_id = 10 AND template_id = 1 AND status = 2 AND project_id IN (SELECT project_id FROM projects WHERE portal = true AND closedate IS NULL);
