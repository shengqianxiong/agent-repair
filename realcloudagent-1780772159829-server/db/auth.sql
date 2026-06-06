-- 极简登录 Demo 认证表 DDL
-- 数据库：travel

-- 管理端用户表
CREATE TABLE IF NOT EXISTS `auth_admin_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `username`    VARCHAR(64)  NOT NULL COMMENT '登录用户名',
    `password`    VARCHAR(128) NOT NULL COMMENT '登录密码（MD5）',
    `nickname`    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_auth_admin_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理端登录用户表';

-- 用户端用户表
CREATE TABLE IF NOT EXISTS `auth_app_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `username`    VARCHAR(64)  NOT NULL COMMENT '登录用户名',
    `password`    VARCHAR(128) NOT NULL COMMENT '登录密码（MD5）',
    `nickname`    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_auth_app_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户端登录用户表';

-- 初始化账号（密码均为 123456 的 MD5）
INSERT INTO `auth_admin_user` (`username`, `password`, `nickname`, `status`)
VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 1)
ON DUPLICATE KEY UPDATE `username` = `username`;

INSERT INTO `auth_app_user` (`username`, `password`, `nickname`, `status`)
VALUES ('user', 'e10adc3949ba59abbe56e057f20f883e', '普通用户', 1)
ON DUPLICATE KEY UPDATE `username` = `username`;
