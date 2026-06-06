CREATE TABLE IF NOT EXISTS login_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL,
    password VARCHAR(64) NOT NULL,
    role TINYINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    create_time TIMESTAMP NOT NULL,
    update_time TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX uk_login_account_username ON login_account (username);

CREATE TABLE IF NOT EXISTS login_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(64) NOT NULL,
    account_id BIGINT NOT NULL,
    expire_time TIMESTAMP NOT NULL,
    create_time TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX uk_login_token_token ON login_token (token);
