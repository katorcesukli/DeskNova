-- add CURRENT_TIMESTAMP to date_resolved for resolved tickets
UPDATE tickets
SET date_resolved = CURRENT_TIMESTAMP
WHERE status = 'RESOLVED';