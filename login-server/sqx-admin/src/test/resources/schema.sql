-- 测试环境账号表结构
DROP TABLE IF EXISTS login_account;
CREATE TABLE login_account (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '账号ID',
    username    VARCHAR(64)  NOT NULL COMMENT '登录账号',
    password    VARCHAR(64)  NOT NULL COMMENT '密码（MD5密文）',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    role        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色 0-普通用户 1-管理员',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    CONSTRAINT uk_login_account_username UNIQUE (username)
);
