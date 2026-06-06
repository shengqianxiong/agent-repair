-- H2 测试库表结构（与 MySQL DDL 语义一致）

CREATE TABLE IF NOT EXISTS login_account (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(64)  NOT NULL,
    password    VARCHAR(64)  NOT NULL,
    status      TINYINT      NOT NULL DEFAULT 1,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_login_account_username ON login_account (username);

CREATE TABLE IF NOT EXISTS login_token (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id  BIGINT       NOT NULL,
    token       VARCHAR(64)  NOT NULL,
    expire_time TIMESTAMP    NOT NULL,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_login_token_token ON login_token (token);
