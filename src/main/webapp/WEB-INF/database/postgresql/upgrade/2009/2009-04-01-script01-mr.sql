CREATE INDEX projects_cat_idx ON projects(category_id);
CREATE INDEX projects_subcat1_idx ON projects(subcategory1_id);
CREATE INDEX projects_usr_conl_usr_idx ON user_contribution_log(user_id);
CREATE INDEX projects_usr_conl_prj_idx ON user_contribution_log(project_id);
CREATE INDEX projects_usr_conl_cdt_idx ON user_contribution_log(contribution_date);
