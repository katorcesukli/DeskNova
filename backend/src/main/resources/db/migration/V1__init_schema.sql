CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    role       ENUM('CLIENT', 'AGENT', 'ADMIN') NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE tickets
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    client_id   BIGINT          NOT NULL,

    -- set to nullable for possible admin ticket assignment
    agent_id    BIGINT NULL,

    category    ENUM(
        'HARDWARE',
        'SOFTWARE',
        'NETWORK',
        'ACCOUNTS_AND_ACCESS',
        'SERVICES',
        'GENERAL'
    ) NOT NULL,

    status      ENUM(
        'OPEN',
        'IN_PROGRESS',
        'RESOLVED',
        'CLOSED'
    ) NOT NULL DEFAULT 'OPEN',

    priority    ENUM(
        'LOW',
        'MEDIUM',
        'HIGH',
        'CRITICAL'
    ) NOT NULL DEFAULT 'MEDIUM',

    date_opened DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_closed DATETIME NULL,
    assigned_at DATETIME NULL,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_ticket_client
        FOREIGN KEY (client_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_ticket_agent
        FOREIGN KEY (agent_id)
            REFERENCES users (id)
            ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE ticket_comments
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id  BIGINT      NOT NULL,
    user_id    BIGINT      NOT NULL,
    comment    TEXT     NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comment_ticket
        FOREIGN KEY (ticket_id)
            REFERENCES tickets (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_comment_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
) ENGINE=InnoDB;