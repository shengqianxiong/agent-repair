-- 酒吧点餐小程序 - 核心业务 DDL（用户/订单/预约/桌位/购物车/活动）
-- 数据库：travel

USE `travel`;

-- 用户/会员
DROP TABLE IF EXISTS `bar_user`;
CREATE TABLE `bar_user` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `openid`            VARCHAR(64)  NOT NULL COMMENT '微信OpenID',
    `nickname`          VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `avatar`            VARCHAR(512) DEFAULT NULL COMMENT '头像',
    `phone`             VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `member_level`      INT          NOT NULL DEFAULT 1 COMMENT '会员等级',
    `member_level_name` VARCHAR(32)  NOT NULL DEFAULT '普通会员' COMMENT '等级名称',
    `points`            INT          NOT NULL DEFAULT 0 COMMENT '积分余额',
    `status`            VARCHAR(16)  DEFAULT 'offline' COMMENT '在线状态',
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒吧用户/会员';

-- 桌位
DROP TABLE IF EXISTS `bar_table`;
CREATE TABLE `bar_table` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '桌位ID',
    `table_no`    VARCHAR(16)  NOT NULL COMMENT '桌号',
    `area`        VARCHAR(32)  DEFAULT NULL COMMENT '区域',
    `capacity`    INT          NOT NULL DEFAULT 4 COMMENT '容纳人数',
    `status`      VARCHAR(16)  NOT NULL DEFAULT '空闲' COMMENT '空闲/占用/预约中',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='酒吧桌位';

-- 点餐订单
DROP TABLE IF EXISTS `bar_order`;
CREATE TABLE `bar_order` (
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no`     VARCHAR(32)   NOT NULL COMMENT '订单编号',
    `user_id`      BIGINT        NOT NULL COMMENT '用户ID',
    `table_id`     BIGINT        DEFAULT NULL COMMENT '桌位ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总额',
    `pay_amount`   DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    `status`       VARCHAR(16)   NOT NULL DEFAULT '待支付' COMMENT '订单状态',
    `remark`       VARCHAR(256)  DEFAULT NULL COMMENT '备注',
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    `pay_time`     DATETIME      DEFAULT NULL COMMENT '支付时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点餐订单';

-- 订单明细
DROP TABLE IF EXISTS `bar_order_item`;
CREATE TABLE `bar_order_item` (
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '明细ID',
    `order_id`     BIGINT        NOT NULL COMMENT '订单ID',
    `product_id`   BIGINT        NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(128)  NOT NULL COMMENT '商品名称',
    `quantity`     INT           NOT NULL COMMENT '数量',
    `unit_price`   DECIMAL(10,2) NOT NULL COMMENT '单价',
    `subtotal`     DECIMAL(10,2) NOT NULL COMMENT '小计',
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细';

-- 购物车
DROP TABLE IF EXISTS `bar_cart`;
CREATE TABLE `bar_cart` (
    `id`          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
    `product_id`  BIGINT   NOT NULL COMMENT '商品ID',
    `quantity`    INT      NOT NULL DEFAULT 1 COMMENT '数量',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车';

-- 桌位预约
DROP TABLE IF EXISTS `bar_booking`;
CREATE TABLE `bar_booking` (
    `id`           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '预约ID',
    `user_id`      BIGINT      NOT NULL COMMENT '用户ID',
    `booking_date` DATE        NOT NULL COMMENT '预约日期',
    `time_slot`    VARCHAR(32) NOT NULL COMMENT '时段',
    `guest_count`  INT         NOT NULL COMMENT '人数',
    `seat_type`    VARCHAR(32) DEFAULT NULL COMMENT '席位类型',
    `table_id`     BIGINT      DEFAULT NULL COMMENT '分配桌位ID',
    `status`       VARCHAR(16) NOT NULL DEFAULT '待确认' COMMENT '预约状态',
    `create_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
    `update_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_status` (`user_id`, `status`),
    KEY `idx_booking_date` (`booking_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='桌位预约';

-- 精选活动
DROP TABLE IF EXISTS `bar_activity`;
CREATE TABLE `bar_activity` (
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '活动ID',
    `title`          VARCHAR(128)  NOT NULL COMMENT '标题',
    `image`          VARCHAR(512)  DEFAULT NULL COMMENT '封面',
    `tag`            VARCHAR(32)   DEFAULT NULL COMMENT '标签',
    `description`    VARCHAR(512)  DEFAULT NULL COMMENT '描述',
    `price`          DECIMAL(10,2) DEFAULT NULL COMMENT '活动价',
    `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    `start_time`     DATETIME      DEFAULT NULL COMMENT '开始时间',
    `end_time`       DATETIME      DEFAULT NULL COMMENT '结束时间',
    `status`         TINYINT       NOT NULL DEFAULT 1 COMMENT '0-下架 1-上架',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='精选活动';

-- 初始化桌位
INSERT INTO `bar_table` (`table_no`, `area`, `capacity`, `status`) VALUES
('A01', '大厅', 4, '空闲'),
('A02', '大厅', 4, '空闲'),
('B01', '卡座', 6, '空闲'),
('B02', '卡座', 6, '空闲'),
('V01', 'VIP', 8, '空闲');

-- 初始化活动
INSERT INTO `bar_activity` (`title`, `image`, `tag`, `description`, `price`, `original_price`, `start_time`, `end_time`, `status`) VALUES
('周末畅饮套餐', 'https://example.com/activity1.jpg', '限时优惠', '周末指定啤酒买二送一', 88.00, 128.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1),
('新人专享鸡尾酒', 'https://example.com/activity2.jpg', '热门', '新会员首单鸡尾酒半价', 24.00, 48.00, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), 1);
