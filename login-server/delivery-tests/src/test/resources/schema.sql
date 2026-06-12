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

CREATE TABLE IF NOT EXISTS bar_product_category (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(64)  NOT NULL,
    sort        INT          NOT NULL DEFAULT 0,
    status      TINYINT      NOT NULL DEFAULT 1,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bar_product (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(128) NOT NULL,
    category_id    BIGINT       NOT NULL,
    image          VARCHAR(512),
    price          DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    description    VARCHAR(512),
    stock          INT          NOT NULL DEFAULT 0,
    status         TINYINT      NOT NULL DEFAULT 1,
    create_time    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
