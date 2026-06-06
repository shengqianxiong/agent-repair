-- 极简登录功能数据库初始化脚本
-- 数据库：login

CREATE DATABASE IF NOT EXISTS `login` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `login`;

-- 登录账号表：存储系统所有可登录账号
CREATE TABLE IF NOT EXISTS `login_account` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    VARCHAR(64)  NOT NULL COMMENT '登录账号',
    `password`    VARCHAR(64)  NOT NULL COMMENT '密码（MD5加密）',
    `role`        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `create_time` DATETIME     NOT NULL COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_login_account_username` (`username`),
    KEY `idx_login_account_status` (`status`),
    KEY `idx_login_account_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录账号表';

-- 登录会话Token表：存储用户/管理员登录会话，默认有效期2小时
CREATE TABLE IF NOT EXISTS `login_token` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `token`       VARCHAR(64)  NOT NULL COMMENT '会话Token',
    `account_id`  BIGINT       NOT NULL COMMENT '关联账号ID',
    `expire_time` DATETIME     NOT NULL COMMENT '过期时间',
    `create_time` DATETIME     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_login_token_token` (`token`),
    KEY `idx_login_token_account_id` (`account_id`),
    KEY `idx_login_token_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录会话Token表';

-- 初始化数据：admin/admin123（管理员），user/user123（普通用户）
INSERT INTO `login_account` (`username`, `password`, `role`, `status`, `create_time`, `update_time`)
VALUES
    ('admin', '0192023a7bbd73250516f069df18b500', 1, 1, NOW(), NOW()),
    ('user', '6ad14ba9986e3615423dfca256d04e3f', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `update_time` = NOW();
