-- 极简登录账号管理 - 账号表 DDL
-- 数据库：login_db

CREATE TABLE IF NOT EXISTS `login_account` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`    VARCHAR(64)  NOT NULL COMMENT '登录账号，全局唯一',
    `password`    VARCHAR(64)  NOT NULL COMMENT '密码（MD5 加密存储）',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '账号状态：1-正常，0-禁用',
    `role`        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色：1-管理员，0-普通用户',
    `create_time` DATETIME     NOT NULL COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_login_account_username` (`username`),
    KEY `idx_login_account_status` (`status`),
    KEY `idx_login_account_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统登录账号表';

-- 初始化默认管理员账号：admin / admin123（密码 MD5：0192023a7bbd73250516f069df18b500）
INSERT INTO `login_account` (`username`, `password`, `status`, `role`, `create_time`, `update_time`)
VALUES ('admin', '0192023a7bbd73250516f069df18b500', 1, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `update_time` = NOW();
