-- 极简登录功能数据库脚本
-- 数据库：login_db

CREATE DATABASE IF NOT EXISTS `login_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `login_db`;

-- 登录账号表：存储系统登录账号及状态
CREATE TABLE IF NOT EXISTS `login_account` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    VARCHAR(64)  NOT NULL COMMENT '登录账号',
    `password`    VARCHAR(64)  NOT NULL COMMENT '密码（MD5加密）',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '账号状态：0-禁用，1-正常',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_login_account_username` (`username`),
    KEY `idx_login_account_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录账号表';

-- 登录会话Token表：存储登录会话，默认有效期2小时
CREATE TABLE IF NOT EXISTS `login_token` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `account_id`  BIGINT       NOT NULL COMMENT '关联账号ID',
    `token`       VARCHAR(64)  NOT NULL COMMENT '会话Token',
    `expire_time` DATETIME     NOT NULL COMMENT '过期时间',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_login_token_token` (`token`),
    KEY `idx_login_token_account_id` (`account_id`),
    KEY `idx_login_token_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录会话Token表';

-- 初始化管理员账号：admin / admin123（密码 MD5：0192023a7bbd73250516f069df18b500）
INSERT INTO `login_account` (`username`, `password`, `status`)
SELECT 'admin', '0192023a7bbd73250516f069df18b500', 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `login_account` WHERE `username` = 'admin');
