-- 批次 1.5：项目/客户/交付软删除字段
-- 逻辑删除字段 deleted 与 application.yml 中 mybatis-plus.global-config.db-config 对齐

CREATE TABLE IF NOT EXISTS `agent_project` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(128) NOT NULL COMMENT '项目名称',
    `code` VARCHAR(64) NOT NULL COMMENT '项目编码',
    `directory_path` VARCHAR(512) DEFAULT NULL COMMENT '项目工作区目录',
    `description` VARCHAR(512) DEFAULT NULL COMMENT '项目描述',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0-正常 1-已删除',
    `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_agent_project_code` (`code`),
    KEY `idx_agent_project_deleted` (`deleted`, `deleted_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自动化项目';

CREATE TABLE IF NOT EXISTS `agent_customer` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `project_id` BIGINT NOT NULL COMMENT '所属项目',
    `name` VARCHAR(128) NOT NULL COMMENT '客户名称',
    `contact` VARCHAR(128) DEFAULT NULL COMMENT '联系方式',
    `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0-正常 1-已删除',
    `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_agent_customer_project` (`project_id`),
    KEY `idx_agent_customer_deleted` (`deleted`, `deleted_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目客户';

CREATE TABLE IF NOT EXISTS `agent_delivery` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `project_id` BIGINT NOT NULL COMMENT '所属项目',
    `customer_id` BIGINT NOT NULL COMMENT '所属客户',
    `name` VARCHAR(128) NOT NULL COMMENT '交付名称',
    `directory_path` VARCHAR(512) DEFAULT NULL COMMENT '交付物目录',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0-草稿 1-已交付',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '0-正常 1-已删除',
    `deleted_time` DATETIME DEFAULT NULL COMMENT '删除时间',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_agent_delivery_project` (`project_id`),
    KEY `idx_agent_delivery_customer` (`customer_id`),
    KEY `idx_agent_delivery_deleted` (`deleted`, `deleted_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户交付';
