DROP TABLE IF EXISTS bar_product;
DROP TABLE IF EXISTS bar_product_category;

CREATE TABLE bar_product_category (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(64)  NOT NULL,
    sort        INT          NOT NULL DEFAULT 0,
    status      TINYINT      NOT NULL DEFAULT 1,
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE bar_product (
    id             BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(128)  NOT NULL,
    category_id    BIGINT        NOT NULL,
    image          VARCHAR(512)  DEFAULT NULL,
    price          DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2) DEFAULT NULL,
    description    VARCHAR(512)  DEFAULT NULL,
    stock          INT           NOT NULL DEFAULT 0,
    status         TINYINT       NOT NULL DEFAULT 1,
    create_time    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
