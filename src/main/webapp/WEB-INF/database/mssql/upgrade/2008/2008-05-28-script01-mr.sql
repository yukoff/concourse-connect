
ALTER TABLE projects ADD membership_required BIT DEFAULT 1;
UPDATE projects SET membership_required = 0 WHERE allow_guests = 1;
