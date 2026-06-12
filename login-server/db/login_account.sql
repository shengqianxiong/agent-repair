-- 极简登录功能数据库初始化脚本
-- 数据库：login

CREATE DATABASE IF NOT EXISTS `login` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `login`;

-- 登录账号表
CREATE TABLE IF NOT EXISTS `login_account` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `account`     VARCHAR(64)  NOT NULL COMMENT '登录账号',
    `password`    VARCHAR(64)  NOT NULL COMMENT '密码（MD5加密）',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '账号状态：1-正常，0-禁用',
    `role`        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色：1-管理员，0-普通用户',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_login_account_account` (`account`),
    KEY `idx_login_account_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录账号表';

-- 登录 Token 表
CREATE TABLE IF NOT EXISTS `login_token` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `account_id`  BIGINT       NOT NULL COMMENT '账号ID',
    `token`       VARCHAR(64)  NOT NULL COMMENT '会话Token',
    `expire_time` DATETIME     NOT NULL COMMENT '过期时间',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_login_token_token` (`token`),
    KEY `idx_login_token_account_id` (`account_id`),
    KEY `idx_login_token_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录Token表';

-- 初始账号：admin/admin123（管理员）、user/user123（普通用户）
-- MD5: admin123 -> 0192023a7bbd73250516f069df18b500, user123 -> 6ad14ba9986e3615423dfca256d04e3f
INSERT INTO `login_account` (`account`, `password`, `status`, `role`) VALUES
('admin', '0192023a7bbd73250516f069df18b500', 1, 1),
('user', '6ad14ba9986e3615423dfca256d04e3f', 1, 0)
ON DUPLICATE KEY UPDATE `account` = VALUES(`account`);
