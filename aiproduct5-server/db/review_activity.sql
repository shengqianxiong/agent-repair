-- 豆评助手 - 评价活动表
CREATE TABLE IF NOT EXISTS review_activity (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    code                  VARCHAR(32)   NOT NULL,
    title                 VARCHAR(128)  NOT NULL,
    product_name          VARCHAR(128)  NOT NULL,
    product_image         VARCHAR(512),
    shop_name             VARCHAR(128),
    product_url           VARCHAR(512),
    rebate_amount         DECIMAL(10,2) NOT NULL DEFAULT 0,
    valid_end_time        TIMESTAMP,
    douyin_link           VARCHAR(512),
    copywriting_json      CLOB,
    recommend_images_json CLOB,
    status                TINYINT       NOT NULL DEFAULT 1,
    scan_count            INT           NOT NULL DEFAULT 0,
    submit_count          INT           NOT NULL DEFAULT 0,
    verify_count          INT           NOT NULL DEFAULT 0,
    create_time           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS review_task (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id      BIGINT        NOT NULL,
    user_identifier  VARCHAR(64)   NOT NULL,
    status           VARCHAR(16)   NOT NULL DEFAULT 'started',
    copy_text_id     BIGINT,
    copy_reported    TINYINT       NOT NULL DEFAULT 0,
    screenshot_url   VARCHAR(512),
    remark           VARCHAR(256),
    contact          VARCHAR(64),
    create_time      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS review_rebate (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id          BIGINT        NOT NULL,
    activity_id      BIGINT        NOT NULL,
    user_identifier  VARCHAR(64)   NOT NULL,
    amount           DECIMAL(10,2) NOT NULL DEFAULT 0,
    status           VARCHAR(16)   NOT NULL DEFAULT 'auditing',
    paid_time        TIMESTAMP,
    reject_reason    VARCHAR(256),
    create_time      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);
