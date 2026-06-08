-- 批次 1.4：产品+员工模块软删除 DDL

CREATE TABLE IF NOT EXISTS `tao_goods` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(128) NOT NULL COMMENT '商品名称',
    `category_id` BIGINT       DEFAULT NULL COMMENT '分类ID',
    `image`       VARCHAR(512) DEFAULT NULL COMMENT '图片',
    `price`       DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '价格',
    `description` TEXT         DEFAULT NULL COMMENT '描述',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '0-下架 1-上架',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '0-正常 1-已删除',
    `delete_time` DATETIME     DEFAULT NULL COMMENT '删除时间',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='淘商品';

CREATE TABLE IF NOT EXISTS `agent_category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(64)  NOT NULL COMMENT '分类名称',
    `parent_id`   BIGINT       NOT NULL DEFAULT 0 COMMENT '父分类ID',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '0-禁用 1-启用',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '0-正常 1-已删除',
    `delete_time` DATETIME     DEFAULT NULL COMMENT '删除时间',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类';

CREATE TABLE IF NOT EXISTS `product_demo` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(128) NOT NULL COMMENT '演示名称',
    `product_id`  BIGINT       DEFAULT NULL COMMENT '关联商品ID',
    `demo_url`    VARCHAR(512) DEFAULT NULL COMMENT '演示链接',
    `content`     TEXT         DEFAULT NULL COMMENT '演示内容',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '0-禁用 1-启用',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '0-正常 1-已删除',
    `delete_time` DATETIME     DEFAULT NULL COMMENT '删除时间',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品演示';

CREATE TABLE IF NOT EXISTS `agent_employee` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(64)  NOT NULL COMMENT '姓名',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `position`    VARCHAR(64)  DEFAULT NULL COMMENT '职位',
    `department`  VARCHAR(64)  DEFAULT NULL COMMENT '部门',
    `avatar`      VARCHAR(512) DEFAULT NULL COMMENT '头像',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '0-离职 1-在职',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '0-正常 1-已删除',
    `delete_time` DATETIME     DEFAULT NULL COMMENT '删除时间',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工';

CREATE TABLE IF NOT EXISTS `staff_daily_report` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `employee_id` BIGINT   NOT NULL COMMENT '员工ID',
    `report_date` DATE     NOT NULL COMMENT '日报日期',
    `content`     TEXT     NOT NULL COMMENT '日报内容',
    `status`      TINYINT  NOT NULL DEFAULT 0 COMMENT '0-草稿 1-已提交',
    `deleted`     TINYINT  NOT NULL DEFAULT 0 COMMENT '0-正常 1-已删除',
    `delete_time` DATETIME DEFAULT NULL COMMENT '删除时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_employee_id` (`employee_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工日报';

CREATE TABLE IF NOT EXISTS `user_link` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
    `link_type`   VARCHAR(32)  DEFAULT NULL COMMENT '链接类型',
    `link_url`    VARCHAR(512) NOT NULL COMMENT '链接地址',
    `title`       VARCHAR(128) DEFAULT NULL COMMENT '标题',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '0-禁用 1-启用',
    `deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '0-正常 1-已删除',
    `delete_time` DATETIME     DEFAULT NULL COMMENT '删除时间',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户链接';
