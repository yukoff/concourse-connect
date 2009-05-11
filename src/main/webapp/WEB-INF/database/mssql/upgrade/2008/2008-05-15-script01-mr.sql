INSERT INTO lookup_news_template (group_id, level, description, mapped_jsp, load_project_article_list, load_public_projects) VALUES (1, 5, 'Dashboard', 'templateDashboard.jsp', 0, 0);

ALTER TABLE users ADD nickname VARCHAR(255);

ALTER TABLE project_team ADD purpose INTEGER;
ALTER TABLE project_team ADD notification BIT DEFAULT 0;

ALTER TABLE projects ADD badges_enabled BIT DEFAULT 1 NOT NULL;
ALTER TABLE projects ADD badges_label VARCHAR(50);
ALTER TABLE projects ADD badges_order INTEGER DEFAULT 12 NOT NULL;
ALTER TABLE projects ADD badges_description VARCHAR(255);

ALTER TABLE projects ADD reviews_enabled BIT DEFAULT 1 NOT NULL;
ALTER TABLE projects ADD reviews_label VARCHAR(50);
ALTER TABLE projects ADD reviews_order INTEGER DEFAULT 13 NOT NULL;
ALTER TABLE projects ADD reviews_description VARCHAR(255);

ALTER TABLE projects ADD classifieds_enabled BIT DEFAULT 1 NOT NULL;
ALTER TABLE projects ADD classifieds_label VARCHAR(50);
ALTER TABLE projects ADD classifieds_order INTEGER DEFAULT 14 NOT NULL;
ALTER TABLE projects ADD classifieds_description VARCHAR(255);

ALTER TABLE projects ADD ads_enabled BIT DEFAULT 1 NOT NULL;
ALTER TABLE projects ADD ads_label VARCHAR(50);
ALTER TABLE projects ADD ads_order INTEGER DEFAULT 15 NOT NULL;
ALTER TABLE projects ADD ads_description VARCHAR(255);
