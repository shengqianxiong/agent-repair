-- 酒吧点餐小程序 - 酒水商品模块 DDL
-- 数据库：travel

USE `travel`;

-- 酒水分类
DROP TABLE IF EXISTS `bar_product_category`;
CREATE TABLE `bar_product_category` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`        VARCHAR(64)  NOT NULL COMMENT '分类名称',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status_sort` (`status`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒水分类';

-- 酒水商品
DROP TABLE IF EXISTS `bar_product`;
CREATE TABLE `bar_product` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name`           VARCHAR(128)  NOT NULL COMMENT '商品名称',
    `category_id`    BIGINT        NOT NULL COMMENT '分类ID',
    `image`          VARCHAR(512)  DEFAULT NULL COMMENT '商品图片',
    `price`          DECIMAL(10,2) NOT NULL COMMENT '售价',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `description`    VARCHAR(512)  DEFAULT NULL COMMENT '描述',
    `stock`          INT           NOT NULL DEFAULT 0 COMMENT '库存',
    `status`         TINYINT       NOT NULL DEFAULT 1 COMMENT '状态 0-下架 1-上架',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_category_status` (`category_id`, `status`),
    KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒水商品';

-- 初始化分类数据
INSERT INTO `bar_product_category` (`name`, `sort`, `status`) VALUES
('啤酒', 1, 1),
('鸡尾酒', 2, 1),
('威士忌', 3, 1),
('小食', 4, 1);

-- 初始化商品数据
INSERT INTO `bar_product` (`name`, `category_id`, `image`, `price`, `original_price`, `description`, `stock`, `status`) VALUES
('青岛纯生', 1, 'https://example.com/beer1.jpg', 18.00, 22.00, '清爽麦香，冰镇更佳', 200, 1),
('莫吉托', 2, 'https://example.com/mojito.jpg', 48.00, 58.00, '薄荷青柠经典鸡尾酒', 80, 1),
('麦卡伦12年', 3, 'https://example.com/whisky1.jpg', 88.00, 108.00, '单一麦芽威士忌', 30, 1),
('薯条拼盘', 4, 'https://example.com/snack1.jpg', 28.00, 32.00, '下酒小食', 100, 1);
