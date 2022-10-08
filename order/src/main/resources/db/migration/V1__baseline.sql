DROP TABLE IF EXISTS `t_order`;
create TABLE `t_order`  (
  `tenant_id` varchar(64) NOT NULL COMMENT '租户id',
  `id` varchar(64) NOT NULL COMMENT '订单id',
  `type` varchar(64) NOT NULL DEFAULT '' COMMENT '类别',
  `platform` varchar(64) NOT NULL COMMENT '商品所在平台',
  `platform_account` varchar(64) not null default '' COMMENT '平台账号',
  `confirm_time` datetime NOT NULL COMMENT 'url',
  `sn` varchar(512) NOT NULL COMMENT '图片url',
  `num` int(10) NOT NULL COMMENT '价格',
  `price` float(5,2) DEFAULT 0 COMMENT '价格',
  `image_url` varchar(512) NOT NULL COMMENT '图片url',
  `detail_url` varchar(1024) not null COMMENT '详细url',
  `remark` text default '' COMMENT 'remark',
  `sku` varchar(128) not null COMMENT 'sku',
  `last_notify_time` datetime COMMENT '卖家店铺url',
  `add_time` timestamp NOT NULL DEFAULT current_timestamp() COMMENT '添加时间',
  `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON update current_timestamp() COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT = '订单表';
