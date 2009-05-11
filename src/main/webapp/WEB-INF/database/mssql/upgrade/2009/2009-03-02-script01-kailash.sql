-- Add and update temporary short description column
ALTER TABLE projects ADD temp_shortdescription VARCHAR(1000);
UPDATE projects SET temp_shortdescription = shortdescription;

-- Drop the current short description column and add a new one
ALTER TABLE projects DROP shortdescription;
ALTER TABLE projects ADD shortdescription VARCHAR(1000);

-- Set the values of the new column with the values in the temporary short description column
-- Drop the temporary short description column
UPDATE projects SET shortdescription = temp_shortdescription;
ALTER TABLE projects DROP temp_shortdescription;



