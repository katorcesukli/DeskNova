UPDATE ticket_priority as tp
SET tp.sla_minutes = 60
WHERE tp.name = 'LOW';

UPDATE ticket_priority as tp
SET tp.sla_minutes = 240
WHERE tp.name = 'MEDIUM';

UPDATE ticket_priority as tp
SET tp.sla_minutes = 480
WHERE tp.name = 'HIGH';

UPDATE ticket_priority as tp
SET tp.sla_minutes = 1440
WHERE tp.name = 'CRITICAL';



