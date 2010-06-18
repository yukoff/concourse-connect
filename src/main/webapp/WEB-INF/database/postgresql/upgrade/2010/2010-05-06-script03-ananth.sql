ALTER TABLE projects_rating ADD modifiedby BIGINT REFERENCES users(user_id);
UPDATE projects_rating SET modifiedby = enteredby;
ALTER TABLE projects_rating ALTER modifiedby SET NOT NULL;
