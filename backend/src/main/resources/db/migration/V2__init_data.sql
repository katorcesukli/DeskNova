-- initializing dummy users
INSERT INTO users (email, password, first_name, last_name, role)
VALUES ('client1@example.com', 'password', 'Client', 'One', 'CLIENT'),
       ('client2@example.com', 'password', 'Client', 'Two', 'CLIENT'),
       ('client3@example.com', 'password', 'Client', 'Three', 'CLIENT'),
       ('client4@example.com', 'password', 'Client', 'Four', 'CLIENT'),
       ('client5@example.com', 'password', 'Client', 'Five', 'CLIENT'),
       ('client6@example.com', 'password', 'Client', 'Six', 'CLIENT'),
       ('client7@example.com', 'password', 'Client', 'Seven', 'CLIENT'),
       ('client8@example.com', 'password', 'Client', 'Eight', 'CLIENT'),
       ('client9@example.com', 'password', 'Client', 'Nine', 'CLIENT'),
       ('client10@example.com', 'password', 'Client', 'Ten', 'CLIENT');
INSERT INTO users (email, password, first_name, last_name, role)
VALUES ('agent1@example.com', 'password', 'Agent', 'One', 'AGENT'),
       ('agent2@example.com', 'password', 'Agent', 'Two', 'AGENT'),
       ('agent3@example.com', 'password', 'Agent', 'Three', 'AGENT'),
       ('agent4@example.com', 'password', 'Agent', 'Four', 'AGENT');
INSERT INTO users (email, password, first_name, last_name, role)
VALUES ('admin@example.com', 'password', 'System', 'Admin', 'ADMIN');

-- initializing dummy data for tickets
INSERT INTO tickets
(title, description, client_id, agent_id, category, status, priority,
 date_opened, date_closed, assigned_at)

SELECT CONCAT('Ticket #', n),
       CONCAT('Generated description for ticket #', n),
       FLOOR(1 + (RAND() * 10)),
       IF(RAND() > 0.2, FLOOR(11 + (RAND() * 4)), NULL),
       ELT(FLOOR(1 + (RAND() * 6)),
           'HARDWARE', 'SOFTWARE', 'NETWORK',
           'ACCOUNTS_AND_ACCESS', 'SERVICES', 'GENERAL'),
       @status := ELT(FLOOR(1 + (RAND()*4)),
        'OPEN','IN_PROGRESS','RESOLVED','CLOSED'),
    ELT(FLOOR(1 + (RAND()*4)),
        'LOW','MEDIUM','HIGH','CRITICAL'),
    @opened := NOW() - INTERVAL FLOOR(RAND()*30) DAY,
    IF(@status = 'CLOSED',
        @opened + INTERVAL FLOOR(1 + (RAND()*5)) DAY,
        NULL
    ),
    IF(@status IN ('IN_PROGRESS','RESOLVED','CLOSED'),
        @opened + INTERVAL FLOOR(RAND()*2) DAY,
        NULL
    )
FROM (
    SELECT @row := @row + 1 AS n
    FROM information_schema.columns, (SELECT @row := 0) r
    LIMIT 100
    ) numbers;

-- initializing dummy data for comments
INSERT INTO ticket_comments (ticket_id, user_id, comment, created_at)
SELECT t.id,
       IF(t.agent_id IS NOT NULL AND RAND() > 0.5,
          t.agent_id,
          t.client_id),
       CONCAT('Auto-generated comment for ticket #', t.id),
       NOW() - INTERVAL FLOOR(RAND()*20) DAY
FROM tickets t
    JOIN (
    SELECT @row2 := @row2 + 1 AS n
    FROM information_schema.columns, (SELECT @row2 := 0) r2
    LIMIT 200
    ) numbers2
ON t.id = FLOOR(1 + (RAND()*100));