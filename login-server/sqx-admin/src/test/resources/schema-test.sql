CREATE TABLE IF NOT EXISTS login_account (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    account     VARCHAR(64)  NOT NULL,
    password    VARCHAR(64)  NOT NULL,
    status      TINYINT      NOT NULL DEFAULT 1,
    role        TINYINT      NOT NULL DEFAULT 0,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS login_token (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id  BIGINT       NOT NULL,
    token       VARCHAR(64)  NOT NULL,
    expire_time TIMESTAMP    NOT NULL,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO login_account (account, password, status, role) VALUES
('admin', '0192023a7bbd73250516f069df18b500', 1, 1),
('user', '6ad14ba9986e3615423dfca256d04e3f', 1, 0);
