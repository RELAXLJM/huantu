-- ============================================================
-- 寰途旅游 App - 数据库初始化脚本
-- 数据库名: huantu
-- MySQL版本: 8.0+
-- 字符集: utf8mb4
-- ============================================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS huantu
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
USE huantu;

-- ============================================================
-- 2. 建表
-- ============================================================

-- 2.1 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`            BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '用户ID',
    `nickname`      VARCHAR(50)     NOT NULL                 COMMENT '昵称',
    `avatar_url`    VARCHAR(500)    DEFAULT NULL             COMMENT '头像URL（OSS）',
    `phone`         VARCHAR(20)     NOT NULL                 COMMENT '手机号',
    `password_hash` VARCHAR(255)    NOT NULL                 COMMENT '密码哈希（BCrypt）',
    `wx_openid`     VARCHAR(100)    DEFAULT NULL             COMMENT '微信OpenID',
    `city`          VARCHAR(50)     DEFAULT NULL             COMMENT '所在城市',
    `bio`           VARCHAR(200)    DEFAULT NULL             COMMENT '个人简介',
    `status`        TINYINT         NOT NULL DEFAULT 1       COMMENT '状态：1正常 0禁用',
    `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_wx_openid` (`wx_openid`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';


-- 2.2 路线表
DROP TABLE IF EXISTS `route`;
CREATE TABLE `route` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '路线ID',
    `user_id`         BIGINT        NOT NULL                 COMMENT '用户ID',
    `title`           VARCHAR(100)  NOT NULL                 COMMENT '路线名称',
    `cover_url`       VARCHAR(500)  DEFAULT NULL             COMMENT '封面图URL（OSS）',
    `destination`     VARCHAR(50)   NOT NULL                 COMMENT '目的地城市',
    `city_code`       VARCHAR(20)   DEFAULT NULL             COMMENT '城市编码（高德）',
    `days`            TINYINT       NOT NULL DEFAULT 1       COMMENT '游玩天数（1-5）',
    `total_duration`  INT           DEFAULT NULL             COMMENT '总预估时长（分钟）',
    `companion_type`  VARCHAR(20)   DEFAULT NULL             COMMENT '同行人：solo/couple/family/elderly/pet',
    `budget_min`      INT           DEFAULT NULL             COMMENT '每日最低预算（元）',
    `budget_max`      INT           DEFAULT NULL             COMMENT '每日最高预算（元）',
    `preference`      VARCHAR(200)  DEFAULT NULL             COMMENT '偏好标签（逗号分隔）',
    `status`          TINYINT       NOT NULL DEFAULT 0       COMMENT '状态：0草稿 1已保存 2已结束',
    `ai_prompt`       TEXT          DEFAULT NULL             COMMENT 'AI生成时的Prompt（用于重新生成）',
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_destination` (`destination`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路线表';


-- 2.3 路线-景点关联表
DROP TABLE IF EXISTS `route_scenic`;
CREATE TABLE `route_scenic` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '关联ID',
    `route_id`        BIGINT        NOT NULL                 COMMENT '路线ID',
    `scenic_id`       BIGINT        NOT NULL                 COMMENT '景点ID',
    `day_number`      TINYINT       NOT NULL DEFAULT 1       COMMENT '第几天',
    `sort_order`      INT           NOT NULL DEFAULT 0       COMMENT '当天排序',
    `stay_duration`   INT           DEFAULT 60               COMMENT '建议停留时长（分钟）',
    `transport_tip`   VARCHAR(200)  DEFAULT NULL             COMMENT '交通建议（如"步行5分钟"）',
    `memo`            VARCHAR(500)  DEFAULT NULL             COMMENT '用户备注',
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_route_id` (`route_id`),
    KEY `idx_scenic_id` (`scenic_id`),
    KEY `idx_route_day` (`route_id`, `day_number`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='路线-景点关联表';


-- 2.4 景点/POI表
DROP TABLE IF EXISTS `scenic`;
CREATE TABLE `scenic` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '景点ID',
    `name`            VARCHAR(100)  NOT NULL                 COMMENT '景点名称',
    `poi_type`        VARCHAR(20)   DEFAULT NULL             COMMENT 'POI类型：scenic/food/hotel/shopping',
    `longitude`       DECIMAL(10,6) NOT NULL                 COMMENT '经度',
    `latitude`        DECIMAL(10,6) NOT NULL                 COMMENT '纬度',
    `address`         VARCHAR(300)  DEFAULT NULL             COMMENT '详细地址',
    `city`            VARCHAR(50)   DEFAULT NULL             COMMENT '所在城市',
    `city_code`       VARCHAR(20)   DEFAULT NULL             COMMENT '城市编码',
    `rating`          DECIMAL(2,1)  DEFAULT 0.0              COMMENT '评分（0-5）',
    `images`          JSON          DEFAULT NULL             COMMENT '图片列表（JSON数组）',
    `open_time`       VARCHAR(100)  DEFAULT NULL             COMMENT '营业时间',
    `price_info`      VARCHAR(100)  DEFAULT NULL             COMMENT '门票/人均价格',
    `tag`             VARCHAR(200)  DEFAULT NULL             COMMENT '标签（逗号分隔）',
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_city_code` (`city_code`),
    KEY `idx_poi_type` (`poi_type`),
    KEY `idx_location` (`longitude`, `latitude`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='景点/POI表';


-- 2.5 社区帖子表
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '帖子ID',
    `author_id`       BIGINT        NOT NULL                 COMMENT '作者ID',
    `title`           VARCHAR(200)  DEFAULT NULL             COMMENT '标题',
    `content`         TEXT          NOT NULL                 COMMENT '正文内容',
    `images`          JSON          DEFAULT NULL             COMMENT '图片列表（JSON数组）',
    `video_url`       VARCHAR(500)  DEFAULT NULL             COMMENT '视频URL',
    `city`            VARCHAR(50)   DEFAULT NULL             COMMENT '城市',
    `city_code`       VARCHAR(20)   DEFAULT NULL             COMMENT '城市编码',
    `location_tag`    VARCHAR(200)  DEFAULT NULL             COMMENT '位置标签（如"北京路步行街"）',
    `is_local_auth`   TINYINT       NOT NULL DEFAULT 0       COMMENT '是否本地人认证：0否 1是',
    `scenic_id`       BIGINT        DEFAULT NULL             COMMENT '关联景点ID（帖子中推荐的地点）',
    `status`          TINYINT       NOT NULL DEFAULT 0       COMMENT '状态：0正常 1审核中 2下架',
    `like_count`      INT           NOT NULL DEFAULT 0       COMMENT '点赞数（冗余，定期同步）',
    `collect_count`   INT           NOT NULL DEFAULT 0       COMMENT '收藏数（冗余）',
    `useful_count`    INT           NOT NULL DEFAULT 0       COMMENT '有用数（冗余）',
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_city_code` (`city_code`),
    KEY `idx_status` (`status`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区帖子表';


-- 2.6 帖子互动表
DROP TABLE IF EXISTS `post_interact`;
CREATE TABLE `post_interact` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '互动ID',
    `post_id`         BIGINT        NOT NULL                 COMMENT '帖子ID',
    `user_id`         BIGINT        NOT NULL                 COMMENT '用户ID',
    `type`            TINYINT       NOT NULL                 COMMENT '类型：1点赞 2收藏 3有用',
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_user_type` (`post_id`, `user_id`, `type`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子互动表';


-- 2.7 用户足迹表
DROP TABLE IF EXISTS `user_footprint`;
CREATE TABLE `user_footprint` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '记录ID',
    `user_id`         BIGINT        NOT NULL                 COMMENT '用户ID',
    `scenic_id`       BIGINT        DEFAULT NULL             COMMENT '景点ID',
    `city`            VARCHAR(50)   DEFAULT NULL             COMMENT '城市名称',
    `city_code`       VARCHAR(20)   DEFAULT NULL             COMMENT '城市编码',
    `longitude`       DECIMAL(10,6) DEFAULT NULL             COMMENT '打卡经度',
    `latitude`        DECIMAL(10,6) DEFAULT NULL             COMMENT '打卡纬度',
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '打卡时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_city` (`user_id`, `city_code`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户足迹表';


-- 2.8 收藏表
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT  COMMENT '收藏ID',
    `user_id`         BIGINT        NOT NULL                 COMMENT '用户ID',
    `target_id`       BIGINT        NOT NULL                 COMMENT '目标ID',
    `target_type`     TINYINT       NOT NULL                 COMMENT '目标类型：1路线 2帖子 3景点',
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_target` (`user_id`, `target_id`, `target_type`),
    KEY `idx_target` (`target_id`, `target_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';


-- ============================================================
-- 3. 初始化数据（可选）
-- ============================================================

-- 插入一些示例城市数据（方便开发测试）
INSERT INTO `scenic` (`name`, `poi_type`, `longitude`, `latitude`, `address`, `city`, `city_code`, `rating`, `tag`) VALUES
('广州塔', 'scenic', 113.3295, 23.1086, '广州市海珠区阅江西路222号', '广州', '440100', 4.5, '地标,夜景,观景台'),
('白云山', 'scenic', 113.3094, 23.1561, '广州市白云区广园中路801号', '广州', '440100', 4.4, '自然,登山,徒步'),
('北京路步行街', 'food', 113.2644, 23.1291, '广州市越秀区北京路', '广州', '440100', 4.3, '美食,购物,老城区'),
('长隆野生动物世界', 'scenic', 113.3472, 23.0142, '广州市番禺区大石镇105国道', '广州', '440100', 4.7, '亲子,动物园,动物表演'),
('珠江夜游', 'scenic', 113.2566, 23.1150, '广州市越秀区沿江中路', '广州', '440100', 4.2, '夜景,游船,浪漫'),
('深圳湾公园', 'scenic', 113.9517, 22.5156, '深圳市南山区望海路', '深圳', '440300', 4.5, '海滨,骑行,日落'),
('世界之窗', 'scenic', 113.9711, 22.5348, '深圳市南山区深南大道9037号', '深圳', '440300', 4.3, '主题公园,地标,亲子'),
('东门老街', 'food', 114.1215, 22.5470, '深圳市罗湖区东门', '深圳', '440300', 4.1, '美食,购物,老街区');
