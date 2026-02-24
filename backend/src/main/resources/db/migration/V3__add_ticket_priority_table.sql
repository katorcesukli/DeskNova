CREATE TABLE ticket_priority(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE NOT NULL,
    weight INT NOT NULL,
    sla_minutes INT,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO ticket_priority (name, weight, sla_minutes)
VALUES
    ('LOW', 1, 1440),
    ('MEDIUM', 2, 480),
    ('HIGH', 5, 240),
    ('CRITICAL', 10, 60);


-- add new key column for priority with no constraint yet
ALTER TABLE tickets
ADD COLUMN priority_id BIGINT NULL;


-- add corresponding PRIORITY value priority_id column
UPDATE tickets t
    JOIN ticket_priority p
ON t.priority = p.name
    SET t.priority_id = p.id;


-- add constraint & reference priority id
ALTER TABLE tickets
    ADD CONSTRAINT fk_ticket_priority
        FOREIGN KEY (priority_id)
            REFERENCES ticket_priority(id)
            ON DELETE SET NULL;

-- remove old priority column
ALTER TABLE tickets
DROP COLUMN priority;
