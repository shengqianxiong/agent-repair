-- RealCloudAgent-1780772159829 登录认证模块 DDL
-- 数据库：travel

USE `travel`;

-- 管理端用户表
DROP TABLE IF EXISTS `auth_admin_user`;
CREATE TABLE `auth_admin_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
    `username`    VARCHAR(64)  NOT NULL COMMENT '登录用户名',
    `password`    VARCHAR(64)  NOT NULL COMMENT '登录密码（MD5密文）',
    `nickname`    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理端用户表';

-- 用户端用户表
DROP TABLE IF EXISTS `auth_app_user`;
CREATE TABLE `auth_app_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(64)  NOT NULL COMMENT '登录用户名',
    `password`    VARCHAR(64)  NOT NULL COMMENT '登录密码（MD5密文）',
    `nickname`    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户端用户表';

-- 初始化管理端账号（密码明文：123456，MD5：e10adc3949ba59abbe56e057f20f883e）
INSERT INTO `auth_admin_user` (`username`, `password`, `nickname`, `status`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 1);

-- 初始化用户端账号（密码明文：123456，MD5：e10adc3949ba59abbe56e057f20f883e）
INSERT INTO `auth_app_user` (`username`, `password`, `nickname`, `status`) VALUES
('user', 'e10adc3949ba59abbe56e057f20f883e', '演示用户', 1);
