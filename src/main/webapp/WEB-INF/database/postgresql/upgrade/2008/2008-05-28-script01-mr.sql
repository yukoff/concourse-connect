
ALTER TABLE projects ADD membership_required BOOLEAN DEFAULT true;
UPDATE projects SET membership_required = false WHERE allow_guests = true;
