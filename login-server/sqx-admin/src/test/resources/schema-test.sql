CREATE TABLE IF NOT EXISTS login_account (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    username    VARCHAR(64)  NOT NULL COMMENT '登录账号',
    password    VARCHAR(64)  NOT NULL COMMENT '密码（MD5）',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    role        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色：1-管理员，0-普通用户',
    create_time TIMESTAMP    NOT NULL COMMENT '创建时间',
    update_time TIMESTAMP    NOT NULL COMMENT '更新时间'
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_login_account_username ON login_account (username);
