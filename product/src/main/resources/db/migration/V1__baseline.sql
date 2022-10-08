drop table IF EXISTS `t_product`;
create TABLE `t_product`  (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `reptile_rule_id` bigint(10) NOT NULL COMMENT '采集规则id',
  `platform` varchar(128) NOT NULL COMMENT '商品所在平台',
  `language` varchar(64) NOT NULL COMMENT '语言',
  `category` varchar(128) NOT NULL DEFAULT '' COMMENT '类别',
  `subject` varchar(512) NOT NULL COMMENT '名字',
  `brand` varchar(64) default '' COMMENT '品牌',
  `description` text NOT NULL DEFAULT '' COMMENT '描述',
  `detail_url` varchar(512) NOT NULL COMMENT 'url',
  `main_image_url` varchar(2048) NOT NULL COMMENT '图片url',
  `extra_image_urls` varchar(8196) DEFAULT 0 COMMENT '商品额外图片',
  `feature` text default null COMMENT '商品特征属性',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = '商品表';


drop table IF EXISTS `t_goods_sku`;
create TABLE `t_product_sku`  (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `sku_id` varchar(64) NOT NULL COMMENT 'sku_id',
  `product_id` varchar(64) NOT NULL COMMENT '商品id',
  `reptile_rule_id` bigint(10) NOT NULL COMMENT '采集规则id',
  `name` varchar(256) NOT NULL DEFAULT '' COMMENT 'sku名字',
  `image_url` varchar(2048) NOT NULL COMMENT '图片url',
  `sku_feature` text NOT NULL DEFAULT '' COMMENT 'sku属性',
  `price` DECIMAL(5,2) DEFAULT 0 COMMENT 'sku价格',
  `price_unit` varchar(64) NOT  NULL COMMENT '价格单位',
  `weight` varchar(32) default '' COMMENT '重量',
  `shipping_fee` DECIMAL(5,2) DEFAULT 0 COMMENT '运费',
  `storage` varchar(512) DEFAULT 0 COMMENT '库存',
  `follow_sells` text not null default '' comment '跟卖信息',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = '商品sku表';
