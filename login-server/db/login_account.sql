-- 极简登录功能 - 账号表 DDL
-- 数据库：login_db

CREATE DATABASE IF NOT EXISTS `login_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `login_db`;

DROP TABLE IF EXISTS `login_account`;
CREATE TABLE `login_account` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '账号ID',
    `username`    VARCHAR(64)  NOT NULL COMMENT '登录账号',
    `password`    VARCHAR(64)  NOT NULL COMMENT '密码（MD5密文，明文不入库）',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '账号状态 0-禁用 1-正常',
    `role`        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色 0-普通用户 1-管理员',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_login_account_username` (`username`),
    KEY `idx_login_account_status` (`status`),
    KEY `idx_login_account_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录账号表';

-- 初始化管理员账号：admin / admin123（密码 MD5：0192023a7bbd73250516f069df18b500）
INSERT INTO `login_account` (`username`, `password`, `status`, `role`) VALUES
('admin', '0192023a7bbd73250516f069df18b500', 1, 1);
