DROP TABLE IF EXISTS login_token;
DROP TABLE IF EXISTS login_account;

CREATE TABLE login_account (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(64)  NOT NULL,
    password    VARCHAR(64)  NOT NULL,
    status      TINYINT      NOT NULL DEFAULT 1,
    role        TINYINT      NOT NULL DEFAULT 0,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE login_token (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id  BIGINT       NOT NULL,
    token       VARCHAR(64)  NOT NULL,
    client_type TINYINT      NOT NULL,
    expire_time TIMESTAMP    NOT NULL,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO login_account (id, username, password, status, role, create_time, update_time) VALUES
(1, 'admin', '0192023a7bbd73250516f069df18b500', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'user01', '6ad14ba9986e3615423dfca256d04e3f', 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
