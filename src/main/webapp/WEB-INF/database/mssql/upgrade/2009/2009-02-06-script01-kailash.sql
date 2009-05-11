ALTER TABLE project_issue_replies ADD solution_date DATETIME;
UPDATE project_issue_replies 
SET solution_date = modified
WHERE solution = 1;