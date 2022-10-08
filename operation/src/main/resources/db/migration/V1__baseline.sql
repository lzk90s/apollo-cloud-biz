drop table IF EXISTS `t_follow_sell_rule`;
create TABLE `t_follow_sell_rule`  (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `tenant_id` varchar(32) NOT NULL COMMENT '租户id',
  `platform` varchar(32) NOT NULL COMMENT '平台',
  `goods_id` varchar(64) NOT NULL COMMENT '商品id',
  `sku_id` varchar(64) NOT NULL COMMENT '商品sku',
  `price_step` float(5,2) NOT NULL COMMENT '价格步长',
  `min_price` float(5,2) NOT NULL COMMENT '最小价格',
  `status` tinyint(2) NOT NULL DEFAULT 1 COMMENT '状态',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已删除',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_tenant_id_platform_goods_id_sku_id` (`tenant_id`, `platform`, `goods_id`, `sku_id`) USING BTREE
) ENGINE = InnoDB COMMENT = '跟卖规则表';

