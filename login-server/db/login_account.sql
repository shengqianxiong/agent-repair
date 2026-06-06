-- 极简登录功能 - 账号与会话 DDL
-- 数据库：login_db

USE `login_db`;

-- 登录账号表：存储系统全部可登录账号
DROP TABLE IF EXISTS `login_token`;
DROP TABLE IF EXISTS `login_account`;

CREATE TABLE `login_account` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '账号ID',
    `username`    VARCHAR(64)  NOT NULL COMMENT '登录账号',
    `password`    VARCHAR(64)  NOT NULL COMMENT '密码（MD5加密）',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '账号状态：0-禁用 1-正常',
    `role`        TINYINT      NOT NULL DEFAULT 0 COMMENT '角色：0-普通用户 1-管理员',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_status_create_time` (`status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录账号表';

-- 登录会话 Token 表：记录用户端/管理端登录态，默认有效期 2 小时
CREATE TABLE `login_token` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `account_id`  BIGINT       NOT NULL COMMENT '账号ID',
    `token`       VARCHAR(64)  NOT NULL COMMENT '会话Token',
    `client_type` TINYINT      NOT NULL COMMENT '客户端类型：1-用户端 2-管理端',
    `expire_time` DATETIME     NOT NULL COMMENT '过期时间',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_token` (`token`),
    KEY `idx_account_client` (`account_id`, `client_type`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录会话Token表';

-- 初始化管理员账号：admin / admin123（密码 MD5: 0192023a7bbd73250516f069df18b500）
INSERT INTO `login_account` (`username`, `password`, `status`, `role`) VALUES
('admin', '0192023a7bbd73250516f069df18b500', 1, 1);

-- 初始化普通用户账号：user01 / user123（密码 MD5: 6ad14ba9986e3615423dfca256d04e3f）
INSERT INTO `login_account` (`username`, `password`, `status`, `role`) VALUES
('user01', '6ad14ba9986e3615423dfca256d04e3f', 1, 0);
